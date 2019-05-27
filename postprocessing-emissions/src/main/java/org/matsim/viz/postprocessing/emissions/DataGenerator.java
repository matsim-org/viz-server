package org.matsim.viz.postprocessing.emissions;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.val;
import org.matsim.contrib.emissions.analysis.EmissionGridAnalyzer;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.utils.collections.Tuple;
import org.matsim.viz.postprocessing.bundle.VisualizationGenerator;

@Log
@RequiredArgsConstructor
public class DataGenerator implements VisualizationGenerator<Visualization> {

    private static final String NETWORK_KEY = "Network";
    private static final String EVENTS_KEY = "Events";
    private static final String CELL_SIZE_KEY = "Cell size";
    private static final String SMOOTHING_RADIUS_KEY = "Smoothing radius";
    private static final String TIME_BIN_SIZE_KEY = "Time bin size";

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
        new MatsimNetworkReader(network).readFile(input.getInputFiles().get(NETWORK_KEY).getPath().toString());

        val analyzer = new EmissionGridAnalyzer.Builder()
                .withTimeBinSize(input.getVisualization().getTimeBinSize())
                .withGridSize(input.getVisualization().getCellSize())
                .withSmoothingRadius(input.getVisualization().getSmoothingRadius())
                .withGridType(EmissionGridAnalyzer.GridType.Hexagonal)
                .withNetwork(network)
                .build();

        log.info("Start processing emissions. This may take a while.");

        analyzer.processTimeBinsWithEmissions(input.getInputFiles().get(EVENTS_KEY).getPath().toString());

        // all of the bins need to be successful, or the transaction should fail.
        session.beginTransaction();

        while (analyzer.hasNextTimeBin()) {
            Tuple<Double, String> result = analyzer.processNextTimeBin();
            log.info("-- JSON size: " + result.getSecond().length());

            Bin bin = new Bin();
            bin.setStartTime(result.getFirst());
            bin.setData(result.getSecond());
            mergedViz.addBin(bin);
        }

        session.getTransaction().commit();

        log.info("Finished committing database transactions");
    }
}
