package com.example.expensetracker;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;

import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private TextView mHeadingTextView;
    private TextView mAddExpenseButton;
    private TextView mAddIncomeButton;

    private DatabaseHelper databaseHelper;
    private RecyclerView mRecyclerView;
    private TransactionTracker mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHeadingTextView = (TextView) findViewById(R.id.budget_value);
        mAddIncomeButton = (TextView) findViewById(R.id.addIncome);
        mAddExpenseButton = (TextView) findViewById(R.id.addExpense);
        mRecyclerView = findViewById(R.id.recycler);

        mAddIncomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, AddIncomeActivity.class);
                startActivity(i);
            }
        });

        mAddExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, AddExpenseActivity.class);
                startActivity(i);
            }
        });


        mAddIncomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, AddIncomeActivity.class);
                startActivity(i);
            }
        });

        // Create an instance of your DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Get a reference to the database
        SQLiteDatabase database = databaseHelper.getReadableDatabase();


        // Fetch expenses data from the database
        List<Transaction> expens = getTransactions(database);


        // Call the getSumOfAmount method to retrieve the sum
        float sumOfExpenses = getSumOfAmount("transactions", "expenses",database);
        float sumOfIncomes = getSumOfAmount("transactions", "incomes",database);

        // Close the database connection
        database.close();
        // Initialize the RecyclerView and its adapter
        mAdapter = new TransactionTracker(expens);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        // Set the sum value to the TextView
        mHeadingTextView.setText(Float.toString(sumOfIncomes - sumOfExpenses) + "€");
    }

    // For each transaction
// For each transaction
    private List<Transaction> getTransactions(SQLiteDatabase database) {
        List<Transaction> expens = new ArrayList<>();

        // Execute the query to fetch expenses from the "transactions" table
        String query = "SELECT * FROM transactions ORDER BY created_at DESC";
        Cursor cursor = database.rawQuery(query, null);

        try {
            int titleIndex = cursor.getColumnIndexOrThrow("title");
            int amountIndex = cursor.getColumnIndexOrThrow("amount");
            int typeIndex = cursor.getColumnIndexOrThrow("type");
            int categoryIndex = cursor.getColumnIndexOrThrow("category");
            int dateIndex = cursor.getColumnIndexOrThrow("created_at");

            // Iterate over the cursor to retrieve expense data
            while (cursor.moveToNext()) {
                String title = cursor.getString(titleIndex);
                float amount = cursor.getFloat(amountIndex);
                String type = cursor.getString(typeIndex);
                String category = cursor.getString(categoryIndex);
                String date = cursor.getString(dateIndex);

                // Convert the date string to a timestamp
                long timestamp = Long.parseLong(date);

                // Format the timestamp as a relative time string
                CharSequence relativeTime = DateUtils.getRelativeTimeSpanString(timestamp, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS);

                // Create an Expense object with the retrieved data and add it to the list
                Transaction transaction = new Transaction(title, amount, type, category, relativeTime.toString());
                expens.add(transaction);
            }
        } catch (IllegalArgumentException e) {
            Log.e("MainActivity", "Error retrieving expenses: " + e.getMessage());
        } finally {
            // Close the cursor
            cursor.close();
        }

        return expens;
    }




    public float getSumOfAmount(String tableName, String type, SQLiteDatabase database) {
        // Define the column to sum
        String columnToSum = "amount";

        // Execute the query to calculate the sum
        String query = "SELECT SUM(" + columnToSum + ") FROM " + tableName + " WHERE type='" + type + "'";
        Cursor cursor = database.rawQuery(query, null);

        // Retrieve the sum value from the cursor
        float totalAmount = 0.0f;
        if (cursor.moveToFirst()) {
            totalAmount = cursor.getFloat(0);
        }

        // Close the cursor
        cursor.close();

        return totalAmount;
    }


}