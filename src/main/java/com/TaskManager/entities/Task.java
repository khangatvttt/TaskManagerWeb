package com.TaskManager.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Task implements Serializable {

	public static enum Status {
		PENDING,
		INPROGRESS,
		COMPLETED,
		CANCELLED
	}

	@Id
	@GeneratedValue(strategy= GenerationType.SEQUENCE, generator="TaskSequenceGenerator")
	@SequenceGenerator(allocationSize=1, schema="public",  name="TaskSequenceGenerator", sequenceName = "TaskSequence")
	private Integer id;

	private String taskName;

	private String description;

	private int priority;

	private LocalDateTime createAt;

	private LocalDateTime dueAt;

	@Enumerated(EnumType.STRING)
	private Status status;

	@OneToMany(mappedBy = "taskId")
	private List<TaskAssignment> taskAssignments;

}

