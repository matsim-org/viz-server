package org.matsim.webvis.files.project;

import io.dropwizard.auth.Auth;
import org.hibernate.validator.constraints.NotEmpty;
import org.matsim.webvis.error.UnauthorizedException;
import org.matsim.webvis.files.entities.Agent;
import org.matsim.webvis.files.entities.Project;
import org.matsim.webvis.files.entities.User;
import org.matsim.webvis.files.file.FileResource;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/projects")
@Produces(MediaType.APPLICATION_JSON)
public class ProjectResource {

    private ProjectService projectService = ProjectService.Instance;

    @POST
    public Project createProject(
            @Auth Agent subject,
            @NotEmpty @QueryParam("name") String name) {

        if (!(subject instanceof User))
            throw new UnauthorizedException("Only real people can create Projects");

        return projectService.createNewProject(name, (User) subject);
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
}
