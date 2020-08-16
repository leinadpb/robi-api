package robi.api.auth.jwt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import robi.api.auth.app_user.AppUser;

import java.util.ArrayList;
import java.util.Collection;

public class JwtUser implements UserDetails {

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    private AppUser user;

    public JwtUser(AppUser user) {
        this.user = user;
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return this.user.getPassword();
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return this.user.getUsername();
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<>();
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.user.isLocked();
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return !this.user.isLocked();
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
