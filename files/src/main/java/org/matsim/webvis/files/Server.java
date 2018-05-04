package org.matsim.webvis.files;

import com.beust.jcommander.JCommander;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.webvis.files.config.CommandLineArgs;
import org.matsim.webvis.files.config.Configuration;
import org.matsim.webvis.files.entities.VisualizationType;
import org.matsim.webvis.files.visualization.VisualizationService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static spark.Spark.*;

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
        insertVizTypes();
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
        initExceptionHandler(Server::handleInitializationFailure);

        if (!args.isDebug() || !Configuration.getInstance().getTlsKeyStore().isEmpty()) {
            secure(Configuration.getInstance().getTlsKeyStore(), Configuration.getInstance().getTlsKeyStorePassword(), null, null);
        }
        try {
            Routes.initialize();
        } catch (Exception e) {
            handleInitializationFailure(e);
        }

        logger.info("\n\nStarted Server on Port: " + Configuration.getInstance().getPort() + "\n");
    }

    private static void handleInitializationFailure(Exception e) {

        String tlsKeyStore = Configuration.getInstance().getTlsKeyStore();
        String tlsKeyStorePassword = Configuration.getInstance().getTlsKeyStorePassword();

        if (tlsKeyStore == null || tlsKeyStore.isEmpty()) {
            logger.error("\n\nInitialization failed. TlsKeystore location was not present.\n\n");
        } else if (tlsKeyStorePassword == null || tlsKeyStorePassword.isEmpty()) {
            logger.error("\n\nInitialization failed. TlsKeystore password was not present\n\n");
        } else {
            logger.error("Exception which caused failure was: ", e);
        }
    }

    private static void insertVizTypes() {

        VisualizationService service = new VisualizationService();
        for (VisualizationType type : Configuration.getInstance().getVizTypes()) {
            logger.info("Add viz type: " + type.getKey());
            service.persistType(type);
        }
    }
}