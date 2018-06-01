package whatsapp.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.singlagroup.R;
import com.singlagroup.customwidgets.CustomTextView;
import com.singlagroup.customwidgets.DateFormatsMethods;
import com.singlagroup.customwidgets.MyWebViewClient;

import java.util.ArrayList;
import java.util.List;
import whatsapp.autoresponder.Model.SchedulerModel;

/**
 * Created by Rakesh on 08-Oct-16.
 */
public class SchedulerAdapter extends RecyclerView.Adapter<SchedulerAdapter.RecyclerViewHolder>{

    private Context context;
    private ProgressDialog progressDialog;
    private LayoutInflater inflater;
    private List<SchedulerModel> datasetList,filterDatasetList;
    private static String TAG = SchedulerAdapter.class.getSimpleName();
    public SchedulerAdapter(Context context, List<SchedulerModel> datasetList) {
        this.context=context;
        this.datasetList=datasetList;
        this.filterDatasetList = new ArrayList<SchedulerModel>();
        this.filterDatasetList.addAll(this.datasetList);
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.cardview_all_customer_list, parent, false);
        RecyclerViewHolder viewHolder=new RecyclerViewHolder(v);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {

        final SchedulerModel dataset = filterDatasetList.get(position);
        //holder.cardView.setTag(dataset);
        if(dataset.getFlag().equals("0")){
            //TODO: Flag 0 means work done
            holder.cardView.setBackgroundColor(context.getResources().getColor(android.R.color.background_light));
        }else if (dataset.getFlag().equals("1")){
            //TODO: Flag 1 means Ready to excute
            holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.TransparentGreen));
        }else if (dataset.getFlag().equals("2")){
            //TODO: Flag 2 means work done
            holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.Yellow));
        }else if (dataset.getFlag().equals("3")){
            //TODO: Flag 3 means number not registered on whats app
            holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.TransparentRed));
        }
        //TODO: call TableLayout method
        setTableLayout(dataset,holder.tableLayout,holder.webView);
    }
    @Override
    public int getItemCount() {
        return (null != filterDatasetList ? filterDatasetList.size() : 0);
    }
    public Object getItem(int position){
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
                    for (SchedulerModel item : datasetList) {
                        if(item.getConversationName()!=null && item.getPhnNumber()!=null && item.getIsFromGroup()!=null && item.getMessageType()!=null && item.getText()!=null && item.getFlag()!=null)
                            if (item.getConversationName().toLowerCase().contains(text.toLowerCase()) ||
                                item.getPhnNumber().toLowerCase().contains(text.toLowerCase()) ||
                                item.getIsFromGroup().toLowerCase().contains(text.toLowerCase()) ||
                                item.getMessageType().toLowerCase().contains(text.toLowerCase()) ||
                                item.getText().toLowerCase().contains(text.toLowerCase()) ||
                                item.getFlag().toLowerCase().contains(text.toLowerCase()) ) {
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
        TableLayout tableLayout;
        ImageView imageViewInfo;
        WebView webView;
        public RecyclerViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.card_view);
            tableLayout = (TableLayout) itemView.findViewById(R.id.table_Layout);
            imageViewInfo = (ImageView) itemView.findViewById(R.id.imageview_info);
            webView = (WebView) itemView.findViewById(R.id.web_view);
        }
    }
    //TODO: Display TableLayout
    private void setTableLayout(SchedulerModel dataset, TableLayout tableLayout,final WebView webView){

        String ConName = (dataset.getConversationName() == null || dataset.getConversationName().equals("null") ?"":dataset.getConversationName());
        String PhonNo = (dataset.getPhnNumber() == null || dataset.getPhnNumber().equals("null") ?"":dataset.getPhnNumber());
        String Group = (dataset.getIsFromGroup() == null || dataset.getIsFromGroup().equals("null") ?"":dataset.getIsFromGroup());
               Group = (Group.equals("1") ? "YES" : "NO");
        String MsgType = (dataset.getMessageType() == null || dataset.getMessageType().equals("null") ?"":dataset.getMessageType());
        String Msg = (dataset.getText() == null || dataset.getText().equals("null") ?"":dataset.getText());
        String FileName = (dataset.getFileName() == null || dataset.getFileName().equals("null") ?"":dataset.getFileName());
        String NotifyTime = (dataset.getTime() == null || dataset.getTime().equals("null") ?"": DateFormatsMethods.DateFormat_DD_MM_YYYY_HH_MM_SS_SSS(dataset.getTime()));
        String UpdateTime = (dataset.getUpdateTime() == null || dataset.getUpdateTime().equals("null") ?"": DateFormatsMethods.DateFormat_DD_MM_YYYY_HH_MM_SS(dataset.getUpdateTime()));
        String Flag = (dataset.getFlag() == null || dataset.getFlag().equals("null") ?"":dataset.getFlag());
        String AStatus = "NA";
        if (Flag.equals("0")){
            AStatus = "Waiting";
        }else if (Flag.equals("1")){
            AStatus = "Executing";
        }else if (Flag.equals("2")){
            AStatus = "Executed";
        }else if (Flag.equals("3")){
            AStatus = "Number unregistered";
        }
        //TODO: TableLayout set
        tableLayout.removeAllViewsInLayout();
        tableLayout.removeAllViews();

        View v = inflater.inflate(R.layout.table_row, tableLayout, false);
        if (!ConName.isEmpty()) {
            //TODO: Conversion Name
            v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout, false);
            tableLayout.addView(CustomTextView.setTableRow2Columns(context, v, "Conversion Name", ConName));
        }
        if (!PhonNo.isEmpty()) {
            //TODO: Phone Number
            v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout, false);
            tableLayout.addView(CustomTextView.setTableRow2Columns(context, v, "Phone No", PhonNo));
        }
        if (!Group.isEmpty()) {
            //TODO: Group
            v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout, false);
            tableLayout.addView(CustomTextView.setTableRow2Columns(context, v, "Group", Group));
        }
        if (!Msg.isEmpty()) {
            //TODO: Message
            v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout, false);
            tableLayout.addView(CustomTextView.setTableRow2Columns(context, v, "Message", Msg));
        }
        if (!MsgType.isEmpty() && !MsgType.equals("0")) {

            //TODO: File name
            v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout, false);
            tableLayout.addView(CustomTextView.setTableRow2Columns(context, v, "File Name", FileName));
        }
        //TODO: Notify Time
        v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context, v, "Notify Time", NotifyTime));

        //TODO: Entry /Update Time
        v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context, v, "Entry/Update Time", UpdateTime));

        //TODO: Flag Status
        v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context, v, "Action Status", AStatus));
    }
}
