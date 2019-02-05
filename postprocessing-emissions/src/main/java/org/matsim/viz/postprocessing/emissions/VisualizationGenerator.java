package org.matsim.viz.postprocessing.emissions;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.matsim.contrib.emissions.analysis.EmissionGridAnalyzer;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.viz.error.InvalidInputException;
import org.matsim.viz.filesApi.FilesApi;
import org.matsim.viz.filesApi.VisualizationInput;
import org.matsim.viz.postprocessing.emissions.persistenceModel.Visualization;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

@Log
@RequiredArgsConstructor
class VisualizationGenerator {

    private static final String NETWORK_KEY = "network";
    private static final String EVENTS_KEY = "events";
    private static final String CELL_SIZE_KEY = "cell-size";
    private static final String SMOOTHING_RADIUS_KEY = "smoothing-radius";
    private static final String TIME_BIN_SIZE_KEY = "time-bin-size";

    private final SessionFactory sessionFactory;
    private final FilesApi filesApi;
    private final Path tmpFiles;
    private final org.matsim.viz.filesApi.Visualization inputVisualization;

    void generate() {

        if (!isValidInput(inputVisualization))
            throw new InvalidInputException("visualization from files server doesn't have the required values");

        val vizFolder = createTmpFolderIfNecessary(inputVisualization.getId());
        try (val session = sessionFactory.openSession()) {

            val visualization = createVisualization(session);

            try {
                val inputFiles = fetchInputFiles(vizFolder);
                processInputFiles(visualization, inputFiles, session);

            } catch (Exception e) {
                persistProgress(visualization, Visualization.Progress.Failed, session);
                log.severe("There was an error processing the visualization");
                throw new RuntimeException(e);
            }
        } finally {
            log.info("Finished generation of visualization. Remove all input files.");
            removeAllInputFiles(vizFolder);
        }
    }

    private void processInputFiles(Visualization visualization, Map<String, Path> inputFiles, Session session) {

        persistProgress(visualization, Visualization.Progress.GeneratingData, session);

        val network = NetworkUtils.createNetwork();
        new MatsimNetworkReader(network).readFile(inputFiles.get(NETWORK_KEY).toString());

        val analyzer = new EmissionGridAnalyzer.Builder()
                .withTimeBinSize(visualization.getTimeBinSize())
                .withGridSize(visualization.getCellSize())
                .withSmoothingRadius(visualization.getSmoothingRadius())
                .withGridType(EmissionGridAnalyzer.GridType.Hexagonal)
                .withNetwork(network)
                .build();

        log.info("Start processing emissions. This may take some while.");
        val json = analyzer.processToJsonString(inputFiles.get(EVENTS_KEY).toString());

        log.info("Finished processing emissions. Write result to database");
        session.beginTransaction();
        visualization.setData(json);
        visualization.setProgress(Visualization.Progress.Done);
        session.getTransaction().commit();
    }

    private Map<String, Path> fetchInputFiles(Path vizFolder) {

        Map<String, Path> inputFiles = new HashMap<>();
        for (val input : inputVisualization.getInputFiles().values()) {
            val filePath = fetchInputFile(input, vizFolder);
            inputFiles.put(input.getInputKey(), filePath);
        }

        return inputFiles;
    }

    private Visualization createVisualization(Session session) {

        session.beginTransaction();

        Visualization visualization = new Visualization();
        visualization.setId(inputVisualization.getId());
        visualization.setProgress(Visualization.Progress.DownloadingInput);
        visualization.setCellSize(Double.parseDouble(inputVisualization.getParameters().get(CELL_SIZE_KEY).getValue()));
        visualization.setSmoothingRadius(Double.parseDouble(inputVisualization.getParameters().get(SMOOTHING_RADIUS_KEY).getValue()));
        visualization.setTimeBinSize(Double.parseDouble(inputVisualization.getParameters().get(TIME_BIN_SIZE_KEY).getValue()));

        Visualization mergedVisualization = (Visualization) session.merge(visualization);
       /* inputVisualization.getPermissions().forEach(permission -> {

            Agent agent = new Agent(permission.getAgent().getAuthId());
            Agent mergedAgent = (Agent) session.merge(agent);
            val permissionToPersist = new Permission(mergedAgent, mergedVisualization);
            mergedVisualization.getPermissions().add(permissionToPersist);
            session.persist(permissionToPersist);
        });*/
        session.getTransaction().commit();
        return mergedVisualization;
    }

    private Path fetchInputFile(VisualizationInput input, Path vizFolder) {
        log.info("fetching file: " + input.getFileEntry().getUserFileName() + " with size: " + input.getFileEntry().getSizeInBytes());
        val fileStream = filesApi.downloadFile(inputVisualization.getProject().getId(), input.getFileEntry().getId());
        val inputFile = vizFolder.resolve(input.getFileEntry().getUserFileName());
        try {
            log.info("copy file to: " + inputFile.toString());
            Files.copy(fileStream, inputFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("something went wrong when downloading the file");
        }
        return inputFile;
    }

    private Path createTmpFolderIfNecessary(String vizId) {

        Path folder = tmpFiles.resolve(vizId);
        try {
            return Files.createDirectories(folder);
        } catch (IOException e) {
            log.severe("could not create viz folder.");
            throw new RuntimeException("Could not create temp viz folder");
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
        return visualization.getInputFiles().containsKey(NETWORK_KEY)
                && visualization.getInputFiles().containsKey(EVENTS_KEY)
                && visualization.getParameters().containsKey(CELL_SIZE_KEY)
                && visualization.getParameters().containsKey(SMOOTHING_RADIUS_KEY)
                && visualization.getParameters().containsKey(TIME_BIN_SIZE_KEY);
    }
}
