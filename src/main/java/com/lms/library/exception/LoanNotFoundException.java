/**
 * Exception thrown when a requested loan is not found in the system.
 *
 * @author Joel Silva
 * @version 1.0
 * @since 2025
 */
package com.lms.library.exception;

public class LoanNotFoundException extends RuntimeException {

    /**
     * Constructs a new LoanNotFoundException with the specified detail message.
     *
     * @param message the detail message explaining which loan was not found
     */
    public LoanNotFoundException(String message) {
        super(message);
    }
}
