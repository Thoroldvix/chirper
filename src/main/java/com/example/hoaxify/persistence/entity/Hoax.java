package com.example.hoaxify.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Hoax {

    @Id
    @GeneratedValue
    private Long id;

    private String content;


    private LocalDateTime createdAt;
}
