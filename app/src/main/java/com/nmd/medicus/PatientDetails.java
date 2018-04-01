package com.nmd.medicus;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class PatientDetails extends AppCompatActivity {

    String name,email,image,phone,address;
    TextView det;
    CircleImageView x;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_details);

        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        image = getIntent().getStringExtra("image");
        phone = getIntent().getStringExtra("phone");
        address = getIntent().getStringExtra("address");

        det = (TextView) findViewById(R.id.patientDet);
        x = (CircleImageView) findViewById(R.id.patientPic);

        Glide.with(PatientDetails.this).load(image).into(x);

        det.setText(name + "\n" + email + "\n\n" + phone + "\n\n" + address);
    }

    public void sendMail(View view){
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Regarding your appoinment");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Dear " + name + " this to inform you");
        startActivity(Intent.createChooser(emailIntent, "Title"));
    }
}
