package com.msrts.hostel.repository;

import com.msrts.hostel.entity.Tenant;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {
    @Query(value = "from Tenant t where t.firstName like '%?1%' and t.middleName like '%?1%' and t.lastName like '%?1%'")
    List<Tenant> findAllTenantsByGivenNameContains(String name, Pageable pageable);

    @Query(value = "from Tenant t where t.idProof like '%?1%'")
    List<Tenant> findAllTenantsByGivenIdProofContains(String idNumber, Pageable pageable);
}
