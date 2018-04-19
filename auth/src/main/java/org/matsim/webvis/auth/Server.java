package org.matsim.webvis.auth;

import com.beust.jcommander.JCommander;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.webvis.auth.config.*;
import org.matsim.webvis.auth.entities.RelyingParty;
import org.matsim.webvis.auth.entities.User;
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
            User created = userService.createUser(user);
            logger.info("Created User: " + created.getEMail());
        }

        RelyingPartyService relyingPartyService = new RelyingPartyService();
        for (ConfigClient client : Configuration.getInstance().getClients()) {
            RelyingParty created = relyingPartyService.createClient(client);
            logger.info("Created client: " + created.getName());
        }

        for (ConfigRelyingParty party : Configuration.getInstance().getProtectedResources()) {
            RelyingParty created = relyingPartyService.createRelyingParty(party);
            logger.info("Created relying party: " + created.getName());

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
