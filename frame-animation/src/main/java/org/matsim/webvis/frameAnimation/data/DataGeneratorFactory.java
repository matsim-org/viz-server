package org.matsim.webvis.frameAnimation.data;

import org.matsim.webvis.frameAnimation.entities.Visualization;

class DataGeneratorFactory {

    DataGenerator createGenerator(Visualization viz) {
        return new DataGenerator(viz);
    }
}
