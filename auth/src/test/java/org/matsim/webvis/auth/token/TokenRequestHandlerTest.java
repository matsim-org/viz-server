package org.matsim.webvis.auth.token;

public class TokenRequestHandlerTest {

    //TODO
    /*
    private TokenRequestHandler testObject;

    @BeforeClass
    public static void setUpFixture() throws UnsupportedEncodingException, FileNotFoundException {
        TestUtils.loadTestConfig();
    }

    @Before
    public void setUp() {
        testObject = new TokenRequestHandler();
    }

    @Test(expected = InvalidInputException.class)
    public void process_invalidParameters_invalidInputException() {

        Request request = TestUtils.mockRequestWithQueryParamsMap(new HashMap<>(),
                ContentType.FORM_URL_ENCODED);

        testObject.process(request, null);

        fail("invalid request should cause exception");
    }

    @Test(expected = InternalException.class)
    public void process_unsupportedGrantType_internalException() {

        Map<String, String[]> map = new HashMap<>();
        map.put("grant_type", new String[]{"unsupported"});
        Request request = TestUtils.mockRequestWithQueryParamsMap(map, ContentType.FORM_URL_ENCODED);
        when(request.headers(BasicAuthentication.HEADER_AUTHORIZATION)).thenReturn(TestUtils.encodeBasicAuth("principal", "credential"));

        testObject.process(request, null);

        fail("unsupported grant_type should cause exception");
    }

    @Test
    public void process_passwordGrant_tokenResponse() {

        Map<String, String[]> map = new HashMap<>();
        map.put("grant_type", new String[]{"password"});
        map.put("username", new String[]{"username"});
        map.put("password", new String[]{"user-password"});
        Request request = TestUtils.mockRequestWithQueryParamsMap(map, ContentType.FORM_URL_ENCODED);
        when(request.headers(BasicAuthentication.HEADER_AUTHORIZATION)).thenReturn(TestUtils.encodeBasicAuth("principal", "credential"));

        testObject.passwordGrant.tokenService = mock(TokenService.class);
        when(testObject.passwordGrant.tokenService.grantWithPassword(anyString(), any())).thenReturn(getDummyToken());

        Answer answer = testObject.process(request, null);

        assertEquals(HttpStatus.OK, answer.getStatusCode());
        assertTrue(answer.getResponse() instanceof AccessTokenResponse);
    }

    @Test
    public void process_clientCredentialsGrant_tokenResponse() {

        Map<String, String[]> map = new HashMap<>();
        map.put("grant_type", new String[]{"client_credentials"});
        Request request = TestUtils.mockRequestWithQueryParamsMap(map, ContentType.FORM_URL_ENCODED);
        when(request.headers(BasicAuthentication.HEADER_AUTHORIZATION)).thenReturn(TestUtils.encodeBasicAuth("principal", "credential"));

        testObject.clientCredentialsGrant.tokenService = mock(TokenService.class);
        when(testObject.clientCredentialsGrant.tokenService.grantForScope(any())).thenReturn(getDummyToken());

        Answer answer = testObject.process(request, null);

        assertEquals(HttpStatus.OK, answer.getStatusCode());
        assertTrue(answer.getResponse() instanceof AccessTokenResponse);
    }

    private Token getDummyToken() {
        Token token = new Token();
        token.setTokenValue("some-value");
        token.setExpiresAt(Instant.now().plus(Duration.ofHours(1)));
        return token;
    }
    */
}
