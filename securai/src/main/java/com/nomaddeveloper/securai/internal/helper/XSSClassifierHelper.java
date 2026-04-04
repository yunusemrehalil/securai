package com.nomaddeveloper.securai.internal.helper;

import static com.nomaddeveloper.securai.internal.model.ClassifierMessage.ANALYZING_VALUE;
import static com.nomaddeveloper.securai.internal.model.ClassifierMessage.CLASSIFICATION_SUCCESS;
import static com.nomaddeveloper.securai.internal.model.ClassifierMessage.CLASSIFYING_BODY;
import static com.nomaddeveloper.securai.internal.model.ClassifierMessage.CLASSIFYING_HEADERS;
import static com.nomaddeveloper.securai.internal.model.ClassifierMessage.CLASSIFYING_PARAMS;
import static com.nomaddeveloper.securai.internal.model.ClassifierMessage.INITIALIZED_SUCCESS;
import static com.nomaddeveloper.securai.internal.model.ClassifierMessage.INITIALIZING;
import static com.nomaddeveloper.securai.internal.model.ClassifierMessage.NO_VALID_RESULT;
import static com.nomaddeveloper.securai.internal.model.ClassifierMessage.UNEXPECTED_RESULT_SIZE;
import static com.nomaddeveloper.securai.internal.model.Field.BODY;
import static com.nomaddeveloper.securai.internal.model.Field.HEADER;
import static com.nomaddeveloper.securai.internal.model.Field.PARAM;
import static com.nomaddeveloper.securai.internal.model.SecuraiError.NO_FIELD_VALUE_PROVIDED;
import static com.nomaddeveloper.securai.internal.model.SecuraiError.SECURITY_THREAT_DETECTED;
import static com.nomaddeveloper.securai.internal.model.SecuraiError.XSS_CATEGORY_MISSING;
import static com.nomaddeveloper.securai.internal.model.SecuraiError.XSS_CLASSIFIER_INIT_FAILED;
import static com.nomaddeveloper.securai.internal.model.SecuraiError.XSS_CLASSIFIER_NOT_INITIALIZED;
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


    /**
     * Constructs an {@code XSSClassifierHelper} instance and initializes the classifier.
     *
     * @param context The application context used for loading the model.
     */
    public XSSClassifierHelper(@NonNull final Context context, final float threshold) {
        this.context = context;
        this.threshold = threshold;
        initClassifier();
    }

    /**
     * Initializes the TensorFlow Lite XSS classifier.
     * Loads the pre-trained model and prepares it for classification.
     * If initialization fails, logs an error message.
     */
    public synchronized void initClassifier() {
        if (xssClassifier != null) return;
        try {
            SecuraiLogger.debug(TAG, INITIALIZING.getMessage());
            final BaseOptions baseOptions = BaseOptions.builder()
                    .setModelAssetPath(XSS_MODEL)
                    .build();
            final TextClassifier.TextClassifierOptions options = TextClassifier.TextClassifierOptions.builder()
                    .setBaseOptions(baseOptions)
                    .build();
            xssClassifier = TextClassifier.createFromOptions(context, options);
            SecuraiLogger.info(TAG, INITIALIZED_SUCCESS.getMessage());
        } catch (final Exception e) {
            SecuraiLogger.error(TAG, XSS_CLASSIFIER_INIT_FAILED.getMessage() + ": " + e.getMessage());
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
                SecuraiLogger.error(TAG, XSS_CLASSIFIER_NOT_INITIALIZED.getMessage());
                return new SecuraiResult(false, true, XSS_CLASSIFIER_NOT_INITIALIZED.getMessage());
            }
        }

        if (request == null) {
            SecuraiLogger.error(TAG, NO_FIELD_VALUE_PROVIDED.getMessage());
            return new SecuraiResult(false, true, NO_FIELD_VALUE_PROVIDED.getMessage());
        }

        if (request.getBody() != null && !request.getBody().isEmpty()) {
            SecuraiLogger.debug(TAG, CLASSIFYING_BODY.getMessage());
            final SecuraiResult result = isThreat(request.getBody(), BODY);
            if (result != null) return result;
        }

        if (request.getHeaders() != null && !request.getHeaders().isEmpty()) {
            SecuraiLogger.debug(TAG, CLASSIFYING_HEADERS.getMessage());
            for (final Map.Entry<String, List<String>> header : request.getHeaders().entrySet()) {
                for (final String value : header.getValue()) {
                    final SecuraiResult result = isThreat(value, HEADER);
                    if (result != null) return result;
                }
            }
        }

        if (request.getQueryParameters() != null && !request.getQueryParameters().isEmpty()) {
            SecuraiLogger.debug(TAG, CLASSIFYING_PARAMS.getMessage());
            for (final Map.Entry<String, String> queryParameter : request.getQueryParameters().entrySet()) {
                final SecuraiResult result = isThreat(queryParameter.getValue(), PARAM);
                if (result != null) return result;
            }
        }

        SecuraiLogger.info(TAG, CLASSIFICATION_SUCCESS.getMessage());
        return new SecuraiResult(false, false, "No threat detected.");
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
        SecuraiLogger.debug(TAG, ANALYZING_VALUE.getMessage() + field + "]: " + value);

        final TextClassifierResult result;
        try {
            result = xssClassifier.classify(value);
        } catch (final Exception e) {
            final String error = NO_VALID_RESULT.getMessage() + value;
            SecuraiLogger.error(TAG, error);
            return new SecuraiResult(false, true, error);
        }

        if (result == null || result.classificationResult() == null ||
                result.classificationResult().classifications().isEmpty()) {
            final String error = NO_VALID_RESULT.getMessage() + value;
            SecuraiLogger.error(TAG, error);
            return new SecuraiResult(false, true, error);
        }

        final ClassificationResult classificationResult = result.classificationResult();
        final List<Category> categories = classificationResult.classifications().get(0).categories();

        if (categories.size() != CLASSIFICATION_RESULT_SIZE) {
            final String error = UNEXPECTED_RESULT_SIZE.getMessage() + value;
            SecuraiLogger.error(TAG, error);
            return new SecuraiResult(false, true, error);
        }

        final float xssScore = categories.stream()
                .filter(category -> category.index() == THREAT.getValue())
                .map(Category::score)
                .findFirst()
                .orElse(DEFAULT_XSS_SCORE);

        if (xssScore == DEFAULT_XSS_SCORE) {
            final String error = XSS_CATEGORY_MISSING.getMessage() + ": " + value;
            SecuraiLogger.error(TAG, error);
            return new SecuraiResult(false, true, error);
        }

        if (xssScore > threshold) {
            SecuraiLogger.error(TAG, SECURITY_THREAT_DETECTED.getMessage() + " in " + field + ": " + value);
            return new SecuraiResult(true, false, value);
        }

        return null;
    }
}