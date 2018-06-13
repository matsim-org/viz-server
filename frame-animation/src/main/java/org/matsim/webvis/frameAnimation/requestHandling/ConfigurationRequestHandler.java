package org.matsim.webvis.frameAnimation.requestHandling;

import com.google.gson.Gson;
import org.matsim.webvis.frameAnimation.contracts.ConfigurationResponse;
import org.matsim.webvis.frameAnimation.contracts.RectContract;
import org.matsim.webvis.frameAnimation.contracts.VisualizationRequest;

public class ConfigurationRequestHandler extends AbstractPostRequestHandler<VisualizationRequest> {


    public ConfigurationRequestHandler() {
        super(VisualizationRequest.class);
    }

    @Override
    public Answer process(VisualizationRequest body) {

        RectContract bounds = getData().getBounds(body.getId());
        double timestepSize = getData().getTimestepSize(body.getId());
        double firstTimestep = getData().getFirstTimestep(body.getId());
        double lastTimestep = getData().getLastTimestep(body.getId());
        ConfigurationResponse response = new ConfigurationResponse(bounds, firstTimestep, lastTimestep,
                timestepSize);
        String result = new Gson().toJson(response);
        return Answer.ok(result);
    }
}
