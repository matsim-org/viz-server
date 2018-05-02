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

    private static void validate(Project project, User creator) throws CodedException {
        if (isNull(project)) throw new CodedException(Error.RESOURCE_NOT_FOUND, "could not find project");
        if (!isCreator(project, creator)) throw new CodedException(Error.FORBIDDEN, "user is not creator");
    }

    private static boolean isNull(Project project) {
        return project == null;
    }

    Project createNewProject(String projectName, User creator) {

        Project project = new Project();
        project.setName(projectName);
        project.setCreator(creator);
        return projectDAO.persist(project);
    }

    Project findFlat(String projectId, User creator) throws CodedException {
        Project result = projectDAO.findFlat(projectId);
        validate(result, creator);
        return result;
    }

    public Project find(String projectId, User creator) throws CodedException {
        Project result = projectDAO.find(projectId);
        validate(result, creator);
        return result;
    }

    List<Project> find(List<String> projectIds, User user) {
        return projectDAO.find(projectIds, user);
    }

    private static boolean isCreator(Project project, User user) {
        return project.getCreator().getId().equals(user.getId());
    }

    List<Project> findAllForUserFlat(User user) {
        return projectDAO.findAllForUserFlat(user);
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

    public Project removeFileFromProject(String projectId, String fileId, User creator) throws CodedException {

        Project project = find(projectId, creator);
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

    Project addVisualization(String projectId, Visualization viz, User subject) throws CodedException {

        Project project = find(projectId, subject);
        project.addVisualization(viz);
        return projectDAO.persist(project);
    }
}
