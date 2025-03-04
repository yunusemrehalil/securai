package com.nomaddeveloper.securai.internal.model;

import androidx.annotation.RestrictTo;

import com.nomaddeveloper.securai.internal.exception.SecuraiException;

/**
 * Enum representing various error messages for Securai.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public enum SecuraiError {

    UNKNOWN_ERROR("Unknown classification error occurred."),
    XSS_CLASSIFIER_INIT_FAILED("Failed to initialize XSS classifier."),
    THREAD_INTERRUPTED("Thread interrupted while waiting for XSS classification."),
    XSS_CLASSIFIER_NOT_INITIALIZED("XSS classifier is not initialized."),
    NO_FIELD_VALUE_PROVIDED("No field value provided for classification."),
    XSS_CATEGORY_MISSING("Category with index '1' (XSS) not found."),
    SECURITY_THREAT_DETECTED("Request contains a security threat");

    private final String message;

    SecuraiError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public SecuraiException toException() {
        return new SecuraiException(message);
    }

    public SecuraiException toException(Throwable cause) {
        return new SecuraiException(message, cause);
    }
}
