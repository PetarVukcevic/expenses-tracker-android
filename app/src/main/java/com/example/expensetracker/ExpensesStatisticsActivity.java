package com.example.expensetracker;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ExpensesStatisticsActivity extends Activity {

    private RecyclerView mRecyclerView;
    private DatabaseHelper databaseHelper;
    private TransactionsStatisticsTracker mAdapter;
    private Spinner timeFilterSpinner;
    private TextView titleTextView;
    private BarChart barChart;

    private static final int TIME_FILTER_ALL = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expenses_stats_activity);
        mRecyclerView = findViewById(R.id.recycler);
        timeFilterSpinner = findViewById(R.id.timeFilterSpinner);
        titleTextView = findViewById(R.id.titleTextView);

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

        barChart = findViewById(R.id.barChart);
        setupBarChart();
        updateTransactionsBasedOnTimeFilter("All Time");
    }

    private void setupBarChart() {
        // Configure the bar chart
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.getDescription().setEnabled(false);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);

        // Format the x-axis labels as categories
        XAxis xAxis = barChart.getXAxis();
        xAxis.setTextSize(14f);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        // Format the y-axis labels as currency values
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return value + "€";
            }
        });
        leftAxis.setTextSize(14f); // Set the desired text size for the values
        leftAxis.setDrawGridLines(true);
        leftAxis.setSpaceTop(30f);
        barChart.getAxisRight().setEnabled(false);

        // Remove the legend
        Legend legend = barChart.getLegend();
        legend.setEnabled(false);


        // Disable interactions with the chart
        barChart.setTouchEnabled(false);
        barChart.setDragEnabled(false);
        barChart.setScaleEnabled(false);
        // Adjust the bottom padding
        barChart.setExtraBottomOffset(10f); // Set the desired bottom padding value
        barChart.setFitBars(true);
    }

    private void updateBarChart(List<Transaction> transactions) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        // Prepare the data for the bar chart
        HashMap<String, Float> categoryAmountMap = new HashMap<>();

        for (Transaction transaction : transactions) {
            String category = transaction.getCategory();
            float amount = transaction.getAmount();

            if (categoryAmountMap.containsKey(category)) {
                // Category already exists, add the amount to the existing value
                float existingAmount = categoryAmountMap.get(category);
                categoryAmountMap.put(category, existingAmount + amount);
            } else {
                // New category, add it to the map
                categoryAmountMap.put(category, amount);
            }
        }

        int index = 0;
        for (Map.Entry<String, Float> entry : categoryAmountMap.entrySet()) {
            String category = entry.getKey();
            float amount = entry.getValue();

            entries.add(new BarEntry(index, amount));
            labels.add(category);

            index++;
        }

        // Create a bar data set
        BarDataSet dataSet = new BarDataSet(entries, "Expenses");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(14f); // Set the desired text size for the values on top of the bars

        // Create the bar data
        BarData barData = new BarData(dataSet);

        // Set the data to the chart and refresh
        barChart.setData(barData);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        barChart.notifyDataSetChanged();
        barChart.invalidate();
    }


    private float calculateExpenseSum(List<Transaction> transactions) {
        float sum = 0;
        for (Transaction transaction : transactions) {
            sum += transaction.getAmount();
        }
        return sum;
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
            else if (timeFilter.equals("This Week")) {
                // Set start time to the beginning of the current week
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                startTime = cal.getTimeInMillis();
            } else if (timeFilter.equals("This Month")) {
                // Set start time to the beginning of the current month
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                startTime = cal.getTimeInMillis();
            } else if (timeFilter.equals("This Year")) {
                // Set start time to the beginning of the current year
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.MONTH, Calendar.JANUARY);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                startTime = cal.getTimeInMillis();
            }

            transactions = getTransactions(database, startTime, endTime);


        }

        // Close the database connection
        database.close();
        float sum = 0;

        if (transactions != null) {
            // Calculate the sum of income transactions
            sum = calculateExpenseSum(transactions);
        }


        // Update the titleTextView based on the selected time filter and the sum of income transactions
        String title = "Expenses: " + sum + "€";
        titleTextView.setText(title);

        // Update the RecyclerView adapter with the new transactions
        mAdapter.setTransactions(transactions);
        mAdapter.notifyDataSetChanged();

        // Update the bar chart with the new transactions
        updateBarChart(transactions);
    }

    private List<Transaction> getTransactions(SQLiteDatabase database, Long startTime, Long endTime) {
        List<Transaction> transactions = new ArrayList<>();

        // Build the query to fetch expenses from the "transactions" table
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM transactions WHERE type='expenses'");
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
