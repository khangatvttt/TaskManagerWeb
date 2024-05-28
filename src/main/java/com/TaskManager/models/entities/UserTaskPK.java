package com.TaskManager.models.entities;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class UserTaskPK implements Serializable {

    private Integer userId;

    private Integer taskId;
}
