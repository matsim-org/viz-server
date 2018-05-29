package org.matsim.webvis.animation;

import com.beust.jcommander.JCommander;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.webvis.animation.config.CommandlineArgs;
import org.matsim.webvis.animation.config.Configuration;
import org.matsim.webvis.common.auth.AuthenticationHandler;
import org.matsim.webvis.common.communication.StartSpark;

import java.io.FileNotFoundException;
import java.nio.file.Paths;

public class Server {

    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {

        CommandlineArgs ca = new CommandlineArgs();
        JCommander.newBuilder().addObject(ca).build().parse(args);

        loadConfigFile(ca);
        startSparkServer();
    }

    private static void loadConfigFile(CommandlineArgs ca) {

        try {
            Configuration.loadConfigFile(ca.getConfigFile());
        } catch (FileNotFoundException e) {
            logger.error("Could not config file", e);
            System.exit(100);
        }
    }

    private static void startSparkServer() {

        StartSpark.withPort(Configuration.getInstance().getPort());
        StartSpark.withInitializationExceptionHandler(Server::handleInitializationFailure);
        StartSpark.withTLS(Configuration.getInstance().getTlsKeyStore(), Configuration.getInstance().getTlsKeyStorePassword(),
                Configuration.getInstance().getTlsTrustStore(), Configuration.getInstance().getTlsTrustStorePassword());
        StartSpark.withPermissiveAccessControl();
        StartSpark.withExceptionMapping();

        try {
            AuthenticationHandler authHandler = AuthenticationHandler.builder()
                    .setIntrospectionEndpoint(Configuration.getInstance().getIntrospectionEndpoint())
                    .setRelyingPartyId(Configuration.getInstance().getRelyingPartyId())
                    .setRelyingPartySecret(Configuration.getInstance().getRelyingPartySecret())
                    .setTrustStore(Paths.get(Configuration.getInstance().getTlsTrustStore()))
                    .setTrustStorePassword(Configuration.getInstance().getTlsTrustStorePassword().toCharArray())
                    .build();
            StartSpark.withAuthHandler(authHandler);
        } catch (Exception e) {
            handleInitializationFailure(e);
        }
        Routes.initialize();

        logger.info("\n\nStarted animation Server on Port: " + Configuration.getInstance().getPort() + "\n");
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
}
