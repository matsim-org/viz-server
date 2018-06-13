package org.matsim.webvis.frameAnimation.requestHandling;

import com.google.gson.GsonBuilder;
import org.matsim.webvis.frameAnimation.constants.Params;
import org.matsim.webvis.frameAnimation.contracts.PlanRequest;
import org.matsim.webvis.frameAnimation.contracts.geoJSON.Feature;
import org.matsim.webvis.frameAnimation.contracts.geoJSON.FeatureCollection;
import org.matsim.webvis.frameAnimation.contracts.geoJSON.FeatureSerializer;

public class PlanRequestHandler extends AbstractPostRequestHandler<PlanRequest>{

    public PlanRequestHandler() {
        super(PlanRequest.class);
    }

    @Override
    public Answer process(PlanRequest body) {

        FeatureCollection result;
        try {
            result = getData().getPlan(body.getId(), body.getIdIndex());
        } catch (RuntimeException e) {
            System.out.println("Error in PlanRequestHandler: requestedIndexId: " + body.getIdIndex());
            System.out.println(e.getMessage());
            return new Answer(Params.STATUS_BADREQUEST, "Timestep or index for timestep not available");
        }
        String json = new GsonBuilder().registerTypeAdapter(Feature.class, new FeatureSerializer()).
                create().toJson(result);
        return Answer.ok(json);
    }
}
