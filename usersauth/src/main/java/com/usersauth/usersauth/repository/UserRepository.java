package com.usersauth.usersauth.repository;

import com.usersauth.usersauth.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUserName(String username);
    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
}
