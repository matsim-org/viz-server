package org.matsim.viz.auth;

import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.jersey.sessions.SessionFactoryProvider;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.jetty.setup.ServletEnvironment;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import org.eclipse.jetty.server.session.SessionHandler;
import org.matsim.viz.auth.authorization.AuthorizationResource;
import org.matsim.viz.auth.config.AppConfiguration;
import org.matsim.viz.auth.config.ConfigClient;
import org.matsim.viz.auth.config.ConfigRelyingParty;
import org.matsim.viz.auth.config.ConfigUser;
import org.matsim.viz.auth.entities.RelyingParty;
import org.matsim.viz.auth.entities.User;
import org.matsim.viz.auth.relyingParty.RelyingPartyAuthenticator;
import org.matsim.viz.auth.relyingParty.RelyingPartyService;
import org.matsim.viz.auth.token.IntrospectResource;
import org.matsim.viz.auth.token.TokenResource;
import org.matsim.viz.auth.user.LoginResource;
import org.matsim.viz.auth.user.UserService;
import org.matsim.viz.error.CodedExceptionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App extends Application<AppConfiguration> {

    private static Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws Exception {
        new App().run(args);
    }

    @Override
    public void initialize(Bootstrap<AppConfiguration> bootstrap) {
        bootstrap.addBundle(new ViewBundle<>());
    }

    @Override
    public void run(AppConfiguration appConfiguration, Environment environment) {

        AppConfiguration.setInstance(appConfiguration);
        loadResources(appConfiguration);

        registerBasicAuth(environment.jersey());
        registerSessionHandling(environment.jersey(), environment.servlets());
        registerEndpoints(environment.jersey());

        environment.jersey().register(new CodedExceptionMapper());
        //environment.jersey().register(new DefaultExceptionMapper());
    }

    private void loadResources(AppConfiguration config) {

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

    private void registerBasicAuth(JerseyEnvironment jersey) {

        // register basic auth handler for token introspection and ClientCredentialsGrant
        jersey.register(new AuthDynamicFeature(new BasicCredentialAuthFilter.Builder<RelyingParty>()
                .setAuthenticator(new RelyingPartyAuthenticator())
                .setRealm("token introspection")
                .buildAuthFilter()));
        jersey.register(new AuthValueFactoryProvider.Binder<>(RelyingParty.class));
    }

    private void registerSessionHandling(JerseyEnvironment jersey, ServletEnvironment servlet) {

        jersey.register(SessionFactoryProvider.class);
        servlet.setSessionHandler(new SessionHandler());
    }

    private void registerEndpoints(JerseyEnvironment jersey) {

        jersey.register(new IntrospectResource());
        jersey.register(new TokenResource());
        jersey.register(new AuthorizationResource());
        jersey.register(new LoginResource());
    }
}
