package org.matsim.viz.files.visualization;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
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

	private String[] tagIds = new String[0];
	private String properties = "";
	private String thumbnail = "";
}
