package com.example.chirper.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Data
@Entity
public class Post {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @Size(min = 10, max = 5000)
    @Column(length = 5000)
    private String content;

    @ManyToOne
    private UserEntity user;

    private LocalDateTime createdAt;

    public long toMillis() {
        if (createdAt == null) {
            return 0;
        }
        ZonedDateTime zdt = ZonedDateTime.of(createdAt, ZoneId.systemDefault());
        return zdt.toInstant().toEpochMilli();
    }
}
