package requestHandling;

import com.google.gson.Gson;
import contracts.ConfigurationResponse;
import contracts.RectContract;
import data.MatsimDataProvider;

public class ConfigurationRequestHandler extends AbstractPostRequestHandler<Object> {

    public ConfigurationRequestHandler(MatsimDataProvider data) {
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
