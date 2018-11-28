package org.matsim.viz.files.project;

import org.matsim.viz.error.CodedException;
import org.matsim.viz.error.Error;
import org.matsim.viz.error.InternalException;
import org.matsim.viz.error.InvalidInputException;
import org.matsim.viz.files.entities.*;
import org.matsim.viz.files.file.FileDownload;
import org.matsim.viz.files.file.FileUpload;
import org.matsim.viz.files.file.Repository;
import org.matsim.viz.files.notifications.AbstractNotification;
import org.matsim.viz.files.notifications.NotificationType;
import org.matsim.viz.files.notifications.Notifier;
import org.matsim.viz.files.permission.PermissionService;
import org.matsim.viz.files.visualization.VisualizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.RollbackException;
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

    public Project patchProject(String projectId, String projectName, Agent agent) {

        Permission permission = permissionService.findWritePermission(agent, projectId);
        Project project = (Project) permission.getResource();
        project.setName(projectName);
        try {
            return projectDAO.persist(project);
        } catch (Exception e) {
            throw new CodedException(409, Error.RESOURCE_EXISTS, "A project with this name already exsists");
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


    public FileEntry addFileToProject(FileUpload upload, String projectId, Agent agent) {

        permissionService.findWritePermission(agent, projectId);

        Project project = projectDAO.findWithFullGraph(projectId);
        FileEntry fileEntry = repository.addFile(upload);
        project.addFileEntry(fileEntry);

        try {
            project = projectDAO.persist(project);
            FileEntry persistedFileEntry = project.getFiles().stream()
                    .filter(entry -> entry.getPersistedFileName().equals(fileEntry.getPersistedFileName())).findFirst().get();
            notifier.dispatchAsync(new FileCreatedNotification(persistedFileEntry));
            return persistedFileEntry;
        } catch (Exception e) {
            repository.removeFile(fileEntry);
            throw new InternalException("Error while adding files to project.");
        }
    }

    public FileDownload getFileDownload(String projectId, String fileId, Agent agent) {

        permissionService.findReadPermission(agent, fileId);

        FileEntry entry = projectDAO.findFileEntry(projectId, fileId);
        return new FileDownload(repository.getFileStream(entry), entry);
    }

    public void removeFileFromProject(String projectId, String fileId, Agent creator) {

        permissionService.findDeletePermission(creator, fileId);

        Project project = find(projectId, creator);
        Optional<FileEntry> optional = project.getFiles().stream().filter(e -> e.getId().equals(fileId)).findFirst();
        if (!optional.isPresent()) {
            throw new InternalException("fileId not present");
        }

        project.removeFileEntry(optional.get());

        try {
            projectDAO.persist(project); // remove entry from the database first to ensure consistent database
            repository.removeFile(optional.get());
            notifier.dispatchAsync(new FileDeletedNotification(optional.get()));
        } catch (RollbackException e) {
            throw new InternalException("could not remove file. Make sure it is not used by any visualization.");
        } catch (Exception ignored) {
        }
    }

    Permission addPermission(String projectId, Agent permissionUser, Permission.Type type, Agent subject) {

        Permission ownerPermission = permissionService.findOwnerPermission(subject, projectId);

        try {
            Project project = (Project) ownerPermission.getResource();
            project = projectDAO.addPermission(project, permissionService.createUserPermission(
                    project, permissionUser, type
            ));
            return project.getPermissions().stream()
                    .filter(permission -> permission.getAgent().equals(permissionUser))
                    .findFirst().get();
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

    Tag addTag(String projectId, String tagName, String tagType, Agent subject) {

        permissionService.findWritePermission(subject, projectId);

        Project project = projectDAO.findWithFullGraph(projectId);
        Tag tag = new Tag();
        tag.setName(tagName);
        tag.setType(tagType);
        project.addTag(tag);

        try {
            project = projectDAO.persist(project);
            return project.getTags().stream().filter(t -> t.getName().equals(tag.getName()) && t.getType().equals(tag.getType())).findFirst().get();
        } catch (Exception e) {
            logger.error("Could not persist tag with name: " + tagName, e);
            throw new CodedException(409, Error.RESOURCE_EXISTS, "tag with name: " + tagName + " already exists");
        }
    }

    Project removeTag(String projectId, String tagId, Agent subject) {

        permissionService.findDeletePermission(subject, projectId);

        Project project = projectDAO.findWithFullGraph(projectId);
        if (project.getTags().removeIf(tag -> tag.getId().equals(tagId))) {
            return projectDAO.persist(project);
        } else {
            throw new InvalidInputException("could not find tag with id: " + tagId);
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
