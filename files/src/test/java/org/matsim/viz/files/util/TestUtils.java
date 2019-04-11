package org.matsim.viz.files.util;

import org.matsim.viz.database.PersistenceUnit;
import org.matsim.viz.files.agent.AgentService;
import org.matsim.viz.files.agent.UserDAO;
import org.matsim.viz.files.config.AppConfiguration;
import org.matsim.viz.files.entities.FileEntry;
import org.matsim.viz.files.entities.Project;
import org.matsim.viz.files.entities.Tag;
import org.matsim.viz.files.entities.User;
import org.matsim.viz.files.notifications.NotificationDAO;
import org.matsim.viz.files.notifications.Notifier;
import org.matsim.viz.files.permission.PermissionDAO;
import org.matsim.viz.files.permission.PermissionService;
import org.matsim.viz.files.project.ProjectDAO;
import org.matsim.viz.files.project.ProjectService;
import org.matsim.viz.files.visualization.VisualizationDAO;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

public class TestUtils {

    private static final PersistenceUnit persistenceUnit = new PersistenceUnit("org.matsim.viz.files");
    private static final UserDAO userDAO = new UserDAO(persistenceUnit);
    private static final ProjectDAO projectDAO = new ProjectDAO(persistenceUnit);
    private static final VisualizationDAO visualizationDao = new VisualizationDAO(persistenceUnit);
    private static final NotificationDAO notificationDAO = new NotificationDAO(persistenceUnit);
    private static final AgentService agentService = new AgentService(userDAO);
    private static final PermissionService permissionService = new PermissionService(agentService, new PermissionDAO(persistenceUnit));
    private static final ProjectService projectService = new ProjectService(projectDAO, permissionService, null, mock(Notifier.class));

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

    public static NotificationDAO getNotificationDAO() {
        return notificationDAO;
    }

    public static VisualizationDAO getVisualizationDAO() {
        return visualizationDao;
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

    public static Project addFileEntry(Project project, String filename) {
        FileEntry entry = new FileEntry();
        entry.setUserFileName(filename);
        entry.setPersistedFileName(filename);
        project.getFiles().add(entry);
        entry.setProject(project);
        return projectDAO.persist(project);
    }

    public static Project addTag(Project project, String tagName, String tagType) {
        Tag tag = new Tag(tagName, tagType, project);
        project.addTag(tag);
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

    public static void removeNotificationTypes() {
        notificationDAO.removeAllNotificationTypes();
    }

    public static void removeSubscriptions() {
        notificationDAO.removeAllSubscriptions();
    }
}
