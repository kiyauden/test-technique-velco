package tech.velco.testtechnique.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Error {

    /**
     * Line of the error in the file (1 based indexing)
     */
    Integer line;
    String message;
    String value;

}
