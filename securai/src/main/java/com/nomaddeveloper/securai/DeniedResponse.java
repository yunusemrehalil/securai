package com.nomaddeveloper.securai;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Interface defining methods for handling various request denial scenarios.
 * Implementations create and return OkHttp Response objects with appropriate error details.
 */
public interface DeniedResponse {

    /**
     * Handles requests blocked due to security vulnerabilities.
     *
     * @param request The original request
     * @param summary Summary of the detected vulnerability
     * @return OkHttp Response with error details
     */
    Response onSecurityViolation(Request request, String summary);
}