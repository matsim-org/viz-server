package org.matsim.viz.postprocessing.bundle;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.matsim.viz.filesApi.VisualizationParameter;

import java.util.Map;


public interface VisualizationGenerator<T extends PersistentVisualization> {

    T createVisualization();

    void generate(Input<T> input);

    @RequiredArgsConstructor
    @Getter
    class Input<T> {
        private final T visualization;
        private final Map<String, InputFile> inputFiles;
        private final Map<String, VisualizationParameter> params;
        private final Session session;
    }
}


