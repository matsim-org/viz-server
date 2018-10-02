package org.matsim.webvis.database;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class DbConfiguration {

    private Map<String, String> properties = new HashMap<>();

    public void setDriverClass(String value) {
        properties.put("javax.persistence.jdbc.driver", value);
    }

    public void setUser(String value) {
        properties.put("javax.persistence.jdbc.user", value);
    }

    public String getUser() {
        return properties.get("javax.persistence.jdbc.user");
    }

    public void setPassword(String value) {
        properties.put("javax.persistence.jdbc.password", value);
    }

    public String getPassword() {
        return properties.get("javax.persistence.jdbc.password");
    }

    public void setJdbcUrl(String value) {
        properties.put("javax.persistence.jdbc.url", value);
    }

    public String getJdbcUrl() {
        return properties.get("javax.persistence.jdbc.url");
    }

    public void setHbm2ddl(String value) {
        properties.put("hibernate.hbm2ddl.auto", value);
    }

    public void setPrintSql(boolean value) {
        properties.put("hibernate.show_sql", Boolean.toString(value));
        properties.put("hibernate.format_sql", Boolean.toString(value));
    }
}


