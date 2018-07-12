package org.matsim.webvis.frameAnimation.data;

import org.apache.commons.io.FileUtils;
import org.glassfish.jersey.client.oauth2.OAuth2ClientSupport;
import org.matsim.webvis.error.InternalException;
import org.matsim.webvis.error.InvalidInputException;
import org.matsim.webvis.frameAnimation.communication.ServiceCommunication;
import org.matsim.webvis.frameAnimation.config.AppConfiguration;
import org.matsim.webvis.frameAnimation.entities.Visualization;
import org.matsim.webvis.frameAnimation.entities.VisualizationInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

class SimulationDataFetcher {

    private static final String NETWORK_KEY = "network";
    private static final String EVENTS_KEY = "events";
    private static final String PLANS_KEY = "plans";
    private static final String SNAPSHOT_INTERVAL_KEY = "snapshotInterval";

    private static Logger logger = LoggerFactory.getLogger(SimulationDataFetcher.class);
    private static final SimulationDataDAO simulationDataDAO = new SimulationDataDAO();
    private static final Path tempFolder = createTempFolder(AppConfiguration.getInstance().getTmpFilePath());

    private final Visualization visualization;
    private Path vizFolder;
    private Map<String, Path> writtenFiles = new HashMap<>();


    private SimulationDataFetcher(Visualization visualization) {
        if (!isValidInput(visualization))
            throw new InvalidInputException("visualization did not contain required input");
        this.visualization = visualization;
    }

    static void generateVisualization(Visualization visualization) {
        SimulationDataFetcher generator = new SimulationDataFetcher(visualization);
        generator.generate();
    }

    private static Path createVizFolder(String vizId) {

        Path folder = tempFolder.resolve(vizId);
        try {
            return Files.createDirectory(folder);
        } catch (IOException e) {
            logger.error("could not create viz directory", e);
            throw new InternalException("Could not create viz directory");
        }
    }

    private static Path createTempFolder(String relativePath) {

        Path directory = Paths.get(relativePath);
        try {
            return Files.createDirectories(directory);
        } catch (IOException e) {
            logger.error("Error while creating temp directory", e);
            throw new InvalidInputException("Could not create temp directory");
        }
    }

    private void generate() {

        vizFolder = createVizFolder(this.visualization.getId());
        try {
            for (VisualizationInput input : visualization.getInputFiles().values())
                fetchFile(input);

            SimulationData data = new SimulationData(
                    writtenFiles.get(NETWORK_KEY).toAbsolutePath().toString(),
                    writtenFiles.get(EVENTS_KEY).toAbsolutePath().toString(),
                    writtenFiles.get(PLANS_KEY).toAbsolutePath().toString(),
                    Integer.parseInt(visualization.getParameters().get(SNAPSHOT_INTERVAL_KEY).getValue())
            );
            simulationDataDAO.add(visualization.getId(), data);
        } catch (IOException e) {
            logger.error("Error while fetching input files", e);
        } finally {
            removeAllInputFiles();
        }
    }

    private void fetchFile(VisualizationInput input) throws IOException {


        URI uri = UriBuilder.fromUri(AppConfiguration.getInstance().getFileServer())
                .path("projects").path(visualization.getProject().getId()).path("files")
                .path(input.getFileEntry().getId()).build();
        try {
            InputStream fileStream = ServiceCommunication.getClient().target(uri)
                    .request()
                    .property(OAuth2ClientSupport.OAUTH2_PROPERTY_ACCESS_TOKEN, ServiceCommunication.getAuthentication().getAccessToken())
                    .get(InputStream.class);

            Path inputFile = vizFolder.resolve(input.getFileEntry().getUserFileName());
            Files.copy(fileStream, inputFile);
            writtenFiles.put(input.getKey(), inputFile);
        } catch (RuntimeException e) {
            logger.error("error while fetching file: ", e);
        }

    }

    private void removeAllInputFiles() {
        try {
            FileUtils.deleteDirectory(vizFolder.toFile());
        } catch (IOException e) {
            logger.error("Could not delete temporary viz folder");
            throw new RuntimeException("Could not delete temporary viz folder", e);
        }
    }

    private boolean isValidInput(Visualization visualization) {
        return visualization.getInputFiles().size() == 3
                && visualization.getInputFiles().containsKey(NETWORK_KEY)
                && visualization.getInputFiles().containsKey(EVENTS_KEY)
                && visualization.getInputFiles().containsKey(PLANS_KEY)
                && visualization.getParameters().size() == 1
                && visualization.getParameters().containsKey(SNAPSHOT_INTERVAL_KEY);
    }
}
