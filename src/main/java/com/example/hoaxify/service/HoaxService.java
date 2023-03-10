package com.example.hoaxify.service;

import com.example.hoaxify.persistence.entity.Hoax;
import com.example.hoaxify.persistence.entity.repository.HoaxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class HoaxService {

    private final HoaxRepository hoaxRepository;

    @Autowired
    public HoaxService(HoaxRepository hoaxRepository) {
        this.hoaxRepository = hoaxRepository;
    }

    public void save(Hoax hoax) {
        hoax.setCreatedAt(LocalDateTime.now());
        hoaxRepository.save(hoax);
    }
}
