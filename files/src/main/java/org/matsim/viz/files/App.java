package org.matsim.viz.files;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.google.common.collect.Lists;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.chained.ChainedAuthFilter;
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.jetty.setup.ServletEnvironment;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.flywaydb.core.Flyway;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.matsim.viz.clientAuth.NoAuthAuthenticator;
import org.matsim.viz.clientAuth.NoAuthFilter;
import org.matsim.viz.clientAuth.OAuthAuthenticator;
import org.matsim.viz.database.AbstractEntity;
import org.matsim.viz.database.DbConfiguration;
import org.matsim.viz.database.PersistenceUnit;
import org.matsim.viz.error.CodedExceptionMapper;
import org.matsim.viz.files.agent.AgentService;
import org.matsim.viz.files.agent.UserDAO;
import org.matsim.viz.files.config.AppConfiguration;
import org.matsim.viz.files.config.H2DbConfigurationFactory;
import org.matsim.viz.files.entities.Agent;
import org.matsim.viz.files.entities.VisualizationType;
import org.matsim.viz.files.notifications.NotificationDAO;
import org.matsim.viz.files.notifications.NotificationResource;
import org.matsim.viz.files.notifications.Notifier;
import org.matsim.viz.files.permission.PermissionDAO;
import org.matsim.viz.files.permission.PermissionService;
import org.matsim.viz.files.permission.SubjectFactory;
import org.matsim.viz.files.project.ProjectDAO;
import org.matsim.viz.files.project.ProjectResource;
import org.matsim.viz.files.project.ProjectService;
import org.matsim.viz.files.serialization.AbstractEntityMixin;
import org.matsim.viz.files.visualization.VisualizationDAO;
import org.matsim.viz.files.visualization.VisualizationResource;
import org.matsim.viz.files.visualization.VisualizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.RollbackException;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.ws.rs.client.Client;
import java.util.EnumSet;

public class App extends Application<AppConfiguration> {

    private Logger logger = LoggerFactory.getLogger(App.class);

    private AgentService agentService;


    public static void main(String[] args) throws Exception {
        new App().run(args);
    }

    @Override
    public void initialize(Bootstrap<AppConfiguration> bootstrap) {

        bootstrap.getObjectMapper().registerModule(new Hibernate5Module());
        bootstrap.getObjectMapper().addMixIn(AbstractEntity.class, AbstractEntityMixin.class);

        bootstrap.addBundle(new MultiPartBundle());
    }

    @Override
    public void run(AppConfiguration configuration, Environment environment) {

        AppConfiguration.setInstance(configuration);

        PersistenceUnit persistenceUnit = establishDatabaseConnection(configuration);
        registerEndpoints(environment, configuration, persistenceUnit);
        registerOAuth(configuration, environment);
        registerExceptionMappers(environment.jersey());
        registerCORSFilter(environment.servlets());
    }

    private PersistenceUnit establishDatabaseConnection(AppConfiguration configuration) {

        DbConfiguration dbConfiguration = configuration.getDatabaseFactory().createConfiguration();

        if (!(configuration.getDatabaseFactory() instanceof H2DbConfigurationFactory)) {
            // execute schema migration with flyway before connecting to the database
            // if H2 in memory database is used, this is not necessary
            Flyway flyway = new Flyway();
            flyway.setDataSource(dbConfiguration.getJdbcUrl(), dbConfiguration.getUser(), dbConfiguration.getPassword());
            flyway.migrate();
        }

        // create the actual connection to the database
        return new PersistenceUnit("org.matsim.viz.files", dbConfiguration);
    }

    private void registerOAuth(AppConfiguration config, Environment environment) {

        // register oauth authentication when making requests to other servers
        HttpAuthenticationFeature auth = HttpAuthenticationFeature.basicBuilder().build();
        final Client client = new JerseyClientBuilder(environment).using(config.getJerseyClient()).build("files");
        // client.register(auth);

        // register oauth authentication for other clients making requests to this server
        SubjectFactory subjectFactory = new SubjectFactory(agentService);
        final OAuthAuthenticator<Agent> authenticator = new OAuthAuthenticator<>(client, config.getIdProvider(),
                subjectFactory::createSubject);
        OAuthCredentialAuthFilter oauthFilter = new OAuthCredentialAuthFilter.Builder<Agent>()
                .setAuthenticator(authenticator)
                .setPrefix("Bearer")
                .buildAuthFilter();

        // register default auth-filter for unauthorized requests
        NoAuthFilter noAuthFilter = new NoAuthFilter.Builder<Agent>()
                .setAuthenticator(new NoAuthAuthenticator<>(subjectFactory::createPublicAgent))
                .setPrefix("")
                .buildAuthFilter();

        ChainedAuthFilter chainedAuthFilter = new ChainedAuthFilter(Lists.newArrayList(oauthFilter, noAuthFilter));
        environment.jersey().register(new AuthDynamicFeature(chainedAuthFilter));
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(Agent.class));
    }

    private void registerExceptionMappers(JerseyEnvironment jersey) {

        jersey.register(new CodedExceptionMapper());
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

    private void registerEndpoints(Environment environment, AppConfiguration configuration, PersistenceUnit persistenceUnit) {

        Notifier notifier = new Notifier(new JerseyClientBuilder(environment).using(configuration.getJerseyClient()).build("notification-client"), new NotificationDAO(persistenceUnit));
        ProjectDAO projectDAO = new ProjectDAO(persistenceUnit);
        VisualizationDAO visualizationDAO = new VisualizationDAO(persistenceUnit);
        UserDAO userDAO = new UserDAO(persistenceUnit);
        PermissionDAO permissionDAO = new PermissionDAO(persistenceUnit);

        agentService = new AgentService(userDAO);
        PermissionService permissionService = new PermissionService(agentService, permissionDAO);
        ProjectService projectService = new ProjectService(projectDAO, permissionService,
                configuration.getRepositoryFactory().createRepository(persistenceUnit), notifier);
        VisualizationService visualizationService = new VisualizationService(visualizationDAO, projectService, permissionService, notifier);

        environment.jersey().register(new ProjectResource(projectService, visualizationService, agentService));
        environment.jersey().register(new VisualizationResource(visualizationService));
        environment.jersey().register(new NotificationResource(notifier));

        this.loadVizTypes(visualizationService, configuration);
    }

    private void loadVizTypes(VisualizationService visualizationService, AppConfiguration config) {
        for (VisualizationType type : config.getVizTypes()) {
            logger.info("persisting viz type: " + type.getTypeName());
            try {
                visualizationService.persistType(type);
            } catch (RollbackException e) {
                logger.info("viz type: " + type.getTypeName() + " already exists");
            }
        }
    }
}
