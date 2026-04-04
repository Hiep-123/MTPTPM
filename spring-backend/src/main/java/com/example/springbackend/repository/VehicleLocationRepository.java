package com.example.springbackend.repository;

import com.example.springbackend.model.VehicleLocation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface VehicleLocationRepository
        extends MongoRepository<VehicleLocation, String> {

    Optional<VehicleLocation> findTopByBookingIdOrderByTimestampDesc(String bookingId);
}