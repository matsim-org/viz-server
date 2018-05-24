package org.matsim.webvis.files.file;

import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.matsim.webvis.common.communication.Answer;
import org.matsim.webvis.common.service.CodedException;
import org.matsim.webvis.files.communication.Subject;
import org.matsim.webvis.files.entities.Project;
import org.matsim.webvis.files.entities.User;
import org.matsim.webvis.files.project.ProjectService;
import org.matsim.webvis.files.util.TestUtils;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FileDeleteRequestHandlerTest {

    private FileDeleteRequestHandler testObject;

    @Before
    public void setUp() {
        testObject = new FileDeleteRequestHandler();
        testObject.projectService = mock(ProjectService.class);
    }

    @Test
    public void process_projectIdMising_badRequest() {

        FileRequest request = new FileRequest("id", "");
        Subject subject = TestUtils.createSubject(new User());

        Answer answer = testObject.process(request, subject);

        assertEquals(HttpStatus.SC_BAD_REQUEST, answer.getStatusCode());
    }

    @Test
    public void process_fileIdMissing_badRequest() {

        FileRequest request = new FileRequest("", "id");
        Subject subject = TestUtils.createSubject(new User());

        Answer answer = testObject.process(request, subject);

        assertEquals(HttpStatus.SC_BAD_REQUEST, answer.getStatusCode());
    }

    @Test(expected = CodedException.class)
    public void process_removeInServiceThrowsException_internalError() throws CodedException {

        FileRequest request = new FileRequest("id", "id");
        Subject subject = TestUtils.createSubject(new User());
        when(testObject.projectService.removeFileFromProject(any(), any(), any())).thenThrow(new CodedException("error", "error"));

        Answer answer = testObject.process(request, subject);

        fail("failing service call should result in exception");
    }

    @Test
    public void process_allGood_ok() throws CodedException {

        FileRequest request = new FileRequest("id", "id");
        Subject subject = TestUtils.createSubject(new User());
        Project result = new Project();
        when(testObject.projectService.removeFileFromProject(any(), any(), any())).thenReturn(result);

        Answer answer = testObject.process(request, subject);

        assertEquals(HttpStatus.SC_OK, answer.getStatusCode());
        assertEquals(result, answer.getResponse());
    }
}
