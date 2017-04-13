package requestHandling;

import contracts.ConfigurationRequest;
import data.MatsimDataProvider;

import static org.matsim.webvis.contracts.Contracts.Configuration;
import static org.matsim.webvis.contracts.Contracts.Rect;

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
        Rect rect = Rect.newBuilder().setLeft(1).setRight(2).setTop(1).setBottom(2).build();
        config.setId("some id")
                .setBounds(rect)
                .setFirstTimestep(1)
                .setLastTimestep(10)
                .setTimestepSize(1);

        byte[] result = config.build().toByteArray();
        return Answer.ok(result);
    }
}
