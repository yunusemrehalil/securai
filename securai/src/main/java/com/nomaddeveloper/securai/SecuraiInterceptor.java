package com.nomaddeveloper.securai;

import static com.nomaddeveloper.securai.internal.helper.InterceptorHelper.extractFieldValues;
import static com.nomaddeveloper.securai.internal.helper.InterceptorHelper.extractFields;
import static com.nomaddeveloper.securai.internal.helper.InterceptorHelper.getSecuredAnnotation;
import static com.nomaddeveloper.securai.internal.model.SecuraiMessages.CLASSIFIER_ERROR;
import static com.nomaddeveloper.securai.internal.model.SecuraiMessages.CLASSIFIER_ERROR_BLOCKED;
import static com.nomaddeveloper.securai.internal.model.SecuraiMessages.INTERCEPTING_REQUEST;
import static com.nomaddeveloper.securai.internal.model.SecuraiMessages.SECURITY_THREAT_DETECTED_BLOCKED;

import androidx.annotation.NonNull;

import com.nomaddeveloper.securai.annotation.Secured;
import com.nomaddeveloper.securai.internal.helper.XSSClassifierHelper;
import com.nomaddeveloper.securai.internal.logger.SecuraiLogger;
import com.nomaddeveloper.securai.internal.model.SecuraiResult;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Invocation;

/**
 * An OkHttp interceptor that analyzes HTTP requests for potential security vulnerabilities using the {@link Secured} annotation.
 * This interceptor extracts specified fields from the request and performs security checks using an XSS classifier.
 */
public class SecuraiInterceptor implements Interceptor {

    private static final String TAG = SecuraiInterceptor.class.getSimpleName();
    private final XSSClassifierHelper xssClassifierHelper;
    private final DeniedResponse deniedResponse;
    private final boolean failClosed;
    private final SecuraiLogger logger;

    /**
     * Constructs a new {@code SecuraiInterceptor} using the provided {@link SecuraiInterceptorBuilder}.
     * This constructor initializes the interceptor with the configuration specified in the builder,
     * including setting up logging, initializing the XSS classifier, and configuring the denied response handler.
     *
     * @param builder The {@link SecuraiInterceptorBuilder} containing the configuration for this interceptor.
     *                Must not be null.
     */
    SecuraiInterceptor(@NonNull final SecuraiInterceptorBuilder builder) {
        this.logger = new SecuraiLogger(builder.isLoggingEnabled());
        this.xssClassifierHelper = new XSSClassifierHelper(builder.getContext(), builder.getThreshold(), logger);
        this.deniedResponse = builder.getDeniedResponse();
        this.failClosed = builder.isFailClosed();
    }

    @NonNull
    @Override
    public Response intercept(@NonNull final Chain chain) throws IOException {
        final Request request = chain.request();

        logger.debug(TAG, INTERCEPTING_REQUEST + request.url());

        final Invocation invocation = request.tag(Invocation.class);
        if (invocation == null) {
            return chain.proceed(request);
        }

        final Secured secured = getSecuredAnnotation(invocation);
        if (secured == null) {
            return chain.proceed(request);
        }

        final SecuraiResult result = xssClassifierHelper.classify(extractFieldValues(request, extractFields(secured, logger), logger));

        if (result.threatDetected()) {
            logger.error(TAG, SECURITY_THREAT_DETECTED_BLOCKED + request.url());
            return deniedResponse.onSecurityViolation(request, result.value());
        }

        if (result.hasError()) {
            if (failClosed) {
                logger.error(TAG, CLASSIFIER_ERROR_BLOCKED + request.url());
                return deniedResponse.onSecurityViolation(request, result.value());
            }
            logger.warn(TAG, CLASSIFIER_ERROR + request.url());
        }

        return chain.proceed(request);
    }
}