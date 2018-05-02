package org.matsim.webvis.files.visualization;

import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.matsim.webvis.common.communication.Answer;
import org.matsim.webvis.common.service.CodedException;
import org.matsim.webvis.files.entities.User;
import org.matsim.webvis.files.entities.Visualization;
import org.matsim.webvis.files.util.TestUtils;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CreateVisualizationRequestHandlerTest {

    private CreateVisualizationRequestHandler testObject;

    @Before
    public void setUp() {
        testObject = new CreateVisualizationRequestHandler();
    }

    @Test
    public void process_answerOk() throws CodedException {

        Visualization viz = new Visualization();
        testObject.visualizationService = mock(VisualizationService.class);
        when(testObject.visualizationService.createVisualizationFromRequest(any(), any())).thenReturn(viz);
        CreateVisualizationRequest request = createCreateRequest("some-id", "some-type");
        request.getInputFiles().put("some-key", "some-file-id");

        Answer answer = testObject.process(request, TestUtils.createSubject(new User()));

        assertEquals(HttpStatus.SC_OK, answer.getStatusCode());
    }

    @Test
    public void process_serviceThrowsException_answerBadRequest() throws CodedException {

        testObject.visualizationService = mock(VisualizationService.class);
        when(testObject.visualizationService.createVisualizationFromRequest(any(), any())).thenThrow(new CodedException("bla", "bla"));
        CreateVisualizationRequest request = createCreateRequest("some-id", "some-type");
        request.getInputFiles().put("some-key", "some-file-id");

        Answer answer = testObject.process(request, TestUtils.createSubject(new User()));

        assertEquals(HttpStatus.SC_BAD_REQUEST, answer.getStatusCode());
    }

    @Test
    public void process_noProjectId_invalidRequest() {
        CreateVisualizationRequest request = createCreateRequest("", "some-type");
        request.getInputFiles().put("some-key", "some-file-id");

        Answer answer = testObject.process(request, TestUtils.createSubject(new User()));

        assertEquals(HttpStatus.SC_BAD_REQUEST, answer.getStatusCode());
    }

    @Test
    public void process_noType_invalidRequest() {

        CreateVisualizationRequest request = createCreateRequest("asdf", null);
        request.getInputFiles().put("some-key", "some-file-id");

        Answer answer = testObject.process(request, TestUtils.createSubject(new User()));

        assertEquals(HttpStatus.SC_BAD_REQUEST, answer.getStatusCode());
    }

    @Test
    public void process_noInputFiles_invalidRequest() {

        CreateVisualizationRequest request = createCreateRequest("asdf", "some-type");

        Answer answer = testObject.process(request, TestUtils.createSubject(new User()));

        assertEquals(HttpStatus.SC_BAD_REQUEST, answer.getStatusCode());
    }

    private CreateVisualizationRequest createCreateRequest(String projectId, String type) {

        return new CreateVisualizationRequest(
                projectId, type, new HashMap<>(), new HashMap<>()
        );
    }
}
