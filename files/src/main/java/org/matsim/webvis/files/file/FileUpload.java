package org.matsim.webvis.files.file;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.InputStream;

@Getter
@AllArgsConstructor
public class FileUpload {

    private String fileName;
    private String contentType;
    private long sizeInBytes;
    private InputStream file;
}
