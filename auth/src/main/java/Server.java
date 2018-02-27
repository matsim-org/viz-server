import com.beust.jcommander.JCommander;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import user.UserService;

import static spark.Spark.port;

public class Server {
    private static final Logger logger = LogManager.getLogger(Server.class);

    public static void main(String[] args) {

        CommandlineArgs ca = new CommandlineArgs();
        JCommander.newBuilder().addObject(ca).build().parse(args);

        if (ca.debug) insertDummyUsersIntoDatabase();

        startSparkServer();
    }

    private static void startSparkServer() {
        final int port = 3000;

        port(port);
        Routes.initialize();

        logger.info("\n\nStarted Server on Port: " + port + "\n");
    }

    private static void insertDummyUsersIntoDatabase() {
        UserService userService = new UserService();
        try {
            userService.createUser("user@mail.de", "longpassword".toCharArray(), "longpassword".toCharArray());
            userService.createUser("otherUser@mail.de", "longpassword".toCharArray(), "longpassword".toCharArray());
        } catch (Exception e) {
            System.out.println("error while creating dummy users.");
        }
    }
}
