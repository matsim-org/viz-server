package org.matsim.webvis.auth.token;

import org.matsim.webvis.common.communication.Answer;

public interface Grant {

    Answer processRequest(TokenRequest request);
}
