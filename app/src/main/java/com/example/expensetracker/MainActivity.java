package com.example.expensetracker;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;

import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

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
        mHeadingTextView = (TextView) findViewById(R.id.budgetTextView);
        mAddIncomeButton = (TextView) findViewById(R.id.addIncome);
        mAddExpenseButton = (TextView) findViewById(R.id.addExpense);
        mRecyclerView = findViewById(R.id.recycler);
        mHeadingTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BudgetChangesActivity.class);
                startActivity(intent);
            }
        });

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


        // Create an instance of your DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Get a reference to the database
        SQLiteDatabase database = databaseHelper.getReadableDatabase();


        // Fetch expenses data from the database
        List<Transaction> transactions = getTransactions(database);


        // Call the getSumOfAmount method to retrieve the sum
        float sumOfExpenses = getSumOfAmount("transactions", "expenses",database);
        float sumOfIncomes = getSumOfAmount("transactions", "incomes",database);

        setUpGraph(database);

        // Close the database connection
        database.close();
        // Initialize the RecyclerView and its adapter
        mAdapter = new TransactionTracker(transactions);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);

        // Set the sum value to the TextView
        if (sumOfIncomes == 0) {
            // No incomes, display a message
            mHeadingTextView.setText("No budget, add new income");
        } else {
            // Incomes exist, calculate and display the budget
            float budget = sumOfIncomes - sumOfExpenses;
            mHeadingTextView.setText("Budget: " + Float.toString(sumOfIncomes - sumOfExpenses) + "€");
        }

    }

    private void setUpGraph(SQLiteDatabase database) {
        List<PieEntry> pieEntryList = new ArrayList<>();
        List<Integer> colorsList = new ArrayList<>();
        float income = getSumOfAmount("transactions", "incomes", database);
        float expense = getSumOfAmount("transactions", "expenses", database);

        if (income != 0) {
            float incomePercentage = (income / (income + expense)) * 100;
            pieEntryList.add(new PieEntry(incomePercentage, "Incomes(%)"));
            colorsList.add(getResources().getColor(R.color.green));
        }
        if (expense != 0) {
            float expensePercentage = (expense / (income + expense)) * 100;
            pieEntryList.add(new PieEntry(expensePercentage, "Expenses(%)"));
            colorsList.add(getResources().getColor(R.color.red));
        }
        PieDataSet pieDataSet = new PieDataSet(pieEntryList, "");
        pieDataSet.setColors(colorsList);
        PieData pieData = new PieData(pieDataSet);

        PieChart pieChart = findViewById(R.id.pieChart);
        pieChart.setData(pieData);
        pieChart.invalidate();

        // Increase text size for entry labels
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(14f); // Adjust the size as needed
        pieChart.setEntryLabelTypeface(Typeface.DEFAULT_BOLD); // Set the typeface to bold
        pieData.setValueTextSize(20f);
        pieData.setValueTypeface(Typeface.DEFAULT_BOLD);
        pieChart.getDescription().setEnabled(false);

        // Set onClick listener for the pie chart
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, Highlight highlight) {
                int index = (int) highlight.getX();

                if (index >= 0 && index < pieEntryList.size()) {
                    PieEntry selectedEntry = pieEntryList.get(index);
                    String label = selectedEntry.getLabel();

                    if (label.equals("Incomes(%)")) {
                        Intent intent = new Intent(MainActivity.this, IncomesStatisticsActivity.class);
                        startActivity(intent);
                    } else if (label.equals("Expenses(%)")) {
                        Intent intent = new Intent(MainActivity.this, ExpensesStatisticsActivity.class);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onNothingSelected() {
                // Do nothing
            }
        });


        Legend legend = pieChart.getLegend();
        legend.setTextSize(16f); // Set the desired text size for the legend
    }


    // For each transaction
// For each transaction
    private List<Transaction> getTransactions(SQLiteDatabase database) {
        List<Transaction> transactions = new ArrayList<>();

        // Execute the query to fetch expenses from the "transactions" table
        String query = "SELECT * FROM transactions ORDER BY created_at DESC";
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

                // Format the timestamp as a relative time string
                CharSequence relativeTime = DateUtils.getRelativeTimeSpanString(timestamp, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS);

                // Create an Expense object with the retrieved data and add it to the list
                Transaction transaction = new Transaction(title, amount, type, category, relativeTime.toString());
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