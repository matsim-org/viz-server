package org.matsim.webvis.common.communication;

import org.matsim.webvis.common.service.*;
import org.matsim.webvis.common.service.Error;
import spark.Filter;

import java.util.function.Consumer;

import static spark.Spark.*;

public class StartSpark {

    public static void withPort(int port) {
        port(port);
    }

    public static void withTLS(String keystorePath, String keystorePassword, String truststorePath, String truststorePassword) {
        secure(keystorePath, keystorePassword, truststorePath, truststorePassword);
    }

    public static void withPermissiveAccessControl() {

        afterAfter(((request, response) -> {
            String origin = request.headers("Origin");
            response.header("Access-Control-Allow-Origin", (origin != null) ? origin : "*");
            response.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
            response.header("Access-Control-Allow-Credentials", "true");
            response.header("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS, DELETE");
        }));
        options("/*", (request, response) -> "OK");
    }

    public static void withAuthHandler(Filter authHandler) {
        before(((request, response) -> {
            if (!request.requestMethod().equals("OPTIONS"))
                authHandler.handle(request, response);
        }));
    }

    public static void withInitializationExceptionHandler(Consumer<Exception> handler) {
        initExceptionHandler(handler);
    }

    public static void withExceptionMapping() {
        notFound((request, response) -> JsonHelper.createJsonResponse(Answer.notFound(), response));

        internalServerError(((request, response) ->
                JsonHelper.createJsonResponse(Answer.internalError(Error.UNSPECIFIED_ERROR, "internal error."), response)
        ));

        exception(ForbiddenException.class, (e, request, response) ->
                JsonHelper.createJsonResponse(Answer.forbidden(e.getMessage()), response)
        );

        exception(UnauthorizedException.class, (e, request, response) ->
                JsonHelper.createJsonResponse(Answer.unauthorized(e.getErrorCode(), e.getMessage()), response)
        );

        exception(InvalidInputException.class, (e, request, response) ->
                JsonHelper.createJsonResponse(Answer.invalidRequest(e.getMessage()), response)
        );

        exception(CodedException.class, (e, request, response) ->
                JsonHelper.createJsonResponse(Answer.internalError(e.getErrorCode(), e.getMessage()), response)
        );
    }
}
