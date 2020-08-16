package robi.api.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ServiceException extends RuntimeException {
    /**
     * Service Exception
     */

    public ServiceException(String msg) {
        super(msg);
    }

    public ServiceException(String msg, Throwable th) {
        super(msg, th);
    }

    @Override
    public String getLocalizedMessage() {
        StackTraceElement[] stackTrace = super.getStackTrace();
        return "Service: " + (stackTrace == null ? "" : "(" + stackTrace[0].getMethodName() + ") ") + super.getLocalizedMessage();
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
