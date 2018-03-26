package org.matsim.webvis.files.project;

import org.matsim.webvis.common.communication.AbstractRequestHandler;
import org.matsim.webvis.common.communication.Answer;
import org.matsim.webvis.common.communication.ErrorCode;
import org.matsim.webvis.files.entities.Project;

public class CreateProjectRequestHandler extends AbstractRequestHandler<CreateProjectRequest> {

    ProjectService projectService = new ProjectService();

    public CreateProjectRequestHandler() {
        super(CreateProjectRequest.class);
    }

    @Override
    protected Answer process(CreateProjectRequest body) {

        if (body.getName().isEmpty() || body.getUserId().isEmpty()) {
            return Answer.badRequest(ErrorCode.INVALID_REQUEST, "project name or org.matsim.webvis.auth.user id was not set.");
        }

        Project result;
        try {
            result = projectService.createNewProject(body.getName(), body.getUserId());
        } catch (Exception e) {
            return Answer.conflict(ErrorCode.RESOURCE_EXISTS, "project exists or org.matsim.webvis.auth.user does not exist");
        }
        return Answer.ok(result);
    }
}
