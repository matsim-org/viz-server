package org.matsim.webvis.files.project;

import org.matsim.webvis.common.communication.Answer;
import org.matsim.webvis.common.service.CodedException;
import org.matsim.webvis.files.communication.AuthenticatedJsonRequestHandler;
import org.matsim.webvis.files.communication.GsonFactory;
import org.matsim.webvis.files.communication.Subject;
import org.matsim.webvis.files.entities.Project;

import java.util.ArrayList;
import java.util.List;

public class ProjectRequestHandler extends AuthenticatedJsonRequestHandler<ProjectRequest> {

    ProjectService projectService = new ProjectService();

    public ProjectRequestHandler() {
        super(ProjectRequest.class, GsonFactory.createParserWithExclusionStrategy());
    }

    @Override
    protected Answer process(ProjectRequest body, Subject subject) throws CodedException {

        List<Project> result;

        if (body.getProjectId() != null) {
            result = new ArrayList<>();
            result.add(projectService.find(body.getProjectId(), subject.getUser()));
        } else {
            result = projectService.findAllForUserFlat(subject.getUser());
        }
        return Answer.ok(result);
    }
}
