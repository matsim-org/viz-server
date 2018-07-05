package org.matsim.webvis.files;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.matsim.webis.oauth.OAuthAuthenticator;
import org.matsim.webvis.database.AbstractEntity;
import org.matsim.webvis.error.CodedExceptionMapper;
import org.matsim.webvis.files.communication.AbstractEntityMixin;
import org.matsim.webvis.files.config.AppConfiguration;
import org.matsim.webvis.files.entities.Agent;
import org.matsim.webvis.files.entities.VisualizationType;
import org.matsim.webvis.files.permission.Subject;
import org.matsim.webvis.files.project.ProjectResource;
import org.matsim.webvis.files.visualization.VisualizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class App extends Application<AppConfiguration> {

    private Logger logger = LoggerFactory.getLogger(App.class);


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
    public void run(AppConfiguration configuration, Environment environment) throws IOException {

        AppConfiguration.setInstance(configuration);

        createUploadDirectories(configuration);
        loadVizTypes(configuration);

        registerOAuth(configuration, environment);
        registerExceptionMappers(environment.jersey());
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

    private void registerEndpoints(JerseyEnvironment jersey) {

        jersey.register(new ProjectResource());
        // jersey.register(FileResource.class);

    }
}
