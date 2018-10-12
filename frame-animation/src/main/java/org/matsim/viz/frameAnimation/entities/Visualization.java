package org.matsim.viz.frameAnimation.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.matsim.viz.database.AbstractEntity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Visualization extends AbstractEntity {

    private Project project;
    private Set<Permission> permissions = new HashSet<>();
    private Map<String, VisualizationInput> inputFiles = new HashMap<>();
    private Map<String, VisualizationParameter> parameters = new HashMap<>();
}
