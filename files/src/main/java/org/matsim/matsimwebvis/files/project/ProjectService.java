package org.matsim.matsimwebvis.files.project;

import org.apache.commons.fileupload.FileItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.matsimwebvis.files.entities.FileEntry;
import org.matsim.matsimwebvis.files.entities.Project;

import java.io.IOException;
import java.util.List;

public class ProjectService {

    private static Logger logger = LogManager.getLogger();

    private ProjectDAO projectDAO = new ProjectDAO();

    public Project createNewProject(String projectName, String userId) throws Exception {

        Project project = new Project();
        project.setName(projectName);
        return projectDAO.persistNewProject(project, userId);

    }

    public Project addFilesToProject(List<FileItem> items, String projectId, String userId) throws Exception {

        Project project = projectDAO.find(projectId);

        if (project == null) {
            throw new Exception("Project was not found.");
        }
        if (!mayUserAddFiles(project, userId)) {
            throw new Exception("User is not allowed to add files to this project");
        }

        DiskProjectRepository repository = new DiskProjectRepository(project);
        List<FileEntry> entries = repository.addFiles(items);
        project.addFileEntries(entries);

        try {
            return projectDAO.persist(project);
        } catch (Exception e) {
            repository.removeFiles(entries);
            return projectDAO.find(projectId);
        }
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
