package robi.api.robi_proxy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import robi.api.auth.app_user.AppUser;
import robi.api.auth.app_user.AppUserRepository;

import java.time.Duration;

@Component
public class RobyProxy {

    private static final Logger logger = LogManager.getLogger(RobyProxy.class);

    private final RestTemplate restTemplate;
    private AppUserRepository userRepository;

    private static final int MAX_RETRIES = 3;

    public RobyProxy(AppUserRepository userRepository) {
        this.userRepository = userRepository;
        restTemplate = new RestTemplateBuilder()
                .setReadTimeout(Duration.ofSeconds(10))
                .setConnectTimeout(Duration.ofSeconds(30))
                .build();
    }

    public ResponseEntity<String> makeGET (Long userId, String path) {
        AppUser user = userRepository.getById(userId);
        String baseUrl = user.getRobotEndpoint();
        return makeGETWithRetries(baseUrl + path);

    }

    public ResponseEntity<String> makeGETWithRetries (String url) {
        ResponseEntity<String> resp = null;

        try {
            resp = makeGET(url);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        if (isValid(resp)) {
            return resp;
        }

        int c = 0;
        while (c < MAX_RETRIES) {
            try {
                Thread.sleep(200); // Give dataplicity service some time to wake up...
                resp = makeGET(url);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
            if (isValid(resp)) {
                break;
            }
            c++;
        }
        return resp;
    }

    public ResponseEntity<String> makeGET (String url) {
        logger.info("EXECUTE URL: " + url);
        ResponseEntity<String> resp =  restTemplate.exchange(url, HttpMethod.GET, null, String.class);
        logger.info("RESPONSE: " + resp.getStatusCode() + ": " + resp.getBody());
        return resp;
    }

    public boolean isValid(ResponseEntity resp) {
        if (resp == null) return false;
        boolean result = resp.getStatusCode() == HttpStatus.OK || resp.getStatusCode() == HttpStatus.CREATED;
        String respMsg = (String) resp.getBody();
        if (respMsg == null) {
            return false;
        }
        return result && !respMsg.toLowerCase().contains("dataplicity port forwarding");
    }

}
