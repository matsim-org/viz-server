package org.matsim.webvis.auth.util;

import org.matsim.webvis.auth.config.AppConfiguration;
import org.matsim.webvis.auth.entities.User;
import org.matsim.webvis.auth.relyingParty.RelyingPartyDAO;
import org.matsim.webvis.auth.user.UserDAO;
import org.matsim.webvis.auth.user.UserService;
import spark.QueryParamsMap;
import spark.Request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Base64;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SuppressWarnings("WeakerAccess")
public class TestUtils {

    private static UserService userService = UserService.Instance;
    private static UserDAO userDAO = new UserDAO();
    private static RelyingPartyDAO relyingPartyDAO = new RelyingPartyDAO();

    public static HttpSession mockSession(String id) {

        HttpSession session = mock(HttpSession.class);
        when(session.getId()).thenReturn(id);
        return session;
    }

    public static void loadTestConfigIfNecessary() {

        if (AppConfiguration.getInstance() != null)
            return;

        AppConfiguration.setInstance(new TestAppConfiguration());
    }

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

    public static Request mockRequestWithQueryParamsMapAndBasicAuth
            (Map<String, String[]> parameterMap, String contentType, String principal, String credential) {
        Request request = mockRequestWithQueryParamsMap(parameterMap, contentType);
        when(request.headers("Authorization")).thenReturn(encodeBasicAuth(principal, credential));
        return request;
    }

    public static String getTestConfigPath() throws UnsupportedEncodingException {
        return getResourcePath("test-config.yml");
    }

    public static String getEmptyTestConfigPath() throws UnsupportedEncodingException {
        return getResourcePath("empty-test-config.json");
    }

    @SuppressWarnings("ConstantConditions")
    public static String getResourcePath(String resourceFile) throws UnsupportedEncodingException {
        return URLDecoder.decode(TestUtils.class.getClassLoader().getResource(resourceFile).getFile(), "UTF-8");
    }

    public static String encodeBasicAuth(String principal, String credential) {
        return "Basic " + Base64.getEncoder().encodeToString((principal + ":" + credential).getBytes());
    }

    public static User persistUser(String eMail, String password) {
        return userService.createUser(eMail, password.toCharArray(), password.toCharArray());
    }

    public static void removeAllUser() {
        userDAO.removeAllUsers();
    }

    public static void removeAllRelyingParties() {
        relyingPartyDAO.removeAllRelyingParties();
    }
}
