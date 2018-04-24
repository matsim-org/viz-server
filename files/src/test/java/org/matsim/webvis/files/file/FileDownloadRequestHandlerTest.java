package org.matsim.webvis.files.file;

import com.google.gson.Gson;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.matsim.webvis.files.communication.AuthenticationResult;
import org.matsim.webvis.files.communication.ContentType;
import org.matsim.webvis.files.communication.Subject;
import org.matsim.webvis.files.entities.User;
import org.matsim.webvis.files.project.ProjectService;
import org.matsim.webvis.files.user.UserService;
import spark.Request;
import spark.Response;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class FileDownloadRequestHandlerTest {

    private FileDownloadRequestHandler testObject;

    @Before
    public void setUp() {
        testObject = new FileDownloadRequestHandler();
    }

    @Test
    public void handle_wrongContentType_invalidRequest() {
        Request req = mock(Request.class);
        when(req.contentType()).thenReturn("something-wrong");

        Response res = mock(Response.class);

        testObject.handle(req, res);

        verify(res).status(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void handle_parsingError_invalidRequest() {
        Request req = mock(Request.class);
        when(req.contentType()).thenReturn(ContentType.APPLICATION_JSON);
        when(req.body()).thenReturn("{not: json");

        Response res = mock(Response.class);

        testObject.handle(req, res);

        verify(res).status(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void handle_errorWhileCreatingInputStream_internalError() throws Exception {

        testObject.projectService = mock(ProjectService.class);
        when(testObject.projectService.getFileStream(any(), any(), any())).thenThrow(new Exception("e"));

        Request req = mock(Request.class);
        when(req.contentType()).thenReturn(ContentType.APPLICATION_JSON);

        FileDownloadRequest body = new FileDownloadRequest();
        body.fileId = "id";
        body.projectId = "pId";
        when(req.body()).thenReturn(new Gson().toJson(body));
        when(req.attribute(any())).thenReturn(new AuthenticationResult());

        User subject = new User();
        Subject.userService = mock(UserService.class);
        when(Subject.userService.findByIdentityProviderId(any())).thenReturn(subject);

        Response res = mock(Response.class);

        testObject.handle(req, res);

        verify(res).status(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void handle_allRight_outputStream() throws Exception {

        InputStream inStream = new InputStream() {
            @Override
            public int read() {
                return -1;
            }
        };
        testObject.projectService = mock(ProjectService.class);
        when(testObject.projectService.getFileStream(any(), any(), any())).thenReturn(inStream);

        Request req = mock(Request.class);
        when(req.contentType()).thenReturn(ContentType.APPLICATION_JSON);

        FileDownloadRequest body = new FileDownloadRequest();
        body.fileId = "id";
        body.projectId = "pId";
        when(req.body()).thenReturn(new Gson().toJson(body));
        when(req.attribute(any())).thenReturn(new AuthenticationResult());

        User subject = new User();
        Subject.userService = mock(UserService.class);
        when(Subject.userService.findByIdentityProviderId(any())).thenReturn(subject);

        HttpServletResponse rawResponse = mock(HttpServletResponse.class);
        when(rawResponse.getOutputStream()).thenReturn(new ServletOutputStream() {
            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {

            }

            @Override
            public void write(int b) {

            }
        });
        Response res = mock(Response.class);
        when(res.raw()).thenReturn(rawResponse);

        Object result = testObject.handle(req, res);

        assertTrue(result instanceof String);
        assertEquals("OK", (String) result);
    }
}
