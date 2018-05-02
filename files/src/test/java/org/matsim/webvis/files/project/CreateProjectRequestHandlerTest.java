package org.matsim.webvis.files.project;

import org.junit.Before;
import org.junit.Test;
import org.matsim.webvis.common.communication.Answer;
import org.matsim.webvis.common.communication.ErrorResponse;
import org.matsim.webvis.common.communication.HttpStatus;
import org.matsim.webvis.common.communication.RequestError;
import org.matsim.webvis.common.service.CodedException;
import org.matsim.webvis.common.service.Error;
import org.matsim.webvis.files.communication.Subject;
import org.matsim.webvis.files.entities.Project;
import org.matsim.webvis.files.entities.User;
import org.matsim.webvis.files.user.UserService;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CreateProjectRequestHandlerTest {

    private CreateProjectRequestHandler testObject;

    @Before
    public void setUp() {
        Subject.userService = mock(UserService.class);
        when(Subject.userService.findByIdentityProviderId(any())).thenReturn(new User());
        testObject = new CreateProjectRequestHandler();
    }

    @Test
    public void process_noProjectName_answerBadRequest() {

        CreateProjectRequest request = new CreateProjectRequest("");

        Answer answer = testObject.process(request, null);

        assertEquals(HttpStatus.BAD_REQUEST, answer.getStatusCode());
        assertTrue(answer.getResponse() instanceof ErrorResponse);
        assertEquals(RequestError.INVALID_REQUEST, ((ErrorResponse) answer.getResponse()).getError());
    }

    @Test
    public void process_createNewProjectThrowsException_answerResourceExists() throws CodedException {

        CreateProjectRequest request = new CreateProjectRequest("name");
        testObject.projectService = mock(ProjectService.class);
        when(testObject.projectService.createNewProject(any(), any())).thenThrow(new CodedException("bla", "bla"));

        User subject = new User();
        subject.setId("id");

        Answer answer = testObject.process(request, new Subject(null, subject));

        assertEquals(HttpStatus.CONFLICT, answer.getStatusCode());
        assertTrue(answer.getResponse() instanceof ErrorResponse);
        assertEquals(Error.RESOURCE_EXISTS, ((ErrorResponse) answer.getResponse()).getError());
    }

    @Test
    public void process_success_project() throws CodedException {

        CreateProjectRequest request = new CreateProjectRequest("name");
        testObject.projectService = mock(ProjectService.class);
        Project project = new Project();
        project.setId("id");
        when(testObject.projectService.createNewProject(any(), any())).thenReturn(project);
        User subject = new User();
        subject.setId("id");

        Answer answer = testObject.process(request, new Subject(null, subject));

        assertEquals(HttpStatus.OK, answer.getStatusCode());
        assertTrue(answer.getResponse() instanceof Project);
        assertEquals(project.getId(), ((Project) answer.getResponse()).getId());
    }
}
