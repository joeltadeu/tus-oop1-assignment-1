package com.lms.library.repository;

import com.lms.library.model.LoanItem;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 * Repository class for managing LoanItem entities.
 * Provides data access operations for loan items using an in-memory store.
 *
 * @author Joel Silva
 * @version 1.0
 * @see LoanItem
 * @since 2025
 */
@Repository
public class LoanItemRepository {

    private static final Logger log = LoggerFactory.getLogger(LoanItemRepository.class);
    private static final Map<Long, LoanItem> STORE = new ConcurrentHashMap<>();
    private static final AtomicLong ID_SEQ = new AtomicLong(1);

    /**
     * Default constructor for LoanItemRepository.
     */
    public LoanItemRepository() {
    }

    /**
     * Saves a loan item to the repository.
     * If the item has no ID, generates a new one automatically.
     *
     * @param item the loan item to save
     * @return the saved loan item with generated ID
     */
    public LoanItem save(LoanItem item) {
        if (item.getId() == null) {
            item.setId(ID_SEQ.getAndIncrement());
        }
        STORE.put(item.getId(), item);
        return item;
    }

    /**
     * Finds all loan items for a specific loan.
     *
     * @param loanId the ID of the loan
     * @return a list of all loan items for the specified loan
     */
    public List<LoanItem> findByLoanId(Long loanId) {
        return STORE.values().stream()
                .filter(i -> i.getLoan().getId().equals(loanId))
                .toList();
    }

    /**
     * Finds a specific loan item by loan ID and item ID.
     *
     * @param loanId the ID of the loan
     * @param itemId the ID of the library item
     * @return an Optional containing the found loan item, or empty if not found
     */
    public Optional<LoanItem> findByLoanIdAndItemId(Long loanId, Long itemId) {
        return STORE.values().stream()
                .filter(i -> i.getLoan().getId().equals(loanId) &&
                        i.getItem().getId().equals(itemId))
                .findFirst();
    }

    /**
     * Finds all active (not returned) items for a specific loan.
     *
     * @param loanId the ID of the loan
     * @return a list of loan items that haven't been returned yet
     */
    public List<LoanItem> findActiveItemsByLoanId(Long loanId) {
        return STORE.values().stream()
                .filter(i -> i.getLoan().getId().equals(loanId) && !i.isReturned())
                .toList();
    }

    /**
     * Saves multiple loan items in batch.
     *
     * @param items the list of loan items to save
     */
    public void saveAll(List<LoanItem> items) {
        for (LoanItem item : items) {
            save(item);
        }
    }
}
