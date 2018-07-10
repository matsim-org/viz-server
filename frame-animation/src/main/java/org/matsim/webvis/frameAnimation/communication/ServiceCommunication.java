package org.matsim.webvis.frameAnimation.communication;

import lombok.Getter;
import org.matsim.webis.oauth.ClientAuthentication;

import javax.ws.rs.client.Client;


public class ServiceCommunication {

    @Getter
    private static Client client;
    @Getter
    private static ClientAuthentication authentication;

    public static void initialize(Client client, ClientAuthentication authentication) {
        ServiceCommunication.client = client;
        ServiceCommunication.authentication = authentication;
    }
}
