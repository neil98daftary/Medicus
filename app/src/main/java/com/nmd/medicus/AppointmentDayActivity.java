package com.nmd.medicus;
//This code contains the functions which handle the
//list of appointments that a doctor will have.


import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class AppointmentDayActivity extends AppCompatActivity {

    private String selectedDay;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    private HashMap<String, HashMap<String, Date>> dates;
    private HashMap<String, Date> times;

    ListView list;
    CustomAdapter3 adapter;
    public  AppointmentDayActivity CustomListView = null;
    public ArrayList<AppointmentModel> CustomListViewValuesArr = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_day);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        selectedDay = getIntent().getStringExtra("currentDay");

        CustomListView = this;

        list= ( ListView )findViewById( R.id.list );

        setListData();
    }

    public void setListData() {
        db.collection("appointments")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getData().get("uid").equals(user.getUid().toString()) && document.getData().containsKey("dates")) {
                                    dates = (HashMap<String, HashMap<String, Date>>) document.getData().get("dates");
                                    times = dates.get(selectedDay);
                                    for (final String patientUid : times.keySet()) {
                                        db.collection("patients")
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                                if (document.getData().get("uid").equals(patientUid)) {
                                                                    String name = document.getData().get("name").toString();
                                                                    String email = document.getData().get("email").toString();
                                                                    String image = document.getData().get("image").toString();
//                                                                    Log.v("tag1", "ENTEREDDDDD");
                                                                    CustomListViewValuesArr.add(new AppointmentModel(name, email, image, patientUid, selectedDay));
                                                                }
                                                            }
                                                            Resources res =getResources();
                                                            adapter=new CustomAdapter3( CustomListView, CustomListViewValuesArr,res );
                                                            list.setAdapter(adapter);
                                                        }
                                                    }
                                                });
                                    }
                                }
                            }
                        } else {
                            Log.w("tag1", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void onItemClick(int mPosition)
    {
        final AppointmentModel tempValues = ( AppointmentModel ) CustomListViewValuesArr.get(mPosition);

        db.collection("patients").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getData().get("uid").equals(tempValues.getUid())){
                                    Intent i = new Intent(AppointmentDayActivity.this,PatientDetails.class);
                                    i.putExtra("name",document.getData().get("name").toString());
                                    i.putExtra("email",document.getData().get("email").toString());
                                    i.putExtra("image",document.getData().get("image").toString());
                                    i.putExtra("phone",document.getData().get("phone").toString());
                                    i.putExtra("address",document.getData().get("address").toString());
                                    startActivity(i);
                                }
                            }
                        }
                        else {
                            Log.w("tag1", "Error getting documents.", task.getException());
                        }
                    }
                });


        Toast.makeText(CustomListView,""+tempValues.getName(),Toast.LENGTH_LONG).show();
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + tempValues.getEmail().toString()));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Regarding your appoinment");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Dear " + tempValues.getName().toString() + " this to inform you that your appoinment has been succesfully conducted");
        startActivity(Intent.createChooser(emailIntent, "Title"));


    }
}
