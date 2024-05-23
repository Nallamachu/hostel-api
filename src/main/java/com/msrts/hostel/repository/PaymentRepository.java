package com.msrts.hostel.repository;

import com.msrts.hostel.entity.Payment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query(value = "from Payment p where p.tenant=?1")
    List<Payment> findAllByTenantId(Long tenantId, Pageable pageable);

    @Query(value = "select p.* from Payment p join Tenant t on p.tenant=t.id and t.id in (?1) and t.isActive = true and p.date between ?2 and ?3", nativeQuery = true)
    List<Payment> findAllPaymentsByTenantIdsAndTimePeriod(List<Long> tenantIds, String startDate, String endDate, Pageable pageable);
}
