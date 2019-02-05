package org.matsim.viz.postprocessing.bundle;

import org.matsim.viz.filesApi.VisualizationParameter;

import java.util.Map;

@FunctionalInterface
public interface VisualizationGenerator {

    void generate(PersistentVisualization visualization, Map<String, InputFile> inputFiles, Map<String, VisualizationParameter> parameter);
}
