package com.example.expensetracker;

public class Expense {
    private String title;
    private float amount;
    private String category;
    private String type;
    private String date;

    public Expense(String title, float amount, String type, String category, String date) {
        this.title = title;
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.date = date;
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
