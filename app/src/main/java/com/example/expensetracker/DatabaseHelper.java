package com.example.expensetracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "expense-tracker-android.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createCategoriesTableQuery = "CREATE TABLE categories (id INTEGER PRIMARY KEY, name TEXT)";
        db.execSQL(createCategoriesTableQuery);

        // Create the expenses table
        String createExpensesTableQuery = "CREATE TABLE expenses (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, description TEXT, amount REAL, created_at TIMESTAMP, " +
                "category_id INTEGER, FOREIGN KEY (category_id) REFERENCES categories(id))";
        db.execSQL(createExpensesTableQuery);


        // Create the incomes table
        String createIncomesTableQuery = "CREATE TABLE incomes (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, description TEXT, amount REAL, created_at TIMESTAMP)";
        db.execSQL(createIncomesTableQuery);

        // Create the budget table
        String createBudgetTableQuery = "CREATE TABLE budget (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "time_period TEXT, budget_amount REAL)";
        db.execSQL(createBudgetTableQuery);

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Upgrade your database here if needed
        // This method is called when DATABASE_VERSION is incremented
    }
}
