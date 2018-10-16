package org.matsim.viz.auth.relyingParty;

import io.dropwizard.auth.basic.BasicCredentials;
import org.junit.Test;
import org.matsim.viz.auth.entities.RelyingParty;
import org.matsim.viz.error.UnauthorizedException;

import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RelyingPartyAuthenticatorTest {

    @Test
    public void authenticate_success() {

        RelyingPartyService rpService = mock(RelyingPartyService.class);
        when(rpService.validateRelyingParty(anyString(), anyString())).thenReturn(new RelyingParty());
        RelyingPartyAuthenticator testObject = new RelyingPartyAuthenticator(rpService);

        BasicCredentials credentials = new BasicCredentials("username", "password");

        Optional<RelyingParty> result = testObject.authenticate(credentials);

        assertTrue(result.isPresent());
    }

    @Test(expected = UnauthorizedException.class)
    public void authenticate_noSucess() {

        RelyingPartyService rpService = mock(RelyingPartyService.class);
        when(rpService.validateRelyingParty(anyString(), anyString())).thenThrow(new UnauthorizedException("no"));
        RelyingPartyAuthenticator testObject = new RelyingPartyAuthenticator(rpService);

        BasicCredentials credentials = new BasicCredentials("username", "password");

        Optional<RelyingParty> result = testObject.authenticate(credentials);

        assertFalse(result.isPresent());
    }
}
