package orderbooking.barcode_search.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.singlagroup.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import orderbooking.barcode_search.BarcodeSearchViewPagerActivity;

/**
 * Created by Rakesh on 08-Oct-16.
 */
public class ColorWiseBarcodeAdapter extends RecyclerView.Adapter<ColorWiseBarcodeAdapter.RecyclerViewHolder>{

    private Context context;
    private ProgressDialog progressDialog;
    private LayoutInflater inflater;
    private List<String> datasetList,filterDatasetList;
    private static String TAG = ColorWiseBarcodeAdapter.class.getSimpleName();
    public ColorWiseBarcodeAdapter(Context context, List<String> datasetList) {
        this.context=context;
        this.datasetList=datasetList;
        this.filterDatasetList = new ArrayList<>();
        this.filterDatasetList.addAll(this.datasetList);
        this.inflater = LayoutInflater.from(context);
        this.progressDialog = new ProgressDialog(this.context);
        this.progressDialog.setMessage("Please wait...");
        this.progressDialog.setCanceledOnTouchOutside(false);
    }
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.cardview_header, parent, false);
        RecyclerViewHolder viewHolder=new RecyclerViewHolder(v);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {

        holder.txtHeader.setText(""+filterDatasetList.get(position));
        holder.cardView.setTag(filterDatasetList.get(position)+"/"+position);
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String tag = v.getTag().toString();
                String[] str = tag.split("/");
                String Barcode = str[0];
                final int pos = Integer.valueOf(str[1]);
                PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.inflate(R.menu.popup_menu);
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_pop_up_delete:
                                    filterDatasetList.remove(pos);
                                    BarcodeSearchViewPagerActivity.ColorWiseTabFragment.dataListBarcode.remove(pos);
                                    notifyDataSetChanged();
                                return true;
                        }
                        return false;
                    }
                });
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
                    for (String item : datasetList) {
                        if(item!=null)
                        if (item.toLowerCase().contains(text.toLowerCase())  ) {
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
        TextView txtHeader;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            txtHeader = (TextView) itemView.findViewById(R.id.textView_Header);
        }
    }
}
