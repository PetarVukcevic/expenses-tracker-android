package com.example.expensetracker;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class TransactionDetailsActivity extends Activity {
    private TextView mTitleTextView;
    private TextView mAmountTextView;
    private TextView mCategoryTextView;
    private TextView mDateTextView;
    private TextView mDescriptionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details);
        mTitleTextView = findViewById(R.id.titleTextView);
        mAmountTextView = findViewById(R.id.amountTextView);
        mCategoryTextView = findViewById(R.id.categoryTextView);
        mDateTextView = findViewById(R.id.dateTextView);
        mDescriptionTextView = findViewById(R.id.descriptionTextView);

        // Get the transaction object from the intent
        Transaction transaction = (Transaction) getIntent().getSerializableExtra("transaction");
        if (transaction != null) {
            // Set the transaction details in the TextViews
            mTitleTextView.setText(transaction.getTitle());
            mAmountTextView.setText(String.valueOf(transaction.getAmount()));
            mCategoryTextView.setText(transaction.getCategory());
            mDateTextView.setText(transaction.getDate());
            mDescriptionTextView.setText(transaction.getDescription());
        }
    }
}
