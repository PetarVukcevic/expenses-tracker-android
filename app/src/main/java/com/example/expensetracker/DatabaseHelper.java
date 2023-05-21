package com.example.expensetracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "expense-tracker-android.db";
    private static final int DATABASE_VERSION = 4;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {




        String createTransactionsTableQuery = "CREATE TABLE transactions (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, description TEXT, amount REAL, created_at TIMESTAMP, " +
                "category TEXT, type TEXT)";
        db.execSQL(createTransactionsTableQuery);

    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 4) {

            // Delete the existing tables
            db.execSQL("DROP TABLE IF EXISTS expenses");
            db.execSQL("DROP TABLE IF EXISTS incomes");
            db.execSQL("DROP TABLE IF EXISTS transactions");


            String createIncomesTableQuery = "CREATE TABLE transactions (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "title TEXT, description TEXT, amount REAL, created_at TIMESTAMP, " +
                    "category TEXT, type TEXT)";
            db.execSQL(createIncomesTableQuery);
        }
        // Handle other database upgrades here if needed
    }


}
