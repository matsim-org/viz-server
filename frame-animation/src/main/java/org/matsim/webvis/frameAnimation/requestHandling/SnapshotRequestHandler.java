package org.matsim.webvis.frameAnimation.requestHandling;

import org.matsim.webvis.frameAnimation.constants.Params;
import org.matsim.webvis.frameAnimation.contracts.SnapshotRequest;

import java.io.IOException;

public class SnapshotRequestHandler extends AbstractPostRequestHandler<SnapshotRequest> {

    public SnapshotRequestHandler() {
        super(SnapshotRequest.class);
    }

    @Override
    public Answer process(SnapshotRequest body) {

        double startTime = body.getFromTimestep();
        int size = body.getSize();
        double speedFactor = body.getSpeedFactor();

        byte[] bytes;
        try {
            bytes = getData().getSnapshots(body.getId(), startTime, size, speedFactor);
        } catch (IOException e) {
            e.printStackTrace();
            return new Answer(Params.STATUS_INTERNAL_SERVER_ERROR, "Sorry.");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new Answer(Params.STATUS_BADREQUEST, e.getMessage());
        }
        return Answer.ok(bytes);
    }
}
