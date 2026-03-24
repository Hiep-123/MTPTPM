package com.example.springbackend.service;

import com.example.springbackend.model.DetailPayment;
import com.example.springbackend.model.Payment;
import com.example.springbackend.repository.DetailPaymentRepository;
import com.example.springbackend.repository.PaymentRepository;
import com.example.springbackend.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final DetailPaymentRepository detailPaymentRepository;
    private final BookingRepository bookingRepository;

    public PaymentService(PaymentRepository paymentRepository, DetailPaymentRepository detailPaymentRepository,
            BookingRepository bookingRepository) {
        this.paymentRepository = paymentRepository;
        this.detailPaymentRepository = detailPaymentRepository;
        this.bookingRepository = bookingRepository;
    }

    public Payment createPayment(Payment payment, Double amountCar, Double totalAmount) {
        payment.setPaymentStatus("pending");
        payment.setTransactionDate(new Date());
        Payment saved = paymentRepository.save(payment);
        DetailPayment dp = new DetailPayment();
        dp.setBookingId(payment.getBookingId());
        dp.setPaymentId(saved.getId());
        dp.setAmountCar(amountCar);
        dp.setTotalAmount(totalAmount);
        detailPaymentRepository.save(dp);
        return saved;
    }

    public Payment updatePayment(String id, Payment update) {
        return paymentRepository.findById(id).map(p -> {
            p.setMethod(update.getMethod());
            p.setPaymentStatus(update.getPaymentStatus());
            Payment saved = paymentRepository.save(p);
            if ("completed".equals(saved.getPaymentStatus())) {
                bookingRepository.findById(saved.getBookingId()).ifPresent(b -> {
                    b.setStatus("completed");
                    bookingRepository.save(b);
                });
            }
            return saved;
        }).orElse(null);
    }

    public List<DetailPayment> getAllDetails() {
        return detailPaymentRepository.findAll();
    }
}
