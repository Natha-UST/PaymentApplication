package com.example.payments.repository;

import com.example.payments.model.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface PaymentRepository extends MongoRepository<Payment, String> {
    // 1. Find by status
    List<Payment> findByStatus(String status);

    // 2. Sum all amounts
    @Query(value = "{}", fields = "{ 'amount' : 1 }")
    List<Payment> findAllPayments();

    default Double sumAllAmounts() {
        return findAllPayments()
                .stream()
                .mapToDouble(Payment::getAmount)
                .sum();
    }// 3. Find by invoice number
    Payment findByInvoicenumber(String invoiceNumber);

    // 4. Find by payment date and status
    List<Payment> findByPaymentdateAndStatus(String paymentDate, String status);
}
