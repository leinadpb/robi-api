package robi.api.auth.grant;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;

import java.sql.Timestamp;

public class Grant implements GrantedAuthority {

    private String grantId;
    private String grantName;
    private Boolean grantDefault;
    private Boolean hidden;
    private Timestamp createdAt;
    private String description;
    private Long roleId;

    @JsonIgnore
    private Long customUserId;

    public String getGrantId() {
        return grantId;
    }

    public void setGrantId(String grantId) {
        this.grantId = grantId;
    }

    public String getGrantName() {
        return grantName;
    }

    public void setGrantName(String grantName) {
        this.grantName = grantName;
    }

    public Boolean getGrantDefault() {
        return grantDefault;
    }

    public void setGrantDefault(Boolean grantDefault) {
        this.grantDefault = grantDefault;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getAuthority() {
        return this.grantId;
    }

    public Long getCustomUserId() {
        return customUserId;
    }

    public void setCustomUserId(Long customUserId) {
        this.customUserId = customUserId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}
