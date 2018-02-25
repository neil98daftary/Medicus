package com.nmd.medicus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private RadioGroup radioGroup;
    static RadioButton rb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.clearCheck();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb && checkedId > -1) {
                    Toast.makeText(LoginActivity.this, rb.getText(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void login(View view)
    {
        if (rb.getText().equals("Patient"))
        {
            Intent myIntent = new Intent(this, MainActivity.class);
            LoginActivity.this.startActivity(myIntent);
        }
        else
        {
            Intent myIntent = new Intent(this, CustomListViewAndroidExample.class);
            LoginActivity.this.startActivity(myIntent);
        }
    }
}
