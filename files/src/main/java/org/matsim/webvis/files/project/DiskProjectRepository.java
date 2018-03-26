package org.matsim.webvis.files.project;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FilenameUtils;
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
import java.util.UUID;

public class DiskProjectRepository implements ProjectRepository {

    private static Logger logger = LogManager.getLogger();

    private Project project;
    private Path projectDirectory;

    DiskProjectRepository(Project project) throws IOException {

        this.project = project;
        this.projectDirectory = getProjectDirectory();
    }

    public List<FileEntry> addFiles(Collection<FileItem> items) throws Exception {

        List<FileEntry> writtenFiles = new ArrayList<>();
        for (FileItem item : items) {
            FileEntry entry = addFile(item);
            writtenFiles.add(entry);
        }
        return writtenFiles;
    }

    private FileEntry addFile(FileItem item) throws Exception {

        String diskFileName = UUID.randomUUID().toString() + "." + FilenameUtils.getExtension(item.getName());
        Path file = projectDirectory.resolve(diskFileName);
        item.write(file.toFile());

        FileEntry entry = new FileEntry();
        entry.setUserFileName(item.getName());
        entry.setPersistedFileName(diskFileName);
        entry.setContentType(item.getContentType());
        entry.setSizeInBytes(item.getSize());
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
        Path file = directory.resolve(entry.getPersistedFileName());
        Files.delete(file);
    }

    private Path getProjectDirectory() throws IOException {
        Path directory = Paths.get(Configuration.getInstance().getUploadedFilePath(), project.getCreator().getId(), project.getId());
        return Files.createDirectories(directory);
    }
}
