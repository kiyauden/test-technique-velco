package tech.velco.testtechnique.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.ObjectError;

import java.util.List;

/**
 * Exception when a data validation fails
 */
@RequiredArgsConstructor
@Getter
public class ValidationException extends RuntimeException {

    private final List<ObjectError> errors;
    
}
