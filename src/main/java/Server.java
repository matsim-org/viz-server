import constants.Params;
import constants.Path;
import data.MatsimDataProvider;
import requestHandling.AgentRequestHandler;
import requestHandling.ConfigurationRequestHandler;
import requestHandling.NetworkRequestHandler;
import requestHandling.PlanRequestHandler;

import static spark.Spark.port;
import static spark.Spark.post;

public class Server {

    private static MatsimDataProvider data;

    private static String networkPath = "network.xml";
    private static String eventsPath = "events.xml.gz";
    private static String plansPath = "plans.xml";
    private static double snapshotPeriod = 1.0;
    private static int port = 3001;

    public static void main(String[] args) {
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
                WrongArgs(arg, "wrong order");
            }
        }
    }

    private static void handleArg(String argName, String arg) {

        if (arg.charAt(0) == '-') {
            WrongArgs(argName, arg);
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
            case Params.ARG_PLANS:
                plansPath = arg;
                break;
            case Params.ARG_PERIOD:
                snapshotPeriod = Double.parseDouble(arg);
                break;
            case Params.ARG_PORT:
                port = Integer.parseInt(arg);
                break;
            default:
                WrongArgs(argName, arg);
        }
    }

    private static void WrongArgs(String argName, String arg) {
        throw new RuntimeException("Couldn't parse argumentName: " + argName + " and value: " + arg +
                                           "\n\nPossible arguments and values are: \n\n" +
                                           "'-network path/To/network'\n" +
                                           "'-events path/to/events'\n" +
                                           "'-plans path/to/plans'\n" +
                                           "'-snapshotPeriod [some number]\n" +
                                           "'-port [some number]");
    }

    private static void initializeData() {

        data = new MatsimDataProvider(networkPath, eventsPath, plansPath, snapshotPeriod);
    }

    private static void initializeRoutes() {

        //this is for development purposes the webpack-dev-server will proxy all calls
        //to localhost:3000/data/* to localhost:3001/data/* which is this server
        System.out.println("\nStarting Server on Port: " + port + "\n");
        port(port);

        post(Path.CONFIGURATION, new ConfigurationRequestHandler(data));
        post(Path.NETWORK, new NetworkRequestHandler(data));
        post(Path.AGENTS, new AgentRequestHandler(data));
        post(Path.PLAN, new PlanRequestHandler(data));
    }
}
