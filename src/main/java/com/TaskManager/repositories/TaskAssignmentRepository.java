package com.TaskManager.repositories;

import com.TaskManager.models.entities.TaskAssignment;
import com.TaskManager.models.entities.UserAccount;
import com.TaskManager.models.entities.UserTaskPK;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskAssignmentRepository extends JpaRepository<TaskAssignment, UserTaskPK> {
}
