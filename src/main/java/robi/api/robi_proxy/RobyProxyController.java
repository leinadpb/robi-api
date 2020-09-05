package robi.api.robi_proxy;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/v1/robi")
public class RobyProxyController {

    private final RobyProxy robyProxy;

    public RobyProxyController(RobyProxy robyProxy) {
        this.robyProxy = robyProxy;
    }

    @PostMapping
    public ResponseEntity<String> makeRequest(HttpServletRequest req,  @RequestBody ProxyModel proxyModel) {
        Long userId = Long.parseLong(req.getHeader("user_id"));
        ResponseEntity<String> resp = robyProxy.makeGET(userId,proxyModel.getRobotPath());
        String respMsg = (String) resp.getBody();
        if (respMsg != null && respMsg.toLowerCase().contains("device not connected")) {
            return new ResponseEntity<>("El Robot no esta conectado a internet. Path: " + proxyModel.getRobotPath(), HttpStatus.CONFLICT);
        }
        return resp;
    }

}
