package tech.velco.testtechnique.exception.response;

import lombok.Builder;
import lombok.Value;

/**
 * Represents the response in case of an uncaught ApiException
 */
@Value
@Builder
public class ApiExceptionResponse {

    String error;
    Integer status;

}
