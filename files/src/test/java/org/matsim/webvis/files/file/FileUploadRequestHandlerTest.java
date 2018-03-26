package org.matsim.webvis.files.file;

import org.junit.Before;
import org.junit.Test;
import org.matsim.webvis.common.communication.HttpStatus;
import org.matsim.webvis.files.entities.Project;
import org.matsim.webvis.files.project.ProjectService;
import org.matsim.webvis.files.util.TestUtils;
import spark.Request;
import spark.Response;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class FileUploadRequestHandlerTest {

    private FileUploadRequestHandler testObject;

    @Before
    public void setUp() {
        testObject = new FileUploadRequestHandler();
    }

    @Test
    public void handle_fileUploadFails_badRequest() {

        Request request = TestUtils.mockRequestWithRawRequest("Wrong", "request");
        Response response = mock(Response.class);

        testObject.handle(request, response);

        verify(response).status(HttpStatus.BAD_REQUEST);
        verify(response).body(anyString());
    }

    @Test
    public void handle_projectNotFound_forbidden() throws Exception {

        Request request = TestUtils.mockMultipartRequest();
        Response response = mock(Response.class);

        FileUploadRequest upload = mock(FileUploadRequest.class);
        testObject.requestFactory = mock(FileUploadRequestHandler.RequestFactory.class);
        when(testObject.requestFactory.createRequest(any())).thenReturn(upload);

        testObject.projectService = mock(ProjectService.class);
        when(testObject.projectService.getProjectIfAllowed(any(), any())).thenThrow(new Exception());

        testObject.handle(request, response);

        verify(response).status(HttpStatus.FORBIDDEN);
        verify(response).body(anyString());
    }

    @Test
    public void handle_addFilesToProjectFails_internalError() throws Exception {

        Request request = TestUtils.mockMultipartRequest();
        Response response = mock(Response.class);

        FileUploadRequest upload = mock(FileUploadRequest.class);
        testObject.requestFactory = mock(FileUploadRequestHandler.RequestFactory.class);
        when(testObject.requestFactory.createRequest(any())).thenReturn(upload);

        testObject.projectService = mock(ProjectService.class);
        when(testObject.projectService.getProjectIfAllowed(any(), any())).thenReturn(new Project());
        when(testObject.projectService.addFilesToProject(any(), any())).thenThrow(new Exception());

        testObject.handle(request, response);

        verify(response).status(HttpStatus.INTERNAL_SERVER_ERROR);
        verify(response).body(anyString());
    }

    @Test
    public void handle_filesAreAdded_ok() throws Exception {
        Request request = TestUtils.mockMultipartRequest();
        Response response = mock(Response.class);

        FileUploadRequest upload = mock(FileUploadRequest.class);
        testObject.requestFactory = mock(FileUploadRequestHandler.RequestFactory.class);
        when(testObject.requestFactory.createRequest(any())).thenReturn(upload);

        testObject.projectService = mock(ProjectService.class);
        when(testObject.projectService.getProjectIfAllowed(any(), any())).thenReturn(new Project());
        when(testObject.projectService.addFilesToProject(any(), any())).thenReturn(new Project());

        testObject.handle(request, response);

        verify(response).status(HttpStatus.OK);
        verify(response).body(anyString());

    }
}
