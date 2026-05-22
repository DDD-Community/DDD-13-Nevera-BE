package com.example.nevera.repository;

import com.example.nevera.entity.NotificationFailure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationFailureRepository extends JpaRepository<NotificationFailure, Long> {

    List<NotificationFailure> findAllByOrderByFailedAtDesc();
}
