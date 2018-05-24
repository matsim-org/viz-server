package org.matsim.webvis.files.project;

import org.apache.commons.fileupload.FileItem;
import org.matsim.webvis.files.entities.FileEntry;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;

public interface ProjectRepository {

    List<FileEntry> addFiles(Collection<FileItem> items);

    FileEntry addFile(FileItem item);

    InputStream getFileStream(FileEntry fileEntry);

    void removeFiles(Collection<FileEntry> entries);

    void removeFile(FileEntry entry);
}
