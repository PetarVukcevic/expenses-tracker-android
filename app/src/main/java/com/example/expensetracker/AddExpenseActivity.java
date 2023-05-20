package com.example.expensetracker;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class AddExpenseActivity extends Activity {
    private TextView mAddExpenseHeading;
    private Button mAddExpenseButton;
    private EditText mAddAmountInput;
    private EditText mTitleInput;
    private EditText mDescriptionInput;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);
        mAddExpenseHeading = (TextView) findViewById(R.id.add_expenses_heading);
        mTitleInput = (EditText) findViewById(R.id.add_title_input);
        mDescriptionInput = (EditText) findViewById(R.id.add_description_input);
        mAddAmountInput = (EditText) findViewById(R.id.add_amount_input);
        mAddExpenseButton = (Button) findViewById(R.id.add_expense_button);
        // Create an instance of your DatabaseHelper
        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        // Get a reference to the database
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        mAddExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertTransaction("expenses", mTitleInput.getText().toString(), mDescriptionInput.getText().toString(), Float.valueOf(mAddAmountInput.getText().toString()), database);
            }
        });
        // Close the database connection
        database.close();
    }

    public void insertTransaction(String table, String title, String description, Float amount, SQLiteDatabase database) {
        // Create an instance of your DatabaseHelper
        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        // Get a reference to the database
        database = databaseHelper.getWritableDatabase();

        // Create a ContentValues object to hold the values you want to insert
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("description", description);
        values.put("amount", amount);
        values.put("created_at", System.currentTimeMillis());

        // Insert the values into the table
        long rowId = database.insert(table, null, values);

        if (rowId != -1) {
            // Insertion successful
            // rowId contains the ID of the newly inserted row
            System.out.println("Uspjesno opet");
        } else {
            // Insertion failed
            System.out.println("Ne valja kurca");

        }
        database.close();
    }
}
