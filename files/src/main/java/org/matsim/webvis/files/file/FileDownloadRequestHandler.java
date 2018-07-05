package org.matsim.webvis.files.file;

public class FileDownloadRequestHandler {
/*
    ProjectService projectService = new ProjectService();

    @Override
    public Object handle(Request request, Response response) {

        if (!ContentType.isJson(request.contentType()))
            throw new InvalidInputException("only content-type: 'application/json' allowed");

        FileRequest body = JsonHelper.parseJson(request.body(), FileRequest.class);
        AuthenticationResult authResult = AuthenticationResult.fromRequestAttribute(request);
        Subject subject = null; //Subject.createSubject(authResult);
        Project project = projectService.find(body.getProjectId(), subject.getAgent());

        FileEntry fileEntry = project.getFiles().stream().filter(file -> file.getId().equals(body.getFileId())).findFirst()
                .orElseThrow(() -> new CodedException(Error.RESOURCE_NOT_FOUND, "file not found."));
        response.type(fileEntry.getContentType());

        try (InputStream inStream = projectService.getFileStream(project, fileEntry, subject.getAgent());
             OutputStream outStream = response.raw().getOutputStream()
        ) {
            IOUtils.copy(inStream, outStream);
            outStream.flush();
        } catch (IOException e) {
            throw new CodedException(Error.UNSPECIFIED_ERROR, "sorry, something went wrong.");
        }

        return HttpStatus.OK;
    }
    */
}
