package com.msrts.hostel.repository;

import com.msrts.hostel.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query(value = "from User u where u.email=?1")
    User findByEmail(String email);

    @Query(value = "from User u where u.username=?1")
    User findByUsername(String username);

}
