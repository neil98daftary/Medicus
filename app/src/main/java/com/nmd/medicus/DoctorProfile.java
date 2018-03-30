package com.nmd.medicus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.borax12.materialdaterangepicker.date.DatePickerDialog;
import com.borax12.materialdaterangepicker.time.RadialPickerLayout;
import com.borax12.materialdaterangepicker.time.TimePickerDialog;
import com.dpro.widgets.WeekdaysPicker;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.loopj.android.http.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class DoctorProfile extends AppCompatActivity implements AdapterView.OnItemSelectedListener, TimePickerDialog.OnTimeSetListener {

    ListView listView;
    RequestParams requestParams;
    TextView quot;
    String jsonResponse;
    ImageView imagepic;
    Bitmap bitmap;
    private GoogleApiClient mGoogleApiClient;
    private int PLACE_PICKER_REQUEST = 1;
    private EditText doc_name, doc_contact;
    private String location, startTime = "9:00", endTime = "21:00";
    private Spinner spinner;
    private WeekdaysPicker widget;
    private FirebaseFirestore db;
    SharedPreferences docData;
    SharedPreferences.Editor docEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile);

        docData = getSharedPreferences("Doctors", Context.MODE_PRIVATE);
        docEdit = docData.edit();

        db = FirebaseFirestore.getInstance();

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();

        imagepic = (ImageView)findViewById(R.id.image);

        doc_name = ((TextInputLayout)findViewById(R.id.doc_name)).getEditText();
        doc_contact = ((TextInputLayout)findViewById(R.id.doc_contact)).getEditText();

        // Spinner element
        spinner = (Spinner) findViewById(R.id.spinner);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        widget = (WeekdaysPicker) findViewById(R.id.weekdays);

        // Spinner Drop down elements
        ArrayList<String> categories = new ArrayList<String>();
        categories.add("GP");
        categories.add("Oncology");
        categories.add("Cardiology");
        categories.add("Paediatry");
        categories.add("Ophthalmology");
        categories.add("Neurology");
        categories.add("Dermatology");
        categories.add("Anesthesiology");
        categories.add("Allergy & Immunology");
        categories.add("Pathology");
        categories.add("Psychiatry");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

//        requestParams = new RequestParams();
//        requestParams.put("sentence", "very good doctor");
//        AsyncHttpClient client = new AsyncHttpClient();
//        client.post("https://medicus-api.herokuapp.com/get_rating/no_train", requestParams, new JsonHttpResponseHandler(){
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                super.onSuccess(statusCode, headers, response);
//                jsonResponse = response.toString();
//                Log.i("Works", "onSuccess: " + jsonResponse);
//                quot.setText(jsonResponse);
//                Toast.makeText(getApplicationContext(),"Success:"+jsonResponse,Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                Toast.makeText(getApplicationContext(), "Failed Again", Toast.LENGTH_SHORT).show();
//                quot.setText("Failure");
//            }
//        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void selectpic(View view){
        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, 2);
    }

    public void selectloc(View view){
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(DoctorProfile.this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    public void done(View v) {
        if(!doc_name.getText().toString().equals("") && !doc_contact.getText().toString().equals("") && (location != null)) {
            FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mFirebaseAuth.getCurrentUser();

            Map<String, Object> doctorAppointmentObject = new HashMap<>();
            doctorAppointmentObject.put("uid", user.getUid().toString());
            doctorAppointmentObject.put("startTime", startTime);
            doctorAppointmentObject.put("endTime", endTime);
            db.collection("appointments").document(user.getUid()).set(doctorAppointmentObject).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

            Map<String, Object> doctorObject = new HashMap<>();
            doctorObject.put("uid", user.getUid().toString());
            doctorObject.put("name", doc_name.getText().toString());
            doctorObject.put("contact", doc_contact.getText().toString());
            doctorObject.put("email", user.getEmail().toString());
            doctorObject.put("location", location);
            doctorObject.put("specialty", spinner.getSelectedItem().toString());
            doctorObject.put("image", user.getPhotoUrl().toString());
            doctorObject.put("score", "0");
            doctorObject.put("days",TextUtils.join("," , widget.getSelectedDaysText()));
            doctorObject.put("startTime", startTime);
            doctorObject.put("endTime", endTime);

            docEdit.putString("uid",user.getUid().toString());
            docEdit.putString("name",doc_name.getText().toString());
            docEdit.putString("contact",doc_contact.getText().toString());
            docEdit.putString("specialty",spinner.getSelectedItem().toString());
            docEdit.commit();
            db.collection("doctors")
                    .add(doctorObject)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Intent i = new Intent(DoctorProfile.this, DoctorAppointments.class);
                            startActivity(i);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }
        else {
            Toast.makeText(DoctorProfile.this, "Please enter all details", Toast.LENGTH_SHORT).show();
        }
    }

    public void openrange(View v) {
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                DoctorProfile.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false
        );
        tpd.show(getFragmentManager(), "Timepickerdialog");
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int hourOfDayEnd, int minuteEnd) {
        String hourString = hourOfDay < 10 ? "0"+hourOfDay : ""+hourOfDay;
        String minuteString = minute < 10 ? "0"+minute : ""+minute;
        String hourStringEnd = hourOfDayEnd < 10 ? "0"+hourOfDayEnd : ""+hourOfDayEnd;
        String minuteStringEnd = minuteEnd < 10 ? "0"+minuteEnd : ""+minuteEnd;
        String time = "You picked the following time: From - "+hourString+"h"+minuteString+" To - "+hourStringEnd+"h"+minuteStringEnd;
        startTime = hourString + ":" + minuteString;
        endTime = hourStringEnd + ":" + minuteStringEnd;
//        Log.v("tag1", time);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == RESULT_OK
                && null != data) {

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            bitmap = BitmapFactory.decodeFile(picturePath);
            imagepic.setImageBitmap(bitmap);

//            if (bitmap != null) {
////                ImageView rotate = (ImageView) findViewById(R.id.rotate);
//
//            }
//
//        } else {
//
//            Log.i("SonaSys", "resultCode: " + resultCode);
//            switch (resultCode) {
//                case 0:
//                    Log.i("SonaSys", "User cancelled");
//                    break;
//                case -1:
////                    onPhotoTaken();
//                    break;
//
//            }

        }
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place selectedPlace = PlacePicker.getPlace(this, data);
                // Do something with the place
                location = selectedPlace.getLatLng().latitude + "," + selectedPlace.getLatLng().longitude;
                Log.d("Place: ", selectedPlace.getLatLng().toString());
            }
        }
    }



}
