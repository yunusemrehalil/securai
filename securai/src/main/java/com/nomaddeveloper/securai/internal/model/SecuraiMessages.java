package com.nomaddeveloper.securai.internal.model;

import androidx.annotation.RestrictTo;

/**
 * Consolidates all internal messages and error strings used throughout the library.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class SecuraiMessages {

    // Builder messages
    public static final String CONTEXT_MUST_NOT_BE_NULL = "Context must not be null.";
    public static final String THRESHOLD_IS_NOT_IN_BOUND = "Threshold must be between 0 and 1";

    // Classifier status messages
    public static final String INITIALIZING = "Initializing XSS classifier...";
    public static final String INITIALIZED_SUCCESS = "XSS classifier initialized successfully.";
    public static final String CLASSIFYING_BODY = "Classifying request body...";
    public static final String CLASSIFYING_HEADERS = "Classifying request headers...";
    public static final String CLASSIFYING_PARAMS = "Classifying request query parameters...";
    public static final String CLASSIFICATION_SUCCESS = "Request classified successfully. No threats detected.";
    public static final String NO_THREAT_DETECTED = "No threat detected.";

    // Classifier error messages
    public static final String XSS_CLASSIFIER_INIT_FAILED = "Failed to initialize XSS classifier.";
    public static final String XSS_CLASSIFIER_NOT_INITIALIZED = "XSS classifier is not initialized.";
    public static final String NO_FIELD_VALUE_PROVIDED = "No field value provided for classification.";
    public static final String XSS_CATEGORY_MISSING = "Category with index '1' (XSS) not found.";
    public static final String NO_VALID_RESULT = "No valid classification result for value: ";
    public static final String UNEXPECTED_RESULT_SIZE = "Unexpected classification result size for value: ";
    public static final String ANALYZING_VALUE = "Analyzing value in field [";

    // Interceptor messages
    public static final String CLASSIFIER_ERROR = "Classifier error. Proceeding request: ";
    public static final String CLASSIFIER_ERROR_BLOCKED = "Classifier error. Blocking request: ";
    public static final String INTERCEPTING_REQUEST = "Intercepting request: ";
    public static final String SECURITY_THREAT_DETECTED_BLOCKED = "Security threat detected! Blocking request: ";

    // General threat message
    public static final String SECURITY_THREAT_DETECTED = "Request contains a security threat";

    private SecuraiMessages() {
        throw new UnsupportedOperationException("Utility class - cannot be instantiated.");
    }
}
