package org.matsim.viz.frameAnimation.data;

import org.apache.commons.io.FileUtils;
import org.matsim.viz.error.InternalException;
import org.matsim.viz.error.InvalidInputException;
import org.matsim.viz.error.UnauthorizedException;
import org.matsim.viz.frameAnimation.communication.FilesAPI;
import org.matsim.viz.frameAnimation.config.AppConfiguration;
import org.matsim.viz.frameAnimation.entities.Visualization;
import org.matsim.viz.frameAnimation.entities.VisualizationInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

class DataGenerator {

    private static final String NETWORK_KEY = "network";
    private static final String EVENTS_KEY = "events";
    private static final String PLANS_KEY = "plans";
    private static final String SNAPSHOT_INTERVAL_KEY = "snapshotInterval";

    private static Logger logger = LoggerFactory.getLogger(DataGenerator.class);
    private static final Path tempFolder = createTempFolder(AppConfiguration.getInstance().getTmpFilePath());
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(8);

    FilesAPI filesAPI = new FilesAPI(AppConfiguration.getInstance().getFileServer());
    SimulationDataFactory simulationDataFactory = new SimulationDataFactory();
    DataProvider dataProvider = DataProvider.Instance;
    private final Visualization visualization;
    private final VisualizationData visualizationData;
    private Path vizFolder;
    private Map<String, Path> writtenFiles = new HashMap<>();


    DataGenerator(Visualization visualization) {
        if (!isValidInput(visualization))
            throw new InvalidInputException("visualization did not contain required input");
        this.visualization = visualization;
        visualizationData = new VisualizationData();
        visualizationData.setPermissions(visualization.getPermissions());
    }

    private static Path createVizFolder(String vizId) {

        Path folder = tempFolder.resolve(vizId);
        try {
            return Files.createDirectories(folder);
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

    private void removeAllInputFiles() {
        try {
            FileUtils.deleteDirectory(vizFolder.toFile());
        } catch (IOException e) {
            logger.error("Could not delete temporary viz folder", e);
            throw new RuntimeException("Could not delete temporary viz folder", e);
        }
    }

    private boolean isValidInput(Visualization visualization) {
        return visualization.getInputFiles().size() == 3
                && visualization.getInputFiles().containsKey(NETWORK_KEY)
                && visualization.getInputFiles().containsKey(EVENTS_KEY)
                && visualization.getInputFiles().containsKey(PLANS_KEY)
                && visualization.getParameters().size() == 1
                && visualization.getParameters().containsKey(SNAPSHOT_INTERVAL_KEY)
                && !visualization.getPermissions().isEmpty();
    }

    void generate() {

        if (dataProvider.contains(visualization.getId())) {
            logger.info("Visualization: " + visualization.getId() +
                    " is already present and has status: " + dataProvider.getProgress(visualization.getId()));
            return;
        }

        visualizationData.setProgress(VisualizationData.Progress.DownloadingInput);
        dataProvider.add(visualization.getId(), visualizationData);
        vizFolder = createVizFolder(this.visualization.getId());
        try {
            for (VisualizationInput input : visualization.getInputFiles().values())
                fetchFile(input);

            visualizationData.setProgress(VisualizationData.Progress.GeneratingData);
            SimulationData data = simulationDataFactory.createSimulationData(
                    writtenFiles.get(NETWORK_KEY).toAbsolutePath().toString(),
                    writtenFiles.get(EVENTS_KEY).toAbsolutePath().toString(),
                    writtenFiles.get(PLANS_KEY).toAbsolutePath().toString(),
                    Integer.parseInt(visualization.getParameters().get(SNAPSHOT_INTERVAL_KEY).getValue())
            );
            visualizationData.setSimulationData(data);
            visualizationData.setProgress(VisualizationData.Progress.Done);
        } catch (UnauthorizedException e) {
            dataProvider.remove(visualization.getId());
        } catch (Exception e) {
            logger.error("Error while fetching input files", e);
            visualizationData.setProgress(VisualizationData.Progress.Failed);
        } finally {
            removeAllInputFiles();
            logger.info("Done processing data for viz: " + visualization.getId());
        }
    }

    private void fetchFile(VisualizationInput input) throws IOException {

        InputStream fileStream = filesAPI.fetchFile(visualization.getProject().getId(), input.getFileEntry().getId());

            Path inputFile = vizFolder.resolve(input.getFileEntry().getUserFileName());
        // assume that if any artifacts in that folder are present, they are from an interrupted fetch and are no longer needed.
        Files.copy(fileStream, inputFile, StandardCopyOption.REPLACE_EXISTING);
        writtenFiles.put(input.getInputKey(), inputFile);
    }
}
