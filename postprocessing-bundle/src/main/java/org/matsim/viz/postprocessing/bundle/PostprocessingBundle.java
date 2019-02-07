package org.matsim.viz.postprocessing.bundle;

import io.dropwizard.ConfiguredBundle;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.jetty.setup.ServletEnvironment;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.eclipse.jetty.util.component.LifeCycle;
import org.matsim.viz.clientAuth.OAuthAuthenticator;
import org.matsim.viz.clientAuth.OAuthNoAuthFilter;
import org.matsim.viz.filesApi.FilesApi;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.ws.rs.client.Client;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.Optional;
import java.util.concurrent.Executors;

import static java.util.Objects.requireNonNull;

@Log
@RequiredArgsConstructor
public class PostprocessingBundle<T extends PostprocessingConfiguration> implements ConfiguredBundle<T> {

    private final HibernateBundle hibernate;
    private final VisualizationGenerator generator;
    private final String visualizationType;

    @Override
    public void run(T configuration, Environment environment) throws Exception {

        final Path tmpFiles = createTmpDirectory(configuration);
        final Client client = createJerseyClient(configuration, environment);
        final FilesApi api = createFilesApi(configuration, client);
        final VisualizationFetcher fetcher =
                new VisualizationFetcher(new LazySessionFactory(hibernate), api, tmpFiles, requireNonNull(generator), visualizationType);
        registerAuthFilter(configuration, environment, client);
        registerCORSFilter(environment.servlets());

        log.info("register notification resource");

        // trigger a visualization fetch, when component has started up. Most callbacks are not interesting for us.
        environment.lifecycle().addLifeCycleListener(new LifeCycle.Listener() {
            @Override
            public void lifeCycleStarting(LifeCycle lifeCycle) {
            }

            @Override
            public void lifeCycleStarted(LifeCycle lifeCycle) {
                Executors.newSingleThreadExecutor().submit(fetcher::fetchVisualizationData);
            }

            @Override
            public void lifeCycleFailure(LifeCycle lifeCycle, Throwable throwable) {

            }

            @Override
            public void lifeCycleStopping(LifeCycle lifeCycle) {

            }

            @Override
            public void lifeCycleStopped(LifeCycle lifeCycle) {

            }
        });
        environment.jersey().register(new NotificationResource(api, configuration.getOwnHostname(), fetcher));
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {

    }

    private Path createTmpDirectory(T configuration) throws IOException {

        Path directory = Paths.get(configuration.getTmpFiles());
        log.info("creating tmp files directory at: " + directory.toString());
        return Files.createDirectories(directory);
    }

    private Client createJerseyClient(T configuration, Environment environment) {

        log.info("Creating jersey client with default object mapper");
        return new JerseyClientBuilder(environment)
                .using(configuration.getJerseyClient())
                .using(FilesApi.getObjectMapper())
                .build("postprocessing_" + visualizationType);
    }

    private FilesApi createFilesApi(T configuration, Client client) {

        log.info("Creating file api.");
        return new FilesApi.FilesApiBuilder()
                .withClient(client)
                .withFilesEndpoint(configuration.getFileServer())
                .withRelyingPartyId(configuration.getRelyingPartyId())
                .withRelyingPartySecret(configuration.getRelyingPartySecret())
                .withTokenEndpoint(configuration.getTokenEndpoint())
                .build();
    }

    private void registerAuthFilter(T configuration, Environment environment, Client client) {

        log.info("register OAuth filter");
        final OAuthAuthenticator<Agent> authenticator = new OAuthAuthenticator<>(client, configuration.getIdProvider(),
                result -> Optional.of(new Agent(result.getSubjectId())));

        log.info("register auth filter for public access");
        final OAuthNoAuthFilter filter = new OAuthNoAuthFilter.Builder<Agent>()
                .setNoAuthPrincipalProvider(() -> Optional.of(new Agent(org.matsim.viz.filesApi.Agent.publicPermissionId)))
                .setAuthenticator(authenticator)
                .setPrefix("Bearer")
                .buildAuthFilter();

        environment.jersey().register(new AuthDynamicFeature(filter));
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(Agent.class));
    }

    @SuppressWarnings("Duplicates")
    private void registerCORSFilter(ServletEnvironment servlet) {

        log.info("register cors filter for GET, POST, PUT, OPTIONS, DELETE, Authorization, Content-Type for all paths");
        final FilterRegistration.Dynamic cors = servlet.addFilter("CORS", CrossOriginFilter.class);
        cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET, POST, PUT, OPTIONS, DELETE");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "Authorization, Content-Type");
        cors.setInitParameter(CrossOriginFilter.ALLOW_CREDENTIALS_PARAM, "true");

        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        cors.setInitParameter(CrossOriginFilter.CHAIN_PREFLIGHT_PARAM, Boolean.FALSE.toString());
    }
}
