package requestHandling;

import contracts.RectContract;
import data.MatsimDataProvider;
import org.matsim.core.utils.collections.QuadTree;
import org.matsim.webvis.contracts.Contracts;

import java.util.Collection;

public class NetworkRequestHandler extends AbstractPostRequestHandler<RectContract> {

    public NetworkRequestHandler(MatsimDataProvider dataProvider) {

        super(RectContract.class, dataProvider);
    }

    @Override
    public Answer process(RectContract body) {
        QuadTree.Rect bounds = body.copyToMatsimRect();
        Collection<Contracts.Link> links = dataProvider.getLinks(bounds);

        Contracts.Network network = Contracts.Network.newBuilder()
                .addAllLinks(links).build();
        return Answer.ok(network);
    }
}
