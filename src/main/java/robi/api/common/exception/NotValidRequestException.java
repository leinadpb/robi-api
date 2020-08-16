package robi.api.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NotValidRequestException extends RuntimeException {
    /**
     * Not Valid Request Exception
     */

    public NotValidRequestException(String msg) {
        super(msg);
    }

    public NotValidRequestException(String msg, Throwable th) {
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
