package com.TaskManager.repositories;

import com.TaskManager.models.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification,Integer> {

}

