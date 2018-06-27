package org.matsim.webvis.auth;

import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.jersey.sessions.SessionFactoryProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import org.eclipse.jetty.server.session.SessionHandler;
import org.matsim.webvis.auth.authorization.AuthorizationResource;
import org.matsim.webvis.auth.config.AuthConfiguration;
import org.matsim.webvis.auth.config.ConfigClient;
import org.matsim.webvis.auth.config.ConfigRelyingParty;
import org.matsim.webvis.auth.config.ConfigUser;
import org.matsim.webvis.auth.entities.RelyingParty;
import org.matsim.webvis.auth.entities.User;
import org.matsim.webvis.auth.relyingParty.RelyingPartyAuthenticator;
import org.matsim.webvis.auth.relyingParty.RelyingPartyService;
import org.matsim.webvis.auth.token.IntrospectResource;
import org.matsim.webvis.auth.token.TokenResource;
import org.matsim.webvis.auth.user.LoginResource;
import org.matsim.webvis.auth.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthApplication extends Application<AuthConfiguration> {

    private static Logger logger = LoggerFactory.getLogger(AuthApplication.class);

    public static void main(String[] args) throws Exception {
        new AuthApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<AuthConfiguration> bootstrap) {
        bootstrap.addBundle(new ViewBundle<>());
    }

    @Override
    public void run(AuthConfiguration authConfiguration, Environment environment) {

        AuthConfiguration.setInstance(authConfiguration);
        loadResources(authConfiguration);

        environment.jersey().register(new AuthDynamicFeature(new BasicCredentialAuthFilter.Builder<RelyingParty>()
                .setAuthenticator(new RelyingPartyAuthenticator())
                .setRealm("token introspection")
                .buildAuthFilter()));
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(RelyingParty.class));
        //environment.jersey().register(SessionFactoryProvider.class);
        environment.jersey().register(SessionFactoryProvider.class);
        environment.servlets().setSessionHandler(new SessionHandler());

        environment.jersey().register(new IntrospectResource());
        environment.jersey().register(new TokenResource());
        environment.jersey().register(new AuthorizationResource());
        environment.jersey().register(new LoginResource());
    }

    private void loadResources(AuthConfiguration config) {

        UserService userService = UserService.Instance;
        for (ConfigUser user : config.getUsers()) {
            User created = userService.createUser(user);
            logger.info("Created User: " + created.getEMail());
        }

        RelyingPartyService relyingPartyService = RelyingPartyService.Instance;
        for (ConfigClient client : config.getClients()) {
            RelyingParty created = relyingPartyService.createClient(client);
            logger.info("Created client: " + created.getName());
        }

        for (ConfigRelyingParty party : config.getProtectedResources()) {
            RelyingParty created = relyingPartyService.createRelyingParty(party);
            logger.info("Created relying party: " + created.getName());
        }
    }
}
