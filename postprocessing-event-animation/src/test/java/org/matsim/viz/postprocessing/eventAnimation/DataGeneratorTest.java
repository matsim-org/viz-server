package org.matsim.viz.postprocessing.eventAnimation;

import io.dropwizard.testing.junit.DAOTestRule;
import lombok.val;
import org.junit.Rule;
import org.junit.Test;
import org.matsim.viz.postprocessing.bundle.Agent;
import org.matsim.viz.postprocessing.bundle.InputFile;
import org.matsim.viz.postprocessing.bundle.Permission;
import org.matsim.viz.postprocessing.bundle.VisualizationGenerator;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import static org.junit.Assert.assertFalse;

public class DataGeneratorTest {

	@Rule
	public DAOTestRule database = DAOTestRule.newBuilder()
			.addEntityClass(Visualization.class)
			.addEntityClass(MatsimNetwork.class)
			.addEntityClass(LinkTrip.class)
			.addEntityClass(Agent.class)
			.addEntityClass(Permission.class)
			.setShowSql(true)
			.build();

	@Test
	public void generate() {

		val generator = new DataGenerator();
		val viz = generator.createVisualization();

		val inputFiles = new HashMap<String, InputFile>();
		inputFiles.put("network", new InputFile("network", getResourcePath("test-network.xml")));
		inputFiles.put("events", new InputFile("events", getResourcePath("test-events-100.xml.gz")));
		val input = new VisualizationGenerator.Input<Visualization>(viz, inputFiles, new HashMap<>(), database.getSessionFactory());

		generator.generate(input);

		assertFalse(true);

	}

	private Path getResourcePath(String filename) {
		try {
			return Paths.get(this.getClass().getClassLoader().getResource(filename).toURI());
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
}
