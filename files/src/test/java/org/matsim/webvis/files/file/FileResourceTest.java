package org.matsim.webvis.files.file;

import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.junit.Before;
import org.junit.Test;
import org.matsim.webvis.error.InvalidInputException;
import org.matsim.webvis.files.entities.FileEntry;
import org.matsim.webvis.files.entities.Project;
import org.matsim.webvis.files.entities.User;
import org.matsim.webvis.files.project.ProjectService;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FileResourceTest {

    private FileResource testObject;

    @Before
    public void setUp() {
        testObject = new FileResource("some-id");
    }

    @Test(expected = InvalidInputException.class)
    public void uploadFiles_onlyTextFields_exception() {

        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.field("key", "value");

        testObject.uploadFiles(null, multiPart);

        fail("no file uploads should cause exception");
    }

    @Test(expected = InvalidInputException.class)
    public void uploadFiles_noFileName_exception() {

        ContentDisposition cd = mock(ContentDisposition.class);
        when(cd.getFileName()).thenReturn("");
        FormDataBodyPart formDataBodyPart = mock(FormDataBodyPart.class);
        when(formDataBodyPart.getContentDisposition()).thenReturn(cd);
        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.getBodyParts().add(formDataBodyPart);

        testObject.uploadFiles(null, multiPart);

        fail("no filename should not be accepted");
    }

    @Test
    public void uploadFiles_allGood_invokeProjectService() {

        Project project = new Project();
        testObject.projectService = mock(ProjectService.class);
        when(testObject.projectService.addFilesToProject(any(), anyString(), any())).thenReturn(project);

        ContentDisposition cd = mock(ContentDisposition.class);
        when(cd.getFileName()).thenReturn("filename");
        FormDataBodyPart formDataBodyPart = mock(FormDataBodyPart.class);
        when(formDataBodyPart.getContentDisposition()).thenReturn(cd);
        when(formDataBodyPart.getMediaType()).thenReturn(MediaType.WILDCARD_TYPE);

        InputStream stream = mock(InputStream.class);
        when(formDataBodyPart.getValueAs(InputStream.class)).thenReturn(stream);
        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.getBodyParts().add(formDataBodyPart);

        Project result = testObject.uploadFiles(new User(), multiPart);

        assertEquals(project, result);
    }

    @Test
    public void downloadFile_allHeadersSet() {

        FileEntry entry = new FileEntry();
        entry.setSizeInBytes(10);
        entry.setContentType("application/json");
        FileDownload download = new FileDownload(mock(InputStream.class), entry);
        testObject.projectService = mock(ProjectService.class);
        when(testObject.projectService.getFileDownload(anyString(), anyString(), any()))
                .thenReturn(download);

        Response response = testObject.downloadFile(new User(), "id");

        assertEquals(Response.Status.OK, response.getStatusInfo());
        assertEquals(entry.getContentType(), response.getMediaType().toString());
        assertEquals(String.valueOf(entry.getSizeInBytes()), response.getHeaderString("Content-Length"));
        assertNotNull(response.getHeaderString("Content-Disposition"));
    }

    @Test
    public void deleteFile_invokeProjectService() {

        Project project = new Project();
        testObject.projectService = mock(ProjectService.class);
        when(testObject.projectService.removeFileFromProject(anyString(), anyString(), any())).thenReturn(project);

        Project response = testObject.deleteFile(new User(), "id");

        assertEquals(project, response);
    }
}
