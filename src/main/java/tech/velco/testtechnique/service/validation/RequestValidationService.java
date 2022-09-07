package tech.velco.testtechnique.service.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.validation.ObjectError;
import org.springframework.web.multipart.MultipartFile;
import tech.velco.testtechnique.controller.dto.Request;
import tech.velco.testtechnique.exception.ApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Service in charge of the request validation
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RequestValidationService implements IRequestValidationService {

    private static final List<String> ALLOWED_SORT_KEYS = asList("price", "size");

    /**
     * The expected mime type for the uploaded file
     */
    private static final String EXPECTED_MIME_TIME = "text/plain";

    private final Tika tika;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ObjectError> validate(final Request request) {
        log.debug("Validating request");

        final List<ObjectError> errors = new ArrayList<>();

        // The file is mandatory
        final MultipartFile file = request.getFile();
        if (file == null) {
            log.debug("File missing");
            errors.add(new ObjectError("file", "The file is mandatory"));
        } else {
            try {
                final String mimeType = tika.detect(file.getInputStream());
                if (!mimeType.equals(EXPECTED_MIME_TIME)) {
                    log.debug("Incorrect file type");
                    errors.add(new ObjectError("file", "The file must be a text file, " + mimeType + " provided"));
                }
            } catch (final IOException e) {
                throw new ApiException("Error while file mime type detection", e);
            }

            if (file.isEmpty()) {
                log.debug("Empty file");
                errors.add(new ObjectError("file", "The file must have content"));
            }
        }

        // The sortKey must be price or size
        final String sortKey = request.getSortKey();
        if (sortKey != null && !ALLOWED_SORT_KEYS.contains(sortKey)) {
            log.debug("SortKey {} not allowed", sortKey);
            errors.add(new ObjectError("sortKey", "The sortkey must be one of " + ALLOWED_SORT_KEYS));
        }

        return errors;
    }

}
