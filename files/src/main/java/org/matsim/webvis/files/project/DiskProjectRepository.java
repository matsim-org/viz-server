package org.matsim.webvis.files.project;

import org.apache.commons.fileupload.FileItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.webvis.files.config.Configuration;
import org.matsim.webvis.files.entities.FileEntry;
import org.matsim.webvis.files.entities.Project;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DiskProjectRepository {

    private static Logger logger = LogManager.getLogger();

    private Project project;

    DiskProjectRepository(Project project) {
        this.project = project;
    }

    public List<FileEntry> addFiles(List<FileItem> items) throws IOException {

        Path directory = getProjectDirectory();
        List<FileEntry> writtenFiles = new ArrayList<>();
        for (FileItem item : items) {
            FileEntry entry = addFile(item, directory);
            writtenFiles.add(entry);
        }
        return writtenFiles;
    }

    public FileEntry addFile(FileItem item) throws IOException {
        return addFile(item, getProjectDirectory());
    }

    private FileEntry addFile(FileItem item, Path directory) {
        String filename = item.getName();
        try {
            Path file = directory.resolve(filename);
            item.write(file.toFile());
        } catch (Exception e) {
            logger.error("error while writing file.", e);
        }
        FileEntry entry = new FileEntry();
        entry.setFileName(filename);
        return entry;
    }

    public InputStream getFileStream(FileEntry entry) {
        return null;
    }

    public void removeFile(FileEntry entry) throws IOException {
        removeFile(entry, getProjectDirectory());
    }

    public void removeFiles(Collection<FileEntry> entries) throws IOException {

        Path directory = getProjectDirectory();
        for (FileEntry entry : entries) {
            removeFile(entry, directory);
        }
    }

    public void removeAllFiles() throws IOException {

        removeFiles(new ArrayList<>(project.getFiles()));
    }

    private void removeFile(FileEntry entry, Path directory) throws IOException {
        Path file = directory.resolve(entry.getFileName());
        Files.delete(file);
    }

    private Path getProjectDirectory() throws IOException {
        Path directory = Paths.get(Configuration.getInstance().getUploadedFilePath(), project.getCreator().getId(), project.getName());
        return Files.createDirectories(directory);
    }
}
