package com.example.springbackend.repository;

import com.example.springbackend.model.Car;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CarRepository extends MongoRepository<Car, String> {
}
