package util;

import spark.QueryParamsMap;
import spark.Request;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class TestUtils {

    public static QueryParamsMap mockQueryParamsMap(Map<String, String[]> parameterMap) {
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        when(servletRequest.getParameterMap()).thenReturn(parameterMap);

        return new QueryParamsMap(servletRequest);
    }

    public static Request mockRequestWithQueryParams(Map<String, String> parameterMap) {

        Request result = mock(Request.class);
        doAnswer(invocationOnMock -> {
            String key = invocationOnMock.getArgument(0);
            return parameterMap.get(key);
        }).when(result).queryParams(anyString());

        return result;
    }
}
