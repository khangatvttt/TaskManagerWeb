package com.TaskManager.models.entities;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
public class UserTaskPK implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer taskExecutor;

    private Integer task;
}
