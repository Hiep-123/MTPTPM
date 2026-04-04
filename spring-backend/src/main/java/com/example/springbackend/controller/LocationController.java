package com.example.springbackend.controller;

import com.example.springbackend.model.VehicleLocation;
import com.example.springbackend.repository.VehicleLocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
@CrossOrigin("*")
public class LocationController {

    private final VehicleLocationRepository repository;

    @GetMapping("/latest/{bookingId}")
    public VehicleLocation latest(@PathVariable String bookingId) {
        return repository
                .findTopByBookingIdOrderByTimestampDesc(bookingId)
                .orElse(null);
    }
}