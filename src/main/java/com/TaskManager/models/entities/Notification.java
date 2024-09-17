package com.TaskManager.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="TaskSequenceGenerator")
    @SequenceGenerator(allocationSize=1, schema="public",  name="TaskSequenceGenerator", sequenceName = "TaskSequence")
    private Integer id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "userId")
    private UserAccount receiver;

    private String notification;

    private boolean isRead;

    private LocalDateTime time;
}
