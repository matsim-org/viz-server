package requestHandling;

import constants.Params;
import contracts.RectContract;
import data.MatsimDataProvider;
import org.matsim.core.utils.collections.QuadTree;

import java.io.IOException;

public class NetworkRequestHandler extends AbstractPostRequestHandler<Object> {

    public NetworkRequestHandler(MatsimDataProvider dataProvider) {

        super(Object.class, dataProvider);
    }

    @Override
    public Answer process(Object body) {

        byte[] bytes;

        try {
            bytes = dataProvider.getLinks();
        } catch (IOException e) {
            e.printStackTrace();
            return new Answer(Params.STATUS_INTERNAL_SERVER_ERROR, "Sorry");
        }
        return Answer.ok(bytes);
    }
}
