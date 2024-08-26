package com.TaskManager.models.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.Field;
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

    private String subTaskName;

    private LocalDateTime assignedAt;

    private Boolean isAccepted;

    //Progress of each person participant in Task
    @Enumerated(EnumType.STRING)
    private Task.Status status;

    //Percentage of completion
    private int progression;

    @Max(value = 10, message = "The priority can only be between 1-10")
    @Min(value = 1, message = "The priority can only be between 1-10")
    private int priority;

    public void merge(TaskAssignment otherTask){
        //User can only update these fields
        if (otherTask.getPriority()!=0){
            this.setPriority(otherTask.getPriority());
        }
        if (otherTask.getStatus()!=null){
            this.setStatus(otherTask.getStatus());
        }
        if (otherTask.getProgression()!=0){
            this.setProgression(otherTask.getProgression());
        }
        if (otherTask.getIsAccepted()!=null){
            this.setIsAccepted(otherTask.getIsAccepted());
        }
    }

}

