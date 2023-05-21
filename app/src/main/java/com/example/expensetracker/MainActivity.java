package com.example.expensetracker;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.util.Log;
import android.view.View;

import android.widget.Button;
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
    private ExpenseAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        mHeadingTextView = (TextView) findViewById(R.id.budget_value);
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
        List<Expense> expenses = getExpenses(database);


        // Call the getSumOfAmount method to retrieve the sum
        float sumOfExpenses = getSumOfAmount("expenses", database);
        float sumOfIncomes = getSumOfAmount("incomes", database);

        // Close the database connection
        database.close();
        // Initialize the RecyclerView and its adapter
        mAdapter = new ExpenseAdapter(expenses);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        // Set the sum value to the TextView
//        mHeadingTextView.setText(Float.toString(sumOfIncomes - sumOfExpenses) + "€");
    }

    private List<Expense> getExpenses(SQLiteDatabase database) {
        List<Expense> expenses = new ArrayList<>();

        // Execute the query to fetch expenses from the "expenses" table
        String query = "SELECT title, amount FROM expenses ORDER BY created_at DESC";
        Cursor cursor = database.rawQuery(query, null);

        // Iterate over the cursor to retrieve expense data
        while (cursor.moveToNext()) {
            String title = cursor.getString(cursor.getColumnIndex("title"));
            float amount = cursor.getFloat(cursor.getColumnIndex("amount"));

            // Create an Expense object with the retrieved data and add it to the list
            Expense expense = new Expense(title, amount);
            expenses.add(expense);
        }

        // Close the cursor
        cursor.close();

        return expenses;
    }

    public float getSumOfAmount(String tableName, SQLiteDatabase database) {
        // Define the column to sum
        String columnToSum = "amount";

        // Execute the query to calculate the sum
        String query = "SELECT SUM(" + columnToSum + ") FROM " + tableName;
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

    public void insertCategory(String name) {

        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name);

        long rowId = db.insert("categories", null, values);

        if (rowId != -1) {
            // Insertion successful
            // rowId contains the ID of the newly inserted row
            Log.d("DatabaseHelper", "Category inserted successfully!");
        } else {
            // Insertion failed
            Log.d("DatabaseHelper", "Failed to insert category!");
        }

        db.close();
    }

}