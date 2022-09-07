package tech.velco.testtechnique.model;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.velco.testtechnique.model.Color.R;
import static tech.velco.testtechnique.model.Color.fromString;

class ColorTest {

    @Test
    void fromString_WhenKnownShouldReturnColor() {
        final Optional<Color> optionalColor = fromString("R");
        assertTrue(optionalColor.isPresent());
        assertEquals(R, optionalColor.get());
    }

    @Test
    void fromString_WhenUnKnownShouldReturnEmptyOptional() {
        final Optional<Color> optionalColor = fromString("A");
        assertFalse(optionalColor.isPresent());
    }

}
