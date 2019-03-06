package org.matsim.viz.frameAnimation;

import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.jetty.setup.ServletEnvironment;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import lombok.val;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.flywaydb.core.Flyway;
import org.matsim.viz.clientAuth.OAuthAuthenticator;
import org.matsim.viz.clientAuth.OAuthNoAuthFilter;
import org.matsim.viz.filesApi.FilesApi;
import org.matsim.viz.frameAnimation.communication.NotificationHandler;
import org.matsim.viz.frameAnimation.config.AppConfiguration;
import org.matsim.viz.frameAnimation.inputProcessing.VisualizationFetcher;
import org.matsim.viz.frameAnimation.inputProcessing.VisualizationGeneratorFactory;
import org.matsim.viz.frameAnimation.persistenceModel.*;
import org.matsim.viz.frameAnimation.requestHandling.VisualizationResource;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.ws.rs.client.Client;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.Optional;

public class App extends Application<AppConfiguration> {

    private HibernateBundle<AppConfiguration> hibernate = new HibernateBundle<AppConfiguration>(
            Agent.class, MatsimNetwork.class, Permission.class, Plan.class, Snapshot.class, Visualization.class, FetchInformation.class
    ) {
        @Override
        public PooledDataSourceFactory getDataSourceFactory(AppConfiguration appConfiguration) {

            // database migration must run before hibernate gets initialized
            executeDatabaseMigration(appConfiguration);
            return appConfiguration.getDatabase();
        }
    };

    public static void main(String[] args) throws Exception {
        new App().run(args);
    }

    @Override
    public void initialize(Bootstrap<AppConfiguration> bootstrap) {
        bootstrap.addBundle(hibernate);
    }

    @Override
    public void run(AppConfiguration configuration, Environment environment) throws Exception {

        AppConfiguration.setInstance(configuration);

        createUploadDirectory(configuration);
        final Client client = createJerseyClient(configuration, environment);
        final FilesApi api = createFilesApi(configuration, client);
        final VisualizationFetcher fetcher = createVisualizationFetcher(configuration, api);
        registerAuthFilter(configuration, client, environment);
        registerCORSFilter(environment.servlets());
        registerEndpoints(environment.jersey(), configuration, fetcher, api);
    }

    private void createUploadDirectory(AppConfiguration config) throws IOException {

        Path directory = Paths.get(config.getTmpFilePath());
        Files.createDirectories(directory);
    }

    private void executeDatabaseMigration(AppConfiguration configuration) {

        if (!configuration.getDatabase().getDriverClass().equals("org.h2.Driver")) {
            // execute schema migration with flyway before connecting to the database
            // if H2 in memory database is used, this is not necessary
            Flyway flyway = Flyway.configure().dataSource(
                    configuration.getDatabase().getUrl(),
                    configuration.getDatabase().getUser(),
                    configuration.getDatabase().getPassword()
            ).load();
            flyway.migrate();
        }
    }

    private Client createJerseyClient(AppConfiguration config, Environment environment) {

        return new JerseyClientBuilder(environment)
                .using(config.getJerseyClient())
                .using(FilesApi.getObjectMapper())
                .build("frame-animation");
    }

    private FilesApi createFilesApi(AppConfiguration configuration, Client client) {
        return new FilesApi.FilesApiBuilder()
                .withClient(client)
                .withFilesEndpoint(configuration.getFileServer())
                .withRelyingPartyId(configuration.getRelyingPartyId())
                .withRelyingPartySecret(configuration.getRelyingPartySecret())
                .withTokenEndpoint(configuration.getTokenEndpoint())
                .build();
    }

    private VisualizationFetcher createVisualizationFetcher(AppConfiguration configuration, FilesApi api) {

        val factory = new VisualizationGeneratorFactory(api, hibernate.getSessionFactory(), Paths.get(configuration.getTmpFilePath()));
        val fetcher = new VisualizationFetcher(api, factory, hibernate.getSessionFactory());
        fetcher.scheduleFetching();
        return fetcher;
    }

    private void registerAuthFilter(AppConfiguration configuration, Client client, Environment environment) {

        final OAuthAuthenticator<Agent> authenticator1 = new OAuthAuthenticator<>(client, configuration.getIdProvider(),
                result -> Optional.of(new Agent(result.getSubjectId())));

        OAuthNoAuthFilter filter = new OAuthNoAuthFilter.Builder<Agent>()
                .setNoAuthPrincipalProvider(() -> Optional.of(new Agent(Agent.publicPermissionId)))
                .setAuthenticator(authenticator1)
                .setPrefix("Bearer")
                .buildAuthFilter();

        environment.jersey().register(new AuthDynamicFeature(filter));
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(Agent.class));
    }

    @SuppressWarnings("Duplicates")
    private void registerCORSFilter(ServletEnvironment servlet) {

        final FilterRegistration.Dynamic cors = servlet.addFilter("CORS", CrossOriginFilter.class);
        cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET, POST, PUT, OPTIONS, DELETE");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "Authorization, Content-Type");
        cors.setInitParameter(CrossOriginFilter.ALLOW_CREDENTIALS_PARAM, "true");

        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        cors.setInitParameter(CrossOriginFilter.CHAIN_PREFLIGHT_PARAM, Boolean.FALSE.toString());
    }

    private void registerEndpoints(JerseyEnvironment jersey, AppConfiguration configuration, VisualizationFetcher fetcher, FilesApi api) {

        jersey.register(new VisualizationResource(hibernate.getSessionFactory()));
        jersey.register(new NotificationHandler(api, fetcher, configuration.getOwnHostname(), hibernate.getSessionFactory()));
    }
}
