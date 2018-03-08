import client.ClientService;
import com.beust.jcommander.JCommander;
import data.entities.Client;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import user.UserService;

import java.io.UnsupportedEncodingException;
import java.net.URI;

import static spark.Spark.port;
import static spark.Spark.staticFiles;

public class Server {
    private static final Logger logger = LogManager.getLogger(Server.class);

    public static void main(String[] args) {

        CommandlineArgs ca = new CommandlineArgs();
        JCommander.newBuilder().addObject(ca).build().parse(args);

        if (ca.debug) insertDummyUsersIntoDatabase();

        try {
            startSparkServer();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private static void startSparkServer() throws UnsupportedEncodingException {
        final int port = 3000;

        port(port);
        staticFiles.location("/public");
        Routes.initialize();

        logger.info("\n\nStarted Server on Port: " + port + "\n");
    }

    private static void insertDummyUsersIntoDatabase() {
        UserService userService = new UserService();
        ClientService clientService = new ClientService();
        Client client;
        try {
            userService.createUser("user@mail.de", "longpassword".toCharArray(), "longpassword".toCharArray());
            userService.createUser("otherUser@mail.de", "longpassword".toCharArray(), "longpassword".toCharArray());

            client = clientService.createClient("test client", URI.create("http://localhost:3001/callback"));
            logger.info(client.getId().toString());

        } catch (Exception e) {
            System.out.println("error while creating dummy users.");
        }
    }
}
