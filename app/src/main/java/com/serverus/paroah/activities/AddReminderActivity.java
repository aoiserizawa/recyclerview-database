package com.serverus.paroah.activities;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.serverus.paroah.DB.MyDBHandler;
import com.serverus.paroah.R;
import com.serverus.paroah.adapters.RemindersAdapter;
import com.serverus.paroah.broadcastReceiver.AlertReceiver;
import com.serverus.paroah.models.ListInfo;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class AddReminderActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText setDateEdit;

    private int selectedYear ;
    private int selectedDay ;
    private int selectedMonth;

    private int savedYear ;
    private int savedDay ;
    private int savedMonth;

    private DatePickerDialog datePickerDialog;

    private Button saveBtn;
    private EditText reminderTitle;
    private EditText reminderDesc;
    private EditText setTimeEdit;
    private RemindersAdapter adapter;
    private Calendar c;


    private int mHour;
    private int mHourOfDay;
    private int mMinute;

    MyDBHandler dbHandler;

    private AlarmManager alarmManager;
    private PendingIntent sender;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        dbHandler = new MyDBHandler(this);

        reminderTitle   = (EditText) findViewById(R.id.edit_text_title);
        reminderDesc    = (EditText) findViewById(R.id.edit_text_desc);
        setDateEdit     = (EditText) findViewById(R.id.date_edit);
        setTimeEdit     = (EditText) findViewById(R.id.time_edit);
        saveBtn         = (Button) findViewById(R.id.save_btn);

        saveBtn.setOnClickListener(this);
        setTimeEdit.setOnClickListener(this);
        setDateEdit.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_reminder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setDate(MenuItem item){

        setDateEdit.setText("");

        c = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                selectedYear = year;
                selectedMonth = monthOfYear;
                selectedDay = dayOfMonth;
                setDateEdit.setText(formatDate(selectedYear, selectedMonth, selectedDay));
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        switch (item.getItemId()){
            case R.id.set_date_today:
                setDateEdit.setText(
                        formatDate(
                                c.get(Calendar.YEAR),
                                c.get(Calendar.MONTH),
                                c.get(Calendar.DAY_OF_MONTH)
                        ));
                break;
            case R.id.set_date_tomorrow:
                c.add(Calendar.DAY_OF_MONTH, 1);
                setDateEdit.setText(
                        formatDate(
                                c.get(Calendar.YEAR),
                                c.get(Calendar.MONTH),
                                c.get(Calendar.DAY_OF_MONTH)
                        ));
                break;
            default:
                datePickerDialog.show();
                break;
        }
    }

    private String formatDate(int year, int month, int day) {
        saveDateToVar(year,month,day);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        cal.set(year, month, day);
        Date date = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        return sdf.format(date);
    }

    //I have to use this method to save the selected date from onDateSet
    // I call this method inside formateDate, because Im losing the value
    // of variable inside onDateSet.

    //using this inside onDateSet doesnt also get the selected date
    // thats why I have to use this inside formatDate
    private void saveDateToVar(int year, int month, int day){
        savedYear = year;
        savedDay = day;
        savedMonth = month;
    }

    private String formatDateTime(){
        Calendar cal = new GregorianCalendar();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss aa");
        cal.set(Calendar.YEAR, savedYear);
        cal.set(Calendar.MONTH, savedMonth);
        cal.set(Calendar.DAY_OF_MONTH, savedDay);
        cal.set(Calendar.HOUR_OF_DAY, mHourOfDay);
        cal.set(Calendar.MINUTE, mMinute);
        cal.set(Calendar.SECOND, 0);

        return sdf.format(cal.getTime());
    }

    private void setTime(){

        TimePickerDialog.OnTimeSetListener timePicker=new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay,
                                  int minute) {

                mHourOfDay = mHour =  hourOfDay;
                mMinute = minute;

                String AM_PM ;
                if (hourOfDay > 12) {
                    mHour -= 12;
                    AM_PM = "PM";
                } else if (hourOfDay == 0) {
                    mHour += 12;
                    AM_PM = "AM";
                } else if (hourOfDay == 12)
                    AM_PM = "PM";
                else
                    AM_PM = "AM";

                setTimeEdit.setText(mHour + ":" + utilTime(mMinute) + " " + AM_PM);
            }
        };

        Calendar cal = Calendar.getInstance();
        // the 5th parameter is to tell if the timepicker is 24hr or not, this time its not
        TimePickerDialog timePickerDialog =  new TimePickerDialog(this, timePicker,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                false);

        timePickerDialog.show();
    }

    private String utilTime(int value) {
        if (value < 10) {
            return "0" + String.valueOf(value);
        }else{
            return String.valueOf(value);
        }
    }

    private void showDateMenu(){
        PopupMenu popupDateMenu = new PopupMenu(this, setDateEdit);
        popupDateMenu.getMenuInflater().inflate(R.menu.menu_date, popupDateMenu.getMenu());

        popupDateMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                setDate(item);
                return true;
            }
        });

        popupDateMenu.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.date_edit:
                showDateMenu();
                break;
            case R.id.save_btn:
                saveReminder();
                break;
            case R.id.time_edit:
                setTime();
                break;
        }
    }

    public void setAlarm(int alarmId, String dateTime){
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        try {
            date = sdf.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Intent alertIntent = new Intent(this, AlertReceiver.class);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alertIntent.putExtra("title", reminderTitle.getText().toString());
        alertIntent.putExtra("time", formatDateTime());

        sender = PendingIntent.getBroadcast(this, alarmId, alertIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, date.getTime(), sender);
    }

    private void saveReminder() {
        ListInfo reminder = new ListInfo(
                reminderTitle.getText().toString(),
                reminderDesc.getText().toString(),
                formatDateTime()
        );

        long id = dbHandler.addReminder(reminder);
        int newId = (int) id;

        Log.d("aoi", "FORMAT DATE TIME "+formatDateTime());

        setAlarm(newId, formatDateTime());

        adapter.notifyDataSetChanged();
    }
}
