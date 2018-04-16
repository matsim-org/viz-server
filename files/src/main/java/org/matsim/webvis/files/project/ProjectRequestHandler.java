package org.matsim.webvis.files.project;

import org.matsim.webvis.common.communication.Answer;
import org.matsim.webvis.files.communication.AuthenticatedJsonRequestHandler;
import org.matsim.webvis.files.communication.Subject;
import org.matsim.webvis.files.entities.Project;

import java.util.List;

public class ProjectRequestHandler extends AuthenticatedJsonRequestHandler<Object> {

    private ProjectService projectService = new ProjectService();

    public ProjectRequestHandler() {
        super(Object.class);
    }

    @Override
    protected Answer process(Object body, Subject subject) {

        List<Project> projects = projectService.getAllProjectsForUser(subject.getUser());
        return Answer.ok(projects);

    }
}
