package com.example.payments.service;

import com.example.payments.dto.Paymentdto;
import com.example.payments.model.Item;
import com.example.payments.model.Payment;
import com.example.payments.repository.PaymentRepository;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.text.Document;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;
    public void addItemToPayment(String paymentId, String itemName, double pricePerDay, int quantity) {
        Optional<Payment> optionalPayment = paymentRepository.findById(paymentId);
        if (optionalPayment.isPresent()) {
            Payment payment = optionalPayment.get();
            Item newItem = Item.builder()
                    .itemName(itemName)
                    .pricePerDay(pricePerDay)
                    .quantity(quantity)
                    .totalAmount(pricePerDay * quantity) // Calculate total amount
                    .build();
            payment.addItem(newItem);
            paymentRepository.save(payment); // Save updated payment with new item
        } else {
            throw new RuntimeException("Payment not found");
        }
    }

    public Payment initiatePayment(Paymentdto payment) {
        Payment p=Payment.builder()
                .amount(payment.getAmount())
                .vendorname(payment.getVendorname())
                .vendorAddress(payment.getVendorAddress())
                .username(payment.getUsername())
                .ponumber(payment.getPonumber())
                .invoicenumber(payment.getInvoicenumber())
                .targetBankAccount(payment.getTargetBankAccount())
                .tds(payment.getTds())
                .sourceBankAccount(payment.getSourceBankAccount())
                .status(payment.getStatus())
                .paymentdate(payment.getPaymentdate())
                .build();
        return paymentRepository.save(p);
    }
    // Method to initiate a list of payments
    public List<Payment> initiatePayments(List<Paymentdto> payments) {
        List<Payment> paymentList = payments.stream().map(payment -> Payment.builder()
                .amount(payment.getAmount())
                .vendorname(payment.getVendorname())
                .vendorAddress(payment.getVendorAddress())
                .username(payment.getUsername())
                .ponumber(payment.getPonumber())
                .invoicenumber(payment.getInvoicenumber())
                .targetBankAccount(payment.getTargetBankAccount())
                .tds(payment.getTds())
                .sourceBankAccount(payment.getSourceBankAccount())
                .status(payment.getStatus())
                .paymentdate(payment.getPaymentdate())
                .build()).collect(Collectors.toList());

        return paymentRepository.saveAll(paymentList);
    }
    // 1. Find pending payments
    public List<Payment> findPendingPayments() {
        return paymentRepository.findByStatus("PENDING");
    }

    // 2. Find total amount
    public Double getTotalAmount() {
        return paymentRepository.sumAllAmounts();
    }

    // 3. Find amount by invoice number
    public Double getAmountByInvoiceNumber(String invoiceNumber) {
        Payment payment = paymentRepository.findByInvoicenumber(invoiceNumber);
        return payment != null ? payment.getAmount() : 0.0;
    }

    // 4. Find complete and pending payments by payment date
    public Map<String, List<Payment>> getPaymentsByStatusAndDate(String paymentDate) {
        Map<String, List<Payment>> paymentsByStatus = new HashMap<>();
        paymentsByStatus.put("completed", paymentRepository.findByPaymentdateAndStatus(paymentDate, "PAID"));
        paymentsByStatus.put("pending", paymentRepository.findByPaymentdateAndStatus(paymentDate, "PENDING"));
        return paymentsByStatus;
    }

    // 5. Edit payment
    public Payment editPayment(String id, Paymentdto paymentdto) {
        Optional<Payment> optionalPayment = paymentRepository.findById(id);
        if (optionalPayment.isPresent()) {
            Payment payment = optionalPayment.get();
            payment.setAmount(paymentdto.getAmount());
            payment.setVendorname(paymentdto.getVendorname());
            payment.setVendorAddress(paymentdto.getVendorAddress());
            payment.setUsername(paymentdto.getUsername());
            payment.setPonumber(paymentdto.getPonumber());
            payment.setInvoicenumber(paymentdto.getInvoicenumber());
            payment.setTargetBankAccount(paymentdto.getTargetBankAccount());
            payment.setSourceBankAccount(paymentdto.getSourceBankAccount());
            payment.setTds(paymentdto.getTds());
            payment.setStatus(paymentdto.getStatus());
            payment.setPaymentdate(paymentdto.getPaymentdate());
            return paymentRepository.save(payment);
        }
        throw new RuntimeException("Payment not found");
    }

    // 6. Delete payment
    public void deletePayment(String id) {
        paymentRepository.deleteById(id);
    }
    //7.get items by payment id
    public List<Item> getItemsByPaymentId(String paymentId) {
        Optional<Payment> optionalPayment = paymentRepository.findById(paymentId);
        if (optionalPayment.isPresent()) {
            return optionalPayment.get().getItems(); // Assuming getItems() returns the list of items
        } else {
            throw new RuntimeException("Payment not found");
        }
    }
    //get items by invoice no
    public List<Item> getItemsByInvoiceNumber(String invoiceNumber) {
        // Fetch the payment based on the invoice number
        Payment payment = paymentRepository.findByInvoicenumber(invoiceNumber);

        if (payment == null) {
            throw new RuntimeException("No payment found for the given invoice number");
        }

        return payment.getItems(); // Return the list of items associated with the payment
    }

}

