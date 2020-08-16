package robi.api.common.log;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import robi.api.common.BaseRepository;

import javax.sql.DataSource;
import java.sql.Types;

@Repository
public class LogRepository extends BaseRepository {

    public LogRepository(@Qualifier("ROBIDataSource") DataSource dataSource) {
        super(dataSource);
    }

    public void addLoginLog(LoginLog loginLog) {
        execute("ins_login_log", loginLog, Types.BIGINT);
    }
}
