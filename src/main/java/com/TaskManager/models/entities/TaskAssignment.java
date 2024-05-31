package com.TaskManager.models.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Entity
@IdClass(UserTaskPK.class)
@NoArgsConstructor
@Data
public class TaskAssignment implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @ManyToOne
    @JoinColumn(referencedColumnName = "id", name = "executorId")
    private UserAccount taskExecutor;

    @Id
    @ManyToOne
    @JoinColumn(referencedColumnName = "id", name = "taskId")
    private Task task;

    private UserAccount taskCreator;

    //Progress of each person participant in Task
    @Enumerated(EnumType.STRING)
    private Task.Status status;
}

