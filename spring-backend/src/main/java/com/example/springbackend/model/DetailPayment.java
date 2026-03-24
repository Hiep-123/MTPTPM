package com.example.springbackend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "detailpayments")
public class DetailPayment {
    @Id
    private String id;
    private String bookingId;
    private String paymentId;
    private Double amountCar;
    private Double totalAmount;
}
