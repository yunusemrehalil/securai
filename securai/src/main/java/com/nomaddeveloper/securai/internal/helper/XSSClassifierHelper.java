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
import static com.nomaddeveloper.securai.internal.model.SecuraiError.XSS_CATEGORY_MISSING;
import static com.nomaddeveloper.securai.internal.model.SecuraiError.XSS_CLASSIFIER_INIT_FAILED;
import static com.nomaddeveloper.securai.internal.model.SecuraiError.XSS_CLASSIFIER_NOT_INITIALIZED;
import static com.nomaddeveloper.securai.internal.model.XSS.THREAT;

import android.content.Context;

import androidx.annotation.RestrictTo;

import com.google.mediapipe.tasks.components.containers.Category;
import com.google.mediapipe.tasks.components.containers.ClassificationResult;
import com.google.mediapipe.tasks.core.BaseOptions;
import com.google.mediapipe.tasks.text.textclassifier.TextClassifier;
import com.google.mediapipe.tasks.text.textclassifier.TextClassifierResult;
import com.nomaddeveloper.securai.internal.callback.ClassifyListener;
import com.nomaddeveloper.securai.internal.logger.SecuraiLogger;
import com.nomaddeveloper.securai.internal.model.Field;
import com.nomaddeveloper.securai.internal.model.SecuraiRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * A helper class for detecting potential XSS (Cross-Site Scripting) threats in HTTP requests.
 * This class utilizes a TensorFlow Lite-based {@link TextClassifier} to analyze request content
 * and determine whether it poses a security risk.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class XSSClassifierHelper {

    private static final String TAG = XSSClassifierHelper.class.getCanonicalName();
    private static final String XSS_MODEL = "xss_model.tflite";
    private static final float XSS_SECURITY_THRESHOLD = 0.8f;
    private static final float DEFAULT_XSS_SCORE = -1f;
    private static final int CLASSIFICATION_RESULT_SIZE = 2;

    private final Context context;
    private TextClassifier xssClassifier;


    /**
     * Constructs an {@code XSSClassifierHelper} instance and initializes the classifier.
     *
     * @param context The application context used for loading the model.
     */
    public XSSClassifierHelper(Context context) {
        this.context = context;
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
            BaseOptions baseOptions = BaseOptions.builder()
                    .setModelAssetPath(XSS_MODEL)
                    .build();
            TextClassifier.TextClassifierOptions options = TextClassifier.TextClassifierOptions.builder()
                    .setBaseOptions(baseOptions)
                    .build();
            xssClassifier = TextClassifier.createFromOptions(context, options);
            SecuraiLogger.info(TAG, INITIALIZED_SUCCESS.getMessage());
        } catch (Exception e) {
            SecuraiLogger.error(TAG, XSS_CLASSIFIER_INIT_FAILED.getMessage() + ": " + e.getMessage());
        }
    }

    /**
     * Classifies an HTTP request for potential XSS threats.
     * Analyzes the request body, headers, and query parameters to identify security risks.
     *
     * @param request          The {@link SecuraiRequest} containing the request data to be analyzed.
     * @param classifyListener The {@link ClassifyListener} that handles the classification results.
     */
    public void classify(SecuraiRequest request, ClassifyListener classifyListener) {
        if (xssClassifier == null) {
            initClassifier();
            if (xssClassifier == null) {
                classifyListener.onError(TAG, XSS_CLASSIFIER_NOT_INITIALIZED.getMessage());
                return;
            }
        }

        if (request == null) {
            classifyListener.onError(TAG, NO_FIELD_VALUE_PROVIDED.getMessage());
            return;
        }

        Map<Field, Map<String, TextClassifierResult>> results = new HashMap<>();
        AtomicBoolean hasError = new AtomicBoolean(false);

        Consumer<String> errorHandler = (error) -> {
            if (!hasError.get()) {
                hasError.set(true);
                classifyListener.onError(TAG, error);
            }
        };

        if (request.getBody() != null && !request.getBody().isEmpty()) {
            SecuraiLogger.debug(TAG, CLASSIFYING_BODY.getMessage());
            if (isThreat(request.getBody(), results, BODY, errorHandler, classifyListener)) {
                return;
            }
        }

        if (hasError.get()) return;

        if (request.getHeaders() != null && !request.getHeaders().isEmpty()) {
            SecuraiLogger.debug(TAG, CLASSIFYING_HEADERS.getMessage());
            for (Map.Entry<String, List<String>> header : request.getHeaders().entrySet()) {
                for (String value : header.getValue()) {
                    if (isThreat(value, results, HEADER, errorHandler, classifyListener)) {
                        return;
                    }
                }
            }
        }

        if (hasError.get()) return;

        if (request.getQueryParameters() != null && !request.getQueryParameters().isEmpty()) {
            SecuraiLogger.debug(TAG, CLASSIFYING_PARAMS.getMessage());
            for (Map.Entry<String, String> queryParameter : request.getQueryParameters().entrySet()) {
                if (isThreat(queryParameter.getValue(), results, PARAM, errorHandler, classifyListener)) {
                    return;
                }
            }
        }

        if (hasError.get()) return;

        List<Long> timestamps = results.values().stream()
                .flatMap(map -> map.values().stream())
                .map(TextClassifierResult::timestampMs)
                .collect(Collectors.toList());

        SecuraiLogger.info(TAG, CLASSIFICATION_SUCCESS.getMessage() + timestamps);
        classifyListener.onResult(results);
    }

    /**
     * Analyzes a text value to determine if it poses an XSS security threat.
     *
     * <p>This method classifies the given text using the XSS classifier and evaluates
     * its threat level based on the classification score. If an error occurs during
     * classification, the provided {@code errorHandler} is invoked. If a security
     * threat is detected, the {@code classifyListener} is notified.</p>
     *
     * @param value            The text value to be analyzed.
     * @param results          A map that stores classification results for different fields.
     * @param field            The {@link Field} (e.g., BODY, HEADER, PARAM) where the value was extracted from.
     * @param errorHandler     A {@link Consumer} that handles errors during classification.
     * @param classifyListener The {@link ClassifyListener} used to notify security threats.
     * @return {@code true} if a security threat is detected, {@code false} otherwise.
     */
    private boolean isThreat(String value,
                             Map<Field, Map<String, TextClassifierResult>> results,
                             Field field,
                             Consumer<String> errorHandler,
                             ClassifyListener classifyListener) {
        SecuraiLogger.debug(TAG, ANALYZING_VALUE.getMessage() + field + "]: " + value);

        TextClassifierResult result;
        try {
            result = xssClassifier.classify(value);
        } catch (Exception e) {
            errorHandler.accept(NO_VALID_RESULT.getMessage() + value);
            return false;
        }

        if (result == null || result.classificationResult() == null ||
                result.classificationResult().classifications().isEmpty()) {
            errorHandler.accept(NO_VALID_RESULT.getMessage() + value);
            return false;
        }

        ClassificationResult classificationResult = result.classificationResult();
        List<Category> categories = classificationResult.classifications().get(0).categories();

        if (categories.size() != CLASSIFICATION_RESULT_SIZE) {
            errorHandler.accept(UNEXPECTED_RESULT_SIZE.getMessage() + value);
            return false;
        }

        float xssScore = categories.stream()
                .filter(category -> category.index() == THREAT.getValue())
                .map(Category::score)
                .findFirst()
                .orElse(DEFAULT_XSS_SCORE);

        if (xssScore == DEFAULT_XSS_SCORE) {
            errorHandler.accept(XSS_CATEGORY_MISSING.getMessage() + ": " + value);
            return false;
        }

        if (xssScore > XSS_SECURITY_THRESHOLD) {
            classifyListener.onThreatDetected(field, value);
            return true;
        }

        results.computeIfAbsent(field, k -> new HashMap<>()).put(value, result);
        return false;
    }
}