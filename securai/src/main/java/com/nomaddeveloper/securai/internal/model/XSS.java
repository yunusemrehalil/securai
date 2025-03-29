package com.nomaddeveloper.securai.internal.model;

import androidx.annotation.RestrictTo;

/**
 * Enum representing the classification of a request based on potential XSS (Cross-Site Scripting) threats.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public enum XSS {

    /**
     * Indicates that the request contains an XSS threat.
     */
    THREAT(1),

    /**
     * Indicates that the request does not contain an XSS threat.
     */
    NOT_THREAT(0);

    private final int value;

    /**
     * Constructs an {@code XSS} enumeration with a corresponding integer value.
     *
     * @param value The integer representation of the classification.
     */
    XSS(final int value) {
        this.value = value;
    }

    /**
     * Retrieves the integer value associated with the classification.
     *
     * @return The classification value (1 for THREAT, 0 for NOT_THREAT).
     */
    public int getValue() {
        return value;
    }
}