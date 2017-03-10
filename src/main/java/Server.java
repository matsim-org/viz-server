import constants.Path;
import data.MatsimDataProvider;
import requestHandling.NetworkRequestHandler;

import static spark.Spark.port;
import static spark.Spark.post;

public class Server {

    private static MatsimDataProvider data;

    public static void main(String[] args) {

        if (args == null || args.length == 0) {
            System.out.println("Please provide a path to some network file!!!");
        }

        String networkPath = args[0];
        initializeData(networkPath);
        initializeRoutes();
    }

    private static void initializeData(String networkFilePath) {

        //this should be configurable through command line args or a config file
        data = new MatsimDataProvider(networkFilePath, "");
    }

    private static void initializeRoutes() {

        //this is for development purposes the webpack-dev-server will proxy all calls
        //to localhost:3000/data/* to localhost:3001/data/* which is this server
        port(3001);

        post(Path.NETWORK, new NetworkRequestHandler(data));

    }
}
