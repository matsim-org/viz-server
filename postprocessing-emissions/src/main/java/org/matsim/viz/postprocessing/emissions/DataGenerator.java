package org.matsim.viz.postprocessing.emissions;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.val;
import org.hibernate.SessionFactory;
import org.matsim.contrib.emissions.analysis.EmissionGridAnalyzer;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.viz.filesApi.VisualizationParameter;
import org.matsim.viz.postprocessing.bundle.InputFile;
import org.matsim.viz.postprocessing.bundle.VisualizationGenerator;
import org.matsim.viz.postprocessing.emissions.persistenceModel.Visualization;

import java.util.Map;

@Log
@RequiredArgsConstructor
public class DataGenerator implements VisualizationGenerator<Visualization> {

    private static final String NETWORK_KEY = "network";
    private static final String EVENTS_KEY = "events";
    private static final String CELL_SIZE_KEY = "cell-size";
    private static final String SMOOTHING_RADIUS_KEY = "smoothing-radius";
    private static final String TIME_BIN_SIZE_KEY = "time-bin-size";

    private final SessionFactory sessionFactory;

    @Override
    public Visualization createVisualization() {
        return new Visualization();
    }

    @Override
    public void generate(Visualization visualization, Map<String, InputFile> inputFiles, Map<String, VisualizationParameter> parameter) {

        try (val session = sessionFactory.openSession()) {

            val mergedViz = (Visualization) session.merge(visualization);
            session.beginTransaction();
            mergedViz.setCellSize(Double.parseDouble(parameter.get(CELL_SIZE_KEY).getValue()));
            mergedViz.setSmoothingRadius(Double.parseDouble(parameter.get(SMOOTHING_RADIUS_KEY).getValue()));
            mergedViz.setTimeBinSize(Double.parseDouble(parameter.get(TIME_BIN_SIZE_KEY).getValue()));
            session.getTransaction().commit();

            val network = NetworkUtils.createNetwork();
            new MatsimNetworkReader(network).readFile(inputFiles.get(NETWORK_KEY).toString());

            val analyzer = new EmissionGridAnalyzer.Builder()
                    .withTimeBinSize(visualization.getTimeBinSize())
                    .withGridSize(visualization.getCellSize())
                    .withSmoothingRadius(visualization.getSmoothingRadius())
                    .withGridType(EmissionGridAnalyzer.GridType.Hexagonal)
                    .withNetwork(network)
                    .build();

            log.info("Start processing emissions. This may take some while.");
            val json = analyzer.processToJsonString(inputFiles.get(EVENTS_KEY).toString());

            log.info("Finished processing emissions. Write result to database");

            session.beginTransaction();
            mergedViz.setData(json);
            session.getTransaction().commit();
        }
    }
}
