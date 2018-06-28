package org.matsim.webvis.auth.relyingParty;

import io.dropwizard.auth.basic.BasicCredentials;
import org.junit.Before;
import org.junit.Test;
import org.matsim.webvis.auth.entities.RelyingParty;
import org.matsim.webvis.common.errorHandling.UnauthorizedException;

import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RelyingPartyAuthenticatorTest {

    private RelyingPartyAuthenticator testObject;

    @Before
    public void setUp() {
        testObject = new RelyingPartyAuthenticator();
        testObject.rpService = mock(RelyingPartyService.class);
    }

    @Test
    public void authenticate_success() {

        when(testObject.rpService.validateRelyingParty(anyString(), anyString())).thenReturn(new RelyingParty());
        BasicCredentials credentials = new BasicCredentials("username", "password");

        Optional<RelyingParty> result = testObject.authenticate(credentials);

        assertTrue(result.isPresent());
    }

    @Test
    public void authenticate_noSucess() {

        when(testObject.rpService.validateRelyingParty(anyString(), anyString())).thenThrow(new UnauthorizedException("no"));
        BasicCredentials credentials = new BasicCredentials("username", "password");

        Optional<RelyingParty> result = testObject.authenticate(credentials);

        assertFalse(result.isPresent());
    }
}
