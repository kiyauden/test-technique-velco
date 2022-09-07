package tech.velco.testtechnique.model;

import java.util.Optional;

import static java.util.stream.Stream.of;

/**
 * Represents the reference's color (type in the json)
 */
public enum Color {

    R,
    G,
    B;

    /**
     * Return an optional of a color, given a string
     *
     * @param color the color to find
     * @return an optional of a Color
     */
    public static Optional<Color> fromString(final String color) {
        return of(Color.values())
                .filter(c -> c.name().equals(color))
                .findFirst();
    }

}
