package com.nmd.medicus;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DoctorAppointments extends AppCompatActivity {

    private String[] currentDate;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_appointments);

//        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(myToolbar);

        CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView);

        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        currentDate = dateFormat.format(date).split("/");

        calendar.set(Integer.parseInt(currentDate[0]), Integer.parseInt(currentDate[1]), Integer.parseInt(currentDate[2]));

        try {
            calendarView.setDate(calendar);
        } catch (OutOfDateRangeException e) {
            e.printStackTrace();
        }

        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                Calendar clickedDayCalendar = eventDay.getCalendar();
                Intent i = new Intent(DoctorAppointments.this, AppointmentDayActivity.class);
                i.putExtra("currentDay", clickedDayCalendar.get(Calendar.DAY_OF_MONTH) + "/" + clickedDayCalendar.get(Calendar.MONTH) + "/" + clickedDayCalendar.get(Calendar.YEAR));
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sign_out: {
                AuthUI.getInstance()
                        .signOut(DoctorAppointments.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                startActivity(new Intent(DoctorAppointments.this, LoginActivity.class));
                            }
                        });
                break;
            }
        }
        return false;
    }

    public void onBackPressed(){
//        Intent myIntent = new Intent(this, LoginActivity.class);
//        CustomListViewAndroidExample.this.startActivity(myIntent);
//        super.onBackPressed();
        if (doubleBackToExitPressedOnce) {
//            super.onBackPressed();
//            return;
//            System.exit(0);
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            System.exit(0);
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
