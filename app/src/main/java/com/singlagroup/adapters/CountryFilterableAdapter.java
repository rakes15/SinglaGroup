package com.singlagroup.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import com.singlagroup.datasets.CountryDataset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rakesh on 19-May-16.
 */
public class CountryFilterableAdapter extends BaseAdapter implements Filterable {
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<CountryDataset> items;
    private ArrayList<CountryDataset> items2;
    public CountryFilterableAdapter(Context context, ArrayList<CountryDataset> items, ArrayList<CountryDataset> items2) {
        this.context = context;
        this.items = items;
        this.items2 = items2;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        txt.setText(items.get(position).getCountryName());
        if(position == 0){
            // Set the hint text color gray
            txt.setTextColor(Color.GRAY);
        }
        else {
            txt.setTextColor(Color.BLACK);
        }
        return  txt;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item,parent, false);
        CountryDataset dataset = items.get(position);
        if (dataset != null) {
            TextView customerNameLabel = (TextView) convertView.findViewById(android.R.id.text1);
            if (customerNameLabel != null) {
                customerNameLabel.setTag(dataset.getCountryID());
                customerNameLabel.setText(dataset.getCountryName());
            }
        }
        return convertView;
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return items.size();
    }
    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return items.get(position);
    }
    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }
    public Filter getFilter() {

        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults Result = new FilterResults();
                // if constraint is empty return the original names
                items=items2;
                if(constraint.length() == 0 ){
                    Result.values = items;
                    Result.count = items.size();
                    Log.e("Results", Result.values.toString() + String.valueOf(Result.count));
                    return Result;
                }

                List<CountryDataset> Filtered_Names = new ArrayList<CountryDataset>();
                String filterString = constraint.toString().toLowerCase();
                String filterableString;

                for(int i = 0; i<items.size(); i++){
                    CountryDataset searchdata = items.get(i);
                    String DocNo = searchdata.getCountryName();
                    filterableString = DocNo;
                    if(filterableString.toLowerCase().contains(filterString)){
                        Filtered_Names.add(items.get(i));
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
                        items = new ArrayList<CountryDataset>((List<CountryDataset>) results.values) ;
                    }
                    else
                    {
                        items = new ArrayList<CountryDataset>() ;
                    }
                }
                notifyDataSetChanged();
            }

        };
    }

}