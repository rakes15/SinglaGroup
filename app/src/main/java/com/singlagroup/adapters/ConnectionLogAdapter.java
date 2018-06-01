package com.singlagroup.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.singlagroup.BriefcaseSettingActivity;
import com.singlagroup.R;
import com.singlagroup.customwidgets.CustomTextView;
import com.singlagroup.customwidgets.DateFormatsMethods;
import com.singlagroup.customwidgets.MessageDialog;
import com.singlagroup.datasets.ModulePermissionDetailsDataset;
import com.singlagroup.datasets.connection.ConnectionLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DatabaseController.CommanStatic;
import DatabaseController.DatabaseSqlLiteHandlerUserInfo;
import me.grantland.widget.AutofitTextView;

/**
 * Created by Rakesh on 08-Oct-16.
 */
public class ConnectionLogAdapter extends RecyclerView.Adapter<ConnectionLogAdapter.RecyclerViewHolder> {

    private Context context;
    private RecyclerView recyclerView;
    private LayoutInflater inflater;
    private List<ConnectionLog> datasetList,filterDatasetList;
    private DatabaseSqlLiteHandlerUserInfo DBHandler;
    public ConnectionLogAdapter(Context context, List<ConnectionLog> datasetList) {
        this.context=context;
        this.datasetList=datasetList;
        this.filterDatasetList = new ArrayList<ConnectionLog>();
        this.filterDatasetList.addAll(this.datasetList);
        this.recyclerView=recyclerView;
        this.inflater = LayoutInflater.from(context);
        this.DBHandler=new DatabaseSqlLiteHandlerUserInfo(context);
    }
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.cardview_all_customer_list, parent, false);
        RecyclerViewHolder viewHolder=new RecyclerViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        ConnectionLog dataset = filterDatasetList.get(position);
        setTableLayout(dataset,holder.tableLayout);
    }
    @Override
    public int getItemCount() {

        return datasetList==null?0:datasetList.size();
    }
    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TableLayout tableLayout;
        public RecyclerViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.card_view);
            tableLayout = (TableLayout) itemView.findViewById(R.id.table_Layout);
        }
    }
    //TODO: Filter Search
    public void filter(final String text) {

        // Searching could be complex..so we will dispatch it to a different thread...
        new Thread(new Runnable() {
            @Override
            public void run() {

                // Clear the filter list
                filterDatasetList.clear();

                // If there is no search value, then add all original list items to filter list
                if (TextUtils.isEmpty(text)) {

                    filterDatasetList.addAll(datasetList);
                    Log.e("TAG","text:"+text);
                } else {
                    // Iterate in the original List and add it to filter list...
                    for (ConnectionLog item : datasetList) {
                        if(item.getImeiNo()!=null && item.getMacId()!=null && item.getModelNo()!=null && item.getType()!=null && item.getUserName()!=null && item.getUpdateDate()!=null) {
                            if (item.getImeiNo().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getMacId().toLowerCase().contains(text.toLowerCase())  ||
                                    item.getModelNo().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getType().toString().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getUserName().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getUpdateDate().toLowerCase().contains(text.toLowerCase())) {
                                // Adding Matched items
                                filterDatasetList.add(item);
                            }
                        }
                    }
                }

                // Set on UI Thread
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Notify the List that the DataSet has changed...
                        notifyDataSetChanged();
                    }
                });

            }
        }).start();

    }
    //TODO: Display TableLayout
    private void setTableLayout(ConnectionLog dataset, TableLayout tableLayout){

        //TODO: TableLayout set
        tableLayout.removeAllViewsInLayout();
        tableLayout.removeAllViews();
        //TODO: User Id
        View v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"User Id",(dataset.getUserName()==null || dataset.getUserName().equals("null") ? "":dataset.getUserName())));
        //TODO: Status
        v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"Status",(dataset.getConnectionStatus()==null || dataset.getConnectionStatus().equals("null") ? "":dataset.getConnectionStatus())));
        //TODO: Date
        v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"Date",(dataset.getUpdateDate()==null || dataset.getUpdateDate().equals("null") ? "": DateFormatsMethods.DateFormat_DD_MM_YYYY(dataset.getUpdateDate()))));
        //TODO: Model No
        v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"Model No",(dataset.getModelNo()==null || dataset.getModelNo().equals("null") ? "":dataset.getModelNo())));
        //TODO: Imei No
        v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"IMEI No",(dataset.getImeiNo()==null || dataset.getImeiNo().equals("null") ? "":dataset.getImeiNo())));
        //TODO: Mac Id
        v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"MAC Id",(dataset.getMacId()==null || dataset.getMacId().equals("null") ? "":dataset.getMacId())));
        //TODO: Type
        v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"Type",""+dataset.getType()));
    }
}
