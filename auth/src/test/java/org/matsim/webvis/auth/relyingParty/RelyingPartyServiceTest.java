package org.matsim.webvis.auth.relyingParty;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.matsim.webvis.auth.config.ConfigClient;
import org.matsim.webvis.auth.config.ConfigRelyingParty;
import org.matsim.webvis.auth.entities.Client;
import org.matsim.webvis.auth.entities.RedirectUri;
import org.matsim.webvis.auth.entities.RelyingParty;
import org.matsim.webvis.auth.entities.RelyingPartyCredential;
import org.matsim.webvis.common.errorHandling.UnauthorizedException;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class RelyingPartyServiceTest {

    private RelyingPartyService testObject;
    private RelyingPartyDAO relyingPartyDAO = new RelyingPartyDAO();

    @Before
    public void setUp() {
        testObject = RelyingPartyService.Instance;
    }

    @After
    public void tearDown() {
        relyingPartyDAO.removeAllRelyingParties();
    }

    @Test
    public void createClient_allFine() {

        final String name = "name";
        final URI uri = URI.create("http://test.org/callback");
        List<URI> uris = new ArrayList<>();
        uris.add(uri);

        Client client = testObject.createClient(name, uris);

        assertEquals(name, client.getName());
        assertNotEquals(0, client.getId());
        assertEquals(1, client.getRedirectUris().size());

        RedirectUri redirect = client.getRedirectUris().iterator().next();
        assertEquals(uri, redirect.getUri());
        assertEquals(client, redirect.getClient());
    }

    @Test
    public void createClient_configClient_allFine() {

        String name = "name";
        String id = "id";
        String secret = "secret";
        URI uri = URI.create("http://some.uri");
        List<URI> uris = new ArrayList<>();
        uris.add(uri);
        Set<String> scopes = new HashSet<>();
        scopes.add("scope");
        ConfigClient configClient = new ConfigClient(name, id, secret, uris, scopes);

        Client client = testObject.createClient(configClient);

        assertEquals(name, client.getName());
        assertEquals(id, client.getId());
        assertEquals(uris.size(), client.getRedirectUris().size());
        assertEquals(scopes.size(), client.getScopes().size());
    }

    @Test
    public void createRelyingParty_allFine() {

        String name = "name";
        String id = "id";
        String secret = "secret";
        Set<String> scopes = new HashSet<>();
        scopes.add("scope");
        scopes.add("other-scope");
        ConfigRelyingParty configRelyingParty = new ConfigRelyingParty(id, name, secret, scopes);

        RelyingParty relyingParty = testObject.createRelyingParty(configRelyingParty);

        assertEquals(name, relyingParty.getName());
        assertEquals(id, relyingParty.getId());
        assertEquals(scopes.size(), relyingParty.getScopes().size());
    }

    @Test
    public void findClient_notPresent_null() {

        Client client = testObject.findClient("someId");
        assertNull(client);
    }

    @Test
    public void findClient_present_client() {

        final String name = "name";
        final URI uri = URI.create("http://test.org");
        List<URI> uris = new ArrayList<>();
        uris.add(uri);

        Client client = testObject.createClient(name, uris);

        Client found = testObject.findClient(client.getId());

        assertEquals(client.getName(), found.getName());
        assertEquals(client.getId(), found.getId());
    }

    @Test(expected = UnauthorizedException.class)
    public void validateRelyingParty_noClient_exception() {

        testObject.validateRelyingParty("invalid", "password");

        fail("if client is not present exception should be thrown");
    }

    @Test(expected = UnauthorizedException.class)
    public void validateRelyingParty_secretsDontMatch_exception() {

        RelyingParty party = new RelyingParty();
        party.getScopes().add("scope");
        RelyingPartyCredential credential = new RelyingPartyCredential();
        credential.setRelyingParty(party);

        RelyingPartyCredential persisted = relyingPartyDAO.persistCredential(credential);

        testObject.validateRelyingParty(party.getId(), "wrong secret");

        fail("if secret is wrong exception should be thrown");
    }

    @Test(expected = UnauthorizedException.class)
    public void validateRelyingParty_scopesDontMatch_unauthorizedException() {

        List<String> scopes = new ArrayList<>();
        scopes.add("scope");
        RelyingParty party = new RelyingParty();
        party.getScopes().add("other-scope");
        RelyingPartyCredential credential = new RelyingPartyCredential();
        credential.setRelyingParty(party);

        RelyingPartyCredential persisted = relyingPartyDAO.persistCredential(credential);

        testObject.validateRelyingParty(party.getId(), persisted.getSecret(), scopes);

        fail("not matching scopes should cause exception");
    }

    @Test
    public void validateRelyingParty_allGood_relyingParty() {
        RelyingParty party = new RelyingParty();
        party.setName("name");
        RelyingPartyCredential credential = new RelyingPartyCredential();
        credential.setRelyingParty(party);

        RelyingPartyCredential persisted = relyingPartyDAO.persistCredential(credential);

        RelyingParty result = testObject.validateRelyingParty(party.getId(), persisted.getSecret());

        assertNotNull(result);
        assertEquals(party.getName(), result.getName());
    }

    @Test
    public void validateRelyingParty_allGoodWithScopes_relyingParty() {

        List<String> scopes = new ArrayList<>();
        scopes.add("first-scope");
        scopes.add("second-scope");
        RelyingParty party = new RelyingParty();
        party.getScopes().addAll(scopes);
        RelyingPartyCredential credential = new RelyingPartyCredential();
        credential.setRelyingParty(party);

        RelyingPartyCredential persisted = relyingPartyDAO.persistCredential(credential);

        RelyingParty result = testObject.validateRelyingParty(party.getId(), persisted.getSecret(), scopes);

        assertNotNull(result);
        assertEquals(party.getName(), result.getName());
    }

    @Test(expected = UnauthorizedException.class)
    public void validateClient_clientNotPresent_exception() {

        testObject.validateClient("some-id", null, null);

        fail("invalid client id should cause exception");
    }

    @Test(expected = UnauthorizedException.class)
    public void validateClient_wrongRedirectUri_exception() {

        final URI redirectURI = URI.create("http://some.uri");
        final List<String> scopes = new ArrayList<>();
        scopes.add("scope");

        RelyingPartyCredential credential = persistWithClient(URI.create("http://other.uri"), scopes);

        testObject.validateClient(credential.getRelyingParty().getId(), redirectURI, scopes);

        fail("invalid redirect uri should cause exception");
    }

    @Test(expected = UnauthorizedException.class)
    public void validateClient_scopesDontMatch_exception() {

        final URI redirectURI = URI.create("http://some.uri");
        final List<String> scopes = new ArrayList<>();
        scopes.add("some scopes");
        final List<String> otherScopes = new ArrayList<>();
        otherScopes.add("some other scopes");

        RelyingPartyCredential credential = persistWithClient(redirectURI, scopes);

        testObject.validateClient(credential.getRelyingParty().getId(), redirectURI, otherScopes);

        fail("invalid scopes should cause exception");
    }

    @Test
    public void validateClient_allGood_Client() {

        final URI redirectURI = URI.create("http://some.uri");
        final List<String> scopes = new ArrayList<>();
        scopes.add("some scopes");

        RelyingPartyCredential credential = persistWithClient(redirectURI, scopes);

        Client client = testObject.validateClient(credential.getRelyingParty().getId(), redirectURI, scopes);

        assertNotNull(client);
        assertTrue(credential.getRelyingParty().equalId(client));
    }

    private RelyingPartyCredential persistWithClient(URI redirectUri, List<String> scopes) {
        Client client = new Client();
        RedirectUri uri = new RedirectUri();
        uri.setUri(redirectUri);
        client.addRedirectUri(uri);
        client.getScopes().addAll(scopes);
        RelyingPartyCredential credential = new RelyingPartyCredential();
        credential.setRelyingParty(client);

        return relyingPartyDAO.persistCredential(credential);
    }
}
