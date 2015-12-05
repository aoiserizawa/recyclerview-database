package com.serverus.paroah.activities;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.serverus.paroah.DB.MyDBHandler;
import com.serverus.paroah.R;
import com.serverus.paroah.models.ListInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ReminderPreviewActivity extends AppCompatActivity {

    private Toolbar mToolBar;

    private TextView titleTextView;
    private TextView descTextView;
    private TextView timeTextView;
    private TextView dateTextView;

    private String title;
    private String desc;
    private String date;

    private MyDBHandler dbHandler;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_preview);

        dbHandler = new MyDBHandler(this);

        mToolBar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        titleTextView   = (TextView) findViewById(R.id.titleTextView);
        descTextView    = (TextView) findViewById(R.id.descTextView);
        timeTextView    = (TextView) findViewById(R.id.timeTextView);
        dateTextView    = (TextView) findViewById(R.id.dateTextView);

        Intent extras = getIntent();

        if(extras.getStringExtra("title") != null){
            setContentFromExtras(extras);
        }else{
            setContentFromDB(extras);
        }
    }

    private void setContentFromExtras(Intent extras){
        title   = extras.getStringExtra("title");
        desc    = extras.getStringExtra("desc");
        date    = extras.getStringExtra("date");

        String[] dateDB = date.split(" ");

        titleTextView.setText(title);
        descTextView.setText(desc);
        timeTextView.setText(formatTime(dateDB[1])+" "+dateDB[2]);
        dateTextView.setText(formatDate(dateDB[0]));
    }

    public void setContentFromDB(Intent extras){
        int id = 0;
        int reminderID = extras.getIntExtra("id", id);

        cursor = dbHandler.getReminder(reminderID);

        while (cursor.moveToNext()){
            int _id         =cursor.getInt(cursor.getColumnIndex(dbHandler.COLUMN_ID));
            String title    = cursor.getString(cursor.getColumnIndex(dbHandler.COLUMN_TITLE_REMINDER));
            String desc     = cursor.getString(cursor.getColumnIndex(dbHandler.COLUMN_DESC_REMINDER));
            String date     = cursor.getString(cursor.getColumnIndex(dbHandler.COLUMN_DATE_REMINDER));

            String[] dateDB = date.split(" ");

            titleTextView.setText(title);
            descTextView.setText(desc);
            timeTextView.setText(formatTime(dateDB[1])+" "+dateDB[2]);
            dateTextView.setText(formatDate(dateDB[0]));
        }
    }

    private String formatDate(String date) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date newDate = null;
        String time;
        try {
            newDate = sdf.parse(date);

            time = newDate.toString().substring(10, 29);

            String stringNewDate = String.valueOf(newDate);
            String formatedDate = stringNewDate.replace(time, "");

            return formatedDate;
        } catch (ParseException e) {
            e.printStackTrace();
            return String.valueOf(e);
        }
    }

    private String formatTime(String time){
        String hour = time.substring(0,2);
        String minute = time.substring(3,5);
        int intTime = Integer.parseInt(hour);

        if(intTime > 12){
            intTime = intTime-12;
        }else if(intTime == 00){
            intTime = 12;
        }
        return String.valueOf(intTime)+":"+minute;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
