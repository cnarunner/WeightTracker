package com.snhu.cs_360.weighttracker_brycejensen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * The WeightDAO class provides methods for interacting with the SQLite database
 * to perform CRUD (Create, Read, Update, Delete) operations on weight entries.
 */
public class WeightDAO {
    private SQLiteDatabase database;

    /**
     * Constructor for the WeightDAO class.
     *
     * @param context The application context.
     */
    public WeightDAO(Context context) {
        WeightDB dbHelper = new WeightDB(context);
        database = dbHelper.getWritableDatabase();
    }

    /**
     * Inserts a new weight entry into the database.
     *
     * @param weight The WeightModel object containing the weight entry data.
     */
    public void insert(WeightModel weight) {
        // Insert a new weight entry
        ContentValues values = new ContentValues();
        values.put(WeightDB.COLUMN_WEIGHT, weight.getWeight_Weight());
        values.put(WeightDB.COLUMN_DATE, weight.getWeight_Date());
        values.put(WeightDB.COLUMN_PLUS_MINUS, weight.getWeight_PlusMinus());
        values.put(WeightDB.COLUMN_USERNAME, weight.getWeight_Username());

        database.insert(WeightDB.TABLE_WEIGHTS, null, values);
    }

    /**
     * Updates an existing weight entry in the database.
     *
     * @param weight The WeightModel object containing the updated weight entry data.
     */
    public void update(WeightModel weight) {
        // Update an existing weight entry
        ContentValues values = new ContentValues();
        values.put(WeightDB.COLUMN_WEIGHT, weight.getWeight_Weight());
        values.put(WeightDB.COLUMN_DATE, weight.getWeight_Date());
        values.put(WeightDB.COLUMN_PLUS_MINUS, weight.getWeight_PlusMinus());
        database.update(WeightDB.TABLE_WEIGHTS, values, WeightDB.COLUMN_ID + " = ?", new String[]{String.valueOf(weight.getWeight_Id())});
    }

    /**
     * Deletes a weight entry from the database based on its ID.
     *
     * @param id The ID of the weight entry to be deleted.
     */
    public void delete(int id) {
        // Delete a weight entry by id
        database.delete(WeightDB.TABLE_WEIGHTS, WeightDB.COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }
    
    /**
     * Retrieves all weight entries from the database, ordered by date in descending order.
     *
     * @return A list of WeightModel objects representing the weight entries.
     */
    public List<WeightModel> getAllWeights() {
        // Retrieve all weight entries
        List<WeightModel> weightList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + WeightDB.TABLE_WEIGHTS + " ORDER BY " + WeightDB.COLUMN_DATE + " DESC";
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(WeightDB.COLUMN_ID));
                String weight = cursor.getString(cursor.getColumnIndexOrThrow(WeightDB.COLUMN_WEIGHT));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(WeightDB.COLUMN_DATE));
                String plusMinus = cursor.getString(cursor.getColumnIndexOrThrow(WeightDB.COLUMN_PLUS_MINUS));
                String username = cursor.getString(cursor.getColumnIndexOrThrow(WeightDB.COLUMN_USERNAME));

                WeightModel weightModel = new WeightModel(weight, date, plusMinus, username);
                weightModel.setWeight_Id(id);
                weightList.add(weightModel);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return weightList;
    }

    /**
     * Deletes all weight entries for a given username from the database.
     *
     * @param username The username for which all weight entries should be deleted.
     */
    public void deleteAllWeightsForUser(String username) {
        String selection = WeightDB.COLUMN_USERNAME + " = ?";
        String[] selectionArgs = {username};
        database.delete(WeightDB.TABLE_WEIGHTS, selection, selectionArgs);
    }
}

