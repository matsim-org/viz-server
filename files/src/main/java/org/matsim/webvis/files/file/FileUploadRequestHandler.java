package org.matsim.webvis.files.file;

public class FileUploadRequestHandler {
/*
    ProjectService projectService = new ProjectService();
    RequestFactory requestFactory = new RequestFactory();

    public FileUploadRequestHandler() {
        super(GsonFactory.createParserWithExclusionStrategy());
    }

    protected Answer process(Request request, Response response) {

        AuthenticationResult authResult = AuthenticationResult.fromRequestAttribute(request);
        Subject subject = null;//Subject.createSubject(authResult);

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
    */
}
