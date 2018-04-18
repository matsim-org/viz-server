package org.matsim.webvis.files.project;

import org.matsim.webvis.common.communication.Answer;
import org.matsim.webvis.files.communication.AuthenticatedJsonRequestHandler;
import org.matsim.webvis.files.communication.Subject;
import org.matsim.webvis.files.entities.Project;

import java.util.List;

public class ProjectRequestHandler extends AuthenticatedJsonRequestHandler<ProjectRequest> {

    private ProjectService projectService = new ProjectService();

    public ProjectRequestHandler() {
        super(ProjectRequest.class);
    }

    @Override
    protected Answer process(ProjectRequest body, Subject subject) {

        List<Project> result;
        if (body.projectIds != null && body.projectIds.size() > 0) {
            result = projectService.findProjectsForUser(body.projectIds, subject.getUser());
        } else {
            result = projectService.findAllProjectsForUser(subject.getUser());
        }
        return Answer.ok(result);

    }
}
