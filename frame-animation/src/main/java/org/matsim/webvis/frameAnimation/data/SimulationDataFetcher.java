package org.matsim.webvis.frameAnimation.data;

import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.webvis.common.auth.ClientAuthentication;
import org.matsim.webvis.common.communication.Http;
import org.matsim.webvis.common.errorHandling.InternalException;
import org.matsim.webvis.common.errorHandling.InvalidInputException;
import org.matsim.webvis.common.errorHandling.UnauthorizedException;
import org.matsim.webvis.frameAnimation.communication.ServiceCommunication;
import org.matsim.webvis.frameAnimation.config.Configuration;
import org.matsim.webvis.frameAnimation.entities.FileEntry;
import org.matsim.webvis.frameAnimation.entities.Visualization;
import org.matsim.webvis.frameAnimation.entities.VisualizationInput;
import org.matsim.webvis.frameAnimation.entities.VisualizationParameter;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class SimulationDataFetcher {

    public static final String NETWORK_KEY = "network";
    public static final String EVENTS_KEY = "events";
    public static final String PLANS_KEY = "plans";
    public static final String SNAPSHOT_INVERVAL_KEY = "snapshotInterval";


    private static Logger logger = LogManager.getLogger();
    SimulationDataDAO simulationDataDAO = new SimulationDataDAO();
    private Path tmpFilePath = createFolder(Configuration.getInstance().getTmpFilePath());
    private URI fileEndpoint = Configuration.getInstance().getFileServer().resolve("/file/");
    private Http http = ServiceCommunication.http();
    private ClientAuthentication authentication = ServiceCommunication.authentication();

    public void generateSimulationData(Visualization visualization) {

        Path vizDirectory = createFolder(tmpFilePath.resolve(visualization.getId()));

        VisualizationInput network = extractRequiredValue(visualization.getInputFiles(), "network");
        VisualizationInput events = extractRequiredValue(visualization.getInputFiles(), "events");
        VisualizationInput plans = extractRequiredValue(visualization.getInputFiles(), "plans");
        VisualizationParameter snapshotInterval = extractRequiredValue(visualization.getParameters(), "SnapshotIntervall");

        try {
            Path networkFile = fetchFile(visualization.getProject().getId(), network.getFileEntry(), vizDirectory);
            Path eventsFile = fetchFile(visualization.getProject().getId(), events.getFileEntry(), vizDirectory);
            Path plansFile = fetchFile(visualization.getProject().getId(), plans.getFileEntry(), vizDirectory);

            SimulationData simData = new SimulationData(
                    networkFile.toAbsolutePath().toString(),
                    eventsFile.toAbsolutePath().toString(),
                    plansFile.toAbsolutePath().toString(),
                    Integer.parseInt(snapshotInterval.getValue())
            );
            simulationDataDAO.add(visualization.getId(), simData);
        } catch (UnauthorizedException e) {
            throw e;
        } catch (Exception e) {
            //clean up the temp files
            logger.error("Error while downloading files", e);
        }
    }

    private Path fetchFile(String projectId, FileEntry input, Path vizFolder) throws IOException {

        Path tmpFile = vizFolder.resolve(input.getUserFileName());
        Files.createFile(tmpFile);

        http.post(fileEndpoint)
                .withJsonBody(new FileRequest(projectId, input.getId()))
                .withCredential(authentication)
                .executeWithFileResponse(tmpFile);
        return tmpFile;
    }

    private void removeTmpFile(Path file) throws IOException {
        Files.delete(file);
    }

    private <T> T extractRequiredValue(Map<String, T> map, String key) {
        if (!map.containsKey(key))
            throw new InternalException("required value " + key + " is not present");
        return map.get(key);
    }

    private Path createFolder(String relativePath) {

        Path directory = Paths.get(relativePath);
        return createFolder(directory);
    }

    private Path createFolder(Path path) {
        try {
            return Files.createDirectories(path);
        } catch (IOException e) {
            logger.error("Error while creating tmp directory.", e);
            throw new InvalidInputException("Could not create tmp directory");
        }
    }

   /* private void validateInput(Visualization visualization) {
        if (!visualization.getInputFiles().containsKey(NETWORK_KEY)
                || !visualization.getInputFiles().containsKey(EVENTS_KEY)
                || !visualization.getInputFiles().c
                )
    }*/

    @AllArgsConstructor
    private static class FileRequest {

        final String projectId;
        final String fileId;
    }
}
