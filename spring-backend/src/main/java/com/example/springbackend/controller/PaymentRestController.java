package com.example.springbackend.controller;

import com.example.springbackend.model.DetailPayment;
import com.example.springbackend.model.Payment;
import com.example.springbackend.repository.BookingRepository;
import com.example.springbackend.repository.DetailPaymentRepository;
import com.example.springbackend.repository.PaymentRepository;
import com.example.springbackend.repository.UserRepository;
import com.example.springbackend.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin
public class PaymentRestController {
    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final DetailPaymentRepository detailPaymentRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    public PaymentRestController(PaymentService paymentService, PaymentRepository paymentRepository,
            DetailPaymentRepository detailPaymentRepository,
            UserRepository userRepository, BookingRepository bookingRepository) {
        this.paymentService = paymentService;
        this.paymentRepository = paymentRepository;
        this.detailPaymentRepository = detailPaymentRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        List<Payment> payments = paymentRepository.findAll();
        List<Map<String, Object>> response = new ArrayList<>();

        for (Payment payment : payments) {
            Map<String, Object> paymentData = new HashMap<>();
            paymentData.put("payment", payment);

            // Get user info
            if (payment.getUserId() != null) {
                userRepository.findById(payment.getUserId()).ifPresent(user -> paymentData.put("user", user));
            }

            // Get booking info
            if (payment.getBookingId() != null) {
                bookingRepository.findById(payment.getBookingId())
                        .ifPresent(booking -> paymentData.put("booking", booking));
            }

            // Get detail payment info
            List<DetailPayment> detailPayments = detailPaymentRepository.findAllByPaymentId(payment.getId());
            if (!detailPayments.isEmpty()) {
                paymentData.put("detailPayment", detailPayments.get(0));
            }

            response.add(paymentData);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        return paymentRepository.findById(id).map(payment -> {
            Map<String, Object> response = new HashMap<>();
            response.put("payment", payment);

            // Get user info
            if (payment.getUserId() != null) {
                userRepository.findById(payment.getUserId()).ifPresent(user -> response.put("user", user));
            }

            // Get booking info
            if (payment.getBookingId() != null) {
                bookingRepository.findById(payment.getBookingId())
                        .ifPresent(booking -> response.put("booking", booking));
            }

            // Get detail payment info
            List<DetailPayment> detailPayments = detailPaymentRepository.findAllByPaymentId(payment.getId());
            if (!detailPayments.isEmpty()) {
                response.put("detailPayment", detailPayments.get(0));
            }

            return ResponseEntity.ok(response);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createPayment(@RequestBody Map<String, Object> body) {
        try {
            Payment p = new Payment();
            p.setUserId((String) body.get("userId"));
            p.setBookingId((String) body.get("bookingId"));
            p.setMethod((String) body.get("method"));
            Double amountCar = body.get("amountCar") != null ? Double.parseDouble(body.get("amountCar").toString())
                    : 0.0;
            Double totalAmount = body.get("totalAmount") != null
                    ? Double.parseDouble(body.get("totalAmount").toString())
                    : 0.0;
            Payment saved = paymentService.createPayment(p, amountCar, totalAmount);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Payment created successfully!");
            response.put("payment", saved);

            // Get user info
            if (p.getUserId() != null) {
                userRepository.findById(p.getUserId()).ifPresent(user -> response.put("user", user));
            }

            // Get booking info
            if (p.getBookingId() != null) {
                bookingRepository.findById(p.getBookingId()).ifPresent(booking -> response.put("booking", booking));
            }

            // Get detail payment info
            List<DetailPayment> detailPayments = detailPaymentRepository.findAllByPaymentId(saved.getId());
            if (!detailPayments.isEmpty()) {
                response.put("detailPayment", detailPayments.get(0));
            }

            return ResponseEntity.status(201).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePayment(@PathVariable String id, @RequestBody Payment p) {
        Payment updated = paymentService.updatePayment(id, p);
        if (updated == null)
            return ResponseEntity.notFound().build();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Cập nhật thanh toán thành công");
        response.put("payment", updated);

        // Get user info
        if (updated.getUserId() != null) {
            userRepository.findById(updated.getUserId()).ifPresent(user -> response.put("user", user));
        }

        // Get booking info
        if (updated.getBookingId() != null) {
            bookingRepository.findById(updated.getBookingId()).ifPresent(booking -> response.put("booking", booking));
        }

        // Get detail payment info
        List<DetailPayment> detailPayments = detailPaymentRepository.findAllByPaymentId(updated.getId());
        if (!detailPayments.isEmpty()) {
            response.put("detailPayment", detailPayments.get(0));
        }

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePayment(@PathVariable String id) {
        return paymentRepository.findById(id).map(payment -> {
            paymentRepository.deleteById(id);
            // Also delete associated detail payment
            List<DetailPayment> detailPayments = detailPaymentRepository.findAllByPaymentId(id);
            for (DetailPayment dp : detailPayments) {
                detailPaymentRepository.delete(dp);
            }
            return ResponseEntity.ok(Map.of("message", "Xóa thanh toán thành công", "payment", payment));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
