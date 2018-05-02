package org.matsim.webvis.files.file;

import org.matsim.webvis.common.communication.Answer;
import org.matsim.webvis.common.communication.JsonResponseHandler;
import org.matsim.webvis.common.communication.RequestException;
import org.matsim.webvis.common.service.Error;
import org.matsim.webvis.files.communication.GsonFactory;
import org.matsim.webvis.files.communication.Subject;
import org.matsim.webvis.files.entities.Project;
import org.matsim.webvis.files.project.ProjectService;
import spark.Request;
import spark.Response;


public class FileUploadRequestHandler extends JsonResponseHandler {

    ProjectService projectService = new ProjectService();
    RequestFactory requestFactory = new RequestFactory();

    public FileUploadRequestHandler() {
        super(GsonFactory.createParserWithExclusionStrategy());
    }

    protected Answer process(Request request, Response response) {

        Subject subject = Subject.getSubject(request);

        // Parsing and uploading of the request
        FileUploadRequest uploadRequest;
        Project project;
        try {
            uploadRequest = requestFactory.createRequest(request);
            uploadRequest.parseUpload(request);
            project = projectService.find(uploadRequest.getProjectId(), subject.getUser());
        } catch (RequestException e) {
            return Answer.badRequest(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            return Answer.forbidden(e.getMessage());
        }

        // Processing of the uploaded content
        try {
            Project persisted = projectService.addFilesToProject(uploadRequest.getFiles(), project);
            return Answer.ok(persisted);
        } catch (Exception e) {
            return Answer.internalError(Error.UNSPECIFIED_ERROR, "Error during file upload. Try again.");
        }
    }

    class RequestFactory {
        FileUploadRequest createRequest(Request request) throws RequestException {
            return new FileUploadRequest(request);
        }
    }
}
