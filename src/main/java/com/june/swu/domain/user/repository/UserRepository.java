package com.june.swu.domain.user.repository;

import com.june.swu.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u from User u where u.userId = :id and u.isActive = true")
    Optional<User> findById(@Param("id") Long id);
    @Query("select u from User u where u.email = :email and u.isActive = true")
    Optional<User> findByEmail(@Param("email") String email);
}
