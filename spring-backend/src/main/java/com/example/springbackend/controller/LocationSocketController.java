package com.example.springbackend.controller;

import com.example.springbackend.model.VehicleLocation;
import com.example.springbackend.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.time.Instant;

@Controller
@RequiredArgsConstructor
public class LocationSocketController {

    private final LocationService locationService;

    @MessageMapping("/location.send")
    public void receiveLocation(@Payload VehicleLocation location) {

        // 🔥 SET TIMESTAMP TẠI BACKEND
        location.setTimestamp(Instant.now());

        System.out.println("GPS RECEIVED: " + location);
        locationService.saveAndBroadcast(location);
    }
}