package tech.velco.testtechnique.model;

import lombok.Builder;
import lombok.Value;

/**
 * Data class representing a reference
 */
@Value
@Builder
public class Reference {

    String numReference;
    Integer size;
    Float price;
    Color type;

}
