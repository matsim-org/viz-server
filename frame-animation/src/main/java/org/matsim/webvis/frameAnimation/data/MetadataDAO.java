package org.matsim.webvis.frameAnimation.data;

import org.matsim.webvis.database.AbstractEntity;
import org.matsim.webvis.frameAnimation.entities.Visualization;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

class MetadataDAO {

    //index of visualizations, could change to some database later
    private static Map<String, Visualization> data = new HashMap<>();

    void persistVisualizations(Collection<Visualization> visualizations) {
        data.putAll(visualizations.stream().collect(Collectors.toMap(AbstractEntity::getId, viz -> viz)));
    }

    Visualization find(String vizId) {
        return data.get(vizId);
    }
}
