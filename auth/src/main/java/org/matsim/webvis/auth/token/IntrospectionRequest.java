package org.matsim.webvis.auth.token;

import lombok.Getter;
import org.matsim.webvis.common.communication.RequestError;
import org.matsim.webvis.common.communication.RequestException;
import org.matsim.webvis.common.communication.RequestWithParams;
import spark.Request;

import java.util.Base64;

@Getter
class IntrospectionRequest extends RequestWithParams {

    private String rpId;
    private String rpSecret;
    private String token;

    IntrospectionRequest(Request request) throws RequestException {

        parseCredentials(request.headers("Authorization"));
        token = extractRequiredValue(OAuthParameters.TOKEN, request.queryMap());
    }

    private void parseCredentials(String authorization) throws RequestException {
        if (authorization == null) {
            throw new RequestException(RequestError.INVALID_REQUEST, "request must contain 'Authorization' header");
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
            throw new RequestException(RequestError.INVALID_REQUEST, "Invalid Basic Authentication request");
        }
    }
}
