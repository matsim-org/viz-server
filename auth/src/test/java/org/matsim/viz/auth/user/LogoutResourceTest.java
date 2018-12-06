package org.matsim.viz.auth.user;

import org.junit.Test;

import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class LogoutResourceTest {

    @Test
    public void logout() {

        LogoutResource testObject = new LogoutResource();

        Response response = testObject.logout();

        assertEquals(Response.Status.ACCEPTED.getStatusCode(), response.getStatus());

        // test if we have a login cookie with no value which immediately expires
        assertTrue(response.getCookies().containsKey("login"));
        NewCookie loginCookie = response.getCookies().get("login");
        assertEquals("", loginCookie.getValue());
        assertEquals(0, loginCookie.getMaxAge());
    }
}
