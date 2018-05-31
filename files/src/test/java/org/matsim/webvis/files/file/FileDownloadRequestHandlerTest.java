package org.matsim.webvis.files.file;

import com.google.gson.Gson;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.matsim.webvis.common.auth.AuthenticationResult;
import org.matsim.webvis.common.communication.ContentType;
import org.matsim.webvis.common.service.CodedException;
import org.matsim.webvis.common.service.Error;
import org.matsim.webvis.common.service.InvalidInputException;
import org.matsim.webvis.files.communication.Subject;
import org.matsim.webvis.files.entities.FileEntry;
import org.matsim.webvis.files.entities.Project;
import org.matsim.webvis.files.entities.User;
import org.matsim.webvis.files.project.ProjectService;
import org.matsim.webvis.files.agent.AgentService;
import spark.Request;
import spark.Response;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

import static junit.framework.TestCase.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class FileDownloadRequestHandlerTest {

    private FileDownloadRequestHandler testObject;

    @Before
    public void setUp() {
        testObject = new FileDownloadRequestHandler();
    }

    @Test(expected = InvalidInputException.class)
    public void handle_wrongContentType_invalidRequest() {
        Request req = mock(Request.class);
        when(req.contentType()).thenReturn("something-wrong");

        Response res = mock(Response.class);

        testObject.handle(req, res);

        fail("invalid input should yield invalid input exception.");
    }

    @Test(expected = InvalidInputException.class)
    public void handle_parsingError_invalidRequest() {
        Request req = mock(Request.class);
        when(req.contentType()).thenReturn(ContentType.APPLICATION_JSON);
        when(req.body()).thenReturn("{not: json");

        Response res = mock(Response.class);

        testObject.handle(req, res);

        fail("invalid input should yield invalid input exception.");
    }

    @Test
    public void handle_fileNotFound_codedException() {

        Project project = new Project();
        testObject.projectService = mock(ProjectService.class);
        when(testObject.projectService.getFileStream(any(), any(), any())).thenThrow(new CodedException("e", "error"));
        when(testObject.projectService.find(any(), any())).thenReturn(project);

        Request req = mock(Request.class);
        when(req.contentType()).thenReturn(ContentType.APPLICATION_JSON);

        FileRequest body = new FileRequest("id", "pid");
        when(req.body()).thenReturn(new Gson().toJson(body));
        when(req.attribute(any())).thenReturn(new AuthenticationResult());

        User subject = new User();
        Subject.agentService = mock(AgentService.class);
        when(Subject.agentService.findByIdentityProviderId(any())).thenReturn(subject);

        Response res = mock(Response.class);

        try {
            testObject.handle(req, res);
            fail("file not found should yield exception");
        } catch (CodedException e) {
            assertEquals(Error.RESOURCE_NOT_FOUND, e.getErrorCode());
        }
    }

    @Test(expected = CodedException.class)
    public void handle_errorWhileCreatingInputStream_internalError() {

        testObject.projectService = mock(ProjectService.class);
        when(testObject.projectService.getFileStream(any(), any(), any())).thenThrow(new CodedException("e", "error"));

        Request req = mock(Request.class);
        when(req.contentType()).thenReturn(ContentType.APPLICATION_JSON);

        FileRequest body = new FileRequest("id", "pid");
        when(req.body()).thenReturn(new Gson().toJson(body));
        when(req.attribute(any())).thenReturn(new AuthenticationResult());

        FileEntry entry = new FileEntry();
        entry.setId(body.getFileId());
        Project project = new Project();
        project.setId(body.getProjectId());
        project.getFiles().add(entry);
        when(testObject.projectService.find(any(), any())).thenReturn(project);

        User subject = new User();
        Subject.agentService = mock(AgentService.class);
        when(Subject.agentService.findByIdentityProviderId(any())).thenReturn(subject);

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

        FileRequest body = new FileRequest("id", "pId");
        when(req.body()).thenReturn(new Gson().toJson(body));
        when(req.attribute(any())).thenReturn(new AuthenticationResult());

        FileEntry entry = new FileEntry();
        entry.setId(body.getFileId());
        Project project = new Project();
        project.setId(body.getProjectId());
        project.getFiles().add(entry);
        when(testObject.projectService.find(any(), any())).thenReturn(project);

        User subject = new User();
        Subject.agentService = mock(AgentService.class);
        when(Subject.agentService.findByIdentityProviderId(any())).thenReturn(subject);

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

        assertTrue(result instanceof Integer);
        assertEquals(HttpStatus.SC_OK, (int) result);
    }
}
