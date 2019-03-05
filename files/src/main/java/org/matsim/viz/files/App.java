package org.matsim.viz.files;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.jetty.setup.ServletEnvironment;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import lombok.extern.java.Log;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.flywaydb.core.Flyway;
import org.matsim.viz.clientAuth.OAuthAuthenticator;
import org.matsim.viz.clientAuth.OAuthNoAuthFilter;
import org.matsim.viz.database.AbstractEntity;
import org.matsim.viz.database.PersistenceUnit;
import org.matsim.viz.error.CodedExceptionMapper;
import org.matsim.viz.files.agent.AgentService;
import org.matsim.viz.files.agent.UserDAO;
import org.matsim.viz.files.config.AppConfiguration;
import org.matsim.viz.files.entities.*;
import org.matsim.viz.files.notifications.*;
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

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.ws.rs.client.Client;
import java.util.EnumSet;

@Log
public class App extends Application<AppConfiguration> {

    private HibernateBundle<AppConfiguration> hibernate = new HibernateBundle<AppConfiguration>(
            Agent.class, FileEntry.class, PendingFileTransfer.class, Permission.class, Project.class, PublicAgent.class,
            Resource.class, ServiceAgent.class, Tag.class, User.class, Visualization.class, VisualizationInput.class,
            VisualizationParameter.class, NotificationType.class, Subscription.class
    ) {
        @Override
        public PooledDataSourceFactory getDataSourceFactory(AppConfiguration appConfiguration) {

			//log.warning("Database Migration is disabled until moving to another db");
			executeDatabaseMigration(appConfiguration);
            return appConfiguration.getDatabase();
        }
    };

    private AgentService agentService;


    public static void main(String[] args) throws Exception {
        new App().run(args);
    }

    @Override
    public void initialize(Bootstrap<AppConfiguration> bootstrap) {

        bootstrap.getObjectMapper().registerModule(new Hibernate5Module());
        bootstrap.getObjectMapper().addMixIn(AbstractEntity.class, AbstractEntityMixin.class);

        bootstrap.addBundle(new MultiPartBundle());
        bootstrap.addBundle(hibernate);
    }

    @Override
    public void run(AppConfiguration configuration, Environment environment) {

        AppConfiguration.setInstance(configuration);

        PersistenceUnit persistenceUnit = new PersistenceUnit(hibernate.getSessionFactory());
        registerEndpoints(environment, configuration, persistenceUnit);
        registerOAuth(configuration, environment);
        registerExceptionMappers(environment.jersey());
        registerCORSFilter(environment.servlets());
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

    private void registerOAuth(AppConfiguration config, Environment environment) {

        // create client to make requests to other servers
        final Client client = new JerseyClientBuilder(environment).using(config.getJerseyClient()).build("files");

        // register oauth authentication for other clients making requests to this server
        SubjectFactory subjectFactory = new SubjectFactory(agentService);
        final OAuthAuthenticator<Agent> authenticator = new OAuthAuthenticator<>(client, config.getIdProvider(),
                subjectFactory::createSubject);

        OAuthNoAuthFilter filter = new OAuthNoAuthFilter.Builder<Agent>()
                .setNoAuthPrincipalProvider(subjectFactory::createPublicAgent)
                .setAuthenticator(authenticator)
                .setPrefix("Bearer")
                .buildAuthFilter();

        environment.jersey().register(new AuthDynamicFeature(filter));
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(Agent.class));
    }

    private void registerExceptionMappers(JerseyEnvironment jersey) {

        jersey.register(new CodedExceptionMapper());
    }

    @SuppressWarnings("Duplicates")
    private void registerCORSFilter(ServletEnvironment servlet) {

        final FilterRegistration.Dynamic cors = servlet.addFilter("CORS", CrossOriginFilter.class);
        cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET, POST, PUT, OPTIONS, DELETE, PATCH");
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
    }
}
