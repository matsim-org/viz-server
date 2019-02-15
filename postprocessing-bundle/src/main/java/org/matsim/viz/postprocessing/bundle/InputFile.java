package org.matsim.viz.postprocessing.bundle;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.file.Path;

@AllArgsConstructor
@Getter
public class InputFile {

    private String key;
    private Path path;
}
