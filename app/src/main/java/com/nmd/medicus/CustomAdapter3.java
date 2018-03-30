package com.nmd.medicus;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by USER on 31-03-2018.
 */

public class CustomAdapter3 extends BaseAdapter implements View.OnClickListener {

    private Activity activity;
    private ArrayList data;
    private static LayoutInflater inflater=null;
    public Resources res;
    AppointmentModel tempValues=null;
    int i=0;

    public CustomAdapter3(Activity a, ArrayList d,Resources resLocal) {

        /********** Take passed values **********/
        activity = a;
        data=d;
        res = resLocal;

        /***********  Layout inflator to call external xml layout () ***********/
        inflater = ( LayoutInflater )activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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

        public TextView uid,time;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        CustomAdapter3.ViewHolder holder;

        if(convertView==null){

            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.single_appointment, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new CustomAdapter3.ViewHolder();
            holder.uid = (TextView) vi.findViewById(R.id.appt_uid);
            holder.time=(TextView) vi.findViewById(R.id.appt_time);

            /************  Set holder with LayoutInflater ************/
            vi.setTag( holder );
        }
        else
            holder=(CustomAdapter3.ViewHolder)vi.getTag();

        if(data.size()<=0)
        {
            holder.uid.setText("No Data");

        }
        else
        {
            /***** Get each Model object from Arraylist ********/
            tempValues=null;
            tempValues = ( AppointmentModel ) data.get( position );

            /************  Set Model values in Holder elements ***********/

            holder.uid.setText( tempValues.getUid() );
            holder.time.setText(tempValues.getTime().toString());
//            holder.image.setImageResource(R.mipmap.ic_launcher_round);

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

            sct.onItemClick(mPosition);
            /****  Call  onItemClick Method inside DoctorActivity Class ( See Below )****/
        }
    }

}
