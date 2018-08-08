package org.matsim.webvis.frameAnimation;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.Application;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.jetty.setup.ServletEnvironment;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.client.oauth2.OAuth2ClientSupport;
import org.matsim.webis.oauth.ClientAuthentication;
import org.matsim.webis.oauth.Credentials;
import org.matsim.webvis.database.AbstractEntity;
import org.matsim.webvis.frameAnimation.communication.ServiceCommunication;
import org.matsim.webvis.frameAnimation.config.AppConfiguration;
import org.matsim.webvis.frameAnimation.data.DataController;
import org.matsim.webvis.frameAnimation.entities.AbstractEntityMixin;
import org.matsim.webvis.frameAnimation.requestHandling.VisualizationResource;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Feature;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;

public class App extends Application<AppConfiguration> {


    public static void main(String[] args) throws Exception {
        new App().run(args);
    }

    @Override
    public void run(AppConfiguration configuration, Environment environment) throws Exception {

        AppConfiguration.setInstance(configuration);

        createUploadDirectory(configuration);
        registerOauth(configuration, environment);
        registerCORSFilter(environment.servlets());
        registerEndpoints(environment.jersey());

        DataController.Instance.scheduleFetching();
    }

    private void createUploadDirectory(AppConfiguration config) throws IOException {

        Path directory = Paths.get(config.getTmpFilePath());
        Files.createDirectories(directory);
    }

    private void registerOauth(AppConfiguration config, Environment environment) {

        // register a new objectMapper for jersey client because we are using jsonIdentityInfo for serializing out
        // object graph and the default object mapper by dropwizard does not support this.
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.addMixIn(AbstractEntity.class, AbstractEntityMixin.class);

        final Client client = new JerseyClientBuilder(environment)
                .using(config.getJerseyClient())
                .using(mapper)
                .build("frame-animation");

        // register basic auth support for retrieving access tokens
        HttpAuthenticationFeature basicAuth = HttpAuthenticationFeature.basicBuilder().nonPreemptive().build();
        client.register(basicAuth);

        ClientAuthentication authentication = new ClientAuthentication(client, config.getTokenEndpoint(),
                "service-client", new Credentials(config.getRelyingPartyId(), config.getRelyingPartySecret()));
        authentication.requestAccessToken();
        ServiceCommunication.initialize(client, authentication);

        // register oauth support for retrieving data from file server
        Feature oauthFeature = OAuth2ClientSupport.feature(null);
        client.register(oauthFeature);
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

    private void registerEndpoints(JerseyEnvironment jersey) {

        jersey.register(new VisualizationResource());
    }
}
