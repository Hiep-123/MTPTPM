package com.example.springbackend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private List<String> bookingId;
    private String name;
    private String email;
    private String phone;
    private String password;
    private String userName;
    private String role; // 'user' or 'admin'
}
