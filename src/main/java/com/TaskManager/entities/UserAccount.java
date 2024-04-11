package com.TaskManager.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
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

    @OneToMany(mappedBy = "userId")
    private List<TaskAssignment> taskAssignments;


}

