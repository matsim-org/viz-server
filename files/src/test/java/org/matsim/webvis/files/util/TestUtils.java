package org.matsim.webvis.files.util;

import org.apache.commons.fileupload.FileItem;
import org.matsim.webvis.files.communication.AuthenticationResult;
import org.matsim.webvis.files.communication.Subject;
import org.matsim.webvis.files.config.Configuration;
import org.matsim.webvis.files.entities.User;
import spark.Request;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestUtils {

    public static void loadConfig() throws UnsupportedEncodingException, FileNotFoundException {
        Configuration.loadConfigFile(getResourcePath("test-config.json"), true);
    }

    private static String getResourcePath(String resourceFile) throws UnsupportedEncodingException {
        //noinspection ConstantConditions
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
        when(result.attribute(Subject.SUBJECT_ATTRIBUTE)).thenReturn(new AuthenticationResult());
        return result;
    }

    public static Subject createSubject(User user) {
        return new Subject(null, user);
    }

    /**
     * removes files like https://docs.oracle.com/javase/8/docs/api/java/nio/file/FileVisitor.html
     *
     * @param start root of the file tree. Will be removed as well.
     * @throws IOException if something goes wrong
     */
    public static void removeFileTree(Path start) throws IOException {
        if (!Files.exists(start)) {
            return;
        }
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
                if (e == null) {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                } else {
                    throw e;
                }
            }
        });
    }
}
