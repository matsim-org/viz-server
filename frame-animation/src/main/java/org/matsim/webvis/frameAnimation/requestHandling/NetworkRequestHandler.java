package org.matsim.webvis.frameAnimation.requestHandling;

import org.matsim.webvis.frameAnimation.data.MatsimDataProvider;

public class NetworkRequestHandler extends AbstractPostRequestHandler<Object> {

    public NetworkRequestHandler(MatsimDataProvider dataProvider) {

        super(Object.class, dataProvider);
    }

    @Override
    public Answer process(Object body) {

        byte[] bytes;

        bytes = dataProvider.getLinks();
        return Answer.ok(bytes);
    }
}
