package org.matsim.webvis.frameAnimation.requestHandling;

import com.google.gson.Gson;
import org.matsim.webvis.frameAnimation.contracts.ConfigurationResponse;
import org.matsim.webvis.frameAnimation.contracts.RectContract;
import org.matsim.webvis.frameAnimation.data.SimulationData;

public class ConfigurationRequestHandler extends AbstractPostRequestHandler<Object> {

    public ConfigurationRequestHandler(SimulationData data) {
        super(Object.class, data);
    }

    @Override
    public Answer process(Object body) {

        RectContract bounds = dataProvider.getBounds();
        double timestepSize = dataProvider.getTimestepSize();
        double firstTimestep = dataProvider.getFirstTimestep();
        double lastTimestep = dataProvider.getLastTimestep();
        ConfigurationResponse response = new ConfigurationResponse(bounds, firstTimestep, lastTimestep,
                timestepSize);
        String result = new Gson().toJson(response);
        return Answer.ok(result);
    }
}
