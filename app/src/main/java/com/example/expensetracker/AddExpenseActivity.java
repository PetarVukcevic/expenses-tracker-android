package com.example.expensetracker;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class AddExpenseActivity extends Activity {
    private TextView mAddExpenseHeading;
    private Button mAddExpenseButton;
    private EditText mAddExpenseInput;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);
        mAddExpenseHeading = (TextView) findViewById(R.id.add_expenses_heading);
        mAddExpenseInput = (EditText) findViewById(R.id.add_expenses_input);
        mAddExpenseButton = (Button) findViewById(R.id.add_expense_button);


    }
}
