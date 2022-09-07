package tech.velco.testtechnique.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.velco.testtechnique.controller.dto.Request;
import tech.velco.testtechnique.exception.ValidationException;
import tech.velco.testtechnique.model.ReferenceFormat;
import tech.velco.testtechnique.service.convert.IConvertService;
import tech.velco.testtechnique.service.validation.IRequestValidationService;

import java.util.List;

/**
 * Controller for the REST API
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class ConvertController {

    private final IConvertService convertService;
    private final IRequestValidationService requestValidationService;

    @PostMapping("/convert")
    public ReferenceFormat convert(final Request request) {
        log.info("Answering a convert request");

        // Validates the request before going further
        final List<ObjectError> errors = requestValidationService.validate(request);
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        return convertService.convert(request.getFile(), request.getSortKey());
    }

}
