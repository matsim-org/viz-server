package org.matsim.webvis.frameAnimation.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.matsim.webvis.database.AbstractEntity;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FileEntry extends AbstractEntity {

    private String userFileName;
    private String contentType;
    private long sizeInBytes;
    private Project project;
}
