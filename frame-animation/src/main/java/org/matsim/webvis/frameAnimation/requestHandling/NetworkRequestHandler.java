package org.matsim.webvis.frameAnimation.requestHandling;

import org.matsim.webvis.frameAnimation.contracts.VisualizationRequest;

public class NetworkRequestHandler extends AbstractPostRequestHandler<VisualizationRequest> {

    public NetworkRequestHandler() {

        super(VisualizationRequest.class);
    }

    @Override
    public Answer process(VisualizationRequest body) {

        byte[] bytes = getData().getLinks(body.getId());
        return Answer.ok(bytes);
    }
}
