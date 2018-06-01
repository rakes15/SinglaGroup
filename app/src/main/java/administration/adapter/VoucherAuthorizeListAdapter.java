package administration.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.singlagroup.AppController;
import com.singlagroup.R;
import com.singlagroup.customwidgets.MessageDialog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import administration.RequestForApprovalActivity;
import administration.datasets.VoucherAuthorizeListDataset;
import orderbooking.StaticValues;

/**
 * Created by Rakesh on 08-Oct-16.
 */
public class VoucherAuthorizeListAdapter extends RecyclerView.Adapter<VoucherAuthorizeListAdapter.RecyclerViewHolder>{

    private Context context;
    private LayoutInflater inflater;
    private List<VoucherAuthorizeListDataset> datasetList,filterDatasetList;
    private static String TAG = VoucherAuthorizeListAdapter.class.getSimpleName();
    public VoucherAuthorizeListAdapter(Context context, List<VoucherAuthorizeListDataset> datasetList) {
        this.context=context;
        this.datasetList=datasetList;
        this.filterDatasetList = new ArrayList<>();
        this.filterDatasetList.addAll(this.datasetList);
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.cardview_recyclerview, parent, false);
        RecyclerViewHolder viewHolder=new RecyclerViewHolder(v);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {

        final VoucherAuthorizeListDataset dataset = filterDatasetList.get(position);
        holder.txtView.setText(dataset.getHeading());
        holder.cardView.setTag(dataset);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VoucherAuthorizeListDataset vDatasets = (VoucherAuthorizeListDataset) view.getTag();
                Intent intent = new Intent(context, RequestForApprovalActivity.class);
                intent.putExtra("Heading",vDatasets.getHeading());
                intent.putExtra("VType",vDatasets.getVType());
                context.startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {
        return (null != filterDatasetList ? filterDatasetList.size() : 0);
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
                    for (VoucherAuthorizeListDataset item : datasetList) {
                        if(item.getHeading()!=null && item.getVType()!=null)
                        if (item.getHeading().toLowerCase().contains(text.toLowerCase()) ||
                            item.getVType().toLowerCase().contains(text.toLowerCase()) ) {
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

        TextView txtView;
        CardView cardView;
        ImageView imageViewInfo;
        public RecyclerViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.card_view);
            txtView = (TextView) itemView.findViewById(R.id.textView);
            imageViewInfo = (ImageView) itemView.findViewById(R.id.ImageView_Info);
        }
    }
}
