package com.lms.library.exception;

/**
 * Exception thrown when a requested item is not available for checkout.
 *
 * @author Joel Silva
 * @version 1.0
 * @since 2025
 */
public class ItemNotAvailableException extends RuntimeException {

  /**
   * Constructs a new ItemNotAvailableException with the specified detail message.
   *
   * @param message the detail message explaining why the item is not available
   */
  public ItemNotAvailableException(String message) {
    super(message);
  }
}
