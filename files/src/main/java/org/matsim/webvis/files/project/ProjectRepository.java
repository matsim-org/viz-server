package org.matsim.webvis.files.project;

import org.apache.commons.fileupload.FileItem;
import org.matsim.webvis.files.entities.FileEntry;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

public interface ProjectRepository {

    List<FileEntry> addFiles(Collection<FileItem> items) throws Exception;

    FileEntry addFile(FileItem item) throws Exception;

    InputStream getFileStream(FileEntry fileEntry) throws IOException;

    void removeFiles(Collection<FileEntry> entries) throws IOException;

    void removeFile(FileEntry entry) throws IOException;
}
