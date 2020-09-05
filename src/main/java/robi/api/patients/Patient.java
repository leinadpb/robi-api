package robi.api.patients;

import robi.api.common.BaseEntity;

public class Patient extends BaseEntity {

    private String fullName;

    private String birthDate;

    private Long ownerId;
    private String description;
    private Double visualProgress;
    private Double auditiveProgress;
    private Double touchProgress;
    private Double vestibularProgress;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
       this.birthDate = birthDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getVisualProgress() {
        return visualProgress;
    }

    public void setVisualProgress(Double visualProgress) {
        this.visualProgress = visualProgress;
    }

    public Double getAuditiveProgress() {
        return auditiveProgress;
    }

    public void setAuditiveProgress(Double auditiveProgress) {
        this.auditiveProgress = auditiveProgress;
    }

    public Double getTouchProgress() {
        return touchProgress;
    }

    public void setTouchProgress(Double touchProgress) {
        this.touchProgress = touchProgress;
    }

    public Double getVestibularProgress() {
        return vestibularProgress;
    }

    public void setVestibularProgress(Double vestibularProgress) {
        this.vestibularProgress = vestibularProgress;
    }
}
