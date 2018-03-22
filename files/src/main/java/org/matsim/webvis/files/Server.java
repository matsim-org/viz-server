package org.matsim.webvis.files;

import com.beust.jcommander.JCommander;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.webvis.files.config.CommandLineArgs;
import org.matsim.webvis.files.config.Configuration;
import org.matsim.webvis.files.entities.Project;
import org.matsim.webvis.files.entities.User;
import org.matsim.webvis.files.project.ProjectService;
import org.matsim.webvis.files.user.UserDAO;

import java.io.FileNotFoundException;

import static spark.Spark.port;

public class Server {

    private static Logger logger = LogManager.getLogger();

    public static void main(String[] args) {

        CommandLineArgs commandLineArgs = new CommandLineArgs();
        JCommander.newBuilder().addObject(commandLineArgs).build().parse(args);

        try {
            loadConfigFile(commandLineArgs);
        } catch (FileNotFoundException e) {
            logger.error(e);
        }


        startSparkServer(commandLineArgs);
        initializeDummyEntities();
    }

    private static void loadConfigFile(CommandLineArgs args) throws FileNotFoundException {

        if (!args.getConfigFile().isEmpty()) {
            Configuration.loadConfigFile(args.getConfigFile(), args.isDebug());
        }
    }

    private static void startSparkServer(CommandLineArgs args) {
        port(Configuration.getInstance().getPort());
        Routes.initialize();

        logger.info("\n\nStarted Server on Port: " + Configuration.getInstance().getPort() + "\n");
    }

    private static void initializeDummyEntities() {


        try {
            User user = new UserDAO().persistUser(new User());
            Project project = new ProjectService().createNewProject("test-project", user.getId());
            logger.info("\n\ntest org.matsim.webvis.auth.user id: " + user.getId() + "\n\n");
            logger.info("\n\ntest project id: " + project.getId() + "\n\n");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}