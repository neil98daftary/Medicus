package com.nmd.medicus;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

public class SelectAppointmentActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    private Calendar selectedDay;
    private String uid;
    private FirebaseFirestore db;
    private String appointmentString;
    private TimePickerDialog tpd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_appointment);

        selectedDay = (Calendar) getIntent().getSerializableExtra("MyClass");
        uid = getIntent().getStringExtra("uid");

        db = FirebaseFirestore.getInstance();

        db.collection("doctors")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getData().get("uid").equals(uid)) {
                                    if(document.getData().containsKey("appointments")) {
                                        appointmentString = document.getData().get("appointments").toString();
                                    }
                                }
                                Log.d("tag1", document.getId() + " => " + document.getData().get("type"));
                            }
                        } else {
                            Log.w("tag1", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void selecttime(View v) {
        Calendar now = Calendar.getInstance();
        tpd = TimePickerDialog.newInstance(
                SelectAppointmentActivity.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false
        );
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        String hourString = hourOfDay < 10 ? "0"+hourOfDay : ""+hourOfDay;
        String minuteString = minute < 10 ? "0"+minute : ""+minute;
        String secondString = second < 10 ? "0"+second : ""+second;
        String time = "You picked the following time: "+hourString+"h"+minuteString+"m"+secondString+"s";
    }
}
