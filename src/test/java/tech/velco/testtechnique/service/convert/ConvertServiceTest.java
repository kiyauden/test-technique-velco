package tech.velco.testtechnique.service.convert;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;
import tech.velco.testtechnique.model.Error;
import tech.velco.testtechnique.model.Reference;
import tech.velco.testtechnique.model.ReferenceFormat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.velco.testtechnique.model.Color.R;
import static tech.velco.testtechnique.model.ErrorMessage.INCORRECT_FORMAT_PRICE;
import static tech.velco.testtechnique.model.ErrorMessage.INCORRECT_FORMAT_SIZE;
import static tech.velco.testtechnique.model.ErrorMessage.INCORRECT_NUMBER_OF_CHARACTERS_NUM_REFERENCE;
import static tech.velco.testtechnique.model.ErrorMessage.INCORRECT_VALUE_COLOR;

@ExtendWith(SpringExtension.class)
class ConvertServiceTest {

    private ConvertService service;

    @BeforeEach
    void setUp() {
        service = new ConvertService();
    }

    @Test
    void convert_whenFileCorrect_ShouldReturnAllLinesWithoutErrors() {
        final MultipartFile multipartFile = loadMultipartFile("Ref_01.txt");

        final ReferenceFormat referenceFormat = service.convert(multipartFile, null);

        assertEquals("Ref_01.txt", referenceFormat.getInputFile());
        assertEquals(4, referenceFormat.getReferences().size());
        assertEquals(0, referenceFormat.getErrors().size());

        // 1460100040;R;45.12;27
        final Reference firstLine = referenceFormat.getReferences().get(0);
        assertEquals("1460100040", firstLine.getNumReference());
        assertEquals(R, firstLine.getType());
        assertEquals(45.12f, firstLine.getPrice());
        assertEquals(27, firstLine.getSize());
    }

    @Test
    void convert_whenFileContainsErrors_ShouldReturnAllLinesWithErrors() {
        final MultipartFile multipartFile = loadMultipartFile("Ref_02.txt");

        final ReferenceFormat referenceFormat = service.convert(multipartFile, null);

        assertEquals("Ref_02.txt", referenceFormat.getInputFile());
        assertEquals(4, referenceFormat.getReferences().size());
        assertEquals(4, referenceFormat.getErrors().size());

        // 1234;A;100.0;9
        // Two errors on this line (line 5)
        final List<Error> errors = referenceFormat.getErrors();

        // First error -> the reference number is not 10 characters long
        final Error lineFiveFirstError = errors.get(0);
        assertEquals(5, lineFiveFirstError.getLine());
        assertEquals(INCORRECT_NUMBER_OF_CHARACTERS_NUM_REFERENCE.getMessage(), lineFiveFirstError.getMessage());
        assertEquals("1234;A;100.0;9", lineFiveFirstError.getValue());

        // Second error -> color A does nor exits
        final Error lineFiveSecondError = errors.get(1);
        assertEquals(5, lineFiveSecondError.getLine());
        assertEquals(INCORRECT_VALUE_COLOR.getMessage(), lineFiveSecondError.getMessage());
        assertEquals("1234;A;100.0;9", lineFiveSecondError.getValue());

        // 1462100403;B;wrongType;97.5
        // Two errors on this line (line 6)

        // First error -> the price is not a float
        final Error lineSixFirstError = errors.get(2);
        assertEquals(6, lineSixFirstError.getLine());
        assertEquals(INCORRECT_FORMAT_PRICE.getMessage(), lineSixFirstError.getMessage());
        assertEquals("1462100403;B;wrongType;97.5", lineSixFirstError.getValue());

        // Second error -> color A does nor exits
        final Error lineSixSecondError = errors.get(3);
        assertEquals(6, lineSixSecondError.getLine());
        assertEquals(INCORRECT_FORMAT_SIZE.getMessage(), lineSixSecondError.getMessage());
        assertEquals("1462100403;B;wrongType;97.5", lineSixSecondError.getValue());
    }

    @Test
    void convert_whenSortOnePrice_ShouldSortOnPrice() {
        final MultipartFile multipartFile = loadMultipartFile("Ref_03.txt");

        final ReferenceFormat referenceFormat = service.convert(multipartFile, "price");

        assertEquals("Ref_03.txt", referenceFormat.getInputFile());
        assertEquals(4, referenceFormat.getReferences().size());
        assertEquals(0, referenceFormat.getErrors().size());

        // Test sorting
        final List<Reference> references = referenceFormat.getReferences();
        float previous = references.get(0).getPrice();
        for (int i = 1; i < references.size(); i++) {
            final float current = references.get(i).getPrice();
            assertTrue(previous <= current);
            previous = current;
        }
    }

    @Test
    void convert_whenSortOneSize_ShouldSortOnSize() {
        final MultipartFile multipartFile = loadMultipartFile("Ref_03.txt");

        final ReferenceFormat referenceFormat = service.convert(multipartFile, "size");

        assertEquals("Ref_03.txt", referenceFormat.getInputFile());
        assertEquals(4, referenceFormat.getReferences().size());
        assertEquals(0, referenceFormat.getErrors().size());

        // Test sorting
        final List<Reference> references = referenceFormat.getReferences();
        int previous = references.get(0).getSize();
        for (int i = 1; i < references.size(); i++) {
            final int current = references.get(i).getSize();
            assertTrue(previous <= current);
            previous = current;
        }
    }

    /**
     * Loads a file from the ressources folder
     */
    private MultipartFile loadMultipartFile(final String filename) {
        try {
            final File file = ResourceUtils.getFile("classpath:testfiles/" + filename);
            return new MockMultipartFile("File", file.getName(), "text", Files.newInputStream(file.toPath()));
        } catch (final IOException e) {
            // Converts the checked exception to an unchecked to make testing easier
            throw new RuntimeException(e);
        }

    }
}
