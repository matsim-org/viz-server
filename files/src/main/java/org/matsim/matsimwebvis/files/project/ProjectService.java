package org.matsim.matsimwebvis.files.project;

import org.apache.commons.fileupload.FileItem;
import org.matsim.matsimwebvis.files.config.Configuration;
import org.matsim.matsimwebvis.files.entities.FileEntry;
import org.matsim.matsimwebvis.files.entities.Project;

import java.io.File;
import java.util.List;

public class ProjectService {

    private ProjectDAO projectDAO = new ProjectDAO();

    public Project createNewProject(String projectName, String userId) throws Exception {

        Project project = new Project();
        project.setName(projectName);
        return projectDAO.persistNewProject(project, userId);

    }

    public Project addFilesToProject(List<FileItem> items, String projectId, String userId) throws Exception {

        Project project = projectDAO.find(projectId);

        //TODO: check whether user can add files to project
        if (project == null) {
            throw new Exception();
        }

        //add file entries to the project
        File projectDirectory = new File(Configuration.getInstance().getFilePath() + "/" + project.getName());
        projectDirectory.mkdir();

        //set a flag to false that indicates not all files are stored where they belong

        //write the files to their designated location
        for (FileItem item : items) {
            String filename = item.getName();

            FileEntry entry = new FileEntry();
            entry.setFileName(filename);
            entry.setProject(project);
            project.getFiles().add(entry);

            File file = new File(projectDirectory.getAbsolutePath() + filename);
            item.write(file);
        }

        return projectDAO.persist(project);
    }
}
