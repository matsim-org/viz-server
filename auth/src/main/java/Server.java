import client.ClientService;
import com.beust.jcommander.JCommander;
import config.CommandlineArgs;
import config.ConfigUser;
import config.Configuration;
import data.entities.Client;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

        if (ca.configFile != null)
            Configuration.loadConfigFile(ca.configFile);

        UserService userService = new UserService();
        for (ConfigUser user : Configuration.getInstance().getUsers()) {
            userService.createUser(user);
        }

        ClientService clientService = new ClientService();
        for (Client client : Configuration.getInstance().getClients()) {
            clientService.persistNewClient(client);
        }
    }

    private static void startSparkServer(CommandlineArgs ca) {

        port(Configuration.getInstance().getPort());
        initExceptionHandler(Server::handleInitializationFailure);

        if (!ca.debug) {
            secure(Configuration.getInstance().getKeyStoreLocation(), Configuration.getInstance().getKeyStorePassword(), null, null);
        }
        Routes.initialize();

        logger.info("\n\nStarted Server on Port: " + Configuration.getInstance().getPort() + "\n");
    }

    private static void handleInitializationFailure(Exception e) {

        String keystoreLocation = Configuration.getInstance().getKeyStoreLocation();
        String keystorePassword = Configuration.getInstance().getKeyStorePassword();

        if (keystoreLocation == null || keystoreLocation.isEmpty()) {
            logger.error("\n\nInitialization failed. Keystore location was not present.\n\n");
        } else if (keystorePassword == null || keystorePassword.isEmpty()) {
            logger.error("\n\nInitialization failed. Keystore password was not present\n\n");
        } else {
            logger.error("\n\nInitialization failed.\n\n");
        }
        logger.error("Exception which caused failure was: ", e);
        System.exit(100);
    }
}
