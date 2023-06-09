package com.example.expensetracker;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

public class TransactionTracker extends RecyclerView.Adapter<TransactionTracker.TransactionViewHolder> {
    private List<Transaction> transactionList;

    public TransactionTracker(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_item, parent, false);
        return new TransactionViewHolder(itemView, transactionList);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);
        String title = transaction.getTitle();
        String amount = String.valueOf(transaction.getAmount());
        String type = transaction.getType();
        String category = transaction.getCategory();
        String date = transaction.getDate();
        holder.bindData(title, amount, type, category, date);
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }


    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView amountTextView;
        private TextView categoryTextView;
        private TextView dateTextView;
        private List<Transaction> transactionList;

        public TransactionViewHolder(View itemView, List<Transaction> transactionList) {
            super(itemView);
            this.transactionList = transactionList;
            titleTextView = itemView.findViewById(R.id.titleTextView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
            categoryTextView = itemView.findViewById(R.id.categoryTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Transaction transaction = transactionList.get(position);
                        // Handle the click event for the transaction
                        // For example, you can start a new activity to show transaction details
                        Intent intent = new Intent(itemView.getContext(), TransactionDetailsActivity.class);
                        intent.putExtra("transaction", transaction);
                        itemView.getContext().startActivity(intent);
                    }
                }
            });
        }

        public void bindData(String title, String amount, String type, String category, String date) {
            titleTextView.setText(title);
            categoryTextView.setText(category);
            dateTextView.setText(date);
            if (type != null) {
                if (type.equals("incomes")) {
                    amountTextView.setText("+" + amount + " €");
                    amountTextView.setTextColor(Color.GREEN); // Set text color to green for incomes
                } else {
                    amountTextView.setText("-" + amount + " €");
                    amountTextView.setTextColor(Color.RED); // Set text color to red for expenses
                }
            } else {
                // Handle null type value here
                amountTextView.setText(amount + "€");
                amountTextView.setTextColor(Color.BLACK); // Set default text color
            }
        }
    }
}

