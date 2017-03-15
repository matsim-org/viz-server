import constants.Path;
import data.MatsimDataProvider;
import requestHandling.AgentRequestHandler;
import requestHandling.ConfigurationRequestHandler;
import requestHandling.NetworkRequestHandler;

import static spark.Spark.port;
import static spark.Spark.post;

public class Server {

    private static MatsimDataProvider data;

    public static void main(String[] args) {

        if (args == null || args.length != 2) {
            System.out.println("Please provide a path to some network and events file!!!");
        }

        //This is for debugging purposes and should be nicer
        String networkPath = args[0];
        String eventsPath = args[1];
        initializeData(networkPath, eventsPath);
        initializeRoutes();
    }

    private static void initializeData(String networkFilePath, String eventsFilePath) {

        data = new MatsimDataProvider(networkFilePath, eventsFilePath);
    }

    private static void initializeRoutes() {

        //this is for development purposes the webpack-dev-server will proxy all calls
        //to localhost:3000/data/* to localhost:3001/data/* which is this server
        port(3001);

        post(Path.CONFIGURATION, new ConfigurationRequestHandler(data));
        post(Path.NETWORK, new NetworkRequestHandler(data));
        post(Path.AGENTS, new AgentRequestHandler(data));
    }
}
