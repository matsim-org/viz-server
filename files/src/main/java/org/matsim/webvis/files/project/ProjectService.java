package org.matsim.webvis.files.project;

import org.apache.commons.fileupload.FileItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.webvis.files.entities.FileEntry;
import org.matsim.webvis.files.entities.Project;
import org.matsim.webvis.files.entities.User;

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

    public Project findProjectIfAllowed(String projectId, String userId) throws Exception {
        Project project = projectDAO.find(projectId);

        if (project == null) {
            throw new Exception("Project was not found.");
        }
        if (!mayUserAddFiles(project, userId)) {
            throw new Exception("User is not allowed to add files to this project");
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

    public Project removeFileFromProject(String projectId, String fileId, User subject) throws Exception {

        Project project = findProjectIfAllowed(projectId, subject.getId());
        Optional<FileEntry> optional = project.getFiles().stream().filter(e -> e.getId().equals(fileId)).findFirst();
        if (!optional.isPresent()) {
            throw new Exception("file id not present");
        }

        ProjectRepository repository = this.repositoryFactory.getRepository(project);
        repository.removeFile(optional.get());
        projectDAO.removeFileEntry(optional.get());
        project.getFiles().remove(optional.get());
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
}
