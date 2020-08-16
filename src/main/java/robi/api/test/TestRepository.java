package robi.api.test;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import robi.api.common.BaseRepository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class TestRepository extends BaseRepository<Test> {

    public TestRepository(@Qualifier("ROBIDataSource") DataSource dataSource) {
        super(dataSource);
    }

    public List<Test> allTests() {
        return all("get_all_from_test_robi");
    }

    public List<Test> allTestsAlike(String alike) {
        return all("get_all_from_test_robi_like", alike, "name");
    }

}
