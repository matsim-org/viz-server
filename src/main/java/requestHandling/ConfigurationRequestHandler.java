package requestHandling;

import com.google.gson.Gson;
import contracts.ConfigurationRequest;
import contracts.ConfigurationResponse;
import contracts.RectContract;
import data.MatsimDataProvider;

public class ConfigurationRequestHandler extends AbstractPostRequestHandler<ConfigurationRequest> {

    public ConfigurationRequestHandler(MatsimDataProvider data) {
        super(ConfigurationRequest.class, data);
    }

    @Override
    public Answer process(ConfigurationRequest body) {
        RectContract bounds = dataProvider.getBounds();
        ConfigurationResponse response = new ConfigurationResponse(body.getId(), bounds);
        String result = new Gson().toJson(response);
        return Answer.ok(result);
    }
}
