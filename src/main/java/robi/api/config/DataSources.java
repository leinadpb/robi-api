package robi.api.config;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
public class DataSources {

    private Logger logger = LogManager.getLogger(DataSources.class);

    @Bean("ROBIDataSource")
    public DataSource robiDataSource(
            @Value("${datasource.robi.url}") String url,
            @Value("${datasource.robi.user}") String user,
            @Value("${datasource.robi.password}") String password
    ) throws SQLException {

        HikariDataSource ds = new HikariDataSource();
        ds.setLoginTimeout(10000);
        ds.setUsername(user);
        ds.setPassword(password);
        ds.setJdbcUrl(url);
        return ds;
    }

}
