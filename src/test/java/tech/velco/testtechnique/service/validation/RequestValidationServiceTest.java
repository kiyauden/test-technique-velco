package tech.velco.testtechnique.service.validation;

import org.apache.tika.Tika;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.ObjectError;
import org.springframework.web.multipart.MultipartFile;
import tech.velco.testtechnique.controller.dto.Request;
import tech.velco.testtechnique.exception.ApiException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class RequestValidationServiceTest {

    @MockBean
    private Tika tika;

    private RequestValidationService service;

    @BeforeEach
    void setUp() throws IOException {
        service = new RequestValidationService(tika);
        when(tika.detect(any(InputStream.class))).thenReturn("text/plain");
    }

    @Test
    void validate_whenFilePresent_ShouldReturnNoErrors() {
        final Request request = Request.builder()
                .file(buildMultipartFile())
                .build();

        final List<ObjectError> errors = service.validate(request);

        assertEquals(0, errors.size());
    }

    @Test
    void validate_whenFilePresentButMimeTypeIncorrecte_ShouldReturnOneError() throws IOException {
        final Request request = Request.builder()
                .file(buildMultipartFile())
                .build();

        when(tika.detect(any(InputStream.class))).thenReturn("wrong/type");

        final List<ObjectError> errors = service.validate(request);

        assertEquals(1, errors.size());
    }

    @Test
    void validate_whenFileIsMissing_ShouldReturnAnError() {
        final Request request = Request.builder()
                .build();

        final List<ObjectError> errors = service.validate(request);

        assertEquals(1, errors.size());
    }

    @Test
    void validate_whenFilePresentButEmpty_ShouldReturnAnError() {
        final Request request = Request.builder()
                .file(buildMultipartFile(true))
                .build();

        final List<ObjectError> errors = service.validate(request);

        assertEquals(1, errors.size());
    }

    @Test
    void validate_whenSortKeyIsAllowed_ShouldReturnNoErrors() {
        final Request request = Request.builder()
                .file(buildMultipartFile())
                .sortKey("price")
                .build();

        final List<ObjectError> errors = service.validate(request);

        assertEquals(0, errors.size());
    }

    @Test
    void validate_whenSortKeyIsNotAllowed_ShouldReturnOneError() {
        final Request request = Request.builder()
                .file(buildMultipartFile())
                .sortKey("someKey")
                .build();

        final List<ObjectError> errors = service.validate(request);

        assertEquals(1, errors.size());
    }

    @Test
    void validate_whenFileException_ShouldThrowApiException() throws IOException {
        final MultipartFile spy = spy(buildMultipartFile());

        final Request request = Request.builder()
                .file(spy)
                .build();

        when(spy.getInputStream()).thenThrow(new IOException());

        assertThrows(ApiException.class, () -> service.validate(request));
    }

    /**
     * Builds a dummy MultipartFile
     */
    private MultipartFile buildMultipartFile(final boolean empty) {
        return new MockMultipartFile("file", "originalName", "text",
                                     empty ? "".getBytes() : "test".getBytes());
    }

    /**
     * Builds a dummy MultipartFile with some content
     */
    private MultipartFile buildMultipartFile() {
        return buildMultipartFile(false);
    }
}
