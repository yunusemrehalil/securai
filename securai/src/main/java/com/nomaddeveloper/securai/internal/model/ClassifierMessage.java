package com.nomaddeveloper.securai.internal.model;

import androidx.annotation.RestrictTo;

/**
 * Enum representing classification messages.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public enum ClassifierMessage {
    INITIALIZING("Initializing XSS classifier..."),
    INITIALIZED_SUCCESS("XSS classifier initialized successfully."),
    CLASSIFYING_BODY("Classifying request body..."),
    CLASSIFYING_HEADERS("Classifying request headers..."),
    CLASSIFYING_PARAMS("Classifying request query parameters..."),
    CLASSIFICATION_SUCCESS("Request classified successfully. No threats detected. Classification timestamps: "),
    NO_VALID_RESULT("No valid classification result for value: "),
    UNEXPECTED_RESULT_SIZE("Unexpected classification result size for value: "),
    ANALYZING_VALUE("Analyzing value in field ["),
    THREAT_DETECTED("Threat detected in value: ");

    private final String message;

    ClassifierMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}