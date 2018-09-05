package org.matsim.webvis.files.project;

import org.matsim.webvis.error.InternalException;
import org.matsim.webvis.files.entities.*;
import org.matsim.webvis.files.file.FileDownload;
import org.matsim.webvis.files.file.FileUpload;
import org.matsim.webvis.files.file.Repository;
import org.matsim.webvis.files.permission.PermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class ProjectService {

    private static Logger logger = LoggerFactory.getLogger(ProjectService.class);

    private final ProjectDAO projectDAO;
    private final Repository repository;
    private final PermissionService permissionService;

    public ProjectService(ProjectDAO projectDAO, PermissionService permissionService, Repository repository) {
        this.projectDAO = projectDAO;
        this.permissionService = permissionService;
        this.repository = repository;
    }

    public Project createNewProject(String projectName, User creator) {

        Project project = new Project();
        project.setName(projectName);
        project.setCreator(creator);
        Permission permission = permissionService.createUserPermission(project, creator, Permission.Type.Delete);
        project.addPermission(permission);
        project.addPermission(permissionService.createServicePermission(project));
        try {
            return projectDAO.persist(project);
        } catch (Exception e) {
            throw new InternalException("project already exists");
        }
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
            return projectDAO.persist(project);
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
        repository.removeFile(optional.get());
        return result;
    }
}
