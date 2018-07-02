package org.matsim.webvis.files;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import org.matsim.webvis.files.config.AppConfiguration;

public class App extends Application<AppConfiguration> {


    public static void main(String[] args) throws Exception {
        new App().run(args);
    }

    @Override
    public void run(AppConfiguration configuration, Environment environment) {

        AppConfiguration.setInstance(configuration);
    }
}
