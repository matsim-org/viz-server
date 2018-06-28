package org.matsim.webvis.auth.token;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.matsim.webvis.auth.entities.Token;
import org.matsim.webvis.auth.util.TestUtils;

import java.time.Instant;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IntrospectResourceTest {

    private IntrospectResource testObject;

    @BeforeClass
    public static void setUpFixture() {
        TestUtils.loadTestConfigIfNecessary();
    }

    @Before
    public void setUp() {
        testObject = new IntrospectResource();
        testObject.tokenService = mock(TokenService.class);
    }

    @Test
    public void introspect_invalidToken_inactiveResponse() {

        when(testObject.tokenService.validateToken(anyString())).thenThrow(new RuntimeException(""));

        IntrospectionResponse response = testObject.introspect(null, "some-token");

        assertFalse(response.isActive());
        assertNull(response.getScope());
        assertNull(response.getToken_type());
        assertNull(response.getSub());
    }

    @Test
    public void introspect_validToken_activeResponse() {

        Token token = new Token();
        token.setTokenValue("some-value");
        token.setSubjectId("sub-id");
        token.setScope("scope");
        token.setCreatedAt(Instant.now());
        token.setExpiresAt(Instant.now());

        when(testObject.tokenService.validateToken(anyString())).thenReturn(token);

        IntrospectionResponse response = testObject.introspect(null, "some-value");

        assertTrue(response.isActive());
        assertTrue(response instanceof ActiveIntrospectionResponse);
        assertEquals(token.getScope(), response.getScope());
        assertEquals(token.getSubjectId(), response.getSub());
        assertEquals("Bearer", response.getToken_type());
        assertEquals(token.getCreatedAt().toEpochMilli(), response.getIat());
        assertEquals(token.getExpiresAt().toEpochMilli(), response.getExp());
    }


}
