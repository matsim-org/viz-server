package org.matsim.webvis.files;

import com.beust.jcommander.JCommander;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.webvis.common.auth.AuthenticationHandler;
import org.matsim.webvis.common.auth.PrincipalCredentialToken;
import org.matsim.webvis.common.communication.Http;
import org.matsim.webvis.common.communication.HttpClientFactory;
import org.matsim.webvis.common.communication.HttpClientFactoryWithTruststore;
import org.matsim.webvis.common.communication.StartSpark;
import org.matsim.webvis.files.config.CommandLineArgs;
import org.matsim.webvis.files.config.Configuration;
import org.matsim.webvis.files.entities.VisualizationType;
import org.matsim.webvis.files.visualization.VisualizationService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Server {

    private static Logger logger = LogManager.getLogger();

    public static void main(String[] args) {

        CommandLineArgs commandLineArgs = new CommandLineArgs();
        JCommander.newBuilder().addObject(commandLineArgs).build().parse(args);

        try {
            loadConfigFile(commandLineArgs);
            createUploadDirectories();
        } catch (IOException e) {
            logger.error(e);
            System.exit(10);
        }

        startSparkServer(commandLineArgs);
        insertVizTypes();
    }

    private static void loadConfigFile(CommandLineArgs args) throws FileNotFoundException {

        if (!args.getConfigFile().isEmpty()) {
            Configuration.loadConfigFile(args.getConfigFile(), args.isDebug());
        }
    }

    private static void createUploadDirectories() throws IOException {
        Path tmpUploadDirectory = Paths.get(Configuration.getInstance().getTmpFilePath());
        Files.createDirectories(tmpUploadDirectory);

        Path uploadDirectory = Paths.get(Configuration.getInstance().getUploadedFilePath());
        Files.createDirectories(uploadDirectory);
    }

    private static void startSparkServer(CommandLineArgs args) {
        StartSpark.withPort(Configuration.getInstance().getPort());
        StartSpark.withInitializationExceptionHandler(Server::handleInitializationFailure);

        if (!args.isDebug() || !Configuration.getInstance().getTlsKeyStore().isEmpty()) {
            StartSpark.withTLS(Configuration.getInstance().getTlsKeyStore(), Configuration.getInstance().getTlsKeyStorePassword(), null, null);
        }
        try {
            HttpClientFactory factory = new HttpClientFactoryWithTruststore(
                    Paths.get(Configuration.getInstance().getTlsTrustStore()),
                    Configuration.getInstance().getTlsTrustStorePassword().toCharArray());

            Http http = new Http(factory);
            PrincipalCredentialToken token = new PrincipalCredentialToken(
                    Configuration.getInstance().getRelyingPartyId(),
                    Configuration.getInstance().getRelyingPartySecret()
            );

            AuthenticationHandler authHandler = new AuthenticationHandler(
                    http, token, URI.create(Configuration.getInstance().getIntrospectionEndpoint())
            );

            StartSpark.withAuthHandler(authHandler);
            StartSpark.withPermissiveAccessControl();
            StartSpark.withExceptionMapping();
            Routes.initialize();
        } catch (Exception e) {
            handleInitializationFailure(e);
        }

        logger.info("\n\nStarted Server on Port: " + Configuration.getInstance().getPort() + "\n");
    }

    private static void handleInitializationFailure(Exception e) {

        String tlsKeyStore = Configuration.getInstance().getTlsKeyStore();
        String tlsKeyStorePassword = Configuration.getInstance().getTlsKeyStorePassword();

        if (tlsKeyStore == null || tlsKeyStore.isEmpty()) {
            logger.error("\n\nInitialization failed. TlsKeystore location was not present.\n\n");
        } else if (tlsKeyStorePassword == null || tlsKeyStorePassword.isEmpty()) {
            logger.error("\n\nInitialization failed. TlsKeystore password was not present\n\n");
        } else {
            logger.error("Exception which caused failure was: ", e);
        }
        logger.error("Exception wich caused failure was: ", e);
        System.exit(100);
    }

    private static void insertVizTypes() {

        VisualizationService service = new VisualizationService();
        for (VisualizationType type : Configuration.getInstance().getVizTypes()) {
            logger.info("Add viz type: " + type.getKey());
            service.persistType(type);
        }
    }
}