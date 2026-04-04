package com.nomaddeveloper.securai.internal.model;

import androidx.annotation.RestrictTo;

import java.util.List;
import java.util.Map;

/**
 * Represents an HTTP request intercepted by the security interceptor.
 * This record encapsulates the request body, headers, and query parameters,
 * allowing security validation and analysis.
 *
 * @param body            The request body as a string.
 * @param headers         A map of HTTP headers where the key is the header name
 *                        and the value is a list of header values.
 * @param queryParameters A map of query parameters where the key is the parameter name
 *                        and the value is the parameter value.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public record SecuraiRequest(
        String body,
        Map<String, List<String>> headers,
        Map<String, String> queryParameters
) {
}