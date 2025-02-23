package com.nomaddeveloper.securai.logger;

import android.util.Log;

/**
 * Centralized logging utility for the Securai.
 * This class provides a consistent and convenient way to log messages with different severity levels.
 */
public class SecuraiLogger {

    private static final String TAG_PREFIX = "Securai";

    /**
     * Logs an informational message.
     *
     * @param tag     The tag associated with the log message.
     * @param message The message to be logged.
     */
    public static void log(String tag, String message) {
        Log.i(TAG_PREFIX + "-" + tag, message);
    }

    /**
     * Logs a warning message.
     *
     * @param tag     The tag associated with the log message.
     * @param message The message to be logged.
     */
    public static void warn(String tag, String message) {
        Log.w(TAG_PREFIX + "-" + tag, message);
    }

    /**
     * Logs an error message along with a throwable.
     *
     * @param tag     The tag associated with the log message.
     * @param message The message to be logged.
     * @param t       The throwable associated with the error.
     */
    public static void error(String tag, String message, Throwable t) {
        Log.e(TAG_PREFIX + "-" + tag, message, t);
    }

    /**
     * Logs an error message.
     *
     * @param tag     The tag associated with the log message.
     * @param message The message to be logged.
     */
    public static void error(String tag, String message) {
        Log.e(TAG_PREFIX + "-" + tag, message);
    }
}