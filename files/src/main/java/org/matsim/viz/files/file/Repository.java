package org.matsim.viz.files.file;

import org.matsim.viz.files.entities.FileEntry;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;

public interface Repository {

    List<FileEntry> addFiles(Collection<FileUpload> uploads);

    FileEntry addFile(FileUpload item);

    InputStream getFileStream(FileEntry fileEntry);

    void removeFiles(Collection<FileEntry> entries);

    void removeFile(FileEntry entry);
}
