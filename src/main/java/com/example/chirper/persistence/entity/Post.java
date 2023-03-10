package com.example.chirper.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

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
}
