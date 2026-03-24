package com.example.springbackend.service;

import com.example.springbackend.model.Booking;
import com.example.springbackend.model.User;
import com.example.springbackend.repository.BookingRepository;
import com.example.springbackend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public BookingService(BookingRepository bookingRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    public Booking createBooking(Booking booking) throws Exception {
        // Set default status if not provided
        if (booking.getStatus() == null) {
            booking.setStatus("pending");
        }

        Booking saved = bookingRepository.save(booking);

        // Update user with booking info, phone, name, email
        if (booking.getUserId() != null) {
            userRepository.findById(booking.getUserId()).ifPresent(user -> {
                // Update user info from booking
                if (booking.getName() != null && !booking.getName().isEmpty()) {
                    user.setName(booking.getName());
                }
                if (booking.getPhone() != null && !booking.getPhone().isEmpty()) {
                    user.setPhone(booking.getPhone());
                }
                if (booking.getEmail() != null && !booking.getEmail().isEmpty()) {
                    user.setEmail(booking.getEmail());
                }

                // Add booking ID to user's bookingId list
                if (user.getBookingId() == null) {
                    user.setBookingId(new ArrayList<>());
                }
                user.getBookingId().add(saved.getId());
                userRepository.save(user);

                System.out.println("✅ Updated user " + user.getId() + " with name: " + user.getName() + ", phone: "
                        + user.getPhone());
            });
        }
        return saved;
    }

    public User getUserById(String userId) throws Exception {
        return userRepository.findById(userId).orElse(null);
    }
}
