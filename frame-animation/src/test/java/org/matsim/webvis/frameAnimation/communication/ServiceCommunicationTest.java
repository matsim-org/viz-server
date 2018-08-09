package org.matsim.webvis.frameAnimation.communication;

import org.glassfish.jersey.client.JerseyClient;
import org.junit.Test;
import org.matsim.webis.oauth.ClientAuthentication;

import javax.ws.rs.client.Client;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.mock;

public class ServiceCommunicationTest {

    @Test
    public void initialize_propertiesSet() {

        Client client = mock(JerseyClient.class);
        ClientAuthentication authentication = mock(ClientAuthentication.class);

        ServiceCommunication.initialize(client, authentication);

        assertEquals(client, ServiceCommunication.getClient());
        assertEquals(authentication, ServiceCommunication.getAuthentication());
    }
}
