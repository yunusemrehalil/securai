package com.nomaddeveloper.securai;

import static com.nomaddeveloper.securai.internal.model.SecuraiError.CONTEXT_MUST_NOT_BE_NULL;
import static com.nomaddeveloper.securai.internal.model.SecuraiError.THRESHOLD_IS_NOT_IN_BOUND;

import android.content.Context;

import androidx.annotation.NonNull;

import com.nomaddeveloper.securai.internal.response.denied.DeniedResponse;
import com.nomaddeveloper.securai.internal.response.denied.DeniedResponseImpl;
import com.nomaddeveloper.securai.internal.exception.SecuraiException;

import java.util.Objects;

/**
 * Builder class for creating instances of {@link SecuraiInterceptor}.
 * <p>
 * This class provides a fluent API to configure the settings of the {@link SecuraiInterceptor}
 * before instantiation. It allows setting options such as logging enablement, threshold for
 * security checks, and a custom denied response handler.
 */
public class SecuraiInterceptorBuilder {

    private final Context context;
    private boolean loggingEnabled = false;
    private float threshold = 0.8f;
    private DeniedResponse deniedResponse;

    /**
     * Constructs a new {@code SecuraiInterceptorBuilder} with the given application context.
     *
     * @param context The Android application context. Must not be null.
     * @throws NullPointerException if the provided context is null.
     */
    public SecuraiInterceptorBuilder(@NonNull Context context) {
        this.context = Objects.requireNonNull(context, CONTEXT_MUST_NOT_BE_NULL.getMessage());
    }

    /**
     * Enables or disables logging of security-related information by the interceptor.
     * When enabled, the interceptor will log details about the checks performed and their outcomes.
     * This can be useful for debugging and understanding the interceptor's behavior.
     *
     * @param loggingEnabled {@code true} to enable logging, {@code false} to disable. Defaults to {@code false}.
     * @return This {@code SecuraiInterceptorBuilder} instance for chaining method calls.
     */
    public SecuraiInterceptorBuilder setLoggingEnabled(boolean loggingEnabled) {
        this.loggingEnabled = loggingEnabled;
        return this;
    }

    /**
     * Sets the threshold value for security checks.
     * The threshold must be between 0.0 and 1.0, inclusive.
     *
     * @param threshold The threshold value between 0.0 and 1.0. Defaults to {@code 0.8f}.
     * @return This {@code SecuraiInterceptorBuilder} instance for chaining method calls.
     * @throws SecuraiException if the provided threshold is not within the valid range (0 to 1).
     */
    public SecuraiInterceptorBuilder setThreshold(float threshold) {
        if (threshold < 0 || threshold > 1) {
            throw new SecuraiException(THRESHOLD_IS_NOT_IN_BOUND.getMessage());
        }
        this.threshold = threshold;
        return this;
    }

    /**
     * Sets a custom {@link DeniedResponse} to be used when a request is denied by the interceptor.
     * This allows for customization of the response returned to the client when a security check fails.
     * If no custom {@code DeniedResponse} is provided, a default implementation will be used.
     *
     * @param deniedResponse The custom {@link DeniedResponse} implementation. Can be null to use the default.
     * @return This {@code SecuraiInterceptorBuilder} instance for chaining method calls.
     */
    public SecuraiInterceptorBuilder setDeniedResponse(DeniedResponse deniedResponse) {
        this.deniedResponse = deniedResponse;
        return this;
    }

    /**
     * Builds and returns a new instance of {@link SecuraiInterceptor} configured with the
     * settings provided to this builder.
     *
     * @return A new {@link SecuraiInterceptor} instance.
     */
    public SecuraiInterceptor build() {
        return new SecuraiInterceptor(this);
    }

    /**
     * Retrieves the application context associated with this builder.
     * This method is intended for internal use by the {@link SecuraiInterceptor} during its initialization.
     *
     * @return The Android application context.
     */
    Context getContext() {
        return context;
    }

    /**
     * Retrieves the logging enabled status configured in this builder.
     * This method is intended for internal use by the {@link SecuraiInterceptor}.
     *
     * @return {@code true} if logging is enabled, {@code false} otherwise.
     */
    boolean isLoggingEnabled() {
        return loggingEnabled;
    }

    /**
     * Retrieves the threshold value configured in this builder.
     * This method is intended for internal use by the {@link SecuraiInterceptor}.
     *
     * @return The threshold value.
     */
    float getThreshold() {
        return threshold;
    }

    /**
     * Retrieves the {@link DeniedResponse} configured in this builder.
     * If no custom {@code DeniedResponse} was set, this method returns a default implementation.
     * This method is intended for internal use by the {@link SecuraiInterceptor}.
     *
     * @return The configured {@link DeniedResponse} or a default instance.
     */
    DeniedResponse getDeniedResponse() {
        return deniedResponse != null ? deniedResponse : new DeniedResponseImpl();
    }
}