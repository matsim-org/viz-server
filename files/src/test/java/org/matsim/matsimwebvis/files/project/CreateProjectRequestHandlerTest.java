package org.matsim.matsimwebvis.files.project;

import communication.Answer;
import communication.ErrorCode;
import communication.ErrorResponse;
import communication.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.matsim.matsimwebvis.files.entities.Project;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CreateProjectRequestHandlerTest {

    private CreateProjectRequestHandler testObject;

    @Before
    public void setUp() {
        testObject = new CreateProjectRequestHandler();
    }

    @Test
    public void process_noProjectName_answerBadRequest() {

        CreateProjectRequest request = new CreateProjectRequest("", "id");

        Answer answer = testObject.process(request);

        assertEquals(HttpStatus.BAD_REQUEST, answer.getStatusCode());
        assertTrue(answer.getResponse() instanceof ErrorResponse);
        assertEquals(ErrorCode.INVALID_REQUEST, ((ErrorResponse) answer.getResponse()).getError());
    }

    @Test
    public void process_noUserId_answerBadRequest() {

        CreateProjectRequest request = new CreateProjectRequest("name", "");

        Answer answer = testObject.process(request);

        assertEquals(HttpStatus.BAD_REQUEST, answer.getStatusCode());
        assertTrue(answer.getResponse() instanceof ErrorResponse);
        assertEquals(ErrorCode.INVALID_REQUEST, ((ErrorResponse) answer.getResponse()).getError());
    }

    @Test
    public void process_createNewProjectThrowsException_answerResourceExists() throws Exception {

        CreateProjectRequest request = new CreateProjectRequest("name", "id");
        testObject.projectService = mock(ProjectService.class);
        when(testObject.projectService.createNewProject(any(), any())).thenThrow(new Exception());

        Answer answer = testObject.process(request);

        assertEquals(HttpStatus.CONFLICT, answer.getStatusCode());
        assertTrue(answer.getResponse() instanceof ErrorResponse);
        assertEquals(ErrorCode.RESOURCE_EXISTS, ((ErrorResponse) answer.getResponse()).getError());
    }

    @Test
    public void process_success_project() throws Exception {

        CreateProjectRequest request = new CreateProjectRequest("name", "id");
        testObject.projectService = mock(ProjectService.class);
        Project project = new Project();
        project.setId("id");
        when(testObject.projectService.createNewProject(any(), any())).thenReturn(project);

        Answer answer = testObject.process(request);

        assertEquals(HttpStatus.OK, answer.getStatusCode());
        assertTrue(answer.getResponse() instanceof Project);
        assertEquals(project.getId(), ((Project) answer.getResponse()).getId());
    }
}
