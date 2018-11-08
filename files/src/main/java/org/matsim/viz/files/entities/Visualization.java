package org.matsim.viz.files.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Entity
public class Visualization extends Resource {

    private String type;

    @ManyToOne(optional = false)
    private Project project;

    @OneToMany(mappedBy = "visualization", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Map<String, VisualizationInput> inputFiles = new HashMap<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Map<String, VisualizationParameter> parameters = new HashMap<>();

    public void addInput(VisualizationInput input) {
        this.inputFiles.put(input.getInputKey(), input);
        input.setVisualization(this);
    }

    public void addParameter(VisualizationParameter parameter) {
        this.parameters.put(parameter.getParameterKey(), parameter);
    }
}
