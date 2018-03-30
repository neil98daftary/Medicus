package com.nmd.medicus;

import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class DoctorActivity extends AppCompatActivity {

    ListView list;
    CustomAdapter2 adapter;
    public  DoctorActivity CustomListView = null;
    public ArrayList<ListModel> CustomListViewValuesArr = new ArrayList<ListModel>();
    private String specialty;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor);

        db = FirebaseFirestore.getInstance();

        CustomListView = this;

        specialty = getIntent().getStringExtra("specialty");
//        Log.v("tag1", specialty);

        setListData();

//        Resources res =getResources();
        list= ( ListView )findViewById( R.id.list );  // List defined in XML ( See Below )

        /**************** Create Custom Adapter *********/
//        adapter=new CustomAdapter( CustomListView, CustomListViewValuesArr,res );
//        list.setAdapter( adapter );
    }

    public void setListData() {
        db.collection("doctors")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.v("tag1", document.getData().get("specialty").toString());
                                if(document.getData().get("specialty") == null){continue;}
                                if(document.getData().get("specialty").toString().equals(specialty)) {
                                    CustomListViewValuesArr.add(new ListModel(document.getData().get("name").toString(), document.getData().get("image").toString(), document.getData().get("uid").toString()));
                                }
                            }
                            Resources res =getResources();
                            adapter=new CustomAdapter2( CustomListView, CustomListViewValuesArr,res );
                            list.setAdapter( adapter );
                        } else {
                            Log.w("tag1", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void onItemClick(int mPosition)
    {
        ListModel tempValues = ( ListModel ) CustomListViewValuesArr.get(mPosition);

        Toast.makeText(CustomListView,""+tempValues.getName(),Toast.LENGTH_LONG).show();
        Intent myIntent = new Intent(getApplicationContext(), DoctorProfilePatient.class);
        myIntent.putExtra("uid", tempValues.getId());
        DoctorActivity.this.startActivity(myIntent);
    }

    public void onBackPressed(){
        Intent myIntent = new Intent(this, CustomListViewAndroidExample.class);
        DoctorActivity.this.startActivity(myIntent);
    }
}

//String  itemValue    = (String) listView.getItemAtPosition(position);
//Toast.makeText(getApplicationContext(), itemValue, Toast.LENGTH_SHORT).show();
//Intent myIntent = new Intent(getApplicationContext(), DoctorProfilePatient.class);
//DoctorActivity.this.startActivity(myIntent);
//finish();