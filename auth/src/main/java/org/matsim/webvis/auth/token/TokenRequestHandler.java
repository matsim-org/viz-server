package org.matsim.webvis.auth.token;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.webvis.auth.entities.AccessToken;
import org.matsim.webvis.common.communication.AbstractRequestHandler;
import org.matsim.webvis.common.communication.Answer;
import org.matsim.webvis.common.communication.ErrorCode;
import org.matsim.webvis.common.communication.RequestException;
import spark.Request;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TokenRequestHandler extends AbstractRequestHandler<TokenRequest> {

    private static final String TYPE_FORM_URL_ENCODED = "application/x-www-form-urlencoded";
    private static final Logger logger = LogManager.getLogger();

    TokenService tokenService = new TokenService();

    public TokenRequestHandler() throws Exception {
        super(TokenRequest.class);
    }

    @Override
    protected TokenRequest parseBody(Request request) throws RequestException {

        if (!isFormUrlEncoded(request.contentType())) {
            throw new RequestException(ErrorCode.INVALID_REQUEST, "only content type: " + TYPE_FORM_URL_ENCODED + " allowed");
        }

        Map<String, String> parameters = Arrays.stream(request.body().split("&"))
                .map(this::parseQueryParameter)
                .collect(HashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), HashMap::putAll);

        return new TokenRequest(parameters);
    }

    private AbstractMap.SimpleImmutableEntry<String, String> parseQueryParameter(String parameter) {
        String[] split = parameter.split("=");
        String key = "";
        String val = "";
        try {
            key = URLDecoder.decode(split[0], "UTF-8");
            val = split.length > 1 ? URLDecoder.decode(split[1], "UTF-8") : null;
        } catch (UnsupportedEncodingException e) {
            logger.error(e);
        }

        return new AbstractMap.SimpleImmutableEntry<>(key, val);
    }

    @Override
    protected Answer process(TokenRequest body) {

        if (isGrantTypePassword(body.parameters)) {
            return processPasswordGrant(body.parameters);
        }
        return Answer.unsupportedGrantType("api only supports grant_type password.");
    }

    private Answer processPasswordGrant(Map<String, String> parameters) {

        Answer result;
        if (hasPasswordGrantParameters(parameters)) {
            try {
                AccessToken token =
                        tokenService.grantWithPassword(parameters.get(OAuthParameters.USERNAME),
                                parameters.get(OAuthParameters.PASSWORD).toCharArray());
                result = Answer.ok(new AccessTokenResponse(token));
            } catch (Exception e) {
                result = Answer.forbidden("username or password was wrong");
            }
        } else {
            result = Answer.invalidRequest("username or password parameter was not specified");
        }
        return result;
    }

    private boolean hasPasswordGrantParameters(Map<String, String> parameters) {
        return (parameters.containsKey(OAuthParameters.USERNAME) && parameters.containsKey(OAuthParameters.PASSWORD));
    }

    private boolean isGrantTypePassword(Map<String, String> parameters) {
        return (parameters.containsKey(OAuthParameters.GRANT_TYPE) &&
                parameters.get(OAuthParameters.GRANT_TYPE).equals(OAuthParameters.GRANT_TYPE_PASSWORD)
        );
    }

    private boolean isFormUrlEncoded(String contentType) {
        return contentType.equals(TYPE_FORM_URL_ENCODED);
    }
}
