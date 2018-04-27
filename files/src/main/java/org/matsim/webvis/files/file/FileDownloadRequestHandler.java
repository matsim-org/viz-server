package org.matsim.webvis.files.file;

import org.apache.commons.io.IOUtils;
import org.matsim.webvis.common.communication.Answer;
import org.matsim.webvis.common.communication.ContentType;
import org.matsim.webvis.common.communication.JsonHelper;
import org.matsim.webvis.common.communication.RequestError;
import org.matsim.webvis.common.service.Error;
import org.matsim.webvis.files.communication.Subject;
import org.matsim.webvis.files.entities.FileEntry;
import org.matsim.webvis.files.entities.Project;
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

        if (!ContentType.isJson(request.contentType())) {
            return JsonHelper.createJsonResponse(
                    Answer.badRequest(RequestError.INVALID_REQUEST, "only content-type: 'application/json' allowed"),
                    response);
        }
        FileRequest body;
        try {
            body = JsonHelper.parseJson(request.body(), FileRequest.class);
        } catch (RuntimeException e) {
            return JsonHelper.createJsonResponse(Answer.badRequest(RequestError.INVALID_REQUEST,
                                                                   "error while parsing message body"), response);
        }
        Subject subject = Subject.getSubject(request);

        try {

            Project project = projectService.findProjectIfAllowed(body.getProjectId(), subject.getUser().getId());
            @SuppressWarnings("ConstantConditions")
            FileEntry fileEntry = project.getFiles().stream().filter(file -> file.getId().equals(body.getFileId())).findFirst().get();
            response.type(fileEntry.getContentType());

            try (InputStream inStream = projectService.getFileStream(project, fileEntry);
                 OutputStream outStream = response.raw().getOutputStream()
            ) {
                IOUtils.copy(inStream, outStream);
                outStream.flush();
            } catch (IOException e) {
                return JsonHelper.createJsonResponse(
                        Answer.internalError(Error.UNSPECIFIED_ERROR, "something went wrong"),
                        response);
            }
        } catch (Exception e) {
            return JsonHelper.createJsonResponse(
                    Answer.badRequest(RequestError.INVALID_REQUEST, e.getMessage()), response);
        }
        return "OK";
    }
}
