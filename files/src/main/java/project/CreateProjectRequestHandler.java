package project;

import communication.AbstractRequestHandler;
import communication.Answer;
import communication.ErrorCode;
import entities.Project;

public class CreateProjectRequestHandler extends AbstractRequestHandler<CreateProjectRequest> {

    ProjectService projectService = new ProjectService();

    public CreateProjectRequestHandler() {
        super(CreateProjectRequest.class);
    }

    @Override
    protected Answer process(CreateProjectRequest body) {

        if (body.getName().isEmpty() || body.getUserId().isEmpty()) {
            return Answer.badRequest(ErrorCode.INVALID_REQUEST, "project name or user id was not set.");
        }

        Project result;
        try {
            result = projectService.createNewProject(body.getName(), body.getUserId());
        } catch (Exception e) {
            return Answer.conflict(ErrorCode.RESOURCE_EXISTS, "the resource exists");
        }
        return Answer.ok(result);
    }
}
