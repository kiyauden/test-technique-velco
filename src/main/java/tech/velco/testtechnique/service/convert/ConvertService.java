package tech.velco.testtechnique.service.convert;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tech.velco.testtechnique.exception.ApiException;
import tech.velco.testtechnique.model.Color;
import tech.velco.testtechnique.model.Error;
import tech.velco.testtechnique.model.Reference;
import tech.velco.testtechnique.model.ReferenceFormat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;
import static java.util.Comparator.comparing;
import static tech.velco.testtechnique.model.Color.fromString;
import static tech.velco.testtechnique.model.ErrorMessage.INCORRECT_FORMAT_PRICE;
import static tech.velco.testtechnique.model.ErrorMessage.INCORRECT_FORMAT_SIZE;
import static tech.velco.testtechnique.model.ErrorMessage.INCORRECT_NUMBER_OF_CHARACTERS_NUM_REFERENCE;
import static tech.velco.testtechnique.model.ErrorMessage.INCORRECT_VALUE_COLOR;

@Service
@Slf4j
public class ConvertService implements IConvertService {

    public static final String ERROR_ON_LINE_OF_TYPE = "Error on line {} of type {}";
    public static final String SEPARATOR = ";";

    /**
     * {@inheritDoc}
     */
    @Override
    public ReferenceFormat convert(final MultipartFile inputFile, final String sortKey) {
        log.debug("Converting a file");

        // Retrieve file data
        final String fileName = inputFile.getOriginalFilename();
        final List<String> lines = readFile(inputFile);

        final List<Error> errors = new ArrayList<>();
        final List<Reference> references = new ArrayList<>();

        // Parses the content of the file
        for (int i = 0; i < lines.size(); i++) {
            final String line = lines.get(i);
            parseLine(line, i + 1, references, errors);
        }

        // Sort
        if (sortKey != null) {
            // The sortkey was already validated in RequestValidationService
            if (sortKey.equals("price")) {
                log.debug("Sorting by price");
                references.sort(comparing(Reference::getPrice));
            }
            if (sortKey.equals("size")) {
                log.debug("Sorting by size");
                references.sort(comparing(Reference::getSize));
            }
        } else {
            log.debug("No sorting");
        }

        return ReferenceFormat.builder()
                .inputFile(fileName)
                .references(references)
                .errors(errors)
                .build();
    }

    /**
     * Parse a lineContent from the file
     *
     * @param lineContent the content of the lineContent
     * @param lineNumber  the lineContent number (1 based indexed)
     * @param references  the list of references
     * @param errors      the list of errors
     */
    private void parseLine(final String lineContent, final int lineNumber, final List<Reference> references,
                           final List<Error> errors) {
        log.debug("Parsing line {} : {}", lineNumber, lineContent);

        final List<Error> lineErrors = new ArrayList<>();

        final String[] lineElements = lineContent.split(SEPARATOR);

        // Parse numReference
        final String numReference = lineElements[0];
        if (numReference.length() != 10) {
            log.debug(ERROR_ON_LINE_OF_TYPE, lineNumber, INCORRECT_NUMBER_OF_CHARACTERS_NUM_REFERENCE);
            final Error error = Error.builder()
                    .line(lineNumber)
                    .message(INCORRECT_NUMBER_OF_CHARACTERS_NUM_REFERENCE.getMessage())
                    .value(lineContent)
                    .build();
            lineErrors.add(error);
        }

        // Parse type (color)
        final Optional<Color> color = fromString(lineElements[1]);
        if (!color.isPresent()) {
            log.debug(ERROR_ON_LINE_OF_TYPE, lineNumber, INCORRECT_VALUE_COLOR);
            final Error error = Error.builder()
                    .line(lineNumber)
                    .message(INCORRECT_VALUE_COLOR.getMessage())
                    .value(lineContent)
                    .build();
            lineErrors.add(error);
        }

        // Parse type (color)
        float price = 0;
        try {
            price = parseFloat(lineElements[2]);
        } catch (final NumberFormatException e) {
            log.debug(ERROR_ON_LINE_OF_TYPE, lineNumber, INCORRECT_FORMAT_PRICE);
            final Error error = Error.builder()
                    .line(lineNumber)
                    .message(INCORRECT_FORMAT_PRICE.getMessage())
                    .value(lineContent)
                    .build();
            lineErrors.add(error);
        }

        // Parse size
        int size = 0;
        try {
            size = parseInt(lineElements[3]);
        } catch (final NumberFormatException e) {
            log.debug(ERROR_ON_LINE_OF_TYPE, lineNumber, INCORRECT_FORMAT_SIZE);
            final Error error = Error.builder()
                    .line(lineNumber)
                    .message(INCORRECT_FORMAT_SIZE.getMessage())
                    .value(lineContent)
                    .build();
            lineErrors.add(error);
        }


        if (!lineErrors.isEmpty()) {
            log.debug("Line {} contains {} error(s)", lineNumber, lineErrors.size());
            errors.addAll(lineErrors);
        } else {
            final Reference reference = Reference.builder()
                    .numReference(numReference)
                    .type(color.orElse(null))
                    .price(price)
                    .size(size)
                    .build();
            references.add(reference);
        }
    }

    /**
     * Reads the file
     *
     * @param inputFile the file to read
     * @return the list of all lines
     */
    private List<String> readFile(final MultipartFile inputFile) {
        log.debug("Reading file");
        final List<String> lines = new ArrayList<>();

        try {
            try (final BufferedReader br = new BufferedReader(new InputStreamReader(
                    inputFile.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    lines.add(line);
                }
            }

        } catch (final IOException e) {
            throw new ApiException("Error while reading file", e);
        }

        return lines;
    }

}
