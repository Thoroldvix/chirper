package com.example.chirper.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class FileAttachment {
    @Id
    @GeneratedValue
    private Long id;
    private LocalDateTime date;
    private String name;

    private String fileType;

    @OneToOne
    private Post post;


}
