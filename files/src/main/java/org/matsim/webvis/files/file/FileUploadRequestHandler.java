package org.matsim.webvis.files.file;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.matsim.webvis.common.communication.*;
import org.matsim.webvis.files.entities.Project;
import org.matsim.webvis.files.project.ProjectService;
import spark.Request;
import spark.Response;
import spark.Route;

public class FileUploadRequestHandler implements Route {

    ProjectService projectService = new ProjectService();

    private static Gson gson = new GsonBuilder().
            registerTypeHierarchyAdapter(Iterable.class, new IterableSerializer())
            .registerTypeAdapterFactory(new EntityAdapterFactory())
            .setExclusionStrategies(new FileEntryExclusionStrategy())
            .create();
    RequestFactory requestFactory = new RequestFactory();

    @Override
    public Object handle(Request request, Response response) {

        // Parsing and uploading of the request
        FileUploadRequest uploadRequest;
        Project project;
        try {
            uploadRequest = requestFactory.createRequest(request);
            uploadRequest.parseUpload(request);
            project = projectService.getProjectIfAllowed(uploadRequest.getProjectId(), uploadRequest.getUserId());
        } catch (RequestException e) {
            return createJsonResponse(Answer.badRequest(e.getErrorCode(), e.getMessage()), response);
        } catch (Exception e) {
            return createJsonResponse(Answer.forbidden(e.getMessage()), response);
        }

        // Processing of the uploaded content
        Answer answer;
        try {
            Project persisted = projectService.addFilesToProject(uploadRequest.getFiles(), project);
            answer = Answer.ok(persisted);
        } catch (Exception e) {
            answer = Answer.internalError(ErrorCode.UNSPECIFIED_ERROR, "Error during file upload. Try again.");
        }

        // Response
        return createJsonResponse(answer, response);
    }

    public class RequestFactory {
        FileUploadRequest createRequest(Request request) throws RequestException {
            return new FileUploadRequest(request);
        }
    }

    private String createJsonResponse(Answer answer, Response response) {

        response.status(answer.getStatusCode());
        response.type("application/json");
        String json = gson.toJson(answer.getResponse());
        response.body(json);
        return response.body();
    }
}
