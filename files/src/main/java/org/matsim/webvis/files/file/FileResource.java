package org.matsim.webvis.files.file;

import io.dropwizard.auth.Auth;
import lombok.Getter;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.matsim.webvis.error.InvalidInputException;
import org.matsim.webvis.files.entities.Agent;
import org.matsim.webvis.files.entities.Project;
import org.matsim.webvis.files.project.ProjectService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Path("/")
public class FileResource {

    ProjectService projectService = ProjectService.Instance;
    @Getter
    private String projectId;

    public FileResource(String projectId) {
        this.projectId = projectId;
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Project uploadFiles(@Auth Agent agent, final FormDataMultiPart multiPart) {

        List<FileUpload> uploads = new ArrayList<>();
        for (List<FormDataBodyPart> bodyParts : multiPart.getFields().values()) {
            for (FormDataBodyPart bodyPart : bodyParts) {
                if (isValidFileUpload(bodyPart))
                    uploads.add(new FileUpload(
                            bodyPart.getContentDisposition().getFileName(),
                            bodyPart.getMediaType().toString(),
                            bodyPart.getValueAs(InputStream.class)));
            }
        }
        if (uploads.size() == 0)
            throw new InvalidInputException("No files to upload");
        return projectService.addFilesToProject(uploads, projectId, agent);
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
    public Project deleteFile(@Auth Agent agent, @PathParam("fileId") String fileId) {

        return projectService.removeFileFromProject(projectId, fileId, agent);
    }

    private boolean isValidFileUpload(FormDataBodyPart bodyPart) {
        return !bodyPart.isSimple() &&
                StringUtils.isNotBlank(bodyPart.getContentDisposition().getFileName());
        // a test for valid content types could make sense once we've settled for allowed content
    }
}
