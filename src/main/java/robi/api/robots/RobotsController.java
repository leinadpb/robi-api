package robi.api.robots;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/v1/app_robots")
public class RobotsController {

    private final RobotsRepository robotsRepository;

    public RobotsController(RobotsRepository robotsRepository) {
        this.robotsRepository = robotsRepository;
    }

    @GetMapping
    public List<Robot> getAllRobots() {
        return robotsRepository.allRobots();
    }

    @PostMapping
    public Robot addUserRobot(@RequestBody Robot userRobot)
    {
        return robotsRepository.createAppRobot(userRobot);
    }

}
