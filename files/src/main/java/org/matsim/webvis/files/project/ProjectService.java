package org.matsim.webvis.files.project;

import org.apache.commons.fileupload.FileItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.webvis.common.service.CodedException;
import org.matsim.webvis.common.service.Error;
import org.matsim.webvis.files.entities.*;
import org.matsim.webvis.files.permission.PermissionService;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

public class ProjectService {

    private static Logger logger = LogManager.getLogger();

    ProjectDAO projectDAO = new ProjectDAO();
    RepositoryFactory repositoryFactory = new RepositoryFactory();
    PermissionService permissionService = new PermissionService();

    public Project createNewProject(String projectName, User creator) {

        Project project = new Project();
        project.setName(projectName);
        project.setCreator(creator);
        Permission permission = PermissionService.createUserPermission(project, creator, Permission.Type.Delete);
        project.addPermission(permission);
        try {
            return projectDAO.persist(project);
        } catch (Exception e) {
            throw new CodedException(Error.RESOURCE_EXISTS, "project already exists");
        }
    }

    Project findFlat(String projectId, User creator) {

        permissionService.findReadPermission(creator, projectId);
        return projectDAO.findFlat(projectId);
    }

    public Project find(String projectId, User creator) {

        permissionService.findReadPermission(creator, projectId);
        return projectDAO.find(projectId);
    }

    List<Project> findAllForUserFlat(User user) {
        return projectDAO.findAllForUserFlat(user);
    }


    public Project addFilesToProject(List<FileItem> items, Project project, Agent agent) {

        permissionService.findWritePermission(agent, project.getId());

        ProjectRepository repository = repositoryFactory.getRepository(project);
        List<FileEntry> entries = repository.addFiles(items);
        project.addFileEntries(entries);

        try {
            return projectDAO.persist(project);
        } catch (Exception e) {
            repository.removeFiles(entries);
            throw new CodedException(Error.UNSPECIFIED_ERROR, "Error while persisting project");
        }
    }

    public InputStream getFileStream(Project project, FileEntry file, Agent agent) {

        permissionService.findReadPermission(agent, file.getId());

        ProjectRepository repository = repositoryFactory.getRepository(project);
        return repository.getFileStream(file);
    }

    public Project removeFileFromProject(String projectId, String fileId, User creator) {

        permissionService.findDeletePermission(creator, fileId);

        Project project = find(projectId, creator);
        Optional<FileEntry> optional = project.getFiles().stream().filter(e -> e.getId().equals(fileId)).findFirst();
        if (!optional.isPresent()) {
            throw new CodedException(Error.RESOURCE_NOT_FOUND, "fileId not present");
        }

        ProjectRepository repository = this.repositoryFactory.getRepository(project);
        repository.removeFile(optional.get());
        project.removeFileEntry(optional.get());
        return projectDAO.persist(project);
    }
}
