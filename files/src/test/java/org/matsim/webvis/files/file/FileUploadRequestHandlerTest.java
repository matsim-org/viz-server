package org.matsim.webvis.files.file;

import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.matsim.webvis.common.communication.Answer;
import org.matsim.webvis.common.communication.ErrorResponse;
import org.matsim.webvis.files.communication.Subject;
import org.matsim.webvis.files.entities.Project;
import org.matsim.webvis.files.entities.User;
import org.matsim.webvis.files.project.ProjectService;
import org.matsim.webvis.files.user.UserService;
import org.matsim.webvis.files.util.TestUtils;
import spark.Request;
import spark.Response;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FileUploadRequestHandlerTest {

    private FileUploadRequestHandler testObject;

    @Before
    public void setUp() {

        testObject = new FileUploadRequestHandler();
        Subject.userService = mock(UserService.class);
        when(Subject.userService.findByIdentityProviderId(any())).thenReturn(new User());
    }

    @Test
    public void process_fileUploadFails_badRequest() {

        Request request = TestUtils.mockRequestWithRawRequest("Wrong", "request");
        Response response = mock(Response.class);

        Answer answer = testObject.process(request, response);

        assertEquals(HttpStatus.SC_BAD_REQUEST, answer.getStatusCode());
        assertTrue(answer.getResponse() instanceof ErrorResponse);
    }

    @Test
    public void process_projectNotFound_forbidden() throws Exception {

        Request request = TestUtils.mockMultipartRequest();
        Response response = mock(Response.class);

        FileUploadRequest upload = mock(FileUploadRequest.class);
        testObject.requestFactory = mock(FileUploadRequestHandler.RequestFactory.class);
        when(testObject.requestFactory.createRequest(any())).thenReturn(upload);

        testObject.projectService = mock(ProjectService.class);
        when(testObject.projectService.getProjectIfAllowed(any(), any())).thenThrow(new Exception());

        Answer answer = testObject.process(request, response);

        assertEquals(HttpStatus.SC_FORBIDDEN, answer.getStatusCode());
        assertTrue(answer.getResponse() instanceof ErrorResponse);
    }

    @Test
    public void process_addFilesToProjectFails_internalError() throws Exception {

        Request request = TestUtils.mockMultipartRequest();
        Response response = mock(Response.class);

        FileUploadRequest upload = mock(FileUploadRequest.class);
        testObject.requestFactory = mock(FileUploadRequestHandler.RequestFactory.class);
        when(testObject.requestFactory.createRequest(any())).thenReturn(upload);

        testObject.projectService = mock(ProjectService.class);
        when(testObject.projectService.getProjectIfAllowed(any(), any())).thenReturn(new Project());
        when(testObject.projectService.addFilesToProject(any(), any())).thenThrow(new Exception());

        Answer answer = testObject.process(request, response);

        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, answer.getStatusCode());
        assertTrue(answer.getResponse() instanceof ErrorResponse);
    }

    @Test
    public void process_filesAreAdded_ok() throws Exception {
        Request request = TestUtils.mockMultipartRequest();
        Response response = mock(Response.class);

        FileUploadRequest upload = mock(FileUploadRequest.class);
        testObject.requestFactory = mock(FileUploadRequestHandler.RequestFactory.class);
        when(testObject.requestFactory.createRequest(any())).thenReturn(upload);

        testObject.projectService = mock(ProjectService.class);
        when(testObject.projectService.getProjectIfAllowed(any(), any())).thenReturn(new Project());
        when(testObject.projectService.addFilesToProject(any(), any())).thenReturn(new Project());

        Answer answer = testObject.process(request, response);

        assertEquals(HttpStatus.SC_OK, answer.getStatusCode());
        assertTrue(answer.getResponse() instanceof Project);
    }
}
