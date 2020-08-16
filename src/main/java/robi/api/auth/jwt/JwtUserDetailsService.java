package robi.api.auth.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import robi.api.auth.app_user.AppUser;
import robi.api.auth.app_user.AppUserRepository;
import robi.api.common.exception.*;
import robi.api.common.exception.*;
import robi.api.common.log.LogRepository;
import robi.api.common.log.LoginLog;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    private Logger LOGGER = LogManager.getLogger(JwtUserDetailsService.class);

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final LogRepository logRepository;
    private final String PASSWORD_REGEX = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,32}$";
    private final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);

    @Autowired
    public JwtUserDetailsService(AppUserRepository userRepository,
                                 PasswordEncoder passwordEncoder, JwtTokenUtil _jwtTokenUtil,
                                 LogRepository logRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwtTokenUtil = _jwtTokenUtil;
        this.logRepository = logRepository;
    }

    public Long createUser(String username, String name, String email, String password) throws ServiceException, RepositoryException {
        if (password == null) {
            throw new ServiceException("Por favor, especifique una contraseña.");
        }
        String hashedPassword = hashPassword(password);
        AppUser user = new AppUser();
        user.setUsername(username);
        user.setFullName(name);
        user.setPassword(hashedPassword);
        user.setEmail(email);
        return userRepository.add(user);
    }

    public AppUser login(String username, String password) throws NotFoundException {
        AppUser loggedUser = null;
        String userPassword = null;
        String notValidError = "Por favor, especifique un nombre de usuario y una contraseña.";
        String credentialsError = "Usuario o contraseña inválidos.";
        String lockedError = "La cuenta: %s esta bloqueada.";
        LoginLog loginLog = new LoginLog(logRepository);
        loginLog.setUsername(username);
        loginLog.setStatus("E");

        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            loginLog.setErrorDetail(notValidError);
            loginLog.persist();
            LOGGER.error(notValidError);
            throw new NotValidRequestException(notValidError);
        }

        try {
            loggedUser = userRepository.getByUsername(username);
            loginLog.setUserId(loggedUser.getId());
        } catch (RepositoryException e) {
            loginLog.setErrorDetail(e.getMessage());
            loginLog.persist();
            LOGGER.error(e.getMessage());
            throw new NotFoundException(String.format("El usuario %s no existe.", username));
        }

        userPassword = loggedUser.getPassword();

        if (!matchPassword(userPassword, password)) {
            loginLog.setErrorDetail(credentialsError);
            loginLog.persist();
            LOGGER.error(credentialsError);
            throw new BadCredentialsException(credentialsError);
        }

        if (loggedUser.isLocked()) {
            loginLog.setErrorDetail(String.format(lockedError, loggedUser.getUsername()));
            loginLog.persist();
            LOGGER.error(String.format(lockedError, loggedUser.getUsername()));
            throw new ServiceException(String.format(lockedError, loggedUser.getUsername()));
        }

        final String token = jwtTokenUtil.generateToken(new JwtUser(loggedUser));

        loggedUser.setAccessToken(token);
        loginLog.setStatus("C");
        loginLog.persist();

        return loggedUser;
    }

    @Override
    public JwtUser loadUserByUsername(String username) {
        AppUser user = null;
        try {
            user = userRepository.getByUsername(username);
        } catch (RepositoryException e) {
            throw new RuntimeException(e.getLocalizedMessage());
        }
        return new JwtUser(user);
    }

    public AppUser loadUserByID(Long userId) {
        try {
            return userRepository.getById(userId);
        } catch (RepositoryException e) {
            throw new RuntimeException(e.getLocalizedMessage());
        }
    }

    public Long getUserIdFromRequest(HttpServletRequest request) throws ServiceException {
        String requestTokenHeader = request.getHeader("Authorization");
        String jwtToken = null;
        Long userID = null;
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                userID = Long.valueOf(jwtTokenUtil.getUserIdFromToken(jwtToken));

            } catch (IllegalArgumentException e) {
                System.out.println("Unable to get JWT Token");

            } catch (ExpiredJwtException e) {
                System.out.println("JWT Token has expired");

             } catch (Exception e) {
                throw new ServiceException("Hubo un error obteniendo el token.");
            }
        } else {
            throw new ServiceException("El token no esta en el header Authorization del request.");
        }
        return userID;
    }


    private String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    private boolean matchPassword(String hashPassword, String rawPassword) {
        return passwordEncoder.matches(rawPassword, hashPassword);
    }

    // Helper methods
    public AppUser processUser(String username) throws ServiceException, RepositoryException {
        final JwtUser jwtUser = loadUserByUsername(username);

        if (jwtUser == null || jwtUser.getUser() == null) {
            throw new ServiceException(String.format("User with username: %s couldn't be found.", username));
        }

        // User validations...
        AppUser user = jwtUser.getUser();

        // Not locked
        if (!jwtUser.isAccountNonLocked()) {
            throw new ServiceException(String.format("Account for user: %s is locked.", jwtUser.getUsername()));
        }

        final String token = jwtTokenUtil.generateToken(jwtUser);
        jwtUser.getUser().setAccessToken("Bearer " + token);

        return user;
    }


    public Boolean usernameAvailable(String username) {
        return userRepository.usernameAvailable(username);
    }

    public Boolean emailAvailable(String email) {
        return userRepository.emailAvailable(email);
    }
}
