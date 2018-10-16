package org.matsim.viz.auth.token;

import org.junit.Test;
import org.matsim.viz.auth.entities.Token;

import java.time.Instant;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IntrospectResourceTest {

    @Test
    public void introspect_invalidToken_inactiveResponse() {

        TokenService tokenService = mock(TokenService.class);
        when(tokenService.validateToken(anyString())).thenThrow(new RuntimeException(""));
        IntrospectResource testObject = new IntrospectResource(tokenService);

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


        TokenService tokenService = mock(TokenService.class);
        when(tokenService.validateToken(anyString())).thenReturn(token);
        IntrospectResource testObject = new IntrospectResource(tokenService);

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
