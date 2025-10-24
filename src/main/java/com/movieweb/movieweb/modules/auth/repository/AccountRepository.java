package com.movieweb.movieweb.modules.auth.repository;

import com.movieweb.movieweb.modules.auth.entity.Account;
import com.movieweb.movieweb.modules.auth.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUsername(String username);
    Optional<Account> findByUsernameAndRole(String username, String role);
    boolean existsByRole(Role role);
}