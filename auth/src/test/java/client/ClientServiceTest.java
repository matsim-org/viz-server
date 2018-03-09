package client;

import data.entities.Client;
import data.entities.RedirectUri;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.*;

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

    @Test
    public void findClient_notPresent_null() {

        Client client = testObject.findClient("someId");
        assertNull(client);
    }

    @Test
    public void findClient_present_client() {

        final String name = "name";
        final URI uri = URI.create("http://test.org");
        Client client = testObject.createClient(name, uri);

        Client found = testObject.findClient(client.getId());

        assertEquals(client.getName(), found.getName());
        assertEquals(client.getId(), found.getId());
    }

}
