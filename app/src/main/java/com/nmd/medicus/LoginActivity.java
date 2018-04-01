package com.nmd.medicus;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.firebase.ui.auth.ui.*;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {

    private RadioGroup radioGroup;
    static RadioButton rb;
    TextView name;
    private Button continueButton;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    FirebaseUser user;
    private FirebaseFirestore db;

    private int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.setVisibility(View.INVISIBLE);
        radioGroup.clearCheck();
        continueButton = (Button)findViewById(R.id.continueButton);
        continueButton.setVisibility(View.INVISIBLE);

        mFirebaseAuth = FirebaseAuth.getInstance();

        name = (TextView) findViewById(R.id.username);

        db = FirebaseFirestore.getInstance();


        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {


                    name.setText(user.getDisplayName() + "\n" + user.getEmail() + "\n" + user.getUid());

                    // Code to save userdata
//                    Log.v("Userdetails",user.getDisplayName()+" "+user.getEmail());


                    db.collection("users")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            if(document.getData().get("uid").equals(user.getUid())) {
                                                if(document.getData().get("type").equals("doctor")) {
                                                    Toast.makeText(LoginActivity.this, "Already registered as a doctor.", Toast.LENGTH_SHORT).show();
                                                    db.collection("doctors")
                                                            .get()
                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                                            if(document.getData().get("uid").equals(user.getUid())) {
                                                                                Intent i = new Intent(LoginActivity.this, DoctorAppointments.class);
                                                                                startActivity(i);
                                                                            }
//                                                                            Log.d("tag1", document.getId() + " => " + document.getData().get("type"));
                                                                        }
//                                                                        Intent i = new Intent(LoginActivity.this, DoctorProfile.class);
//                                                                        startActivity(i);
                                                                    } else {
                                                                        Log.w("tag1", "Error getting documents.", task.getException());
                                                                    }
                                                                }
                                                            });
                                                }
                                                else if(document.getData().get("type").equals("patient")) {
                                                    Intent i = new Intent(LoginActivity.this, CustomListViewAndroidExample.class);
                                                    startActivity(i);
                                                }
                                            }
//                                            Log.d("tag1", document.getId() + " => " + document.getData().get("type"));
                                        }
                                    } else {
                                        Log.w("tag1", "Error getting documents.", task.getException());
                                    }
                                }
                            });

                }
                else {
                    // User is signed out
                    List<AuthUI.IdpConfig> providers = Arrays.asList(
                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build());

                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(providers)
                                    .setLogo(R.mipmap.ic_launcher)
                                    .build(),
                            0);
                }
            }
        };


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb && checkedId > -1) {
                    continueButton.setVisibility(View.VISIBLE);
                }
            }
        });

//        ActivityCompat.requestPermissions(LoginActivity.this,
//                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
//                1);



//        int PERMISSION_ALL = 1;
//        String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
//
//        if(!hasPermissions(this, PERMISSIONS)){
//            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
//        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                // Sign-in succeeded, set up the UI
                //String x = "Signed In as" + user.getDisplayName().toString();
                //Toast.makeText(this, "Signed In", Toast.LENGTH_SHORT).show();
                user = mFirebaseAuth.getCurrentUser();
                name.setText(user.getDisplayName() + "\n" + user.getEmail() + "\n" + user.getUid());

                db.collection("users")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        if(document.getData().get("uid").equals(user.getUid())) {
                                            if(document.getData().get("type").equals("doctor")) {
                                                Toast.makeText(LoginActivity.this, "Already registered as a doctor.", Toast.LENGTH_SHORT).show();
                                                flag = 1;
                                            }
                                            else if(document.getData().get("type").equals("patient")) {
                                                Toast.makeText(LoginActivity.this, "Already registered as a patient.", Toast.LENGTH_SHORT).show();
                                                flag = 1;
                                            }
                                        }
//                                        Log.d("tag1", document.getId() + " => " + document.getData().get("type"));
                                    }
                                    if(flag == 0) {
                                        radioGroup.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    Log.w("tag1", "Error getting documents.", task.getException());
                                }
                            }
                        });

            } else if (resultCode == RESULT_CANCELED) {
                // Sign in was canceled by the user, finish the activity
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                finishAffinity();
                System.exit(0);
            }
        }
    }

    public void login(View view)
    {
        user = mFirebaseAuth.getCurrentUser();

        final Map<String, Object> userObject = new HashMap<>();
        userObject.put("uid", user.getUid());

        if (rb.getText().equals("Doctor"))
        {
            userObject.put("type", "doctor");
            db.collection("users")
                    .add(userObject)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Intent myIntent = new Intent(LoginActivity.this, DoctorProfile.class);
                            LoginActivity.this.startActivity(myIntent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("tag1", "Error adding document", e);
                        }
                    });
        }
        else if(rb.getText().equals("Patient"))
        {
            userObject.put("type", "patient");
            db.collection("users")
                    .add(userObject)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(LoginActivity.this, "Already registered as a patient.", Toast.LENGTH_SHORT).show();
                            Intent myIntent = new Intent(LoginActivity.this, PatientProfile.class);
                            LoginActivity.this.startActivity(myIntent);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("tag1", "Error adding document", e);
                        }
                    });
        }
//        else {
//            Toast.makeText(LoginActivity.this, "Please choose a profile type.", Toast.LENGTH_SHORT).show();
//        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(LoginActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

//    public void logout(View view){
//        AuthUI.getInstance()
//                .signOut(LoginActivity.this)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    public void onComplete(@NonNull Task<Void> task) {
//                        name.setText("");
//                    }
//                });
//
//        radioGroup.setVisibility(View.INVISIBLE);
//    }
}
