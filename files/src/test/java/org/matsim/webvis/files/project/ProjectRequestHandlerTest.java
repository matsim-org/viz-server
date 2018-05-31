package org.matsim.webvis.files.project;

import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.matsim.webvis.common.communication.Answer;
import org.matsim.webvis.common.service.ForbiddenException;
import org.matsim.webvis.files.communication.Subject;
import org.matsim.webvis.files.entities.Project;
import org.matsim.webvis.files.entities.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProjectRequestHandlerTest {

    private ProjectRequestHandler testObject;

    @Before
    public void setUp() {
        testObject = new ProjectRequestHandler();
        testObject.projectService = mock(ProjectService.class);
    }

    @Test
    public void projectIdSupplied_AnswerOk() throws ForbiddenException {

        Project project = createProject("first");
        when(testObject.projectService.find(anyString(), any())).thenReturn(project);
        ProjectRequest body = new ProjectRequest(project.getId());
        Subject subject = new Subject(new User(), null);

        Answer answer = testObject.process(body, subject);

        assertTrue(answer.getResponse() instanceof Collection);
        assertEquals(1, ((Collection) answer.getResponse()).size());
        assertEquals(HttpStatus.SC_OK, answer.getStatusCode());
    }

    @Test
    public void noProjectId_AnswerOk_allProjects() {

        List<Project> projects = new ArrayList<>();
        projects.add(createProject("first"));
        projects.add(createProject("second"));
        when(testObject.projectService.findAllForUserFlat(any())).thenReturn(projects);
        ProjectRequest body = new ProjectRequest();
        Subject subject = new Subject(new User(), null);

        Answer answer = testObject.process(body, subject);

        assertEquals(projects, answer.getResponse());
        assertEquals(HttpStatus.SC_OK, answer.getStatusCode());
    }

    private Project createProject(String id) {
        Project result = new Project();
        result.setId(id);
        return result;
    }
}
