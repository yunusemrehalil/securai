package com.nomaddeveloper.securai.internal.callback;

import androidx.annotation.RestrictTo;

import com.google.mediapipe.tasks.text.textclassifier.TextClassifierResult;
import com.nomaddeveloper.securai.internal.model.Field;

import java.util.Map;

/**
 * Interface definition for a callback to be invoked when a classification operation completes.
 * This interface provides methods for handling both successful and error scenarios
 * during the classification process.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public interface ClassifyListener {

    /**
     * Called when an error occurs during the classification process.
     *
     * @param error A descriptive error message indicating the cause of the failure.
     */
    void onError(String tag, String error);

    /**
     * Called when a potential threat is detected during the classification process.
     * This method is invoked immediately when the XSS classifier identifies a text segment
     * with an XSS score exceeding the defined security threshold.
     * It allows for immediate action to be taken, such as blocking the request or logging the threat,
     * without waiting for the entire classification process to complete.
     */
    void onThreatDetected(Field field, String thread);

    /**
     * Called when the classification operation completes successfully.
     *
     * @param results A map containing the classification results.
     *                The outer map's keys are `Field` objects, representing the fields that were classified.
     *                The inner map's keys are strings representing the text segments that were classified.
     *                The inner map's values are `TextClassifierResult` objects, containing the classification results for each text segment.
     */
    void onResult(Map<Field, Map<String, TextClassifierResult>> results);
}