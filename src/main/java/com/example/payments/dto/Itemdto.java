package com.example.payments.dto;


import lombok.Data;

@Data
public class Itemdto {
    private String itemName;   // Name of the item
    private int quantity;      // Number of days
    private double pricePerDay; // Price per day
}
