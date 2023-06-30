package be.vlaanderen.vip.magda.legallogging.model;

import lombok.Builder;

/**
 * An "Annotatie" field within an {@link UitzonderingEntry}.
 * This contains further information on the Uitzondering in the form of a key-value map.
 * The set of keys in this map should be considered arbitrary and depends entirely on the specific type of Uitzondering.
 *
 * @param name the key, from the "Naam" field
 * @param value the value, from the "Waarde" field
 */
@Builder
public record AnnotatieField(String name, String value) { }
