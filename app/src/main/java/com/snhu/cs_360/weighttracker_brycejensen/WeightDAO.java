package com.snhu.cs_360.weighttracker_brycejensen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class WeightDAO {
    private SQLiteDatabase database;

    public WeightDAO(Context context) {
        WeightDB dbHelper = new WeightDB(context);
        database = dbHelper.getWritableDatabase();
    }

    public void insert(WeightModel weight) {
        // Insert a new weight entry
        ContentValues values = new ContentValues();
        values.put(WeightDB.COLUMN_WEIGHT, weight.getWeight_Weight());
        values.put(WeightDB.COLUMN_DATE, weight.getWeight_Date());
        values.put(WeightDB.COLUMN_PLUS_MINUS, weight.getWeight_PlusMinus());
        database.insert(WeightDB.TABLE_WEIGHTS, null, values);
    }

    public void update(WeightModel weight) {
        // Update an existing weight entry
        ContentValues values = new ContentValues();
        values.put(WeightDB.COLUMN_WEIGHT, weight.getWeight_Weight());
        values.put(WeightDB.COLUMN_DATE, weight.getWeight_Date());
        values.put(WeightDB.COLUMN_PLUS_MINUS, weight.getWeight_PlusMinus());
        database.update(WeightDB.TABLE_WEIGHTS, values, WeightDB.COLUMN_ID + " = ?", new String[]{String.valueOf(weight.getWeight_Id())});
    }

    public void delete(int id) {
        // Delete a weight entry by id
        database.delete(WeightDB.TABLE_WEIGHTS, WeightDB.COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

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
                WeightModel weightModel = new WeightModel(weight, date, plusMinus);
                weightModel.setWeight_Id(id);
                weightList.add(weightModel);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return weightList;
    }
}
