package org.matsim.matsimwebvis.files;

import com.beust.jcommander.JCommander;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.matsimwebvis.files.config.CommandLineArgs;
import org.matsim.matsimwebvis.files.config.Configuration;
import org.matsim.matsimwebvis.files.entities.Project;
import org.matsim.matsimwebvis.files.entities.User;
import org.matsim.matsimwebvis.files.project.ProjectService;
import org.matsim.matsimwebvis.files.user.UserDAO;

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

        logger.info("\n\nStarted org.matsim.matsimwebvis.files.Server on Port: " + Configuration.getInstance().getPort() + "\n");
    }

    private static void initializeDummyEntities() {


        try {
            User user = new UserDAO().persistUser(new User());
            Project project = new ProjectService().createNewProject("test-project", user.getId());
            logger.info("\n\ntest user id: " + user.getId() + "\n\n");
            logger.info("\n\ntest project id: " + project.getId() + "\n\n");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}