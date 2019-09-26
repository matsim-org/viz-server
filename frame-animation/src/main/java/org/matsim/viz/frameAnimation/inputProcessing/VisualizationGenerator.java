package org.matsim.viz.frameAnimation.inputProcessing;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.matsim.viz.error.InvalidInputException;
import org.matsim.viz.filesApi.FilesApi;
import org.matsim.viz.filesApi.VisualizationInput;
import org.matsim.viz.frameAnimation.persistenceModel.Agent;
import org.matsim.viz.frameAnimation.persistenceModel.Permission;
import org.matsim.viz.frameAnimation.persistenceModel.Visualization;

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
class VisualizationGenerator {

    private static final String NETWORK_KEY = "network";
    private static final String EVENTS_KEY = "events";
    private static final String PLANS_KEY = "plans";
    private static final String SNAPSHOT_INTERVAL_KEY = "snapshotInterval";

    private final FilesApi filesAPI;
    private final Path tmpFolder;
    private final org.matsim.viz.filesApi.Visualization inputVisualization;
    private EntityManagerFactory emFactory;

    void generate() {

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
        } catch (Exception e) {
            log.severe("failed to create visualization! Beacuse of the following error:");
            log.severe(e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
            removeAllInputFiles(vizFolder);
        }
    }

    private Visualization createVisualization(EntityManager em) {

        em.getTransaction().begin();

        val visualization = new Visualization();
        visualization.setId(inputVisualization.getId());
        visualization.setTimestepSize(Double.parseDouble(inputVisualization.getParameters().get(SNAPSHOT_INTERVAL_KEY).getValue()));
        visualization.setProgress(Visualization.Progress.DownloadingInput);

        val mergedVisualization = em.merge(visualization);

        inputVisualization.getPermissions().forEach(permission -> {

            val agent = new Agent(permission.getAgent().getAuthId());
            val mergedAgent = em.merge(agent);
            val permissionToPersist = new Permission(mergedAgent, mergedVisualization);
            mergedVisualization.getPermissions().add(permissionToPersist);
            em.persist(permissionToPersist);
        });
        em.getTransaction().commit();
        return mergedVisualization;
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
        val fileStream = filesAPI.downloadFile(inputVisualization.getProject().getId(), input.getFileEntry().getId());
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
            log.severe("could not create viz folder");
            throw new RuntimeException("Could not create tmp folder");
        }
    }

    private void removeAllInputFiles(Path folder) {
        try {
            FileUtils.deleteDirectory(folder.toFile());
        } catch (IOException e) {
            log.severe("Could not delete temp viz folder");
            throw new RuntimeException("Could not delete temp viz folder");
        }
    }

    private void persistProgress(Visualization visualization, Visualization.Progress progress, EntityManager em) {
        em.getTransaction().begin();
        visualization.setProgress(progress);
        em.getTransaction().commit();
    }

    private boolean isValidInput(org.matsim.viz.filesApi.Visualization visualization) {

        return visualization.getInputFiles().size() == 3
                && visualization.getInputFiles().containsKey(NETWORK_KEY)
                && visualization.getInputFiles().containsKey(EVENTS_KEY)
                && visualization.getInputFiles().containsKey(PLANS_KEY)
                && visualization.getParameters().containsKey(SNAPSHOT_INTERVAL_KEY)
                && !visualization.getPermissions().isEmpty();
    }
}
