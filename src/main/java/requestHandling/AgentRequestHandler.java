package requestHandling;

import constants.Params;
import contracts.AgentRequest;
import data.MatsimDataProvider;
import org.matsim.core.utils.collections.QuadTree;

import java.io.IOException;

public class AgentRequestHandler extends AbstractPostRequestHandler<AgentRequest> {

    public AgentRequestHandler(MatsimDataProvider data) {
        super(AgentRequest.class, data);
    }

    @Override
    public Answer process(AgentRequest body) {

        QuadTree.Rect bounds = body.getBounds().copyToMatsimRect();
        double startTime = body.getFromTimestep();
        int size = body.getSize();

        byte[] bytes;
        try {
            bytes = dataProvider.getSnapshots(bounds, startTime, size);
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
