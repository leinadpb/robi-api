package robi.api.robots;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import robi.api.common.BaseRepository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class RobotsRepository extends BaseRepository<Robot> {

    public RobotsRepository(@Qualifier("ROBIDataSource") DataSource dataSource) {
        super(dataSource);
    }

    public List<Robot> allRobots() {
        return all("get_all_robots");
    }

    public Robot createAppRobot(Robot userRobot) {
        return (Robot) execute(Robot.class,"create_app_robot", userRobot);
    }
}
