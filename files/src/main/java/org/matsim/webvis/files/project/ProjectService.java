package org.matsim.webvis.files.project;

import org.apache.commons.fileupload.FileItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.webvis.common.service.CodedException;
import org.matsim.webvis.common.service.Error;
import org.matsim.webvis.files.entities.FileEntry;
import org.matsim.webvis.files.entities.Project;
import org.matsim.webvis.files.entities.User;
import org.matsim.webvis.files.entities.Visualization;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

public class ProjectService {

    private static Logger logger = LogManager.getLogger();

    ProjectDAO projectDAO = new ProjectDAO();
    RepositoryFactory repositoryFactory = new RepositoryFactory();

    Project createNewProject(String projectName, String userId) throws Exception {

        Project project = new Project();
        project.setName(projectName);
        return projectDAO.persistNewProject(project, userId);

    }

    public Project findProjectIfAllowed(String projectId, String userId) throws CodedException {
        Project project = projectDAO.find(projectId);

        if (project == null) {
            throw new CodedException(Error.RESOURCE_NOT_FOUND, "Project was not found.");
        }
        if (!mayUserAddFiles(project, userId)) {
            throw new CodedException(Error.FORBIDDEN, "User is not allowed to add files to this project");
        }
        return project;
    }

    List<Project> findProjectsForUser(List<String> projectIds, User user) {
        return projectDAO.findForUser(projectIds, user);
    }

    List<Project> findAllProjectsForUser(User user) {
        return projectDAO.findAllForUser(user);
    }

    public List<Project> getAllProjectsForUser(User user) {

        return projectDAO.findAllForUser(user);
    }

    private static boolean isCreator(Project project, User user) {
        return project.getCreator().getId().equals(user.getId());
    }

    public Project addFilesToProject(List<FileItem> items, Project project) throws Exception {

        ProjectRepository repository = repositoryFactory.getRepository(project);
        List<FileEntry> entries = repository.addFiles(items);
        project.addFileEntries(entries);

        try {
            return projectDAO.persist(project);
        } catch (Exception e) {
            repository.removeFiles(entries);
            throw new Exception("Error while persisting project", e);
        }
    }

    public InputStream getFileStream(Project project, FileEntry file) throws Exception {
        ProjectRepository repository = repositoryFactory.getRepository(project);
        return repository.getFileStream(file);
    }

    public Project removeFileFromProject(String projectId, String fileId, User subject) throws CodedException {

        Project project = findProjectIfAllowed(projectId, subject.getId());
        Optional<FileEntry> optional = project.getFiles().stream().filter(e -> e.getId().equals(fileId)).findFirst();
        if (!optional.isPresent()) {
            throw new CodedException(Error.RESOURCE_NOT_FOUND, "fileId not present");
        }

        try {
            ProjectRepository repository = this.repositoryFactory.getRepository(project);
            repository.removeFile(optional.get());
        } catch (IOException e) {
            throw new CodedException(Error.UNSPECIFIED_ERROR, "Could not remove file.");
        }
        project.removeFileEntry(optional.get());
        return projectDAO.persist(project);
    }

    Project findWithRelations(String projectId, User subject) throws CodedException {
        Project project = projectDAO.find(projectId);

        if (project == null) throw new CodedException(Error.RESOURCE_NOT_FOUND, "project not found");
        if (!isCreator(project, subject)) throw new CodedException(Error.FORBIDDEN, "User is not allowed");

        return project;
    }

    public void removeProject(Project project) throws IOException {

        //delete all associated files
        DiskProjectRepository repository = new DiskProjectRepository(project);
        repository.removeAllFiles();

        //delete project from db
        projectDAO.remove(project);
    }

    private boolean mayUserAddFiles(Project project, String userId) {
        return project.getCreator().getId().equals(userId);
    }

    public Project addVisualization(String projectId, Visualization viz, User subject) throws CodedException {

        Project project = findWithRelations(projectId, subject);

        project.addVisualization(viz);
        return projectDAO.persist(project);
    }
}
