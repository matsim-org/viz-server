package requestHandling;

import contracts.ConfigurationRequest;
import data.MatsimDataProvider;

import static org.matsim.webvis.contracts.Contracts.Configuration;

public class ConfigurationRequestHandler extends AbstractPostRequestHandler<ConfigurationRequest> {

    public ConfigurationRequestHandler(MatsimDataProvider data) {
        super(ConfigurationRequest.class, data);
    }

    @Override
    public Answer process(ConfigurationRequest body) {
        /*RectContract bounds = dataProvider.getBounds();
        double timestepSize = dataProvider.getTimestepSize();
        double firstTimestep = dataProvider.getFirstTimestep();
        double lastTimestep = dataProvider.getLastTimestep();
        ConfigurationResponse response = new ConfigurationResponse(body.getId(), bounds, firstTimestep, lastTimestep,
                timestepSize);
        String result = new Gson().toJson(response);
        */

        //create a test repsonse with protobuf

        Configuration.Builder config = Configuration.newBuilder();
        config.setId("some id")
                .setBounds(dataProvider.getBounds())
                .setFirstTimestep(dataProvider.getFirstTimestep())
                .setLastTimestep(dataProvider.getLastTimestep())
                .setTimestepSize(dataProvider.getTimestepSize());

        byte[] result = config.build().toByteArray();
        return Answer.ok(result);
    }
}
