package org.matsim.viz.postprocessing.od;

import io.dropwizard.Application;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.matsim.viz.postprocessing.bundle.*;

import java.nio.file.Path;
import java.nio.file.Paths;

public class App extends Application<AppConfiguration> {

	private HibernateBundle<PostprocessingConfiguration> hibernate = new HibernateBundle<PostprocessingConfiguration>(
			Agent.class, Permission.class, Visualization.class, FetchInformation.class, ODRelation.class
	) {
		@Override
		public PooledDataSourceFactory getDataSourceFactory(PostprocessingConfiguration appConfiguration) {

			// TODO add some database migration
			return appConfiguration.getDatabase();
		}
	};
	private DataGenerator dataGenerator;


	public static void main(String[] args) throws Exception {
		new App().run(args);
	}

	@Override
	public void initialize(Bootstrap<AppConfiguration> bootstrap) {

		dataGenerator = new DataGenerator();
		bootstrap.addBundle(hibernate);
		bootstrap.addBundle(new PostprocessingBundle<>(hibernate, dataGenerator, "od"));
	}

	@Override
	public void run(AppConfiguration appConfiguration, Environment environment) {

		Path geoJsonPath = Paths.get(appConfiguration.getGeoJsonFiles());
		dataGenerator.setGeoJsonFolder(geoJsonPath);
		environment.jersey().register(new VisualizationResource(hibernate.getSessionFactory(), geoJsonPath));
	}
}
