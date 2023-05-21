package com.example.expensetracker;

public class Expense {
    private String title;
    private float amount;

    public Expense(String title, float amount) {
        this.title = title;
        this.amount = amount;
    }

    public String getTitle() {
        return title;
    }

    public float getAmount() {
        return amount;
    }
}
