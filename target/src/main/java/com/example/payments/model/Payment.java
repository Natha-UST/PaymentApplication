package com.example.payments.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "payments")
@Builder
public class Payment {
    @Id
    private String id;
    private Double amount;
    private String vendorname;
    private String vendorAddress;
    private String username;
    private String ponumber;
    private String invoicenumber;
    private String targetBankAccount;
    private String sourceBankAccount;
    private int tds;
    private String status;
    private String paymentdate;
   private String IfscCode;
   private String branch;
    private List<Item> items = new ArrayList<>();
    public void addItem(Item item) {
        items.add(item);
        // Optionally update the total amount if needed
    }

    // Method to calculate total amount based on items
    public double calculateTotalAmount() {
        return items.stream()
                .mapToDouble(item -> item.getQuantity() * item.getPricePerDay())
                .sum();
    }
}

