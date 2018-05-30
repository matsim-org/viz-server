package org.matsim.webvis.frameAnimation.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.matsim.webvis.common.database.AbstractEntity;

@Getter
@AllArgsConstructor
@NoArgsConstructor
class VisualizationParameter extends AbstractEntity {

    private String key;
    private String value;
}
