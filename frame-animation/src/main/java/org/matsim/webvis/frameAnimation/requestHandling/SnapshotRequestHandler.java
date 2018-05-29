package org.matsim.webvis.frameAnimation.requestHandling;

import org.matsim.core.utils.collections.QuadTree;
import org.matsim.webvis.frameAnimation.constants.Params;
import org.matsim.webvis.frameAnimation.contracts.SnapshotRequest;
import org.matsim.webvis.frameAnimation.data.MatsimDataProvider;

import java.io.IOException;

public class SnapshotRequestHandler extends AbstractPostRequestHandler<SnapshotRequest> {

    public SnapshotRequestHandler(MatsimDataProvider data) {
        super(SnapshotRequest.class, data);
    }

    @Override
    public Answer process(SnapshotRequest body) {

        QuadTree.Rect bounds = body.getBounds().copyToMatsimRect();
        double startTime = body.getFromTimestep();
        int size = body.getSize();
        double speedFactor = body.getSpeedFactor();

        byte[] bytes;
        try {
            bytes = dataProvider.getSnapshots(bounds, startTime, size, speedFactor);
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
