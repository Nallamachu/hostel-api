package com.msrts.hostel.repository;

import com.msrts.hostel.entity.Room;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    @Query(value = "from Room r where r.hostel=?1")
    List<Room> findAllRoomsByHostelId(Long hostelId, Pageable pageable);

    @Query(value = "select count(*) from Room where id=?1 and isActive=true", nativeQuery = true)
    Number activeTenantCountByRoomId(Long roomId);
}
