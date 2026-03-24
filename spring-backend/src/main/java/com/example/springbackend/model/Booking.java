package com.example.springbackend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "bookings")
public class Booking {
    @Id
    private String id;
    private String userId;
    private String carId;
    private String pickupAddress;
    private Date pickupDate;
    private String pickupTime;
    private String dropOffAddress;
    private String dropOffTime;
    private Date dropOffDate;
    private String status = "pending"; // pending, approved, cancelled
    private String name;
    private String phone;
    private String email;
}
