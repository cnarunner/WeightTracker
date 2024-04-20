package com.snhu.cs_360.weighttracker_brycejensen;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WeightDB extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "WeightTracker.db";

    // Weights table
    public static final String TABLE_WEIGHTS = "weights";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_WEIGHT = "weight";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_PLUS_MINUS = "plus_minus";
    public static final String COLUMN_USERNAME = "username";

    public WeightDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_WEIGHTS_TABLE = "CREATE TABLE " + TABLE_WEIGHTS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_WEIGHT + " REAL,"
                + COLUMN_DATE + " TEXT,"
                + COLUMN_PLUS_MINUS + " TEXT,"
                + COLUMN_USERNAME + " TEXT" + ")";
        db.execSQL(CREATE_WEIGHTS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_WEIGHTS);

        onCreate(db);
    }

}
