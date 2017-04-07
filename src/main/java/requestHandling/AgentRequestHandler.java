package requestHandling;

import com.google.gson.Gson;
import constants.Params;
import contracts.AgentRequest;
import contracts.SnapshotContract;
import data.MatsimDataProvider;
import org.matsim.core.utils.collections.QuadTree;

import java.util.List;

public class AgentRequestHandler extends AbstractPostRequestHandler<AgentRequest> {

    public AgentRequestHandler(MatsimDataProvider data) {
        super(AgentRequest.class, data);
    }

    @Override
    public Answer process(AgentRequest body) {

        QuadTree.Rect bounds = body.getBounds().copyToMatsimRect();
        double startTime = body.getFromTimestep();
        int size = body.getSize();
        List<SnapshotContract> snapshots;

        try {
            snapshots = dataProvider.getSnapshot(bounds, startTime, size);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());
            return new Answer(Params.STATUS_BADREQUEST, e.getMessage());
        }
        String result = new Gson().toJson(snapshots);
        return Answer.ok(result);
    }
}
