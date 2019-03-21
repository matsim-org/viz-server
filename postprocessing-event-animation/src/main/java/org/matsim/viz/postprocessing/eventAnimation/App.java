package org.matsim.viz.postprocessing.eventAnimation;

import io.dropwizard.Application;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.matsim.viz.postprocessing.bundle.*;

public class App extends Application<PostprocessingConfiguration> {

	private HibernateBundle<PostprocessingConfiguration> hibernate = new HibernateBundle<PostprocessingConfiguration>(
			Agent.class, Permission.class, FetchInformation.class
	) {
		@Override
		public PooledDataSourceFactory getDataSourceFactory(PostprocessingConfiguration postprocessingConfiguration) {
			// executeDatabaseMigration(appConfiguration);
			return postprocessingConfiguration.getDatabase();
		}
	};

	public static void main(String[] args) throws Exception {
		new App().run(args);
	}

	@Override
	public void initialize(Bootstrap<PostprocessingConfiguration> bootstrap) {

		bootstrap.addBundle(hibernate);
		bootstrap.addBundle(new PostprocessingBundle<>(hibernate, null, "event-animation"));
	}

	@Override
	public void run(PostprocessingConfiguration postprocessingConfiguration, Environment environment) throws Exception {

	}
}
