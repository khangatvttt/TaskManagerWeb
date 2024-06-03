package com.TaskManager.models.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

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

    private LocalDateTime assignedAt;

    private Boolean isAccepted;

    //Progress of each person participant in Task
    @Enumerated(EnumType.STRING)
    private Task.Status status;
}

