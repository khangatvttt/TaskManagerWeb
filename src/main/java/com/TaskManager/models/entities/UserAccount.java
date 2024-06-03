package com.TaskManager.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class UserAccount implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="UserSequenceGenerator")
    @SequenceGenerator(allocationSize=1, schema="public",  name="UserSequenceGenerator", sequenceName = "UserSequence")
    private Integer id;

    @Column(unique = true)
    @Email(message = "Email is invalid")
    private String email;

    @NotBlank(message = "Password can't be leave empty")
    @Size(min = 6, message = "Password must have at least 6 characters")
    private String password;

    @NotBlank(message = "Name can't be leave empty")
    private String name;

    private String profilePicture;

    @OneToMany(mappedBy = "creator")
    @JsonIgnore
    private List<Task> createdTaskList = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "taskExecutor", cascade = CascadeType.ALL)
    @Fetch(FetchMode.JOIN)
    private List<TaskAssignment> taskAssignments = new ArrayList<>();





    public void merge(UserAccount otherUser){
        Field[] fields = this.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(otherUser);
                if (value != null) {
                    field.set(this, value);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

