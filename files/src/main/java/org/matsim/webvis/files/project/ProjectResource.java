package org.matsim.webvis.files.project;

import io.dropwizard.auth.Auth;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;
import org.matsim.webvis.error.UnauthorizedException;
import org.matsim.webvis.files.entities.Agent;
import org.matsim.webvis.files.entities.Project;
import org.matsim.webvis.files.entities.User;
import org.matsim.webvis.files.file.FileResource;
import org.matsim.webvis.files.visualization.ProjectVisualizationResource;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/projects")
@Produces(MediaType.APPLICATION_JSON)
public class ProjectResource {

    ProjectService projectService = ProjectService.Instance;

    @POST
    public Project createProject(
            @Auth Agent subject,
            @NotNull @Valid CreateProject request) {

        if (!(subject instanceof User))
            throw new UnauthorizedException("Only real people can create Projects");

        return projectService.createNewProject(request.getName(), (User) subject);
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
        return new FileResource(projectId);
    }

    @Path("{id}/visualizations")
    public ProjectVisualizationResource visualizations(@PathParam("id") String projectId) {
        return new ProjectVisualizationResource();
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    static class CreateProject {

        @NotEmpty
        private String name;
    }
}
