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
    private Button mAddExpenseButton;
    private EditText mAddAmountInput;
    private EditText mTitleInput;
    private EditText mDescriptionInput;
    private Spinner mCategorySpinner;
    private Map<String, Integer> categories;

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
        mCategorySpinner = findViewById(R.id.category_spinner);

        // Create an instance of your DatabaseHelper
        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        // Get a reference to the database
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        // Set up the spinner
        categories = getCategories(database);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories.keySet().toArray(new String[0])
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategorySpinner.setAdapter(adapter);

        // Close the database connection
        database.close();

        // Handle add expense button click
        mAddExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String category = mCategorySpinner.getSelectedItem().toString();
                int categoryId = categories.get(category);
                String title = mTitleInput.getText().toString();
                String description = mDescriptionInput.getText().toString();
                Float amount = Float.valueOf(mAddAmountInput.getText().toString());

                // Insert the expense into the database
                insertTransaction("expenses", categoryId, title, description, amount, database);
            }
        });
    }

    private Map<String, Integer> getCategories(SQLiteDatabase database) {
        Map<String, Integer> categories = new HashMap<>();

        // Execute the query to retrieve categories from the database
        String query = "SELECT id, name FROM categories";
        Cursor cursor = database.rawQuery(query, null);

        // Iterate over the cursor and add categories to the map
        if (cursor.moveToFirst()) {
            do {
                int categoryIdIndex = cursor.getColumnIndex("id");
                int categoryIndex = cursor.getColumnIndex("name");

                if (categoryIdIndex >= 0 && categoryIndex >= 0) {
                    int categoryId = cursor.getInt(categoryIdIndex);
                    String category = cursor.getString(categoryIndex);
                    categories.put(category, categoryId);
                }
            } while (cursor.moveToNext());
        }

        // Close the cursor
        cursor.close();

        return categories;
    }

    public void insertTransaction(String table, int categoryId, String title, String description, Float amount, SQLiteDatabase database) {
        // Create an instance of your DatabaseHelper
        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        // Get a reference to the database
        database = databaseHelper.getWritableDatabase();

        // Create a ContentValues object to hold the values you want to insert
        ContentValues values = new ContentValues();
        values.put("category_id", categoryId);
        values.put("title", title);
        values.put("description", description);
        values.put("amount", amount);
        values.put("created_at", System.currentTimeMillis());

        // Insert the values into the table
        long rowId = database.insert(table, null, values);

        if (rowId != -1) {
            // Insertion successful
            // rowId contains the ID of the newly inserted row
            Toast.makeText(this, "Expense added successfully!", Toast.LENGTH_SHORT).show();
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