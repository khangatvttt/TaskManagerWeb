package com.TaskManager.repositories;

import com.TaskManager.models.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task,Integer> {

}
