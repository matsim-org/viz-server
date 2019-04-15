package org.matsim.viz.files.visualization;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
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
	private String title;
    @NotEmpty
    private Map<String, String> inputFiles;
    @NotNull
    private Map<String, String> inputParameters;
	@NotNull
	private String[] tagIds = new String[0];
	@NotNull
	private Map<String, String> properties = new HashMap<>();
	@NotEmpty
	private String thumbnail = "";
}
