package org.matsim.viz.filesApi;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.matsim.viz.database.AbstractEntity;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VisualizationInput extends AbstractEntity {

    private String inputKey;
    private FileEntry fileEntry;
}
