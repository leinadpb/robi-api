package robi.api.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class RepositoryException extends RuntimeException {
    /**
     * Repository Exception
     */

    public RepositoryException(String msg) {
        super(msg);
    }

    public RepositoryException(String msg, Throwable th) {
        super(msg, th);
    }

    @Override
    public String getLocalizedMessage() {
        StackTraceElement[] stackTrace = super.getStackTrace();
        return "Repository: " + (stackTrace == null ? "" : "(" + stackTrace[0].getMethodName() + ") ") + super.getLocalizedMessage();
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
