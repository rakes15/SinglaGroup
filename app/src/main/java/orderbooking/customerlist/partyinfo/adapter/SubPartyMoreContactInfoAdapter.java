package orderbooking.customerlist.partyinfo.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import com.singlagroup.R;
import com.singlagroup.customwidgets.DateFormatsMethods;

import java.util.ArrayList;
import java.util.List;

import orderbooking.Database_Sqlite.DatabaseSqlLiteHandlerPartyInfo;
import orderbooking.customerlist.partyinfo.model.SubPartyCompleteInfo;

/**
 * Created by Rakesh on 08-Oct-16.
 */
public class SubPartyMoreContactInfoAdapter extends RecyclerView.Adapter<SubPartyMoreContactInfoAdapter.RecyclerViewHolder>{

    private Context context;
    private ProgressDialog progressDialog;
    private LayoutInflater inflater;
    private String Title;
    private DatabaseSqlLiteHandlerPartyInfo partyInfo;
    private List<SubPartyCompleteInfo.ContactInfo>  PartyMoreContactInfoList,filterPartyMoreContactInfoList;
    private List<SubPartyCompleteInfo.PendingOrderInfo>  PendingOrderInfoList,filterPendingOrderInfoList;
    private static String TAG = SubPartyMoreContactInfoAdapter.class.getSimpleName();
    public SubPartyMoreContactInfoAdapter(Context context, String Title , SubPartyCompleteInfo.Result result) {
        this.context=context;
        this.Title=Title;
        this.partyInfo = new DatabaseSqlLiteHandlerPartyInfo(context);
        if (Title.equals("More Contacts")){
            this.PartyMoreContactInfoList = result.getBasicInfo().getContactInfo();
            this.filterPartyMoreContactInfoList = new ArrayList<>();
            this.filterPartyMoreContactInfoList.addAll(this.PartyMoreContactInfoList);
        }else if (Title.equals("Pending Orders")){
            this.PendingOrderInfoList = result.getPendingOrderInfo();
            this.filterPendingOrderInfoList = new ArrayList<>();
            this.filterPendingOrderInfoList.addAll(this.PendingOrderInfoList);
        }
        this.inflater = LayoutInflater.from(context);
        this.progressDialog = new ProgressDialog(this.context);
        this.progressDialog.setMessage("Please wait...");
        this.progressDialog.setCanceledOnTouchOutside(false);
    }
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.cardview_table_layout_horizontal_scroll, parent, false);
        RecyclerViewHolder viewHolder=new RecyclerViewHolder(v);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {

        if (Title.equals("More Contacts")){
            SubPartyCompleteInfo.ContactInfo dataset = filterPartyMoreContactInfoList.get(position);
            holder.cardView.setTag(dataset);
            //TODO: call TableLayout method
            setTableLayoutParty(dataset, holder.tableLayout);
        }else if (Title.equals("Pending Orders")){
            SubPartyCompleteInfo.PendingOrderInfo dataset = filterPendingOrderInfoList.get(position);
            holder.cardView.setTag(dataset);
            //TODO: call TableLayout method
            setTableLayoutSubParty(dataset, holder.tableLayout);
        }
    }
    @Override
    public int getItemCount() {
        if (Title.equals("More Contacts")){
            return (null != filterPendingOrderInfoList ? filterPartyMoreContactInfoList.size() : 0);
        }else if (Title.equals("Pending Orders")) {
            return (null != filterPendingOrderInfoList ? filterPendingOrderInfoList.size() : 0);
        }
        return 0;
    }
    //TODO: Filter Search
    public void filter(final String text) {

        // Searching could be complex..so we will dispatch it to a different thread...
        new Thread(new Runnable() {
            @Override
            public void run() {

                //if (Title.equals("Group Wise Sales")){
                    // Clear the filter list
                if (Title.equals("More Contacts")){
                    filterPartyMoreContactInfoList.clear();
                    // If there is no search value, then add all original list items to filter list
                    if (TextUtils.isEmpty(text)) {
                        filterPartyMoreContactInfoList.addAll(PartyMoreContactInfoList);
                    } else {
                        // Iterate in the original List and add it to filter list...
                        for (SubPartyCompleteInfo.ContactInfo item : PartyMoreContactInfoList) {
                            if (item.getName() != null && item.getCellNo() != null && item.getPhoneNo() != null && item.getEmail() != null && item.getDesignation() != null && item.getAddress1() != null && item.getAddress2() != null && item.getAddress3() != null)
                                if (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                                        item.getCellNo().toLowerCase().contains(text.toLowerCase()) ||
                                        item.getPhoneNo().toLowerCase().contains(text.toLowerCase()) ||
                                        item.getEmail().toLowerCase().contains(text.toLowerCase()) ||
                                        item.getDesignation().toLowerCase().contains(text.toLowerCase()) ||
                                        item.getAddress1().toLowerCase().contains(text.toLowerCase()) ||
                                        item.getAddress2().toLowerCase().contains(text.toLowerCase()) ||
                                        item.getAddress3().toLowerCase().contains(text.toLowerCase())) {
                                    // Adding Matched items
                                    filterPartyMoreContactInfoList.add(item);
                                }
                        }
                    }
                }else if (Title.equals("Pending Orders")) {
                    filterPendingOrderInfoList.clear();
                    // If there is no search value, then add all original list items to filter list
                    if (TextUtils.isEmpty(text)) {
                        filterPendingOrderInfoList.addAll(PendingOrderInfoList);
                    } else {
                        // Iterate in the original List and add it to filter list...
                        for (SubPartyCompleteInfo.PendingOrderInfo item : PendingOrderInfoList) {
                            if (item.getCombinedVno() != null && item.getShowroom() != null && item.getVDate() != null && item.getOrderQty() != null && item.getPendingQty()!=null)
                                if (item.getCombinedVno().toLowerCase().contains(text.toLowerCase()) ||
                                        item.getShowroom().toLowerCase().contains(text.toLowerCase()) ||
                                        item.getVDate().toLowerCase().contains(text.toLowerCase()) ||
                                        String.valueOf(item.getOrderQty()).toLowerCase().contains(text.toLowerCase()) ||
                                        String.valueOf(item.getPendingQty()).toLowerCase().contains(text.toLowerCase())) {
                                    // Adding Matched items
                                    filterPendingOrderInfoList.add(item);
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
    //TODO RecyclerViewHolder
    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TableLayout tableLayout;
        //ImageView imageViewInfo;
        public RecyclerViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.card_view);
            tableLayout = (TableLayout) itemView.findViewById(R.id.table_Layout);
            //imageViewInfo = (ImageView) itemView.findViewById(R.id.imageview_info);
        }
    }
    //TODO: Display Party TableLayout
    private void setTableLayoutParty(SubPartyCompleteInfo.ContactInfo dataset, TableLayout tableLayout){

        //TODO: TableLayout set
        tableLayout.removeAllViewsInLayout();
        tableLayout.removeAllViews();
        //TODO: 1st Row
        View v = inflater.inflate(R.layout.table_row, tableLayout, false);
        TextView txtHeader= (TextView) v.findViewById(R.id.header);
        txtHeader.setText("Primary Contact:");

        TextView txt= (TextView) v.findViewById(R.id.content);
        txt.setText(""+(dataset.getIsDefault()==null?"":(dataset.getIsDefault()==0 ? "No" : "Yes")));
        tableLayout.addView(v);
        //TODO: 2nd Row
        v = inflater.inflate(R.layout.table_row, tableLayout, false);
        txtHeader = (TextView) v.findViewById(R.id.header);
        txtHeader.setText("Name:");

        txt = (TextView) v.findViewById(R.id.content);
        txt.setText(""+(dataset.getName()==null?"":dataset.getName()));
        tableLayout.addView(v);
        //TODO: 3rd Row
        v = inflater.inflate(R.layout.table_row, tableLayout, false);
        txtHeader = (TextView) v.findViewById(R.id.header);
        txtHeader.setText("Designation:");

        txt = (TextView) v.findViewById(R.id.content);
        txt.setText(""+(dataset.getDesignation()==null?"":dataset.getDesignation()));
        tableLayout.addView(v);
        //TODO: 4th Row
        v = inflater.inflate(R.layout.table_row, tableLayout, false);
        txtHeader = (TextView) v.findViewById(R.id.header);
        txtHeader.setText("Cell No:");

        txt = (TextView) v.findViewById(R.id.content);
        txt.setText(""+(dataset.getCellNo()==null?"":dataset.getCellNo()));
        tableLayout.addView(v);
        //TODO: 5th Row
        v = inflater.inflate(R.layout.table_row, tableLayout, false);
        txtHeader = (TextView) v.findViewById(R.id.header);
        txtHeader.setText("Phone No:");

        txt = (TextView) v.findViewById(R.id.content);
        txt.setText(""+(dataset.getPhoneNo()==null?"":dataset.getPhoneNo()));
        tableLayout.addView(v);
        //TODO: 6th Row
        v = inflater.inflate(R.layout.table_row, tableLayout, false);
        txtHeader = (TextView) v.findViewById(R.id.header);
        txtHeader.setText("Email:");

        txt = (TextView) v.findViewById(R.id.content);
        txt.setText(""+(dataset.getEmail()==null?"":dataset.getEmail()));
        tableLayout.addView(v);
        //TODO: 7th Row
        v = inflater.inflate(R.layout.table_row, tableLayout, false);
        txtHeader = (TextView) v.findViewById(R.id.header);
        txtHeader.setText("Address:");

        txt = (TextView) v.findViewById(R.id.content);
        txt.setText(""+(dataset.getAddress1()==null?"":dataset.getAddress1()+", "+dataset.getAddress2()+", "+dataset.getAddress3()));
        tableLayout.addView(v);
    }
    //TODO: Display Party TableLayout
    private void setTableLayoutSubParty(SubPartyCompleteInfo.PendingOrderInfo dataset, TableLayout tableLayout){

        //TODO: TableLayout set
        tableLayout.removeAllViewsInLayout();
        tableLayout.removeAllViews();
        //TODO: 1st Row
        View v = inflater.inflate(R.layout.table_row, tableLayout, false);
        TextView txtHeader= (TextView) v.findViewById(R.id.header);
        txtHeader.setText("Combine VNo:");

        TextView txt= (TextView) v.findViewById(R.id.content);
        txt.setText(""+(dataset.getCombinedVno()==null?"":dataset.getCombinedVno()));
        tableLayout.addView(v);
        //TODO: 2nd Row
        v = inflater.inflate(R.layout.table_row, tableLayout, false);
        txtHeader = (TextView) v.findViewById(R.id.header);
        txtHeader.setText("Date:");

        txt = (TextView) v.findViewById(R.id.content);
        txt.setText(""+(dataset.getVDate()==null?"": DateFormatsMethods.DateFormat_DD_MM_YYYY(dataset.getVDate())));
        tableLayout.addView(v);
        //TODO: 3rd Row
        v = inflater.inflate(R.layout.table_row, tableLayout, false);
        txtHeader = (TextView) v.findViewById(R.id.header);
        txtHeader.setText("Showroom:");

        txt = (TextView) v.findViewById(R.id.content);
        txt.setText(""+(dataset.getShowroom()==null?"":dataset.getShowroom()));
        tableLayout.addView(v);
        //TODO: 4th Row
        v = inflater.inflate(R.layout.table_row, tableLayout, false);
        txtHeader = (TextView) v.findViewById(R.id.header);
        txtHeader.setText("Pending / Order Qty:");

        txt = (TextView) v.findViewById(R.id.content);
        txt.setText(""+(dataset.getPendingQty()==null && dataset.getOrderQty()==null ? "" : dataset.getPendingQty()+" / "+dataset.getOrderQty()));
        tableLayout.addView(v);
        //TODO: 5th Row
        v = inflater.inflate(R.layout.table_row, tableLayout, false);
        txtHeader = (TextView) v.findViewById(R.id.header);
        txtHeader.setText("Percent:");

        txt = (TextView) v.findViewById(R.id.content);
        txt.setText(""+(dataset.getPendingPercent()==null?"":dataset.getPendingPercent()+"%"));
        tableLayout.addView(v);
    }
}
