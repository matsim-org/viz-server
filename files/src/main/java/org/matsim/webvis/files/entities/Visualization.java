package org.matsim.webvis.files.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.matsim.webvis.common.database.AbstractEntity;

import javax.persistence.*;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true, exclude = {"inputFiles", "parameters"})
@Entity
public class Visualization extends AbstractEntity {

    private String name;
    private URI backendService;

    @ManyToOne(optional = false)
    private Project project;

    @OneToMany(mappedBy = "visualization", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Map<String, VisualizationInput> inputFiles = new HashMap<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Map<String, VisualizationParameter> parameters = new HashMap<>();

    public void addInput(VisualizationInput input) {
        this.inputFiles.put(input.getKey(), input);
        input.setVisualization(this);
    }

    public void addParameter(VisualizationParameter parameter) {
        this.parameters.put(parameter.getKey(), parameter);
    }


}
