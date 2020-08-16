package robi.api.auth.grant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/v1/grant")
@RestController
public class GrantController {

    private final GrantService grantService;

    public GrantController(GrantService grantService) {
        this.grantService = grantService;
    }

    @GetMapping
    public ResponseEntity<List<Grant>> getSystemGrants() {
        return new ResponseEntity<>(grantService.getSystemGrants(), HttpStatus.OK);
    }

}
