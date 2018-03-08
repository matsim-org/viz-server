package client;

import data.entities.Client;
import data.entities.RedirectUri;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ClientServiceTest {

    private ClientService testObject;
    private ClientDAO clientDAO = new ClientDAO();

    @Before
    public void setUp() {
        testObject = new ClientService();
    }

    @After
    public void tearDown() {
        clientDAO.removeAllClients();
    }

    @Test
    public void createClient_allFine() {

        final String name = "name";
        final URI uri = URI.create("http://test.org/callback");

        Client client = testObject.createClient(name, uri);

        assertEquals(name, client.getName());
        assertNotEquals(0, client.getId());
        assertEquals(1, client.getRedirectUris().size());

        RedirectUri redirect = client.getRedirectUris().iterator().next();
        assertEquals(uri.toString(), redirect.getUri());
        assertEquals(client, redirect.getClient());
    }

}
