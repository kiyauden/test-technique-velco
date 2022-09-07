package tech.velco.testtechnique.service.validation;

import org.springframework.validation.ObjectError;
import tech.velco.testtechnique.controller.dto.Request;

import java.util.List;

/**
 * Contract for the RequestValidationService
 */
public interface IRequestValidationService {

    /**
     * Validates the request
     *
     * @param request the request to validate
     * @return a list of error in the request
     */
    List<ObjectError> validate(Request request);

}
