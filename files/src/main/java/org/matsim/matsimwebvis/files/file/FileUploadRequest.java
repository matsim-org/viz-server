package org.matsim.matsimwebvis.files.file;

import communication.ErrorCode;
import communication.RequestException;
import lombok.Getter;
import org.apache.commons.fileupload.FileItem;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
class FileUploadRequest {

    private String project_id;
    private String user_id;
    private List<FileItem> files;

    FileUploadRequest(List<FileItem> items) throws RequestException {

        if (items.size() >= 3) {
            parseMetadata(items);
            parseFiles(items);
        } else {
            throw new RequestException(ErrorCode.INVALID_REQUEST, "file upload must at least contain: 'user_id', 'project_id', one file");
        }
    }

    private void parseMetadata(List<FileItem> items) throws RequestException {

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
