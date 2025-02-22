package com.nomaddeveloper.securai.helper;

import android.util.Log;

import com.nomaddeveloper.securai.Field;
import com.nomaddeveloper.securai.annotation.Secured;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.GzipSource;
import retrofit2.Invocation;

public class InterceptorHelper {

    private static final String TAG = InterceptorHelper.class.getCanonicalName();
    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    /**
     * Retrieves the @Secured annotation from the given method invocation.
     *
     * @param invocation The method invocation to inspect.
     * @return The Secured annotation if present, otherwise null.
     */
    public static Secured getSecuredAnnotation(Invocation invocation) {
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
    public static Set<Field> extractFields(Secured secured) {
        Field[] fields = secured.fields();
        if (fields.length == 0) {
            return EnumSet.of(Field.ALL);
        }
        try {
            return EnumSet.copyOf(Arrays.asList(fields));
        } catch (IllegalArgumentException e) {
            Log.w(TAG, "Invalid Field values in @Secured: " + Arrays.toString(fields) + ", defaulting to ALL", e);
            return EnumSet.of(Field.ALL);
        }
    }

    /**
     * Extracts values for the specified fields from the request, with robust UTF-8 detection.
     *
     * @param request         The OkHttp Request to analyze
     * @param fieldsToAnalyze The fields to extract (BODY, HEADER, PARAM, ALL)
     * @return A map of Field to their extracted values
     */
    public static Map<Field, Object> extractFieldValues(Request request, Set<Field> fieldsToAnalyze) {
        Map<Field, Object> fieldValues = new HashMap<>();

        if (fieldsToAnalyze.contains(Field.ALL)) {
            fieldsToAnalyze = EnumSet.of(Field.BODY, Field.HEADER, Field.PARAM);
        }

        for (Field field : fieldsToAnalyze) {
            switch (field) {
                case BODY:
                    RequestBody requestBody = request.body();
                    if (requestBody == null) {
                        fieldValues.put(Field.BODY, null);
                        break;
                    }

                    if (requestBody.isDuplex() || requestBody.isOneShot()) {
                        fieldValues.put(Field.BODY, "[Duplex or one-shot body not extracted]");
                        break;
                    }

                    try {
                        Buffer buffer = new Buffer();
                        requestBody.writeTo(buffer);

                        // Handle gzip compression
                        if ("gzip".equalsIgnoreCase(request.header("Content-Encoding"))) {
                            try (GzipSource gzipSource = new GzipSource(buffer)) {
                                Buffer unzippedBuffer = new Buffer();
                                unzippedBuffer.writeAll(gzipSource);
                                buffer = unzippedBuffer;
                            }
                        }

                        MediaType contentType = requestBody.contentType();
                        Charset charset = contentType != null ? contentType.charset(UTF_8) : UTF_8;

                        if (isProbablyUtf8(buffer)) {
                            fieldValues.put(Field.BODY, buffer.readString(charset));
                        } else {
                            fieldValues.put(Field.BODY, "[Binary body, " + requestBody.contentLength() + "-byte]");
                        }
                    } catch (IOException e) {
                        Log.w(TAG, "Failed to extract request body", e);
                        fieldValues.put(Field.BODY, "[Error extracting body: " + e.getMessage() + "]");
                    }
                    break;

                case HEADER:
                    fieldValues.put(Field.HEADER, request.headers().toMultimap());
                    break;

                case PARAM:
                    Map<String, String> params = new HashMap<>();
                    request.url().queryParameterNames().forEach(name -> {
                        String value = request.url().queryParameter(name);
                        params.put(name, value);
                    });
                    fieldValues.put(Field.PARAM, params);
                    break;

                default:
                    Log.w(TAG, "Unknown field type: " + field);
                    break;
            }
        }

        return fieldValues;
    }

    /**
     * Determines if the buffer content is probably UTF-8 encoded.
     * Adapted from OkHttp's HttpLoggingInterceptor.
     *
     * @param buffer The buffer to check
     * @return True if probably UTF-8, false otherwise
     */
    private static boolean isProbablyUtf8(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = Math.min(buffer.size(), 64);
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence
        }
    }
}
