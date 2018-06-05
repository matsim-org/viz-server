package org.matsim.webvis.files.project;

import org.apache.commons.lang3.StringUtils;
import org.matsim.webvis.common.communication.Answer;
import org.matsim.webvis.common.errorHandling.CodedException;
import org.matsim.webvis.common.errorHandling.Error;
import org.matsim.webvis.common.errorHandling.InvalidInputException;
import org.matsim.webvis.files.communication.AuthenticatedJsonRequestHandler;
import org.matsim.webvis.files.communication.GsonFactory;
import org.matsim.webvis.files.entities.Project;
import org.matsim.webvis.files.entities.User;
import org.matsim.webvis.files.permission.Subject;

public class CreateProjectRequestHandler extends AuthenticatedJsonRequestHandler<CreateProjectRequest> {

    ProjectService projectService = new ProjectService();

    public CreateProjectRequestHandler() {
        super(CreateProjectRequest.class, GsonFactory.createParserWithExclusionStrategy());
    }

    @Override
    protected Answer process(CreateProjectRequest body, Subject subject) {

        if (!isValidRequest(body)) throw new InvalidInputException("parameter 'name' is missing");

        Project result;
        try {
            result = projectService.createNewProject(body.getName(), (User) subject.getAgent());
        } catch (CodedException e) {
            return Answer.conflict(Error.RESOURCE_EXISTS, "project exists or user does not exist");
        }
        return Answer.ok(result);
    }

    private boolean isValidRequest(CreateProjectRequest request) {
        return StringUtils.isNotBlank(request.getName());
    }
}
