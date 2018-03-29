package com.nmd.medicus;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class ProfileFragment extends Fragment {

    private TextView docName, docSpecialty, docContact, docEmail, docScore;
    private ImageView image;
    private FirebaseFirestore db;
    private String uid;

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        uid = getActivity().getIntent().getStringExtra("uid");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_profile, container, false);

        docName = (TextView)rootView.findViewById(R.id.docName);
        docSpecialty = (TextView)rootView.findViewById(R.id.docSpecialty);
        docContact = (TextView)rootView.findViewById(R.id.docContact);
        docEmail = (TextView)rootView.findViewById(R.id.docEmail);
        docScore = (TextView)rootView.findViewById(R.id.docScore);
        image = (ImageView)rootView.findViewById(R.id.docPhoto);

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
                                    docScore.setText(document.getData().get("score").toString());
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
