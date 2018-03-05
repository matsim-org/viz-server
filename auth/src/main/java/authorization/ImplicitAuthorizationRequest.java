package authorization;

import lombok.Data;
import requests.RequestException;
import spark.Request;

import java.net.URI;
import java.net.URISyntaxException;

@Data
public class ImplicitAuthorizationRequest {

    private String responseType;
    private String clientId;
    private URI redirectURI;
    private String scope;
    private String state;

    public static ImplicitAuthorizationRequest create(Request request) throws Exception {

        if (isParamMissing(request)) {
            throw new RequestException("invalid_request", "a required parameter is missing");
        }

        if (!isTypeToken(request)) {
            throw new Exception("Implicit authorization request mus have response_type: token but was: " + request.queryParams("response_type"));
        }

        ImplicitAuthorizationRequest req = new ImplicitAuthorizationRequest();

        req.setRedirectURI(parseRedirectURI(request));
        req.setResponseType(request.queryParams("response_type"));
        req.setClientId(request.queryParams("client_id"));

        if (request.queryParams().contains("scope"))
            req.setScope(request.queryParams("scope"));
        if (request.queryParams().contains("state"))
            req.setState(request.queryParams("state"));

        return req;
    }

    private static boolean isParamMissing(Request request) {
        return (!request.queryParams().contains("response_type") ||
                !request.queryParams().contains("client_id") ||
                !request.queryParams().contains("redirect_uri"));
    }

    private static boolean isTypeToken(Request request) {
        return (request.queryParams("response_type").equals("token"));
    }

    private static URI parseRedirectURI(Request request) throws RequestException {
        try {
            return new URI(request.queryParams("redirect_uri"));
        } catch (URISyntaxException e) {
            throw new RequestException("invalid_request", "the redirect uri could not be parsed");
        }
    }
}
