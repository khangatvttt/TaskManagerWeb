package com.TaskManager.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class UserAccount implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="UserSequenceGenerator")
    @SequenceGenerator(allocationSize=1, schema="public",  name="UserSequenceGenerator", sequenceName = "UserSequence")
    private Integer id;

    private String email;

    private String password;

    private String name;

    private String profilePicture;

    @JsonIgnore
    @OneToMany(mappedBy = "userId", cascade = CascadeType.DETACH)
    private List<TaskAssignment> taskAssignments = new ArrayList<>();


}

