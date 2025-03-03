package com.nomaddeveloper.securai.internal.model;

/**
 * Enum representing different parts of an HTTP request that can be analyzed for security.
 */
public enum Field {

    /**
     * Analyze the request body (e.g., JSON payload).
     */
    BODY,

    /**
     * Analyze request headers.
     */
    HEADER,

    /**
     * Analyze request query parameters.
     */
    PARAM,

    /**
     * Analyze all parts of the request (default).
     */
    ALL
}

