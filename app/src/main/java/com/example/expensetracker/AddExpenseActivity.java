package com.example.expensetracker;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

public class AddExpenseActivity extends Activity {
    private TextView mAddExpenseHeading;
    private TextView mAddExpenseButton;
    private EditText mAddAmountInput;
    private EditText mTitleInput;
    private EditText mDescriptionInput;
    private EditText mAddCategoryInput;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        // Initialize views
        mAddExpenseHeading = findViewById(R.id.add_expenses_heading);
        mTitleInput = findViewById(R.id.add_title_input);
        mDescriptionInput = findViewById(R.id.add_description_input);
        mAddAmountInput = findViewById(R.id.add_amount_input);
        mAddExpenseButton = findViewById(R.id.add_expense_button);
        mAddCategoryInput = findViewById(R.id.add_category_input);

        // Create an instance of your DatabaseHelper
        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        // Get a reference to the database
        SQLiteDatabase database = databaseHelper.getReadableDatabase();


        // Close the database connection
        database.close();

        // Handle add expense button click
        mAddExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String category = mAddCategoryInput.getText().toString();
                String title = mTitleInput.getText().toString();
                String description = mDescriptionInput.getText().toString();
                String amountText = mAddAmountInput.getText().toString().trim();

                // Perform validation
                if (category.isEmpty()) {
                    Toast.makeText(AddExpenseActivity.this, "Please enter a category", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (title.isEmpty()) {
                    mTitleInput.setError("Please enter a title");
                    return;
                }

                if (description.isEmpty()) {
                    mDescriptionInput.setError("Please enter a description");
                    return;
                }

                if (amountText.isEmpty()) {
                    mAddAmountInput.setError("Please enter an amount");
                    return;
                }

                float amount;
                try {
                    amount = Float.parseFloat(amountText);
                } catch (NumberFormatException e) {
                    mAddAmountInput.setError("Invalid amount format");
                    return;
                }

                // Calculate the budget
                float budget = calculateBudget(database);

                // Check if the expense can be added
                if (amount > budget) {
                    Toast.makeText(AddExpenseActivity.this, "Not enough budget for this expense", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // Insert the expense into the database
                insertTransaction("transactions",title, description, category,amount,  "expenses", database);
            }
        });
    }

    private float calculateBudget(SQLiteDatabase database) {
        // Create an instance of your DatabaseHelper
        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        // Get a reference to the database
        database = databaseHelper.getReadableDatabase();

        // Query to get the total incomes
        String incomeQuery = "SELECT SUM(amount) AS total_income FROM transactions WHERE type='incomes'";
        Cursor incomeCursor = database.rawQuery(incomeQuery, null);
        float totalIncome = 0;

        if (incomeCursor.moveToFirst()) {
            int totalIncomeIndex = incomeCursor.getColumnIndex("total_income");
            if (totalIncomeIndex >= 0) {
                totalIncome = incomeCursor.getFloat(totalIncomeIndex);
            }
        }

        // Query to get the total expenses
        String expenseQuery = "SELECT SUM(amount) AS total_expense FROM transactions WHERE type='expenses'";
        Cursor expenseCursor = database.rawQuery(expenseQuery, null);
        float totalExpense = 0;

        if (expenseCursor.moveToFirst()) {
            int totalExpenseIndex = expenseCursor.getColumnIndex("total_expense");
            if (totalExpenseIndex >= 0) {
                totalExpense = expenseCursor.getFloat(totalExpenseIndex);
            }
        }

        // Close the cursors
        incomeCursor.close();
        expenseCursor.close();

        // Calculate the budget
        return totalIncome - totalExpense;
    }

    public void insertTransaction(String table, String title, String description, String category, Float amount, String type, SQLiteDatabase database) {
        // Create an instance of your DatabaseHelper
        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        // Get a reference to the database
        database = databaseHelper.getWritableDatabase();

        // Create a ContentValues object to hold the values you want to insert
        ContentValues values = new ContentValues();
        values.put("category", category);
        values.put("title", title);
        values.put("description", description);
        values.put("amount", amount);
        values.put("created_at", System.currentTimeMillis());
        values.put("type", type);
        // Insert the values into the table
        long rowId = database.insert(table, null, values);

        if (rowId != -1) {
            // Insertion successful
            // rowId contains the ID of the newly inserted row
            if (type.equals("expenses")) {
                Toast.makeText(this, "Expense added successfully!", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Income added successfully!", Toast.LENGTH_SHORT).show();

            }
            Intent i = new Intent(AddExpenseActivity.this, MainActivity.class);
            startActivity(i);
            System.out.println("Successfully inserted expense");
        } else {
            // Insertion failed
            System.out.println("Failed to insert expense");
        }

        database.close();
    }
}