import com.beust.jcommander.JCommander;
import config.CommandlineArgs;
import config.ConfigUser;
import config.Configuration;
import entities.Client;
import entities.RelyingParty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import relyingParty.RelyingPartyService;
import user.UserService;

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

        for (RelyingParty party : Configuration.getInstance().getProtectedResources()) {
            relyingPartyService.persistNewRelyingParty(party);
        }
    }

    private static void startSparkServer(CommandlineArgs ca) {

        port(Configuration.getInstance().getPort());
        initExceptionHandler(Server::handleInitializationFailure);

        if (!ca.isDebug()) {
            secure(Configuration.getInstance().getTlsKeyStore(), Configuration.getInstance().getTlsKeyStorePassword(), null, null);
        }

        try {
            Routes.initialize();
        } catch (Exception e) {
            handleInitializationFailure(e);
        }

        logger.info("\n\nStarted Server on Port: " + Configuration.getInstance().getPort() + "\n");
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
