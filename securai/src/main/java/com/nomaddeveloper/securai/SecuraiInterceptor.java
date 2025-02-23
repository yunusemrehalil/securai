package com.nomaddeveloper.securai;

import static com.nomaddeveloper.securai.helper.InterceptorHelper.extractFieldValues;
import static com.nomaddeveloper.securai.helper.InterceptorHelper.extractFields;
import static com.nomaddeveloper.securai.helper.InterceptorHelper.getSecuredAnnotation;

import android.content.Context;

import androidx.annotation.NonNull;

import com.nomaddeveloper.securai.annotation.Secured;
import com.nomaddeveloper.securai.callback.ClassifyListenerImpl;
import com.nomaddeveloper.securai.helper.XSSClassifierHelper;
import com.nomaddeveloper.securai.model.Field;
import com.nomaddeveloper.securai.response.denied.DeniedResponse;
import com.nomaddeveloper.securai.response.denied.DeniedResponseImpl;

import java.io.IOException;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Invocation;

/**
 * An OkHttp interceptor that analyzes HTTP requests for potential security vulnerabilities using the {@link Secured} annotation.
 * This interceptor extracts specified fields from the request and performs security checks using an XSS classifier.
 */
public class SecuraiInterceptor implements Interceptor {

    private final XSSClassifierHelper XSSClassifierHelper;
    private final DeniedResponse deniedResponse;
    private final boolean loggingEnabled;

    /**
     * Constructs a new SecuraiInterceptor with default denied response handling.
     *
     * @param context        The application context.
     * @param loggingEnabled Enables or disables logging.
     */
    public SecuraiInterceptor(@NonNull Context context, boolean loggingEnabled) {
        this(context, new DeniedResponseImpl(), loggingEnabled);
    }

    /**
     * Constructs a new SecuraiInterceptor with custom denied response handling.
     *
     * @param context        The application context.
     * @param deniedResponse Custom handler for denied responses.
     * @param loggingEnabled Enables or disables logging.
     */
    public SecuraiInterceptor(@NonNull Context context, @NonNull DeniedResponse deniedResponse, boolean loggingEnabled) {
        this.XSSClassifierHelper = new XSSClassifierHelper(context, new ClassifyListenerImpl());
        this.deniedResponse = deniedResponse;
        this.loggingEnabled = loggingEnabled;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();

        Invocation invocation = request.tag(Invocation.class);
        if (invocation == null) {
            return chain.proceed(request);
        }

        Secured secured = getSecuredAnnotation(invocation);
        if (secured == null) {
            return chain.proceed(request);
        }

        Map<Field, Object> fieldValues = extractFieldValues(request, extractFields(secured));

        //TODO() TextClassification implementation

        return chain.proceed(request);
    }
}
