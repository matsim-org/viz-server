package org.matsim.webvis.files.util;

import org.apache.commons.fileupload.FileItem;
import org.matsim.webvis.common.auth.AuthenticationResult;
import org.matsim.webvis.files.agent.UserDAO;
import org.matsim.webvis.files.config.Configuration;
import org.matsim.webvis.files.entities.FileEntry;
import org.matsim.webvis.files.entities.Project;
import org.matsim.webvis.files.entities.User;
import org.matsim.webvis.files.permission.Subject;
import org.matsim.webvis.files.project.ProjectDAO;
import org.matsim.webvis.files.project.ProjectService;
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

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestUtils {

    private static UserDAO userDAO = new UserDAO();
    private static ProjectDAO projectDAO = new ProjectDAO();
    private static ProjectService projectService = new ProjectService();

    public static Project persistProjectWithCreator(String projectName, String creatorsAuthId) {
        User user = new User();
        user.setAuthId(creatorsAuthId);
        try {
            userDAO.persist(user);
        } catch (Exception e) {
            fail("failed to persist user with auth id: " + creatorsAuthId);
        }
        try {
            return projectService.createNewProject(projectName, user);
        } catch (Exception e) {
            fail("Failed to create project with name: " + projectName);
        }
        return null;
    }

    public static Project persistProjectWithCreator(String projectName) {
        return persistProjectWithCreator(projectName, "some-auth-id");
    }

    public static User persistUser(String authId) {
        User user = new User();
        user.setAuthId(authId);
        return userDAO.persist(user);
    }

    public static Project addFileEntry(Project project) {
        FileEntry entry = new FileEntry();
        project.getFiles().add(entry);
        entry.setProject(project);
        return projectDAO.persist(project);
    }

    public static void removeAllEntities() {
        projectDAO.removeAllProjects();
        userDAO.removeAllUser();
    }

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
        when(result.contentType()).thenReturn(contentType);
        return result;
    }

    public static Request mockRequest(String contentType, String scope, String subjectId) {
        return mockRequest(contentType, mockAuthResult(scope, subjectId));
    }

    private static Request mockRequest(String contentType, AuthenticationResult authResult) {

        Request request = mock(Request.class);
        when(request.contentType()).thenReturn(contentType);
        when(request.attribute(AuthenticationResult.SUBJECT_ATTRIBUTE)).thenReturn(authResult);
        return request;
    }

    public static AuthenticationResult mockAuthResult(String scope, String subjectId) {

        AuthenticationResult result = mock(AuthenticationResult.class);
        when(result.getScope()).thenReturn(scope);
        when(result.getSub()).thenReturn(subjectId);
        return result;
    }


    public static Subject createSubject(User user) {
        return new Subject(user, null);
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
