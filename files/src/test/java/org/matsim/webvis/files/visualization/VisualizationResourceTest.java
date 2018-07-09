package org.matsim.webvis.files.visualization;

import org.junit.Before;
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

    private VisualizationResource testObject;

    @Before
    public void setUp() {
        testObject = new VisualizationResource();
        testObject.visualizationService = mock(VisualizationService.class);
    }

    @Test
    public void findByType() {

        List<Visualization> vizes = new ArrayList<>();
        vizes.add(new Visualization());
        when(testObject.visualizationService.findByType(anyString(), any())).thenReturn(vizes);

        List<Visualization> result = testObject.findByType(new User(), "type");

        assertEquals(vizes, result);
    }

    @Test
    public void types() {

        List<VisualizationType> types = new ArrayList<>();
        types.add(new VisualizationType());
        when(testObject.visualizationService.findAllTypes()).thenReturn(types);

        List<VisualizationType> result = testObject.types();

        assertEquals(types, result);
    }
}
