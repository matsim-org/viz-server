package org.matsim.viz.files.config;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import org.matsim.viz.database.DbConfiguration;

@Getter
@JsonTypeName("mariaDB")
public class MariaDbConfigurationFactory implements DbConfigurationFactory {

    private String user = "";
    private String password = "";
    private String url = "";
    private String hbm2ddl = "validate";
    private boolean printSql = false;

    @Override
    public DbConfiguration createConfiguration() {
        DbConfiguration config = new DbConfiguration();
        config.setDriverClass("org.mariadb.jdbc.Driver");
        config.setUser(user);
        config.setPassword(password);
        config.setJdbcUrl(url);
        config.setHbm2ddl(hbm2ddl);
        config.setPrintSql(printSql);
        return config;
    }
}
