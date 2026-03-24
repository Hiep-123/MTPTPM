package com.example.springbackend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "brandcars")
public class BrandCar {
    @Id
    private String id;
    private String nameBrandCar;
}
