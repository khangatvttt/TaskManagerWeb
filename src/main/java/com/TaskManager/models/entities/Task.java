package com.TaskManager.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

	//Status of Task itself (Completed when all users that participant in this finished)
	@Enumerated(EnumType.STRING)
	private Status status;

	@JsonIgnore
	@OneToMany(mappedBy = "taskId", cascade = CascadeType.ALL)
	private List<TaskAssignment> taskAssignments = new ArrayList<>();

	public void merge(Task otherTask){
		Field[] fields = this.getClass().getDeclaredFields();

		for (Field field : fields) {
			field.setAccessible(true);
			try {
				Object value = field.get(otherTask);
				if (value != null) {
					field.set(this, value);
				}
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
	}

}

