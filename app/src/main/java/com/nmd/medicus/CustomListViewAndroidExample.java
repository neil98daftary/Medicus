package com.nmd.medicus;

import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class CustomListViewAndroidExample extends AppCompatActivity {

    ListView list;
    CustomAdapter adapter;
    public  CustomListViewAndroidExample CustomListView = null;
    public  ArrayList<ListModel> CustomListViewValuesArr = new ArrayList<ListModel>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_list_view_android_example);

        CustomListView = this;

        setListData();

        Resources res =getResources();
        list= ( ListView )findViewById( R.id.list );  // List defined in XML ( See Below )

        /**************** Create Custom Adapter *********/
        adapter=new CustomAdapter( CustomListView, CustomListViewValuesArr,res );
        list.setAdapter( adapter );
    }

    public void setListData()
    {

        CustomListViewValuesArr.add(new ListModel("GP", "https://s-i.huffpost.com/gen/1358280/images/o-GENERAL-PRACTITIONER-facebook.jpg", "0"));
        CustomListViewValuesArr.add(new ListModel("Oncology", "https://www.roche.com/dam/jcr:58413c25-9e21-492e-b25b-51c199a08a39/en/rd_oncology_stage.jpg", "1"));
        CustomListViewValuesArr.add(new ListModel("Cardiology", "http://ultrasourcemedical.com/wp-content/uploads/2017/05/cardiology.jpg", "2"));
        CustomListViewValuesArr.add(new ListModel("Paediatry", "https://cdn-thumbs.barewalls.com/happy-doctor-or-pediatrician-with-baby-at-clinic_bwc50131350.jpg", "3"));
        CustomListViewValuesArr.add(new ListModel("Ophthalmology", "https://www.news-medical.net/image.axd?picture=ophthalmologist%20eye%20exam%20-%20wavebreakmedia%20_thumb.jpg", "4"));
        CustomListViewValuesArr.add(new ListModel("Neurology", "https://www.bmc.org/sites/default/files/Patient%20Care%20Images/Neurology-307x287.jpg", "5"));
        CustomListViewValuesArr.add(new ListModel("Dermatology", "http://premierplasticsurgeryanddermatology.com/images/home_carousel/3a-c.jpg", "6"));
        CustomListViewValuesArr.add(new ListModel("Anesthesiology", "https://career.webindia123.com/career/options/images/anesthesia.jpg", "7"));
        CustomListViewValuesArr.add(new ListModel("Allergy & Immunology", "http://d1aueex22ha5si.cloudfront.net/Conference/219/Highlight/immunology-1504704035959.jpg", "8"));
        CustomListViewValuesArr.add(new ListModel("Pathology", "http://medschool.umaryland.edu/pathology/pathology03.jpg", "9"));
        CustomListViewValuesArr.add(new ListModel("Psychiatry", "https://gendertrender.files.wordpress.com/2012/10/psychiatry.jpg", "10"));

    }

    public void onItemClick(int mPosition)
    {
        ListModel tempValues = ( ListModel ) CustomListViewValuesArr.get(mPosition);

        Toast.makeText(CustomListView,""+tempValues.getName(),Toast.LENGTH_LONG).show();
        Intent myIntent = new Intent(getApplicationContext(), DoctorActivity.class);
        myIntent.putExtra("specialty", tempValues.getName());
        CustomListViewAndroidExample.this.startActivity(myIntent);
    }

    public void onBackPressed(){
        Intent myIntent = new Intent(this, LoginActivity.class);
        CustomListViewAndroidExample.this.startActivity(myIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sign_out: {
                AuthUI.getInstance()
                        .signOut(CustomListViewAndroidExample.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                startActivity(new Intent(CustomListViewAndroidExample.this, LoginActivity.class));
                            }
                        });
                break;
            }
        }
        return false;
    }
}
