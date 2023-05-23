package com.example.expensetracker;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AddIncomeActivity extends Activity {

    private TextView mAddIncomeHeading;
    private TextView mAddIncomeButton;
    private EditText mAddAmountInput;
    private EditText mTitleInput;
    private EditText mDescriptionInput;
    private EditText mAddCategoryInput;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_income);
        mAddIncomeHeading = (TextView) findViewById(R.id.add_incomes_heading);
        mTitleInput = (EditText) findViewById(R.id.add_title_input);
        mDescriptionInput = (EditText) findViewById(R.id.add_description_input);
        mAddAmountInput = (EditText) findViewById(R.id.add_amount_input);
        mAddIncomeButton = (TextView) findViewById(R.id.add_income_button);
        mAddCategoryInput = findViewById(R.id.add_category_input);


        // Create an instance of your DatabaseHelper
        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        // Get a reference to the database
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        mAddIncomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title = mTitleInput.getText().toString();
                String description = mDescriptionInput.getText().toString();
                String amountText = mAddAmountInput.getText().toString().trim();
                String category = mAddCategoryInput.getText().toString();
                // Perform validation
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

                if (category.isEmpty()) {
                    mAddAmountInput.setError("Please enter a category");
                    return;
                }

                float amount;
                try {
                    amount = Float.parseFloat(amountText);
                } catch (NumberFormatException e) {
                    mAddAmountInput.setError("Invalid amount format");
                    return;
                }
                insertTransaction("transactions", mTitleInput.getText().toString(), mDescriptionInput.getText().toString(),mAddCategoryInput.getText().toString() , Float.valueOf(mAddAmountInput.getText().toString()), "incomes",database);
            }
        });
        // Close the database connection
        database.close();
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
            Intent i = new Intent(AddIncomeActivity.this, MainActivity.class);
            startActivity(i);
            System.out.println("Successfully inserted expense");
        } else {
            // Insertion failed
            System.out.println("Failed to insert expense");
        }

        database.close();
    }
}
