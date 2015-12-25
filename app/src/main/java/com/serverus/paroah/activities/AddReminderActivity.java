package com.serverus.paroah.activities;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
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
    private Calendar c;
    private RelativeLayout containerLayout;

    private TextInputLayout layoutTitle;

    private int mHour;
    private int mHourOfDay;
    private int mMinute;

    MyDBHandler dbHandler;

    private static AlarmManager alarmManager;
    private PendingIntent sender;
    public static Context context;
    private Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        mToolBar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        dbHandler = new MyDBHandler(this);

        reminderTitle   = (EditText) findViewById(R.id.edit_text_title);
        reminderDesc    = (EditText) findViewById(R.id.edit_text_desc);
        setDateEdit     = (EditText) findViewById(R.id.date_edit);
        setTimeEdit     = (EditText) findViewById(R.id.time_edit);
        saveBtn         = (Button) findViewById(R.id.save_btn);

        layoutTitle     = (TextInputLayout) findViewById(R.id.title_layout);

        containerLayout = (RelativeLayout) findViewById(R.id.reminderContainer);

        saveBtn.setOnClickListener(this);
        setTimeEdit.setOnClickListener(this);
        setDateEdit.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_add_reminder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

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

        if(savedYear == 0 && savedMonth == 0 && savedDay == 0 ){
            return null;
        }else{

            cal.set(Calendar.YEAR, savedYear);
            cal.set(Calendar.MONTH, savedMonth);
            cal.set(Calendar.DAY_OF_MONTH, savedDay);
            cal.set(Calendar.HOUR_OF_DAY, mHourOfDay);
            cal.set(Calendar.MINUTE, mMinute);
            cal.set(Calendar.SECOND, 0);

            return sdf.format(cal.getTime());
        }
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

                setTimeEdit.setText(mHour + ":" + utilMinute(mMinute) + " " + AM_PM);
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

    private String utilMinute(int value) {
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
        alertIntent.putExtra("id", alarmId);
        alertIntent.putExtra("title", reminderTitle.getText().toString());
        alertIntent.putExtra("time", formatDateTime());

        sender = PendingIntent.getBroadcast(this, alarmId, alertIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, date.getTime(), sender);
    }

    private void saveReminder() {
        String title = reminderTitle.getText().toString();
        String desc = reminderDesc.getText().toString();

        Log.d("aoi", "sample return no date " + formatDateTime());

        if(!title.isEmpty() && formatDateTime() != null && !setTimeEdit.getText().toString().isEmpty()){
            ListInfo reminder = new ListInfo(
                    title,
                    desc,
                    formatDateTime()
            );

            long id = dbHandler.addReminder(reminder);
            int newId = (int) id;

            setAlarm(newId, formatDateTime());

            //adapter.notifyDataSetChanged();

            finish();

        }else{

            //validate all the fields that needed to be filled
            // pass a RelativeLayout type object
            formIsValid(containerLayout);
        }
    }

    public void formIsValid(RelativeLayout layout) {
        //loop through to all the childview inside relative layout
        for (int i = 0; i < layout.getChildCount(); i++) {
            // get each child by position
            View v = layout.getChildAt(i);
            // get the class of the view
            Class<? extends View> child = v.getClass();
            // check if its TextInputLayout
            if (child == TextInputLayout.class) {
                TextInputLayout textInput = (TextInputLayout) v;
                // get the child of TextInputLayout which is EditText in this case
                View childTextInput =  textInput.getChildAt(0);
                // get the class of the EditText
                Class<? extends View> ch =  childTextInput.getClass();
                // check if its EditText, in this case its AppCompatEditText
                if(ch == AppCompatEditText.class){
                    EditText et = (EditText) childTextInput;
                    Log.d("aoi", "EDIT TEXT "+et.getText().toString());
                    if(et.getText().toString().equals("")){
                        textInput.setErrorEnabled(true);
                        textInput.setError("Please Fill this field");
                    }
                }
            }
        }
    }
}
