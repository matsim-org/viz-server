package org.matsim.webvis.files.file;

import lombok.Getter;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.webvis.common.errorHandling.InvalidInputException;
import org.matsim.webvis.files.config.Configuration;
import spark.Request;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
class FileUploadRequest {

    private static Logger logger = LogManager.getLogger();

    ServletFileUpload upload;
    private String projectId;
    private List<FileItem> files = new ArrayList<>();

    FileUploadRequest(Request request) {

        if (!ServletFileUpload.isMultipartContent(request.raw())) {
            throw new InvalidInputException("must be a multipart request");
        }
        upload = createUpload();
    }

    void parseUpload(Request request) {
        try {
            List<FileItem> fileItems = upload.parseRequest(request.raw());
            parseMetadata(fileItems);
            parseFiles(fileItems);
        } catch (FileUploadException e) {
            throw new InvalidInputException("An error occurred during file upload.");
        }
    }

    public void removeTemporaryFiles() {
        for (FileItem file : files) {
            file.delete();
        }
    }

    private ServletFileUpload createUpload() {
        DiskFileItemFactory factory = new DiskFileItemFactory();
        Path repository = Paths.get(Configuration.getInstance().getTmpFilePath());
        factory.setRepository(repository.toFile());
        return new ServletFileUpload(factory);
    }

    private void parseMetadata(List<FileItem> items) throws InvalidInputException {

        if (items.size() < 2) {
            throw new InvalidInputException("file upload must contain: 'projectId'and one file");
        }
        projectId = findFormField(items, "projectId").getString();
    }

    private FileItem findFormField(List<FileItem> items, String fieldName) {
        Optional<FileItem> optional = items.stream()
                .filter(item -> item.isFormField() && item.getFieldName().equals(fieldName))
                .findFirst();

        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new InvalidInputException(fieldName + " is missing");
        }
    }

    private void parseFiles(List<FileItem> items) {
        files = items.stream().filter(item -> !item.isFormField()).collect(Collectors.toList());
        if (files.size() < 1) {
            throw new InvalidInputException("file upload must contain at least one file");
        }
    }
}
