package com.nomaddeveloper.securai.callback;

import com.google.mediapipe.tasks.text.textclassifier.TextClassifierResult;
import com.nomaddeveloper.securai.logger.SecuraiLogger;
import com.nomaddeveloper.securai.model.Field;

import java.util.Map;

/**
 * An implementation of the {@link ClassifyListener} interface that logs classification results and errors.
 * This class uses {@link SecuraiLogger} for logging, providing a simple way to monitor classification operations.
 */
public class ClassifyListenerImpl implements ClassifyListener {

    private static final String TAG = ClassifyListenerImpl.class.getCanonicalName();

    /**
     * Handles errors that occur during classification by logging them using {@link SecuraiLogger}.
     *
     * @param error A descriptive error message indicating the cause of the failure.
     */
    @Override
    public void onError(String error) {
        SecuraiLogger.error(TAG, "Classification error: " + error);
    }

    /**
     * Handles successful classification results by logging them along with the inference time using {@link SecuraiLogger}.
     *
     * @param results       A map containing the classification results.
     *                      The outer map's keys are {@link Field} objects, representing the fields that were classified.
     *                      The inner map's keys are strings representing the text segments that were classified.
     *                      The inner map's values are {@link TextClassifierResult} objects, containing the classification results for each text segment.
     * @param inferenceTime The time taken (in milliseconds) for the classification process.
     */
    @Override
    public void onResult(Map<Field, Map<String, TextClassifierResult>> results, long inferenceTime) {
        SecuraiLogger.log(TAG, "Classification completed. Results: " + results + ", Duration: " + inferenceTime + "ms");
    }
}