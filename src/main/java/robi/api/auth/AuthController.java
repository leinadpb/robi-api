package robi.api.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import robi.api.auth.app_user.AppUser;
import robi.api.auth.jwt.JwtRequest;
import robi.api.auth.jwt.JwtUserDetailsService;
import robi.api.common.exception.NotFoundException;
import robi.api.common.exception.RepositoryException;
import robi.api.common.exception.ServiceException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    private final JwtUserDetailsService userDetailsService;

    public AuthController(JwtUserDetailsService jwtUserDetailsService) {
        this.userDetailsService = jwtUserDetailsService;
    }

    /**
     * Generate a new token for a specified user with credentials
     *
     * @param authRequest with userName and password
     * @return AppUserDTO object
     */
    @PostMapping("/login")
    public ResponseEntity<AppUser> login(@RequestBody JwtRequest authRequest, HttpServletResponse resp) throws NotFoundException {
        AppUser user = userDetailsService.login(authRequest.getUsername(), authRequest.getPassword());
        resp.addHeader("Authorization", user.getAccessToken());
        user.setPassword(null);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    /**
     *
     * @param req http request
     * @return logged in user info
     */
    @GetMapping("/info")
    public ResponseEntity<AppUser> info(HttpServletRequest req) {
        String userId = req.getHeader("user_id");
        AppUser user = userDetailsService.loadUserByID(Long.parseLong(userId));
        user.setPassword(null);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    /**
     * Creates a new user in the Database
     *
     * @param authenticationRequest with userName, password and email (optional)
     * @return AppUserDTO object
     */
    @PostMapping("/create")
    public ResponseEntity<AppUser> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws ServiceException, RepositoryException {
        Long createdId = userDetailsService.createUser(authenticationRequest.getUsername(), authenticationRequest.getFullName(),
                authenticationRequest.getEmail(), authenticationRequest.getPassword());
        AppUser user = userDetailsService.loadUserByID(createdId);
        user.setPassword(null);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @GetMapping("/check/username/{username}")
    public ResponseEntity<Boolean> checkUsernameAvailable(@PathVariable String username) {
        return new ResponseEntity<>(userDetailsService.usernameAvailable(username), HttpStatus.OK);
    }

    @GetMapping("/check/email/{email}")
    public ResponseEntity<Boolean> checkEmailAvailable(@PathVariable String email) {
        return new ResponseEntity<>(userDetailsService.emailAvailable(email), HttpStatus.OK);
    }

}
