package org.matsim.webvis.files.file;

import org.apache.commons.io.IOUtils;
import org.matsim.webvis.common.communication.Answer;
import org.matsim.webvis.common.communication.ErrorCode;
import org.matsim.webvis.files.communication.ContentType;
import org.matsim.webvis.files.communication.JsonHelper;
import org.matsim.webvis.files.communication.Subject;
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

        //check json content type
        if (!ContentType.isJson(request.contentType())) {
            return JsonHelper.createJsonResponse(
                    Answer.badRequest(ErrorCode.INVALID_REQUEST, "only content-type: 'application/json' allowed"),
                    response);
        }
        FileDownloadRequest body;
        try {
            body = JsonHelper.parseJson(request.body(), FileDownloadRequest.class);
        } catch (RuntimeException e) {
            return JsonHelper.createJsonResponse(Answer.badRequest(ErrorCode.INVALID_REQUEST,
                    "error while parsing message body"), response);
        }
        // get the subject
        Subject subject = Subject.getSubject(request);

        try {
            response.type(ContentType.APPLICATION_OCTET_STREAM);

            try (InputStream inStream = projectService.getFileStream(body.projectId, body.fileId, subject.getUser());
                 OutputStream outStream = response.raw().getOutputStream()
            ) {
                IOUtils.copy(inStream, outStream);
                outStream.flush();

            } catch (IOException e) {
                return JsonHelper.createJsonResponse(
                        Answer.internalError(ErrorCode.UNSPECIFIED_ERROR, "something went wrong"),
                        response);
            }
        } catch (Exception e) {
            JsonHelper.createJsonResponse(
                    Answer.badRequest(ErrorCode.INVALID_REQUEST, e.getMessage()), response);
        }
        return "OK";
    }
}
