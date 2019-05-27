package org.matsim.viz.postprocessing.od;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.geotools.referencing.CRS;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.viz.postprocessing.bundle.InputFile;
import org.matsim.viz.postprocessing.bundle.VisualizationGenerator;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

import java.io.IOException;

public class DataGenerator implements VisualizationGenerator<Visualization> {

	private static final String ZONES_KEY = "Zones";
	private static final String NETWORK_KEY = "Network";
	private static final String EVENTS_Key = "Events";

	private static ObjectMapper objectMapper = new ObjectMapper().registerModule(new JtsModule());
	private static CoordinateReferenceSystem wgs84;

	public DataGenerator() {
		if (wgs84 == null) {
			try {
				CRS.decode("urn:ogc:def:crs:EPSG:3857");
			} catch (FactoryException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public Visualization createVisualization() {
		return new Visualization();
	}

	@Override
	public void generate(Input<Visualization> input) {

		if (!isValidInput(input)) {
			throw new RuntimeException("Input was not valid");
		}


		try {
			// get the cells
			InputFile zonesfile = input.getInputFiles().get(ZONES_KEY);
			FeatureCollection zonesCollection = objectMapper.readValue(zonesfile.getPath().toFile(), FeatureCollection.class);
			FeatureCollection transformedCollection = zonesCollection.transformCollection(wgs84);

			// get the network
			Network network = NetworkUtils.createNetwork();
			new MatsimNetworkReader(network).readFile(input.getInputFiles().get(NETWORK_KEY).getPath().toString());

			// set up event handler


		} catch (IOException e) {
			e.printStackTrace();
		} catch (FactoryException e) {
			e.printStackTrace();
		} catch (TransformException e) {
			e.printStackTrace();
		}
	}

	private boolean isValidInput(Input<Visualization> input) {

		return input.getInputFiles().containsKey(ZONES_KEY) &&
				input.getInputFiles().containsKey(EVENTS_Key) &&
				input.getInputFiles().containsKey(NETWORK_KEY);

	}
}
