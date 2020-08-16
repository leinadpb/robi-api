package robi.api.common.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

public class ApiError {
    private HttpStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;
    private String message;
    private String debugMessage;
    private List<ApiSubError> subErrors;


    ApiError(HttpStatus status) {
        super();
        this.status = status;
        timestamp = LocalDateTime.now();
    }

    ApiError(HttpStatus status, Throwable ex) {
        super();
        this.status = status;
        this.message = "Error inesperado.";
        this.debugMessage = ex.getLocalizedMessage();
        timestamp = LocalDateTime.now();
    }

    ApiError(HttpStatus status, String message, Throwable ex) {
        super();
        this.status = status;
        this.message = message;
        this.debugMessage = ex.getLocalizedMessage();
        timestamp = LocalDateTime.now();
    }

    public int getStatusCode() {
        return this.status.value();
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDebugMessage() {
        return debugMessage;
    }

    public void setDebugMessage(String debugMessage) {
        this.debugMessage = debugMessage;
    }

    public List<ApiSubError> getSubErrors() {
        return subErrors;
    }

    public void setSubErrors(List<ApiSubError> subErrors) {
        this.subErrors = subErrors;
    }
}
