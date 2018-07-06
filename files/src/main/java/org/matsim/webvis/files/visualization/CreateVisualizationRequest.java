package org.matsim.webvis.files.visualization;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
class CreateVisualizationRequest {

    @NotEmpty
    private String projectId;
    @NotEmpty
    private String typeKey;
    @NotEmpty
    private Map<String, String> inputFiles;
    @NotEmpty
    private Map<String, String> inputParameters;
}
