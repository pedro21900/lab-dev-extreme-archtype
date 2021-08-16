package br.jus.tre_pa.app.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Component("DB_Oracle_Teste")
public class OracleHealthIndicator implements HealthIndicator {

    @Autowired
    private DataSource ds;

    @Override
    public Health health() {
        try(Connection conn = ds.getConnection();){
            Statement stmt = conn.createStatement();
            stmt.execute("SELECT 1 FROM DUAL");
        } catch (SQLException ex) {
            return Health.down().withException(ex).build();
        }
        return Health.up().build();
    }

}
