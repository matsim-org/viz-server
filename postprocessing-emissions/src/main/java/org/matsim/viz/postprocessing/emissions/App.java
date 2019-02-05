package org.matsim.viz.postprocessing.emissions;

import io.dropwizard.Application;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import lombok.extern.java.Log;
import org.flywaydb.core.Flyway;
import org.matsim.viz.postprocessing.bundle.*;
import org.matsim.viz.postprocessing.emissions.persistenceModel.Visualization;

@Log
public class App extends Application<PostprocessingConfiguration> {

    private HibernateBundle<PostprocessingConfiguration> hibernate = new HibernateBundle<PostprocessingConfiguration>(
            Agent.class, Permission.class, Visualization.class, FetchInformation.class
    ) {
        @Override
        public PooledDataSourceFactory getDataSourceFactory(PostprocessingConfiguration appConfiguration) {

            executeDatabaseMigration(appConfiguration);
            return appConfiguration.getDatabase();
        }
    };

    public static void main(String[] args) throws Exception {
        new App().run(args);
    }

    @Override
    public void initialize(Bootstrap<PostprocessingConfiguration> bootstrap) {

        bootstrap.addBundle(hibernate);
        bootstrap.addBundle(new PostprocessingBundle<>((a, b, c) -> log.info("called generator"), hibernate, "emissions"));
    }

    @Override
    public void run(PostprocessingConfiguration configuration, Environment environment) {


        environment.jersey().register(new VisualizationResource(hibernate.getSessionFactory()));

      /*  createTempDirectory(configuration);
        final Client client = createJerseyClient(configuration, environment);
        final FilesApi filesApi = createfilesApi(configuration, client);
        final VisualizationFetcher fetcher = createVisualizationFetcher(configuration, filesApi);
        registerAuthFilter(configuration, client, environment);
        registerCORSFilter(environment.servlets());
        registerEndpoints(environment.jersey(), configuration, fetcher, filesApi);
        */
    }

    /*  private void createTempDirectory(AppConfiguration configuration) throws IOException {
          Files.createDirectories(Paths.get(configuration.getTmpFiles()));
      }
  */
    private void executeDatabaseMigration(PostprocessingConfiguration configuration) {

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
/*
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
                hibernate.getSessionFactory(), api, Paths.get(configuration.getTmpFiles()));

        val fetcher = new VisualizationFetcher(hibernate.getSessionFactory(), api, factory);
        fetcher.fetchVisualizationData();
        return fetcher;
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
        jersey.register(new VisualizationResource(hibernate.getSessionFactory()));
    }*/


}
