package org.matsim.webvis.files.file;

import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.matsim.webvis.common.communication.Answer;
import org.matsim.webvis.common.communication.ErrorResponse;
import org.matsim.webvis.common.service.CodedException;
import org.matsim.webvis.common.service.InvalidInputException;
import org.matsim.webvis.files.communication.Subject;
import org.matsim.webvis.files.entities.Project;
import org.matsim.webvis.files.entities.User;
import org.matsim.webvis.files.project.ProjectService;
import org.matsim.webvis.files.agent.AgentService;
import org.matsim.webvis.files.util.TestUtils;
import spark.Request;
import spark.Response;

import static junit.framework.TestCase.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FileUploadRequestHandlerTest {

    private FileUploadRequestHandler testObject;

    @Before
    public void setUp() {

        testObject = new FileUploadRequestHandler();
        Subject.agentService = mock(AgentService.class);
        when(Subject.agentService.findByIdentityProviderId(any())).thenReturn(new User());
    }

    @Test(expected = InvalidInputException.class)
    public void process_invalidRequest_invalidInputException() {

        Request request = TestUtils.mockRequestWithRawRequest("Wrong", "request");
        Response response = mock(Response.class);

        testObject.process(request, response);

        fail("invalid input should raise exception");
    }

    @Test(expected = CodedException.class)
    public void process_projectNotFound_codedException() {

        Request request = TestUtils.mockMultipartRequest();
        Response response = mock(Response.class);

        FileUploadRequest upload = mock(FileUploadRequest.class);
        testObject.requestFactory = mock(FileUploadRequestHandler.RequestFactory.class);
        when(testObject.requestFactory.createRequest(any())).thenReturn(upload);

        testObject.projectService = mock(ProjectService.class);
        when(testObject.projectService.find(any(), any())).thenThrow(new CodedException("code", "message"));

        Answer answer = testObject.process(request, response);

        assertEquals(HttpStatus.SC_FORBIDDEN, answer.getStatusCode());
        assertTrue(answer.getResponse() instanceof ErrorResponse);
    }

    @Test(expected = CodedException.class)
    public void process_addFilesToProjectFails_internalError() {

        Request request = TestUtils.mockMultipartRequest();
        Response response = mock(Response.class);

        FileUploadRequest upload = mock(FileUploadRequest.class);
        testObject.requestFactory = mock(FileUploadRequestHandler.RequestFactory.class);
        when(testObject.requestFactory.createRequest(any())).thenReturn(upload);

        testObject.projectService = mock(ProjectService.class);
        when(testObject.projectService.find(any(), any())).thenReturn(new Project());
        when(testObject.projectService.addFilesToProject(any(), any(), any())).thenThrow(new CodedException("bla", "bla"));

        Answer answer = testObject.process(request, response);

        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, answer.getStatusCode());
        assertTrue(answer.getResponse() instanceof ErrorResponse);
    }

    @Test
    public void process_filesAreAdded_ok() {
        Request request = TestUtils.mockMultipartRequest();
        Response response = mock(Response.class);

        FileUploadRequest upload = mock(FileUploadRequest.class);
        testObject.requestFactory = mock(FileUploadRequestHandler.RequestFactory.class);
        when(testObject.requestFactory.createRequest(any())).thenReturn(upload);

        testObject.projectService = mock(ProjectService.class);
        when(testObject.projectService.find(any(), any())).thenReturn(new Project());
        when(testObject.projectService.addFilesToProject(any(), any(), any())).thenReturn(new Project());

        Answer answer = testObject.process(request, response);

        assertEquals(HttpStatus.SC_OK, answer.getStatusCode());
        assertTrue(answer.getResponse() instanceof Project);
    }
}
