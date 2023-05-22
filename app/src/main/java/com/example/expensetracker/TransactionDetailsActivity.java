package com.example.expensetracker;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

public class TransactionDetailsActivity extends Activity {
    private TextView mTitleTextView;
    private TextView mAmountTextView;
    private TextView mCategoryTextView;
    private TextView mDateTextView;
    private TextView mDescriptionTextView;
    private TextView mBigTitleTextView;

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
}
