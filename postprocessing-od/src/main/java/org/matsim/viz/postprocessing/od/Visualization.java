package org.matsim.viz.postprocessing.od;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.matsim.viz.postprocessing.bundle.PersistentVisualization;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Visualization extends PersistentVisualization {

	@OneToMany(mappedBy = "visualization", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<ODRelation> odRelations = new HashSet<>();

	void addRelation(ODRelation relation) {
		this.odRelations.add(relation);
		relation.setVisualization(this);
	}
}
