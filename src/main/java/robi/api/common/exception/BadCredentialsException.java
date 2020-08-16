package robi.api.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadCredentialsException extends RuntimeException {
    /**
     * BadCredentials Exception
     */

    public BadCredentialsException(String msg) {
        super(msg);
    }

    public BadCredentialsException(String msg, Throwable th) {
        super(msg, th);
    }

    @Override
    public String getLocalizedMessage() {
        StackTraceElement[] stackTrace = super.getStackTrace();
        return "Bad Credentials: " + (stackTrace == null ? "" : "(" + stackTrace[0].getMethodName() + ") ") + super.getLocalizedMessage();
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
