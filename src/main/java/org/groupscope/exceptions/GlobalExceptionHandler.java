package org.groupscope.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.core.NestedRuntimeException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Mykyta Liashko
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String BAD_CREDENTIALS_MESSAGE = "Incorrect username or password. "
            + "Please check the accuracy of the entered data and try again.";

    private static final String MANY_REQUESTS_MESSAGE = "Too Many Requests. "
            + "Please waiting and try again later.";

    private static final String AUTHENTICATION_ERROR_MESSAGE = "Issues with authentication process. "
            + "Please waiting and try again later.";

    private static final String ACCESS_DENIED_MESSAGE = "You don't have permission to access the requested resource";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<Error> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("handleMethodArgumentNotValidException: message = {}", ex.getMessage(), ex);
        return ex.getBindingResult().getAllErrors().stream()
                .map(err -> new Error(
                        err.getDefaultMessage(),
                        ErrorType.VALIDATION_ERROR_TYPE,
                        LocalDateTime.now()
                ))
                .toList();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<Error> handleConstraintViolationException(ConstraintViolationException ex) {
        log.error("ConstraintViolationException: message = {}", ex.getMessage(), ex);
        return ex.getConstraintViolations().stream()
                .map(err -> new Error(
                        createConstraintViolationExceptionMessage(err),
                        ErrorType.VALIDATION_ERROR_TYPE,
                        LocalDateTime.now()
                ))
                .toList();
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Error handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        log.error("MissingServletRequestParameterException: message = {}", ex.getMessage(), ex);
        return new Error(ex.getMessage(), ErrorType.VALIDATION_ERROR_TYPE, LocalDateTime.now());
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Error handleBadCredentialsException(BadCredentialsException ex) {
        log.warn("BadCredentialsException: message = {}", ex.getMessage(), ex);
        return new Error(BAD_CREDENTIALS_MESSAGE, ErrorType.VALIDATION_ERROR_TYPE, LocalDateTime.now());
    }

    @ExceptionHandler({SerializationException.class, RedisConnectionFailureException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Error handleNestedRuntimeException(NestedRuntimeException ex) {
        log.warn("SerializationException: message = {}", ex.getMessage(), ex);
        return new Error(ex.getMessage(), ErrorType.DATABASE_ERROR_TYPE, LocalDateTime.now());
    }

    @ExceptionHandler(HttpClientErrorException.TooManyRequests.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public Error handleHttpClientTooManyRequestsException(HttpClientErrorException.TooManyRequests ex) {
        log.warn("TooManyRequestsException: message = {}", ex.getMessage(), ex);
        return new Error(MANY_REQUESTS_MESSAGE, ErrorType.PROCESSING_ERROR_TYPE, LocalDateTime.now());
    }

    @ExceptionHandler(HttpClientErrorException.UnprocessableEntity.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Error handleHttpClientUnprocessableEntityException(HttpClientErrorException.UnprocessableEntity ex) {
        log.warn("UnprocessableEntityException: message = {}", ex.getMessage(), ex);
        return new Error(extractDetailsFromMessage(ex.getMessage()), ErrorType.VALIDATION_ERROR_TYPE, LocalDateTime.now());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Error handleNotFoundEntityException(EntityNotFoundException ex) {
        log.error("EntityNotFoundException: message = {}", ex.getMessage(), ex);
        return new Error(ex.getMessage(), ErrorType.PROCESSING_ERROR_TYPE, LocalDateTime.now());
    }

    @ExceptionHandler(DuplicateEntityException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Error handleEntityAlreadyExistsException(DuplicateEntityException ex) {
        log.error("DuplicateEntityException: message = {}", ex.getMessage(), ex);
        return new Error(ex.getMessage(), ErrorType.PROCESSING_ERROR_TYPE, LocalDateTime.now());
    }

    @ExceptionHandler(StudentNotInGroupException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Error handleEntityAlreadyExistsException(StudentNotInGroupException ex) {
        log.error("StudentNotInGroupException: message = {}", ex.getMessage(), ex);
        return new Error(ex.getMessage(), ErrorType.PROCESSING_ERROR_TYPE, LocalDateTime.now());
    }

    @ExceptionHandler(NoLearnerGroupException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Error handleNoLearnerGroupException(NoLearnerGroupException ex) {
        log.error("NoLearnerGroupException: message = {}", ex.getMessage(), ex);
        return new Error(ex.getMessage(), ErrorType.PROCESSING_ERROR_TYPE, LocalDateTime.now());
    }


    @ExceptionHandler(AuthenticationServiceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Error handleAuthenticationServiceException(AuthenticationServiceException ex) {
        log.error("AuthenticationServiceException: message = {}", ex.getMessage(), ex);
        return new Error(AUTHENTICATION_ERROR_MESSAGE, ErrorType.SECURITY_ERROR_TYPE, LocalDateTime.now());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Error handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        Principal userPrincipal = request.getUserPrincipal();
        String username = userPrincipal != null ? userPrincipal.getName() : "unauthorized";
        log.error("AccessDeniedException: message = {}; Username: {}", ex.getMessage(), username);
        return new Error(ACCESS_DENIED_MESSAGE, ErrorType.SECURITY_ERROR_TYPE, LocalDateTime.now());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Error handleRuntimeException(RuntimeException ex) {
        log.error("RuntimeException: message = {}", ex.getMessage(), ex);
        return new Error(ex.getMessage(), ErrorType.PROCESSING_ERROR_TYPE, LocalDateTime.now());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Error handleException(Exception ex) {
        log.error("handleException: message = {}",
                ex.getMessage(), ex);
        return new Error(ex.getMessage(), ErrorType.FATAL_ERROR_TYPE, LocalDateTime.now());
    }

    private String createConstraintViolationExceptionMessage(
            ConstraintViolation<?> constraintViolation
    ) {
        String propertyPath = constraintViolation.getPropertyPath().toString();
        String message = constraintViolation.getMessage();

        // Extract the last part of the property path
        String[] propertyPathParts = propertyPath.split("\\.");
        String propertyName = "'" + propertyPathParts[propertyPathParts.length - 1] + "'";

        return propertyName + " " + message;
    }

    private String extractDetailsFromMessage(String message) {
        int startIndex = message.indexOf('{');
        int endIndex = message.lastIndexOf('}') + 1;
        if (startIndex < 0) return message;
        String jsonString = message.substring(startIndex, endIndex);

        try {
            return new JSONObject(jsonString).getString("details");
        } catch (JSONException e) {
            return message;
        }
    }

}
