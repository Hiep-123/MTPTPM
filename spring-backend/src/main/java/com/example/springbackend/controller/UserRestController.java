package com.example.springbackend.controller;

import com.example.springbackend.model.Booking;
import com.example.springbackend.model.User;
import com.example.springbackend.repository.BookingRepository;
import com.example.springbackend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserRestController {
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    public UserRestController(UserRepository userRepository, BookingRepository bookingRepository) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/getInfo/{id}")
    public ResponseEntity<?> getUser(@PathVariable String id) {
        Optional<User> u = userRepository.findById(id);
        if (u.isEmpty())
            return ResponseEntity.notFound().build();
        User user = u.get();
        if (user.getBookingId() != null && !user.getBookingId().isEmpty()) {
            List<Booking> bookings = bookingRepository.findAllById(user.getBookingId());
            return ResponseEntity.ok(Map.of("user", user, "bookings", bookings));
        }
        return ResponseEntity.ok(Map.of("user", user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody Map<String, Object> body) {
        return userRepository.findById(id).map(user -> {
            if (body.containsKey("userName"))
                user.setUserName((String) body.get("userName"));
            if (body.containsKey("email"))
                user.setEmail((String) body.get("email"));
            if (body.containsKey("phone"))
                user.setPhone((String) body.get("phone"));
            if (body.containsKey("name"))
                user.setName((String) body.get("name"));
            if (body.containsKey("role"))
                user.setRole((String) body.get("role"));
            userRepository.save(user);
            return ResponseEntity.ok(Map.of("message", "Cập nhật thành công", "user", user));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        Optional<User> u = userRepository.findById(id);
        if (u.isEmpty())
            return ResponseEntity.notFound().build();
        userRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "User deleted", "user", u.get()));
    }
}
