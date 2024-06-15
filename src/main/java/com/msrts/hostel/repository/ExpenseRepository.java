package com.msrts.hostel.repository;

import com.msrts.hostel.entity.Expense;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query(value = "from Expense e where e.hostel.id = ?1")
    List<Expense> findAllAllExpensesByHostelId(Long hostelId, Pageable pageable);

    @Query(value = "from Expense e where e.hostel.id = ?1 and e.date between ?2 and ?3")
    List<Expense> findAllExpensesByHostelIdAndTimePeriod(Long hostelId, LocalDate startDate, LocalDate endDate, Pageable pageable);

    @Query(value = """
    select * from Expense e where e.hostel_id in (
    select h.id from hostel h where h.owner_id = ?1) 
    and e.date between ?2 and ?3
    """, nativeQuery = true)
    List<Expense> findAllExpensesByUserIdAndTimePeriod(Long userId, LocalDate startDate, LocalDate endDate);

}
