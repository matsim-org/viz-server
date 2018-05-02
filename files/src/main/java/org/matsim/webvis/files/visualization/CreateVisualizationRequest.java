package org.matsim.webvis.files.visualization;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
class CreateVisualizationRequest {

    private String projectId;
    private String typeKey;
    private Map<String, String> inputFiles;
    private Map<String, String> inputParameters;
}
