package org.matsim.webvis.files.visualization;

import org.junit.Before;
import org.junit.Test;
import org.matsim.webvis.files.entities.User;
import org.matsim.webvis.files.entities.Visualization;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProjectVisualizationResourceTest {

    private ProjectVisualizationResource testObject;

    @Before
    public void setUp() {
        testObject = new ProjectVisualizationResource();
        testObject.visualizationService = mock(VisualizationService.class);
    }

    @Test
    public void createVisualization() {

        Visualization visualization = new Visualization();
        when(testObject.visualizationService.createVisualizationFromRequest(any(), any())).thenReturn(visualization);

        Visualization result = testObject.createVisualization(new User(), new CreateVisualizationRequest());

        assertEquals(visualization, result);
    }

    @Test
    public void getVisualization() {

        Visualization visualization = new Visualization();
        when(testObject.visualizationService.find(anyString(), any())).thenReturn(visualization);

        Visualization result = testObject.getVisualization(new User(), "some-id");

        assertEquals(visualization, result);
    }
}
