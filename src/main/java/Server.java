import constants.Params;
import constants.Path;
import data.MatsimDataProvider;
import requestHandling.AgentRequestHandler;
import requestHandling.ConfigurationRequestHandler;
import requestHandling.NetworkRequestHandler;

import static spark.Spark.*;

public class Server {


    private static MatsimDataProvider data;

    private static String networkPath = "network.xml";
    private static String eventsPath = "events.xml.gz";
    private static double snapshotPeriod = 1.0;
    private static int port = 3001;

    public static void main(String[] args) {

        if (args == null || args.length != 2) {
            System.out.println("Please provide a path to some network and events file!!!");
        }

        //This is for debugging purposes and should be nicer
        parseArgs(args);
        initializeData();
        initializeRoutes();
    }

    private static void parseArgs(String[] args) {

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.charAt(0) == '-') {
                handleArg(arg, args[i + 1]);
                i++;
            } else {
                WrongArgs();
            }
        }
    }

    private static void handleArg(String argName, String arg) {

        if (arg.charAt(0) == '-') {
            WrongArgs();
        }

        //cut of '-'
        String name = argName.substring(1);

        switch (name) {
            case Params.ARG_NETWORK:
                networkPath = arg;
                break;
            case Params.ARG_EVENTS:
                eventsPath = arg;
                break;
            case Params.ARG_PERIOD:
                snapshotPeriod = Double.parseDouble(arg);
                break;
            case Params.ARG_PORT:
                port = Integer.parseInt(arg);
            default:
                WrongArgs();
        }
    }

    private static void WrongArgs() {
        throw new RuntimeException("Please supply correct arguments like \\n\\n'-network path/To/network'\\n'-events path/to/events");
    }

    private static void initializeData() {

        data = new MatsimDataProvider(networkPath, eventsPath, snapshotPeriod);
    }

    private static void initializeRoutes() {

        //this is for development purposes the webpack-dev-server will proxy all calls
        //to localhost:3000/data/* to localhost:3001/data/* which is this server
        port(port);

        post(Path.CONFIGURATION, new ConfigurationRequestHandler(data));
        post(Path.NETWORK, new NetworkRequestHandler(data));
        post(Path.AGENTS, new AgentRequestHandler(data));

        after((request, response) -> {
            response.header("Content-Encoding", "gzip");
        });
    }
}
