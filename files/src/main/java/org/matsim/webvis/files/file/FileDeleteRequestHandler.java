package org.matsim.webvis.files.file;

import org.matsim.webvis.common.communication.Answer;
import org.matsim.webvis.common.communication.ErrorCode;
import org.matsim.webvis.files.communication.AuthenticatedJsonRequestHandler;
import org.matsim.webvis.files.communication.Subject;
import org.matsim.webvis.files.entities.Project;
import org.matsim.webvis.files.project.ProjectService;

public class FileDeleteRequestHandler extends AuthenticatedJsonRequestHandler<FileRequest> {

    private ProjectService projectService = new ProjectService();

    public FileDeleteRequestHandler() {
        super(FileRequest.class);
    }

    @Override
    protected Answer process(FileRequest body, Subject subject) {

        try {
            Project project = projectService.removeFileFromProject(body.projectId, body.fileId, subject.getUser());
            return Answer.ok(project);
        } catch (Exception e) {
            return Answer.internalError(ErrorCode.UNSPECIFIED_ERROR, e.getMessage());
        }
    }
}
