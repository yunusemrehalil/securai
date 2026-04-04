package com.nomaddeveloper.securai.internal.model;

import androidx.annotation.RestrictTo;

/**
 * Represents the result of a security threat detection process.
 * This class encapsulates whether a security threat has been detected,
 * whether an error occurred during classification, and provides
 * additional information related to the result.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public record SecuraiResult(boolean threatDetected, boolean hasError, String value) {

}
