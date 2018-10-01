package org.matsim.webvis.files;

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
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.matsim.webis.oauth.Credentials;
import org.matsim.webis.oauth.NoAuthAuthenticator;
import org.matsim.webis.oauth.NoAuthFilter;
import org.matsim.webis.oauth.OAuthAuthenticator;
import org.matsim.webvis.database.AbstractEntity;
import org.matsim.webvis.database.PersistenceUnit;
import org.matsim.webvis.error.CodedExceptionMapper;
import org.matsim.webvis.files.agent.AgentService;
import org.matsim.webvis.files.agent.UserDAO;
import org.matsim.webvis.files.config.AppConfiguration;
import org.matsim.webvis.files.entities.Agent;
import org.matsim.webvis.files.entities.VisualizationType;
import org.matsim.webvis.files.permission.PermissionDAO;
import org.matsim.webvis.files.permission.PermissionService;
import org.matsim.webvis.files.permission.SubjectFactory;
import org.matsim.webvis.files.project.ProjectDAO;
import org.matsim.webvis.files.project.ProjectResource;
import org.matsim.webvis.files.project.ProjectService;
import org.matsim.webvis.files.serialization.AbstractEntityMixin;
import org.matsim.webvis.files.visualization.VisualizationDAO;
import org.matsim.webvis.files.visualization.VisualizationResource;
import org.matsim.webvis.files.visualization.VisualizationService;
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

        registerEndpoints(environment.jersey(), configuration);
        registerOAuth(configuration, environment);
        registerExceptionMappers(environment.jersey());
        registerCORSFilter(environment.servlets());
    }

    private void registerOAuth(AppConfiguration config, Environment environment) {

        // register oauth authentication when making requests to other servers
        HttpAuthenticationFeature auth = HttpAuthenticationFeature.basicBuilder().build();
        final Client client = new JerseyClientBuilder(environment).using(config.getJerseyClient()).build("files");
        client.register(auth);

        // register oauth authentication for other clients making requests to this server
        SubjectFactory subjectFactory = new SubjectFactory(agentService);
        final OAuthAuthenticator<Agent> authenticator = new OAuthAuthenticator<>(client, config.getIntrospectionEndpoint(),
                subjectFactory::createSubject, new Credentials(config.getRelyingPartyId(), config.getRelyingPartySecret()));
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

    private void registerCORSFilter(ServletEnvironment servlet) {

        final FilterRegistration.Dynamic cors = servlet.addFilter("CORS", CrossOriginFilter.class);
        cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET, POST, PUT, OPTIONS, DELETE");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "Authorization, Content-Type");
        cors.setInitParameter(CrossOriginFilter.ALLOW_CREDENTIALS_PARAM, "true");

        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        cors.setInitParameter(CrossOriginFilter.CHAIN_PREFLIGHT_PARAM, Boolean.FALSE.toString());
    }

    private void registerEndpoints(JerseyEnvironment jersey, AppConfiguration configuration) {

        PersistenceUnit persistenceUnit = new PersistenceUnit("org.matsim.viz.files",
                configuration.getDatabaseFactory().createConfiguration());
        ProjectDAO projectDAO = new ProjectDAO(persistenceUnit);
        VisualizationDAO visualizationDAO = new VisualizationDAO(persistenceUnit);
        UserDAO userDAO = new UserDAO(persistenceUnit);
        PermissionDAO permissionDAO = new PermissionDAO(persistenceUnit);

        agentService = new AgentService(userDAO);
        PermissionService permissionService = new PermissionService(agentService, permissionDAO);
        ProjectService projectService = new ProjectService(projectDAO, permissionService,
                configuration.getRepositoryFactory().createRepository(persistenceUnit));
        VisualizationService visualizationService = new VisualizationService(visualizationDAO, projectService, permissionService);

        jersey.register(new ProjectResource(projectService, visualizationService));
        jersey.register(new VisualizationResource(visualizationService));

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
