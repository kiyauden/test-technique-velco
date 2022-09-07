package tech.velco.testtechnique.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum representing all the possible errors on a line
 */
@Getter
@RequiredArgsConstructor
public enum ErrorMessage {

    INCORRECT_VALUE_COLOR("Incorrect value for color, must be R, G or B"),
    INCORRECT_NUMBER_OF_CHARACTERS_NUM_REFERENCE(
            "Incorrect number of characters for the reference number (10 expected)"),
    INCORRECT_FORMAT_PRICE("Incorrect format for the price, must be a float"),
    INCORRECT_FORMAT_SIZE("Incorrect format for the size, must be an integer");

    private final String message;

}
