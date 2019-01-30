package org.matsim.viz.postprocessing.emissions;

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
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.flywaydb.core.Flyway;
import org.matsim.viz.clientAuth.OAuthAuthenticator;
import org.matsim.viz.clientAuth.OAuthNoAuthFilter;
import org.matsim.viz.filesApi.FilesApi;
import org.matsim.viz.postprocessing.emissions.persistenceModel.Agent;
import org.matsim.viz.postprocessing.emissions.persistenceModel.Permission;
import org.matsim.viz.postprocessing.emissions.persistenceModel.Visualization;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.ws.rs.client.Client;
import java.io.IOException;
import java.nio.file.Files;
import java.util.EnumSet;
import java.util.Optional;

public class App extends Application<AppConfiguration> {

    private HibernateBundle<AppConfiguration> hibernate = new HibernateBundle<AppConfiguration>(
            Agent.class, Permission.class, Visualization.class
    ) {
        @Override
        public PooledDataSourceFactory getDataSourceFactory(AppConfiguration appConfiguration) {
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
    public void run(AppConfiguration configuration, Environment environment) throws IOException {

        createTempDirectory(configuration);
        final Client client = createJerseyClient(configuration, environment);
        final FilesApi filesApi = createfilesApi(configuration, client);
        final VisualizationFetcher fetcher = createVisualizationFetcher(configuration, filesApi);
        registerAuthFilter(configuration, client, environment);
        registerCORSFilter(environment.servlets());
        registerEndpoints(environment.jersey(), configuration, fetcher, filesApi);
    }

    private void createTempDirectory(AppConfiguration configuration) throws IOException {
        Files.createDirectories(configuration.getTmpFiles());
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

    private Client createJerseyClient(AppConfiguration configuration, Environment environment) {
        return new JerseyClientBuilder(environment)
                .using(configuration.getJerseyClient())
                .using(FilesApi.getObjectMapper())
                .build("postprocessing-emissions");
    }

    private FilesApi createfilesApi(AppConfiguration configuration, Client client) {

        return new FilesApi.FilesApiBuilder()
                .withClient(client)
                .withFilesEndpoint(configuration.getFileServer())
                .withRelyingPartyId(configuration.getRelyingPartyId())
                .withRelyingPartySecret(configuration.getRelyingPartySecret())
                .withTokenEndpoint(configuration.getTokenEndpoint())
                .build();
    }

    private VisualizationFetcher createVisualizationFetcher(AppConfiguration configuration, FilesApi api) {

        VisualizationGeneratorFactory factory = new VisualizationGeneratorFactory(
                hibernate.getSessionFactory(), api, configuration.getTmpFiles());

        return new VisualizationFetcher(hibernate.getSessionFactory(), api, factory);
    }

    private void registerAuthFilter(AppConfiguration configuration, Client client, Environment environment) {

        final OAuthAuthenticator<Agent> authenticator = new OAuthAuthenticator<>(client, configuration.getIdProvider(),
                result -> Optional.of(new Agent(result.getSubjectId()))
        );

        OAuthNoAuthFilter filter = new OAuthNoAuthFilter.Builder<Agent>()
                .setNoAuthPrincipalProvider(() -> Optional.of(new Agent(Agent.publicPermissionId)))
                .setAuthenticator(authenticator)
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

    }


}
