package org.matsim.viz.frameAnimation.data;

import org.matsim.viz.frameAnimation.entities.Visualization;

class DataGeneratorFactory {

    DataGenerator createGenerator(Visualization viz) {
        return new DataGenerator(viz);
    }
}
