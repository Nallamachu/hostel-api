package com.msrts.hostel.repository;

import com.msrts.hostel.entity.Payment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query(value = "from Payment p where p.tenant.id=?1")
    List<Payment> findAllByTenantId(Long tenantId, Pageable pageable);

    @Query(value = "from Payment p where p.tenant.id in (?1) and p.startDate between ?2 and ?3")
    List<Payment> findAllPaymentsByTenantIdsAndTimePeriod(List<Long> tenantIds, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}
