package inventory.analysis.catalogue.addtobox.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import com.singlagroup.R;
import com.squareup.picasso.Picasso;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import DatabaseController.CommanStatic;
import inventory.analysis.catalogue.Database_Sqlite.DatabaseSqlLiteHandlerAddToBox;
import inventory.analysis.catalogue.MainGroupOrGroupActivity;
import inventory.analysis.catalogue.Utils;
import inventory.analysis.catalogue.addtobox.dataset.RecyclerBoxListDataset;
import inventory.analysis.catalogue.addtobox.responsedataset.ResponseMoveToWishlistDataset;
import inventory.analysis.catalogue.addtobox.responsedataset.ResponseRemoveToBoxDataset;
import orderbooking.StaticValues;
import retrofit.ApiClient;
import retrofit.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import services.NetworkUtils;

/**
 * Created by Rakesh on 15-Feb-16.
 */
public class RecyclerBoxListAdapter extends RecyclerView.Adapter<RecyclerBoxListAdapter.RecyclerBoxListHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<RecyclerBoxListDataset> mapList;
    android.view.Display display;
    int height,width;
    DatabaseSqlLiteHandlerAddToBox DBHandler;
    ProgressDialog pDialog;
    private static String TAG = RecyclerBoxListAdapter.class.getSimpleName();
    public RecyclerBoxListAdapter(Context context, List<RecyclerBoxListDataset> mapList, LinearLayout linearLayoutPlaceOrderHeader) {
        this.context=context;
        this.mapList=mapList;
        linearLayoutPlaceOrderHeader.setVisibility(View.GONE);
        this.inflater = LayoutInflater.from(context);
        height= StaticValues.mBoxViewHeight;
        width= StaticValues.mBoxViewWidth;
        DBHandler=new DatabaseSqlLiteHandlerAddToBox(context);
        pDialog = new ProgressDialog(context);
        pDialog.setCanceledOnTouchOutside(false);
    }
    @Override
    public RecyclerBoxListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.cardview_add_to_box, parent, false);
        RecyclerBoxListHolder viewHolder=new RecyclerBoxListHolder(v);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(RecyclerBoxListHolder holder, final int position) {

        final int pos=position;
        holder.txtViewColor.setText(mapList.get(position).getColorName()+"( "+mapList.get(position).getItemcode()+" )");
        holder.imageView.getLayoutParams().width = width;
        holder.imageView.getLayoutParams().height = height;
        Picasso.with(context).load(mapList.get(position).getItemImage()).placeholder(R.drawable.placeholder_new).resize(width,height).into(holder.imageView);
        holder.txtViewView.setTag(mapList.get(position).getItemID());
        holder.txtViewView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, ItemDetailsActivity.class);
//                intent.putExtra("ItemID", v.getTag().toString());
//                intent.putExtra("ColorID", mapList.get(pos).getColorID());
//                intent.putExtra("ImagePath", mapList.get(position).getItemImage());
//                context.startActivity(intent);
            }
        });
        holder.txtViewRemoveFromBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ItemID=(mapList.get(pos).getItemID()==null)?"null":mapList.get(pos).getItemID();
                String ColorID=(mapList.get(pos).getColorID()==null)?"null":mapList.get(pos).getColorID();
                String status = NetworkUtils.getConnectivityStatusString(context);
                if (!status.contentEquals("No Internet Connection")) {
                    if(!CommanStatic.DeviceID.isEmpty() && !CommanStatic.DevicePassword.isEmpty()) {
                        CallRetrofit(CommanStatic.DeviceID, CommanStatic.DevicePassword, ItemID, "", ColorID, "", 0, pos);
                    }
                }else{
                    Snackbar.make(v,status,Snackbar.LENGTH_LONG).show();
                }
            }
        });
        holder.txtViewMoveToWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ItemID=(mapList.get(pos).getItemID()==null)?"null":mapList.get(pos).getItemID();
                String ColorID=(mapList.get(pos).getColorID()==null)?"null":mapList.get(pos).getColorID();
                String status = NetworkUtils.getConnectivityStatusString(context);
                if (!status.contentEquals("No Internet Connection")) {
                    if(!CommanStatic.DeviceID.isEmpty() && !CommanStatic.DevicePassword.isEmpty()) {
                        CallRetrofit(CommanStatic.DeviceID, CommanStatic.DevicePassword, ItemID, "", ColorID, "", 1, pos);
                    }
                }else{
                    Snackbar.make(v,status,Snackbar.LENGTH_LONG).show();
                }
            }
        });
        holder.tableLayout.removeAllViews();
        holder.tableLayout.removeAllViewsInLayout();
        //TODO: Grid of Size by Qty and rate
        List<Map<String,String>> sizeList=DBHandler.getSizeListWithQtyRate(mapList.get(position).getGroupID(), mapList.get(position).getItemID(),mapList.get(position).getColorID());
        int i=0;
        TableRow tableRow=new TableRow(context);
        tableRow.setId(i+10);
        tableRow.setBackgroundColor(Color.LTGRAY);

        TableRow tableRow2=new TableRow(context);
        tableRow2.setId(i+20);
        tableRow2.setBackgroundColor(Color.WHITE);

        TableRow tableRow3=new TableRow(context);
        tableRow3.setId(i+30);
        tableRow3.setBackgroundColor(Color.WHITE);

        TableRow tableRow4=new TableRow(context);
        tableRow4.setId(i+40);
        tableRow4.setBackgroundColor(Color.WHITE);

        TextView txt=null;
        for(i=0;i<sizeList.size();i++){
            //ToDO: Size
            txt = new TextView(context);
            txt.setId(i);
            txt.setGravity(Gravity.CENTER_HORIZONTAL);
            txt.setTag(sizeList.get(i).get("SizeID"));
            txt.setText(sizeList.get(i).get("SizeName"));
            txt.setTextColor(Color.BLACK);
            txt.setPadding(5, 5, 5, 5);
            tableRow.addView(txt);// add the column to the table row here
            //TODO: Rate
            txt = new TextView(context);
            txt.setId(i + 10);
            txt.setGravity(Gravity.CENTER_HORIZONTAL);
            txt.setTag(sizeList.get(i).get("SizeID"));
            txt.setText(sizeList.get(i).get("MDRate"));
            txt.setTextColor(Color.GRAY);
            txt.setPadding(5, 5, 5, 5);
            tableRow2.addView(txt);// add the column to the table row here
            //TODO:Qty
            txt = new TextView(context);
            txt.setId(i + 20);
            txt.setGravity(Gravity.CENTER_HORIZONTAL);
            txt.setTag(sizeList.get(i).get("SizeID"));
            txt.setText(sizeList.get(i).get("MDQty"));
            txt.setTextColor(Color.GRAY);
            txt.setPadding(5, 5, 5, 5);
            tableRow3.addView(txt);// add the column to the table row here
            //TODO:Total Qty
            txt = new TextView(context);
            txt.setId(i + 30);
            txt.setGravity(Gravity.CENTER_HORIZONTAL);
            txt.setTag(sizeList.get(i).get("SizeID"));
            txt.setText(sizeList.get(i).get("TotalQty"));
            txt.setTextColor(Color.GRAY);
            txt.setPadding(5, 5, 5, 5);
            tableRow4.addView(txt);// add the column to the table row here
        }
        holder.tableLayout.addView(tableRow);
        holder.tableLayout.addView(tableRow2);
        holder.tableLayout.addView(tableRow3);
        holder.tableLayout.addView(tableRow4);
    }
    @Override
    public int getItemCount() {

        return mapList==null?0:mapList.size();
    }
    public void delete(int postion){
        mapList.remove(postion);
        notifyItemRemoved(postion);
        //notifyDataSetChanged();
    }
    public class RecyclerBoxListHolder extends RecyclerView.ViewHolder {

        TextView txtViewColor, txtViewView, txtViewRemoveFromBox,txtViewMoveToWishlist;
        TableLayout tableLayout;
        ImageView imageView;
        public RecyclerBoxListHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView_icon);
            txtViewColor = (TextView) itemView.findViewById(R.id.txtViewColorName);
            txtViewView = (TextView) itemView.findViewById(R.id.txtView_View);
            txtViewRemoveFromBox = (TextView) itemView.findViewById(R.id.txtView_RemoveFromBox);
            txtViewMoveToWishlist = (TextView) itemView.findViewById(R.id.txtView_MoveToWishlist);
            tableLayout = (TableLayout) itemView.findViewById(R.id.tableLayout);
        }

    }
    private void showpDialog() {
        pDialog.show();
    }
    private void hidepDialog() {
        pDialog.dismiss();
    }
    //TODO: CallRetrofit
    private void CallRetrofit(String AKey, String pwd, final String ItemID, String SubItemID, final String ColorID, String SizeID, int flag, final int pos){
        showpDialog();
        final ApiInterface apiService =  ApiClient.getClient(CommanStatic.BaseUrl).create(ApiInterface.class);
        Map<String, String>  params = new HashMap<String,String>();
        params.put("AKey", AKey);
        params.put("pwd", pwd);
        params.put("ItemID", ItemID);
        params.put("SubItemID", SubItemID);
        params.put("ColorID", ColorID);
        params.put("SizeID", SizeID);
        if (flag==0) {
            Call<ResponseRemoveToBoxDataset> call = apiService.InventoryRemoveItemFromCart(params);
            call.enqueue(new Callback<ResponseRemoveToBoxDataset>() {
                @Override
                public void onResponse(Call<ResponseRemoveToBoxDataset> call, retrofit2.Response<ResponseRemoveToBoxDataset> response) {
                    try {
                        if (response.isSuccessful()) {
                            int Status = response.body().getStatus();
                            String msg = response.body().getMsg();
                            if (Status == 1) {
                                Map<String, String> mapList = response.body().getResult();
                                //result = "Box item removed";
                                Toast.makeText(context, "Box item removed", Toast.LENGTH_LONG).show();
                                DBHandler.DeleteItemFromBox(ItemID,ColorID);
                                delete(pos);
                                StaticValues.totalBoxCount--;
                                Utils.setBadgeCount(context, MainGroupOrGroupActivity.iconBox, StaticValues.totalBoxCount);
                            } else {
                                Toast.makeText(context, "" + msg, Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Remove from box Exception:" + e.getMessage());
                    }
                    hidepDialog();
                }

                @Override
                public void onFailure(Call<ResponseRemoveToBoxDataset> call, Throwable t) {
                    Log.e(TAG, "Remove from box Failure: " + t.toString());
                    Toast.makeText(context, "Remove from box Failure:" + t.toString(), Toast.LENGTH_LONG).show();
                    hidepDialog();
                }
            });
        }else {
            Call<ResponseMoveToWishlistDataset> call = apiService.InventoryMoveToWishList(params);
            call.enqueue(new Callback<ResponseMoveToWishlistDataset>() {
                @Override
                public void onResponse(Call<ResponseMoveToWishlistDataset> call, retrofit2.Response<ResponseMoveToWishlistDataset> response) {
                    try {
                        if (response.isSuccessful()) {
                            int Status = response.body().getStatus();
                            String msg = response.body().getMsg();
                            if (Status == 1) {
                                Map<String, String> mapList = response.body().getResult();
                                delete(pos);
                                StaticValues.totalBoxCount--;
                                StaticValues.totalWishlistCount++;
                                Toast.makeText(context, "Move to wishlist succesfully", Toast.LENGTH_LONG).show();
                                Utils.setBadgeCount(context, MainGroupOrGroupActivity.iconBox, StaticValues.totalBoxCount);
                                Utils.setBadgeCount(context, MainGroupOrGroupActivity.iconWishlist, StaticValues.totalWishlistCount);
                            } else {
                                Toast.makeText(context, "" + msg, Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "MoveToWishList Exception:" + e.getMessage());
                    }
                    hidepDialog();
                }

                @Override
                public void onFailure(Call<ResponseMoveToWishlistDataset> call, Throwable t) {
                    Log.e(TAG, "MoveToWishList Failure: " + t.toString());
                    Toast.makeText(context, "MoveToWishList Failure:" + t.toString(), Toast.LENGTH_LONG).show();
                    hidepDialog();
                }
            });
        }
    }
}
