package com.msrts.hostel.repository;

import com.msrts.hostel.entity.Hostel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HostelRepository extends JpaRepository<Hostel, Long> {

    @Query(value = "from Hostel h where h.owner.id=?1")
    Page<Hostel> findAllHostelsByUserId(Integer userId, Pageable pageable);
}
