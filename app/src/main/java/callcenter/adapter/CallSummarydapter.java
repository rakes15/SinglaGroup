package callcenter.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
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
import java.util.ArrayList;
import java.util.List;
import callcenter.model.CallSummary;

/**
 * Created by Rakesh on 13-Dec-17.
 */
public class CallSummarydapter extends RecyclerView.Adapter<CallSummarydapter.RecyclerViewHolder>{

    private Context context;
    private LayoutInflater inflater;
    private List<CallSummary> datasetList,filterDatasetList;
    private static String TAG = CallSummarydapter.class.getSimpleName();
    public CallSummarydapter(Context context, List<CallSummary> datasetList) {
        this.context=context;
        this.datasetList=datasetList;
        this.filterDatasetList = new ArrayList<CallSummary>();
        this.filterDatasetList.addAll(this.datasetList);
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.cardview_item, parent, false);
        RecyclerViewHolder viewHolder=new RecyclerViewHolder(v);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {

        final CallSummary dataset = filterDatasetList.get(position);

        int status = dataset.getCallStatus();
        if (status == 1){
            holder.textView.setText(""+dataset.getTotalCountg());
            holder.textViewTitle.setText("Missed");
            holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.red));
        }else if (status == 2){
            holder.textView.setText(""+dataset.getTotalCountg());
            holder.textViewTitle.setText("Busy");
            holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.red_80));
        }else if (status == 3){
            holder.textView.setText(""+dataset.getTotalCountg());
            holder.textViewTitle.setText("Unavailble");
            holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.red_50));
        }else if (status == 4){
            holder.textView.setText(""+dataset.getTotalCountg());
            holder.textViewTitle.setText("No extn");
            holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.red_10));
        }else if (status == 5){
            holder.textView.setText(""+dataset.getTotalCountg());
            holder.textViewTitle.setText("Holiday");
            holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.Orange));
        }else if (status == 6){
            holder.textView.setText(""+dataset.getTotalCountg());
            holder.textViewTitle.setText("Off time");
            holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.Orange_50));
        }else if (status == 7){
            holder.textView.setText(""+dataset.getTotalCountg());
            holder.textViewTitle.setText("Answered");
            holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.Green));
        }else if (status == 8){
            holder.textView.setText(""+dataset.getTotalCountg());
            holder.textViewTitle.setText("Outgoing");
            holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.Green_70));
        }else if (status == 9){
            holder.textView.setText(""+dataset.getTotalCountg());
            holder.textViewTitle.setText("Total Call");
            holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.row_active));
        }
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
                    for (CallSummary item : datasetList) {
                        if(item.getCallStatusMsg()!=null)
                            if (item.getCallStatusMsg().toLowerCase().contains(text.toLowerCase()) ) {
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
        TextView textView,textViewTitle;
        public RecyclerViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.card_view);
            textView = (TextView) itemView.findViewById(R.id.textView_Count);
            textViewTitle = (TextView) itemView.findViewById(R.id.textView_header);
        }
    }
}
