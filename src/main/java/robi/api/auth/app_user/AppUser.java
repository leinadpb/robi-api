package robi.api.auth.app_user;

import robi.api.common.BaseEntity;

public class AppUser extends BaseEntity {

    private String username;
    private String password;
    private String fullName;
    private String email;
    private Boolean locked;
    private String accessToken;
    private Boolean requirePasswordChange;
    private Boolean requireLogin;
    private String robotEndpoint;

    public AppUser() {

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean isLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Boolean getRequirePasswordChange() {
        return requirePasswordChange;
    }

    public void setRequirePasswordChange(Boolean requirePasswordChange) {
        this.requirePasswordChange = requirePasswordChange;
    }

    public Boolean getRequireLogin() {
        return requireLogin;
    }

    public void setRequireLogin(Boolean requireLogin) {
        this.requireLogin = requireLogin;
    }

    public Boolean getLocked() {
        return locked;
    }

    public String getRobotEndpoint() {
        return robotEndpoint;
    }

    public void setRobotEndpoint(String robotEndpoint) {
        this.robotEndpoint = robotEndpoint;
    }
}
