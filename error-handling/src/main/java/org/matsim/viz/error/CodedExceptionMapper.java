package org.matsim.viz.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class CodedExceptionMapper implements ExceptionMapper<CodedException> {

    private static Logger logger = LoggerFactory.getLogger(CodedExceptionMapper.class);

    @Override
    public Response toResponse(CodedException exception) {

        logger.error("Uncaught coded exception: ", exception);
        return Response.status(exception.getStatus())
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(new ErrorMessage(exception.getStatus(), exception.getInternalErrorCode(), exception.getMessage()))
                .build();
    }
}
