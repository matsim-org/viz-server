package org.matsim.webvis.files.visualization;

import org.junit.Test;
import org.matsim.webvis.files.entities.User;
import org.matsim.webvis.files.entities.Visualization;
import org.matsim.webvis.files.entities.VisualizationType;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    public void types() {

        List<VisualizationType> types = new ArrayList<>();
        types.add(new VisualizationType());
        VisualizationService visualizationServiceMock = mock(VisualizationService.class);
        when(visualizationServiceMock.findAllTypes()).thenReturn(types);
        VisualizationResource testObject = new VisualizationResource(visualizationServiceMock);

        List<VisualizationType> result = testObject.types();

        assertEquals(types, result);
    }
}
