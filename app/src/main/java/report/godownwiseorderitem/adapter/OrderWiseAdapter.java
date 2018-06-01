package report.godownwiseorderitem.adapter;

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

import com.singlagroup.R;

import java.util.ArrayList;
import java.util.List;

import report.godownwiseorderitem.model.OrderWise;

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
                        if(item.getPartyName()!=null && item.getSubParty()!=null && item.getRefName()!=null && item.getOrderNo()!=null && item.getOrderDate()!=null && item.getOrderTypeName()!=null && item.getShowroom()!=null && item.getOrderStatus()!=null && item.getExpectedDelDate()!=null && item.getUrgencyLevel()!=null)
                            if (item.getPartyName().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getSubParty().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getRefName().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getOrderNo().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getOrderDate().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getShowroom().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getOrderTypeName().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getOrderStatus().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getExpectedDelDate().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getUrgencyLevel().toLowerCase().contains(text.toLowerCase()) ) {
                                // Adding Matched items
                                filterDatasetList.add(item);
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
    private void setTableLayout(OrderWise dataset, TableLayout tableLayout, TableLayout tableLayout2){

        //TODO: TableLayout set
        tableLayout.removeAllViewsInLayout();
        tableLayout.removeAllViews();
        //TODO: 1st Row
        View v = inflater.inflate(R.layout.table_row, tableLayout, false);
        TextView txtHeader= (TextView) v.findViewById(R.id.header);
        txtHeader.setText("Party Name:");

        TextView txt= (TextView) v.findViewById(R.id.content);
        txt.setText(""+(dataset.getPartyName()==null || dataset.getPartyName().equals("null") ?"":dataset.getPartyName()));
        tableLayout.addView(v);

        //TODO: 2nd Row
        View v2 = inflater.inflate(R.layout.table_row, tableLayout, false);
        TextView txtHeader2= (TextView) v2.findViewById(R.id.header);
        txtHeader2.setText("Sub Party Name:");

        TextView txt2= (TextView) v2.findViewById(R.id.content);
        txt2.setText(""+(dataset.getSubParty()==null || dataset.getSubParty().equals("null") ?"":dataset.getSubParty()));
        tableLayout.addView(v2);

        //TODO: 3nd Row
        v2 = inflater.inflate(R.layout.table_row, tableLayout, false);
        txtHeader2= (TextView) v2.findViewById(R.id.header);
        txtHeader2.setText("Reference Name:");

        txt2= (TextView) v2.findViewById(R.id.content);
        txt2.setText(""+(dataset.getRefName()==null || dataset.getRefName().equals("null") ?"":dataset.getRefName()));
        tableLayout.addView(v2);

        //TODO: TableLayout2 set
        tableLayout2.removeAllViewsInLayout();
        tableLayout2.removeAllViews();
        //TODO: 1th Row
        View vt1 = LayoutInflater.from(context).inflate(R.layout.table_row_3_column, tableLayout, false);

        TextView txtContent1= (TextView) vt1.findViewById(R.id.content_column_1);
        txtContent1.setText("Pending Items : "+dataset.getPendingItems());

        TextView txtContent2= (TextView) vt1.findViewById(R.id.content_column_2);
        txtContent2.setText("Pending Qty : "+dataset.getPendingQty());

        TextView txtContent3= (TextView) vt1.findViewById(R.id.content_column_3);
        txtContent3.setText("Order Qty : "+dataset.getOrderQty());
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
