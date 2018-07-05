package org.matsim.webvis.files.file;

import org.matsim.webvis.files.communication.AuthenticatedJsonRequestHandler;
import org.matsim.webvis.files.project.ProjectService;

public class FileDeleteRequestHandler extends AuthenticatedJsonRequestHandler<FileRequest> {

    ProjectService projectService = new ProjectService();
/*
    public FileDeleteRequestHandler() {
        super(FileRequest.class, GsonFactory.createParserWithExclusionStrategy());
    }


    @Override
    protected Answer process(FileRequest body, Subject subject) {

        if (!isValid(body)) {
            return Answer.badRequest(Error.INVALID_REQUEST, "fileId and projectId must be provided");
        }
        Project project = projectService.removeFileFromProject(body.getProjectId(), body.getFileId(), (User) subject.getAgent());
        return Answer.ok(project);
    }

    private boolean isValid(FileRequest body) {
        return StringUtils.isNotBlank(body.getFileId()) && StringUtils.isNotBlank(body.getProjectId());
    }
    */
}
