package org.matsim.viz.files.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.dropwizard.jackson.Discoverable;
import org.matsim.viz.database.DbConfiguration;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public interface DbConfigurationFactory extends Discoverable {

    DbConfiguration createConfiguration();
}
