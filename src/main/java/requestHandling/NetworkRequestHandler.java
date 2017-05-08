package requestHandling;

import constants.Params;
import contracts.RectContract;
import data.MatsimDataProvider;
import org.matsim.core.utils.collections.QuadTree;

import java.io.IOException;

public class NetworkRequestHandler extends AbstractPostRequestHandler<RectContract> {

    public NetworkRequestHandler(MatsimDataProvider dataProvider) {

        super(RectContract.class, dataProvider);
    }

    @Override
    public Answer process(RectContract body) {
        QuadTree.Rect bounds = body.copyToMatsimRect();

        byte[] bytes;

        try {
            bytes = dataProvider.getLinks(bounds);
        } catch (IOException e) {
            e.printStackTrace();
            return new Answer(Params.STATUS_INTERNAL_SERVER_ERROR, "Sorry");
        }
        return Answer.ok(bytes);
    }
}
