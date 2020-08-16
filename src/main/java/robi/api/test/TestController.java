package robi.api.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/test")
public class TestController {

    private final TestRepository repository;

    public TestController(TestRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public String testApi() {
        return "Yei! It works!";
    }

    @GetMapping("/all-test")
    public List<Test> allTests() throws JsonProcessingException {
        return repository.allTests();
    }

    @GetMapping("/all-test/{alike}")
    public List<Test> allTests(@PathVariable String alike) {
        return repository.allTestsAlike(alike);
    }

}
