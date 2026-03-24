package com.example.springbackend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "cars")
public class Car {
    @Id
    private String id;
    private String img;
    private String category;
    private String brandId; // store BrandCar id
    private Double pricePerDay;
    private String des;
    private Date createdAt;
    private Date updatedAt;
}
