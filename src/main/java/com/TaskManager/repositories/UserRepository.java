package com.TaskManager.repositories;

import com.TaskManager.models.entities.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserAccount,Integer> {
}
