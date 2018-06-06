package org.matsim.webvis.auth.util;

import org.matsim.webvis.auth.config.Configuration;
import org.matsim.webvis.auth.entities.User;
import org.matsim.webvis.auth.relyingParty.RelyingPartyDAO;
import org.matsim.webvis.auth.token.TokenRequest;
import org.matsim.webvis.auth.user.UserDAO;
import org.matsim.webvis.auth.user.UserService;
import org.matsim.webvis.common.auth.PrincipalCredentialToken;
import spark.QueryParamsMap;
import spark.Request;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
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

    public static TokenRequest mockTokenRequest(String clientPrincipal, String clientCredential, String[] scope) {

        TokenRequest request = mock(TokenRequest.class);
        when(request.getBasicAuth()).thenReturn(new PrincipalCredentialToken(clientPrincipal, clientCredential));
        when(request.getGrantType()).thenReturn("some-grant-type");
        when(request.getScope()).thenReturn(scope);
        return request;
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
