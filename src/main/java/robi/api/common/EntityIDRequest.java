package robi.api.common;

public class EntityIDRequest {

    private Object value;
    private String entityId;

    private String JSON = "{ \"%s\":\"%s\" }";

    public EntityIDRequest(String entityId, Object value) {
        this.value = value;
        this.entityId = entityId;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    @Override
    public String toString() {
        return String.format(JSON, this.entityId, this.value);
    }
}
