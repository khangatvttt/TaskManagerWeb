package com.TaskManager.repositories;

import com.TaskManager.models.entities.Task;
import com.TaskManager.models.entities.TaskAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task,Integer> {
}
