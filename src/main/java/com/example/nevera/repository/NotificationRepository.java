package com.example.nevera.repository;

import com.example.nevera.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT n FROM Notification n WHERE n.member.id = :memberId ORDER BY n.createdAt DESC LIMIT :limit OFFSET :offset")
    List<Notification> findByMemberIdWithOffset(
            @Param("memberId") Long memberId,
            @Param("offset") int offset,
            @Param("limit") int limit
    );
}
