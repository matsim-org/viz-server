package org.matsim.viz.auth.token;

import org.junit.Test;
import org.matsim.viz.auth.entities.Token;

import java.time.Instant;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class ActiveIntrospectionResponseTest {

    @Test
    public void constructor_allParametersSet_instance() {

        final Instant expiresAt = Instant.now();
        final Instant issuedAt = Instant.now();
        final String subjectId = "id";
        final String scope = "scope";

        Token token = new Token();
        token.setExpiresAt(expiresAt);
        token.setCreatedAt(issuedAt);
        token.setSubjectId(subjectId);
        token.setScope(scope);

        ActiveIntrospectionResponse instance = new ActiveIntrospectionResponse(token);

        assertEquals(expiresAt.toEpochMilli(), instance.getExp());
        assertEquals(issuedAt.toEpochMilli(), instance.getIat());
        assertEquals(subjectId, instance.getSub());
        assertEquals(scope, instance.getScope());
        assertTrue(instance.isActive());
    }
}
