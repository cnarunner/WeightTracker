package com.snhu.cs_360.weighttracker_brycejensen;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * The WeightDB class is a SQLiteOpenHelper subclass that manages the creation and version management
 * of the SQLite database used for storing weight tracking data.
 */
public class WeightDB extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 3; // The current database version
    private static final String DATABASE_NAME = "WeightTracker.db"; // The name of the database file

    // Weights table
    public static final String TABLE_WEIGHTS = "weights"; // The name of the weights table
    public static final String COLUMN_ID = "_id"; // The column name for the unique identifier
    public static final String COLUMN_WEIGHT = "weight"; // The column name for the weight value
    public static final String COLUMN_DATE = "date"; // The column name for the date
    public static final String COLUMN_PLUS_MINUS = "plus_minus"; // The column name for the plus/minus indicator
    public static final String COLUMN_USERNAME = "username"; // The column name for the username

    /**
     * Constructor for the WeightDB class.
     *
     * @param context The application context.
     */
    public WeightDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time. This method creates the weights table.
     *
     * @param db The SQLiteDatabase instance.
     */
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

    /**
     * Called when the database needs to be upgraded to a newer version. This method drops the existing
     * tables and recreates them.
     *
     * @param db         The SQLiteDatabase instance.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_WEIGHTS);
        db.execSQL("drop table if exists users");

        onCreate(db);
    }
}
