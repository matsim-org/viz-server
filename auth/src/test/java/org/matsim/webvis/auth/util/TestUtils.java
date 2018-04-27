package org.matsim.webvis.auth.util;

import org.matsim.webvis.auth.config.Configuration;
import spark.QueryParamsMap;
import spark.Request;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class TestUtils {

    public static QueryParamsMap mockQueryParamsMap(Map<String, String[]> parameterMap) {
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        when(servletRequest.getParameterMap()).thenReturn(parameterMap);

        return new QueryParamsMap(servletRequest);
    }

    public static Request mockRequestWithQueryParams(Map<String, String> parameterMap, String contentType) {

        Request result = mock(Request.class);
        doAnswer(invocationOnMock -> {
            String key = invocationOnMock.getArgument(0);
            return parameterMap.get(key);
        }).when(result).queryParams(anyString());

        when(result.contentType()).thenReturn(contentType);

        return result;
    }

    public static Request mockRequestWithQueryParamsMap(Map<String, String[]> parameterMap, String contentType) {

        Request result = mock(Request.class);
        QueryParamsMap map = mockQueryParamsMap(parameterMap);
        when(result.queryMap()).thenReturn(map);
        when(result.contentType()).thenReturn(contentType);

        return result;
    }

    public static void loadTestConfig() throws UnsupportedEncodingException, FileNotFoundException {
        Configuration.loadTestConfig(getTestConfigPath());
    }

    public static void loadEmptyTestConfig() throws UnsupportedEncodingException, FileNotFoundException {
        Configuration.loadTestConfig(getEmptyTestConfigPath());
    }

    public static String getTestConfigPath() throws UnsupportedEncodingException {
        return getResourcePath("test-config.json");
    }

    public static String getEmptyTestConfigPath() throws UnsupportedEncodingException {
        return getResourcePath("empty-test-config.json");
    }

    @SuppressWarnings("ConstantConditions")
    public static String getResourcePath(String resourceFile) throws UnsupportedEncodingException {
        return URLDecoder.decode(TestUtils.class.getClassLoader().getResource(resourceFile).getFile(), "UTF-8");
    }
}
