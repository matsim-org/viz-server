package org.matsim.viz.frameAnimation.inputProcessing;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import lombok.val;
import org.hibernate.SessionFactory;
import org.matsim.viz.frameAnimation.communication.FilesAPI;
import org.matsim.viz.frameAnimation.entities.VisualizationInput;
import org.matsim.viz.frameAnimation.persistenceModel.Visualization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;

@AllArgsConstructor
@Log
public class VisualizationGenerator {

    private static final String NETWORK_KEY = "network";
    private static final String EVENTS_KEY = "events";
    private static final String PLANS_KEY = "plans";
    private static final String SNAPSHOT_INTERVAL_KEY = "snapshotInterval";

    private static Logger logger = LoggerFactory.getLogger(VisualizationGenerator.class);
    private final FilesAPI filesAPI;
    private final Path tmpFolder;
    private final org.matsim.viz.frameAnimation.entities.Visualization inputVisualization;
    private SessionFactory sessionFactory;

    public void generate() {

        // insert some test, to avoid duplicate processing

        // create a visualization entity if necessary
        val visualization = createVisualization();

        // fetch all the input files
        try {
            Path vizFolder = createTmpFolderIfNecessary(visualization.getFilesServerId());
            HashMap<String, Path> inputFiles = new HashMap<>();
            for (val input : inputVisualization.getInputFiles().values()) {
                val filePath = fetchInputFile(input, vizFolder);
                inputFiles.put(input.getInputKey(), filePath);
            }
      /*      VisualizationProcessor processor = new VisualizationProcessor(
                    inputFiles.get(NETWORK_KEY),
                    inputFiles.get(EVENTS_KEY),
                    inputFiles.get(PLANS_KEY),
                    visualization.getTimestepSize(),
                    sessionFactory
            );
            processor.processVisualization();
*/

        } catch (Exception e) {

        }

    }

    private Visualization createVisualization() {
        val visualization = new Visualization();
        visualization.setTimestepSize(Double.parseDouble(inputVisualization.getParameters().get(SNAPSHOT_INTERVAL_KEY).getValue()));
        visualization.setProgress(Visualization.Progress.DownloadingInput);
        visualization.setFilesServerId(inputVisualization.getId());
        sessionFactory.getCurrentSession().save(visualization);
        return visualization;
    }

    private Path fetchInputFile(VisualizationInput input, Path vizFolder) {
        val fileStream = filesAPI.fetchFile(inputVisualization.getProject().getId(), input.getFileEntry().getId());
        val inputFile = vizFolder.resolve(input.getFileEntry().getUserFileName());
        try {
            Files.copy(fileStream, inputFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("something went wrong when downloading the file");
        }
        return inputFile;
    }

    private Path createTmpFolderIfNecessary(String vizId) {

        Path vizFolder = tmpFolder.resolve(vizId);
        try {
            return Files.createDirectories(vizFolder);
        } catch (IOException e) {
            logger.error("could not create viz folder");
            throw new RuntimeException("Could not create tmp folder");
        }
    }
}
