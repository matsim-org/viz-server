package org.matsim.webvis.files.project;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FilenameUtils;
import org.matsim.webvis.common.errorHandling.CodedException;
import org.matsim.webvis.common.errorHandling.Error;
import org.matsim.webvis.files.config.Configuration;
import org.matsim.webvis.files.entities.FileEntry;
import org.matsim.webvis.files.entities.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class DiskProjectRepository implements ProjectRepository {

    private static Logger logger = LoggerFactory.getLogger(DiskProjectRepository.class);

    private Project project;
    private Path projectDirectory;

    DiskProjectRepository(Project project) {

        this.project = project;
        this.projectDirectory = getProjectDirectory();
    }

    public List<FileEntry> addFiles(Collection<FileItem> items) {

        List<FileEntry> writtenFiles = new ArrayList<>();
        for (FileItem item : items) {
            FileEntry entry = addFile(item);
            writtenFiles.add(entry);
        }
        return writtenFiles;
    }

    public FileEntry addFile(FileItem item) {

        String diskFileName = UUID.randomUUID().toString() + "." + FilenameUtils.getExtension(item.getName());
        Path file = projectDirectory.resolve(diskFileName);
        try {
            item.write(file.toFile());
        } catch (Exception e) {
            logger.error("Error while writing file.", e);
            throw new CodedException(Error.UNSPECIFIED_ERROR, "Error while writing file");
        }

        FileEntry entry = new FileEntry();
        entry.setUserFileName(item.getName());
        entry.setPersistedFileName(diskFileName);
        entry.setContentType(item.getContentType());
        entry.setSizeInBytes(item.getSize());
        return entry;
    }

    public InputStream getFileStream(FileEntry entry) {
        try {
            Path filePath = getProjectDirectory().resolve(entry.getPersistedFileName());
            return Files.newInputStream(filePath);
        } catch (IOException | InvalidPathException e) {
            logger.error("could not get file stream.", e);
            throw new CodedException(Error.UNSPECIFIED_ERROR, "could not get file stream");
        }
    }

    public void removeFile(FileEntry entry) {
        removeFile(entry, getProjectDirectory());
    }

    public void removeFiles(Collection<FileEntry> entries) {

        Path directory = getProjectDirectory();
        for (FileEntry entry : entries) {
            removeFile(entry, directory);
        }
    }

    void removeAllFiles() {

        removeFiles(new ArrayList<>(project.getFiles()));
    }

    private void removeFile(FileEntry entry, Path directory) {
        try {
            Path file = directory.resolve(entry.getPersistedFileName());
            Files.delete(file);
        } catch (IOException | InvalidPathException e) {
            logger.error("Error while removing file", e);
            throw new CodedException(Error.UNSPECIFIED_ERROR, "could not remove file.");
        }
    }

    Path getProjectDirectory() {
        try {
            Path directory = Paths.get(Configuration.getInstance().getUploadedFilePath(), project.getCreator().getId(), project.getId());
            return Files.createDirectories(directory);
        } catch (IOException e) {
            logger.error("Error while creating project directory.", e);
            throw new CodedException(Error.UNSPECIFIED_ERROR, "Could not open directory");
        }
    }
}
