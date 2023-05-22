package com.example.expensetracker;

import java.io.Serializable;

public class Transaction implements Serializable {
    private String title;
    private float amount;
    private String category;
    private String type;
    private String date;
    private String description;

    public Transaction(String title, float amount, String type, String category, String date) {
        this.title = title;
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.date = date;
    }

    public Transaction(String title, float amount, String category, String type, String date, String description) {
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.type = type;
        this.date = date;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public float getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }
}
