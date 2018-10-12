package org.matsim.viz.files.config;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import org.matsim.viz.database.DbConfiguration;

@Getter
@JsonTypeName("h2")
public class H2DbConfigurationFactory implements DbConfigurationFactory {

    private boolean printSql = false;

    @Override
    public DbConfiguration createConfiguration() {
        DbConfiguration config = new DbConfiguration();
        config.setDriverClass("org.h2.Driver");
        config.setJdbcUrl("jdbc:h2:mem:db1;DB_CLOSE_DELAY=-1;MVCC=TRUE");
        config.setUser("sa");
        config.setPassword("");
        config.setHbm2ddl("create");
        config.setPrintSql(printSql);
        return config;
    }
}
