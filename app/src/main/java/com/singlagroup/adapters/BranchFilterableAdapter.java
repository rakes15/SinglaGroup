package com.singlagroup.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filterable;
import android.widget.TextView;

import com.singlagroup.datasets.BranchDataset;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rakesh on 29-Sept-16.
 */
public class BranchFilterableAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private static LayoutInflater inflater=null;
    List<BranchDataset> data=null;
    List<BranchDataset> data2=null;
    public BranchFilterableAdapter(Context context, List<BranchDataset> data, List<BranchDataset> data2) {
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
//    @Override
//    public boolean isEnabled(int position){
//        if(position == 0)
//        {
//            // Disable the first item from Spinner
//            // First item will be use for hint
//            return false;
//        }
//        else
//        {
//            return true;
//        }
//    }
//    @Override
//    public View getDropDownView(int position, View convertView, ViewGroup parent) {
//        TextView txt = new TextView(context);
//        txt.setPadding(14, 14, 14, 14);
//        txt.setTextSize(18);
//        txt.setGravity(Gravity.CENTER_VERTICAL);
//        txt.setText(data.get(position).getBranchName());
//        if(position == 0){
//            // Set the hint text color gray
//            txt.setTextColor(Color.GRAY);
//        }
//        else {
//            txt.setTextColor(Color.BLACK);
//        }
//        return  txt;
//    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // This a new view we inflate the new layout
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item,parent, false);
        BranchDataset dataset = data.get(position);
        if (dataset != null) {
            TextView customerNameLabel = (TextView) convertView.findViewById(android.R.id.text1);
            if (customerNameLabel != null) {
                customerNameLabel.setTag(dataset.getBranchID());
                customerNameLabel.setText(dataset.getBranchName());
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

                List<BranchDataset> Filtered_Names = new ArrayList<BranchDataset>();
                String filterString = constraint.toString().toLowerCase();
                String filterableString;

                for(int i = 0; i<data.size(); i++){
                    BranchDataset searchdata = data.get(i);
                    String DocNo = searchdata.getBranchName();
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
                        data = new ArrayList<BranchDataset>((List<BranchDataset>) results.values) ;
                    }
                    else
                    {
                        data = new ArrayList<BranchDataset>() ;
                    }
                }
                notifyDataSetChanged();
            }

        };
    }
}
