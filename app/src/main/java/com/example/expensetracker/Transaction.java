package com.example.expensetracker;

import android.text.format.DateUtils;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

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

    public String convertRelativeDate(String relativeDate) {
        // Parse the relative date and extract the numeric value and unit
        String[] parts = relativeDate.split(" ");
        int value = Integer.parseInt(parts[0]);
        String unit = parts[1];

        // Calculate the actual date based on the relative time
        LocalDate currentDate = LocalDate.now();
        LocalDate convertedDate;

        switch (unit) {
            case "day":
            case "days":
                convertedDate = currentDate.minusDays(value);
                break;
            case "week":
            case "weeks":
                convertedDate = currentDate.minusWeeks(value);
                break;
            case "month":
            case "months":
                convertedDate = currentDate.minusMonths(value);
                break;
            case "year":
            case "years":
                convertedDate = currentDate.minusYears(value);
                break;
            default:
                System.out.println("Invalid unit: " + unit);
                return "";
        }

        // Format the converted date as "dd/mm/yyyy"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return convertedDate.format(formatter);
    }
}