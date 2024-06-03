package com.TaskManager.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.io.Serial;
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

	@NotBlank(message = "Please provide task name")
	private String taskName;

	private String description;

	@Max(value = 10, message = "The priority can only be between 1-10")
	@Min(value = 1, message = "The priority can only be between 1-10")
	private int priority;

	private LocalDateTime createAt;

	private LocalDateTime dueAt;

	//Status of Task itself (Completed when all users that participant in this finished)
	@Enumerated(EnumType.STRING)
	private Status status;

	@NotNull(message = "Please provide creator of this task")
	@ManyToOne
	@JoinColumn(name = "creatorId")
	private UserAccount creator;

	@JsonIgnore
	@OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
	@Fetch(FetchMode.JOIN)
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

