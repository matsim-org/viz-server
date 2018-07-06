package org.matsim.webvis.files;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.jetty.setup.ServletEnvironment;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.matsim.webis.oauth.OAuthAuthenticator;
import org.matsim.webvis.database.AbstractEntity;
import org.matsim.webvis.error.CodedExceptionMapper;
import org.matsim.webvis.files.communication.AbstractEntityMixin;
import org.matsim.webvis.files.communication.MapDeserializer;
import org.matsim.webvis.files.config.AppConfiguration;
import org.matsim.webvis.files.entities.Agent;
import org.matsim.webvis.files.entities.VisualizationType;
import org.matsim.webvis.files.permission.Subject;
import org.matsim.webvis.files.project.ProjectResource;
import org.matsim.webvis.files.visualization.VisualizationResource;
import org.matsim.webvis.files.visualization.VisualizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.ws.rs.client.Client;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.Map;

public class App extends Application<AppConfiguration> {

    private Logger logger = LoggerFactory.getLogger(App.class);


    public static void main(String[] args) throws Exception {
        new App().run(args);
    }

    @Override
    public void initialize(Bootstrap<AppConfiguration> bootstrap) {

        bootstrap.getObjectMapper().registerModule(new Hibernate5Module());
        bootstrap.getObjectMapper().addMixIn(AbstractEntity.class, AbstractEntityMixin.class);
        SimpleModule module = new SimpleModule().addDeserializer(Map.class, new MapDeserializer());
        bootstrap.getObjectMapper().registerModule(module);

        bootstrap.addBundle(new MultiPartBundle());
    }

    @Override
    public void run(AppConfiguration configuration, Environment environment) throws IOException {

        AppConfiguration.setInstance(configuration);

        createUploadDirectories(configuration);
        loadVizTypes(configuration);

        registerOAuth(configuration, environment);
        registerExceptionMappers(environment.jersey());
        registerCORSFilter(environment.servlets());
        registerEndpoints(environment.jersey());
    }

    private void loadVizTypes(AppConfiguration config) {

        VisualizationService service = new VisualizationService();
        for (VisualizationType type : config.getVizTypes()) {
            logger.info("persisting viz type: " + type.getKey());
            service.persistType(type);
        }
    }

    private void createUploadDirectories(AppConfiguration config) throws IOException {

        Path tmpUploadDirectory = Paths.get(config.getTmpFilePath());
        Files.createDirectories(tmpUploadDirectory);

        Path uploadDirectory = Paths.get(config.getUploadFilePath());
        Files.createDirectories(uploadDirectory);
    }

    private void registerOAuth(AppConfiguration config, Environment environment) {

        HttpAuthenticationFeature auth = HttpAuthenticationFeature.basic(config.getRelyingPartyId(), config.getRelyingPartySecret());
        final Client client = new JerseyClientBuilder(environment).using(config.getJerseyClient()).build("bla");
        client.register(auth);
        final OAuthAuthenticator<Agent> authenticator = new OAuthAuthenticator<>(client, config.getIntrospectionEndpoint(),
                Subject::createSubject);

        environment.jersey().register(new AuthDynamicFeature(new OAuthCredentialAuthFilter.Builder<Agent>()
                .setAuthenticator(authenticator)
                .setPrefix("Bearer")
                .buildAuthFilter()
        ));
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(Agent.class));
    }

    private void registerExceptionMappers(JerseyEnvironment jersey) {

        jersey.register(new CodedExceptionMapper());
        //jersey.register(new DefaultExceptionMapper());
    }

    private void registerCORSFilter(ServletEnvironment servlet) {

        final FilterRegistration.Dynamic cors = servlet.addFilter("CORS", CrossOriginFilter.class);
        cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET, POST, PUT, OPTIONS, DELETE");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "Authorization, Content-Type");
        cors.setInitParameter(CrossOriginFilter.ALLOW_CREDENTIALS_PARAM, "true");

        /*
        cors.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
        cors.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER, "true");
        cors.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_METHODS_HEADER, "GET, POST, PUT, OPTIONS, DELETE");
        cors.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_HEADERS_HEADER, "Authorization, Content-Type");
        */

        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        cors.setInitParameter(CrossOriginFilter.CHAIN_PREFLIGHT_PARAM, Boolean.FALSE.toString());
    }

    private void registerEndpoints(JerseyEnvironment jersey) {

        jersey.register(new ProjectResource());
        jersey.register(new VisualizationResource());
    }
}
