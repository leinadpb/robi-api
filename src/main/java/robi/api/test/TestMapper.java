package robi.api.test;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TestMapper implements RowMapper<Test> {
    @Override
    public Test mapRow(ResultSet rs, int i) throws SQLException {
        Test test = new Test();
        test.setName(rs.getString("name"));
        return test;
    }
}
