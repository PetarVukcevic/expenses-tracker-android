package com.example.expensetracker;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.expensetracker.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
    private TextView mHeadingTextView;
    private Button mAddExpenseButton;
    private Button mAddIncomeButton;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHeadingTextView = (TextView) findViewById(R.id.budget_value);
        mAddIncomeButton = (Button) findViewById(R.id.add_income_button);
        mAddExpenseButton = (Button) findViewById(R.id.add_expense_button);

        // Create an instance of your DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Get a reference to the database
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        // Call the insert method to insert data into the table
//        insertTransaction("incomes", "Salary", "This is my salary", 200.5F, database);

        // Call the getSumOfAmount method to retrieve the sum
        float sumOfExpenses = getSumOfAmount("expenses", database);
        float sumOfIncomes = getSumOfAmount("incomes", database);

        // Close the database connection
        database.close();

        // Set the sum value to the TextView
        mHeadingTextView.setText(Float.toString(sumOfIncomes - sumOfExpenses) + "€");
    }


    public void insertTransaction(String table, String title, String description, Float amount, SQLiteDatabase database) {
        // Create an instance of your DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

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


}