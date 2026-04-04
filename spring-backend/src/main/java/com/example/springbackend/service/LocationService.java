package com.example.springbackend.service;

import com.example.springbackend.dto.LocationRealtimeDto;
import com.example.springbackend.model.VehicleLocation;
import com.example.springbackend.repository.VehicleLocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final VehicleLocationRepository repository;
    private final SimpMessagingTemplate messagingTemplate;

    public VehicleLocation saveAndBroadcast(VehicleLocation location) {

        if (location.getBookingId() == null) {
            throw new IllegalArgumentException("bookingId is null");
        }

        VehicleLocation saved = repository.save(location);

        // 🔥 BẮN DTO REALTIME (KHÔNG BẮN ENTITY)
        messagingTemplate.convertAndSend(
                "/topic/locations/" + saved.getBookingId(),
                new LocationRealtimeDto(
                        saved.getBookingId(),
                        saved.getLat(),
                        saved.getLng(),
                        saved.getTimestamp()
                )
        );

        System.out.println("🚀 BROADCAST GPS FOR BOOKING " + saved.getBookingId());

        return saved;
    }
}