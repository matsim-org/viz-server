package org.matsim.viz.frameAnimation.inputProcessing;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.matsim.viz.error.InvalidInputException;
import org.matsim.viz.frameAnimation.communication.FilesAPI;
import org.matsim.viz.frameAnimation.entities.VisualizationInput;
import org.matsim.viz.frameAnimation.persistenceModel.Visualization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

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
    private EntityManagerFactory emFactory;

    public void generate() {

        // insert some test, to avoid duplicate processing
        if (!isValidInput(inputVisualization))
            throw new InvalidInputException("visualization from files server doesn't have required values");

        Path vizFolder = createTmpFolderIfNecessary(inputVisualization.getId());
        val em = emFactory.createEntityManager();

        try {
            val visualization = createVisualization(em);
            try {
                val inputFiles = fetchInputFiles(vizFolder);
                processInputFiles(visualization, inputFiles, em);
            } catch (Exception e) {
                persistProgress(visualization, Visualization.Progress.Failed, em);
            }
        } finally {
            em.close();
            removeAllInputFiles(vizFolder);
        }
    }

    private Visualization createVisualization(EntityManager em) {
        val visualization = new Visualization();
        visualization.setTimestepSize(Double.parseDouble(inputVisualization.getParameters().get(SNAPSHOT_INTERVAL_KEY).getValue()));
        visualization.setProgress(Visualization.Progress.DownloadingInput);
        visualization.setFilesServerId(inputVisualization.getId());
        em.getTransaction().begin();
        em.persist(visualization);
        em.getTransaction().commit();
        return visualization;
    }

    private Map<String, Path> fetchInputFiles(Path vizFolder) {

        Map<String, Path> inputFiles = new HashMap<>();
        for (val input : inputVisualization.getInputFiles().values()) {
            val filePath = fetchInputFile(input, vizFolder);
            inputFiles.put(input.getInputKey(), filePath);
        }

        return inputFiles;
    }

    private void processInputFiles(Visualization visualization, Map<String, Path> inputFiles, EntityManager em) {

        VisualizationProcessor processor = new VisualizationProcessor(
                inputFiles.get(NETWORK_KEY),
                inputFiles.get(EVENTS_KEY),
                inputFiles.get(PLANS_KEY),
                visualization,
                emFactory
        );
        persistProgress(visualization, Visualization.Progress.GeneratingData, em);
        processor.processVisualization();
        persistProgress(visualization, Visualization.Progress.Done, em);
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

    private void removeAllInputFiles(Path folder) {
        try {
            FileUtils.deleteDirectory(folder.toFile());
        } catch (IOException e) {
            logger.error("Could not delete temp viz folder");
            throw new RuntimeException("Could not delete temp viz folder");
        }
    }

    private void persistProgress(Visualization visualization, Visualization.Progress progress, EntityManager em) {
        em.getTransaction().begin();
        visualization.setProgress(progress);
        em.getTransaction().commit();
    }

    private boolean isValidInput(org.matsim.viz.frameAnimation.entities.Visualization visualization) {

        return visualization.getInputFiles().size() == 3
                && visualization.getInputFiles().containsKey(NETWORK_KEY)
                && visualization.getInputFiles().containsKey(EVENTS_KEY)
                && visualization.getInputFiles().containsKey(PLANS_KEY)
                && visualization.getParameters().size() == 1
                && visualization.getParameters().containsKey(SNAPSHOT_INTERVAL_KEY)
                && !visualization.getPermissions().isEmpty();
    }
}
