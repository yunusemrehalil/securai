package com.nomaddeveloper.securai.internal.helper;

import static com.nomaddeveloper.securai.internal.model.SecuraiMessages.ANALYZING_VALUE;
import static com.nomaddeveloper.securai.internal.model.SecuraiMessages.CLASSIFICATION_SUCCESS;
import static com.nomaddeveloper.securai.internal.model.SecuraiMessages.CLASSIFYING_BODY;
import static com.nomaddeveloper.securai.internal.model.SecuraiMessages.CLASSIFYING_HEADERS;
import static com.nomaddeveloper.securai.internal.model.SecuraiMessages.CLASSIFYING_PARAMS;
import static com.nomaddeveloper.securai.internal.model.SecuraiMessages.INITIALIZED_SUCCESS;
import static com.nomaddeveloper.securai.internal.model.SecuraiMessages.INITIALIZING;
import static com.nomaddeveloper.securai.internal.model.SecuraiMessages.NO_FIELD_VALUE_PROVIDED;
import static com.nomaddeveloper.securai.internal.model.SecuraiMessages.NO_THREAT_DETECTED;
import static com.nomaddeveloper.securai.internal.model.Field.BODY;
import static com.nomaddeveloper.securai.internal.model.Field.HEADER;
import static com.nomaddeveloper.securai.internal.model.Field.PARAM;
import static com.nomaddeveloper.securai.internal.model.SecuraiMessages.NO_VALID_RESULT;
import static com.nomaddeveloper.securai.internal.model.SecuraiMessages.SECURITY_THREAT_DETECTED;
import static com.nomaddeveloper.securai.internal.model.SecuraiMessages.UNEXPECTED_RESULT_SIZE;
import static com.nomaddeveloper.securai.internal.model.SecuraiMessages.XSS_CATEGORY_MISSING;
import static com.nomaddeveloper.securai.internal.model.SecuraiMessages.XSS_CLASSIFIER_INIT_FAILED;
import static com.nomaddeveloper.securai.internal.model.SecuraiMessages.XSS_CLASSIFIER_NOT_INITIALIZED;
import static com.nomaddeveloper.securai.internal.model.XSS.THREAT;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import com.google.mediapipe.tasks.components.containers.Category;
import com.google.mediapipe.tasks.components.containers.ClassificationResult;
import com.google.mediapipe.tasks.core.BaseOptions;
import com.google.mediapipe.tasks.text.textclassifier.TextClassifier;
import com.google.mediapipe.tasks.text.textclassifier.TextClassifierResult;
import com.nomaddeveloper.securai.internal.logger.SecuraiLogger;
import com.nomaddeveloper.securai.internal.model.Field;
import com.nomaddeveloper.securai.internal.model.SecuraiRequest;
import com.nomaddeveloper.securai.internal.model.SecuraiResult;

import java.util.List;
import java.util.Map;

/**
 * A helper class for detecting potential XSS (Cross-Site Scripting) threats in HTTP requests.
 * This class utilizes a TensorFlow Lite-based {@link TextClassifier} to analyze request content
 * and determine whether it poses a security risk.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class XSSClassifierHelper {

    private static final String TAG = XSSClassifierHelper.class.getSimpleName();
    private static final String XSS_MODEL = "xss_model.tflite";
    private static final float DEFAULT_XSS_SCORE = -1f;
    private static final int CLASSIFICATION_RESULT_SIZE = 2;
    private final Context context;
    private final float threshold;
    private TextClassifier xssClassifier;
    private final SecuraiLogger logger;


    /**
     * Constructs a new XSSClassifierHelper.
     *
     * @param context   The application context, used to initialize the ML classifier.
     * @param threshold The classification score threshold above which a value is considered a threat.
     * @param logger    The logger instance.
     */
    public XSSClassifierHelper(@NonNull final Context context, final float threshold, final SecuraiLogger logger) {
        this.context = context;
        this.threshold = threshold;
        this.logger = logger;
    }

    /**
     * Initializes the TensorFlow Lite XSS classifier.
     * Loads the pre-trained model and prepares it for classification.
     * If initialization fails, logs an error message.
     */
    public synchronized void initClassifier() {
        if (xssClassifier != null) return;
        try {
            logger.debug(TAG, INITIALIZING);
            final BaseOptions baseOptions = BaseOptions.builder()
                    .setModelAssetPath(XSS_MODEL)
                    .build();
            final TextClassifier.TextClassifierOptions options = TextClassifier.TextClassifierOptions.builder()
                    .setBaseOptions(baseOptions)
                    .build();
            xssClassifier = TextClassifier.createFromOptions(context, options);
            logger.info(TAG, INITIALIZED_SUCCESS);
        } catch (final Exception e) {
            logger.error(TAG, XSS_CLASSIFIER_INIT_FAILED + ": " + e.getMessage());
        }
    }

    /**
     * Classifies an HTTP request for potential XSS threats.
     * Analyzes the request body, headers, and query parameters to identify security risks.
     *
     * @param request          The {@link SecuraiRequest} containing the request data to be analyzed.
     * @return                 A {@link SecuraiResult} indicating the outcome.
     */
    public SecuraiResult classify(final SecuraiRequest request) {
        if (xssClassifier == null) {
            initClassifier();
            if (xssClassifier == null) {
                logger.error(TAG, XSS_CLASSIFIER_NOT_INITIALIZED);
                return new SecuraiResult(false, true, XSS_CLASSIFIER_NOT_INITIALIZED);
            }
        }

        if (request == null) {
            logger.error(TAG, NO_FIELD_VALUE_PROVIDED);
            return new SecuraiResult(false, true, NO_FIELD_VALUE_PROVIDED);
        }

        if (request.body() != null && !request.body().isEmpty()) {
            logger.debug(TAG, CLASSIFYING_BODY);
            final SecuraiResult result = isThreat(request.body(), BODY);
            if (result != null) return result;
        }

        if (request.headers() != null && !request.headers().isEmpty()) {
            logger.debug(TAG, CLASSIFYING_HEADERS);
            for (final Map.Entry<String, List<String>> header : request.headers().entrySet()) {
                for (final String value : header.getValue()) {
                    final SecuraiResult result = isThreat(value, HEADER);
                    if (result != null) return result;
                }
            }
        }

        if (request.queryParameters() != null && !request.queryParameters().isEmpty()) {
            logger.debug(TAG, CLASSIFYING_PARAMS);
            for (final Map.Entry<String, String> queryParameter : request.queryParameters().entrySet()) {
                final SecuraiResult result = isThreat(queryParameter.getValue(), PARAM);
                if (result != null) return result;
            }
        }

        logger.info(TAG, CLASSIFICATION_SUCCESS);
        return new SecuraiResult(false, false, NO_THREAT_DETECTED);
    }

    /**
     * Analyzes a text value to determine if it poses an XSS security threat.
     *
     * <p>This method classifies the given text using the XSS classifier and evaluates
     * its threat level based on the classification score.
     *
     * @param value            The text value to be analyzed.
     * @param field            The {@link Field} (e.g., BODY, HEADER, PARAM) where the value was extracted from.
     * @return A {@link SecuraiResult} if a threat or error is detected, or {@code null} if clean.
     */
    private SecuraiResult isThreat(final String value, final Field field) {
        logger.debug(TAG, ANALYZING_VALUE + field + "]: " + value);

        final TextClassifierResult result;
        try {
            result = xssClassifier.classify(value);
        } catch (final Exception e) {
            final String error = NO_VALID_RESULT + value;
            logger.error(TAG, error);
            return new SecuraiResult(false, true, error);
        }

        if (result == null || result.classificationResult() == null ||
                result.classificationResult().classifications().isEmpty()) {
            final String error = NO_VALID_RESULT + value;
            logger.error(TAG, error);
            return new SecuraiResult(false, true, error);
        }

        final ClassificationResult classificationResult = result.classificationResult();
        final List<Category> categories = classificationResult.classifications().get(0).categories();

        if (categories.size() != CLASSIFICATION_RESULT_SIZE) {
            final String error = UNEXPECTED_RESULT_SIZE + value;
            logger.error(TAG, error);
            return new SecuraiResult(false, true, error);
        }

        final float xssScore = categories.stream()
                .filter(category -> category.index() == THREAT.getValue())
                .map(Category::score)
                .findFirst()
                .orElse(DEFAULT_XSS_SCORE);

        if (xssScore == DEFAULT_XSS_SCORE) {
            final String error = XSS_CATEGORY_MISSING + ": " + value;
            logger.error(TAG, error);
            return new SecuraiResult(false, true, error);
        }

        if (xssScore > threshold) {
            logger.error(TAG, SECURITY_THREAT_DETECTED + " in " + field + ": " + value);
            return new SecuraiResult(true, false, value);
        }

        return null;
    }
}