package com.example.springbackend.repository;

import com.example.springbackend.model.DetailPayment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DetailPaymentRepository extends MongoRepository<DetailPayment, String> {
    List<DetailPayment> findAllByPaymentId(String paymentId);
}
