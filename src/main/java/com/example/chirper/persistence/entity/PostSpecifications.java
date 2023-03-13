package com.example.chirper.persistence.entity;

import org.springframework.data.jpa.domain.Specification;

public class PostSpecifications {
    public static Specification<Post> isSameUser(UserEntity user) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user"), user);
    }

    public static Specification<Post> idLessThan(Long id) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThan(root.get("id"), id);
    }

    public static Specification<Post> idGreaterThan(Long id) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get("id"), id);
    }
}
