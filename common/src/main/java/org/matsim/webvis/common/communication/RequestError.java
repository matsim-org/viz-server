package org.matsim.webvis.common.communication;

public class RequestError extends Error {
    public static final String INVALID_REQUEST = "invalid_request";
    static final String NOT_FOUND = "not_found";
    public static final String INVALID_CLIENT = "invalid_client";
    public static final String INVALID_TOKEN = "invalid_token";
    public static final String UNAUTHORIZED_CLIENT = "unauthorized_client";
    public static final String UNSUPPORTED_GRANT_TYPE = "unsupported_grant_type";
    @SuppressWarnings("WeakerAccess")
    public static final String UNSUPPORTED_CONTENT_TYPE = "unsupported_content_type";
}
