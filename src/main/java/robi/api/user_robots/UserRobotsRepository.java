package robi.api.user_robots;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import robi.api.common.BaseRepository;
import robi.api.common.ObjectWithOwner;
import robi.api.robots.Robot;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.List;

@Repository
public class UserRobotsRepository extends BaseRepository<Robot> {

    public UserRobotsRepository(@Qualifier("ROBIDataSource") DataSource dataSource) {
        super(dataSource);
    }

    public List<Robot> allRobots(Long userId) {
        return all("get_user_robots", userId, "userId");
    }

    public Robot getRobot(Long userId, String connection) {
        return (Robot) execute(Robot.class,"get_user_robot_by_connection_and_user", new ObjectWithOwner(connection, userId));
    }

    public Robot addRobotToUser(Long userId, Long robotId) {
        return (Robot) execute(Robot.class,"add_robot_to_user", new ObjectWithOwner(robotId, userId));
    }
}
