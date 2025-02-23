package com.nomaddeveloper.securai.helper;

import android.content.Context;

import com.google.mediapipe.tasks.core.BaseOptions;
import com.google.mediapipe.tasks.text.textclassifier.TextClassifier;
import com.nomaddeveloper.securai.callback.ClassifyListener;
import com.nomaddeveloper.securai.logger.SecuraiLogger;

import java.util.concurrent.ScheduledThreadPoolExecutor;

public class XSSClassifierHelper {

    private static final String TAG = XSSClassifierHelper.class.getCanonicalName();
    private static final String XSS_MODEL = "xss_model.tflite";
    private static final float XSS_SECURITY_THRESHOLD = 0.8f;
    private static final int CORE_POOL_SIZE = 1;

    private final Context context;
    private final ClassifyListener classifyListener;
    private TextClassifier textClassifier;
    private final ScheduledThreadPoolExecutor executor;

    /**
     * Constructs an XSSClassifierHelper instance.
     *
     * @param context          The application context.
     * @param classifyListener Listener to handle classification results and errors.
     */
    public XSSClassifierHelper(Context context, ClassifyListener classifyListener) {
        this.context = context;
        this.classifyListener = classifyListener;
        this.executor = new ScheduledThreadPoolExecutor(CORE_POOL_SIZE);
        initClassifier();
    }

    /**
     * Initializes the TensorFlow Lite text classifier.
     * If the initialization fails, an error is logged and reported.
     */
    public void initClassifier() {
        try {
            BaseOptions baseOptions = BaseOptions.builder()
                    .setModelAssetPath(XSS_MODEL)
                    .build();

            TextClassifier.TextClassifierOptions options = TextClassifier.TextClassifierOptions.builder()
                    .setBaseOptions(baseOptions)
                    .build();

            textClassifier = TextClassifier.createFromOptions(context, options);
            SecuraiLogger.log(TAG, "XSS Classifier initialized successfully.");
        } catch (IllegalStateException e) {
            String errorMessage = "Text classifier failed to initialize.";
            classifyListener.onError(errorMessage);
            SecuraiLogger.error(TAG, errorMessage, e);
        } catch (Exception ex) {
            String errorMessage = "Error loading text classifier.";
            classifyListener.onError(errorMessage);
            SecuraiLogger.error(TAG, errorMessage, ex);
        }
    }

    /**
     * Releases resources when the classifier is no longer needed.
     */
    public void release() {
        if (executor != null) {
            executor.shutdown();
        }
        if (textClassifier != null) {
            textClassifier.close();
            textClassifier = null;
        }
        SecuraiLogger.log(TAG, "XSS Classifier resources released.");
    }
}