package com.nomaddeveloper.securai;

import static com.nomaddeveloper.securai.internal.helper.InterceptorHelper.extractFieldValues;
import static com.nomaddeveloper.securai.internal.helper.InterceptorHelper.extractFields;
import static com.nomaddeveloper.securai.internal.helper.InterceptorHelper.getSecuredAnnotation;
import static com.nomaddeveloper.securai.internal.model.InterceptorMessage.INITIAL_ERROR;
import static com.nomaddeveloper.securai.internal.model.InterceptorMessage.INTERCEPTING_REQUEST;
import static com.nomaddeveloper.securai.internal.model.InterceptorMessage.SECURITY_ANALYSIS_TIMEOUT;
import static com.nomaddeveloper.securai.internal.model.InterceptorMessage.SECURITY_THREAT_DETECTED;
import static com.nomaddeveloper.securai.internal.model.InterceptorMessage.THREAD_INTERRUPTED;

import android.content.Context;

import androidx.annotation.NonNull;

import com.nomaddeveloper.securai.annotation.Secured;
import com.nomaddeveloper.securai.internal.callback.ClassifyListener;
import com.nomaddeveloper.securai.internal.callback.ClassifyListenerImpl;
import com.nomaddeveloper.securai.internal.helper.XSSClassifierHelper;
import com.nomaddeveloper.securai.internal.logger.SecuraiLogger;
import com.nomaddeveloper.securai.internal.model.SecuraiResult;
import com.nomaddeveloper.securai.internal.response.denied.DeniedResponse;
import com.nomaddeveloper.securai.internal.response.denied.DeniedResponseImpl;

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
    private static final float DEFAULT_XSS_SECURITY_THRESHOLD = 0.8f;
    private final XSSClassifierHelper xssClassifierHelper;
    private final DeniedResponse deniedResponse;

    /**
     * Constructs a new SecuraiInterceptor with custom xss security threshold.
     *
     * @param context              The application context.
     * @param loggingEnabled       {@code true} to enable logging, {@code false} to disable it.
     * @param xssSecurityThreshold The security threshold, must be between 0 and 1.
     */
    public SecuraiInterceptor(@NonNull Context context, boolean loggingEnabled, float xssSecurityThreshold) {
        SecuraiLogger.setLoggingEnabled(loggingEnabled);
        this.xssClassifierHelper = new XSSClassifierHelper(context, xssSecurityThreshold);
        this.deniedResponse = new DeniedResponseImpl();
    }

    /**
     * Constructs a new SecuraiInterceptor with default xss security threshold.
     *
     * @param context        The application context.
     * @param loggingEnabled {@code true} to enable logging, {@code false} to disable it.
     */
    public SecuraiInterceptor(@NonNull Context context, boolean loggingEnabled) {
        this(context, loggingEnabled, DEFAULT_XSS_SECURITY_THRESHOLD);
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();

        SecuraiLogger.debug(TAG, INTERCEPTING_REQUEST.getMessage() + request.url());

        Invocation invocation = request.tag(Invocation.class);
        if (invocation == null) {
            return chain.proceed(request);
        }

        Secured secured = getSecuredAnnotation(invocation);
        if (secured == null) {
            return chain.proceed(request);
        }

        CountDownLatch latch = new CountDownLatch(DEFAULT_LATCH_COUNT);
        AtomicReference<SecuraiResult> result = new AtomicReference<>(new SecuraiResult(false, INITIAL_ERROR.getMessage()));

        ClassifyListener listener = new ClassifyListenerImpl(latch, result);

        xssClassifierHelper.classify(extractFieldValues(request, extractFields(secured)), listener);

        try {
            if (!latch.await(LATCH_TIMEOUT, TimeUnit.SECONDS)) {
                SecuraiLogger.warn(TAG, SECURITY_ANALYSIS_TIMEOUT.getMessage() + request.url());
                return chain.proceed(request);
            }

            if (result.get().threatDetected()) {
                SecuraiLogger.error(TAG, SECURITY_THREAT_DETECTED.getMessage() + request.url());
                return deniedResponse.onSecurityViolation(request, result.get().value()).raw();
            }

            return chain.proceed(request);
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            SecuraiLogger.error(TAG, THREAD_INTERRUPTED.getMessage(), interruptedException);
            return chain.proceed(request);
        }
    }
}