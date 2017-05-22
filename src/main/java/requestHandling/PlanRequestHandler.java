package requestHandling;

import com.google.gson.Gson;
import constants.Params;
import contracts.PlanRequest;
import contracts.geoJSON.FeatureCollection;
import data.MatsimDataProvider;

public class PlanRequestHandler extends AbstractPostRequestHandler<PlanRequest>{

    public PlanRequestHandler(MatsimDataProvider data) {
        super(PlanRequest.class, data);
    }

    @Override
    public Answer process(PlanRequest body) {

        FeatureCollection result;
        try {
            result = dataProvider.getPlan(body.getTimestep(), body.getIndex());
        } catch (RuntimeException e) {
            return new Answer(Params.STATUS_BADREQUEST, e.getMessage());
        }
        String json = new Gson().toJson(result);
        return Answer.ok(json);
    }
}
