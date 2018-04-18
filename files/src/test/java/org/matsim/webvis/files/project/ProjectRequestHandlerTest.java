package org.matsim.webvis.files.project;

import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.matsim.webvis.common.communication.Answer;
import org.matsim.webvis.common.database.AbstractEntity;
import org.matsim.webvis.files.communication.Subject;
import org.matsim.webvis.files.entities.Project;
import org.matsim.webvis.files.entities.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
    public void projectIdsSupplied_AnswerOk() {

        List<Project> projects = new ArrayList<>();
        projects.add(createProject("first"));
        projects.add(createProject("second"));
        when(testObject.projectService.findProjectsForUser(any(), any())).thenReturn(projects);
        ProjectRequest body = new ProjectRequest();
        body.projectIds = projects.stream().map(AbstractEntity::getId).collect(Collectors.toList());
        Subject subject = new Subject(null, new User());

        Answer answer = testObject.process(body, subject);

        assertEquals(answer.getResponse(), projects);
        assertEquals(HttpStatus.SC_OK, answer.getStatusCode());
    }

    @Test
    public void noProjectIds_AnswerOk_allProjects() {

        List<Project> projects = new ArrayList<>();
        projects.add(createProject("first"));
        projects.add(createProject("second"));
        when(testObject.projectService.findAllProjectsForUser(any())).thenReturn(projects);
        ProjectRequest body = new ProjectRequest();
        Subject subject = new Subject(null, new User());

        Answer answer = testObject.process(body, subject);

        assertEquals(answer.getResponse(), projects);
        assertEquals(HttpStatus.SC_OK, answer.getStatusCode());
    }

    private Project createProject(String id) {
        Project result = new Project();
        result.setId(id);
        return result;
    }
}
