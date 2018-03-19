import com.beust.jcommander.JCommander;
import config.CommandLineArgs;
import config.Configuration;

import java.io.FileNotFoundException;

import static spark.Spark.port;

public class Server {

    static void loadConfigFile(CommandLineArgs args) throws FileNotFoundException {

        if (!args.getConfigFile().isEmpty()) {
            Configuration.loadConfigFile(args.getConfigFile(), args.isDebug());
        }
    }

    static void startSparkServer(CommandLineArgs args) {
        port(Configuration.getInstance().getPort());


    }

    public void main(String[] args) {

        CommandLineArgs commandLineArgs = new CommandLineArgs();
        JCommander.newBuilder().addObject(commandLineArgs).build().parse(args);
    }
}
