package com.example.springbackend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "payments")
public class Payment {
    @Id
    private String id;
    private String userId;
    private String bookingId;
    private String method; // cash, transfer
    private String paymentStatus; // pending, completed, failed
    private Date transactionDate;
}
