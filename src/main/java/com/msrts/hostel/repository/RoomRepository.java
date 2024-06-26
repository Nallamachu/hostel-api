package com.msrts.hostel.repository;

import com.msrts.hostel.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    @Query(value = "from Room r where r.hostel.id=?1")
    Page<Room> findAllRoomsByHostelId(Long hostelId, Pageable pageable);

    @Query(value = "select count(*) from room where id=?1 and isActive=true", nativeQuery = true)
    Number activeTenantCountByRoomId(Long roomId);

    @Query(value = "select distinct r.id from room r where r.hostel_id=?1", nativeQuery = true)
    List<Long> findAllRoomIdsByHostelId(Long hostelId);

    @Query(value = """
            select * from room r where r.hostel_id in (\s
            select h.id from hostel h where h.owner_id=?1 and is_active=true\s
            ) order by r.floor_no\s
            """, nativeQuery = true)
    Page<Room> findAllRoomsIdByUserId(Long userId, Pageable pageable);
}
