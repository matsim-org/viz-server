package org.matsim.webvis.auth;

import com.beust.jcommander.JCommander;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.webvis.auth.config.CommandlineArgs;
import org.matsim.webvis.auth.config.ConfigRelyingParty;
import org.matsim.webvis.auth.config.ConfigUser;
import org.matsim.webvis.auth.config.Configuration;
import org.matsim.webvis.auth.entities.Client;
import org.matsim.webvis.auth.relyingParty.RelyingPartyService;
import org.matsim.webvis.auth.user.UserService;

import static spark.Spark.*;

public class Server {
    private static final Logger logger = LogManager.getLogger(Server.class);

    public static void main(String[] args) {

        CommandlineArgs ca = new CommandlineArgs();
        JCommander.newBuilder().addObject(ca).build().parse(args);

        try {
            loadConfigFile(ca);
        } catch (Exception e) {
            logger.error(e);
            System.exit(100);
        }

        startSparkServer(ca);
    }

    static void loadConfigFile(CommandlineArgs ca) throws Exception {

        if (!ca.getConfigFile().isEmpty())
            Configuration.loadConfigFile(ca.getConfigFile(), ca.isDebug());

        UserService userService = new UserService();
        for (ConfigUser user : Configuration.getInstance().getUsers()) {
            userService.createUser(user);
        }

        RelyingPartyService relyingPartyService = new RelyingPartyService();
        for (Client client : Configuration.getInstance().getClients()) {
            relyingPartyService.persistNewClient(client);
        }

        for (ConfigRelyingParty party : Configuration.getInstance().getProtectedResources()) {
            relyingPartyService.createRelyingParty(party);
        }
    }

    private static void startSparkServer(CommandlineArgs ca) {

        port(Configuration.getInstance().getPort());
        initExceptionHandler(Server::handleInitializationFailure);

        if (!ca.isDebug() || !Configuration.getInstance().getTlsKeyStore().isEmpty()) {
            secure(Configuration.getInstance().getTlsKeyStore(), Configuration.getInstance().getTlsKeyStorePassword(), null, null);
        }

        try {
            Routes.initialize();
        } catch (Exception e) {
            handleInitializationFailure(e);
        }

        logger.info("\n\nStarted auth Server on Port: " + Configuration.getInstance().getPort() + "\n");
    }

    private static void handleInitializationFailure(Exception e) {

        String tlsKeyStore = Configuration.getInstance().getTlsKeyStore();
        String tlsKeyStorePassword = Configuration.getInstance().getTlsKeyStorePassword();

        String signingKeyStore = Configuration.getInstance().getTokenSigningKeyStore();
        String signingKeyStorePassword = Configuration.getInstance().getTokenSigningKeyStorePassword();

        if (tlsKeyStore == null || tlsKeyStore.isEmpty()) {
            logger.error("\n\nInitialization failed. TlsKeystore location was not present.\n\n");
        } else if (tlsKeyStorePassword == null || tlsKeyStorePassword.isEmpty()) {
            logger.error("\n\nInitialization failed. TlsKeystore password was not present\n\n");
        } else if (signingKeyStore == null || signingKeyStore.isEmpty()) {
            logger.error("\n\nInitialization failed. Signing keystore was not present\n\n");
        } else if (signingKeyStorePassword == null || signingKeyStorePassword.isEmpty()) {
            logger.error("\n\nInitialization failed. Signing keystore password was not present\n\n");
        } else {
            logger.error("\n\nInitialization failed.\n\n");
        }
        logger.error("Exception which caused failure was: ", e);
        System.exit(100);
    }
}
