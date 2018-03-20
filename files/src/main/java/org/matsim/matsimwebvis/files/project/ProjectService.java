package org.matsim.matsimwebvis.files.project;

import org.apache.commons.fileupload.FileItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.matsimwebvis.files.config.Configuration;
import org.matsim.matsimwebvis.files.entities.FileEntry;
import org.matsim.matsimwebvis.files.entities.Project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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

        Path directory = createProjectDirectory(project);
        List<Path> writtenFiles = writeFilesToDisk(items, directory);
        project = addWrittenFilesToProject(writtenFiles, project);

        return project;
    }

    private boolean mayUserAddFiles(Project project, String userId) {
        return project.getCreator().getId().equals(userId);
    }

    private Path createProjectDirectory(Project project) throws IOException {
        Path directory = Paths.get(Configuration.getInstance().getFilePath(), project.getCreator().getId(), project.getName());
        return Files.createDirectories(directory);
    }

    private List<Path> writeFilesToDisk(List<FileItem> items, Path directory) {

        List<Path> writtenFiles = new ArrayList<>();
        for (FileItem item : items) {

            String filename = item.getName();
            try {
                Path file = directory.resolve(filename);
                item.write(file.toFile());
                writtenFiles.add(file);
            } catch (Exception e) {
                logger.error("error while writing file.", e);
            }
        }
        return writtenFiles;
    }

    private Project addWrittenFilesToProject(List<Path> writtenFiles, Project project) throws IOException {

        for (Path file : writtenFiles) {
            FileEntry entry = new FileEntry();
            entry.setFileName(file.getFileName().toString());
            entry.setProject(project);
            project.getFiles().add(entry);
        }

        try {
            return projectDAO.persist(project);
        } catch (Exception e) {
            removeWrittenFiles(writtenFiles);
        }
        return project;
    }

    private void removeWrittenFiles(List<Path> writtenFiles) throws IOException {
        for (Path file : writtenFiles) {
            Files.delete(file);
        }
    }
}
