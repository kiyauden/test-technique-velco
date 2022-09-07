package tech.velco.testtechnique.exception;

/**
 * Business exception for an API error
 */
public class ApiException extends RuntimeException {
    public ApiException(final String message) {
        super(message);
    }

    public ApiException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
