package org.matsim.webvis.files.file;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.matsim.webvis.files.entities.FileEntry;

import java.io.InputStream;

@Getter
@AllArgsConstructor
public class FileDownload {

    private InputStream file;
    private FileEntry fileEntry;
}
