package org.matsim.viz.files.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(indexes = {@Index(columnList = "type")})
public class Visualization extends Taggable {

    private String type;

    private String title;

    @Lob
    private String thumbnail;

    // This will create a separate table which is linked automatically
    @ElementCollection
    @Column(name = "value", length = 10000)
    private Map<String, String> properties = new HashMap<>();

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
