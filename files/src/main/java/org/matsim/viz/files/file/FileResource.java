package org.matsim.viz.files.file;

import io.dropwizard.auth.Auth;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.matsim.viz.error.InvalidInputException;
import org.matsim.viz.files.entities.Agent;
import org.matsim.viz.files.entities.FileEntry;
import org.matsim.viz.files.project.ProjectService;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.InputStream;

@AllArgsConstructor
@Path("/")
public class FileResource {

    private final ProjectService projectService;
    @Getter
    private String projectId;

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public FileEntry uploadFile(@Auth Agent agent,
                                @FormDataParam("data") FormDataBodyPart jsonPart,
                                @NotNull @FormDataParam("file") FormDataBodyPart file) {

        if (isValidFileUpload(file) && jsonPart != null) {
            // parse the metadata body part as json
            jsonPart.setMediaType(MediaType.APPLICATION_JSON_TYPE);
            UploadMetadata metadata = jsonPart.getValueAs(UploadMetadata.class);

            // read the filename, media-type and actual file from the file body part
            FileUpload upload = new FileUpload(file.getContentDisposition().getFileName(), file.getMediaType().toString(),
                    file.getValueAs(InputStream.class), metadata.getTagIds());
            return projectService.addFileToProject(upload, projectId, agent);
        } else {
            throw new InvalidInputException("upload must contain filename and media type. Also a body part with metadata must be present.");
        }
    }


    @GET
    @Path("{fileId}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadFile(@Auth Agent agent, @PathParam("fileId") String fileId) {

        FileDownload download = projectService.getFileDownload(projectId, fileId, agent);

        return Response.ok((StreamingOutput) output -> IOUtils.copy(download.getFile(), output))
                .type(download.getFileEntry().getContentType())
                .header("Content-Length", download.getFileEntry().getSizeInBytes())
                .header("Content-Disposition", "attachment; filename=" + download.getFileEntry().getUserFileName())
                .build();
    }

    @DELETE
    @Path("{fileId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteFile(@Auth Agent agent, @PathParam("fileId") String fileId) {

        projectService.removeFileFromProject(projectId, fileId, agent);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    private boolean isValidFileUpload(FormDataBodyPart bodyPart) {
        return StringUtils.isNotBlank(bodyPart.getContentDisposition().getFileName()) &&
                StringUtils.isNotBlank(bodyPart.getMediaType().toString());
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    static class UploadMetadata {

        String[] tagIds = new String[0];
    }
}
