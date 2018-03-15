package token;

import data.entities.IdToken;
import data.entities.Token;
import data.entities.User;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class IntrospectionResponseTest {

    @Test
    public void creation_tokenIsNull_activeFalse() {

        IntrospectionResponse response = new IntrospectionResponse(null);

        assertEquals(false, response.isActive());
        assertNull(response.getSub());
    }

    @Test
    public void creation_token_activeTrue() {

        Token token = new IdToken();
        token.setToken("token");
        token.setCreatedAt(Instant.now());
        token.setExpiresAt(Instant.now().plus(Duration.ofHours(1)));

        User user = new User();
        user.setId("user-id");
        token.setUser(user);

        IntrospectionResponse response = new IntrospectionResponse(token);

        assertEquals(true, response.isActive());
        assertEquals(token.getCreatedAt().toEpochMilli(), response.getIat());
        assertEquals(token.getExpiresAt().toEpochMilli(), response.getExp());
        assertEquals(token.getTokenType(), response.getToken_type());
        assertEquals(token.getUser().getId(), response.getSub());
    }
}
