package com.example.expensetracker;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {
    private List<Expense> expenseList;

    public ExpenseAdapter(List<Expense> expenseList) {
        this.expenseList = expenseList;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.expense_item, parent, false);
        return new ExpenseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenseList.get(position);
        String title = expense.getTitle();
        String amount = String.valueOf(expense.getAmount());
        String type = expense.getType();
        holder.bindData(title, amount, type);
    }


    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView amountTextView;

        public ExpenseViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
        }



        public void bindData(String title, String amount, String type) {
            titleTextView.setText(title);

            if (type != null) {
                if (type.equals("incomes")) {
                    amountTextView.setText("+" + amount + "€");
                    amountTextView.setTextColor(Color.GREEN); // Set text color to green for incomes
                    titleTextView.setTextColor(Color.GREEN);
                } else {
                    amountTextView.setText("-" + amount + "€");
                    amountTextView.setTextColor(Color.RED); // Set text color to red for outcomes
                    titleTextView.setTextColor(Color.RED);

                }
            }
            else {
                // Handle null type value here
                amountTextView.setText(amount + "€");
                amountTextView.setTextColor(Color.BLACK); // Set default text color
            }
        }


    }
}
