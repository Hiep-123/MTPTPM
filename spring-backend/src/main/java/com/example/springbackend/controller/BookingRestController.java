package com.example.springbackend.controller;

import com.example.springbackend.model.Booking;
import com.example.springbackend.model.Car;
import com.example.springbackend.model.User;
import com.example.springbackend.service.BookingService;
import com.example.springbackend.repository.BookingRepository;
import com.example.springbackend.repository.CarRepository;
import com.example.springbackend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin
public class BookingRestController {
    private final BookingService bookingService;
    private final BookingRepository bookingRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;

    public BookingRestController(BookingService bookingService, BookingRepository bookingRepository,
            CarRepository carRepository, UserRepository userRepository) {
        this.bookingService = bookingService;
        this.bookingRepository = bookingRepository;
        this.carRepository = carRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        List<Booking> bookings = bookingRepository.findAll();
        List<Map<String, Object>> response = new java.util.ArrayList<>();

        for (Booking booking : bookings) {
            Map<String, Object> bookingData = new java.util.HashMap<>();
            bookingData.put("booking", booking);

            // Get user info
            if (booking.getUserId() != null) {
                userRepository.findById(booking.getUserId()).ifPresent(user -> bookingData.put("user", user));
            }

            // Get car info
            if (booking.getCarId() != null) {
                carRepository.findById(booking.getCarId()).ifPresent(car -> bookingData.put("car", car));
            }

            response.add(bookingData);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        return bookingRepository.findById(id).map(booking -> {
            Map<String, Object> response = new java.util.HashMap<>();
            response.put("booking", booking);

            // Get user info
            if (booking.getUserId() != null) {
                userRepository.findById(booking.getUserId()).ifPresent(user -> response.put("user", user));
            }

            // Get car info
            if (booking.getCarId() != null) {
                carRepository.findById(booking.getCarId()).ifPresent(car -> response.put("car", car));
            }

            return ResponseEntity.ok(response);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Booking booking) {
        try {
            System.out.println("📝 Creating booking with carId: " + booking.getCarId());

            Booking saved = bookingService.createBooking(booking);
            User user = bookingService.getUserById(booking.getUserId());

            System.out.println("✅ Saved booking with id: " + saved.getId() + ", carId: " + saved.getCarId());

            Map<String, Object> response = new java.util.HashMap<>();
            response.put("message", "Đặt xe thành công!");
            response.put("booking", saved);
            response.put("user", user);

            // Get car info if carId exists
            if (saved.getCarId() != null && !saved.getCarId().isEmpty()) {
                System.out.println("🚗 Looking for car with id: " + saved.getCarId());
                Car car = carRepository.findById(saved.getCarId()).orElse(null);
                if (car != null) {
                    response.put("car", car);
                    System.out.println("✅ Found car: " + car.getId());
                } else {
                    System.out.println("❌ Car not found with id: " + saved.getCarId());
                }
            } else {
                System.out.println("⚠️ carId is null or empty");
            }

            return ResponseEntity.status(201).body(response);
        } catch (Exception e) {
            System.out.println("❌ Error creating booking: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Booking> update(@PathVariable String id, @RequestBody Booking booking) {
        return bookingRepository.findById(id).map(existing -> {
            booking.setId(existing.getId());
            return ResponseEntity.ok(bookingRepository.save(booking));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        bookingRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
