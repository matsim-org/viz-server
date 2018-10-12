package org.matsim.viz.frameAnimation.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.matsim.viz.database.AbstractEntity;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FileEntry extends AbstractEntity {

    @Setter
    private String userFileName;
    private String contentType;
    private long sizeInBytes;
    private Project project;
}
