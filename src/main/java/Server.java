import constants.Params;
import constants.Path;
import data.MatsimDataProvider;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.utils.collections.QuadTree;

import java.util.Collection;

import static spark.Spark.get;
import static spark.Spark.halt;

public class Server {

    private static MatsimDataProvider data;

    public static void main(String[] args) {

        initializeData();
        initializeRoutes();
    }

    private static void initializeData() {

        //this should be configurable through command line args or a config file
        data = new MatsimDataProvider("src/test/data/two-routes-test-network.xml", "");
    }

    private static void initializeRoutes() {

        get(Path.NETWORK, (request, response) -> {

            //get the parameters
            double left = 0;
            double right = 0;
            double top = 0;
            double bottom = 0;

            try {
                left = Double.parseDouble(request.queryParams(Params.BOUNDINGBOX_LEFT));
                right = Double.parseDouble(request.queryParams(Params.BOUNDINGBOX_RIGHT));
                top = Double.parseDouble(request.queryParams(Params.BOUNDINGBOX_TOP));
                bottom = Double.parseDouble(request.queryParams(Params.BOUNDINGBOX_BOTTOM));
            } catch (Exception e) {
                //This, especially the message, should probably go somewhere else
                halt(400, "The parameters should have the following format: left=<double>&right=<double>&top=<double>&bottom=<double>");
            }
            QuadTree.Rect bounds = new QuadTree.Rect(left, top, right, bottom);
            Collection<Link> result = data.getLinks(bounds);

            return "There are " + result.size() + " links within the requested bounds";
        });
    }
}
