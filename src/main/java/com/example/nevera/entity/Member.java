package com.example.nevera.entity;


import com.example.nevera.common.enums.MemberRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name="member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, columnDefinition = "text")
    private String email;

    @Column(columnDefinition = "text")
    private String name;

    @Column(columnDefinition = "text")
    private String password;

    @Column(columnDefinition = "text")
    @Builder.Default
    private String status = "ACTIVE";

    @Column(columnDefinition = "text")
    private String provider;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "text")
    @Builder.Default
    private MemberRole role = MemberRole.USER;


    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = OffsetDateTime.now();
    }

}
