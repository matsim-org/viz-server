package org.matsim.webvis.files.project;

import org.matsim.webvis.error.InternalException;
import org.matsim.webvis.error.InvalidInputException;
import org.matsim.webvis.files.entities.*;
import org.matsim.webvis.files.file.FileDownload;
import org.matsim.webvis.files.file.FileUpload;
import org.matsim.webvis.files.file.Repository;
import org.matsim.webvis.files.notifications.AbstractNotification;
import org.matsim.webvis.files.notifications.NotificationType;
import org.matsim.webvis.files.notifications.Notifier;
import org.matsim.webvis.files.permission.PermissionService;
import org.matsim.webvis.files.visualization.VisualizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProjectService {

    private static Logger logger = LoggerFactory.getLogger(ProjectService.class);

    private final ProjectDAO projectDAO;
    private final Repository repository;
    private final PermissionService permissionService;
    private final Notifier notifier;

    public ProjectService(ProjectDAO projectDAO, PermissionService permissionService, Repository repository, Notifier notifier) {
        this.projectDAO = projectDAO;
        this.permissionService = permissionService;
        this.repository = repository;
        this.notifier = notifier;
        this.createNotificationTypes();
    }

    public Project createNewProject(String projectName, User creator) {

        Project project = new Project();
        project.setName(projectName);
        project.setCreator(creator);
        Permission permission = permissionService.createUserPermission(project, creator, Permission.Type.Owner);
        project.addPermission(permission);
        project.addPermission(permissionService.createServicePermission(project));
        try {
            project = projectDAO.persist(project);
            logger.info("persisted new project with id: " + project.getId());
            notifier.dispatchAsync(new ProjectCreatedNotification(project));
            return project;
        } catch (Exception e) {
            throw new InternalException("project already exists");
        }
    }

    void removeProject(String projectId, Agent agent) {

        permissionService.findOwnerPermission(agent, projectId);
        Project project = projectDAO.find(projectId);

        logger.info("Attempting to delete project with id " + project.getId());
        projectDAO.removeProject(project);
        repository.removeFiles(project.getFiles());

        logger.info("Project removed. Sending notifications for deleted vizes, files and project");
        project.getVisualizations().forEach(viz ->
                notifier.dispatchAsync(new VisualizationService.VisualizationDeletedNotification(viz.getId())));
        project.getFiles().forEach(file ->
                notifier.dispatchAsync(new FileDeletedNotification(file)));
        notifier.dispatchAsync(new ProjectDeletedNotification(project));

    }

    Project findFlat(String projectId, User creator) {

        permissionService.findReadPermission(creator, projectId);
        return projectDAO.findFlat(projectId);
    }

    public Project find(String projectId, Agent creator) {

        permissionService.findReadPermission(creator, projectId);
        return projectDAO.find(projectId);
    }

    public Project findWithFullChildGraph(String projectId, Agent agent) {
        permissionService.findReadPermission(agent, projectId);
        return projectDAO.findWithFullGraph(projectId);
    }

    List<Project> findAllForUserFlat(Agent user) {
        return projectDAO.findAllForUserFlat(user);
    }


    public Project addFilesToProject(List<FileUpload> uploads, String projectId, Agent agent) {

        permissionService.findWritePermission(agent, projectId);

        Project project = projectDAO.findWithFullGraph(projectId);
        List<FileEntry> entries = repository.addFiles(uploads);
        project.addFileEntries(entries);

        try {
            project = projectDAO.persist(project);
            project.getFiles().forEach(file -> notifier.dispatchAsync(new FileCreatedNotification(file)));
            return project;
        } catch (Exception e) {
            repository.removeFiles(entries);
            throw new InternalException("Error while persisting project");
        }
    }

    public FileDownload getFileDownload(String projectId, String fileId, Agent agent) {

        permissionService.findReadPermission(agent, fileId);

        FileEntry entry = projectDAO.findFileEntry(projectId, fileId);
        return new FileDownload(repository.getFileStream(entry), entry);
    }

    public Project removeFileFromProject(String projectId, String fileId, Agent creator) {

        permissionService.findDeletePermission(creator, fileId);

        Project project = find(projectId, creator);
        Optional<FileEntry> optional = project.getFiles().stream().filter(e -> e.getId().equals(fileId)).findFirst();
        if (!optional.isPresent()) {
            throw new InternalException("fileId not present");
        }

        project.removeFileEntry(optional.get());
        Project result = projectDAO.persist(project); // remove entry from the database first to ensure consistent database
        try {
            repository.removeFile(optional.get());
        } catch (Exception ignored) {
        }
        notifier.dispatchAsync(new FileDeletedNotification(optional.get()));
        return result;
    }

    Project addPermission(String projectId, User permissionUser, Permission.Type type, Agent subject) {

        Permission ownerPermission = permissionService.findOwnerPermission(subject, projectId);

        try {
            Project project = (Project) ownerPermission.getResource();
            return projectDAO.addPermission(project, permissionService.createUserPermission(
                    project, permissionUser, type
            ));
        } catch (ClassCastException e) {
            throw new InvalidInputException("id was not a project id");
        }
    }

    Project removePermission(String projectId, Agent permissionAgent, Agent subject) {

        Permission ownerPermission = permissionService.findOwnerPermission(subject, projectId);

        try {
            Project project = (Project) ownerPermission.getResource();
            return projectDAO.removePermission(project, permissionAgent);
        } catch (ClassCastException e) {
            throw new InvalidInputException("id was not a project id");
        }
    }

    private void createNotificationTypes() {
        List<NotificationType> types = new ArrayList<>();
        types.add(ProjectCreatedNotification.getNotificationType());
        types.add(ProjectDeletedNotification.getNotificationType());
        types.add(FileCreatedNotification.getNotificationType());
        types.add(FileDeletedNotification.getNotificationType());
        this.notifier.createNotificationTypes(types);
    }

    private static class ProjectCreatedNotification extends AbstractNotification {

        ProjectCreatedNotification(Project project) {
            super(getNotificationType().getName(), project.getId());
        }

        static NotificationType getNotificationType() {
            return new NotificationType("project_created");
        }
    }

    private static class ProjectDeletedNotification extends AbstractNotification {

        ProjectDeletedNotification(Project project) {
            super(getNotificationType().getName(), project.getId());
        }

        static NotificationType getNotificationType() {
            return new NotificationType("project_deleted");
        }
    }

    private static class FileCreatedNotification extends AbstractNotification {

        FileCreatedNotification(FileEntry file) {
            super(getNotificationType().getName(), file.getId());
        }

        static NotificationType getNotificationType() {
            return new NotificationType("file_created");
        }
    }

    private static class FileDeletedNotification extends AbstractNotification {

        FileDeletedNotification(FileEntry file) {
            super(getNotificationType().getName(), file.getId());
        }

        static NotificationType getNotificationType() {
            return new NotificationType("file_deleted");
        }
    }
}
