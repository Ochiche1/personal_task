package com.usersauth.usersauth.repository;

import com.usersauth.usersauth.Enum.Enum_Roles;
import com.usersauth.usersauth.model.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Roles, Integer> {
Optional<Roles> findByName(Enum_Roles name);

}

