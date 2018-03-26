package org.matsim.webvis.files.util;

import org.apache.commons.fileupload.FileItem;
import org.matsim.webvis.files.config.Configuration;
import spark.Request;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestUtils {

    public static void loadConfig() throws UnsupportedEncodingException, FileNotFoundException {
        Configuration.loadConfigFile(getResourcePath("test-config.json"), true);
    }

    private static String getResourcePath(String resourceFile) throws UnsupportedEncodingException {
        return URLDecoder.decode(TestUtils.class.getClassLoader().getResource(resourceFile).getFile(), "UTF-8");
    }

    public static FileItem mockFileItem(String filename, String contentType, long size) {
        FileItem item = mock(FileItem.class);
        when(item.getName()).thenReturn(filename);
        when(item.getContentType()).thenReturn(contentType);
        when(item.getSize()).thenReturn(size);
        return item;
    }

    public static FileItem mockFormFieldItem(String fieldName, String value) {
        FileItem item = mock(FileItem.class);
        when(item.isFormField()).thenReturn(true);
        when(item.getFieldName()).thenReturn(fieldName);
        when(item.getString()).thenReturn(value);
        return item;
    }

    public static Request mockMultipartRequest() {
        return mockRequestWithRawRequest("POST", "multipart/form-data");
    }

    public static Request mockRequestWithRawRequest(String method, String contentType) {

        HttpServletRequest raw = mock(HttpServletRequest.class);
        when(raw.getMethod()).thenReturn(method);
        when(raw.getContentType()).thenReturn(contentType);

        Request result = mock(Request.class);
        when(result.raw()).thenReturn(raw);
        return result;
    }
}
