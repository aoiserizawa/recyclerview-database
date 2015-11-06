package com.serverus.paroah.activities;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.serverus.paroah.DB.MyDBHandler;
import com.serverus.paroah.R;
import com.serverus.paroah.adapters.RemindersAdapter;
import com.serverus.paroah.fragments.TimePickerFragment;
import com.serverus.paroah.models.ListInfo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
        Calendar cal = Calendar.getInstance();
        cal.set(savedYear, savedDay, savedMonth, mHourOfDay, mMinute,0);
        Date date = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return sdf.format(date);

    }

    private void setTime(){
        TimePickerDialog.OnTimeSetListener timePicker=new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay,
                                  int minute) {
                mHourOfDay = mHour =  hourOfDay;
                mMinute =  minute;

                String AM_PM ;
                if(hourOfDay < 12) {
                    AM_PM = "AM";

                } else {
                    AM_PM = "PM";
                    mHour=mHour-12;
                }

                setTimeEdit.setText(mHour+":"+mMinute+" "+AM_PM);
                formatDateTime();
            }
        };

        // the 5th parameter is to tell if the timepicker is 24hr or not, this time its not
        TimePickerDialog timePickerDialog =  new TimePickerDialog(this, timePicker,
                c.get(Calendar.HOUR_OF_DAY),
                c.get(Calendar.MINUTE),
                false);

        timePickerDialog.show();

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

    private void saveReminder() {
        ListInfo reminder = new ListInfo(
                reminderTitle.getText().toString(),
                reminderDesc.getText().toString(),
                formatDateTime()
        );

        dbHandler.addReminder(reminder);
        adapter.notifyDataSetChanged();
    }
}
