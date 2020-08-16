package robi.api.common.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoginLog {

    private Logger LOGGER = LogManager.getLogger(LoginLog.class);

    private final LogRepository logRepository;

    private Long userId;
    private String username;
    private String ipAddress; // Max 32 chars
    private String userAgent; // Max 255 chars
    private String errorMessage; // Max 255 chars
    private String errorDetail; // Max 255 chars
    private String status; // Max 1 char

    public LoginLog(LogRepository logRepository) {
        this.logRepository = logRepository;
    }


    public void persist() {
        if (logRepository != null) {
           try {
               logRepository.addLoginLog(this);
           } catch (Exception e) {
               LOGGER.error(e.getMessage());
           }
        }
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorDetail() {
        return errorDetail;
    }

    public void setErrorDetail(String errorDetail) {
        this.errorDetail = errorDetail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
