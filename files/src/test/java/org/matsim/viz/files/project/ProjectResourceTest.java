package org.matsim.viz.files.project;

import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.matsim.viz.error.InvalidInputException;
import org.matsim.viz.error.UnauthorizedException;
import org.matsim.viz.files.agent.AgentService;
import org.matsim.viz.files.entities.Permission;
import org.matsim.viz.files.entities.Project;
import org.matsim.viz.files.entities.PublicAgent;
import org.matsim.viz.files.entities.User;
import org.matsim.viz.files.file.FileResource;
import org.matsim.viz.files.visualization.ProjectVisualizationResource;
import org.matsim.viz.files.visualization.VisualizationService;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProjectResourceTest {

    private ProjectResource testObject;

    @Before
    public void setUp() {
        testObject = new ProjectResource(mock(ProjectService.class), mock(VisualizationService.class), mock(AgentService.class));
    }

    @Test(expected = UnauthorizedException.class)
    public void createProject_invalidAgent_exception() {

        ProjectResource.ProjectProperties request = new ProjectResource.ProjectProperties("name");

        testObject.createProject(new PublicAgent(), request);

        fail("invalid client type should cause exception");
    }

    @Test
    public void createProject_serviceInvoked() {

        Project project = new Project();
        ProjectResource.ProjectProperties request = new ProjectResource.ProjectProperties("name");

        ProjectService projectServiceMock = mock(ProjectService.class);
        when(projectServiceMock.createNewProject(anyString(), any())).thenReturn(project);
        testObject = new ProjectResource(projectServiceMock, mock(VisualizationService.class), mock(AgentService.class));

        Project result = testObject.createProject(new User(), request);

        assertEquals(project, result);
    }

    @Test
    public void patchProject_serviceInvoked_status204() {
        testObject = new ProjectResource(mock(ProjectService.class), mock(VisualizationService.class), mock(AgentService.class));
        Response response = testObject.patchProject(new User(), "project-id", new ProjectResource.ProjectProperties("newName"));

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void removeProject_serviceInvoked_status204() {

        testObject = new ProjectResource(mock(ProjectService.class), mock(VisualizationService.class), mock(AgentService.class));
        Response response = testObject.removeProject(new User(), "some-id");

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void findProjects() {

        List<Project> projectList = new ArrayList<>();
        projectList.add(new Project());

        ProjectService projectServiceMock = mock(ProjectService.class);
        when(projectServiceMock.findAllForUserFlat(any())).thenReturn(projectList);
        testObject = new ProjectResource(projectServiceMock, mock(VisualizationService.class), mock(AgentService.class));

        List<Project> result = testObject.findProjects(new User());

        assertEquals(projectList.size(), result.size());
    }

    @Test
    public void findProject() {

        Project project = new Project();

        ProjectService projectServiceMock = mock(ProjectService.class);
        when(projectServiceMock.find(anyString(), any())).thenReturn(project);
        testObject = new ProjectResource(projectServiceMock, mock(VisualizationService.class), mock(AgentService.class));

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

    @Test(expected = InvalidInputException.class)
    public void addPermission_invalidAgentId_exception() {

        AgentService agentService = mock(AgentService.class);
        when(agentService.findAgentByIdentityProviderId(anyString())).thenReturn(null);
        ProjectResource.AddPermissionRequest request = new ProjectResource.AddPermissionRequest(
                "some-resource-id", "some-auth-id", Permission.Type.Read
        );

        testObject = new ProjectResource(mock(ProjectService.class), mock(VisualizationService.class), agentService);

        testObject.addPermission(new User(), request);

        fail("invalid agent id should cause exception");
    }

    @Test
    public void addPermission_permissionAdded() {

        final String agentId = "some-auth-id";
        final String resourceId = "some-resource-id";
        final User permissionUser = new User();
        permissionUser.setAuthId(agentId);
        AgentService agentService = mock(AgentService.class);
        when(agentService.findAgentByIdentityProviderId(agentId)).thenReturn(permissionUser);
        ProjectService projectService = mock(ProjectService.class);
        val permission = new Permission();
        permission.setAgent(permissionUser);
        when(projectService.addPermission(eq(resourceId), eq(permissionUser), any(), any())).thenReturn(permission);

        ProjectResource.AddPermissionRequest request = new ProjectResource.AddPermissionRequest(
                resourceId, agentId, Permission.Type.Read
        );

        testObject = new ProjectResource(projectService, mock(VisualizationService.class), agentService);

        Permission result = testObject.addPermission(new User(), request);

        assertNotNull(result);
        assertEquals(agentId, result.getAgent().getAuthId());
    }

    @Test(expected = InvalidInputException.class)
    public void removePermission_invalidAgentId_exception() {

        AgentService agentService = mock(AgentService.class);
        when(agentService.findAgentByIdentityProviderId(anyString())).thenReturn(null);

        testObject = new ProjectResource(mock(ProjectService.class), mock(VisualizationService.class), agentService);

        testObject.removePermission(new User(), "invalid-id", "some-resource-id");

        fail("invalid agent id should cause exception");
    }

    @Test
    public void removePermission_permissionRemoved() {

        final String agentId = "some-auth-id";
        final String resourceId = "some-resource-id";
        final User permissionUser = new User();
        permissionUser.setAuthId(agentId);
        AgentService agentService = mock(AgentService.class);
        when(agentService.findAgentByIdentityProviderId(agentId)).thenReturn(permissionUser);
        ProjectService projectService = mock(ProjectService.class);
        when(projectService.removePermission(eq(resourceId), eq(permissionUser), any())).thenReturn(new Project());

        testObject = new ProjectResource(projectService, mock(VisualizationService.class), agentService);

        Project result = testObject.removePermission(new User(), agentId, resourceId);

        assertNotNull(result);
    }
}
