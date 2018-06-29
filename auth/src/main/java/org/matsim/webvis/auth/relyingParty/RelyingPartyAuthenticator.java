package org.matsim.webvis.auth.relyingParty;

import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import org.matsim.webvis.auth.entities.RelyingParty;

import java.util.Optional;

public class RelyingPartyAuthenticator implements Authenticator<BasicCredentials, RelyingParty> {

    RelyingPartyService rpService = RelyingPartyService.Instance;

    @Override
    public Optional<RelyingParty> authenticate(BasicCredentials basicCredentials) {

        RelyingParty party = rpService.validateRelyingParty(basicCredentials.getUsername(), basicCredentials.getPassword());
        return Optional.of(party);
    }
}
