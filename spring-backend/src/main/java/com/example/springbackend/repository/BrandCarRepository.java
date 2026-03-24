package com.example.springbackend.repository;

import com.example.springbackend.model.BrandCar;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BrandCarRepository extends MongoRepository<BrandCar, String> {
}
