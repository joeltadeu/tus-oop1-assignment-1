package com.lms.library.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.lms.library.dto.LoanRequest;
import com.lms.library.exception.ItemNotAvailableException;
import com.lms.library.exception.ItemNotFoundException;
import com.lms.library.exception.LoanNotFoundException;
import com.lms.library.exception.MemberNotFoundException;
import com.lms.library.model.*;
import com.lms.library.repository.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for LoanServiceImpl.
 *
 * @author Joel Silva
 * @version 1.0
 * @since 2025
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Loan Service Implementation Unit Tests")
class LoanServiceImplTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private LibraryItemRepository libraryItemRepository;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private LoanItemRepository loanItemRepository;

    @InjectMocks
    private LoanServiceImpl loanService;

    @Captor
    private ArgumentCaptor<Loan> loanCaptor;

    @Captor
    private ArgumentCaptor<List<LoanItem>> loanItemsCaptor;

    private Member testMember;
    private Book availableBook;
    private Journal availableJournal;
    private Book unavailableBook;
    private LoanRequest validLoanRequest;
    private Loan existingLoan;

    @BeforeEach
    void setUp() {
        testMember = new Member("John", "Doe", "john.doe@example.com");
        testMember.setId(1L);

        availableBook = new Book("Clean Code", "Robert Martin",
                LocalDate.of(2008, 1, 1), "978-0132350884", "Programming", 464);
        availableBook.setId(1L);

        availableJournal = new Journal("Nature", "Various",
                LocalDate.of(2024, 1, 1), "1234-5678", "Nature Publishing", 1, 1);
        availableJournal.setId(2L);

        unavailableBook = new Book("Effective Java", "Joshua Bloch",
                LocalDate.of(2018, 1, 1), "978-0134685991", "Programming", 416);
        unavailableBook.setId(3L);
        unavailableBook.setAvailable(false);

        validLoanRequest = new LoanRequest(List.of(1L, 2L));

        existingLoan = new Loan(testMember, LocalDate.now(), LocalDate.now().plusDays(14));
        existingLoan.setId(1L);
    }

    @Nested
    @DisplayName("Checkout Items Tests")
    class CheckoutItemsTests {

        @Test
        @DisplayName("Should successfully checkout available items")
        void checkoutItems_ShouldCreateLoan_WhenAllItemsAvailable() {
            // Given
            when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));
            when(libraryItemRepository.findAvailableItemById(1L)).thenReturn(Optional.of(availableBook));
            when(libraryItemRepository.findAvailableItemById(2L)).thenReturn(Optional.of(availableJournal));
            when(loanRepository.save(any(Loan.class))).thenReturn(existingLoan);

            // When
            var result = loanService.checkoutItems(1L, validLoanRequest);

            // Then
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.getId()).isEqualTo(1L),
                    () -> assertThat(result.getMember()).isEqualTo(testMember)
            );

            verify(memberRepository).findById(1L);
            verify(libraryItemRepository).findAvailableItemById(1L);
            verify(libraryItemRepository).findAvailableItemById(2L);
            verify(loanRepository).save(any(Loan.class));
            verify(loanItemRepository).saveAll(anyList());

            // Verify items were marked as unavailable
            assertThat(availableBook.isAvailable()).isFalse();
            assertThat(availableJournal.isAvailable()).isFalse();
        }

        @Test
        @DisplayName("Should throw MemberNotFoundException when member not found")
        void checkoutItems_ShouldThrowException_WhenMemberNotFound() {
            // Given
            when(memberRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> loanService.checkoutItems(999L, validLoanRequest))
                    .isInstanceOf(MemberNotFoundException.class)
                    .hasMessage("Member not found with ID: 999");

            verify(memberRepository).findById(999L);
            verifyNoInteractions(libraryItemRepository, loanRepository, loanItemRepository);
        }

        @Test
        @DisplayName("Should throw ItemNotFoundException when item not found")
        void checkoutItems_ShouldThrowException_WhenItemNotFound() {
            // Given
            when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));
            when(libraryItemRepository.findAvailableItemById(999L)).thenReturn(Optional.empty());
            when(libraryItemRepository.findById(999L)).thenReturn(Optional.empty());

            var requestWithInvalidItem = new LoanRequest(List.of(999L));

            // When & Then
            assertThatThrownBy(() -> loanService.checkoutItems(1L, requestWithInvalidItem))
                    .isInstanceOf(ItemNotFoundException.class)
                    .hasMessage("Item not found with ID: 999");

            verify(memberRepository).findById(1L);
            verify(libraryItemRepository).findAvailableItemById(999L);
            verify(libraryItemRepository).findById(999L);
        }

        @Test
        @DisplayName("Should throw ItemNotAvailableException when item is not available")
        void checkoutItems_ShouldThrowException_WhenItemNotAvailable() {
            // Given
            when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));
            when(libraryItemRepository.findAvailableItemById(3L)).thenReturn(Optional.empty());
            when(libraryItemRepository.findById(3L)).thenReturn(Optional.of(unavailableBook));

            var requestWithUnavailableItem = new LoanRequest(List.of(3L));

            // When & Then
            assertThatThrownBy(() -> loanService.checkoutItems(1L, requestWithUnavailableItem))
                    .isInstanceOf(ItemNotAvailableException.class)
                    .hasMessage("Item 'Effective Java' is currently loaned out");

            verify(memberRepository).findById(1L);
            verify(libraryItemRepository).findAvailableItemById(3L);
            verify(libraryItemRepository).findById(3L);
        }

        @Test
        @DisplayName("Should set correct loan dates and expected return date")
        void checkoutItems_ShouldSetCorrectDates_WhenCreatingLoan() {
            // Given
            when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));
            when(libraryItemRepository.findAvailableItemById(1L)).thenReturn(Optional.of(availableBook));
            when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> {
                Loan savedLoan = invocation.getArgument(0);
                savedLoan.setId(1L);
                return savedLoan;
            });

            var expectedLoanDate = LocalDate.now();
            var expectedReturnDate = expectedLoanDate.plusDays(14);

            // When
            var result = loanService.checkoutItems(1L, new LoanRequest(List.of(1L)));

            // Then
            assertAll(
                    () -> assertThat(result.getLoanDate()).isEqualTo(expectedLoanDate),
                    () -> assertThat(result.getExpectedReturnDate()).isEqualTo(expectedReturnDate)
            );

            verify(loanRepository).save(loanCaptor.capture());
            var capturedLoan = loanCaptor.getValue();
            assertThat(capturedLoan.getLoanDate()).isEqualTo(expectedLoanDate);
            assertThat(capturedLoan.getExpectedReturnDate()).isEqualTo(expectedReturnDate);
        }

        @Test
        @DisplayName("Should create loan items for each requested item")
        void checkoutItems_ShouldCreateLoanItems_ForEachRequestedItem() {
            // Given
            when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));
            when(libraryItemRepository.findAvailableItemById(1L)).thenReturn(Optional.of(availableBook));
            when(libraryItemRepository.findAvailableItemById(2L)).thenReturn(Optional.of(availableJournal));
            when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> {
                Loan loan = invocation.getArgument(0);
                loan.setId(1L);
                return loan;
            });

            loanService.checkoutItems(1L, validLoanRequest);

            // Then
            verify(loanItemRepository).saveAll(loanItemsCaptor.capture());
            var capturedLoanItems = loanItemsCaptor.getValue();

            verify(loanRepository).save(loanCaptor.capture());
            var capturedLoan = loanCaptor.getValue();

            assertAll(
                    () -> assertThat(capturedLoanItems).hasSize(2),
                    () -> assertThat(capturedLoanItems.get(0).getItem()).isEqualTo(availableBook),
                    () -> assertThat(capturedLoanItems.get(1).getItem()).isEqualTo(availableJournal),
                    () -> assertThat(capturedLoanItems.get(0).getLoan()).isEqualTo(capturedLoan),
                    () -> assertThat(capturedLoanItems.get(1).getLoan()).isEqualTo(capturedLoan)
            );
        }
    }

    @Nested
    @DisplayName("Get Member Loans Tests")
    class GetMemberLoansTests {

        @Test
        @DisplayName("Should return member loans when member exists")
        void getMemberLoans_ShouldReturnLoans_WhenMemberExists() {
            // Given
            when(memberRepository.existsById(1L)).thenReturn(true);
            when(loanRepository.findByMemberIdOrderByLoanDateDesc(1L))
                    .thenReturn(List.of(existingLoan));

            // When
            var result = loanService.getMemberLoans(1L);

            // Then
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result).hasSize(1),
                    () -> assertThat(result.get(0)).isEqualTo(existingLoan)
            );

            verify(memberRepository).existsById(1L);
            verify(loanRepository).findByMemberIdOrderByLoanDateDesc(1L);
        }

        @Test
        @DisplayName("Should return empty list when member has no loans")
        void getMemberLoans_ShouldReturnEmptyList_WhenNoLoansExist() {
            // Given
            when(memberRepository.existsById(1L)).thenReturn(true);
            when(loanRepository.findByMemberIdOrderByLoanDateDesc(1L)).thenReturn(List.of());

            // When
            var result = loanService.getMemberLoans(1L);

            // Then
            assertThat(result).isEmpty();

            verify(memberRepository).existsById(1L);
            verify(loanRepository).findByMemberIdOrderByLoanDateDesc(1L);
        }

        @Test
        @DisplayName("Should throw MemberNotFoundException when member not found")
        void getMemberLoans_ShouldThrowException_WhenMemberNotFound() {
            // Given
            when(memberRepository.existsById(999L)).thenReturn(false);

            // When & Then
            assertThatThrownBy(() -> loanService.getMemberLoans(999L))
                    .isInstanceOf(MemberNotFoundException.class)
                    .hasMessage("Member not found with ID: 999");

            verify(memberRepository).existsById(999L);
            verifyNoInteractions(loanRepository);
        }
    }

    @Nested
    @DisplayName("Get Loan By ID Tests")
    class GetLoanByIdTests {

        @Test
        @DisplayName("Should return loan when loan exists")
        void getLoanById_ShouldReturnLoan_WhenLoanExists() {
            // Given
            when(loanRepository.findByIdWithMemberAndItems(1L)).thenReturn(Optional.of(existingLoan));

            // When
            var result = loanService.getLoanById(1L);

            // Then
            assertThat(result).isEqualTo(existingLoan);

            verify(loanRepository).findByIdWithMemberAndItems(1L);
        }

        @Test
        @DisplayName("Should throw LoanNotFoundException when loan not found")
        void getLoanById_ShouldThrowException_WhenLoanNotFound() {
            // Given
            when(loanRepository.findByIdWithMemberAndItems(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> loanService.getLoanById(999L))
                    .isInstanceOf(LoanNotFoundException.class)
                    .hasMessage("Loan not found with ID: 999");

            verify(loanRepository).findByIdWithMemberAndItems(999L);
        }
    }

    @Nested
    @DisplayName("Return Items Tests")
    class ReturnItemsTests {

        private LoanItem returnedLoanItem;
        private LoanItem notReturnedLoanItem;

        @BeforeEach
        void setUp() {
            returnedLoanItem = new LoanItem(existingLoan, availableBook);
            returnedLoanItem.setId(1L);
            returnedLoanItem.markReturned();

            notReturnedLoanItem = new LoanItem(existingLoan, availableJournal);
            notReturnedLoanItem.setId(2L);

            existingLoan.addItem(returnedLoanItem);
            existingLoan.addItem(notReturnedLoanItem);
        }

        @Test
        @DisplayName("Should return specific items and update loan status")
        void returnItems_ShouldReturnSpecificItems_WhenSuccessful() {
            // Given
            when(loanRepository.findByIdWithItems(1L)).thenReturn(Optional.of(existingLoan));
            when(loanItemRepository.findByLoanIdAndItemId(1L, 2L)).thenReturn(Optional.of(notReturnedLoanItem));
            when(loanRepository.save(any(Loan.class))).thenReturn(existingLoan);

            // When
            var result = loanService.returnItems(1L, List.of(2L));

            // Then
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(notReturnedLoanItem.isReturned()).isTrue(),
                    () -> assertThat(notReturnedLoanItem.getReturnedDate()).isEqualTo(LocalDate.now()),
                    () -> assertThat(availableJournal.isAvailable()).isTrue()
            );

            verify(loanRepository).findByIdWithItems(1L);
            verify(loanItemRepository).findByLoanIdAndItemId(1L, 2L);
            verify(loanItemRepository).save(notReturnedLoanItem);
            verify(loanRepository).save(existingLoan);
        }

        @Test
        @DisplayName("Should throw LoanNotFoundException when loan not found")
        void returnItems_ShouldThrowException_WhenLoanNotFound() {
            // Given
            when(loanRepository.findByIdWithItems(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> loanService.returnItems(999L, List.of(1L)))
                    .isInstanceOf(LoanNotFoundException.class)
                    .hasMessage("Loan not found with ID: 999");

            verify(loanRepository).findByIdWithItems(999L);
            verifyNoInteractions(loanItemRepository);
        }

        @Test
        @DisplayName("Should throw IllegalStateException when loan is already closed")
        void returnItems_ShouldThrowException_WhenLoanAlreadyClosed() {

            // Given
            existingLoan.getItems().get(0).markReturned();
            existingLoan.getItems().get(1).markReturned();
            existingLoan.updateStatus(); // This would set status to CLOSED if all items returned
            when(loanRepository.findByIdWithItems(1L)).thenReturn(Optional.of(existingLoan));

            // When & Then
            assertThatThrownBy(() -> loanService.returnItems(1L, List.of(1L)))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Loan is already closed");

            verify(loanRepository).findByIdWithItems(1L);
            verifyNoInteractions(loanItemRepository);
        }

        @Test
        @DisplayName("Should throw ItemNotFoundException when item not found in loan")
        void returnItems_ShouldThrowException_WhenItemNotInLoan() {
            // Given
            when(loanRepository.findByIdWithItems(1L)).thenReturn(Optional.of(existingLoan));
            when(loanItemRepository.findByLoanIdAndItemId(1L, 999L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> loanService.returnItems(1L, List.of(999L)))
                    .isInstanceOf(ItemNotFoundException.class)
                    .hasMessage("Item 999 not found in loan 1");

            verify(loanRepository).findByIdWithItems(1L);
            verify(loanItemRepository).findByLoanIdAndItemId(1L, 999L);
        }

        @Test
        @DisplayName("Should skip already returned items")
        void returnItems_ShouldSkip_WhenItemAlreadyReturned() {
            // Given
            when(loanRepository.findByIdWithItems(1L)).thenReturn(Optional.of(existingLoan));
            when(loanItemRepository.findByLoanIdAndItemId(1L, 1L)).thenReturn(Optional.of(returnedLoanItem));
            when(loanRepository.save(any(Loan.class))).thenReturn(existingLoan);

            // When
            var result = loanService.returnItems(1L, List.of(1L));

            // Then
            assertThat(result).isNotNull();

            // Verify the already returned item was not saved again
            verify(loanItemRepository, never()).save(returnedLoanItem);
            verify(loanRepository).save(existingLoan);
        }

        @Test
        @DisplayName("Should update loan status to CLOSED when all items are returned")
        void returnItems_ShouldUpdateStatusToClosed_WhenAllItemsReturned() {
            // Given
            var loanWithAllItemsReturnable = new Loan(testMember, LocalDate.now(), LocalDate.now().plusDays(14));
            loanWithAllItemsReturnable.setId(2L);

            var item1 = new LoanItem(loanWithAllItemsReturnable, availableBook);
            item1.setId(1L);
            var item2 = new LoanItem(loanWithAllItemsReturnable, availableJournal);
            item2.setId(2L);

            loanWithAllItemsReturnable.addItem(item1);
            loanWithAllItemsReturnable.addItem(item2);

            when(loanRepository.findByIdWithItems(2L)).thenReturn(Optional.of(loanWithAllItemsReturnable));
            when(loanItemRepository.findByLoanIdAndItemId(2L, 1L)).thenReturn(Optional.of(item1));
            when(loanItemRepository.findByLoanIdAndItemId(2L, 2L)).thenReturn(Optional.of(item2));
            when(loanRepository.save(any(Loan.class))).thenReturn(loanWithAllItemsReturnable);

            // When
            var result = loanService.returnItems(2L, List.of(1L, 2L));

            // Then
            assertThat(result.getStatus()).isEqualTo(LoanStatus.CLOSED);

            verify(loanRepository).save(loanWithAllItemsReturnable);
            assertThat(loanWithAllItemsReturnable.getStatus()).isEqualTo(LoanStatus.CLOSED);
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle single item checkout correctly")
        void checkoutItems_ShouldHandleSingleItem_WhenSuccessful() {
            // Given
            var singleItemRequest = new LoanRequest(List.of(1L));
            when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));
            when(libraryItemRepository.findAvailableItemById(1L)).thenReturn(Optional.of(availableBook));
            when(loanRepository.save(any(Loan.class))).thenReturn(existingLoan);

            // When
            var result = loanService.checkoutItems(1L, singleItemRequest);

            // Then
            assertThat(result).isNotNull();
            verify(loanItemRepository).saveAll(anyList());
        }

        @Test
        @DisplayName("Should handle multiple items checkout correctly")
        void checkoutItems_ShouldHandleMultipleItems_WhenSuccessful() {
            // Given
            var multipleItemsRequest = new LoanRequest(List.of(1L, 2L, 3L));
            when(memberRepository.findById(1L)).thenReturn(Optional.of(testMember));
            when(libraryItemRepository.findAvailableItemById(1L)).thenReturn(Optional.of(availableBook));
            when(libraryItemRepository.findAvailableItemById(2L)).thenReturn(Optional.of(availableJournal));
            when(libraryItemRepository.findAvailableItemById(3L)).thenReturn(Optional.of(availableBook));
            when(loanRepository.save(any(Loan.class))).thenReturn(existingLoan);

            // When
            var result = loanService.checkoutItems(1L, multipleItemsRequest);

            // Then
            assertThat(result).isNotNull();
            verify(loanItemRepository).saveAll(loanItemsCaptor.capture());
            assertThat(loanItemsCaptor.getValue()).hasSize(3);
        }
    }
}
