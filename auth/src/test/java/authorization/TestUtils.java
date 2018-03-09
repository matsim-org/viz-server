package authorization;

import spark.QueryParamsMap;
import spark.Request;
import spark.Session;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestUtils {

    public static Map<String, String[]> createDefaultParameterMap() {
        Map<String, String[]> parameterMap = new HashMap<>();
        parameterMap.put("scope", new String[]{"openid"});
        parameterMap.put("response_type", new String[]{"code"});
        parameterMap.put("client_id", new String[]{"test-client-id"});
        parameterMap.put("redirect_uri", new String[]{"http://valid.uri"});

        return parameterMap;
    }

    public static QueryParamsMap createQueryParams(Map<String, String[]> parameterMap) {
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        when(servletRequest.getParameterMap()).thenReturn(parameterMap);

        return new QueryParamsMap(servletRequest);
    }

    public static QueryParamsMap createQueryParams(String keyToReplace, String valueToReplace) {

        Map<String, String[]> parameterMap = createDefaultParameterMap();

        if (!keyToReplace.isEmpty())
            parameterMap.put(keyToReplace, new String[]{valueToReplace});

        return createQueryParams(parameterMap);
    }

    public static Request mockRequestWithParams() {
        return mockRequestWithParams("", "");
    }

    public static Request mockRequestWithParams(String keyToReplace, String valueToReplace) {

        QueryParamsMap map = createQueryParams(keyToReplace, valueToReplace);
        Session session = mock(Session.class);
        when(session.id()).thenReturn(UUID.randomUUID().toString());
        Request req = mock(Request.class);
        when(req.queryMap()).thenReturn(map);
        when(req.session()).thenReturn(session);
        return req;
    }
}
