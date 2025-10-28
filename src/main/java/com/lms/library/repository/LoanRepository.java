package com.lms.library.repository;

import com.lms.library.model.Loan;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class LoanRepository {
    private static final Map<Long, Loan> STORE = new ConcurrentHashMap<>();
    private static final AtomicLong ID_SEQ = new AtomicLong(1);

    public Loan save(Loan loan) {
        if (loan.getId() == null) {
            loan.setId(ID_SEQ.getAndIncrement());
        }
        STORE.put(loan.getId(), loan);
        return loan;
    }

    public Optional<Loan> findById(Long id) {
        return Optional.ofNullable(STORE.get(id));
    }

    public List<Loan> findByMemberIdOrderByLoanDateDesc(Long memberId) {
        return STORE.values().stream()
                .filter(l -> l.getMember().getId().equals(memberId))
                .sorted(Comparator.comparing(Loan::getLoanDate).reversed())
                .toList();
    }

    public Optional<Loan> findByIdWithMemberAndItems(Long id) {
        return findById(id); // no lazy loading needed
    }

    public Optional<Loan> findByIdWithItems(Long id) {
        return findById(id);
    }
}
