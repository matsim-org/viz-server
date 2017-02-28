import constants.Path;
import data.MatsimDataProvider;
import requestHandling.NetworkRequestHandler;

import static spark.Spark.get;

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

        get(Path.NETWORK, new NetworkRequestHandler(data));

    }
}
