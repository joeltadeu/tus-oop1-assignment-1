package com.lms.library.repository;

import com.lms.library.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByMemberIdOrderByLoanDateDesc(Long memberId);

    @Query("SELECT l FROM Loan l JOIN FETCH l.member WHERE l.id = :id")
    Optional<Loan> findByIdWithMember(@Param("id") Long id);

    @Query("SELECT l FROM Loan l JOIN FETCH l.items i JOIN FETCH i.item WHERE l.id = :id")
    Optional<Loan> findByIdWithItems(@Param("id") Long id);

    @Query("SELECT l FROM Loan l JOIN FETCH l.member JOIN FETCH l.items i JOIN FETCH i.item WHERE l.id = :id")
    Optional<Loan> findByIdWithMemberAndItems(@Param("id") Long id);
}
