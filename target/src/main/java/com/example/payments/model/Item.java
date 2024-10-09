package com.example.payments.model;




import lombok.Builder;
import lombok.Data;

    @Data
    @Builder
    public class Item {
        private String itemName;
        private int quantity;
        private double pricePerDay;
        private double totalAmount;
    }


