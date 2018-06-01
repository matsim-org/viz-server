package org.matsim.webvis.files.project;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.matsim.webvis.common.communication.Answer;
import org.matsim.webvis.common.communication.ErrorResponse;
import org.matsim.webvis.common.communication.HttpStatus;
import org.matsim.webvis.common.service.CodedException;
import org.matsim.webvis.common.service.Error;
import org.matsim.webvis.common.service.InvalidInputException;
import org.matsim.webvis.files.entities.Project;
import org.matsim.webvis.files.entities.User;
import org.matsim.webvis.files.permission.Subject;
import org.matsim.webvis.files.util.TestUtils;

import static junit.framework.TestCase.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CreateProjectRequestHandlerTest {

    private CreateProjectRequestHandler testObject;

    @Before
    public void setUp() {
        testObject = new CreateProjectRequestHandler();
        TestUtils.persistUser("some-auth-id");
    }

    @After
    public void tearDown() {
        TestUtils.removeAllEntities();
    }

    @Test(expected = InvalidInputException.class)
    public void process_noProjectName_invalidInputException() {

        CreateProjectRequest request = new CreateProjectRequest("");

        testObject.process(request, null);

        fail("invalid input should cause exception");
    }

    @Test
    public void process_createNewProjectThrowsException_answerConflict() throws CodedException {

        CreateProjectRequest request = new CreateProjectRequest("name");
        testObject.projectService = mock(ProjectService.class);
        when(testObject.projectService.createNewProject(any(), any())).thenThrow(new CodedException("bla", "bla"));

        User subject = new User();
        subject.setId("id");

        Answer answer = testObject.process(request, new Subject(subject, null));

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

        Answer answer = testObject.process(request, new Subject(subject, null));

        assertEquals(HttpStatus.OK, answer.getStatusCode());
        assertTrue(answer.getResponse() instanceof Project);
        assertEquals(project.getId(), ((Project) answer.getResponse()).getId());
    }
}
