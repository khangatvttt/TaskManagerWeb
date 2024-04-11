package com.TaskManager.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Entity
@IdClass(UserTaskPK.class)
@Data
public class TaskAssignment implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(referencedColumnName = "id", name = "userId")
    private UserAccount userId;

    @Id
    @ManyToOne
    @JoinColumn(referencedColumnName = "id", name = "taskId")
    private Task taskId;

    private UserAccount assignBy;

    @Enumerated(EnumType.STRING)
    private Task.Status status;
}

