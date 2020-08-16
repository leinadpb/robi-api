package robi.api.common.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends Exception {

    /**
     * NotFound Exception
     */

    public NotFoundException(String msg) {
        super(msg);
    }

    public NotFoundException(String msg, Throwable th) {
        super(msg, th);
    }

    @Override
    public String getLocalizedMessage() {
        StackTraceElement[] stackTrace = super.getStackTrace();
        return (stackTrace == null ? "" : "(" + stackTrace[0].getMethodName() + ") ") + super.getLocalizedMessage();
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}