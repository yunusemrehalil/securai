package com.nomaddeveloper.securai.exception;

/**
 * Custom exception class for Securai-specific errors.
 * This exception extends {@link RuntimeException} to provide a way to handle
 * Securai-related errors in a more specific and informative manner.
 */
public class SecuraiException extends RuntimeException {

    /**
     * Constructs a new SecuraiException with the specified detail message and cause.
     *
     * @param message The detail message (which is saved for later retrieval by the {@link #getMessage()} method).
     * @param cause   The cause (which is saved for later retrieval by the {@link #getCause()} method).
     *                (A <tt>null</tt> value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public SecuraiException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new SecuraiException with the specified detail message.
     *
     * @param message The detail message (which is saved for later retrieval by the {@link #getMessage()} method).
     */
    public SecuraiException(String message) {
        super(message);
    }
}