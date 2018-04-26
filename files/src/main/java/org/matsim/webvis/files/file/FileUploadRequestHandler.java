package org.matsim.webvis.files.file;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.matsim.webvis.common.communication.Answer;
import org.matsim.webvis.common.communication.EntityAdapterFactory;
import org.matsim.webvis.common.communication.IterableSerializer;
import org.matsim.webvis.common.communication.RequestException;
import org.matsim.webvis.common.service.Error;
import org.matsim.webvis.files.communication.JsonHelper;
import org.matsim.webvis.files.communication.Subject;
import org.matsim.webvis.files.entities.Project;
import org.matsim.webvis.files.project.ProjectService;
import spark.Request;
import spark.Response;
import spark.Route;


public class FileUploadRequestHandler implements Route {

    ProjectService projectService = new ProjectService();
    RequestFactory requestFactory = new RequestFactory();

    private Gson gson = new GsonBuilder().
            registerTypeHierarchyAdapter(Iterable.class, new IterableSerializer())
            .registerTypeAdapterFactory(new EntityAdapterFactory())
            .setExclusionStrategies(new FileEntryExclusionStrategy())
            .create();

    protected Answer process(Request request, Response response) {

        Subject subject = Subject.getSubject(request);

        // Parsing and uploading of the request
        FileUploadRequest uploadRequest;
        Project project;
        try {
            uploadRequest = requestFactory.createRequest(request);
            uploadRequest.parseUpload(request);
            project = projectService.findProjectIfAllowed(uploadRequest.getProjectId(), subject.getUser().getId());
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

    @Override
    public Object handle(Request request, Response response) {
        Answer answer = process(request, response);
        return JsonHelper.createJsonResponse(answer, response, gson);
    }

    class RequestFactory {
        FileUploadRequest createRequest(Request request) throws RequestException {
            return new FileUploadRequest(request);
        }
    }
}
