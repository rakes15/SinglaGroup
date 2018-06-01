package com.singlagroup.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filterable;
import android.widget.TextView;

import com.singlagroup.R;
import com.singlagroup.datasets.CityDataset;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Rakesh on 29-Sept-16.
 */
public class CommonSearchSpinnerFilterableAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private static LayoutInflater inflater=null;
    ArrayList<Map<String,String>> data=null, data2=null;
    public CommonSearchSpinnerFilterableAdapter(Context context, ArrayList<Map<String,String>> data, ArrayList<Map<String,String>> data2) {
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
    public View getView(int position, View convertView, ViewGroup parent) {

        // This a new view we inflate the new layout
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.cardview_spinner_item,parent, false);
        Map<String,String> map = data.get(position);
        if (map != null) {
            TextView customerNameLabel = (TextView) convertView.findViewById(R.id.textView);
            if (customerNameLabel != null) {
                customerNameLabel.setTag(map.get("ID"));
                if(map.get("Type")==null){
                    customerNameLabel.setText(map.get("Name"));
                }else {
                    customerNameLabel.setText(map.get("Name")+" - "+map.get("Type"));
                }
            }
        }
        return convertView;
    }
    public void setError(View view,CharSequence s) {
        if(view != null){
            TextView customerNameLabel = (TextView) view.findViewById(R.id.textView);
            customerNameLabel.setError(s);
        }
    }
    public android.widget.Filter getFilter() {

        return new android.widget.Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults Result = new FilterResults();
                // if constraint is empty return the original names
                data=data2;
                if(constraint.length() == 0 ){
                    Result.values = data;
                    Result.count = data.size();
                    Log.e("Results", Result.values.toString() + String.valueOf(Result.count));
                    return Result;
                }

                List<Map<String,String>> Filtered_Names = new ArrayList<Map<String,String>>();
                String filterString = constraint.toString().toLowerCase();
                String filterableString;

                for(int i = 0; i<data.size(); i++){
                    Map<String,String> searchdata = data.get(i);
                    String Name = searchdata.get("Name");
                    String Type = searchdata.get("Type");
                    filterableString = Name+" "+Type;
                    if(filterableString.toLowerCase().contains(filterString)){
                        Filtered_Names.add(data.get(i));
                        Log.e("Added", String.valueOf(Filtered_Names.size()));
                    }
                }
                Result.values = Filtered_Names;
                Result.count = Filtered_Names.size();
                Log.e("Results", Result.values.toString() + String.valueOf(Result.count));
                return Result;
            }
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if (results != null) {
                    if (results.count > 0) {
                        data = new ArrayList<Map<String,String>>((List<Map<String,String>>) results.values) ;
                    }
                    else
                    {
                        data = new ArrayList<Map<String,String>>() ;
                    }
                }
                notifyDataSetChanged();
            }

        };
    }
}
