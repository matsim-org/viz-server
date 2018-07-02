package org.matsim.webvis.files;

import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.setup.Environment;
import org.matsim.webis.oauth.OAuthAuthenticator;
import org.matsim.webvis.files.config.AppConfiguration;
import org.matsim.webvis.files.entities.Agent;
import org.matsim.webvis.files.permission.Subject;

import javax.ws.rs.client.Client;

public class App extends Application<AppConfiguration> {


    public static void main(String[] args) throws Exception {
        new App().run(args);
    }

    @Override
    public void run(AppConfiguration configuration, Environment environment) {

        AppConfiguration.setInstance(configuration);
        registerOAuth(configuration, environment);
    }

    private void registerOAuth(AppConfiguration config, Environment environment) {

        final Client client = new JerseyClientBuilder(environment).using(config.getJerseyClient()).build("client-name");
        final OAuthAuthenticator<Agent> authenticator = new OAuthAuthenticator<>(client, config.getIntrospectionEndpoint(),
                Subject::createSubject);

        environment.jersey().register(new AuthDynamicFeature(new OAuthCredentialAuthFilter.Builder<Agent>()
                .setAuthenticator(authenticator)
                .setPrefix("Bearer")
                .buildAuthFilter()
        ));
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(Agent.class));
    }
}
