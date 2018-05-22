package org.matsim.webvis.common.communication;

import org.matsim.webvis.common.service.Error;

public class Answer<T> {

    private int statusCode;
    private T response;

    private Answer(int statusCode, T response) {
        this.statusCode = statusCode;
        this.response = response;
    }

    public static <T> Answer ok(T response) {
        return new Answer<>(HttpStatus.OK, response);
    }

    public static Answer notFound() {
        return error(HttpStatus.NOT_FOUND, RequestError.NOT_FOUND, "url not found");
    }

    public static Answer invalidRequest(String message) {
        return badRequest(RequestError.INVALID_REQUEST, message);
    }

    public static Answer unsupportedGrantType(String message) {
        return Answer.badRequest(RequestError.UNSUPPORTED_GRANT_TYPE, message);
    }

    public static Answer forbidden(String message) {
        return Answer.error(HttpStatus.FORBIDDEN, Error.FORBIDDEN, message);
    }

    public static Answer notFound() {
        return Answer.error(HttpStatus.NOT_FOUND, Error.RESOURCE_NOT_FOUND, "No resource with requested URL");
    }

    public static Answer unauthorized(String errorCode, String message) {
        return Answer.error(HttpStatus.UNAUTHORIZED, errorCode, message);
    }

    public static Answer badRequest(String errorCode, String message) {
        return error(HttpStatus.BAD_REQUEST, errorCode, message);
    }

    public static Answer conflict(String errorCode, String message) {
        return error(HttpStatus.CONFLICT, errorCode, message);
    }

    public static Answer internalError(String errorCode, String message) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, errorCode, message);
    }

    private static Answer error(int code, String errorCode, String message) {
        return new Answer<>(code, new ErrorResponse(errorCode, message));
    }

    public int getStatusCode() {
        return statusCode;
    }

    public T getResponse() {
        return response;
    }

    public boolean isOk() {
        return this.statusCode == HttpStatus.OK;
    }
}
