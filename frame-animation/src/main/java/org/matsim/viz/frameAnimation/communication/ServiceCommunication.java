package org.matsim.viz.frameAnimation.communication;

import lombok.Getter;
import org.matsim.viz.clientAuth.ClientAuthentication;

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
