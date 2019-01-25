package org.matsim.viz.frameAnimation.inputProcessing;

import lombok.val;
import org.junit.Test;
import org.matsim.viz.frameAnimation.communication.FilesAPI;
import org.matsim.viz.frameAnimation.entities.Visualization;
import org.matsim.viz.frameAnimation.utils.DatabaseTest;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class VisualizationFetcherTest extends DatabaseTest {

    @Test
    public void fetchVisualizationData_success() {

        val filesApi = mock(FilesAPI.class);
        when(filesApi.fetchVisualizations(any())).thenReturn(new Visualization[]{new Visualization()});
        val factory = mock(VisualizationGeneratorFactory.class);
        val generator = mock(VisualizationGenerator.class);
        when(factory.create(any())).thenReturn(generator);

        val testObject = new VisualizationFetcher(filesApi, factory, database.getSessionFactory());

        testObject.fetchVisualizationData();

        verify(generator, timeout(500).times(1)).generate();
    }

    @Test
    public void fetchVisualizationData_correctLastFetch() {

        Visualization viz = new Visualization();
        viz.setId("id");
        AtomicReference<Instant> lastFetch = new AtomicReference<>(Instant.now());

        val filesApi = mock(FilesAPI.class);
        when(filesApi.fetchVisualizations(any())).thenAnswer(invocationOnMock -> {
            lastFetch.set(invocationOnMock.getArgument(0));
            return new Visualization[]{viz};
        });
        val factory = mock(VisualizationGeneratorFactory.class);
        val generator = mock(VisualizationGenerator.class);
        when(factory.create(any())).thenReturn(generator);

        val testObject = new VisualizationFetcher(filesApi, factory, database.getSessionFactory());

        testObject.fetchVisualizationData();
        testObject.fetchVisualizationData();

        verify(generator, timeout(500).times(2)).generate();
        ArgumentCaptor<Instant> argrument = ArgumentCaptor.forClass(Instant.class);
        verify(filesApi, times(2)).fetchVisualizations(argrument.capture());
        assertTrue(argrument.getAllValues().get(0).isBefore(argrument.getAllValues().get(1)));
    }

    @Test
    public void fetchVisualization() {

        val filesApi = mock(FilesAPI.class);
        when(filesApi.fetchVisualizations(any())).thenReturn(new Visualization[]{new Visualization()});
        val factory = mock(VisualizationGeneratorFactory.class);
        val generator = mock(VisualizationGenerator.class);
        when(factory.create(any())).thenReturn(generator);

        val testObjectSpy = spy(new VisualizationFetcher(filesApi, factory, database.getSessionFactory()));
        testObjectSpy.fetchVisualizations();

        verify(generator, timeout(500).times(1)).generate();
        verify(testObjectSpy, timeout(500).times(1)).fetchVisualizationData();
    }

    @Test
    public void scheduleFetching() {

        val filesApi = mock(FilesAPI.class);
        when(filesApi.fetchVisualizations(any())).thenReturn(new Visualization[]{new Visualization()});
        val factory = mock(VisualizationGeneratorFactory.class);
        val generator = mock(VisualizationGenerator.class);
        when(factory.create(any())).thenReturn(generator);

        val testObjectSpy = spy(new VisualizationFetcher(filesApi, factory, database.getSessionFactory()));
        testObjectSpy.scheduleFetching();

        verify(generator, timeout(1000).times(1)).generate();
        verify(testObjectSpy, timeout(1000).times(1)).fetchVisualizationData();
    }
}
