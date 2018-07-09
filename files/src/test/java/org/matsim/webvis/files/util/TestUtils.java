package org.matsim.webvis.files.util;

import org.matsim.webvis.files.agent.UserDAO;
import org.matsim.webvis.files.entities.FileEntry;
import org.matsim.webvis.files.entities.Project;
import org.matsim.webvis.files.entities.User;
import org.matsim.webvis.files.project.ProjectDAO;
import org.matsim.webvis.files.project.ProjectService;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import static org.junit.Assert.fail;

public class TestUtils {

    private static UserDAO userDAO = new UserDAO();
    private static ProjectDAO projectDAO = new ProjectDAO();
    private static ProjectService projectService = ProjectService.Instance;

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
