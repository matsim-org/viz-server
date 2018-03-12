import client.ClientService;
import com.beust.jcommander.JCommander;
import config.CommandlineArgs;
import config.ConfigUser;
import config.Configuration;
import data.entities.Client;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import user.UserService;

import static spark.Spark.port;

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

        startSparkServer();
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

    private static void startSparkServer() {

        port(Configuration.getInstance().getPort());
        Routes.initialize();

        logger.info("\n\nStarted Server on Port: " + Configuration.getInstance().getPort() + "\n");
    }
}
