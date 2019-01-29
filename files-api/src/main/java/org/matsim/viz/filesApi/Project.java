package org.matsim.viz.filesApi;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.matsim.viz.database.AbstractEntity;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Project extends AbstractEntity {

    private String name;
}
