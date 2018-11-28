package org.matsim.viz.files.visualization;

import org.junit.Test;
import org.matsim.viz.files.entities.User;
import org.matsim.viz.files.entities.Visualization;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class VisualizationResourceTest {

    @Test
    public void findByType() {

        List<Visualization> vizes = new ArrayList<>();
        vizes.add(new Visualization());

        VisualizationService visualizationServiceMock = mock(VisualizationService.class);
        when(visualizationServiceMock.findByType(anyString(), any(), any())).thenReturn(vizes);
        VisualizationResource testObject = new VisualizationResource(visualizationServiceMock);

        List<Visualization> result = testObject.findByType(new User(), "type", null);

        assertEquals(vizes, result);
    }

    @Test
    public void findByType_afterIsSupplied() {

        final Instant begin = Instant.now();
        List<Visualization> vizes = new ArrayList<>();
        vizes.add(new Visualization());

        VisualizationService visualizationService = mock(VisualizationService.class);
        when(visualizationService.findByType(any(), any(), any())).thenReturn(vizes);
        VisualizationResource testObject = new VisualizationResource(visualizationService);

        List<Visualization> result = testObject.findByType(new User(), "type", begin.toString());

        assertEquals(vizes, result);
        verify(visualizationService).findByType(any(), eq(begin), any());
    }
}
