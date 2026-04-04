package com.nomaddeveloper.securai;

import static com.nomaddeveloper.securai.internal.helper.InterceptorHelper.extractFieldValues;
import static com.nomaddeveloper.securai.internal.helper.InterceptorHelper.extractFields;
import static com.nomaddeveloper.securai.internal.helper.InterceptorHelper.getSecuredAnnotation;
import static com.nomaddeveloper.securai.internal.model.InterceptorMessage.CLASSIFIER_ERROR;
import static com.nomaddeveloper.securai.internal.model.InterceptorMessage.CLASSIFIER_ERROR_BLOCKED;
import static com.nomaddeveloper.securai.internal.model.InterceptorMessage.INITIAL_ERROR;
import static com.nomaddeveloper.securai.internal.model.InterceptorMessage.INTERCEPTING_REQUEST;
import static com.nomaddeveloper.securai.internal.model.InterceptorMessage.SECURITY_ANALYSIS_TIMEOUT;
import static com.nomaddeveloper.securai.internal.model.InterceptorMessage.SECURITY_ANALYSIS_TIMEOUT_BLOCKED;
import static com.nomaddeveloper.securai.internal.model.InterceptorMessage.SECURITY_THREAT_DETECTED;
import static com.nomaddeveloper.securai.internal.model.InterceptorMessage.THREAD_INTERRUPTED;
import static com.nomaddeveloper.securai.internal.model.InterceptorMessage.THREAD_INTERRUPTED_BLOCKED;

import androidx.annotation.NonNull;

import com.nomaddeveloper.securai.annotation.Secured;
import com.nomaddeveloper.securai.internal.callback.ClassifyListener;
import com.nomaddeveloper.securai.internal.callback.ClassifyListenerImpl;
import com.nomaddeveloper.securai.internal.helper.XSSClassifierHelper;
import com.nomaddeveloper.securai.internal.logger.SecuraiLogger;
import com.nomaddeveloper.securai.internal.model.SecuraiResult;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

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
    private static final long LATCH_TIMEOUT = 5L;
    private static final int DEFAULT_LATCH_COUNT = 1;
    private final XSSClassifierHelper xssClassifierHelper;
    private final DeniedResponse deniedResponse;
    private final boolean failClosed;

    /**
     * Constructs a new {@code SecuraiInterceptor} using the provided {@link SecuraiInterceptorBuilder}.
     * This constructor initializes the interceptor with the configuration specified in the builder,
     * including setting up logging, initializing the XSS classifier, and configuring the denied response handler.
     *
     * @param builder The {@link SecuraiInterceptorBuilder} containing the configuration for this interceptor.
     *                Must not be null.
     */
    SecuraiInterceptor(@NonNull final SecuraiInterceptorBuilder builder) {
        SecuraiLogger.setLoggingEnabled(builder.isLoggingEnabled());
        this.xssClassifierHelper = new XSSClassifierHelper(builder.getContext(), builder.getThreshold());
        this.deniedResponse = builder.getDeniedResponse();
        this.failClosed = builder.isFailClosed();
    }

    @NonNull
    @Override
    public Response intercept(@NonNull final Chain chain) throws IOException {
        final Request request = chain.request();

        SecuraiLogger.debug(TAG, INTERCEPTING_REQUEST.getMessage() + request.url());

        final Invocation invocation = request.tag(Invocation.class);
        if (invocation == null) {
            return chain.proceed(request);
        }

        final Secured secured = getSecuredAnnotation(invocation);
        if (secured == null) {
            return chain.proceed(request);
        }

        final CountDownLatch latch = new CountDownLatch(DEFAULT_LATCH_COUNT);
        final AtomicReference<SecuraiResult> result = new AtomicReference<>(new SecuraiResult(false, true, INITIAL_ERROR.getMessage()));

        final ClassifyListener listener = new ClassifyListenerImpl(latch, result);

        xssClassifierHelper.classify(extractFieldValues(request, extractFields(secured)), listener);

        try {
            if (!latch.await(LATCH_TIMEOUT, TimeUnit.SECONDS)) {
                if (failClosed) {
                    SecuraiLogger.error(TAG, SECURITY_ANALYSIS_TIMEOUT_BLOCKED.getMessage() + request.url());
                    return deniedResponse.onSecurityViolation(request, SECURITY_ANALYSIS_TIMEOUT_BLOCKED.getMessage());
                }
                SecuraiLogger.warn(TAG, SECURITY_ANALYSIS_TIMEOUT.getMessage() + request.url());
                return chain.proceed(request);
            }

            if (result.get().threatDetected()) {
                SecuraiLogger.error(TAG, SECURITY_THREAT_DETECTED.getMessage() + request.url());
                return deniedResponse.onSecurityViolation(request, result.get().value());
            }

            if (result.get().hasError()) {
                if (failClosed) {
                    SecuraiLogger.error(TAG, CLASSIFIER_ERROR_BLOCKED.getMessage() + request.url());
                    return deniedResponse.onSecurityViolation(request, result.get().value());
                }
                SecuraiLogger.warn(TAG, CLASSIFIER_ERROR.getMessage() + request.url());
            }

            return chain.proceed(request);
        } catch (final InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            if (failClosed) {
                SecuraiLogger.error(TAG, THREAD_INTERRUPTED_BLOCKED.getMessage() + request.url(), interruptedException);
                return deniedResponse.onSecurityViolation(request, THREAD_INTERRUPTED_BLOCKED.getMessage());
            }
            SecuraiLogger.error(TAG, THREAD_INTERRUPTED.getMessage() + request.url(), interruptedException);
            return chain.proceed(request);
        }
    }
}