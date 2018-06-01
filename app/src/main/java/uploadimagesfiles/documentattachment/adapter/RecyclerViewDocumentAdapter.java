package uploadimagesfiles.documentattachment.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import com.singlagroup.R;
import com.singlagroup.customwidgets.DateFormatsMethods;
import com.singlagroup.customwidgets.MessageDialog;

import java.util.ArrayList;
import java.util.List;

import orderbooking.StaticValues;
import uploadimagesfiles.documentattachment.AttachmentActivity;
import uploadimagesfiles.documentattachment.DocumentAttachmentUploadActivity;
import uploadimagesfiles.documentattachment.datasets.Result;

/**
 * Created by Rakesh on 08-Oct-16.
 */
public class RecyclerViewDocumentAdapter extends RecyclerView.Adapter<RecyclerViewDocumentAdapter.RecyclerViewHolder>{

    private Context context;
    private LayoutInflater inflater;
    private List<Result> documentList, filterDocumentList;
    private static String TAG = RecyclerViewDocumentAdapter.class.getSimpleName();
    public RecyclerViewDocumentAdapter(Context context, List<Result> documentList) {
        this.context=context;
        this.documentList=documentList;
        this.filterDocumentList = new ArrayList<>();
        this.filterDocumentList.addAll(this.documentList);
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
        
        Result dataset = filterDocumentList.get(position);
        setTableLayoutVoucher(dataset,holder.tableLayout);
    }
    @Override
    public int getItemCount() {
        int size = 0;
        size = (null != filterDocumentList ? filterDocumentList.size() : 0);
        return size;
    }
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return filterDocumentList.get(position);
    }
    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        TableLayout tableLayout;
        CardView cardView;
        public RecyclerViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.card_view);
            tableLayout = (TableLayout) itemView.findViewById(R.id.table_Layout);
        }
    }
    private void setTableLayoutVoucher(final Result dataset, TableLayout tableLayout){

        //TODO: TableLayout set
        tableLayout.removeAllViewsInLayout();
        tableLayout.removeAllViews();
        //TODO: 1st Row
        View v = inflater.inflate(R.layout.table_row, tableLayout, false);
        TextView txtHeader= (TextView) v.findViewById(R.id.header);
        txtHeader.setText("Doc No:");

        TextView txt= (TextView) v.findViewById(R.id.content);
        txt.setText(""+(dataset.getCombinedVNo()==null?"":dataset.getCombinedVNo()));
        tableLayout.addView(v);

        //TODO: 2nd Row
        View v1 = inflater.inflate(R.layout.table_row, tableLayout, false);
        TextView txtHeader1= (TextView) v1.findViewById(R.id.header);
        txtHeader1.setText("Doc Date:");

        TextView txt1= (TextView) v1.findViewById(R.id.content);
        txt1.setText(""+(dataset.getVDate()==null?"": dataset.getVDate()));
        tableLayout.addView(v1);

        //TODO: 2nd Row
        v1 = inflater.inflate(R.layout.table_row, tableLayout, false);
        txtHeader1= (TextView) v1.findViewById(R.id.header);
        txtHeader1.setText("Bill Net Amt:");

        txt1= (TextView) v1.findViewById(R.id.content);
        txt1.setText(""+(dataset.getBillNetAmount()==null?"": StaticValues.Rupees+dataset.getBillNetAmount()));
        tableLayout.addView(v1);
    }
    //TODO: Filter Search
    public void filter(final String text) {

        // Searching could be complex..so we will dispatch it to a different thread...
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Clear the filter list
                filterDocumentList.clear();
                //Toast.makeText(context,text,Toast.LENGTH_LONG).show();
                Log.e("TAG", text);
                // If there is no search value, then add all original list items to filter list
                if (TextUtils.isEmpty(text)) {
                    filterDocumentList.addAll(documentList);
                } else {
                    // Iterate in the original List and add it to filter list...
                    for (Result item : documentList) {
                        if (item.getCombinedVNo() != null && item.getVDate() != null && item.getBillNetAmount() != null)
                            if (item.getCombinedVNo().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getVDate().toLowerCase().contains(text.toLowerCase()) ||
                                    String.valueOf(item.getBillNetAmount()).toLowerCase().contains(text.toLowerCase())) {
                                // Adding Matched items
                                filterDocumentList.add(item);
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
}
