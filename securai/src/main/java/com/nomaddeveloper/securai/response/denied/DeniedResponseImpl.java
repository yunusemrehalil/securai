package com.nomaddeveloper.securai.response.denied;

import android.util.Log;

import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Default implementation of DeniedResponse that creates error responses and logs events.
 * Provides hooks for customization via subclassing.
 */
public class DeniedResponseImpl implements DeniedResponse {

    private static final String TAG = DeniedResponseImpl.class.getCanonicalName();

    /**
     * Handles requests blocked due to security vulnerabilities.
     * Logs a warning and returns a 403 response.
     */
    @Override
    public Response<?> onSecurityViolation(Request request, String summary) {
        String message = "Security vulnerability detected: " + summary;
        Log.w(TAG, message);
        return createErrorResponse(request, 403, message);
    }

    /**
     * Handles internal errors during security analysis.
     * Logs an error and returns a 500 response.
     */
    @Override
    public Response<?> onAnalysisError(Request request, Exception error) {
        String message = "Internal error during security analysis: " + error.getMessage();
        Log.e(TAG, message, error);
        return createErrorResponse(request, 500, message);
    }

    /**
     * Handles authentication failures.
     * Logs a warning and returns a 401 response.
     */
    @Override
    public Response<?> onAuthenticationFailure(Request request, String message) {
        String fullMessage = "Authentication failed: " + message;
        Log.w(TAG, fullMessage);
        return createErrorResponse(request, 401, fullMessage);
    }

    /**
     * Handles requests with invalid or malformed input.
     * Logs a warning and returns a 400 response.
     */
    @Override
    public Response<?> onInvalidRequest(Request request, String message) {
        String fullMessage = "Invalid request detected: " + message;
        Log.w(TAG, fullMessage);
        return createErrorResponse(request, 400, fullMessage);
    }

    /**
     * Handles rate limit exceedances.
     * Logs a warning and returns a 429 response.
     */
    @Override
    public Response<?> onRateLimitExceeded(Request request, String message) {
        String fullMessage = "Rate limit exceeded: " + message;
        Log.w(TAG, fullMessage);
        return createErrorResponse(request, 429, fullMessage);
    }

    /**
     * Handles service unavailability scenarios.
     * Logs an error and returns a 503 response.
     */
    @Override
    public Response<?> onServiceUnavailable(Request request, String message) {
        String fullMessage = "Service unavailable: " + message;
        Log.e(TAG, fullMessage);
        return createErrorResponse(request, 503, fullMessage);
    }

    /**
     * Creates a Retrofit error response with the specified code and message.
     *
     * @param request The original request
     * @param code    HTTP status code
     * @param message Error message
     * @return Retrofit Response with error details
     */
    protected Response<?> createErrorResponse(Request request, int code, String message) {
        return Response.error(
                ResponseBody.create("", null),
                new okhttp3.Response.Builder()
                        .request(request)
                        .protocol(Protocol.HTTP_1_1)
                        .code(code)
                        .message(message)
                        .build()
        );
    }
}