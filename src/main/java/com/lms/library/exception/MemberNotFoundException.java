package com.lms.library.exception;

/**
 * Exception thrown when a requested member is not found in the system.
 *
 * @author Joel Silva
 * @version 1.0
 * @since 2025
 */
public class MemberNotFoundException extends RuntimeException {

    /**
     * Constructs a new MemberNotFoundException with the specified detail message.
     *
     * @param message the detail message explaining which member was not found
     */
    public MemberNotFoundException(String message) {
        super(message);
    }
}
