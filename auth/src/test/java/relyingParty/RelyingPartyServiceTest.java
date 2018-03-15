package relyingParty;

import data.entities.Client;
import data.entities.RedirectUri;
import data.entities.RelyingParty;
import data.entities.RelyingPartyCredential;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class RelyingPartyServiceTest {

    private RelyingPartyService testObject;
    private RelyingPartyDAO relyingPartyDAO = new RelyingPartyDAO();

    @Before
    public void setUp() {
        testObject = new RelyingPartyService();
    }

    @After
    public void tearDown() {
        relyingPartyDAO.removeAllClients();
    }

    @Test
    public void createClient_allFine() throws Exception {

        final String name = "name";
        final URI uri = URI.create("http://test.org/callback");
        List<URI> uris = new ArrayList<>();
        uris.add(uri);

        Client client = testObject.createClient(name, uris);

        assertEquals(name, client.getName());
        assertNotEquals(0, client.getId());
        assertEquals(1, client.getRedirectUris().size());

        RedirectUri redirect = client.getRedirectUris().iterator().next();
        assertEquals(uri.toString(), redirect.getUri());
        assertEquals(client, redirect.getClient());
    }

    @Test(expected = Exception.class)
    public void persistNewClient_tooFewRedirectUris_exception() throws Exception {

        Client client = new Client();

        testObject.persistNewClient(client);

        fail("if client has no redirectUris exception should be thrown");
    }

    @Test
    public void findClient_notPresent_null() {

        Client client = testObject.findClient("someId");
        assertNull(client);
    }

    @Test
    public void findClient_present_client() throws Exception {

        final String name = "name";
        final URI uri = URI.create("http://test.org");
        List<URI> uris = new ArrayList<>();
        uris.add(uri);

        Client client = testObject.createClient(name, uris);

        Client found = testObject.findClient(client.getId());

        assertEquals(client.getName(), found.getName());
        assertEquals(client.getId(), found.getId());
    }

    @Test(expected = Exception.class)
    public void validateRelyingParty_noClient_exception() throws Exception {

        testObject.validateRelyingParty("invalid", "password");

        fail("if client is not present exception should be thrown");
    }

    @Test(expected = Exception.class)
    public void validateRelyingParty_secretsDontMatch_exception() throws Exception {

        RelyingParty party = new RelyingParty();
        RelyingPartyCredential credential = new RelyingPartyCredential();
        credential.setRelyingParty(party);

        RelyingPartyCredential persisted = relyingPartyDAO.persist(credential);

        testObject.validateRelyingParty(party.getId(), "wrong secret");

        fail("if secret is wrong exception should be thrown");
    }

    @Test
    public void validateRelyingParty_allGood_relyingParty() throws Exception {
        RelyingParty party = new RelyingParty();
        party.setName("name");
        RelyingPartyCredential credential = new RelyingPartyCredential();
        credential.setRelyingParty(party);

        RelyingPartyCredential persisted = relyingPartyDAO.persist(credential);

        RelyingParty result = testObject.validateRelyingParty(party.getId(), persisted.getSecret());

        assertNotNull(result);
        assertEquals(party.getName(), result.getName());
    }
}
