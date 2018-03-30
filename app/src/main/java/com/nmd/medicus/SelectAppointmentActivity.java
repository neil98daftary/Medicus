package com.nmd.medicus;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SelectAppointmentActivity extends AppCompatActivity {

    private String uid;
    private FirebaseFirestore db;
    private String appointmentString = "";
    private Button selectTime;
    private String key, startTime, endTime, selectedDay;
    private ArrayList<String> appointmentDays = new ArrayList<>();
    private HashMap<String, HashMap<String, Date>> dates = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_appointment);

        selectTime = (Button)findViewById(R.id.selectTime);
        selectTime.setVisibility(View.INVISIBLE);

        selectedDay = getIntent().getStringExtra("currentDay");
        uid = getIntent().getStringExtra("uid");

        db = FirebaseFirestore.getInstance();

        db.collection("appointments")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getData().get("uid").equals(uid)) {
                                    startTime = document.getData().get("startTime").toString();
                                    endTime = document.getData().get("endTime").toString();
                                    if(document.getData().containsKey("dates")) {
                                        dates = (HashMap<String, HashMap<String, Date>>) document.getData().get("dates");
                                    }
                                    key = document.getId();
                                }
                            }
                            selectTime.setVisibility(View.VISIBLE);
                        } else {
                            Log.w("tag1", "Error getting documents.", task.getException());
                        }
                    }
                });

        selectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                new android.app.TimePickerDialog(
                        SelectAppointmentActivity.this,
                        new android.app.TimePickerDialog.OnTimeSetListener(){
                            @Override
                            public void onTimeSet(TimePicker view, int hour, int minute) {
                                try {
                                    checkIfAvailable2(hour, minute);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        false
                ).show();
            }
        });
    }


//    HashMap<selectedDay, HashMap<Date, patientUID>>

    public void checkIfAvailable2(int hour, int minute) throws ParseException {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date newDate = sdf.parse(Integer.toString(hour) + ":" + Integer.toString(minute));
        Date startDate = sdf.parse(startTime);
        Date endDate = sdf.parse(endTime);

        HashMap<String, Date> times;
        int i;
        long firstDifference, nextDifference;

        if(dates != null) {
            if(dates.containsKey(selectedDay)) {
                times = dates.get(selectedDay);
                ArrayList<Date> justTimesNoUid = new ArrayList<>();
                for (String key : times.keySet()) {
                    justTimesNoUid.add(times.get(key));
                }
                Collections.sort(justTimesNoUid);

                if(newDate.after(startDate) && newDate.before(endDate)) {
                    if(newDate.after(startDate) && newDate.before(justTimesNoUid.get(0))) {
                        if(TimeUnit.MILLISECONDS.toMinutes(justTimesNoUid.get(0).getTime() - newDate.getTime()) >= 60) {
                            times.put(user.getUid().toString(), newDate);
                            dates.put(selectedDay, times);

                            makeAppointment(dates);
                        }
                        else {
                            Toast.makeText(SelectAppointmentActivity.this, "This slot is unavailable", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else if(newDate.after(justTimesNoUid.get(justTimesNoUid.size() - 1)) && newDate.before(endDate)) {
                        if(TimeUnit.MILLISECONDS.toMinutes(newDate.getTime() - justTimesNoUid.get(justTimesNoUid.size() - 1).getTime()) >= 60) {
                            times.put(user.getUid().toString(), newDate);
                            dates.put(selectedDay, times);
                            makeAppointment(dates);
                        }
                        else {
                            Toast.makeText(SelectAppointmentActivity.this, "This slot is unavailable", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        for(i = 0; i <= justTimesNoUid.size() - 2; i++) {
                            if(newDate.after(justTimesNoUid.get(i)) && newDate.before(justTimesNoUid.get(i + 1))) {
                                firstDifference = TimeUnit.MILLISECONDS.toMinutes(newDate.getTime() - justTimesNoUid.get(i).getTime());
                                nextDifference = TimeUnit.MILLISECONDS.toMinutes(justTimesNoUid.get(i + 1).getTime() - newDate.getTime());
                                if(firstDifference >= 60 && nextDifference >= 60) {
                                    times.put(user.getUid().toString(), newDate);
                                    dates.put(selectedDay, times);
                                    makeAppointment(dates);
                                }
                                else {
                                    Toast.makeText(SelectAppointmentActivity.this, "This slot is unavailable", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                }
                else {
                    Toast.makeText(SelectAppointmentActivity.this, "The doctor is only available between " + startTime + " and " + endTime, Toast.LENGTH_SHORT).show();
                }

            }
            else {
                times = new HashMap<>();
                times.put(user.getUid().toString(), newDate);
                dates.put(selectedDay, times);
                makeAppointment(dates);
            }
        }
        else {
            dates = new HashMap<>();
            times = new HashMap<>();
            times.put(user.getUid().toString(), newDate);
            dates.put(selectedDay, times);
            makeAppointment(dates);
        }
    }

    public void makeAppointment(HashMap<String, HashMap<String, Date>> datesToBePushed) {
        HashMap<String, HashMap<String, HashMap<String, Date>>> data = new HashMap<>();
        data.put("dates", datesToBePushed);
        db.collection("appointments").document(key)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public void checkIfAvailable(int hour, int minute) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date newDate = sdf.parse(Integer.toString(hour) + ":" + Integer.toString(minute));
        Date start = sdf.parse(startTime);
        Date end = sdf.parse(endTime);
        long firstDifference, nextDifference;

        if(newDate.after(start) && newDate.before(end)) {
            if (appointmentString.equals("")) {
                makeAppointment(String.format("%02d", hour) + ":" + String.format("%02d", minute));
            }
            else {
                String date = appointmentString.split(",")[0];
                String[] times = Arrays.copyOfRange(appointmentString.split(","), 1, appointmentString.split(",").length);
                ArrayList<Date> myDates = new ArrayList<Date>();
                ArrayList<String> myTimes = new ArrayList<String>();
                int i;
                for(String time : times) {
                    myDates.add(sdf.parse(time));
                }
                Collections.sort(myDates);
                for(Date time : myDates) {
                    myTimes.add(sdf.format(time));
                }
                if(newDate.after(start) && newDate.before(myDates.get(0))) {
//                    Log.v("tag1", Long.toString(TimeUnit.MILLISECONDS.toMinutes(myDates.get(0).getTime() - newDate.getTime())));
                    if(TimeUnit.MILLISECONDS.toMinutes(myDates.get(0).getTime() - newDate.getTime()) >= 60) {
                        myTimes.add(sdf.format(newDate));
//                        Log.v("tag1", sdf.format(newDate));
                        makeAppointment(Arrays.toString(myTimes.toArray()).replace("[", "").replace("]", ""));
                    }
                    else{
                        Toast.makeText(SelectAppointmentActivity.this, "This slot is already booked", Toast.LENGTH_SHORT).show();
                    }
                }
                else if(newDate.after(myDates.get(myDates.size() - 1)) && newDate.before(end)) {
//                    Log.v("tag1", Long.toString(newDate.getTime()));
                    if(TimeUnit.MILLISECONDS.toMinutes(newDate.getTime() - myDates.get(myDates.size() - 1).getTime()) >= 60) {
//                        Log.v("tag1", newDate.toString());
//                        Log.v("tag1", sdf.format(newDate));
                        myTimes.add(sdf.format(newDate));
                        makeAppointment(Arrays.toString(myTimes.toArray()).replace("[", "").replace("]", ""));
                    }
                    else{
                        Toast.makeText(SelectAppointmentActivity.this, "This slot is already booked", Toast.LENGTH_SHORT).show();
                    }
                }
                else if(newDate.after(start) && newDate.before(end)) {
//                    Log.v("tag1", "middle");
                    for(i = 0; i <= myDates.size() - 2; i++) {
                        firstDifference = TimeUnit.MILLISECONDS.toMinutes(newDate.getTime() - myDates.get(i).getTime());
                        nextDifference = TimeUnit.MILLISECONDS.toMinutes(myDates.get(i + 1).getTime() - newDate.getTime());
                        if(newDate.after(myDates.get(i)) && newDate.before(myDates.get(i + 1))) {
//                            Log.v("tag1", Long.toString(firstDifference));
//                            Log.v("tag1", Long.toString(nextDifference));
                            if(firstDifference >= 1 && nextDifference >= 60) {
                                myTimes.add(sdf.format(newDate));
                                makeAppointment(Arrays.toString(myTimes.toArray()).replace("[", "").replace("]", ""));
                            }
                            else{
                                Toast.makeText(SelectAppointmentActivity.this, "This slot is already booked", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
                else{
                    Toast.makeText(SelectAppointmentActivity.this, "This slot is already booked", Toast.LENGTH_SHORT).show();
                }
            }

        }
        else {
            Toast.makeText(SelectAppointmentActivity.this, "The doctor is only available between " + startTime + " and " + endTime, Toast.LENGTH_SHORT).show();
        }
    }

    public void makeAppointment(String time) {
        int j = 0;
        String newAddition = selectedDay + "," + time;
//        Log.v("tag1", "," + time);
        if(appointmentDays.isEmpty()) {
            appointmentDays.add(newAddition);
        }
        else {
            for (j = 0; j < appointmentDays.size(); j++) {
                if(appointmentDays.get(j).split(",")[0].equals(selectedDay)) {
                    appointmentDays.set(j, newAddition);
                    break;
                }
            }
        }
        if(j == appointmentDays.size() + 1) {
            Log.v("tag1", "YESSS");
            appointmentDays.add(newAddition);
        }
//        Log.v("tag1", appointmentDays.toString());
        appointmentString = android.text.TextUtils.join(";", appointmentDays);
        Map<String, Object> data = new HashMap<>();
        data.put("appointments", appointmentString);
        db.collection("doctors").document(key)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("tag1", "Error writing document", e);
                    }
                });
        Toast.makeText(SelectAppointmentActivity.this, "Your appointment hass been booked", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(SelectAppointmentActivity.this, CustomListViewAndroidExample.class));
    }
}
