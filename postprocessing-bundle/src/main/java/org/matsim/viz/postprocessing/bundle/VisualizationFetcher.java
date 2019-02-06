package org.matsim.viz.postprocessing.bundle;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.hibernate.Session;
import org.matsim.viz.filesApi.FilesApi;
import org.matsim.viz.filesApi.Visualization;
import org.matsim.viz.filesApi.VisualizationInput;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("ALL")
@Log
@RequiredArgsConstructor
public class VisualizationFetcher {

    private final LazySessionFactory lazySessionFactory;
    private final FilesApi api;
    private final Path tmpFiles;
    private final VisualizationGenerator generator;
    private final String vizType;

    void fetchVisualizationData() {

        val sessionFactory = lazySessionFactory.getSessionFactory();

        try (val session = sessionFactory.openSession()) {

            session.beginTransaction();

            val fetchInformation = getFetchInformation(session);
            val requestTime = Instant.now();

            Visualization[] response = api.fetchVisualizations(vizType, fetchInformation.getLastFetch().toInstant());

            fetchInformation.setLastFetch(Timestamp.from(requestTime));
            session.getTransaction().commit();

            log.info("received data for " + response.length + " visualizations");
            log.info("start processing metadata");

            for (Visualization visualization : response) {
                generateVisualization(visualization);
            }
        }
    }

    private FetchInformation getFetchInformation(EntityManager em) {

        FetchInformation fetchInformation = new JPAQueryFactory(em).selectFrom(QFetchInformation.fetchInformation).fetchFirst();
        if (fetchInformation == null) {
            fetchInformation = new FetchInformation();
            em.persist(fetchInformation);
        }
        return fetchInformation;
    }

    private void generateVisualization(Visualization inputVisualization) {

        Path folder = createTmpFolderIfNecessary(inputVisualization.getId());
        val sessionFactory = lazySessionFactory.getSessionFactory();

        try (Session session = sessionFactory.openSession()) {

            PersistentVisualization visualization = createVisualization(inputVisualization, session);

            try {
                persistProgress(visualization, PersistentVisualization.Progress.DownloadingInput, session);
                Map<String, InputFile> inputFiles = fetchInputFiles(inputVisualization, folder);
                persistProgress(visualization, PersistentVisualization.Progress.GeneratingData, session);
                generator.generate(
                        new VisualizationGenerator.Input(visualization, inputFiles, inputVisualization.getParameters(), session)
                );
                persistProgress(visualization, PersistentVisualization.Progress.Done, session);
            } catch (Exception e) {
                log.severe("something went wrong. Setting processing status to failed");
                persistProgress(visualization, PersistentVisualization.Progress.Failed, session);
                // don't throw here, to let other processing continue
            }
        } finally {
            log.info("Finished generating viz: " + inputVisualization.getId() + " remove all input files");
            removeAllInputFiles(folder);
        }
    }

    private PersistentVisualization createVisualization(Visualization inputVisualization, Session session) {

        session.beginTransaction();

        PersistentVisualization visualization = generator.createVisualization();
        visualization.setId(inputVisualization.getId());
        visualization.setProgress(PersistentVisualization.Progress.DownloadingInput);

        PersistentVisualization mergedVisualization = (PersistentVisualization) session.merge(visualization);
        inputVisualization.getPermissions().forEach(permission -> {

            Agent agent = new Agent(permission.getAgent().getAuthId());
            Agent mergedAgent = (Agent) session.merge(agent);
            val permissionToPersist = new Permission(mergedAgent, mergedVisualization);
            mergedVisualization.getPermissions().add(permissionToPersist);
            session.persist(permissionToPersist);
        });
        session.getTransaction().commit();
        return mergedVisualization;
    }

    private Map<String, InputFile> fetchInputFiles(Visualization inputVisualization, Path vizFolder) {

        Map<String, InputFile> inputFiles = new HashMap<>();
        for (val input : inputVisualization.getInputFiles().values()) {
            val filePath = fetchInputFile(inputVisualization, input, vizFolder);
            inputFiles.put(input.getInputKey(), new InputFile(input.getInputKey(), filePath));
        }

        return inputFiles;
    }

    private Path fetchInputFile(Visualization inputVisualization, VisualizationInput input, Path vizFolder) {
        log.info("fetching file: " + input.getFileEntry().getUserFileName() + " with size: " + input.getFileEntry().getSizeInBytes());
        val fileStream = api.downloadFile(inputVisualization.getProject().getId(), input.getFileEntry().getId());
        val inputFile = vizFolder.resolve(input.getFileEntry().getUserFileName());
        try {
            log.info("copy file to: " + inputFile.toString());
            Files.copy(fileStream, inputFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("something went wrong when downloading the file");
        }
        return inputFile;
    }

    private void removeAllInputFiles(Path folder) {
        try {
            FileUtils.deleteDirectory(folder.toFile());
        } catch (IOException e) {
            log.severe("Could not delete temp viz folder");
            throw new RuntimeException("Could not delete temp viz folder");
        }
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

    private void persistProgress(PersistentVisualization visualization, PersistentVisualization.Progress progress, EntityManager em) {
        em.getTransaction().begin();
        visualization.setProgress(progress);
        em.getTransaction().commit();
    }
}
