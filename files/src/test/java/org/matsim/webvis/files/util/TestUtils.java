package org.matsim.webvis.files.util;

import org.matsim.webvis.database.PersistenceUnit;
import org.matsim.webvis.files.agent.AgentService;
import org.matsim.webvis.files.agent.UserDAO;
import org.matsim.webvis.files.config.AppConfiguration;
import org.matsim.webvis.files.entities.FileEntry;
import org.matsim.webvis.files.entities.Project;
import org.matsim.webvis.files.entities.User;
import org.matsim.webvis.files.permission.PermissionDAO;
import org.matsim.webvis.files.permission.PermissionService;
import org.matsim.webvis.files.project.ProjectDAO;
import org.matsim.webvis.files.project.ProjectService;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import static org.junit.Assert.fail;

public class TestUtils {

    private static final PersistenceUnit persistenceUnit = new PersistenceUnit("org.matsim.viz.files");
    private static final UserDAO userDAO = new UserDAO(persistenceUnit);
    private static final ProjectDAO projectDAO = new ProjectDAO(persistenceUnit);
    private static final AgentService agentService = new AgentService(userDAO);
    private static final PermissionService permissionService = new PermissionService(agentService, new PermissionDAO(persistenceUnit));
    private static final ProjectService projectService = new ProjectService(projectDAO, permissionService, null);

    public static PersistenceUnit getPersistenceUnit() {
        return persistenceUnit;
    }

    public static AgentService getAgentService() {
        return agentService;
    }

    public static PermissionService getPermissionService() {
        return permissionService;
    }

    public static ProjectService getProjectService() {
        return projectService;
    }

    public static ProjectDAO getProjectDAO() {
        return projectDAO;
    }

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

    public static User persistUser() {
        return userDAO.persist(new User());
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

    public static void loadTestConfig() {
        if (AppConfiguration.getInstance() == null)
            AppConfiguration.setInstance(new AppConfiguration());
    }

    public static void writeTextFile(Path file, String content) {

        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
            writer.write(content, 0, content.length());
        } catch (IOException e) {
            throw new RuntimeException("Couldn't write file " + file.toString());
        }
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
