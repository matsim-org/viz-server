package org.matsim.webvis.files.file;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.webvis.common.communication.AbstractRequestHandler;
import org.matsim.webvis.common.communication.Answer;
import org.matsim.webvis.common.communication.ErrorCode;
import org.matsim.webvis.common.communication.RequestException;
import org.matsim.webvis.files.config.Configuration;
import org.matsim.webvis.files.entities.Project;
import org.matsim.webvis.files.project.ProjectService;
import spark.Request;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class UploadFileRequestHandler extends AbstractRequestHandler<FileUploadRequest> {

    private static Logger logger = LogManager.getLogger();

    private ProjectService projectService = new ProjectService();

    public UploadFileRequestHandler() {
        super(FileUploadRequest.class);
    }

    @Override
    protected FileUploadRequest parseBody(Request request) throws RequestException {

        if (ServletFileUpload.isMultipartContent(request.raw())) {
            DiskFileItemFactory factory = new DiskFileItemFactory();
            Path repository = Paths.get(Configuration.getInstance().getFilePath());
            factory.setRepository(repository.toFile());

            ServletFileUpload upload = new ServletFileUpload(factory);
            try {
                List<FileItem> items = upload.parseRequest(request.raw());
                return new FileUploadRequest(items);
            } catch (FileUploadException e) {
                throw new RequestException(ErrorCode.INVALID_REQUEST, "some error");
            }
        } else {
            throw new RequestException(ErrorCode.INVALID_REQUEST, "request must be multipart");
        }
    }

    @Override
    protected Answer process(FileUploadRequest body) {

        Project project;
        try {
            project = projectService.addFilesToProject(body.getFiles(), body.getProject_id(), body.getUser_id());
        } catch (Exception e) {
            logger.error("Error while saving files.", e);
            return Answer.internalError(ErrorCode.UNSPECIFIED_ERROR, "something went wrong");
        }
        return Answer.ok(project);
    }
}
