package com.nomaddeveloper.securai.internal.logger;

import android.util.Log;

import androidx.annotation.RestrictTo;

/**
 * Centralized logging utility for Securai.
 * Provides consistent logging with different severity levels.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class SecuraiLogger {

    private static final String TAG_PREFIX = "Securai";
    private static final String EXCEPTION_PREFIX = "Exception: ";
    private static final String TAG_FORMAT = "%s-%s";
    private static volatile boolean isLoggingEnabled = false;

    private SecuraiLogger() {
        throw new UnsupportedOperationException("Utility class - cannot be instantiated.");
    }

    /**
     * Enables or disables logging globally.
     *
     * @param enabled `true` to enable logging, `false` to disable it.
     */
    public static void setLoggingEnabled(boolean enabled) {
        isLoggingEnabled = enabled;
    }

    /**
     * Logs a debug message.
     */
    public static void debug(String tag, String message) {
        if (!isLoggingEnabled || message == null) return;
        Log.d(formatTag(tag), message);
    }

    /**
     * Logs an informational message.
     */
    public static void info(String tag, String message) {
        if (!isLoggingEnabled || message == null) return;
        Log.i(formatTag(tag), message);
    }

    /**
     * Logs a warning message.
     */
    public static void warn(String tag, String message) {
        if (!isLoggingEnabled || message == null) return;
        Log.w(formatTag(tag), message);
    }

    /**
     * Logs an error message.
     */
    public static void error(String tag, String message) {
        if (!isLoggingEnabled || message == null) return;
        Log.e(formatTag(tag), message);
    }

    /**
     * Logs an error message with an exception.
     */
    public static void error(String tag, String message, Throwable t) {
        if (!isLoggingEnabled || message == null) return;
        Log.e(formatTag(tag), message, t);
    }

    /**
     * Logs an exception with a default message.
     */
    public static void error(String tag, Throwable t) {
        if (!isLoggingEnabled || t == null) return;
        Log.e(formatTag(tag), EXCEPTION_PREFIX + t.getMessage(), t);
    }

    /**
     * Formats the log tag consistently with the prefix.
     */
    private static String formatTag(String tag) {
        return tag == null || tag.isEmpty() ? TAG_PREFIX : String.format(TAG_FORMAT, TAG_PREFIX, tag);
    }
}