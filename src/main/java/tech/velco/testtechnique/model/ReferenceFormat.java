package tech.velco.testtechnique.model;

import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * Data class representing the rest service output
 */
@Value
@Builder
public class ReferenceFormat {

    String inputFile;
    List<Reference> references;
    List<Error> errors;

}
