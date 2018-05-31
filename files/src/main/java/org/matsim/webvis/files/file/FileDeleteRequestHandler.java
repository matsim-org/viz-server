package org.matsim.webvis.files.file;

import org.apache.commons.lang3.StringUtils;
import org.matsim.webvis.common.communication.Answer;
import org.matsim.webvis.common.communication.RequestError;
import org.matsim.webvis.files.communication.AuthenticatedJsonRequestHandler;
import org.matsim.webvis.files.communication.GsonFactory;
import org.matsim.webvis.files.communication.Subject;
import org.matsim.webvis.files.entities.Project;
import org.matsim.webvis.files.entities.User;
import org.matsim.webvis.files.project.ProjectService;

public class FileDeleteRequestHandler extends AuthenticatedJsonRequestHandler<FileRequest> {

    ProjectService projectService = new ProjectService();

    public FileDeleteRequestHandler() {
        super(FileRequest.class, GsonFactory.createParserWithExclusionStrategy());
    }

    @Override
    protected Answer process(FileRequest body, Subject subject) {

        if (!isValid(body)) {
            return Answer.badRequest(RequestError.INVALID_REQUEST, "fileId and projectId must be provided");
        }
        Project project = projectService.removeFileFromProject(body.getProjectId(), body.getFileId(), (User)subject.getUser());
        return Answer.ok(project);
    }

    private boolean isValid(FileRequest body) {
        return StringUtils.isNotBlank(body.getFileId()) && StringUtils.isNotBlank(body.getProjectId());
    }
}
