package token;

import org.apache.logging.log4j.LogManager;
import requests.AbstractRequestHandler;
import requests.Answer;
import spark.Request;
import user.UserService;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TokenRequestHandler extends AbstractRequestHandler<TokenRequest> {

    UserService userService = new UserService();
    TokenService tokenService = new TokenService();

    public TokenRequestHandler() {
        super(TokenRequest.class);
    }

    @Override
    protected TokenRequest parseBody(Request request) {
        LogManager.getLogger().info(request.body());

        Map<String, String> parameters = Arrays.stream(request.body().split("&"))
                .map(this::parseQueryParameter)
                .collect(HashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), HashMap::putAll);

        return new TokenRequest(parameters);
    }

    private AbstractMap.SimpleImmutableEntry<String, String> parseQueryParameter(String parameter) {
        String[] split = parameter.split("=");
        final String key = split[0];
        final String val = split.length > 1 ? split[1] : null;
        return new AbstractMap.SimpleImmutableEntry<>(key, val);
    }

    @Override
    protected Answer process(TokenRequest body) {

        if (body.parameters.containsKey(OAuthParameters.GRANT_TYPE) &&
                body.parameters.get(OAuthParameters.GRANT_TYPE).equals(OAuthParameters.GRANT_TYPE_PASSWORD)
                ) {
            return processPasswordGrant(body.parameters);
        }
        return Answer.internalError("api only supports grant_type password.");
    }

    private Answer processPasswordGrant(Map<String, String> parameters) {

        Answer result = null;
        if (parameters.containsKey(OAuthParameters.USERNAME) && parameters.containsKey(OAuthParameters.PASSWORD)) {
            try {
                AccessTokenResponse response = tokenService.grantWithPassword(parameters.get(OAuthParameters.USERNAME),
                                                                              parameters.get(OAuthParameters.PASSWORD).toCharArray());

                //result = Answer.ok(getGson().toJson(user));
            } catch (Exception e) {
                result = Answer.forbidden("username or password was wrong");
            }
        } else {
            result = Answer.badRequest("username or password parameter was not specified");
        }
        return result;
    }
}
