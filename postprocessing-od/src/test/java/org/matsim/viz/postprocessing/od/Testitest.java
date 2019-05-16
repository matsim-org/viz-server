package org.matsim.viz.postprocessing.od;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Testitest {

	@Test
	public void readGeoJson() throws IOException {

		Path geoJson = Paths.get("C:\\Users\\Janek\\Desktop\\geojson test.geojson");
		InputStream stream = Files.newInputStream(geoJson);

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JtsModule());
		FeatureCollection featureCollection = mapper.readValue(geoJson.toFile(), FeatureCollection.class);


	}
}
