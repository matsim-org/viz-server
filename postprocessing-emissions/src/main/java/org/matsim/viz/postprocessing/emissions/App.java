package org.matsim.viz.postprocessing.emissions;

import io.dropwizard.Application;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import lombok.extern.java.Log;
import org.flywaydb.core.Flyway;
import org.matsim.viz.postprocessing.bundle.*;

@Log
public class App extends Application<PostprocessingConfiguration> {

    private HibernateBundle<PostprocessingConfiguration> hibernate = new HibernateBundle<PostprocessingConfiguration>(
            Agent.class, Permission.class, Bin.class, Visualization.class, FetchInformation.class
    ) {
        @Override
        public PooledDataSourceFactory getDataSourceFactory(PostprocessingConfiguration appConfiguration) {

            executeDatabaseMigration(appConfiguration);
            return appConfiguration.getDatabase();
        }
    };

    public static void main(String[] args) throws Exception {
        new App().run(args);
    }

    @Override
    public void initialize(Bootstrap<PostprocessingConfiguration> bootstrap) {

        bootstrap.addBundle(hibernate);
        bootstrap.addBundle(new PostprocessingBundle<>(hibernate, new DataGenerator(), "emissions"));
    }

    @Override
    public void run(PostprocessingConfiguration configuration, Environment environment) {

        environment.jersey().register(new VisualizationResource(hibernate.getSessionFactory()));
    }

    @SuppressWarnings("Duplicates")
    private void executeDatabaseMigration(PostprocessingConfiguration configuration) {

        if (!configuration.getDatabase().getDriverClass().equals("org.h2.Driver")) {
            // execute schema migration with flyway before connecting to the database
            // if H2 in memory database is used, this is not necessary
            Flyway flyway = Flyway.configure().dataSource(
                    configuration.getDatabase().getUrl(),
                    configuration.getDatabase().getUser(),
                    configuration.getDatabase().getPassword()
            ).load();
            flyway.migrate();
        }
    }
}
