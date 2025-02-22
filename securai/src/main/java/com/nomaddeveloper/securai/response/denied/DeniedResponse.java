package com.nomaddeveloper.securai.response.denied;

import okhttp3.Request;
import retrofit2.Response;

/**
 * Interface defining methods for handling various request denial scenarios.
 * Implementations create and return Retrofit Response objects with appropriate error details.
 */
public interface DeniedResponse {

    /**
     * Handles requests blocked due to security vulnerabilities.
     *
     * @param request The original request
     * @param summary Summary of the detected vulnerability
     * @return Retrofit Response with error details
     */
    Response<?> onSecurityViolation(Request request, String summary);

    /**
     * Handles internal errors during security analysis.
     *
     * @param request The original request
     * @param error   The exception that occurred
     * @return Retrofit Response with error details
     */
    Response<?> onAnalysisError(Request request, Exception error);

    /**
     * Handles authentication failures.
     *
     * @param request The original request
     * @param message Details of the authentication failure
     * @return Retrofit Response with error details
     */
    Response<?> onAuthenticationFailure(Request request, String message);

    /**
     * Handles requests with invalid or malformed input.
     *
     * @param request The original request
     * @param message Details of the invalid input
     * @return Retrofit Response with error details
     */
    Response<?> onInvalidRequest(Request request, String message);

    /**
     * Handles rate limit exceedances.
     *
     * @param request The original request
     * @param message Details of the rate limit violation
     * @return Retrofit Response with error details
     */
    Response<?> onRateLimitExceeded(Request request, String message);

    /**
     * Handles service unavailability scenarios.
     *
     * @param request The original request
     * @param message Details of the service unavailability
     * @return Retrofit Response with error details
     */
    Response<?> onServiceUnavailable(Request request, String message);
}