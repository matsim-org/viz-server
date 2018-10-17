package org.matsim.viz.auth.token;

import lombok.AllArgsConstructor;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/certificates")
@Produces(MediaType.APPLICATION_JSON)
@AllArgsConstructor
public class TokenSigningKeyResource {

    private TokenSigningKeyProvider tokenSigningKeyProvider;

    @GET
    public List<TokenSigningKeyProvider.KeyInformation> getKeys() {
        return tokenSigningKeyProvider.getKeyInformation();
    }

}
