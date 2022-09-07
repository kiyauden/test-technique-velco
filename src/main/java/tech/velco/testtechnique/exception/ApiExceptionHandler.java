package tech.velco.testtechnique.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tech.velco.testtechnique.exception.response.ApiExceptionResponse;
import tech.velco.testtechnique.exception.response.ValidationExceptionResponse;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * Handles the exceptions thrown by the API
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    /**
     * Generates the response when an uncaught ApiException occurs
     */
    @ExceptionHandler(value = ApiException.class)
    public ResponseEntity<Object> handleApiException(final ApiException e) {
        final HttpStatus internalServerError = INTERNAL_SERVER_ERROR;

        final ApiExceptionResponse body = ApiExceptionResponse.builder()
                .status(internalServerError.value())
                .error(e.getMessage())
                .build();

        return new ResponseEntity<>(body, internalServerError);
    }

    /**
     * Generates the response when an uncaught ValidationException occurs
     */
    @ExceptionHandler(value = ValidationException.class)
    public ResponseEntity<Object> handleValidationException(final ValidationException e) {
        final List<ValidationExceptionResponse.Error> validationErrors = new ArrayList<>();
        final List<ObjectError> errors = e.getErrors();

        for (final ObjectError error : errors) {
            validationErrors.add(
                    ValidationExceptionResponse.Error.builder()
                            .error(error.getDefaultMessage())
                            .key(error.getObjectName())
                            .build()
            );
        }

        final ValidationExceptionResponse validationExceptionResponse = ValidationExceptionResponse.builder()
                .errors(validationErrors)
                .build();

        return new ResponseEntity<>(validationExceptionResponse, BAD_REQUEST);
    }
}
