package requestHandling;

import com.google.gson.Gson;
import constants.Params;
import contracts.LinkContract;
import data.MatsimDataProvider;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.utils.collections.QuadTree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class NetworkRequestHandler extends AbstractGetGetRequestHandler {

    private MatsimDataProvider dataProvider;

    public NetworkRequestHandler(MatsimDataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    @Override
    protected Answer processImpl(Map<String, String[]> parameters) {

        QuadTree.Rect bounds;
        try {
            bounds = getBoundsFromParameters(parameters);
        } catch (Exception e) {
            return new Answer(Params.STATUS_BADREQUEST, "The query parameters were not properly formatted");
        }
        Collection<Link> links = dataProvider.getLinks(bounds);
        Collection<LinkContract> contracts = new ArrayList<>(links.size());

        for (Link link : links) {
            contracts.add(new LinkContract(link));
        }
        Gson gson = new Gson();
        return Answer.ok(gson.toJson(contracts));
    }

    private QuadTree.Rect getBoundsFromParameters(Map<String, String[]> parameters) throws Exception {

        double left = Double.parseDouble(parameters.get(Params.BOUNDINGBOX_LEFT)[0]);
        double right = Double.parseDouble(parameters.get(Params.BOUNDINGBOX_RIGHT)[0]);
        double top = Double.parseDouble(parameters.get(Params.BOUNDINGBOX_TOP)[0]);
        double bottom = Double.parseDouble(parameters.get(Params.BOUNDINGBOX_BOTTOM)[0]);

        return new QuadTree.Rect(left, top, right, bottom);
    }
}
