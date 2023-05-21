package com.example.expensetracker;

public class Expense {
    private String title;
    private float amount;
    private String type;

    public Expense(String title, float amount, String type) {
        this.title = title;
        this.amount = amount;
        this.type = type;
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
