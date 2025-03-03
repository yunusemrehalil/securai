package com.nomaddeveloper.securai.internal.response.denied;

import androidx.annotation.RestrictTo;

import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Interface defining methods for handling various request denial scenarios.
 * Implementations create and return Retrofit Response objects with appropriate error details.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public interface DeniedResponse {

    /**
     * Handles requests blocked due to security vulnerabilities.
     *
     * @param request The original request
     * @param summary Summary of the detected vulnerability
     * @return Retrofit Response with error details
     */
    Response<ResponseBody> onSecurityViolation(Request request, String summary);
}