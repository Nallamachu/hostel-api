package com.msrts.hostel.repository;

import com.msrts.hostel.entity.Tenant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {
    @Query(value = "from Tenant t where t.firstName like %?1% or t.middleName like %?2% or t.lastName like %?3%")
    Page<Tenant> findAllTenantsByGivenNameContains(String firstName, String middleName, String lastName, Pageable pageable);

    @Query(value = "from Tenant t where t.idNumber = ?1")
    Page<Tenant> findAllTenantsByGivenIdNumber(String idNumber, Pageable pageable);

    @Query(value = "select count(*) from Tenant t where t.room_id=?1 and t.is_active=true", nativeQuery = true)
    Number activeTenantCountByRoomId(Long roomId);

    @Query(value = "from Tenant t where t.room.id in (?1)")
    Page<Tenant> findAllTenantsByRoomIdsIn(List<Long> roomIds, Pageable pageable);

    @Query(value = """
            select t.* from tenant t 
            where t.is_active = true 
            and t.room_id in (select r.id from room r where r.hostel_id = ?1)
            """, nativeQuery = true)
    Page<Tenant> findAllActiveTenantsByHostelId(Long hostelId, Pageable pageable);
}
