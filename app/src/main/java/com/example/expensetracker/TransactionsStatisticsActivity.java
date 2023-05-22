package com.example.expensetracker;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TransactionsStatisticsActivity extends Activity {
    private RecyclerView mRecyclerView;
    private DatabaseHelper databaseHelper;
    private TransactionsStatisticsTracker mAdapter;
    private Spinner timeFilterSpinner;

    private static final int TIME_FILTER_ALL = 0;
    private static final int TIME_FILTER_TODAY = 1;
    private static final int TIME_FILTER_THIS_WEEK = 2;
    private static final int TIME_FILTER_THIS_MONTH = 3;
    private static final int TIME_FILTER_THIS_YEAR = 4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transactions_stats_activity);
        mRecyclerView = findViewById(R.id.recycler);
        timeFilterSpinner = findViewById(R.id.timeFilterSpinner);

        // Create an instance of your DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Get a reference to the database
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        // Populate the time filter options in the spinner
        populateTimeFilterSpinner();

        // Fetch expenses data from the database
        List<Transaction> transactions = getTransactions(database, null, null);

        // Close the database connection
        database.close();

        // Initialize the RecyclerView and its adapter
        mAdapter = new TransactionsStatisticsTracker(transactions);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
    }

    private void populateTimeFilterSpinner() {
        // Define the time filter options
        String[] timeFilters = {"All Time", "Today", "This Week", "This Month", "This Year"};

        // Create an ArrayAdapter to populate the spinner with options
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, timeFilters);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Set the adapter on the spinner
        timeFilterSpinner.setAdapter(adapter);

        // Set the default selection to "All Time"
        timeFilterSpinner.setSelection(TIME_FILTER_ALL);

        // Set the spinner's OnItemSelectedListener
        timeFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedFilter = timeFilters[position];
                updateTransactionsBasedOnTimeFilter(selectedFilter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle when nothing is selected
            }
        });
    }

    private void updateTransactionsBasedOnTimeFilter(String timeFilter) {
        // Get a reference to the database
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        // Fetch expenses data from the database based on the selected time filter
        List<Transaction> transactions;
        if (timeFilter.equals("All Time")) {
            transactions = getTransactions(database, null, null); // Fetch all transactions
        } else {
            // Get the start and end timestamps based on the selected time filter
            long startTime = 0;
            long endTime = System.currentTimeMillis();
            if (timeFilter.equals("Today")) {
                // Set start time to the beginning of today
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                startTime = cal.getTimeInMillis();
            }
            // ... Other time filter cases ...

            transactions = getTransactions(database, startTime, endTime);
        }

        // Close the database connection
        database.close();

        // Update the RecyclerView adapter with the new transactions
        mAdapter.setTransactions(transactions);
        mAdapter.notifyDataSetChanged();
    }

    private List<Transaction> getTransactions(SQLiteDatabase database, Long startTime, Long endTime) {
        List<Transaction> transactions = new ArrayList<>();

        // Build the query to fetch expenses from the "transactions" table
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM transactions WHERE type='incomes'");
        if (startTime != null && endTime != null) {
            queryBuilder.append(" AND created_at >= ").append(startTime).append(" AND created_at <= ").append(endTime);
        }
        queryBuilder.append(" ORDER BY created_at DESC");
        String query = queryBuilder.toString();

        // Execute the query to fetch expenses from the "transactions" table
        Cursor cursor = database.rawQuery(query, null);

        try {
            int titleIndex = cursor.getColumnIndexOrThrow("title");
            int amountIndex = cursor.getColumnIndexOrThrow("amount");
            int typeIndex = cursor.getColumnIndexOrThrow("type");
            int categoryIndex = cursor.getColumnIndexOrThrow("category");
            int dateIndex = cursor.getColumnIndexOrThrow("created_at");
            int descriptionIndex = cursor.getColumnIndexOrThrow("description");

            // Iterate over the cursor to retrieve expense data
            while (cursor.moveToNext()) {
                String title = cursor.getString(titleIndex);
                float amount = cursor.getFloat(amountIndex);
                String type = cursor.getString(typeIndex);
                String category = cursor.getString(categoryIndex);
                String date = cursor.getString(dateIndex);
                String description = cursor.getString(descriptionIndex);

                // Convert the date string to a timestamp
                long timestamp = Long.parseLong(date);

                // Format the timestamp as "dd/mm/yyyy HH:mm" date string
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                String formattedDate = dateFormat.format(timestamp);

                // Create a Transaction object with the retrieved data and add it to the list
                Transaction transaction = new Transaction(title, amount, type, category, formattedDate);
                transaction.setDescription(description);
                transactions.add(transaction);
            }
        } catch (IllegalArgumentException e) {
            Log.e("MainActivity", "Error retrieving expenses: " + e.getMessage());
        } finally {
            // Close the cursor
            cursor.close();
        }

        return transactions;
    }

    private List<Transaction> getTransactions(SQLiteDatabase database) {
        // Call the getTransactions() method with null values for startTime and endTime
        return getTransactions(database, null, null);
    }
}
