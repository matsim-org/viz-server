package org.matsim.webvis.error;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class CodedExceptionMapper implements ExceptionMapper<CodedException> {
    @Override
    public Response toResponse(CodedException exception) {

        return Response.status(exception.getStatus())
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(new ErrorMessage(exception.getStatus(), exception.getInternalErrorCode(), exception.getMessage()))
                .build();
    }
}
