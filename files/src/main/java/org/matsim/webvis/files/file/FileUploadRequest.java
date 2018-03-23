package org.matsim.webvis.files.file;

import lombok.Getter;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.webvis.common.communication.ErrorCode;
import org.matsim.webvis.common.communication.RequestException;
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

    private String project_id;
    private String user_id;
    private List<FileItem> files = new ArrayList<>();
    private List<FileItemStream> uploads = new ArrayList<>();

    FileUploadRequest(Request request) throws RequestException {

        if (!ServletFileUpload.isMultipartContent(request.raw())) {
            throw new RequestException(ErrorCode.INVALID_REQUEST, "must be a multipart request");
        }
        ServletFileUpload upload = createUpload();
        try {
            List<FileItem> fileItems = upload.parseRequest(request.raw());
            parseMetadata(fileItems);
            parseFiles(fileItems);
        } catch (FileUploadException e) {
            throw new RequestException(ErrorCode.INVALID_REQUEST, "An error occurred during file upload.");
        }
    }

    private ServletFileUpload createUpload() {
        DiskFileItemFactory factory = new DiskFileItemFactory();
        Path repository = Paths.get(Configuration.getInstance().getTmpFilePath());
        factory.setRepository(repository.toFile());
        return new ServletFileUpload(factory);
    }

    private void parseMetadata(List<FileItem> items) throws RequestException {

        if (items.size() < 2) {
            throw new RequestException(ErrorCode.INVALID_REQUEST, "file upload must contain: 'user_id', 'project_id'");
        }
        user_id = findFormField(items, "user_id").getString();
        project_id = findFormField(items, "project_id").getString();
    }

    private FileItem findFormField(List<FileItem> items, String fieldName) throws RequestException {
        Optional<FileItem> optional = items.stream()
                .filter(item -> item.isFormField() && item.getFieldName().equals(fieldName))
                .findFirst();

        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new RequestException(ErrorCode.INVALID_REQUEST, fieldName + " is missing");
        }
    }

    private void parseFiles(List<FileItem> items) throws RequestException {
        files = items.stream().filter(item -> !item.isFormField()).collect(Collectors.toList());
        if (files.size() < 1) {
            throw new RequestException(ErrorCode.INVALID_REQUEST, "file upload must contain at least one file");
        }
    }
}
