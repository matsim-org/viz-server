package org.matsim.viz.files.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.matsim.viz.database.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import java.net.URI;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class VisualizationType extends AbstractEntity {

    @Column(unique = true)
    private String typeName;
    private boolean requiresProcessing;
    private URI endpoint;

    @ElementCollection(fetch = FetchType.LAZY)
    private Set<String> requiredFileKeys;

    @ElementCollection(fetch = FetchType.LAZY)
    private Set<String> requiredParamKeys;
}
