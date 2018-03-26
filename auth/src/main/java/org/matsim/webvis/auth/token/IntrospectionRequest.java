package org.matsim.webvis.auth.token;

import lombok.Getter;
import org.matsim.webvis.common.communication.ErrorCode;
import org.matsim.webvis.common.communication.RequestException;
import spark.Request;

import java.util.Base64;

@Getter
class IntrospectionRequest {

    private String rpId;
    private String rpSecret;
    private String token;

    IntrospectionRequest(Request request) throws RequestException {

        parseCredentials(request.headers("Authorization"));
        parseParameters(request.queryParams("token"));
    }

    private void parseCredentials(String authorization) throws RequestException {
        if (authorization == null) {
            throw new RequestException(ErrorCode.INVALID_REQUEST, "request must contain 'Authorization' header");
        }
        String[] args = authorization.split(" ");


        boolean isValidRequest = false;

        if (args.length == 2 && args[0].equals("Basic")) {
            String[] credentials = new String(Base64.getDecoder().decode(args[1])).split(":");
            if (credentials.length == 2) {
                this.rpId = credentials[0];
                this.rpSecret = credentials[1];
                isValidRequest = true;
            }
        }
        if (!isValidRequest) {
            throw new RequestException(ErrorCode.INVALID_REQUEST, "Invalid Basic Authentication request");
        }
    }

    private void parseParameters(String token) throws RequestException {
        if (token == null || token.isEmpty()) {
            throw new RequestException(ErrorCode.INVALID_REQUEST, "'org.matsim.webvis.auth.token' is a required parameter");
        }
        this.token = token;
    }
}
