package org.matsim.webvis.frameAnimation;

import com.beust.jcommander.JCommander;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.webvis.common.auth.AuthenticationHandler;
import org.matsim.webvis.common.communication.StartSpark;
import org.matsim.webvis.frameAnimation.communication.Authentication;
import org.matsim.webvis.frameAnimation.config.CommandlineArgs;
import org.matsim.webvis.frameAnimation.config.Configuration;
import org.matsim.webvis.frameAnimation.data.FileAPI;
import org.matsim.webvis.frameAnimation.data.SimulationData;

import java.io.FileNotFoundException;
import java.nio.file.Paths;

import static spark.Spark.port;
import static spark.Spark.post;

public class Server {

    private static final Logger logger = LogManager.getLogger();

    private static SimulationData data;

    private static String networkPath = "network.xml";
    private static String eventsPath = "events.xml.gz";
    private static String plansPath = "plans.xml";
    private static double snapshotPeriod = 1.0;
    private static int port = 3001;

    public static void main(String[] args) {

        CommandlineArgs ca = new CommandlineArgs();
        JCommander.newBuilder().addObject(ca).build().parse(args);

        loadConfigFile(ca);
        //initializeData();
        startSparkServer();
        initializeAuth();
    }

    private static void loadConfigFile(CommandlineArgs args) {

        try {
            Configuration.loadConfigFile(args.getConfigFile());
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
        Routes.initialize(data);
        logger.info("\n\nStarted animation Server on Port: " + Configuration.getInstance().getPort() + "\n");
    }

    private static void initializeAuth() {
        Authentication.Instance.requestAccessToken();
        FileAPI.fetchVisualizations();
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

    private static void initializeData() {

        data = new SimulationData(networkPath, eventsPath, plansPath, snapshotPeriod);
    }
}
