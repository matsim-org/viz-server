package org.matsim.webvis.files.file;

import org.matsim.webvis.files.entities.FileEntry;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;

public interface ProjectRepository {

    List<FileEntry> addFiles(Collection<FileUpload> uploads);

    FileEntry addFile(FileUpload item);

    InputStream getFileStream(FileEntry fileEntry);

    void removeFiles(Collection<FileEntry> entries);

    void removeFile(FileEntry entry);
}
