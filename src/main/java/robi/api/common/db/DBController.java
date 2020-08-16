package robi.api.common.db;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;

@RestController
@RequestMapping("/v1/db")
public class DBController {

    private DataSource pbaDataSource;

    public DBController(@Qualifier("ROBIDataSource") DataSource source) {
        pbaDataSource = source;
    }

    @GetMapping("/migrate")
    public String migrate() {
        Flyway flyway;
        try {
            flyway = Flyway.configure().dataSource(pbaDataSource).load();
            flyway.migrate();
        } catch (Exception e) {
            return e.getMessage();
        }
        return "MIGRATE: OK";
    }

    /**
     *
     * IMPORTANT!!!  =>  DO NOT USED THIS METHOD!
     */
    @GetMapping("/clean")
    public String clean() {
        Flyway flyway;
        try {
            flyway = Flyway.configure().dataSource(pbaDataSource).load();
            flyway.clean();
        } catch (Exception e) {
            return e.getMessage();
        }
        return "CLEAN: OK";
    }
}
