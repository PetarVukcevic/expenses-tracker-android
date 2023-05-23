package com.example.expensetracker;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class TransactionDetailsActivity extends Activity {
    private TextView mTitleTextView;
    private TextView mAmountTextView;
    private TextView mCategoryTextView;
    private TextView mDateTextView;
    private TextView mDescriptionTextView;
    private TextView mBigTitleTextView;
    private Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details);
        mTitleTextView = findViewById(R.id.titleTextView);
        mAmountTextView = findViewById(R.id.amountTextView);
        mCategoryTextView = findViewById(R.id.categoryTextView);
        mDateTextView = findViewById(R.id.dateTextView);
        mDescriptionTextView = findViewById(R.id.descriptionTextView);
        mBigTitleTextView = findViewById(R.id.bigTitleTextView);
        deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteTransaction();
            }
        });

        // Get the transaction object from the intent
        Transaction transaction = (Transaction) getIntent().getSerializableExtra("transaction");
        if (transaction != null) {
            // Set the transaction details in the TextViews
            mTitleTextView.setText(transaction.getTitle());
            mAmountTextView.setText(String.valueOf(transaction.getAmount()) + "€");
            mCategoryTextView.setText(transaction.getCategory());
            // Display the formatted date
            mDateTextView.setText((transaction.getDate()));
            mDescriptionTextView.setText(transaction.getDescription());

            // Set the big title based on the type of transaction
            if (transaction.getType().equals("incomes")) {
                mBigTitleTextView.setText("Income");
//                mBigTitleTextView.setTextColor(Color.GREEN);
            } else if (transaction.getType().equals("expenses")) {
                mBigTitleTextView.setText("Expense");
//                mBigTitleTextView.setTextColor(Color.RED);

            }
        }
    }

    private void deleteTransaction() {
        // Get the transaction object from the intent
        Transaction transaction = (Transaction) getIntent().getSerializableExtra("transaction");

        if (transaction != null) {
            // Initialize the DatabaseHelper
            DatabaseHelper databaseHelper = new DatabaseHelper(this);

            // Get a writable database
            SQLiteDatabase db = databaseHelper.getWritableDatabase();

            // Define the WHERE clause for the deletion query
            String whereClause = "id = ?";
            String[] whereArgs = {String.valueOf(transaction.getId())};

            // Perform the deletion operation
            int deletedRows = db.delete("transactions", whereClause, whereArgs);

            // Close the database connection
            db.close();

            // Check if the transaction was successfully deleted
            if (deletedRows > 0) {
                // Transaction deleted successfully
                Toast.makeText(TransactionDetailsActivity.this, "Transaction deleted successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(TransactionDetailsActivity.this, MainActivity.class);
                startActivity(intent);
                // Perform any additional actions or show a success message
            } else {
                // Failed to delete the transaction
                Toast.makeText(TransactionDetailsActivity.this, "Failed to delete the transaction", Toast.LENGTH_SHORT).show();

                // Perform any error handling or show an error message
            }

            // Finish the current activity to return to the previous screen
            finish();
        }
    }


}
