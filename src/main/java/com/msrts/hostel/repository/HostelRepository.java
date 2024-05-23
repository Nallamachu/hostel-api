package com.msrts.hostel.repository;

import com.msrts.hostel.entity.Hostel;
import com.msrts.hostel.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HostelRepository extends JpaRepository<Hostel, Long> {

    @Query(value = "from Hostel h where h.owner=?1")
    List<Hostel> findAllHostelsByUserId(User user, Pageable pageable);
}
