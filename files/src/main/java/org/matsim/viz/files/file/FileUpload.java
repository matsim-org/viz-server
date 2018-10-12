package org.matsim.viz.files.file;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.InputStream;

@Getter
@AllArgsConstructor
public class FileUpload {

    private String fileName;
    private String contentType;
    private InputStream file;
}
