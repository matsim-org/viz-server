package org.matsim.viz.auth.relyingParty;

import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import lombok.AllArgsConstructor;
import org.matsim.viz.auth.entities.RelyingParty;

import java.util.Optional;

@AllArgsConstructor
public class RelyingPartyAuthenticator implements Authenticator<BasicCredentials, RelyingParty> {

    RelyingPartyService rpService;

    @Override
    public Optional<RelyingParty> authenticate(BasicCredentials basicCredentials) {

        RelyingParty party = rpService.validateRelyingParty(basicCredentials.getUsername(), basicCredentials.getPassword());
        return Optional.of(party);
    }
}
