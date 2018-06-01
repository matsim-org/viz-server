package org.matsim.webvis.files.file;

import org.matsim.webvis.common.auth.AuthenticationResult;
import org.matsim.webvis.common.communication.Answer;
import org.matsim.webvis.common.communication.JsonResponseHandler;
import org.matsim.webvis.common.service.InvalidInputException;
import org.matsim.webvis.files.communication.GsonFactory;
import org.matsim.webvis.files.entities.Project;
import org.matsim.webvis.files.permission.Subject;
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

        AuthenticationResult authResult = AuthenticationResult.fromRequestAttribute(request);
        Subject subject = Subject.createSubject(authResult);

        // Parsing and uploading of the request
        FileUploadRequest uploadRequest = requestFactory.createRequest(request);
        uploadRequest.parseUpload(request);
        Project project = projectService.find(uploadRequest.getProjectId(), subject.getAgent());

        // Processing of the uploaded content
        Project persisted = projectService.addFilesToProject(uploadRequest.getFiles(), project, subject.getAgent());
        return Answer.ok(persisted);
    }

    class RequestFactory {
        FileUploadRequest createRequest(Request request) throws InvalidInputException {
            return new FileUploadRequest(request);
        }
    }
}
