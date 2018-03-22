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

    public static Request mockRequestWithQueryParams(Map<String, String> parameterMap) {

        Request result = mock(Request.class);
        doAnswer(invocationOnMock -> {
            String key = invocationOnMock.getArgument(0);
            return parameterMap.get(key);
        }).when(result).queryParams(anyString());

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

    public static String getResourcePath(String resourceFile) throws UnsupportedEncodingException {
        return URLDecoder.decode(TestUtils.class.getClassLoader().getResource(resourceFile).getFile(), "UTF-8");
    }
}
