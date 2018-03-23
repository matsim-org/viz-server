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

    private static Gson gson = new GsonBuilder().
            registerTypeHierarchyAdapter(Iterable.class, new IterableSerializer())
            .registerTypeAdapterFactory(new EntityAdapterFactory())
            .setExclusionStrategies(new FileEntryExclusionStrategy())
            .create();

    private ProjectService projectService = new ProjectService();

    @Override
    public Object handle(Request request, Response response) {

        FileUploadRequest uploadRequest;
        Project project;
        try {
            uploadRequest = new FileUploadRequest(request);
            uploadRequest.parseUpload(request);
            project = projectService.getProjectIfAllowed(uploadRequest.getProject_id(), uploadRequest.getUser_id());
        } catch (RequestException e) {
            return createJsonResponse(Answer.badRequest(e.getErrorCode(), e.getMessage()), response);
        } catch (Exception e) {
            return createJsonResponse(Answer.forbidden(e.getMessage()), response);
        }

        Answer answer;
        try {
            Project persisted = projectService.addFilesToProject(uploadRequest.getFiles(), project);
            answer = Answer.ok(persisted);
        } catch (Exception e) {
            answer = Answer.internalError(ErrorCode.UNSPECIFIED_ERROR, "Error during file upload. Try again.");
        }

        return createJsonResponse(answer, response);
    }

    private String createJsonResponse(Answer answer, Response response) {

        response.status(answer.getStatusCode());
        response.type("application/json");
        String json = gson.toJson(answer.getResponse());
        response.body(json);
        return response.body();
    }
}
