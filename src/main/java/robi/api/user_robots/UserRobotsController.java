package robi.api.user_robots;

import org.springframework.web.bind.annotation.*;
import robi.api.robots.Robot;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/v1/user_robots")
public class UserRobotsController {

    private final UserRobotsRepository userRobotsRepository;

    public UserRobotsController(UserRobotsRepository userRobotsRepository) {
        this.userRobotsRepository = userRobotsRepository;
    }

    @GetMapping
    public List<Robot> getAllRobots(HttpServletRequest req)
    {
        Long userId = Long.parseLong(req.getHeader("user_id"));
        return userRobotsRepository.allRobots(userId);
    }

    @GetMapping("/{connection}")
    public Robot getRobot(HttpServletRequest req, @PathVariable String connection)
    {
        Long userId = Long.parseLong(req.getHeader("user_id"));
        return userRobotsRepository.getRobot(userId, connection);
    }

    @PostMapping("/add/{robotId}")
    public Robot addUserRobot(HttpServletRequest req, @PathVariable Long robotId)
    {
        Long userId = Long.parseLong(req.getHeader("user_id"));
        return userRobotsRepository.addRobotToUser(userId, robotId);
    }

}
