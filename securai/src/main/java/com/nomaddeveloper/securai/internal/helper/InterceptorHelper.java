package com.nomaddeveloper.securai.internal.helper;

import androidx.annotation.RestrictTo;

import com.nomaddeveloper.securai.annotation.Secured;
import com.nomaddeveloper.securai.internal.logger.SecuraiLogger;
import com.nomaddeveloper.securai.internal.model.Field;
import com.nomaddeveloper.securai.internal.model.SecuraiRequest;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.GzipSource;
import retrofit2.Invocation;

/**
 * Helper class for intercepting and analyzing HTTP requests based on the {@link Secured} annotation.
 * This class provides methods to extract secured fields, their values, and handle various request body encoding scenarios.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class InterceptorHelper {

    private static final String TAG = InterceptorHelper.class.getSimpleName();
    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    /**
     * Retrieves the @Secured annotation from the given method invocation.
     *
     * @param invocation The method invocation to inspect.
     * @return The Secured annotation if present, otherwise null.
     */
    public static Secured getSecuredAnnotation(final Invocation invocation) {
        return Arrays.stream(invocation.method().getAnnotations())
                .filter(annotation -> annotation instanceof Secured)
                .map(annotation -> (Secured) annotation)
                .findFirst()
                .orElse(null);
    }

    /**
     * Extracts the fields specified in the @Secured annotation.
     *
     * @param secured The Secured annotation containing the field definitions.
     * @return A set of fields to be analyzed, defaulting to ALL if invalid values are found.
     */
    public static Set<Field> extractFields(final Secured secured) {
        final Field[] fields = secured.fields();
        if (fields.length == 0) {
            return EnumSet.of(Field.ALL);
        }
        try {
            return EnumSet.copyOf(Arrays.asList(fields));
        } catch (final IllegalArgumentException e) {
            SecuraiLogger.warn(TAG, "Invalid Field values in @Secured: " + Arrays.toString(fields) + ", defaulting to ALL");
            return EnumSet.of(Field.ALL);
        }
    }

    /**
     * Extracts values for the specified fields from the request, with robust UTF-8 detection.
     *
     * @param request The OkHttp Request to analyze
     * @param fields  The fields to extract (BODY, HEADER, PARAM, ALL)
     * @return A map of Field to their extracted values
     */
    public static SecuraiRequest extractFieldValues(final Request request, Set<Field> fields) {
        String body = null;
        Map<String, List<String>> headers = null;
        Map<String, String> queryParameters = null;

        if (fields.contains(Field.ALL)) {
            fields = EnumSet.of(Field.BODY, Field.HEADER, Field.PARAM);
        }

        for (final Field field : fields) {
            switch (field) {
                case BODY:
                    final RequestBody requestBody = request.body();
                    if (requestBody == null) {
                        break;
                    }

                    if (requestBody.isDuplex() || requestBody.isOneShot()) {
                        break;
                    }

                    try {
                        Buffer buffer = new Buffer();
                        requestBody.writeTo(buffer);

                        if ("gzip".equalsIgnoreCase(request.header("Content-Encoding"))) {
                            try (final GzipSource gzipSource = new GzipSource(buffer)) {
                                final Buffer unzippedBuffer = new Buffer();
                                unzippedBuffer.writeAll(gzipSource);
                                buffer = unzippedBuffer;
                            }
                        }

                        final MediaType contentType = requestBody.contentType();
                        final Charset charset = contentType != null
                                ? Objects.requireNonNullElse(contentType.charset(UTF_8), UTF_8)
                                : UTF_8;

                        if (isProbablyUtf8(buffer)) {
                            body = buffer.readString(charset);
                        }
                    } catch (final IOException e) {
                        SecuraiLogger.error(TAG, "Failed to extract request body: ", e);
                    }
                    break;

                case HEADER:
                    headers = request.headers().toMultimap();
                    break;

                case PARAM:
                    final Map<String, String> params = new HashMap<>();
                    request.url().queryParameterNames().forEach(name -> {
                        final String value = request.url().queryParameter(name);
                        params.put(name, value);
                    });
                    queryParameters = params;
                    break;
                default:
                    SecuraiLogger.warn(TAG, "Unknown field type: " + field);
                    break;
            }
        }

        return new SecuraiRequest(body, headers, queryParameters);
    }

    /**
     * Determines if the buffer content is probably UTF-8 encoded.
     * Adapted from OkHttp's HttpLoggingInterceptor.
     *
     * @param buffer The buffer to check
     * @return True if probably UTF-8, false otherwise
     */
    private static boolean isProbablyUtf8(final Buffer buffer) {
        try {
            final Buffer prefix = new Buffer();
            final long byteCount = Math.min(buffer.size(), 64);
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                final int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (final EOFException e) {
            return false; // Truncated UTF-8 sequence
        }
    }
}