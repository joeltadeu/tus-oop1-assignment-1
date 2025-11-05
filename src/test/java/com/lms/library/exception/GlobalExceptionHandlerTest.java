package com.lms.library.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for GlobalExceptionHandler class
 *
 * @author Joel Silva
 * @version 1.0
 * @since 2025
 */
@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();

    }

    @Test
    @DisplayName("Handle MemberNotFoundException should return 404 NOT_FOUND")
    void handleNotFoundExceptions_MemberNotFound_ShouldReturn404() {
        // When
        when(request.getRequestURI()).thenReturn("/api/test-endpoint");

        // Arrange
        MemberNotFoundException ex = new MemberNotFoundException("Member with id 123 not found");

        // Act
        ResponseEntity<ApiError> response = exceptionHandler.handleNotFoundExceptions(ex, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        ApiError apiError = response.getBody();
        assertNotNull(apiError);
        assertEquals(HttpStatus.NOT_FOUND.value(), apiError.status());
        assertEquals(HttpStatus.NOT_FOUND.getReasonPhrase(), apiError.error());
        assertEquals("Member with id 123 not found", apiError.message());
        assertEquals("/api/test-endpoint", apiError.path());
    }

    @Test
    @DisplayName("Handle ItemNotFoundException should return 404 NOT_FOUND")
    void handleNotFoundExceptions_ItemNotFound_ShouldReturn404() {
        // When
        when(request.getRequestURI()).thenReturn("/api/test-endpoint");

        // Arrange
        ItemNotFoundException ex = new ItemNotFoundException("Item with id 456 not found");

        // Act
        ResponseEntity<ApiError> response = exceptionHandler.handleNotFoundExceptions(ex, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        ApiError apiError = response.getBody();
        assertNotNull(apiError);
        assertEquals(HttpStatus.NOT_FOUND.value(), apiError.status());
        assertEquals(HttpStatus.NOT_FOUND.getReasonPhrase(), apiError.error());
        assertEquals("Item with id 456 not found", apiError.message());
        assertEquals("/api/test-endpoint", apiError.path());
    }

    @Test
    @DisplayName("Handle LoanNotFoundException should return 404 NOT_FOUND")
    void handleNotFoundExceptions_LoanNotFound_ShouldReturn404() {
        // When
        when(request.getRequestURI()).thenReturn("/api/test-endpoint");

        // Arrange
        LoanNotFoundException ex = new LoanNotFoundException("Loan with id 789 not found");

        // Act
        ResponseEntity<ApiError> response = exceptionHandler.handleNotFoundExceptions(ex, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        ApiError apiError = response.getBody();
        assertNotNull(apiError);
        assertEquals(HttpStatus.NOT_FOUND.value(), apiError.status());
        assertEquals(HttpStatus.NOT_FOUND.getReasonPhrase(), apiError.error());
        assertEquals("Loan with id 789 not found", apiError.message());
        assertEquals("/api/test-endpoint", apiError.path());
    }

    @Test
    @DisplayName("Handle ItemNotAvailableException should return 409 CONFLICT")
    void handleItemNotAvailable_ShouldReturn409() {
        // When
        when(request.getRequestURI()).thenReturn("/api/test-endpoint");

        // Arrange
        ItemNotAvailableException ex = new ItemNotAvailableException("Item is currently on loan");

        // Act
        ResponseEntity<ApiError> response = exceptionHandler.handleItemNotAvailable(ex, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());

        ApiError apiError = response.getBody();
        assertNotNull(apiError);
        assertEquals(HttpStatus.CONFLICT.value(), apiError.status());
        assertEquals(HttpStatus.CONFLICT.getReasonPhrase(), apiError.error());
        assertEquals("Item is currently on loan", apiError.message());
        assertEquals("/api/test-endpoint", apiError.path());
    }

    @Test
    @DisplayName("Handle IllegalArgumentException should return 400 BAD_REQUEST")
    void handleIllegalArgument_ShouldReturn400() {
        // When
        when(request.getRequestURI()).thenReturn("/api/test-endpoint");

        // Arrange
        IllegalArgumentException ex = new IllegalArgumentException("Invalid parameter provided");

        // Act
        ResponseEntity<ApiError> response = exceptionHandler.handleIllegalArgument(ex, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ApiError apiError = response.getBody();
        assertNotNull(apiError);
        assertEquals(HttpStatus.BAD_REQUEST.value(), apiError.status());
        assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), apiError.error());
        assertEquals("Invalid parameter provided", apiError.message());
        assertEquals("/api/test-endpoint", apiError.path());
    }

    @Test
    @DisplayName("Handle IllegalStateException should return 409 CONFLICT")
    void handleIllegalState_ShouldReturn409() {
        // When
        when(request.getRequestURI()).thenReturn("/api/test-endpoint");

        // Arrange
        IllegalStateException ex = new IllegalStateException("Loan cannot be processed in current state");

        // Act
        ResponseEntity<ApiError> response = exceptionHandler.handleIllegalState(ex, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());

        ApiError apiError = response.getBody();
        assertNotNull(apiError);
        assertEquals(HttpStatus.CONFLICT.value(), apiError.status());
        assertEquals(HttpStatus.CONFLICT.getReasonPhrase(), apiError.error());
        assertEquals("Loan cannot be processed in current state", apiError.message());
        assertEquals("/api/test-endpoint", apiError.path());
    }

    @Test
    @DisplayName("Handle generic Exception should return 500 INTERNAL_SERVER_ERROR")
    void handleGenericException_ShouldReturn500() {
        // When
        when(request.getRequestURI()).thenReturn("/api/test-endpoint");

        // Arrange
        Exception ex = new Exception("Unexpected database error");

        // Act
        ResponseEntity<ApiError> response = exceptionHandler.handleGenericException(ex, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        ApiError apiError = response.getBody();
        assertNotNull(apiError);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), apiError.status());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), apiError.error());
        assertEquals("An unexpected error occurred", apiError.message());
        assertEquals("/api/test-endpoint", apiError.path());
    }

    @Test
    @DisplayName("Handle generic Exception with null message should return generic error message")
    void handleGenericException_NullMessage_ShouldReturnGenericMessage() {
        // When
        when(request.getRequestURI()).thenReturn("/api/test-endpoint");

        // Arrange
        Exception ex = new Exception();

        // Act
        ResponseEntity<ApiError> response = exceptionHandler.handleGenericException(ex, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        ApiError apiError = response.getBody();
        assertNotNull(apiError);
        assertEquals("An unexpected error occurred", apiError.message());
    }

    @Test
    @DisplayName("Handle generic RuntimeException should return 500 INTERNAL_SERVER_ERROR")
    void handleGenericException_RuntimeException_ShouldReturn500() {
        // When
        when(request.getRequestURI()).thenReturn("/api/test-endpoint");

        // Arrange
        RuntimeException ex = new RuntimeException("Runtime error occurred");

        // Act
        ResponseEntity<ApiError> response = exceptionHandler.handleGenericException(ex, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        ApiError apiError = response.getBody();
        assertNotNull(apiError);
        assertEquals("An unexpected error occurred", apiError.message());
    }

    @Test
    @DisplayName("Handle exceptions with different request paths should include correct path")
    void handleException_WithDifferentPaths_ShouldIncludeCorrectPath() {
        // When
        when(request.getRequestURI()).thenReturn("/api/test-endpoint");

        // Arrange
        when(request.getRequestURI()).thenReturn("/api/members/123");
        MemberNotFoundException ex = new MemberNotFoundException("Member not found");

        // Act
        ResponseEntity<ApiError> response = exceptionHandler.handleNotFoundExceptions(ex, request);

        // Assert
        assertNotNull(response);
        ApiError apiError = response.getBody();
        assertNotNull(apiError);
        assertEquals("/api/members/123", apiError.path());
    }

    @Test
    @DisplayName("Handle exceptions with empty message should work correctly")
    void handleException_EmptyMessage_ShouldWorkCorrectly() {
        // When
        when(request.getRequestURI()).thenReturn("/api/test-endpoint");

        // Arrange
        MemberNotFoundException ex = new MemberNotFoundException("");

        // Act
        ResponseEntity<ApiError> response = exceptionHandler.handleNotFoundExceptions(ex, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        ApiError apiError = response.getBody();
        assertNotNull(apiError);
        assertEquals("", apiError.message());
    }

    @Test
    @DisplayName("Handle exceptions with null message should work correctly")
    void handleException_NullMessage_ShouldWorkCorrectly() {
        // When
        when(request.getRequestURI()).thenReturn("/api/test-endpoint");

        // Arrange
        MemberNotFoundException ex = new MemberNotFoundException(null);

        // Act
        ResponseEntity<ApiError> response = exceptionHandler.handleNotFoundExceptions(ex, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        ApiError apiError = response.getBody();
        assertNotNull(apiError);
        assertNull(apiError.message());
    }

    @Test
    @DisplayName("Handle multiple exception types with same handler should work correctly")
    void handleNotFoundExceptions_MultipleExceptionTypes_ShouldAllReturn404() {
        // When
        when(request.getRequestURI()).thenReturn("/api/test-endpoint");

        // Arrange
        MemberNotFoundException memberEx = new MemberNotFoundException("Member not found");
        ItemNotFoundException itemEx = new ItemNotFoundException("Item not found");
        LoanNotFoundException loanEx = new LoanNotFoundException("Loan not found");

        // Act & Assert - All should return 404
        ResponseEntity<ApiError> memberResponse = exceptionHandler.handleNotFoundExceptions(memberEx, request);
        ResponseEntity<ApiError> itemResponse = exceptionHandler.handleNotFoundExceptions(itemEx, request);
        ResponseEntity<ApiError> loanResponse = exceptionHandler.handleNotFoundExceptions(loanEx, request);

        assertEquals(HttpStatus.NOT_FOUND, memberResponse.getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, itemResponse.getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, loanResponse.getStatusCode());
    }

    @Test
    @DisplayName("Handle different business rule violations with appropriate status codes")
    void handleDifferentBusinessExceptions_ShouldReturnAppropriateStatusCodes() {
        // When
        when(request.getRequestURI()).thenReturn("/api/test-endpoint");

        // Arrange
        ItemNotAvailableException notAvailableEx = new ItemNotAvailableException("Item on loan");
        IllegalStateException illegalStateEx = new IllegalStateException("Invalid state");
        IllegalArgumentException illegalArgEx = new IllegalArgumentException("Invalid argument");

        // Act & Assert
        ResponseEntity<ApiError> notAvailableResponse = exceptionHandler.handleItemNotAvailable(notAvailableEx, request);
        ResponseEntity<ApiError> illegalStateResponse = exceptionHandler.handleIllegalState(illegalStateEx, request);
        ResponseEntity<ApiError> illegalArgResponse = exceptionHandler.handleIllegalArgument(illegalArgEx, request);

        assertEquals(HttpStatus.CONFLICT, notAvailableResponse.getStatusCode());
        assertEquals(HttpStatus.CONFLICT, illegalStateResponse.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, illegalArgResponse.getStatusCode());
    }

    @Test
    @DisplayName("Handle nested exceptions should work correctly")
    void handleGenericException_WithNestedException_ShouldReturn500() {
        // When
        when(request.getRequestURI()).thenReturn("/api/test-endpoint");

        // Arrange
        Exception cause = new RuntimeException("Root cause");
        Exception ex = new Exception("Wrapper exception", cause);

        // Act
        ResponseEntity<ApiError> response = exceptionHandler.handleGenericException(ex, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        ApiError apiError = response.getBody();
        assertNotNull(apiError);
        assertEquals("An unexpected error occurred", apiError.message());
    }

    @Test
    @DisplayName("GlobalExceptionHandler should be properly initialized")
    void globalExceptionHandler_ShouldBeProperlyInitialized() {
        // This test verifies that the handler can be instantiated and has the expected structure
        assertNotNull(exceptionHandler);

        // Verify it has the default constructor
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        assertNotNull(handler);
    }
}
