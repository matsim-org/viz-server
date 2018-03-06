package authorization;

import lombok.Getter;
import requests.ErrorCode;
import requests.RequestException;
import spark.QueryParamsMap;
import spark.Request;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Map;

@Getter
public class AuthenticationRequest {

    public enum Type { AuthCode, AccessAndIdToken, AccessToken};

    private static final String RESPONSE_TYPE = "response_type";
    private static final String REDIRECT_URI = "redirect_uri";
    private static final String SCOPE = "scope";
    private static final String CLIENT_ID = "client_id";

    //required params
    private String[] responseType;
    private URI redirectUri;
    private String[] scopes;
    private String clientId;
    private AuthenticationRequest.Type type;


    //optional params
    private String state = "";
    private String nonce = "";
    private String display = "";
    private String prompt = "";
    private String maxAge = "";
    private String idTokenHint = "";
    private String loginHint = "";
    private String arcValues = "";

    public AuthenticationRequest(QueryParamsMap params) throws RequestException, URISyntaxException {

        initializeRequiredParameters(params);
        initializeOptionalParameters(params);
    }

    private void initializeRequiredParameters(QueryParamsMap params) throws RequestException, URISyntaxException {

        initScope(params);
        initResponseType(params);
        clientId = extractRequiredValue(CLIENT_ID, params);
        redirectUri = new URI(extractRequiredValue(REDIRECT_URI, params));

    }

    private void initializeOptionalParameters(QueryParamsMap params) {

    }

    private void initScope(QueryParamsMap params) throws RequestException {
        String[] scopes = extractRequiredValue(SCOPE, params).split(" ");
        boolean openid = Arrays.stream(scopes).anyMatch(scope -> scope.equals("openid"));

        if (!openid) throw new RequestException(ErrorCode.INVALID_REQUEST, "only scope must contain 'openid'");
        this.scopes = scopes;
    }

    private void initResponseType(QueryParamsMap params) throws RequestException {
        String[] responseTypes = extractRequiredValue(RESPONSE_TYPE, params).split(" ");

        if (responseTypes.length == 1 && responseTypes[0].equals("code")) {
            this.type = Type.AuthCode;
        }
        else if (responseTypes.length == 1 && responseTypes[0].equals("id_token"))
            this.type = Type.AccessToken;
        else if (responseTypes.length == 2 &&
                responseTypes[0].equals("id_token") &&
                responseTypes[1].equals("token"))
            this.type = Type.AccessAndIdToken;
        else
            throw new RequestException(ErrorCode.INVALID_REQUEST, "response types may be: 'code', 'id_token', 'id_token token'.");

        this.responseType = responseTypes;
    }

    private String extractRequiredValue(String key, QueryParamsMap params) throws RequestException {
        if (!params.hasKey(key))
            throw new RequestException(ErrorCode.INVALID_REQUEST, key + " missing");
        return params.get(key).value();
    }


}
