package com.lms.library.exception;

/**
 * Exception thrown when a requested item is not found in the system.
 *
 * @author Joel Silva
 * @version 1.0
 * @since 2025
 */
public class ItemNotFoundException extends RuntimeException {

    /**
     * Constructs a new ItemNotFoundException with the specified detail message.
     *
     * @param message the detail message explaining which item was not found
     */
    public ItemNotFoundException(String message) {
        super(message);
    }
}
