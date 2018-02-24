package com.nmd.medicus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.loopj.android.http.*;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class DoctorProfile extends AppCompatActivity {

    ListView listView;
    RequestParams requestParams;
    TextView quot;
    String jsonResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile);

        quot = (TextView)findViewById(R.id.quotient);

        listView = (ListView)findViewById(R.id.list);
        String[] values = new String[] { "Monday 11 am",
                "Tuesday 11 am",
                "Wednesday 11 am",
                "Thursday 11 am",
                "Friday 11 am",
                "Saturday 11 am",
                "Monday 09 am",
                "Monday 04 pm"
        };
        ArrayAdapter<String> doctorAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, values);
        listView.setAdapter(doctorAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String  itemValue    = (String) listView.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(), itemValue, Toast.LENGTH_SHORT).show();
            }
        });

        requestParams = new RequestParams();
        requestParams.put("sentence", "very good doctor");
        AsyncHttpClient client = new AsyncHttpClient();
//        client.post("https://medicus-api.herokuapp.com/get_rating/no_train", requestParams, new AsyncHttpResponseHandler(){
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                String doc = new String(responseBody);
//                quot.setText(doc);
//                Log.d("Response", doc);
//                Toast.makeText(getApplicationContext(), "Wow"+doc, Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
//            }
//        });
        client.post("https://medicus-api.herokuapp.com/get_rating/no_train", requestParams, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                jsonResponse = response.toString();
                Log.i("Works", "onSuccess: " + jsonResponse);
                quot.setText(jsonResponse);
                Toast.makeText(getApplicationContext(),"Success:"+jsonResponse,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getApplicationContext(), "Failed Again", Toast.LENGTH_SHORT).show();
                quot.setText("Failure");
            }
        });

    }
}
