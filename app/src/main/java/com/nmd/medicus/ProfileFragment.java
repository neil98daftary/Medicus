package com.nmd.medicus;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class ProfileFragment extends Fragment {

    private TextView docName, docSpecialty, docContact, docEmail, docScore;
    private ImageView image;
    private EditText review;
    private Button submitButton;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private String uid;
    private RequestParams requestParams;
    private String jsonResponse;
    private ProgressBar progressBar;

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user =firebaseAuth.getCurrentUser();
        uid = getActivity().getIntent().getStringExtra("uid");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView =  inflater.inflate(R.layout.fragment_profile, container, false);

        docName = (TextView)rootView.findViewById(R.id.docName);
        docSpecialty = (TextView)rootView.findViewById(R.id.docSpecialty);
        docContact = (TextView)rootView.findViewById(R.id.docContact);
        docEmail = (TextView)rootView.findViewById(R.id.docEmail);
        docScore = (TextView)rootView.findViewById(R.id.docScore);
        image = (ImageView)rootView.findViewById(R.id.docPhoto);
        review = ((TextInputLayout)rootView.findViewById(R.id.review)).getEditText();
        submitButton = (Button)rootView.findViewById(R.id.submitButton);
        review.setVisibility(View.INVISIBLE);
        submitButton.setVisibility(View.INVISIBLE);
        progressBar = (ProgressBar)rootView.findViewById(R.id.progressBar);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.GONE);

        db.collection("patients")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getData().get("uid").equals(user.getUid().toString())) {
                                    ArrayList<String> approvedDoctors = (ArrayList<String>) document.getData().get("approvedDoctors");
                                    if(approvedDoctors != null && approvedDoctors.contains(uid)) {
                                        review.setVisibility(View.VISIBLE);
                                        submitButton.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        }
                    }
                });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestParams = new RequestParams();
                progressBar.setVisibility(View.VISIBLE);
                requestParams.put("sentence", review.getText());
                AsyncHttpClient client = new AsyncHttpClient();
                client.post("https://medicus-api.herokuapp.com/get_rating/no_train", requestParams, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, final JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        progressBar.setVisibility(View.GONE);
                        jsonResponse = response.toString();
                        review.setText("");
                        db.collection("doctors")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if(task.isSuccessful()) {
                                            for(QueryDocumentSnapshot document : task.getResult()) {
                                                if(document.getData().get("uid").equals(uid)) {
                                                    String newScore = null;
                                                    String newTotal = null;
                                                    try {
                                                        newScore = Double.toString(Double.parseDouble(document.getData().get("score").toString()) + Double.parseDouble(response.get("rating").toString()));
                                                        newTotal = Double.toString(Double.parseDouble(document.getData().get("totalReviews").toString()) + 1);
                                                        docScore.setText(String.format("%.2f", (Double.parseDouble(newScore) / Double.parseDouble(newTotal)) * 5));
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }

                                                    DocumentReference doc = db.collection("doctors").document(document.getId());

                                                    doc.update("score", newScore);
                                                    doc.update("totalReviews", newTotal)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Toast.makeText(rootView.getContext(), "Thank you", Toast.LENGTH_SHORT).show();
                                                                    review.setVisibility(View.INVISIBLE);
                                                                    submitButton.setVisibility(View.INVISIBLE);
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(rootView.getContext(), "Failed", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });

                                                    db.collection("patients")
                                                            .get()
                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                    if(task.isSuccessful()) {
                                                                        for(QueryDocumentSnapshot document : task.getResult()) {
                                                                            if(document.getData().get("uid").toString().equals(user.getUid().toString())) {
                                                                                String key = document.getId();
                                                                                HashMap<String, Object> updateObject = new HashMap<>();
                                                                                updateObject.put("approvedDoctors", FieldValue.delete());
                                                                                db.collection("patients").document(key)
                                                                                        .update(updateObject)
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
                                                                        }
                                                                    }
                                                                }
                                                            });
                                                }
                                            }
                                        }
                                    }
                                });
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Toast.makeText(rootView.getContext(), "Failed Again", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        db.collection("doctors")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getData().get("uid").toString().equals(uid)) {
                                    docName.setText(document.getData().get("name").toString());
                                    docSpecialty.setText(document.getData().get("specialty").toString());
                                    docContact.setText(document.getData().get("contact").toString());
                                    docEmail.setText(document.getData().get("email").toString());
                                    if(Double.parseDouble(document.getData().get("totalReviews").toString()) != 0) {
                                        docScore.setText(String.format("%.2f", (Double.parseDouble(document.getData().get("score").toString()) / Double.parseDouble(document.getData().get("totalReviews").toString())) * 5));
                                    }
                                    else{
                                        docScore.setText("This Doctor has not been reviewed yet");
                                    }
                                    Picasso.get()
                                            .load(document.getData().get("image").toString())
                                            .into(image);
                                    break;
                                }
                            }
                        } else {
                            Log.w("tag1", "Error getting documents.", task.getException());
                        }
                    }
                });

        return rootView;
    }

}
