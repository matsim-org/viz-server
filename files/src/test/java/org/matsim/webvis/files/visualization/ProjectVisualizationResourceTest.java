package org.matsim.webvis.files.visualization;

import org.junit.Test;
import org.matsim.webvis.files.entities.User;
import org.matsim.webvis.files.entities.Visualization;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProjectVisualizationResourceTest {

    @Test
    public void createVisualization() {

        Visualization visualization = new Visualization();

        VisualizationService visualizationServiceMock = mock(VisualizationService.class);
        when(visualizationServiceMock.createVisualizationFromRequest(any(), any())).thenReturn(visualization);
        ProjectVisualizationResource testObject = new ProjectVisualizationResource(visualizationServiceMock);

        Visualization result = testObject.createVisualization(new User(), new CreateVisualizationRequest());

        assertEquals(visualization, result);
    }

    @Test
    public void getVisualization() {

        Visualization visualization = new Visualization();
        VisualizationService visualizationServiceMock = mock(VisualizationService.class);
        when(visualizationServiceMock.find(anyString(), any())).thenReturn(visualization);
        ProjectVisualizationResource testObject = new ProjectVisualizationResource(visualizationServiceMock);

        Visualization result = testObject.getVisualization(new User(), "some-id");

        assertEquals(visualization, result);
    }
}
