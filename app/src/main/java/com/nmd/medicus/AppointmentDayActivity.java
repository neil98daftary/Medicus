package com.nmd.medicus;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AppointmentDayActivity extends AppCompatActivity {

    String selected;
    String uid;
    HashMap<String , HashMap<String,Date>> appointments;
    ArrayList<AppointmentModel> x = new ArrayList<AppointmentModel>();
    ListView v;
    CustomAdapter3 adapter;
    public AppointmentDayActivity  z = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_day);

        selected =  getIntent().getStringExtra("Date");
        uid = getIntent().getStringExtra("uid");
        v = (ListView) findViewById(R.id.appointmentList);
        z = this;

        Log.v("uid",uid);
        Log.v("date",selected);

        DocumentReference docRef = FirebaseFirestore.getInstance().collection("appointments").document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                appointments = (HashMap<String , HashMap<String,Date>>) task.getResult().getData().get("dates");
                DocumentSnapshot xx = task.getResult();
                Log.v("testData",xx.getData().get("startTime").toString());
            }
        });

        if(!(appointments == null)) {
            for (Map.Entry<String, HashMap<String, Date>> letterEntry : appointments.entrySet()) {
                String letter = letterEntry.getKey();
                if (!letter.equals(selected)) {
                    continue;
                }
                // ...
                for (Map.Entry<String, Date> nameEntry : letterEntry.getValue().entrySet()) {
                    String uid = nameEntry.getKey();
                    Date time = nameEntry.getValue();
                    x.add(new AppointmentModel(uid, time));
                    // ...
                }
            }
        }
        else{Toast.makeText(this,"Nothing Found",Toast.LENGTH_SHORT).show();}

        Resources res =getResources();
        adapter = new CustomAdapter3(z,x,res);
        v.setAdapter(adapter);

    }

    public void onItemClick(int position){

    }
}
