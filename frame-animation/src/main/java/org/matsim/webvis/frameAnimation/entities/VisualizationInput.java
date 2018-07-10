package org.matsim.webvis.frameAnimation.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.matsim.webvis.database.AbstractEntity;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VisualizationInput extends AbstractEntity {

    private String key;
    private FileEntry fileEntry;
}
