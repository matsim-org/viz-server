package org.matsim.webvis.files.project;

import org.matsim.webvis.common.communication.Answer;
import org.matsim.webvis.common.communication.ErrorCode;
import org.matsim.webvis.files.communication.AuthenticatedJsonRequestHandler;
import org.matsim.webvis.files.communication.Subject;
import org.matsim.webvis.files.entities.Project;

public class CreateProjectRequestHandler extends AuthenticatedJsonRequestHandler<CreateProjectRequest> {

    ProjectService projectService = new ProjectService();

    public CreateProjectRequestHandler() {
        super(CreateProjectRequest.class);
    }

    @Override
    protected Answer process(CreateProjectRequest body, Subject subject) {

        if (body.getName().isEmpty()) {
            return Answer.badRequest(ErrorCode.INVALID_REQUEST, "project name was not set.");
        }

        Project result;
        try {
            result = projectService.createNewProject(body.getName(), subject.getUser().getId());
        } catch (Exception e) {
            return Answer.conflict(ErrorCode.RESOURCE_EXISTS, "project exists or user does not exist");
        }
        return Answer.ok(result);
    }
}
