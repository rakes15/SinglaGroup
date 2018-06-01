package com.singlagroup.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filterable;
import android.widget.TextView;

import com.singlagroup.datasets.DivisionEmpDataset;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Rakesh on 29-Sept-16.
 */
public class DivisionEmpFilterableAdapter extends BaseAdapter{

    private Context context;
    private static LayoutInflater inflater=null;
    ArrayList<Map<String,String>> data=null;
    ArrayList<Map<String,String>> data2=null;
    public DivisionEmpFilterableAdapter(Context context, ArrayList<Map<String,String>> data, ArrayList<Map<String,String>> data2) {
        this.context = context;
        this.data = data;
        this.data2 = data2;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.size();
    }
    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return data.get(position);
    }
    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }
    @Override
    public boolean isEnabled(int position){
        if(position == 0)
        {
            // Disable the first item from Spinner
            // First item will be use for hint
            return false;
        }
        else
        {
            return true;
        }
    }
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView txt = new TextView(context);
        txt.setPadding(14, 14, 14, 14);
        txt.setTextSize(18);
        txt.setGravity(Gravity.CENTER_VERTICAL);
        if(position == 0){
            // Set the hint text color gray
            txt.setText("Select "+data.get(position).get("CompanyName")+"("+data.get(position).get("DivisionName")+")");
            txt.setTextColor(Color.GRAY);
        }
        else {

            txt.setText(data.get(position).get("CompanyName")+"("+data.get(position).get("DivisionName")+")");
            txt.setTextColor(Color.BLACK);
        }
        return  txt;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // This a new view we inflate the new layout
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item,parent, false);
        Map<String,String> map = data.get(position);
        if (map != null) {
            TextView customerNameLabel = (TextView) convertView.findViewById(android.R.id.text1);
            if (customerNameLabel != null) {
                //customerNameLabel.setTag(map.getDivisionID());
                if(position == 0){
                    // Set the hint text color gray
                    customerNameLabel.setText("Select "+map.get("CompanyName")+"("+map.get("DivisionName")+")");
                }
                else {

                    customerNameLabel.setText(map.get("CompanyName")+"("+map.get("DivisionName")+")");
                }

            }
        }
        return convertView;
    }

}
