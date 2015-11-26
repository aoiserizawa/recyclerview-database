package com.serverus.paroah.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.NavigationView;
import android.support.design.widget.SwipeDismissBehavior;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;


import com.serverus.paroah.DB.MyDBHandler;
import com.serverus.paroah.adapters.CursorRecyclerViewAdapter;
import com.serverus.paroah.adapters.RemindersAdapter;
import com.serverus.paroah.broadcastReceiver.AlertReceiver;
import com.serverus.paroah.R;
import com.serverus.paroah.fragments.TimePickerFragment;
import com.serverus.paroah.models.ListInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mToolBar;
    private NavigationView mDrawer;
    private ActionBarDrawerToggle mdrawerToggle;
    private DrawerLayout mDrawerLayout;

    private Button alarmButton;
    private AlarmManager alarmManager;
    private PendingIntent sender;

    private RecyclerView listReminder;
    private RemindersAdapter adapter;

    private List<ListInfo> data = Collections.emptyList();
    ListInfo infoData;

    private Button addReminderBtn;
    private CardView mCardView;

    MyDBHandler dbHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initDrawer();
        initView();
        initSwipeListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        mdrawerToggle.onConfigurationChanged(newConfig);
    }

    public void initDrawer(){
        mToolBar = (Toolbar) findViewById(R.id.app_bar);
        mDrawer = (NavigationView) findViewById(R.id.main_drawer);
        setSupportActionBar(mToolBar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mdrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                mToolBar,
                R.string.drawer_open,
                R.string.drawer_close);
        mDrawerLayout.setDrawerListener(mdrawerToggle);
        // indicator based on whether the drawerlayout is in open or closed
        mdrawerToggle.syncState();
    }

    public void initView(){
        listReminder = (RecyclerView) findViewById(R.id.listData);

        dbHandler = new MyDBHandler(this);

        //cursorToObject(dbHandler.getAllReminders());

        adapter = new RemindersAdapter(this, dbHandler.getAllReminders());
        listReminder.setAdapter(adapter);
        listReminder.setLayoutManager(new LinearLayoutManager(this));

        addReminderBtn = (Button) findViewById(R.id.addBtn);

        addReminderBtn.setOnClickListener(this);

        mCardView = (CardView) findViewById(R.id.cv);


    }

    public void initSwipeListener(){
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                RemindersAdapter.ItemViewHolder itemViewHolder = (RemindersAdapter.ItemViewHolder)viewHolder;
                int itemPosition = itemViewHolder.getAdapterPosition();

                cancelAlarm(itemViewHolder.id);

                adapter.notifyItemRemoved(itemPosition);
                // get the id of an item via itemViewHolder.id
                dbHandler.deleteReminder(itemViewHolder.id);

                // update cursor upon deleting do avoid
                // the card from coming back upon swipe
                adapter.swapCursor(dbHandler.getAllReminders());
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(listReminder);
    }

    public void cancelAlarm(int alarmId){
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alertIntent = new Intent(this, AlertReceiver.class);
        PendingIntent alarmCancel = PendingIntent.getBroadcast(this, alarmId, alertIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(alarmCancel);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.addBtn:
                Intent addReminder = new Intent(this, AddReminderActivity.class);
                startActivity(addReminder);
                break;
        }
    }
}
