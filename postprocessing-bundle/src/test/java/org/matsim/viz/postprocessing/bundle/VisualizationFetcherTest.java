package org.matsim.viz.postprocessing.bundle;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.dropwizard.testing.junit.DAOTestRule;
import lombok.val;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.matsim.viz.filesApi.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class VisualizationFetcherTest {

    private final Path tmpFiles = Paths.get("./tmpFiles");
    @Rule
    public DAOTestRule database = DAOTestRule.newBuilder()
            .addEntityClass(PersistentVisualization.class)
            .addEntityClass(Agent.class)
            .addEntityClass(Permission.class)
            .addEntityClass(FetchInformation.class)
            .setShowSql(true)
            .build();
    private FilesApi filesApi;

    @Before
    public void setUp() {
        filesApi = mock(FilesApi.class);
    }


    @Test
    public void fetchVisualizationData_receivedZeroViz() {

        VisualizationGenerator generator = (visualization, inputFiles, parameters) -> fail("generate should not be called if no viz is fetched");
        when(filesApi.fetchVisualizations(anyString(), any())).thenReturn(new Visualization[0]);
        Instant beforeCall = Instant.now();
        VisualizationFetcher fetcher = new VisualizationFetcher(database.getSessionFactory(), filesApi, tmpFiles, generator);

        fetcher.fetchVisualizationData();

        // assert that last fetch time is updated correctly
        try (val session = database.getSessionFactory().openSession()) {
            val fetchInfo = new JPAQueryFactory(session).selectFrom(QFetchInformation.fetchInformation).fetchFirst();
            assertTrue(beforeCall.isBefore(fetchInfo.lastFetch.toInstant()));
        }
    }

    @Test
    public void fetchVisualizationData_receivedOneViz() {

        final Visualization fetchedViz = createVisualization("some-id");
        VisualizationGenerator generator = mock(VisualizationGenerator.class);
        when(filesApi.fetchVisualizations(anyString(), any())).thenReturn(new Visualization[]{fetchedViz});
        Instant beforeCall = Instant.now();
        VisualizationFetcher fetcher = new VisualizationFetcher(database.getSessionFactory(), filesApi, tmpFiles, generator);

        fetcher.fetchVisualizationData();

        try (val session = database.getSessionFactory().openSession()) {

            // assert that last fetch time is updated correctly
            val fetchInfo = new JPAQueryFactory(session).selectFrom(QFetchInformation.fetchInformation).fetchFirst();
            assertTrue(beforeCall.isBefore(fetchInfo.lastFetch.toInstant()));

            // assert that viz is persisted
            PersistentVisualization persistentVisualization = session.find(PersistentVisualization.class, fetchedViz.getId());
            assertNotNull(persistentVisualization);

            // assert that permissions are stored correctly
            // createVisualization stores two permissions
            assertEquals(2, persistentVisualization.getPermissions().size());
            persistentVisualization.getPermissions().forEach(permission -> assertNotNull(permission.getAgent()));
            assertEquals(PersistentVisualization.Progress.Done, persistentVisualization.getProgress());

            // assert that 'generate' is called once with proper parameters
            verify(generator, times(1)).generate(any(), any(), any());

            // assert that all input files are removed and folder is deleted
            Path vizFolder = tmpFiles.resolve(fetchedViz.getId());
            assertTrue(Files.notExists(vizFolder));
        }
    }

    @Test
    public void fetchVisualizationData_receivedTwoViz() {

        final Visualization fetchedViz = createVisualization("some-id");
        final Visualization otherFetchedViz = createVisualization("some-other-id");
        VisualizationGenerator generator = mock(VisualizationGenerator.class);
        when(filesApi.fetchVisualizations(anyString(), any())).thenReturn(new Visualization[]{fetchedViz, otherFetchedViz});
        Instant beforeCall = Instant.now();
        VisualizationFetcher fetcher = new VisualizationFetcher(database.getSessionFactory(), filesApi, tmpFiles, generator);

        fetcher.fetchVisualizationData();

        try (val session = database.getSessionFactory().openSession()) {

            // assert that last fetch time is updated correctly
            val fetchInfo = new JPAQueryFactory(session).selectFrom(QFetchInformation.fetchInformation).fetchFirst();
            assertTrue(beforeCall.isBefore(fetchInfo.lastFetch.toInstant()));

            // assert that vizes are persisted
            PersistentVisualization persistentVisualization = session.find(PersistentVisualization.class, fetchedViz.getId());
            assertNotNull(persistentVisualization);
            PersistentVisualization otherPersistentVisualization = session.find(PersistentVisualization.class, otherFetchedViz.getId());
            assertNotNull(otherPersistentVisualization);

            // assert that permissions are stored correctly
            // createVisualization stores two permissions
            assertEquals(2, persistentVisualization.getPermissions().size());
            persistentVisualization.getPermissions().forEach(permission -> assertNotNull(permission.getAgent()));
            assertEquals(2, otherPersistentVisualization.getPermissions().size());
            otherPersistentVisualization.getPermissions().forEach(permission -> assertNotNull(permission.getAgent()));

            // assert that 'generate' is called once with proper parameters
            verify(generator, times(2)).generate(any(), any(), any());

            // assert that all input files are removed and folder is deleted
            Path vizFolder = tmpFiles.resolve(fetchedViz.getId());
            assertTrue(Files.notExists(vizFolder));
            Path otherVizFolder = tmpFiles.resolve(otherFetchedViz.getId());
            assertTrue(Files.notExists(otherVizFolder));
        }
    }

    @Test
    public void fetchVisualizationData_ReceivedOneViz_errorDuringProcess() {

        final Visualization fetchedViz = createVisualization("some-id");
        VisualizationGenerator generator = (viz, input, params) -> {
            throw new RuntimeException("Error");
        };
        when(filesApi.fetchVisualizations(anyString(), any())).thenReturn(new Visualization[]{fetchedViz});
        VisualizationFetcher fetcher = new VisualizationFetcher(database.getSessionFactory(), filesApi, tmpFiles, generator);

        fetcher.fetchVisualizationData();

        try (val session = database.getSessionFactory().openSession()) {

            // assert viz is peristed with status Failed
            PersistentVisualization persistentVisualization = session.find(PersistentVisualization.class, fetchedViz.getId());
            assertEquals(PersistentVisualization.Progress.Failed, persistentVisualization.getProgress());

            // assert that all input files are removed and folder is deleted
            Path vizFolder = tmpFiles.resolve(fetchedViz.getId());
            assertTrue(Files.notExists(vizFolder));
        }
    }

    @Test
    public void fetchVisualizationData_ReceivedOneViz_errorDuringFileFetching() {

        final Visualization fetchedViz = createVisualization("some-id");
        FileEntry fileEntry = new FileEntry("some-filename.txt", "application/json", 0, fetchedViz.getProject());
        fileEntry.setId("file-entry-id");
        fetchedViz.getInputFiles().put("first", new VisualizationInput("first", fileEntry));
        VisualizationGenerator generator = (viz, input, params) -> {
            throw new RuntimeException("Error");
        };
        when(filesApi.fetchVisualizations(anyString(), any())).thenReturn(new Visualization[]{fetchedViz});
        when(filesApi.downloadFile(anyString(), anyString())).thenThrow(new RuntimeException("some error while fetching files"));
        VisualizationFetcher fetcher = new VisualizationFetcher(database.getSessionFactory(), filesApi, tmpFiles, generator);

        fetcher.fetchVisualizationData();

        try (val session = database.getSessionFactory().openSession()) {

            // assert viz is peristed with status Failed
            PersistentVisualization persistentVisualization = session.find(PersistentVisualization.class, fetchedViz.getId());
            assertEquals(PersistentVisualization.Progress.Failed, persistentVisualization.getProgress());

            // assert that all input files are removed and folder is deleted
            Path vizFolder = tmpFiles.resolve(fetchedViz.getId());
            assertTrue(Files.notExists(vizFolder));
        }

        verify(filesApi, times(1)).downloadFile(anyString(), anyString());
    }

    private Visualization createVisualization(String vizId) {


        Project project = new Project("project-name");
        project.setId("some-project-id");
        Set<org.matsim.viz.filesApi.Permission> permissions = new HashSet<>();
        permissions.add(new org.matsim.viz.filesApi.Permission(new org.matsim.viz.filesApi.Agent("some-id")));
        permissions.add(new org.matsim.viz.filesApi.Permission(new org.matsim.viz.filesApi.Agent("some-other-id")));

        Visualization result = new Visualization(project, permissions, new HashMap<>(), new HashMap<>());
        result.setId(vizId);
        return result;
    }
}