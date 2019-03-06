package org.matsim.viz.files.file;

import org.apache.commons.io.FilenameUtils;
import org.matsim.viz.error.InternalException;
import org.matsim.viz.files.entities.FileEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class LocalRepository implements Repository {

    private static Logger logger = LoggerFactory.getLogger(LocalRepository.class);

    Path uploadDirectory;

    public LocalRepository(String uploadDirectory) {

        this.uploadDirectory = getUploadDirectory(uploadDirectory);
    }

    public List<FileEntry> addFiles(Collection<FileUpload> uploads) {

        return uploads.stream().map(this::addFile).collect(Collectors.toList());
    }

    public FileEntry addFile(FileUpload upload) {

        return writeFileToDisk(upload);
    }

    public InputStream getFileStream(FileEntry entry) {
        try {
            Path filePath = uploadDirectory.resolve(entry.getPersistedFileName());
            return Files.newInputStream(filePath);
        } catch (IOException | InvalidPathException e) {
            logger.error("could not get file stream.", e);
            throw new InternalException("could not get file stream");
        }
    }

    public void removeFile(FileEntry entry) {
        removeFile(entry, uploadDirectory);
    }

    public void removeFiles(Collection<FileEntry> entries) {

        for (FileEntry entry : entries) {
            removeFile(entry);
        }
    }

    FileEntry writeFileToDisk(FileUpload upload) {
        String diskFileName = UUID.randomUUID().toString() + "." + FilenameUtils.getExtension(upload.getFileName());
        Path file = uploadDirectory.resolve(diskFileName);
        FileEntry entry = new FileEntry();
        try {
            Files.createDirectories(uploadDirectory);
            long bytes = Files.copy(upload.getFile(), file);
            entry.setSizeInBytes(bytes);
        } catch (IOException e) {
            logger.error("Error while writing file", e);
            throw new InternalException("Error while writing file");
        }

        entry.setUserFileName(upload.getFileName());
        entry.setPersistedFileName(diskFileName);
        entry.setContentType(upload.getContentType());
        entry.setStorageType(FileEntry.StorageType.Local);
        entry.addTags(upload.getTagIds());
        return entry;
    }

    private void removeFile(FileEntry entry, Path directory) {
        try {
            Path file = directory.resolve(entry.getPersistedFileName());
            Files.delete(file);
        } catch (IOException | InvalidPathException e) {
            logger.error("Error while removing file", e);
            throw new InternalException("could not remove file.");
        }
    }

    private Path getUploadDirectory(String path) {
        try {
            Path directory = Paths.get(path);
            return Files.createDirectories(directory);
        } catch (IOException e) {
            logger.error("Error while creating project directory.", e);
            throw new InternalException("Could not open directory");
        }
    }
}
