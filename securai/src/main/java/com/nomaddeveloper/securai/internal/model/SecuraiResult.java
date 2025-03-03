package com.nomaddeveloper.securai.internal.model;

import androidx.annotation.RestrictTo;

/**
 * Represents the result of a security threat detection process.
 * This class encapsulates whether a security threat has been detected
 * and provides additional information related to the detected threat.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public record SecuraiResult(boolean threatDetected, String value) {

    /**
     * Constructs a {@code SecuraiResult} instance.
     *
     * @param threatDetected {@code true} if a security threat is detected, otherwise {@code false}.
     * @param value          A descriptive message or value related to the detected threat.
     */
    public SecuraiResult {
    }

    /**
     * Checks whether a security threat has been detected.
     *
     * @return {@code true} if a threat is detected, otherwise {@code false}.
     */
    @Override
    public boolean threatDetected() {
        return threatDetected;
    }

    /**
     * Retrieves additional information or a message related to the detected threat.
     *
     * @return A string containing the threat details or analysis result.
     */
    @Override
    public String value() {
        return value;
    }
}
