package org.matsim.webvis.files.file;

import org.apache.commons.io.IOUtils;
import org.matsim.webvis.common.auth.AuthenticationResult;
import org.matsim.webvis.common.communication.ContentType;
import org.matsim.webvis.common.communication.HttpStatus;
import org.matsim.webvis.common.communication.JsonHelper;
import org.matsim.webvis.common.errorHandling.CodedException;
import org.matsim.webvis.common.errorHandling.Error;
import org.matsim.webvis.common.errorHandling.InvalidInputException;
import org.matsim.webvis.files.entities.FileEntry;
import org.matsim.webvis.files.entities.Project;
import org.matsim.webvis.files.permission.Subject;
import org.matsim.webvis.files.project.ProjectService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileDownloadRequestHandler implements Route {

    ProjectService projectService = new ProjectService();

    @Override
    public Object handle(Request request, Response response) {

        if (!ContentType.isJson(request.contentType()))
            throw new InvalidInputException("only content-type: 'application/json' allowed");

        FileRequest body = JsonHelper.parseJson(request.body(), FileRequest.class);
        AuthenticationResult authResult = AuthenticationResult.fromRequestAttribute(request);
        Subject subject = Subject.createSubject(authResult);
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
}
