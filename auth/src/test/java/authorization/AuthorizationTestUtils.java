package authorization;

import spark.QueryParamsMap;
import spark.Request;
import spark.Session;
import util.TestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthorizationTestUtils {

    public static Map<String, String[]> createDefaultParameterMap() {
        Map<String, String[]> parameterMap = new HashMap<>();
        parameterMap.put("scope", new String[]{"openid"});
        parameterMap.put("response_type", new String[]{"code"});
        parameterMap.put("client_id", new String[]{"test-client-id"});
        parameterMap.put("redirect_uri", new String[]{"http://valid.uri"});

        return parameterMap;
    }

    public static QueryParamsMap mockQueryParams(String keyToReplace, String valueToReplace) {

        Map<String, String[]> parameterMap = createDefaultParameterMap();

        if (!keyToReplace.isEmpty())
            parameterMap.put(keyToReplace, new String[]{valueToReplace});

        return TestUtils.mockQueryParamsMap(parameterMap);
    }

    public static Request mockRequestWithParams() {
        return mockRequestWithParams("", "");
    }

    public static Request mockRequestWithParams(String keyToReplace, String valueToReplace) {

        QueryParamsMap map = mockQueryParams(keyToReplace, valueToReplace);
        Session session = mock(Session.class);
        when(session.id()).thenReturn(UUID.randomUUID().toString());
        Request req = mock(Request.class);
        when(req.queryMap()).thenReturn(map);
        when(req.session()).thenReturn(session);
        return req;
    }
}
