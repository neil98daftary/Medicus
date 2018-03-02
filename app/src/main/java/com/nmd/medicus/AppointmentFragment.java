package com.nmd.medicus;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class AppointmentFragment extends ListFragment {

    ListView listView;

    public static AppointmentFragment newInstance() {
        AppointmentFragment fragment = new AppointmentFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        listView = (ListView)getView().findViewById(R.id.listView);
//        String[] values = new String[] { "Monday 11 am",
//                "Tuesday 11 am",
//                "Wednesday 11 am",
//                "Thursday 11 am",
//                "Friday 11 am",
//                "Saturday 11 am",
//                "Monday 09 am",
//                "Monday 04 pm"
//        };
//        ArrayAdapter<String> doctorAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, values);
//        listView.setAdapter(doctorAdapter);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String  itemValue    = (String) listView.getItemAtPosition(position);
//                Toast.makeText(getActivity(), itemValue, Toast.LENGTH_SHORT).show();
//            }
//        });
//        return inflater.inflate(R.layout.fragment_appointment, container, false);
//    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
                "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
                "Linux", "OS/2" };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);
    }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            // TODO implement some logic
        }
}
