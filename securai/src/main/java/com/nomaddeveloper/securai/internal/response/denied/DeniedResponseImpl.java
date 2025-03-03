package com.nomaddeveloper.securai.internal.response.denied;

import static com.nomaddeveloper.securai.internal.model.SecuraiError.SECURITY_THREAT_DETECTED;

import androidx.annotation.RestrictTo;

import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Default implementation of DeniedResponse that creates error responses and logs events.
 * Provides hooks for customization via subclassing.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class DeniedResponseImpl implements DeniedResponse {

    private static final String TAG = DeniedResponseImpl.class.getCanonicalName();

    /**
     * Handles requests blocked due to security vulnerabilities.
     * Logs a warning and returns a 403 response.
     */
    @Override
    public Response<ResponseBody> onSecurityViolation(Request request, String summary) {
        return createErrorResponse(request, 403, SECURITY_THREAT_DETECTED.getMessage());
    }

    /**
     * Creates a Retrofit error response with the specified code and message.
     *
     * @param request The original request
     * @param code    HTTP status code
     * @param message Error message
     * @return Retrofit Response with error details
     */
    protected Response<ResponseBody> createErrorResponse(Request request, int code, String message) {
        ResponseBody errorBody = ResponseBody.Companion.create(message, MediaType.get("text/plain"));
        okhttp3.Response rawResponse = new okhttp3.Response.Builder()
                .request(request)
                .code(code)
                .protocol(Protocol.HTTP_1_1)
                .message(message)
                .body(errorBody)
                .build();

        return Response.error(errorBody, rawResponse);
    }
}