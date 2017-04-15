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

        Configuration.Builder config = Configuration.newBuilder();
        config.setId("some id")
                .setBounds(dataProvider.getBounds())
                .setFirstTimestep(dataProvider.getFirstTimestep())
                .setLastTimestep(dataProvider.getLastTimestep())
                .setTimestepSize(dataProvider.getTimestepSize());
        return Answer.ok(config.build());
    }
}
