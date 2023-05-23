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

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BudgetChangesActivity extends Activity {
    private TransactionTracker mAdapter;

    private RecyclerView recyclerView;
    private LineChart lineChart;
    private TextView budgetTextView;
    private DatabaseHelper databaseHelper;
    private TextView mAddExpenseButton;
    private TextView mAddIncomeButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_changes);
        lineChart = findViewById(R.id.lineChart);
        budgetTextView = findViewById(R.id.budgetTextView);
        mAddIncomeButton = findViewById(R.id.addIncome);
        mAddExpenseButton = findViewById(R.id.addExpense);
        recyclerView = findViewById(R.id.recycler);

        mAddExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BudgetChangesActivity.this, AddExpenseActivity.class);
                startActivity(intent);
            }
        });

        mAddIncomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BudgetChangesActivity.this, AddIncomeActivity.class);
                startActivity(intent);
            }
        });

        // Create an instance of your DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Get a reference to the database
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        // Fetch expenses data from the database
        List<Transaction> transactions = getTransactions(database);

        float sumOfIncomes = getSumOfAmount("transactions", "incomes", database);
        float sumOfExpenses = getSumOfAmount("transactions", "expenses", database);

        mAdapter = new TransactionTracker(transactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);

        if (sumOfIncomes == 0) {
            // No incomes, display a message
            budgetTextView.setText("No budget, add new income");
        } else {
            // Incomes exist, calculate and display the budget
            float budget = sumOfIncomes - sumOfExpenses;
            budgetTextView.setText("Budget: " + Float.toString(sumOfIncomes - sumOfExpenses) + "€");
        }

        setUpLineGraph(database);
    }

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

                // Format the timestamp as "dd/mm/yyyy HH:mm" date string
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                String formattedDate = dateFormat.format(timestamp);

                // Create an Expense object with the retrieved data and add it to the list
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

    private void setUpLineGraph(SQLiteDatabase database) {
        // Fetch the historical income and expense data from the database
        ArrayList<Transaction> transactions = getTransactionData(database);

        // Create an ArrayList to store the entries for the line chart
        ArrayList<Entry> entries = new ArrayList<>();

        // Initialize the initial budget as zero
        float budget = 0f;

        // Add the initial budget as the first entry
        entries.add(new Entry(0, budget));

        // Calculate the budget changes for each transaction and add them as entries
        for (int i = 0; i < transactions.size(); i++) {
            Transaction transaction = transactions.get(i);
            float amount = transaction.getAmount();

            // Calculate the budget change based on the transaction type
            if (transaction.getType().equals("incomes")) {
                budget += amount;
            } else if (transaction.getType().equals("expenses")) {
                budget -= amount;
            }

            // Add the budget change as an entry
            entries.add(new Entry(i + 1, budget));
        }

        // Create a LineDataSet with the entries
        LineDataSet dataSet = new LineDataSet(entries, "Budget Changes");

        // Customize the appearance of the line
        dataSet.setColor(getResources().getColor(R.color.green));
        dataSet.setLineWidth(4f);
        dataSet.setCircleColor(getResources().getColor(R.color.red));
        dataSet.setCircleRadius(6f);
        dataSet.setDrawCircleHole(false);
        dataSet.setValueTextSize(14f);

        // Create a LineData object with the LineDataSet
        LineData lineData = new LineData(dataSet);

        // Set the LineData to the LineChart
        lineChart.setData(lineData);

        // Customize the appearance of the line chart
        lineChart.getDescription().setEnabled(false);

        // Get references to the X and Y axes
        XAxis xAxis = lineChart.getXAxis();
        YAxis yAxis = lineChart.getAxisLeft();

        // Customize the appearance of the X axis
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(14f); // Adjust the text size of x-labels

        // Customize the appearance of the Y axis
        yAxis.setDrawGridLines(true);
        yAxis.setTextSize(14f); // Set the text size of y-labels

        // Customize the appearance of the legend
        Legend legend = lineChart.getLegend();
        legend.setEnabled(false);

        // Invalidate the line chart to refresh its display
        lineChart.invalidate();
    }



    public ArrayList<Transaction> getTransactionData(SQLiteDatabase database) {
        ArrayList<Transaction> transactions = new ArrayList<>();

        // Define the columns to retrieve
        String[] columns = {"title", "amount", "type", "category", "created_at"};

        // Execute the query to retrieve the transaction data
        Cursor cursor = database.query("transactions", columns, null, null, null, null, "created_at ASC");

        // Get the column indices
        int titleIndex = cursor.getColumnIndex("title");
        int amountIndex = cursor.getColumnIndex("amount");
        int typeIndex = cursor.getColumnIndex("type");
        int categoryIndex = cursor.getColumnIndex("category");
        int dateIndex = cursor.getColumnIndex("created_at");

        // Iterate through the cursor and populate the transactions ArrayList
        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(titleIndex);
                float amount = cursor.getFloat(amountIndex);
                String type = cursor.getString(typeIndex);
                String category = cursor.getString(categoryIndex);
                String date = cursor.getString(dateIndex);
                transactions.add(new Transaction(title, amount, type, category, date));
            } while (cursor.moveToNext());
        }

        // Close the cursor
        cursor.close();

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
