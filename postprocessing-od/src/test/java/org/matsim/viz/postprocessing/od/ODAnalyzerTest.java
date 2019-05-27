package org.matsim.viz.postprocessing.od;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.junit.Test;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ODAnalyzerTest {

    @Test
    public void testRun() throws IOException {

        Path networkPath = Paths.get("G:\\Users\\Janek\\tubcloud\\output_network.xml.gz");
        Path eventsPath = Paths.get("G:\\Users\\Janek\\tubcloud\\output_events.xml.gz");
        Path geoJson = Paths.get("G:\\Users\\Janek\\tubcloud\\geojson test.geojson");

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JtsModule());
        FeatureCollection featureCollection = mapper.readValue(geoJson.toFile(), FeatureCollection.class);

        Network network = NetworkUtils.createNetwork();
        new MatsimNetworkReader(network).readFile(networkPath.toString());

        ODAnalyzer analyzer = new ODAnalyzer(eventsPath, network, featureCollection);
        val result = analyzer.run();

        System.out.println("result");
    }
}
