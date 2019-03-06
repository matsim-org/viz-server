package org.matsim.viz.files.file;

import lombok.val;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.junit.Before;
import org.junit.Test;
import org.matsim.viz.error.InvalidInputException;
import org.matsim.viz.files.entities.FileEntry;
import org.matsim.viz.files.entities.User;
import org.matsim.viz.files.project.ProjectService;
import org.matsim.viz.files.util.TestUtils;

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
        testObject = new FileResource(TestUtils.getProjectService(), "some-id");
    }


    @Test(expected = InvalidInputException.class)
    public void uploadFile_noFileName_exception() {

        ContentDisposition cd = mock(ContentDisposition.class);
        when(cd.getFileName()).thenReturn("");
        val bodyPart = mock(FormDataBodyPart.class);
        when(bodyPart.getContentDisposition()).thenReturn(cd);
        when(bodyPart.getMediaType()).thenReturn(MediaType.APPLICATION_JSON_TYPE);
        bodyPart.setContentDisposition(cd);
        val jsonPart = mock(FormDataBodyPart.class);
        when(jsonPart.getValueAs(FileResource.UploadMetadata.class)).thenReturn(new FileResource.UploadMetadata());

        testObject.uploadFile(null, jsonPart, bodyPart);

        fail("no filename should not be accepted");
    }

    @Test(expected = InvalidInputException.class)
    public void uploadFile_noMetadata_exception() {

        ContentDisposition cd = mock(ContentDisposition.class);
        when(cd.getFileName()).thenReturn("some-name.txt");
        val bodyPart = mock(FormDataBodyPart.class);
        when(bodyPart.getContentDisposition()).thenReturn(cd);
        when(bodyPart.getMediaType()).thenReturn(MediaType.APPLICATION_JSON_TYPE);
        bodyPart.setContentDisposition(cd);

        testObject.uploadFile(null, null, bodyPart);

        fail("no metadata should not be accepted");
    }

    @Test(expected = InvalidInputException.class)
    public void uploadFile_noMediaType_exception() {

        ContentDisposition cd = mock(ContentDisposition.class);
        when(cd.getFileName()).thenReturn("");
        val bodyPart = mock(FormDataBodyPart.class);
        when(bodyPart.getContentDisposition()).thenReturn(cd);
        bodyPart.setContentDisposition(cd);
        val jsonPart = mock(FormDataBodyPart.class);
        when(jsonPart.getValueAs(FileResource.UploadMetadata.class)).thenReturn(new FileResource.UploadMetadata());

        testObject.uploadFile(null, jsonPart, bodyPart);

        fail("no filename should not be accepted");
    }

    @Test
    public void uploadFile_allGood_invokeProjectService() {

        FileEntry entry = new FileEntry();
        entry.setId("some-id");
        ProjectService projectServiceMock = mock(ProjectService.class);
        when(projectServiceMock.addFileToProject(any(), anyString(), any())).thenReturn(entry);
        testObject = new FileResource(projectServiceMock, "some-id");

        ContentDisposition cd = mock(ContentDisposition.class);
        when(cd.getFileName()).thenReturn("some-name.txt");
        val bodyPart = mock(FormDataBodyPart.class);
        when(bodyPart.getContentDisposition()).thenReturn(cd);
        when(bodyPart.getMediaType()).thenReturn(MediaType.WILDCARD_TYPE);

        InputStream stream = mock(InputStream.class);
        when(bodyPart.getValueAs(InputStream.class)).thenReturn(stream);

        val jsonPart = mock(FormDataBodyPart.class);
        when(jsonPart.getValueAs(FileResource.UploadMetadata.class)).thenReturn(new FileResource.UploadMetadata());

        FileEntry result = testObject.uploadFile(new User(), jsonPart, bodyPart);

        assertEquals(entry, result);
    }

    @Test
    public void downloadFile_allHeadersSet() {

        FileEntry entry = new FileEntry();
        entry.setSizeInBytes(10);
        entry.setContentType("application/json");
        FileDownload download = new FileDownload(mock(InputStream.class), entry);
        ProjectService projectServiceMock = mock(ProjectService.class);
        when(projectServiceMock.getFileDownload(anyString(), anyString(), any())).thenReturn(download);
        testObject = new FileResource(projectServiceMock, "any-id");

        Response response = testObject.downloadFile(new User(), "id");

        assertEquals(Response.Status.OK, response.getStatusInfo());
        assertEquals(entry.getContentType(), response.getMediaType().toString());
        assertEquals(String.valueOf(entry.getSizeInBytes()), response.getHeaderString("Content-Length"));
        assertNotNull(response.getHeaderString("Content-Disposition"));
    }

    @Test
    public void deleteFile_invokeProjectService() {

        ProjectService projectServiceMock = mock(ProjectService.class);
        testObject = new FileResource(projectServiceMock, "any-id");

        Response response = testObject.deleteFile(new User(), "id");

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }
}
