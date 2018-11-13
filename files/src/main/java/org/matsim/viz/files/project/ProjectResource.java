package org.matsim.viz.files.project;

import io.dropwizard.auth.Auth;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;
import org.matsim.viz.error.InvalidInputException;
import org.matsim.viz.error.UnauthorizedException;
import org.matsim.viz.files.agent.AgentService;
import org.matsim.viz.files.entities.Agent;
import org.matsim.viz.files.entities.Permission;
import org.matsim.viz.files.entities.Project;
import org.matsim.viz.files.entities.User;
import org.matsim.viz.files.file.FileResource;
import org.matsim.viz.files.visualization.ProjectVisualizationResource;
import org.matsim.viz.files.visualization.VisualizationService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@AllArgsConstructor
@Path("/projects")
@Produces(MediaType.APPLICATION_JSON)
public class ProjectResource {

    private final ProjectService projectService;
    private final VisualizationService visualizationService;
    private final AgentService agentService;

    @POST
    public Project createProject(
            @Auth Agent subject,
            @NotNull @Valid CreateProject request) {

        if (!(subject instanceof User))
            throw new UnauthorizedException("Only real people can create Projects");

        return projectService.createNewProject(request.getName(), (User) subject);
    }

    @DELETE
    @Path("/{id}")
    public Response removeProject(@Auth Agent subject, @NotNull @PathParam("id") String id) {

        projectService.removeProject(id, subject);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @GET
    public List<Project> findProjects(@Auth Agent subject) {
        return projectService.findAllForUserFlat(subject);
    }

    @GET
    @Path("/{id}")
    public Project findProject(@Auth Agent subject, @PathParam("id") String id) {
        return projectService.find(id, subject);
    }

    @Path("{id}/files")
    public FileResource files(@PathParam("id") String projectId) {
        return new FileResource(projectService, projectId);
    }

    @Path("{id}/visualizations")
    public ProjectVisualizationResource visualizations(@PathParam("id") String projectId) {
        return new ProjectVisualizationResource(visualizationService);
    }

    @Path("{id}/permissions")
    @POST
    public Project addPermission(@Auth Agent subject, @Valid AddPermissionRequest request) {

        User user = agentService.findByIdentityProviderId(request.getUserAuthId());
        if (user == null)
            throw new InvalidInputException("could not find user");

        return projectService.addPermission(request.getResourceId(), user, request.getType(), subject);
    }

    @Path("{id}/permissions")
    @DELETE
    public Project removePermission(@Auth Agent subject, @QueryParam("userAuthId") String userId, @QueryParam("projectId") String forProject) {

        User forUser = agentService.findByIdentityProviderId(userId);
        if (forUser == null) {
            throw new InvalidInputException("could not find user");
        }
        return projectService.removePermission(forProject, forUser, subject);
    }

    @Path("{id}/tags")
    @POST
    public Project addTag(@Auth Agent subject, @PathParam("id") String projectId, @Valid AddTagRequest request) {
        return projectService.addTag(projectId, request.name, subject);
    }

    @Path("{id}/tags/{tagId}")
    @DELETE
    public Project removeTag(@Auth Agent subject, @PathParam("id") String projectId, @PathParam("tagId") @NotEmpty String tagId) {
        return projectService.removeTag(projectId, tagId, subject);
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    static class CreateProject {

        @NotEmpty
        private String name;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    static class AddPermissionRequest {

        @NotEmpty
        private String resourceId;
        @NotEmpty
        private String userAuthId;
        @NotNull
        private Permission.Type type;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    private static class AddTagRequest {

        @NotEmpty
        private String name;
    }
}
