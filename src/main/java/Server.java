import constants.Path;
import data.MatsimDataProvider;
import requestHandling.NetworkRequestHandler;

import static spark.Spark.port;
import static spark.Spark.post;

public class Server {

    private static MatsimDataProvider data;

    public static void main(String[] args) {

        initializeData();
        initializeRoutes();
    }

    private static void initializeData() {

        //this should be configurable through command line args or a config file
        data = new MatsimDataProvider("src/test/data/two-routes-test-network.xml", "");
    }

    private static void initializeRoutes() {

        //this is for development purposes the webpack-dev-server will proxy all calls
        //to localhost:3000/data/* to localhost:3001/data/* which is this server
        port(3001);

        post(Path.NETWORK, new NetworkRequestHandler(data));

    }
}
