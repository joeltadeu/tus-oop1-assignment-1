package com.lms.library.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lms.library.dto.LoanRequest;
import com.lms.library.exception.ItemNotAvailableException;
import com.lms.library.exception.ItemNotFoundException;
import com.lms.library.exception.LoanNotFoundException;
import com.lms.library.exception.MemberNotFoundException;
import com.lms.library.model.Book;
import com.lms.library.model.Loan;
import com.lms.library.model.LoanItem;
import com.lms.library.model.Member;
import com.lms.library.service.LoanService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

/**
 * Unit tests for LoanController.
 *
 * @author Joel Silva
 * @version 1.0
 * @since 2025
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Loan Controller Unit Tests")
class LoanControllerTest {

    @Mock
    private LoanService loanService;

    @InjectMocks
    private LoanController loanController;

    private Member testMember;
    private Loan testLoan;
    private LoanRequest testLoanRequest;

    @BeforeEach
    void setUp() {
        testMember = new Member("John", "Doe", "john.doe@example.com");
        testMember.setId(1L);

        testLoan = new Loan(testMember, LocalDate.now(), LocalDate.now().plusDays(14));
        testLoan.setId(1L);

        testLoanRequest = new LoanRequest(List.of(1L, 2L));

        testLoan.addItem(new LoanItem(testLoan, new Book("Clean Code", "Robert C. Martin",
                LocalDate.of(2008, 8, 1), "9780132350884", "Programming", 464)));

        testLoan.addItem(new LoanItem(testLoan, new Book("Effective Java", "Joshua Bloch",
                LocalDate.of(2018, 1, 6), "9780134685991", "Programming", 416)));

    }

    @Nested
    @DisplayName("Checkout Items Tests")
    class CheckoutItemsTests {

        @Test
        @DisplayName("Should successfully checkout items and return loan response")
        void checkoutItems_ShouldReturnLoanResponse_WhenSuccessful() {
            // Given
            when(loanService.checkoutItems(anyLong(), any(LoanRequest.class)))
                    .thenReturn(testLoan);

            // When
            var response = loanController.checkoutItems(1L, testLoanRequest);

            // Then
            assertAll(
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                    () -> assertThat(response.getBody()).isNotNull(),
                    () -> {
                        Assertions.assertNotNull(response.getBody());
                        assertThat(response.getBody().id()).isEqualTo(1L);
                        assertThat(response.getBody().memberId()).isEqualTo(1L);
                        assertThat(response.getBody().status()).isEqualTo("OPEN");
                        assertThat(response.getBody().items()).hasSize(2);
                    }
            );

            verify(loanService).checkoutItems(1L, testLoanRequest);
        }

        @Test
        @DisplayName("Should propagate MemberNotFoundException when member not found")
        void checkoutItems_ShouldThrowMemberNotFoundException_WhenMemberNotFound() {
            // Given
            when(loanService.checkoutItems(anyLong(), any(LoanRequest.class)))
                    .thenThrow(new MemberNotFoundException("Member not found"));

            // When & Then
            try {
                loanController.checkoutItems(999L, testLoanRequest);
            } catch (MemberNotFoundException e) {
                assertThat(e.getMessage()).isEqualTo("Member not found");
            }

            verify(loanService).checkoutItems(999L, testLoanRequest);
        }

        @Test
        @DisplayName("Should propagate ItemNotFoundException when item not found")
        void checkoutItems_ShouldThrowItemNotFoundException_WhenItemNotFound() {
            // Given
            when(loanService.checkoutItems(anyLong(), any(LoanRequest.class)))
                    .thenThrow(new ItemNotFoundException("Item not found"));

            // When & Then
            try {
                loanController.checkoutItems(1L, testLoanRequest);
            } catch (ItemNotFoundException e) {
                assertThat(e.getMessage()).isEqualTo("Item not found");
            }

            verify(loanService).checkoutItems(1L, testLoanRequest);
        }

        @Test
        @DisplayName("Should propagate ItemNotAvailableException when item not available")
        void checkoutItems_ShouldThrowItemNotAvailableException_WhenItemNotAvailable() {
            // Given
            when(loanService.checkoutItems(anyLong(), any(LoanRequest.class)))
                    .thenThrow(new ItemNotAvailableException("Item not available"));

            // When & Then
            try {
                loanController.checkoutItems(1L, testLoanRequest);
            } catch (ItemNotAvailableException e) {
                assertThat(e.getMessage()).isEqualTo("Item not available");
            }

            verify(loanService).checkoutItems(1L, testLoanRequest);
        }
    }

    @Nested
    @DisplayName("Get Member Loans Tests")
    class GetMemberLoansTests {

        @Test
        @DisplayName("Should return list of loan summaries for member")
        void getMemberLoans_ShouldReturnLoanSummaries_WhenMemberExists() {
            // Given
            var loans = List.of(testLoan);
            when(loanService.getMemberLoans(1L)).thenReturn(loans);

            // When
            var response = loanController.getMemberLoans(1L);

            // Then
            assertAll(
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                    () -> assertThat(response.getBody()).isNotNull(),
                    () -> assertThat(response.getBody()).hasSize(1),
                    () -> assertThat(response.getBody().get(0).loanId()).isEqualTo(1L)
            );

            verify(loanService).getMemberLoans(1L);
        }

        @Test
        @DisplayName("Should return empty list when member has no loans")
        void getMemberLoans_ShouldReturnEmptyList_WhenNoLoansExist() {
            // Given
            when(loanService.getMemberLoans(1L)).thenReturn(List.of());

            // When
            var response = loanController.getMemberLoans(1L);

            // Then
            assertAll(
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                    () -> assertThat(response.getBody()).isNotNull(),
                    () -> assertThat(response.getBody()).isEmpty()
            );

            verify(loanService).getMemberLoans(1L);
        }

        @Test
        @DisplayName("Should propagate MemberNotFoundException when member not found")
        void getMemberLoans_ShouldThrowMemberNotFoundException_WhenMemberNotFound() {
            // Given
            when(loanService.getMemberLoans(anyLong()))
                    .thenThrow(new MemberNotFoundException("Member not found"));

            // When & Then
            try {
                loanController.getMemberLoans(999L);
            } catch (MemberNotFoundException e) {
                assertThat(e.getMessage()).isEqualTo("Member not found");
            }

            verify(loanService).getMemberLoans(999L);
        }
    }

    @Nested
    @DisplayName("Get Loan Tests")
    class GetLoanTests {

        @Test
        @DisplayName("Should return loan details when loan exists")
        void getLoan_ShouldReturnLoanDetails_WhenLoanExists() {
            // Given
            when(loanService.getLoanById(1L)).thenReturn(testLoan);

            // When
            var response = loanController.getLoan(1L);

            // Then
            assertAll(
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                    () -> assertThat(response.getBody()).isNotNull(),
                    () -> {
                        Assertions.assertNotNull(response.getBody());
                        assertThat(response.getBody().id()).isEqualTo(1L);
                        assertThat(response.getBody().memberId()).isEqualTo(1L);
                    }
            );

            verify(loanService).getLoanById(1L);
        }

        @Test
        @DisplayName("Should propagate LoanNotFoundException when loan not found")
        void getLoan_ShouldThrowLoanNotFoundException_WhenLoanNotFound() {
            // Given
            when(loanService.getLoanById(anyLong()))
                    .thenThrow(new LoanNotFoundException("Loan not found"));

            // When & Then
            try {
                loanController.getLoan(999L);
            } catch (LoanNotFoundException e) {
                assertThat(e.getMessage()).isEqualTo("Loan not found");
            }

            verify(loanService).getLoanById(999L);
        }
    }

    @Nested
    @DisplayName("Return Items Tests")
    class ReturnItemsTests {

        @Test
        @DisplayName("Should return specific items and update loan status")
        void returnItems_ShouldReturnSpecificItems_WhenSuccessful() {
            // Given
            var returnRequest = new LoanRequest(List.of(1L));
            var returnedLoan = createReturnedLoan();

            when(loanService.returnItems(1L, List.of(1L))).thenReturn(returnedLoan);

            // When
            var response = loanController.returnItems(1L, returnRequest);

            // Then
            assertAll(
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                    () -> assertThat(response.getBody()).isNotNull(),
                    () -> {
                        Assertions.assertNotNull(response.getBody());
                        assertThat(response.getBody().status()).isEqualTo("CLOSED");
                        assertThat(response.getBody().items().getFirst().returnedDate()).isNotNull();
                    }
            );

            verify(loanService).returnItems(1L, List.of(1L));
        }

        @Test
        @DisplayName("Should return all items when no specific items provided")
        void returnItems_ShouldReturnAllItems_WhenNoItemsSpecified() {
            // Given
            var returnedLoan = createReturnedLoan();

            when(loanService.returnItems(any(), anyList())).thenReturn(returnedLoan);

            var returnRequest = new LoanRequest(List.of(1L, 2L));

            // When
            var response = loanController.returnItems(1L, returnRequest);

            // Then
            assertAll(
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                    () -> assertThat(response.getBody()).isNotNull()
            );

            verify(loanService).returnItems(1L, List.of(1L, 2L));
        }

        @Test
        @DisplayName("Should propagate LoanNotFoundException when loan not found")
        void returnItems_ShouldThrowLoanNotFoundException_WhenLoanNotFound() {
            // Given
            var returnRequest = new LoanRequest(List.of(1L));
            when(loanService.returnItems(anyLong(), any()))
                    .thenThrow(new LoanNotFoundException("Loan not found"));

            // When & Then
            try {
                loanController.returnItems(999L, returnRequest);
            } catch (LoanNotFoundException e) {
                assertThat(e.getMessage()).isEqualTo("Loan not found");
            }

            verify(loanService).returnItems(999L, List.of(1L));
        }

        @Test
        @DisplayName("Should propagate ItemNotFoundException when item not found in loan")
        void returnItems_ShouldThrowItemNotFoundException_WhenItemNotInLoan() {
            // Given
            var returnRequest = new LoanRequest(List.of(999L));
            when(loanService.returnItems(anyLong(), any()))
                    .thenThrow(new ItemNotFoundException("Item not found in loan"));

            // When & Then
            try {
                loanController.returnItems(1L, returnRequest);
            } catch (ItemNotFoundException e) {
                assertThat(e.getMessage()).isEqualTo("Item not found in loan");
            }

            verify(loanService).returnItems(1L, List.of(999L));
        }

        private Loan createReturnedLoan() {
            var returnedLoan = new Loan(testMember, LocalDate.now(), LocalDate.now().plusDays(14));
            returnedLoan.setId(1L);

            var loanItem = new LoanItem(returnedLoan, new Book("Clean Code", "Robert C. Martin",
                    LocalDate.of(2008, 8, 1), "9780132350884", "Programming", 464));

            loanItem.markReturned();

            returnedLoan.addItem(loanItem);

            // Simulate that all items are returned
            returnedLoan.updateStatus(); // This should set status to CLOSED if all returned

            return returnedLoan;
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle loan with single item")
        void checkoutItems_ShouldHandleSingleItem_WhenSuccessful() {
            // Given
            var singleItemRequest = new LoanRequest(List.of(1L));
            when(loanService.checkoutItems(anyLong(), any(LoanRequest.class)))
                    .thenReturn(testLoan);

            // When
            var response = loanController.checkoutItems(1L, singleItemRequest);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(loanService).checkoutItems(1L, singleItemRequest);
        }

        @Test
        @DisplayName("Should handle loan with multiple items")
        void checkoutItems_ShouldHandleMultipleItems_WhenSuccessful() {
            // Given
            var multipleItemsRequest = new LoanRequest(List.of(1L, 2L, 3L));
            when(loanService.checkoutItems(anyLong(), any(LoanRequest.class)))
                    .thenReturn(testLoan);

            // When
            var response = loanController.checkoutItems(1L, multipleItemsRequest);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(loanService).checkoutItems(1L, multipleItemsRequest);
        }
    }
}
