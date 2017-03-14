package requestHandling;

import com.google.gson.Gson;
import contracts.AgentRequest;
import contracts.SnapshotContract;
import data.MatsimDataProvider;
import org.matsim.core.utils.collections.QuadTree;

public class AgentRequestHandler extends AbstractPostRequestHandler<AgentRequest> {

    public AgentRequestHandler(MatsimDataProvider data) {
        super(AgentRequest.class, data);
    }

    @Override
    public Answer process(AgentRequest body) {

        QuadTree.Rect bounds = body.getBounds().copyToMatsimRect();
        SnapshotContract snapshot = dataProvider.getSnapshot(bounds, body.getTime());
        String result = new Gson().toJson(snapshot);
        return Answer.ok(result);
    }
}
