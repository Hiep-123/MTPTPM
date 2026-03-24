package com.example.springbackend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "comments")
public class Comment {
    @Id
    private String id;
    private String bookingId; // nullable
    private String userId;
    private Integer ratingPoints;
    private String comment;
    private Date createdAt;
}
