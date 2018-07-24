package org.matsim.webvis.files.project;

import org.junit.Before;
import org.junit.Test;
import org.matsim.webvis.error.UnauthorizedException;
import org.matsim.webvis.files.entities.Project;
import org.matsim.webvis.files.entities.PublicAgent;
import org.matsim.webvis.files.entities.User;
import org.matsim.webvis.files.file.FileResource;
import org.matsim.webvis.files.visualization.ProjectVisualizationResource;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProjectResourceTest {

    private ProjectResource testObject;

    @Before
    public void setUp() {
        testObject = new ProjectResource();
        testObject.projectService = mock(ProjectService.class);
    }

    @Test(expected = UnauthorizedException.class)
    public void createProject_invalidAgent_exception() {

        ProjectResource.CreateProject request = new ProjectResource.CreateProject("name");

        testObject.createProject(new PublicAgent(), request);

        fail("invalid client type should cause exception");
    }

    @Test
    public void createProject_serviceInvoked() {

        Project project = new Project();
        ProjectResource.CreateProject request = new ProjectResource.CreateProject("name");
        when(testObject.projectService.createNewProject(anyString(), any())).thenReturn(project);

        Project result = testObject.createProject(new User(), request);

        assertEquals(project, result);
    }

    @Test
    public void findProjects() {

        List<Project> projectList = new ArrayList<>();
        projectList.add(new Project());
        when(testObject.projectService.findAllForUserFlat(any())).thenReturn(projectList);

        List<Project> result = testObject.findProjects(new User());

        assertEquals(projectList.size(), result.size());
    }

    @Test
    public void findProject() {

        Project project = new Project();
        when(testObject.projectService.find(anyString(), any())).thenReturn(project);

        Project result = testObject.findProject(new User(), "some-id");

        assertEquals(project, result);
    }

    @Test
    public void files() {

        final String id = "id";
        FileResource resource = testObject.files(id);

        assertEquals(id, resource.getProjectId());
    }

    @Test
    public void visualizations() {

        ProjectVisualizationResource resource = testObject.visualizations("id");

        assertNotNull(resource);
    }
}