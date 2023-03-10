package com.example.chirper.persistence.entity;

import com.example.chirper.validation.UniqueUsername;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue
    private Long id;
    @NotNull(message = "{chirper.constraints.username.NotNull.message}")
    @Size(min = 4, max = 255)
    @UniqueUsername
    private String username;
    @NotNull
    @Size(min = 4, max = 255)
    private String displayName;
    @NotNull
    @Size(min = 8, max = 255)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message = "{chirper.constraints.password.Pattern.message}")
    private String password;

    private String image;

    @Enumerated(EnumType.STRING)
    private Role role;


    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    private List<Post> posts;


}
