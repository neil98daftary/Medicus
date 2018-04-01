package com.nmd.medicus;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PatientProfile extends AppCompatActivity {

    EditText phone,address;
    TextView det;
    FirebaseUser user;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_profile);
        det = (TextView) findViewById(R.id.loginDet);
        phone = (EditText) findViewById(R.id.patientContact);
        address = (EditText) findViewById(R.id.patientAddress);

        user = FirebaseAuth.getInstance().getCurrentUser();
        det.setText(user.getDisplayName() + "\n" + user.getEmail());
        db = FirebaseFirestore.getInstance();
    }

    public void registerPatient(View view){

        if(phone.getText().toString().equals("") || address.getText().toString().equals("")){
            Toast.makeText(PatientProfile.this,"Please add proper details",Toast.LENGTH_SHORT).show();
            return;
        }


        Map<String, Object> patientObject = new HashMap<>();
        patientObject.put("uid", user.getUid().toString());
        patientObject.put("name", user.getDisplayName());
        patientObject.put("email", user.getEmail().toString());
        patientObject.put("image", user.getPhotoUrl().toString());
        patientObject.put("phone",phone.getText().toString());
        patientObject.put("address",address.getText().toString());

        db.collection("patients")
                .add(patientObject)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Intent myIntent = new Intent(PatientProfile.this, CustomListViewAndroidExample.class);
                        PatientProfile.this.startActivity(myIntent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }
}
