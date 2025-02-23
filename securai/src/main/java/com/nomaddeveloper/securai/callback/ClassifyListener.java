package com.nomaddeveloper.securai.callback;

import com.google.mediapipe.tasks.text.textclassifier.TextClassifierResult;
import com.nomaddeveloper.securai.model.Field;

import java.util.Map;

/**
 * Interface definition for a callback to be invoked when a classification operation completes.
 * This interface provides methods for handling both successful and error scenarios during the classification process.
 */
public interface ClassifyListener {

    /**
     * Called when an error occurs during the classification process.
     *
     * @param error A descriptive error message indicating the cause of the failure.
     */
    void onError(String error);

    /**
     * Called when the classification operation completes successfully.
     *
     * @param results       A map containing the classification results.
     *                      The outer map's keys are `Field` objects, representing the fields that were classified.
     *                      The inner map's keys are strings representing the text segments that were classified.
     *                      The inner map's values are `TextClassifierResult` objects, containing the classification results for each text segment.
     * @param inferenceTime The time taken (in milliseconds) for the classification process.
     */
    void onResult(Map<Field, Map<String, TextClassifierResult>> results, long inferenceTime);
}