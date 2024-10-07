package com.TaskManager.repositories;

import com.TaskManager.models.entities.Task;
import com.TaskManager.models.entities.TaskAssignment;
import com.TaskManager.models.entities.UserAccount;
import com.TaskManager.models.entities.UserTaskPK;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskAssignmentRepository extends JpaRepository<TaskAssignment, UserTaskPK> {
    void deleteAllByTask(Task task);
    long countByTaskExecutorAndStatus(UserAccount taskExecutor, Task.Status status);
    Page<TaskAssignment> findByTaskExecutorAndStatusAndTask_TaskNameContainingIgnoreCaseOrTaskExecutorAndStatusAndSubTaskNameContainingIgnoreCase(UserAccount user, Task.Status status, Pageable pageable, String taskName, UserAccount user2, Task.Status status2, String subTaskName);
    Page<TaskAssignment> findByTaskExecutorAndTask_TaskNameContainingIgnoreCaseOrTaskExecutorAndSubTaskNameContainingIgnoreCase(UserAccount user, Pageable pageable, String taskName, UserAccount user2, String subTaskName);
    Page<TaskAssignment> findByTaskExecutorAndIsAcceptedAndTask_TaskNameContainingIgnoreCaseOrTaskExecutorAndIsAcceptedAndSubTaskNameContainingIgnoreCase(UserAccount user, Pageable pageable, Boolean accepted, String taskName, UserAccount user2, Boolean accepted2, String subTaskName);


}
