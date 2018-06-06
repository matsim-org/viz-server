package org.matsim.webvis.auth.authorization;

public class AuthorizationRequestHandlerTest {

    private AuthorizationRequestHandler testObject;

   /* @BeforeClass
    public static void setUpFixture() throws UnsupportedEncodingException, FileNotFoundException {
        TestUtils.loadTestConfig();
    }

    @Before
    public void setUp() {
        testObject = new AuthorizationRequestHandler();
        testObject.authService = mock(AuthorizationService.class);
        when(testObject.authService.isValidClientInformation(any())).thenReturn(true);
        testObject.tokenService = mock(TokenService.class);
        testObject.userService = mock(UserService.class);
    }

    @Test
    public void handle_missingOrInvalidUri_errorResponse() {

        Request req = AuthorizationTestUtils.mockRequestWithParams(AuthenticationRequest.REDIRECT_URI, "invalid uri");
        Object result = testObject.handle(req, null);

        assertErrorResponse(result, Error.INVALID_REQUEST);
    }

    @Test
    public void handle_missingOrInvalidRequiredParameter_redirect() {

        Request req = AuthorizationTestUtils.mockRequestWithParams(AuthenticationRequest.SCOPE, "notopenid");
        Response res = mock(Response.class);
        final String expectedQuery = "error=invalid_request";

        testObject.handle(req, res);

        verify(res).redirect(contains(expectedQuery), eq(302));
    }

    @Test
    public void handle_invalidClientInformation_errorResponse() {

        Request req = AuthorizationTestUtils.mockRequestWithParams();
        when(testObject.authService.isValidClientInformation(any())).thenReturn(false);

        Object result = testObject.handle(req, null);

        assertErrorResponse(result, Error.UNAUTHORIZED_CLIENT);
    }

    @Test
    public void handle_userIsNotLoggedIn_loginPrompt() {

        Request req = AuthorizationTestUtils.mockRequestWithParams();
        when(testObject.tokenService.validateToken(any())).thenThrow(new RuntimeException("message"));
        Response res = mock(Response.class);

        testObject.handle(req, res);

        verify(res).redirect(Routes.LOGIN, 302);
    }

    @Test
    public void handle_unknownUser_loginPrompt() {

        Request req = AuthorizationTestUtils.mockRequestWithParams();
        when(testObject.tokenService.validateToken(any())).thenThrow(new RuntimeException("invalid"));
        Response res = mock(Response.class);

        testObject.handle(req, res);

        verify(res).redirect(Routes.LOGIN, 302);
    }

    @Test
    public void handle_success_redirectWithParams() {

        URI uri = URI.create("http://resulting.uri");
        Token token = new Token();
        token.setSubjectId("some-id");
        when(testObject.tokenService.validateToken(any())).thenReturn(token);
        when(testObject.userService.findUser(anyString())).thenReturn(new User());
        when(testObject.authService.generateResponse(any(), any())).thenReturn(uri);

        Request req = AuthorizationTestUtils.mockRequestWithParams();
        Response res = mock(Response.class);

        Object result = testObject.handle(req, res);

        assertTrue(result instanceof String);
        assertEquals("OK", (String) result);

        verify(res).redirect(eq(uri.toString()), eq(302));
    }

    private void assertErrorResponse(Object result, String errorCode) {
        assertTrue(result instanceof String);
        String[] message = ((String) result).split(" ");
        assertTrue(message.length > 1);
        assertEquals("error", message[0]);
        assertEquals(errorCode, message[1]);
    }
    */
}
