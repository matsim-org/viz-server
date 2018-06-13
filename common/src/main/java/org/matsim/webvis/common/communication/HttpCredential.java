package org.matsim.webvis.common.communication;

public interface HttpCredential {

    String AUTHORIZATION_HEADER = "Authorization";

    String headerValue();
}
