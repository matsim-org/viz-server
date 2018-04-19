package org.matsim.webvis.files;

import com.beust.jcommander.JCommander;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.webvis.files.config.CommandLineArgs;
import org.matsim.webvis.files.config.Configuration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static spark.Spark.port;

public class Server {

    private static Logger logger = LogManager.getLogger();

    public static void main(String[] args) {

        CommandLineArgs commandLineArgs = new CommandLineArgs();
        JCommander.newBuilder().addObject(commandLineArgs).build().parse(args);

        try {
            loadConfigFile(commandLineArgs);
            createUploadDirectories();
        } catch (IOException e) {
            logger.error(e);
            System.exit(10);
        }

        startSparkServer(commandLineArgs);
    }

    private static void loadConfigFile(CommandLineArgs args) throws FileNotFoundException {

        if (!args.getConfigFile().isEmpty()) {
            Configuration.loadConfigFile(args.getConfigFile(), args.isDebug());
        }
    }

    private static void createUploadDirectories() throws IOException {
        Path tmpUploadDirectory = Paths.get(Configuration.getInstance().getTmpFilePath());
        Files.createDirectories(tmpUploadDirectory);

        Path uploadDirectory = Paths.get(Configuration.getInstance().getUploadedFilePath());
        Files.createDirectories(uploadDirectory);
    }

    private static void startSparkServer(CommandLineArgs args) {
        port(Configuration.getInstance().getPort());
        Routes.initialize();

        logger.info("\n\nStarted Server on Port: " + Configuration.getInstance().getPort() + "\n");
    }
}