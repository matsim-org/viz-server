package org.matsim.viz.postprocessing.bundle;

import org.matsim.viz.filesApi.VisualizationParameter;

import java.util.Map;


public interface VisualizationGenerator<T extends PersistentVisualization> {

    T createVisualization();

    void generate(T visualization, Map<String, InputFile> inputFiles, Map<String, VisualizationParameter> parameter);
}
