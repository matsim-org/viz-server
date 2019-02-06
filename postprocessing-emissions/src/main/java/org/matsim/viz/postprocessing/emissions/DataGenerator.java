package org.matsim.viz.postprocessing.emissions;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.val;
import org.matsim.contrib.emissions.analysis.EmissionGridAnalyzer;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.viz.postprocessing.bundle.VisualizationGenerator;

@Log
@RequiredArgsConstructor
public class DataGenerator implements VisualizationGenerator<Visualization> {

    private static final String NETWORK_KEY = "network";
    private static final String EVENTS_KEY = "events";
    private static final String CELL_SIZE_KEY = "cell-size";
    private static final String SMOOTHING_RADIUS_KEY = "smoothing-radius";
    private static final String TIME_BIN_SIZE_KEY = "time-bin-size";

    @Override
    public Visualization createVisualization() {
        return new Visualization();
    }

    @Override
    public void generate(Input<Visualization> input) {

        val session = input.getSession();
        val mergedViz = (Visualization) session.merge(input.getVisualization());
        session.beginTransaction();
        mergedViz.setCellSize(Double.parseDouble(input.getParams().get(CELL_SIZE_KEY).getValue()));
        mergedViz.setSmoothingRadius(Double.parseDouble(input.getParams().get(SMOOTHING_RADIUS_KEY).getValue()));
        mergedViz.setTimeBinSize(Double.parseDouble(input.getParams().get(TIME_BIN_SIZE_KEY).getValue()));
        session.getTransaction().commit();

        val network = NetworkUtils.createNetwork();
        new MatsimNetworkReader(network).readFile(input.getInputFiles().get(NETWORK_KEY).toString());

        val analyzer = new EmissionGridAnalyzer.Builder()
                .withTimeBinSize(input.getVisualization().getTimeBinSize())
                .withGridSize(input.getVisualization().getCellSize())
                .withSmoothingRadius(input.getVisualization().getSmoothingRadius())
                .withGridType(EmissionGridAnalyzer.GridType.Hexagonal)
                .withNetwork(network)
                .build();

        log.info("Start processing emissions. This may take some while.");
        val json = analyzer.processToJsonString(input.getInputFiles().get(EVENTS_KEY).toString());

        log.info("Finished processing emissions. Write result to database");

        session.beginTransaction();
        mergedViz.setData(json);
        session.getTransaction().commit();
    }
}
