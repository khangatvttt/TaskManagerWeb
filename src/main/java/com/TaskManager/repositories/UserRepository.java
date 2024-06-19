package com.TaskManager.repositories;

import com.TaskManager.models.entities.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserAccount,Integer> {
    boolean existsByEmail(String email);
    Optional<UserAccount> findByEmail(String email);
    Optional<UserAccount> findByVerificationCode(String code);
}
