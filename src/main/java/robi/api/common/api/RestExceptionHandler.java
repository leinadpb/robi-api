package robi.api.common.api;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import robi.api.common.exception.*;
import robi.api.common.exception.*;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler {

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(NotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFound(
            NotFoundException ex) {
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
        apiError.setMessage(ex.getMessage());
        apiError.setDebugMessage(ex.getLocalizedMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(ServiceException.class)
    protected ResponseEntity<Object> handleBusinessLogicError(
            ServiceException ex) {
        ApiError apiError = new ApiError(HttpStatus.CONFLICT);
        apiError.setMessage(ex.getMessage());
        apiError.setDebugMessage(ex.getLocalizedMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(RepositoryException.class)
    protected ResponseEntity<Object> handleRepositoryError(
            RepositoryException ex) {
        ApiError apiError = new ApiError(HttpStatus.CONFLICT);
        apiError.setMessage(ex.getMessage());
        apiError.setDebugMessage(ex.getLocalizedMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(BadCredentialsException.class)
    protected ResponseEntity<Object> handleBadUserCredentials(
            BadCredentialsException ex) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
        apiError.setMessage(ex.getMessage());
        apiError.setDebugMessage(ex.getLocalizedMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(NotValidRequestException.class)
    protected ResponseEntity<Object> handleNotValidRequest(
            NotValidRequestException ex) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
        apiError.setMessage(ex.getMessage());
        apiError.setDebugMessage(ex.getLocalizedMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleGeneric(
            Exception ex) {
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR);
        apiError.setMessage("Error inesperado, favor consulte servicio al cliente con un printscreen de la pantalla.");
        if (ex != null) {
            apiError.setDebugMessage(ex.getLocalizedMessage());
        }
        return buildResponseEntity(apiError);
    }
}