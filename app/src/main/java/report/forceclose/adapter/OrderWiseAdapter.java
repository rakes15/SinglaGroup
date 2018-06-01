package report.forceclose.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.singlagroup.GlobleValues;
import com.singlagroup.R;
import com.singlagroup.customwidgets.CustomTextView;
import com.singlagroup.customwidgets.DateFormatsMethods;

import java.util.ArrayList;
import java.util.List;

import report.forceclose.model.OrderWise;

/**
 * Created by Rakesh on 03-Aug-17.
 */
public class OrderWiseAdapter extends RecyclerView.Adapter<OrderWiseAdapter.RecyclerViewHolder>{

    private Context context;
    private ProgressDialog progressDialog;
    private LayoutInflater inflater;
    private List<OrderWise> datasetList,filterDatasetList;
    private static String TAG = OrderWiseAdapter.class.getSimpleName();
    public OrderWiseAdapter(Context context, List<OrderWise> datasetList) {
        this.context=context;
        this.datasetList=datasetList;
        this.filterDatasetList = new ArrayList<OrderWise>();
        this.filterDatasetList.addAll(this.datasetList);
        this.inflater = LayoutInflater.from(context);
        this.progressDialog = new ProgressDialog(this.context);
        this.progressDialog.setMessage("Please wait...");
        this.progressDialog.setCanceledOnTouchOutside(false);
    }
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.cardview_all_customer_list, parent, false);
        RecyclerViewHolder viewHolder=new RecyclerViewHolder(v);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {

        final OrderWise dataset = filterDatasetList.get(position);
        holder.cardView.setTag(dataset);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        //TODO: call TableLayout method
        setTableLayout(dataset,holder.tableLayout,holder.tableLayout2);
    }
    @Override
    public int getItemCount() {
        return (null != filterDatasetList ? filterDatasetList.size() : 0);
    }
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return filterDatasetList.get(position);
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

                } else {
                    // Iterate in the original List and add it to filter list...
                    for (OrderWise item : datasetList) {

                        if(item.getPartyName()!=null && item.getSubParty()!=null && item.getRefName()!=null && item.getOrderNo()!=null && item.getOrderDate()!=null && item.getOrderTypeName()!=null && item.getShowroom()!=null && item.getOrderStatus()!=null && item.getExpectedDelDate()!=null && item.getUrgencyLevel()!=null) {
                            String Urgency = "";
                            if (item.getUrgencyLevel().equals("1")) {
                                Urgency = "Immediate";
                            } else if (item.getUrgencyLevel().equals("2")) {
                                Urgency = "Urgent";
                            } else if (item.getUrgencyLevel().equals("3")) {
                                Urgency = "Medium";
                            } else if (item.getUrgencyLevel().equals("4")) {
                                Urgency = "Low";
                            }
                            if (item.getPartyName().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getSubParty().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getRefName().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getOrderNo().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getOrderDate().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getShowroom().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getOrderTypeName().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getOrderStatus().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getExpectedDelDate().toLowerCase().contains(text.toLowerCase()) ||
                                    Urgency.toLowerCase().contains(text.toLowerCase())) {
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
    //TODO RecyclerViewHolder
    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TableLayout tableLayout,tableLayout2;
        ImageView imageViewInfo;
        public RecyclerViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.card_view);
            tableLayout = (TableLayout) itemView.findViewById(R.id.table_Layout);
            tableLayout2 = (TableLayout) itemView.findViewById(R.id.table_Layout2);
            imageViewInfo = (ImageView) itemView.findViewById(R.id.imageview_info);
        }
    }
    //TODO: Display TableLayout
    private void setTableLayout(OrderWise dataset, TableLayout tableLayout, TableLayout tableLayout2) {

        //TODO: TableLayout set
        tableLayout.removeAllViewsInLayout();
        tableLayout.removeAllViews();
        //TODO: Order No
        View v = inflater.inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context, v, "Order No", (dataset.getOrderNo() == null || dataset.getOrderNo().equals("null") ? "" : dataset.getOrderNo())));
        //TODO: Order Date
        v = inflater.inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context, v, "Order Date", (dataset.getOrderDate() == null || dataset.getOrderDate().equals("null") ? "" : DateFormatsMethods.DateFormat_DD_MM_YYYY(dataset.getOrderDate()))));
        //TODO: Party Name
        v = inflater.inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context, v, "Party Name", (dataset.getPartyName() == null || dataset.getPartyName().equals("null") ? "" : dataset.getPartyName())));
        //TODO: SubParty Name
        String SubParty = (dataset.getSubParty() == null || dataset.getSubParty().equals("null") ? "" : dataset.getSubParty());
        if (!SubParty.isEmpty()) {
            v = inflater.inflate(R.layout.table_row, tableLayout, false);
            tableLayout.addView(CustomTextView.setTableRow2Columns(context, v, "Sub Party", SubParty));
        }
        //TODO: Reference Name
        String RefName = (dataset.getRefName() == null || dataset.getRefName().equals("null") ? "" : dataset.getRefName());
        if (!RefName.isEmpty()) {
            v = inflater.inflate(R.layout.table_row, tableLayout, false);
            tableLayout.addView(CustomTextView.setTableRow2Columns(context, v, "Reference Name", RefName));
        }
        //TODO: Order Status
        v = inflater.inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context, v, "Status", (dataset.getOrderStatus() == null || dataset.getOrderStatus().equals("null") ? "" : dataset.getOrderStatus())));
        //TODO: Showroom
        v = inflater.inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context, v, "Showroom", (dataset.getShowroom() == null || dataset.getShowroom().equals("null") ? "" : dataset.getShowroom())));
        //TODO: Order Type
        v = inflater.inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context, v, "Order Type", (dataset.getOrderTypeName() == null || dataset.getOrderTypeName().equals("null") ? "" : dataset.getOrderTypeName())));
        //TODO: Expected Delivery Date
        v = inflater.inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context, v, "Ex-Del. Date", (dataset.getExpectedDelDate() == null || dataset.getExpectedDelDate().equals("null") ? "" : DateFormatsMethods.DateFormat_DD_MM_YYYY_HH_MM_SS(dataset.getExpectedDelDate()))));
        //TODO: Urgency Level
        v = inflater.inflate(R.layout.table_row, tableLayout, false);
        TextView txtHeader = (TextView) v.findViewById(R.id.header);
        txtHeader.setText("Urgency Level:");

        TextView txt = (TextView) v.findViewById(R.id.content);
        String Urgency = (dataset.getUrgencyLevel() == null || dataset.getUrgencyLevel().equals("null") ? "" : dataset.getUrgencyLevel());
        if (Urgency.equals("1")) {
            txt.setText("Immediate");
            txt.setTextColor(context.getResources().getColor(R.color.Maroon));
        }else if (Urgency.equals("2")) {
            txt.setText("Urgent");
            txt.setTextColor(context.getResources().getColor(R.color.red));
        }else if (Urgency.equals("3")) {
            txt.setText("Medium");
            txt.setTextColor(context.getResources().getColor(R.color.Color_Green));
        }else if (Urgency.equals("4")) {
            txt.setText("Low");
            txt.setTextColor(context.getResources().getColor(R.color.Yellow));
        }
        tableLayout.addView(v);
        //TODO: TableLayout2 set
        tableLayout2.removeAllViewsInLayout();
        tableLayout2.removeAllViews();
        //TODO: 1th Row
        String Header1 = "";
        String Header2 = "";
        String Header3 = "";
        if (GlobleValues.multi_action_flag == 0){
            Header1 = "Total Items : "+dataset.getPendingItems();
            Header2 = "Total Qty : "+dataset.getOrderQty();
            Header3 = "Total Amt : "+dataset.getPendingAmt();
        }else if (GlobleValues.multi_action_flag == 1){
            Header1 = "Pending Items : "+dataset.getPendingItems();
            Header2 = "Pending Qty : "+dataset.getPendingQty()+"("+dataset.getPendingPercentage()+"%)";
            Header3 = "Order Qty : "+dataset.getOrderQty();
        }else if (GlobleValues.multi_action_flag == 2){
            Header1 = "Pending Items : "+dataset.getPendingItems();
            Header2 = "Pending Qty : "+dataset.getPendingQty()+"("+dataset.getPendingPercentage()+"%)";
            Header3 = "Order Qty : "+dataset.getOrderQty();
        }
        View vt1 = LayoutInflater.from(context).inflate(R.layout.table_row_3_column, tableLayout, false);

        TextView txtContent1= (TextView) vt1.findViewById(R.id.content_column_1);
        txtContent1.setText(Header1);

        TextView txtContent2= (TextView) vt1.findViewById(R.id.content_column_2);
        txtContent2.setText(Header2);

        TextView txtContent3= (TextView) vt1.findViewById(R.id.content_column_3);
        txtContent3.setText(Header3);
        tableLayout2.addView(vt1);
    }
    private void showpDialog() {
        if(progressDialog!=null) {
            progressDialog.show();
        }
    }
    private void hidepDialog() {
        if(progressDialog!=null) {
            progressDialog.dismiss();
        }
    }
}
