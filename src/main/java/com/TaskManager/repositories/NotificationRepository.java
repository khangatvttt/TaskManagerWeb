package com.TaskManager.repositories;

import com.TaskManager.models.entities.Notification;
import com.TaskManager.models.entities.UserAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification,Integer> {
    Page<Notification> findByReceiverOrderByTimeDesc(UserAccount user, Pageable pageable);
    Integer countByIsReadAndReceiver(Boolean read, UserAccount user);

}

