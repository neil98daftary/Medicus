package com.nmd.medicus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by adityadesai on 31/03/18.
 */

public class CustomAdapter3 extends BaseAdapter implements View.OnClickListener {
    private Activity activity;
    private ArrayList data;
    private static LayoutInflater inflater=null;
    public Resources res;
    AppointmentModel tempValues=null;
    int i=0;

    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    public CustomAdapter3(Activity a, ArrayList d,Resources resLocal) {

        /********** Take passed values **********/
        activity = a;
        data=d;
        res = resLocal;

        /***********  Layout inflator to call external xml layout () ***********/
        inflater = ( LayoutInflater )activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
    }

    @Override
    public void onClick(View v) {
        Log.v("CustomAdapter", "=====Row button clicked=====");
    }

    @Override
    public int getCount() {
        if(data.size()<=0)
            return 1;
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder{

        public TextView text;
        public ImageView image;
        public Button approveButton;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        CustomAdapter3.ViewHolder holder;

        if(convertView==null){

            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.appointment_item, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new CustomAdapter3.ViewHolder();
            holder.text = (TextView) vi.findViewById(R.id.text);
            holder.image=(ImageView)vi.findViewById(R.id.image);
            holder.approveButton = (Button)vi.findViewById(R.id.approveButton);

            /************  Set holder with LayoutInflater ************/
            vi.setTag( holder );
        }
        else
            holder=(CustomAdapter3.ViewHolder)vi.getTag();

        if(data.size()<=0)
        {
            holder.text.setText("No Data");

        }
        else
        {
            /***** Get each Model object from Arraylist ********/
            tempValues=null;
            tempValues = ( AppointmentModel ) data.get( position );

            /************  Set Model values in Holder elements ***********/

            holder.text.setText( tempValues.getName() );
//            holder.image.setImageResource(R.mipmap.ic_launcher_round);
            Picasso.get()
                    .load(tempValues.getImage())
                    .into(holder.image);

            final String nowUid = tempValues.getUid();
            final String nowDay = tempValues.getDay();



            holder.approveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    db.collection("patients")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()) {
                                        for(QueryDocumentSnapshot document : task.getResult()) {
                                            if(document.getData().get("uid").equals(nowUid)) {
                                                Map<String, ArrayList<String>> patientobject = new HashMap<>();
                                                ArrayList<String> approvedDoctors;
                                                if(document.getData().containsKey("approvedDoctors")) {
                                                    approvedDoctors = (ArrayList<String>)document.getData().get("approvedDoctors");
                                                }
                                                else {
                                                    approvedDoctors = new ArrayList<>();
                                                }
                                                approvedDoctors.add(user.getUid().toString());
                                                patientobject.put("approvedDoctors", approvedDoctors);
                                                db.collection("patients").document(document.getId())
                                                        .update("approvedDoctors", approvedDoctors)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                db.collection("appointments")
                                                                        .get()
                                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                if(task.isSuccessful()) {
                                                                                    for(QueryDocumentSnapshot document : task.getResult()) {
                                                                                        if(document.getData().get("uid").equals(user.getUid().toString())) {
                                                                                            HashMap<String, HashMap<String, Date>> dates = (HashMap<String, HashMap<String, Date>>) document.getData().get("dates");
                                                                                            HashMap<String, Date> times = dates.get(nowDay);
                                                                                            times.remove(nowUid);
                                                                                            Map<String, Object> updateObject = new HashMap<>();
                                                                                            if(times.isEmpty()) {
                                                                                                updateObject.put("dates", FieldValue.delete());
                                                                                                db.collection("appointments").document(document.getId())
                                                                                                        .update(updateObject)
                                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onSuccess(Void aVoid) {
                                                                                                                Log.v("tag1", "Success");
                                                                                                                Intent i = new Intent(activity, DoctorAppointments.class);
                                                                                                                activity.startActivity(i);
                                                                                                            }
                                                                                                        })
                                                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                                                            @Override
                                                                                                            public void onFailure(@NonNull Exception e) {
                                                                                                                Log.v("tag1", e.toString());
                                                                                                            }
                                                                                                        });
                                                                                            }
                                                                                            else {
                                                                                                dates.put(nowDay, times);
                                                                                                db.collection("appointments").document(document.getId())
                                                                                                        .update("dates", dates)
                                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onSuccess(Void aVoid) {
                                                                                                                Log.v("tag1", "Success");
                                                                                                                Intent i = new Intent(activity, DoctorAppointments.class);
                                                                                                                activity.startActivity(i);
                                                                                                            }
                                                                                                        })
                                                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                                                            @Override
                                                                                                            public void onFailure(@NonNull Exception e) {
                                                                                                                Log.v("tag1", e.toString());
                                                                                                            }
                                                                                                        });
                                                                                            }
                                                                                            Log.v("tag1", dates.toString());
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        });
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
            });

            /******** Set Item Click Listner for LayoutInflater for each row *******/


            vi.setOnClickListener(new CustomAdapter3.OnItemClickListener( position ));
        }
        return vi;
    }

    private class OnItemClickListener  implements View.OnClickListener {
        private int mPosition;

        OnItemClickListener(int position){
            mPosition = position;
        }

        @Override
        public void onClick(View arg0) {

            AppointmentDayActivity sct = (AppointmentDayActivity) activity;

            /****  Call  onItemClick Method inside CustomListViewAndroidExample Class ( See Below )****/

            sct.onItemClick(mPosition);
        }
    }
}
