package com.example.springbackend.dto;

import java.time.Instant;

public record LocationRealtimeDto(
        String bookingId,
        Double lat,
        Double lng,
        Instant timestamp
) {}