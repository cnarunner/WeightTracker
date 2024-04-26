package com.snhu.cs_360.weighttracker_brycejensen;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * The DBHelper class is a SQLiteOpenHelper subclass that provides methods for managing
 * a SQLite database for storing user information, including username, password,
 * target weight, and phone number. It handles database creation, version upgrades,
 * and CRUD (Create, Read, Update, Delete) operations on the "users" table.
 */
public class DBHelper extends SQLiteOpenHelper {
    public static final String DBName = "register.db";
    public static final String defaultPhone = "+15551234567"; // +1 555-123-4567

    /**
     * Constructor for the DBHelper class.
     * @param context The application context.
     */
    public DBHelper(@Nullable Context context) {
        super(context, DBName, null, 3);
    }

    /**
     * Called when the database is created for the first time.
     * Creates the "users" table with columns for username, password, targetWeight, and phoneNumber.
     * @param db The SQLiteDatabase instance.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table users ( username TEXT primary key, password TEXT, targetWeight TEXT, phoneNumber TEXT)");
    }

    /**
     * Called when the database needs to be upgraded due to a version change.
     * Drops the existing "users" table and recreates it by calling onCreate().
     * @param db The SQLiteDatabase instance.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists users");
        onCreate(db);
    }

    /**
     * Inserts a new user into the "users" table with the provided username and password.
     * The targetWeight is set to a default value of "150", and the phoneNumber is set to a default value of "+15551234567".
     * @param username The user's username.
     * @param password The user's password.
     * @return true if the data was inserted successfully, false otherwise.
     */
    public boolean insertData(String username, String password) {
        SQLiteDatabase myDB = this.getWritableDatabase();
        ContentValues contentValues =  new ContentValues();
        contentValues.put("username", username);
        contentValues.put("password", password);
        String targetWeightDefault = "150";
        contentValues.put("targetWeight", targetWeightDefault);
        contentValues.put("phoneNumber", defaultPhone);
        long result = myDB.insert("users", null, contentValues);

        return result != -1;   // Returns true or false
    }

    /**
     * Checks if a given username exists in the "users" table.
     * @param username The username to check.
     * @return true if the username exists, false otherwise.
     */
    public boolean checkUsername(String username) {
        SQLiteDatabase myDB = this.getWritableDatabase();
        Cursor cursor = myDB.rawQuery("select * from users where username = ?", new String[]{username});

        return cursor.getCount() > 0;  // Returns true or false
    }

    /**
     * Checks if the provided username and password match a record in the "users" table.
     * @param username The user's username.
     * @param password The user's password.
     * @return true if the username and password match a record, false otherwise.
     */
    public boolean checkCredentials(String username, String password) {
        SQLiteDatabase myDB = this.getWritableDatabase();
        Cursor cursor = myDB.rawQuery("select * from users where username = ? and password = ?", new String[]{username, password});

        return cursor.getCount() > 0;
    }

    /**
     * Updates the target weight for a given username in the "users" table.
     * @param username The username of the user whose target weight needs to be updated.
     * @param targetWeight The new target weight value to be set.
     */
    public void updateTargetWeight(String username, String targetWeight) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("targetWeight", targetWeight);
        db.update("users", values, "username = ?", new String[]{username});
        db.close();
    }

    /**
     * Retrieves the target weight for a given username from the "users" table.
     * @param username The username of the user whose target weight needs to be retrieved.
     * @return The target weight value for the given username, or null if not found.
     */
    public String getTargetWeight(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("users", new String[]{"targetWeight"}, "username = ?", new String[]{username}, null, null, null);
        String targetWeight = null;
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") int targetWeightColumnIndex = cursor.getColumnIndexOrThrow(cursor.getColumnName(cursor.getColumnIndex("targetWeight")));
            targetWeight = cursor.getString(targetWeightColumnIndex);
        }
        cursor.close();
        db.close();
        return targetWeight;
    }

    /**
     * Updates the phone number for a given username in the "users" table.
     * @param username The username of the user whose phone number needs to be updated.
     * @param phoneNumber The new phone number value to be set.
     */
    public void updatePhoneNumber(String username, String phoneNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("phoneNumber", phoneNumber);
        db.update("users", values, "username = ?", new String[]{username});
        db.close();
    }

    /**
     * Retrieves the phone number for a given username from the "users" table.
     * @param username The username of the user whose phone number needs to be retrieved.
     * @return The phone number value for the given username, or null if not found.
     */
    public String getPhoneNumber(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("users", new String[]{"phoneNumber"}, "username = ?", new String[]{username}, null, null, null);
        String phoneNumber = null;
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") int phoneNumberColumnIndex = cursor.getColumnIndexOrThrow(cursor.getColumnName(cursor.getColumnIndex("phoneNumber")));
            phoneNumber = cursor.getString(phoneNumberColumnIndex);
        }
        cursor.close();
        db.close();
        return phoneNumber;
    }

    /**
     * Deletes a user record from the "users" table based on the given username.
     * @param username The username of the user to be deleted.
     */
    public void deleteUser(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = "username = ?";
        String[] selectionArgs = {username};
        db.delete("users", selection, selectionArgs);
        db.close();
    }
}
