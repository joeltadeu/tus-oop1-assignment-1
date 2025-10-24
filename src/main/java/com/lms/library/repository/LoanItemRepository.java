package com.lms.library.repository;

import com.lms.library.model.LoanItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanItemRepository extends JpaRepository<LoanItem, Long> {

    List<LoanItem> findByLoanId(Long loanId);

    @Query("SELECT li FROM LoanItem li JOIN FETCH li.item WHERE li.loan.id = :loanId AND li.item.id = :itemId")
    Optional<LoanItem> findByLoanIdAndItemId(@Param("loanId") Long id, @Param("itemId") Long itemId);

    @Query("SELECT li FROM LoanItem li WHERE li.loan.id = :loanId AND li.returnedDate IS NULL")
    List<LoanItem> findActiveItemsByLoanId(@Param("loanId") Long loanId);
}
