package org.matsim.webvis.files.file;

import org.apache.commons.lang3.StringUtils;
import org.matsim.webvis.common.communication.Answer;
import org.matsim.webvis.common.communication.RequestError;
import org.matsim.webvis.common.service.CodedException;
import org.matsim.webvis.files.communication.AuthenticatedJsonRequestHandler;
import org.matsim.webvis.files.communication.Subject;
import org.matsim.webvis.files.entities.Project;
import org.matsim.webvis.files.project.ProjectService;

public class FileDeleteRequestHandler extends AuthenticatedJsonRequestHandler<FileRequest> {

    ProjectService projectService = new ProjectService();

    FileDeleteRequestHandler() {
        super(FileRequest.class);
    }

    @Override
    protected Answer process(FileRequest body, Subject subject) {

        if (!isValid(body)) {
            return Answer.badRequest(RequestError.INVALID_REQUEST, "fileId and projectId must be provided");
        }
        try {
            Project project = projectService.removeFileFromProject(body.getProjectId(), body.getFileId(), subject.getUser());
            return Answer.ok(project);
        } catch (CodedException e) {
            return Answer.internalError(e.getErrorCode(), e.getMessage());
        }
    }

    private boolean isValid(FileRequest body) {
        return StringUtils.isNotBlank(body.getFileId()) && StringUtils.isNotBlank(body.getProjectId());
    }
}
