package com.snhu.cs_360.weighttracker_brycejensen;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DBName = "register.db";
    public static final String defaultPhone = "+15551234567"; // +1 555-123-4567

    public DBHelper(@Nullable Context context) {
        super(context, DBName, null, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table users ( username TEXT primary key, password TEXT, targetWeight TEXT, phoneNumber TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists users");

        onCreate(db);
    }

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

    public boolean checkUsername(String username) {
        SQLiteDatabase myDB = this.getWritableDatabase();
        Cursor cursor = myDB.rawQuery("select * from users where username = ?", new String[]{username});

        return cursor.getCount() > 0;  // Returns true or false
    }

    public boolean checkCredentials(String username, String password) {
        SQLiteDatabase myDB = this.getWritableDatabase();
        Cursor cursor = myDB.rawQuery("select * from users where username = ? and password = ?", new String[]{username, password});

        return cursor.getCount() > 0;
    }

    public void updateTargetWeight(String username, String targetWeight) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("targetWeight", targetWeight);
        db.update("users", values, "username = ?", new String[]{username});
        db.close();
    }

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

    public void updatePhoneNumber(String username, String phoneNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("phoneNumber", phoneNumber);
        db.update("users", values, "username = ?", new String[]{username});
        db.close();
    }

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

    public void deleteUser(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = "username = ?";
        String[] selectionArgs = {username};
        db.delete("users", selection, selectionArgs);
        db.close();
    }
}
