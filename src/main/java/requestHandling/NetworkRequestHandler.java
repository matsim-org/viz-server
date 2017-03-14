package requestHandling;

import com.google.gson.Gson;
import contracts.LinkContract;
import contracts.RectContract;
import data.MatsimDataProvider;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.utils.collections.QuadTree;

import java.util.ArrayList;
import java.util.Collection;

public class NetworkRequestHandler extends AbstractPostRequestHandler<RectContract> {

    public NetworkRequestHandler(MatsimDataProvider dataProvider) {

        super(RectContract.class, dataProvider);
    }

    @Override
    public Answer process(RectContract body) {
        QuadTree.Rect bounds = body.copyToMatsimRect();
        Collection<Link> links = dataProvider.getLinks(bounds);
        Collection<LinkContract> contracts = new ArrayList<>(links.size());

        for (Link link : links) {
            contracts.add(new LinkContract(link));
        }
        String result = new Gson().toJson(contracts);
        return Answer.ok(result);
    }
}
