package org.matsim.webvis.files.file;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.junit.Test;
import org.matsim.webvis.error.InvalidInputException;
import org.matsim.webvis.files.util.TestUtils;
import spark.Request;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FileUploadRequestTest {

    @Test(expected = InvalidInputException.class)
    public void constructor_noMultipart_exception() throws InvalidInputException {

        Request request = TestUtils.mockRequestWithRawRequest("WRONG", "content-type");

        new FileUploadRequest(request);
    }

    @Test
    public void constructor_isMultipart() throws InvalidInputException {

        Request request = TestUtils.mockRequestWithRawRequest("POST", "multipart/form-data");

        FileUploadRequest upload = new FileUploadRequest(request);

        assertNotNull(upload);
    }

    @Test(expected = InvalidInputException.class)
    public void parseUpload_noProjectId_exception() throws FileUploadException, InvalidInputException {
        Request request = TestUtils.mockMultipartRequest();
        FileUploadRequest upload = new FileUploadRequest(request);
        upload.upload = mock(ServletFileUpload.class);

        List<FileItem> items = new ArrayList<>();
        items.add(TestUtils.mockFormFieldItem("invalidKey", "unimportant-value"));
        items.add(TestUtils.mockFileItem("test", "test", 1L));
        when(upload.upload.parseRequest(request.raw())).thenReturn(items);

        upload.parseUpload(request);

        fail("missing projectId should throw exception");
    }

    @Test(expected = InvalidInputException.class)
    public void parseUpload_notEnoughParts_exception() throws FileUploadException, InvalidInputException {
        Request request = TestUtils.mockMultipartRequest();
        FileUploadRequest upload = new FileUploadRequest(request);
        upload.upload = mock(ServletFileUpload.class);

        List<FileItem> items = new ArrayList<>();
        items.add(TestUtils.mockFormFieldItem("projectId", "12345"));
        when(upload.upload.parseRequest(request.raw())).thenReturn(items);

        upload.parseUpload(request);

        fail("to few arguments should throw exception");
    }

    @Test(expected = InvalidInputException.class)
    public void parseUpload_noFile_exception() throws FileUploadException, InvalidInputException {
        Request request = TestUtils.mockMultipartRequest();
        FileUploadRequest upload = new FileUploadRequest(request);
        upload.upload = mock(ServletFileUpload.class);

        List<FileItem> items = new ArrayList<>();
        items.add(TestUtils.mockFormFieldItem("projectId", "12345"));
        items.add(TestUtils.mockFormFieldItem("another", "form_field"));
        when(upload.upload.parseRequest(request.raw())).thenReturn(items);

        upload.parseUpload(request);

        fail("no uploaded file should throw exception");
    }

    @Test
    public void parseUpload_projectIdAndFile_fileItems() throws FileUploadException, InvalidInputException {

        Request request = TestUtils.mockMultipartRequest();
        FileUploadRequest upload = new FileUploadRequest(request);
        upload.upload = mock(ServletFileUpload.class);

        List<FileItem> items = new ArrayList<>();
        final String projectId = "projectId";
        final String userId = "userId";
        items.add(TestUtils.mockFormFieldItem("projectId", projectId));
        items.add(TestUtils.mockFileItem("test", "test", 1L));
        when(upload.upload.parseRequest(request.raw())).thenReturn(items);

        upload.parseUpload(request);

        assertEquals(upload.getProjectId(), projectId);
        assertEquals(1, upload.getFiles().size());
    }
}
