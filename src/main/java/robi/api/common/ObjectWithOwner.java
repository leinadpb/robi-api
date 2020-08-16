package robi.api.common;

public class ObjectWithOwner {
    private Object objectId;
    private Long ownerId;

    public ObjectWithOwner(Long objectId, Long ownerId) {
        this.objectId = objectId;
        this.ownerId = ownerId;
    }

    public ObjectWithOwner(String objectId, Long ownerId) {
        this.objectId = objectId;
        this.ownerId = ownerId;
    }

    public Object getObjectId() {
        return objectId;
    }

    public void setObjectId(Object objectId) {
        this.objectId = objectId;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
}
