package com.nomaddeveloper.securai;

import static com.nomaddeveloper.securai.helper.InterceptorHelper.extractFieldValues;
import static com.nomaddeveloper.securai.helper.InterceptorHelper.extractFields;
import static com.nomaddeveloper.securai.helper.InterceptorHelper.getSecuredAnnotation;

import androidx.annotation.NonNull;

import com.nomaddeveloper.securai.annotation.Secured;
import com.nomaddeveloper.securai.response.denied.DeniedResponse;
import com.nomaddeveloper.securai.response.denied.DeniedResponseImpl;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Invocation;

public class SecuraiInterceptor implements Interceptor {

    private static final Logger LOGGER = Logger.getLogger(SecuraiInterceptor.class.getCanonicalName());

    private final DeniedResponse deniedResponse;

    /**
     * Constructs a new SecuraiInterceptor with default denied response handling.
     */
    public SecuraiInterceptor() {
        this(new DeniedResponseImpl());
    }

    /**
     * Constructs a new SecuraiInterceptor with custom denied response handling.
     *
     * @param deniedResponse Custom handler for denied responses
     */
    public SecuraiInterceptor(@NonNull DeniedResponse deniedResponse) {
        this.deniedResponse = deniedResponse;
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
