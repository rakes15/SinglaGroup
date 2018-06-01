package orderbooking.barcode_search.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import orderbooking.StaticValues;
/**
 * Created by Rakesh on 08-Oct-16.
 */
public class BarcodeWiseBookingAdapter extends RecyclerView.Adapter<BarcodeWiseBookingAdapter.RecyclerViewHolder>{

    private Context context;
    private ProgressDialog progressDialog;
    private LayoutInflater inflater;
    private List<Map<String,String>> datasetList,filterDatasetList;
    private static String TAG = BarcodeWiseBookingAdapter.class.getSimpleName();
    public BarcodeWiseBookingAdapter(Context context, List<Map<String,String>> datasetList) {
        this.context=context;
        this.datasetList=datasetList;
        this.filterDatasetList = new ArrayList<Map<String,String>>();
        this.filterDatasetList.addAll(this.datasetList);
        this.inflater = LayoutInflater.from(context);
        this.progressDialog = new ProgressDialog(this.context);
        this.progressDialog.setMessage("Please wait...");
        this.progressDialog.setCanceledOnTouchOutside(false);
    }
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.cardview_barcodewise_booking_layout, parent, false);
        RecyclerViewHolder viewHolder=new RecyclerViewHolder(v);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {

        final Map<String,String> map = filterDatasetList.get(position);
        int s=1;
        holder.txtSerialNo.setText(position+s+".");
        holder.txtBarcode.setText("Barcode : "+map.get("Barcode")+"\nItem Code : "+map.get("ItemCode"));
        holder.txtItemCode.setText("Item Code : "+map.get("ItemCode"));
        if (map.get("MDApplicable").equals("1")) {
            holder.txtColor.setText("Color : " + map.get("ColorName"));
            holder.txtSize.setText("Size : " + map.get("SizeName"));
            holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.switch_thumb_normal_material_light));
        }else{
            if (map.get("SubItemApplicable").equals("1")) {
                holder.txtColor.setText("SubItem : " + map.get("SubItem"));
                holder.txtSize.setVisibility(View.GONE);
                holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.red_10));
            }else {
                holder.txtColor.setVisibility(View.GONE);
                holder.txtSize.setVisibility(View.GONE);
                holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.YellowLight));
            }
        }
        holder.txtRate.setText("Rate/Mrp : ₹"+map.get("Rate")+"/"+"₹"+map.get("Mrp"));
        holder.txtStock.setText(""+map.get("Stock"));
        holder.txtBookedQty.setText(""+map.get("BookQty"));

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
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
                    for (Map<String,String> item : datasetList) {
                        if(item.get("ItemCode")!=null && item.get("Barcode")!=null)
                        if (item.get("ItemCode").toLowerCase().contains(text.toLowerCase()) ||
                            item.get("Barcode").toLowerCase().contains(text.toLowerCase()) ) {
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
        TextView txtSerialNo,txtBarcode,txtItemCode,txtColor,txtSize,txtRate,txtStock,txtBookedQty;

        public RecyclerViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.card_view);
            txtSerialNo = (TextView) itemView.findViewById(R.id.textView_SerialNo);
            txtBarcode = (TextView) itemView.findViewById(R.id.textView_Barcode);
            txtItemCode = (TextView) itemView.findViewById(R.id.TextView_Itemcode);
            txtColor = (TextView) itemView.findViewById(R.id.TextView_Color);
            txtSize = (TextView) itemView.findViewById(R.id.TextView_Size);
            txtRate = (TextView) itemView.findViewById(R.id.TextView_Rate);
            txtStock = (TextView) itemView.findViewById(R.id.TextView_Stock_S_R);
            txtBookedQty = (TextView) itemView.findViewById(R.id.TextView_bookedQty);
        }
    }
}
