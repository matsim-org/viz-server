import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static spark.Spark.port;

public class Server {
    private static final Logger logger = LogManager.getLogger(Server.class);

    public static void main(String[] args) {
        startSparkServer();
    }

    private static void startSparkServer() {
        final int port = 3000;
        logger.info("\nStarting Server on Port: " + port + "\n");
        port(port);
        Routes.initialize();
    }
}
