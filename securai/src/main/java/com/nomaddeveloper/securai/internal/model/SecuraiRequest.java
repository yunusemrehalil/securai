package com.nomaddeveloper.securai.internal.model;

import androidx.annotation.RestrictTo;

import java.util.List;
import java.util.Map;

/**
 * Represents an HTTP request intercepted by the security interceptor.
 * This class encapsulates the request body, headers, and query parameters,
 * allowing security validation and analysis.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class SecuraiRequest {

    private String body;
    private Map<String, List<String>> headers;
    private Map<String, String> queryParameters;

    /**
     * Constructs a new {@code SecuraiRequest} with the given body, headers, and query parameters.
     *
     * @param body            The request body as a string.
     * @param headers         A map of HTTP headers where the key is the header name
     *                        and the value is a list of header values.
     * @param queryParameters A map of query parameters where the key is the parameter name
     *                        and the value is the parameter value.
     */
    public SecuraiRequest(String body, Map<String, List<String>> headers, Map<String, String> queryParameters) {
        this.body = body;
        this.headers = headers;
        this.queryParameters = queryParameters;
    }

    /**
     * Retrieves the request body.
     *
     * @return The body of the HTTP request.
     */
    public String getBody() {
        return body;
    }

    /**
     * Retrieves the HTTP headers of the request.
     *
     * @return A map containing header names and their corresponding values.
     */
    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    /**
     * Retrieves the query parameters of the request.
     *
     * @return A map containing query parameter names and values.
     */
    public Map<String, String> getQueryParameters() {
        return queryParameters;
    }

    /**
     * Sets the request body.
     *
     * @param body The new request body.
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * Sets the HTTP headers of the request.
     *
     * @param headers A map containing header names and their corresponding values.
     */
    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    /**
     * Sets the query parameters of the request.
     *
     * @param queryParameters A map containing query parameter names and values.
     */
    public void setQueryParameters(Map<String, String> queryParameters) {
        this.queryParameters = queryParameters;
    }
}