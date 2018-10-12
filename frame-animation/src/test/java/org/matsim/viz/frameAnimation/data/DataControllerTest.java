package org.matsim.viz.frameAnimation.data;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.matsim.viz.frameAnimation.communication.FilesAPI;
import org.matsim.viz.frameAnimation.entities.Visualization;
import org.matsim.viz.frameAnimation.utils.TestUtils;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicReference;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DataControllerTest {

    private DataController testObject;

    @BeforeClass
    public static void setUpFixture() {
        TestUtils.loadConfig();
    }

    @Before
    public void setUp() {
        testObject = new DataController();
    }

    @Test
    public void fetchVisualizationData_success() {

        Visualization viz = new Visualization();
        viz.setId("id");

        testObject.filesAPI = mock(FilesAPI.class);
        testObject.dataGeneratorFactory = mock(DataGeneratorFactory.class);

        DataGenerator generator = mock(DataGenerator.class);
        doNothing().when(generator).generate();
        when(testObject.dataGeneratorFactory.createGenerator(any())).thenReturn(generator);
        when(testObject.filesAPI.fetchVisualizations(any())).thenReturn(new Visualization[]{viz});

        testObject.fetchVisualizationData();

        verify(generator, timeout(100).atLeastOnce()).generate();
    }

    @Test
    public void fetchVisualizationData_correctLastFetch() {

        Visualization viz = new Visualization();
        viz.setId("id");
        AtomicReference<Instant> lastFetch = new AtomicReference<>(Instant.now());

        testObject.filesAPI = mock(FilesAPI.class);
        testObject.dataGeneratorFactory = mock(DataGeneratorFactory.class);

        DataGenerator generator = mock(DataGenerator.class);
        doNothing().when(generator).generate();
        when(testObject.dataGeneratorFactory.createGenerator(any())).thenReturn(generator);
        when(testObject.filesAPI.fetchVisualizations(any())).thenAnswer(invocationOnMock -> {
            lastFetch.set(invocationOnMock.getArgument(0));
            return new Visualization[]{viz};
        });

        testObject.fetchVisualizationData();
        testObject.fetchVisualizationData();

        verify(generator, timeout(100).times(2)).generate();
        ArgumentCaptor<Instant> argrument = ArgumentCaptor.forClass(Instant.class);
        verify(testObject.filesAPI, times(2)).fetchVisualizations(argrument.capture());
        assertTrue(argrument.getAllValues().get(0).isBefore(argrument.getAllValues().get(1)));
    }

    @Test
    public void scheduleFetching() {

        testObject.scheduler = mock(ScheduledExecutorService.class);

        testObject.scheduleFetching();

        verify(testObject.scheduler, times(1)).scheduleAtFixedRate(any(), eq(0L), anyLong(), any());
    }

    @Test
    public void fetchVisualzations() {

        testObject.scheduler = mock(ScheduledExecutorService.class);

        testObject.fetchVisualizations();

        verify(testObject.scheduler, times(1)).schedule(any(Runnable.class), eq(0L), any());
    }
}
