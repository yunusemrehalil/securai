package com.nomaddeveloper.securai.internal.callback;

import static com.nomaddeveloper.securai.internal.model.SecuraiError.SECURITY_THREAT_DETECTED;
import static com.nomaddeveloper.securai.internal.model.SecuraiError.UNKNOWN_ERROR;

import androidx.annotation.RestrictTo;

import com.google.mediapipe.tasks.text.textclassifier.TextClassifierResult;
import com.nomaddeveloper.securai.internal.logger.SecuraiLogger;
import com.nomaddeveloper.securai.internal.model.Field;
import com.nomaddeveloper.securai.internal.model.SecuraiResult;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

/**
 * An implementation of the {@link ClassifyListener} interface that processes classification results and errors.
 * This class utilizes a {@link CountDownLatch} to synchronize classification completion
 * and updates an {@link AtomicReference} with the classification result.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class ClassifyListenerImpl implements ClassifyListener {

    private static final String TAG = ClassifyListenerImpl.class.getSimpleName();
    private static final String ERROR_PREFIX = "Error: ";
    private static final String NO_THREAT_DETECTED = "No threat detected.";
    private final CountDownLatch latch;
    private final AtomicReference<SecuraiResult> result;

    /**
     * Constructs a {@code ClassifyListenerImpl} instance.
     *
     * @param latch  A {@link CountDownLatch} used to signal the completion of classification.
     * @param result An {@link AtomicReference} used to store the classification result.
     */
    public ClassifyListenerImpl(CountDownLatch latch, AtomicReference<SecuraiResult> result) {
        this.latch = latch;
        this.result = result;
    }

    /**
     * Handles errors that occur during classification.
     * Logs the error message and signals completion using the latch.
     *
     * @param tag   The tag used for logging, typically indicating the source of the error.
     * @param error A descriptive error message indicating the cause of the failure.
     */
    @Override
    public void onError(String tag, String error) {
        if (error == null || error.isBlank()) {
            error = UNKNOWN_ERROR.getMessage();
        }
        if (tag == null || tag.isEmpty()) {
            tag = TAG;
        }
        SecuraiLogger.error(tag, error);
        result.set(new SecuraiResult(false, ERROR_PREFIX + error));
        latch.countDown();
    }

    /**
     * Handles the detection of a potential security threat during classification.
     * This method is invoked when a classifier identifies a text segment
     * with a risk score exceeding the defined security threshold.
     *
     * @param field  The request field where the threat was detected (e.g., BODY, HEADER, PARAM).
     * @param threat A string describing the detected security threat.
     */
    @Override
    public void onThreatDetected(Field field, String threat) {
        SecuraiLogger.error(TAG, SECURITY_THREAT_DETECTED.getMessage() + " in " + field + ": " + threat);
        result.set(new SecuraiResult(true, threat));
        latch.countDown();
    }

    /**
     * Handles successful classification results.
     * Updates the result reference to indicate no security threat and signals completion.
     *
     * @param results A map containing the classification results.
     *                The outer map's keys are {@link Field} values, representing the request fields that were classified.
     *                The inner map's keys are strings representing the classified text segments.
     *                The inner map's values are {@link TextClassifierResult} objects, containing the classification results for each text segment.
     */
    @Override
    public void onResult(Map<Field, Map<String, TextClassifierResult>> results) {
        result.set(new SecuraiResult(false, NO_THREAT_DETECTED));
        latch.countDown();
    }
}