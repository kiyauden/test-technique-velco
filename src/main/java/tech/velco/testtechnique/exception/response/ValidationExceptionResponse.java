package tech.velco.testtechnique.exception.response;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class ValidationExceptionResponse {

    List<Error> errors;

    @Value
    @Builder
    public static class Error {

        String key;
        String error;

    }

}
