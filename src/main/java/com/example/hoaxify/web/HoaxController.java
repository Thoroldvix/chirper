package com.example.hoaxify.web;

import com.example.hoaxify.persistence.entity.Hoax;
import com.example.hoaxify.service.HoaxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/1.0")
public class HoaxController {

    private final HoaxService hoaxService;

    @Autowired
    public HoaxController(HoaxService hoaxService) {
        this.hoaxService = hoaxService;
    }

    @PostMapping("/hoaxes")
    public void createHoax(@RequestBody Hoax hoax) {
        hoaxService.save(hoax);
    }
}
