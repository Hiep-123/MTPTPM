package com.example.springbackend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document(collection = "vehiclelocations")
public class VehicleLocation {

    @Id
    private String id;

    private String bookingId;
    private Double lat;
    private Double lng;

    // 🔥 Backend là nguồn thời gian DUY NHẤT
    private Instant timestamp;
}