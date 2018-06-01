package administration.adapter;

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
import com.singlagroup.customwidgets.CustomTextView;

import java.util.ArrayList;
import java.util.List;

import administration.datasets.Device;

/**
 * Created by Rakesh on 08-Oct-16.
 */
public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.RecyclerViewHolder>{

    private Context context;
    private ProgressDialog progressDialog,pDialog;
    private LayoutInflater inflater;
    private List<Device> datasetList,filterDatasetList;
    private static String TAG = DeviceListAdapter.class.getSimpleName();
    public DeviceListAdapter(Context context, List<Device> datasetList) {
        this.context=context;
        this.datasetList=datasetList;
        this.filterDatasetList = new ArrayList<>();
        this.filterDatasetList.addAll(datasetList);
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

        final Device dataset = filterDatasetList.get(position);
        //TODO: call TableLayout method
        setTableLayout(dataset,position,holder.tableLayout,holder.tableLayout2,holder.tableLayout3);
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
                    for (Device item : datasetList) {
                        if(item.getUserName()!=null && item.getFullName()!=null && item.getMobileNo()!=null && item.getDModel()!=null && item.getIMEINo()!=null && item.getMacID()!=null && item.getRefName()!=null && item.getRefRemarks()!=null && item.getReqID()!=null && item.getReqDt()!=null)
                        if (item.getUserName().toLowerCase().contains(text.toLowerCase()) ||
                                item.getFullName().toString().toLowerCase().contains(text.toLowerCase()) ||
                                item.getMobileNo().toString().toLowerCase().contains(text.toLowerCase()) ||
                                item.getDModel().toString().toLowerCase().contains(text.toLowerCase()) ||
                                item.getIMEINo().toString().toLowerCase().contains(text.toLowerCase()) ||
                                item.getMacID().toString().toLowerCase().contains(text.toLowerCase()) ||
                                item.getRefName().toString().toLowerCase().contains(text.toLowerCase()) ||
                                item.getRefRemarks().toString().toLowerCase().contains(text.toLowerCase()) ||
                                item.getReqID().toString().toLowerCase().contains(text.toLowerCase()) ||
                                item.getReqDt().toString().toLowerCase().contains(text.toLowerCase()) ) {
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
        TableLayout tableLayout,tableLayout2,tableLayout3;
        public RecyclerViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.card_view);
            tableLayout = (TableLayout) itemView.findViewById(R.id.table_Layout);
            tableLayout2 = (TableLayout) itemView.findViewById(R.id.table_Layout2);
            tableLayout3 = (TableLayout) itemView.findViewById(R.id.table_Layout3);
        }
    }
    //TODO: Display TableLayout
    private void setTableLayout(final Device dataset, final int pos, final TableLayout tableLayout, final TableLayout tableLayout2, TableLayout tableLayout3){

        //TODO: TableLayout set
        tableLayout.removeAllViewsInLayout();
        tableLayout.removeAllViews();
       //TODO: Request No
        View v = inflater.inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"Request No",""+dataset.getReqID()));
        //TODO: Device Model
        v = inflater.inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"Device Model",""+dataset.getDModel()));
        //TODO: Imei
        v = inflater.inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"IMEI No",""+dataset.getIMEINo()));
        //TODO: Mac Id
        v = inflater.inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"Mac ID",""+dataset.getMacID()));
        //TODO: Serial No
        v = inflater.inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"Serial No",""+dataset.getSerialNo()));
        //TODO: User Type
        String UserType = "";
        String[] user = context.getResources().getStringArray(R.array.UserType);
        for (int i=0; i<user.length; i++){
            if (dataset.getUserType() == i){
                UserType = user[i];
                break;
            }
        }
        v = inflater.inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"User Type",""+UserType));
        //TODO: Mobile
        v = inflater.inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"Mobile",""+dataset.getMobileNo()));
        //TODO: Email
        v = inflater.inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"Email ",""));
        //TODO: User Name
        v = inflater.inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"User Name",""+dataset.getUserName()));
    }

}
