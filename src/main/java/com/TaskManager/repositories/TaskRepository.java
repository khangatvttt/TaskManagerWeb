package com.TaskManager.repositories;

import com.TaskManager.models.entities.Task;
import com.TaskManager.models.entities.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task,Integer> {
    Optional<Task> findByTaskNameAndCreator(String taskName, UserAccount creator);
}
