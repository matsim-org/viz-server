package authorization;

import lombok.Getter;
import requests.ErrorCode;
import requests.RequestException;
import spark.QueryParamsMap;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

@Getter
public class AuthenticationRequest {

    public static final String RESPONSE_TYPE = "response_type";
    public static final String REDIRECT_URI = "redirect_uri";
    public static final String SCOPE = "scope";
    public static final String CLIENT_ID = "client_id";
    public static final String STATE = "state";
    public static final String NONCE = "nonce";

    AuthenticationRequest(QueryParamsMap params) throws RequestException, URIException {

        initializeRequiredParameters(params);
        initializeOptionalParameters(params);
    }

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

    private void initializeRequiredParameters(QueryParamsMap params) throws RequestException, URIException {

        this.redirectUri = initRedirectURI(params);
        this.scopes = initScope(params);
        this.responseType = initResponseType(params);
        clientId = extractRequiredValue(CLIENT_ID, params);

        if (!this.type.equals(Type.AuthCode))
            nonce = extractRequiredValue(NONCE, params);

    }

    private void initializeOptionalParameters(QueryParamsMap params) {
        state = extractOptionalValue(STATE, params);
    }

    private String[] initScope(QueryParamsMap params) throws RequestException {
        String[] scopes = extractRequiredValue(SCOPE, params).split(" ");
        boolean openid = Arrays.stream(scopes).anyMatch(scope -> scope.equals("openid"));

        if (!openid) throw new RequestException(ErrorCode.INVALID_REQUEST, "scope must contain 'openid'");
        return scopes;
    }

    private URI initRedirectURI(QueryParamsMap params) throws URIException {
        try {
            if (params.hasKey(REDIRECT_URI)) {
                return new URI(params.get(REDIRECT_URI).value());
            }
        } catch (URISyntaxException ignored) {
        }
        throw new URIException();
    }

    private String[] initResponseType(QueryParamsMap params) throws RequestException {
        String[] responseTypes = extractRequiredValue(RESPONSE_TYPE, params).split(" ");

        if (responseTypes.length == 1 && responseTypes[0].equals("code")) {
            this.type = Type.AuthCode;
        }
        else if (responseTypes.length == 1 && responseTypes[0].equals("id_token"))
            this.type = Type.IdToken;
        else if (responseTypes.length == 2 &&
                responseTypes[0].equals("id_token") &&
                responseTypes[1].equals("token"))
            this.type = Type.AccessAndIdToken;
        else
            throw new RequestException(ErrorCode.INVALID_REQUEST, "response types may be: 'code', 'id_token', 'id_token token'.");

        return responseTypes;
    }

    private String extractOptionalValue(String key, QueryParamsMap params) {
        if (params.hasKey(key)) {
            return params.get(key).value();
        }
        return "";
    }

    private String extractRequiredValue(String key, QueryParamsMap params) throws RequestException {
        if (!params.hasKey(key))
            throw new RequestException(ErrorCode.INVALID_REQUEST, key + " missing");
        return params.get(key).value();
    }

    public enum Type {AuthCode, AccessAndIdToken, IdToken}


}
