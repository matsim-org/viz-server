package org.matsim.webvis.frameAnimation;

import io.dropwizard.Application;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.client.oauth2.OAuth2ClientSupport;
import org.matsim.webis.oauth.ClientAuthentication;
import org.matsim.webis.oauth.Credentials;
import org.matsim.webvis.frameAnimation.communication.ServiceCommunication;
import org.matsim.webvis.frameAnimation.config.AppConfiguration;
import org.matsim.webvis.frameAnimation.data.DataController;
import org.matsim.webvis.frameAnimation.requestHandling.VisualizationResource;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Feature;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class App extends Application<AppConfiguration> {


    public static void main(String[] args) throws Exception {
        new App().run(args);
    }

    @Override
    public void run(AppConfiguration configuration, Environment environment) throws Exception {

        AppConfiguration.setInstance(configuration);

        createUploadDirectory(configuration);
        registerOauth(configuration, environment);
        registerEndpoints(environment.jersey());

        DataController.Instance.scheduleFetching();
    }

    private void createUploadDirectory(AppConfiguration config) throws IOException {

        Path directory = Paths.get(config.getTmpFilePath());
        Files.createDirectories(directory);
    }

    private void registerOauth(AppConfiguration config, Environment environment) {

        // register basic auth support for retrieving access tokens
        final Client client = new JerseyClientBuilder(environment).using(config.getJerseyClient()).build("frame-animation");
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

    private void registerEndpoints(JerseyEnvironment jersey) {

        jersey.register(new VisualizationResource());
    }
}
