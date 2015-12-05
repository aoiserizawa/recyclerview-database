package com.serverus.paroah.DB;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.serverus.paroah.models.ListInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyDBHandler extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 11;
    private static final String DATABASE_NAME = "paroah.db";
    public static final String TABLE_REMINDER = "reminders";
    public static final  String COLUMN_ID = "_id";
    public static final  String COLUMN_TITLE_REMINDER = "title";
    public static final  String COLUMN_DESC_REMINDER = "desc";
    public static final  String COLUMN_DATE_REMINDER = "date_created";
    private Cursor allReminders;

    public MyDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = " CREATE TABLE "
                +TABLE_REMINDER+ "(" +
                COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                COLUMN_TITLE_REMINDER + " TEXT ,"+
                COLUMN_DESC_REMINDER + " TEXT ,"+
                COLUMN_DATE_REMINDER + " DATETIME "+
                ");";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXIST " + TABLE_REMINDER);
//        onCreate(db);

        Log.d("aoi", "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        try {
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_REMINDER);
            onCreate(db);

        } catch (SQLException e) {
            Log.d("aoi",  "getting exception "
                    + e.getLocalizedMessage().toString());
        }
    }

    public long addReminder(ListInfo reminder){
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE_REMINDER, reminder.getTitle());
        values.put(COLUMN_DESC_REMINDER, reminder.getDesc());
        values.put(COLUMN_DATE_REMINDER, reminder.getDate());

        SQLiteDatabase db = getWritableDatabase();

        long id = db.insert(TABLE_REMINDER, null, values);
        db.close();

        return id;
    }

    public List<ListInfo> getAllData_a(){
        SQLiteDatabase db = getWritableDatabase();

        String query = "SELECT * FROM "+TABLE_REMINDER;

        Cursor cursor=db.rawQuery(query, null);
        List<ListInfo> data=new ArrayList<>();

        while (cursor.moveToNext()){
            int _id=cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE_REMINDER));
            String desc  = cursor.getString(cursor.getColumnIndex(COLUMN_DESC_REMINDER));
            String date  = cursor.getString(cursor.getColumnIndex(COLUMN_DATE_REMINDER));

            ListInfo current = new ListInfo();
            current.set_id(_id);
            current.title = title;
            current.desc = desc;
            current.date = date;
            data.add(current);
        }
        return data;

    }

    public boolean deleteReminder(int id)
    {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_REMINDER, COLUMN_ID + "=" + id, null) > 0;
    }

    public Cursor getAllReminders() {

        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM "+TABLE_REMINDER;

        allReminders = db.rawQuery(query, null);

        return allReminders;
    }

    public Cursor getReminder(int id){
        SQLiteDatabase db = getWritableDatabase();

        String[] tableColumns = new String[] {
                COLUMN_ID,
                COLUMN_TITLE_REMINDER,
                COLUMN_DESC_REMINDER,
                COLUMN_DATE_REMINDER
        };

        String whereClause = COLUMN_ID+" = ?";
        String[] whereArgs = new String[] {
                String.valueOf(id)
        };

        Cursor cursor = db.query(TABLE_REMINDER, tableColumns,whereClause,whereArgs, null, null, null);
        db.close();

        return cursor;
    }

}
