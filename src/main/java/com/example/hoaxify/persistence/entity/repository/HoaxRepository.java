package com.example.hoaxify.persistence.entity.repository;

import com.example.hoaxify.persistence.entity.Hoax;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HoaxRepository extends JpaRepository<Hoax, Long> {
}
