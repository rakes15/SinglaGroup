package com.singlagroup.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filterable;
import android.widget.TextView;

import com.singlagroup.datasets.CityDataset;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rakesh on 29-Sept-16.
 */
public class CityFilterableAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private static LayoutInflater inflater=null;
    ArrayList<CityDataset> data=null;
    ArrayList<CityDataset> data2=null;
    public CityFilterableAdapter(Context context, ArrayList<CityDataset> data, ArrayList<CityDataset> data2) {
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
        convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item,parent, false);
        CityDataset dataset = data.get(position);
        if (dataset != null) {
            TextView customerNameLabel = (TextView) convertView.findViewById(android.R.id.text1);
            if (customerNameLabel != null) {
                customerNameLabel.setTag(dataset.getCityID());
                customerNameLabel.setText(dataset.getCityName());
            }
        }
        return convertView;
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

                List<CityDataset> Filtered_Names = new ArrayList<CityDataset>();
                String filterString = constraint.toString().toLowerCase();
                String filterableString;

                for(int i = 0; i<data.size(); i++){
                    CityDataset searchdata = data.get(i);
                    String DocNo = searchdata.getCityName();
                    filterableString = DocNo;
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
                        data = new ArrayList<CityDataset>((List<CityDataset>) results.values) ;
                    }
                    else
                    {
                        data = new ArrayList<CityDataset>() ;
                    }
                }
                notifyDataSetChanged();
            }

        };
    }
}
