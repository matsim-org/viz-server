package requestHandling;

import com.google.gson.GsonBuilder;
import constants.Params;
import contracts.PlanRequest;
import contracts.geoJSON.Feature;
import contracts.geoJSON.FeatureCollection;
import contracts.geoJSON.FeatureSerializer;
import data.MatsimDataProvider;

public class PlanRequestHandler extends AbstractPostRequestHandler<PlanRequest>{

    public PlanRequestHandler(MatsimDataProvider data) {
        super(PlanRequest.class, data);
    }

    @Override
    public Answer process(PlanRequest body) {

        FeatureCollection result;
        try {
            result = dataProvider.getPlan(body.getIdIndex());
        } catch (RuntimeException e) {
            return new Answer(Params.STATUS_BADREQUEST, "Timestep or index for timestep not available");
        }
        String json = new GsonBuilder().registerTypeAdapter(Feature.class, new FeatureSerializer()).
                create().toJson(result);
        return Answer.ok(json);
    }
}
