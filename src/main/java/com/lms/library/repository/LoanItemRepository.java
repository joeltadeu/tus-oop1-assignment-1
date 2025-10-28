package com.lms.library.repository;

import com.lms.library.model.LoanItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class LoanItemRepository {

    private static final Logger log = LoggerFactory.getLogger(LoanItemRepository.class);
    private static final Map<Long, LoanItem> STORE = new ConcurrentHashMap<>();
    private static final AtomicLong ID_SEQ = new AtomicLong(1);

    public LoanItem save(LoanItem item) {
        if (item.getId() == null) {
            item.setId(ID_SEQ.getAndIncrement());
        }
        STORE.put(item.getId(), item);
        return item;
    }

    public List<LoanItem> findByLoanId(Long loanId) {
        return STORE.values().stream()
                .filter(i -> i.getLoan().getId().equals(loanId))
                .toList();
    }

    public Optional<LoanItem> findByLoanIdAndItemId(Long loanId, Long itemId) {
        return STORE.values().stream()
                .filter(i -> i.getLoan().getId().equals(loanId) &&
                        i.getItem().getId().equals(itemId))
                .findFirst();
    }

    public List<LoanItem> findActiveItemsByLoanId(Long loanId) {
        return STORE.values().stream()
                .filter(i -> i.getLoan().getId().equals(loanId) && !i.isReturned())
                .toList();
    }
}
