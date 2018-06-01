package inventory.analysis.catalogue.wishlist.adapter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.singlagroup.AppController;
import com.singlagroup.LoginActivity;
import com.singlagroup.R;
import com.singlagroup.customwidgets.DateFormatsMethods;
import com.singlagroup.customwidgets.MessageDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DatabaseController.CommanStatic;
import inventory.analysis.catalogue.Database_Sqlite.DatabaseSqlLiteHandlerWishlist;
import inventory.analysis.catalogue.MainGroupOrGroupActivity;
import inventory.analysis.catalogue.Utils;
import inventory.analysis.catalogue.adapter.RecyclerColorOptionAdapter;
import inventory.analysis.catalogue.adapter.RecyclerColorOptionDataSet;
import inventory.analysis.catalogue.responsedataset.ResponseAddOrRemoveDataset;
import inventory.analysis.catalogue.responsedataset.ResponseColorOptionDataset;
import inventory.analysis.catalogue.wishlist.GroupWishListActivity;
import inventory.analysis.catalogue.wishlist.dataset.RecyclerWishlistDataset;
import orderbooking.Database_Sqlite.DatabaseSqlLiteHandlerOrderBooking;
import orderbooking.StaticValues;
import orderbooking.barcode_search.BarcodeSearchViewPagerActivity;
import orderbooking.barcode_search.OrderBookingActivity;
import orderbooking.barcode_search.SubItemActivity;
import orderbooking.barcode_search.WithoutSubItemActivity;
import orderbooking.customerlist.datasets.CloseOrBookDataset;
import retrofit.ApiClient;
import retrofit.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import services.NetworkUtils;

/**
 * Created by Rakesh on 15-Feb-16.
 */
public class RecyclerWishListAdapter extends RecyclerView.Adapter<RecyclerWishListAdapter.RecyclerWishListHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<RecyclerWishlistDataset> mapList;
    android.view.Display display;
    int height,width;
    private RecyclerView recyclerView;
    private CloseOrBookDataset closeOrBookDataset;
    DatabaseSqlLiteHandlerWishlist DBHandler;
    ProgressDialog spotsDialog;
    private int flag=0;
    private static String TAG = RecyclerWishListAdapter.class.getSimpleName();
    public RecyclerWishListAdapter(Context context, List<RecyclerWishlistDataset> mapList, int flag, CloseOrBookDataset closeOrBookDataset) {
        this.context=context;
        this.mapList=mapList;
        this.closeOrBookDataset=closeOrBookDataset;
        this.inflater = LayoutInflater.from(context);
        DBHandler=new DatabaseSqlLiteHandlerWishlist(context);
        display = ((WindowManager) context.getSystemService(context.WINDOW_SERVICE)).getDefaultDisplay();
        spotsDialog=new ProgressDialog(context);
        spotsDialog.setMessage("Please wait...");
        spotsDialog.setCanceledOnTouchOutside(false);
        this.flag = flag;
        if (flag==0) {
            width= StaticValues.mViewWidth;
            height= StaticValues.mViewHeight;
        }else if (flag==1) {
            width= StaticValues.sViewHeight-250;
            height= StaticValues.sViewWidth-250;
        }
        //System.out.println("width:"+width+"\nheight:"+height);
        GroupWishListActivity.groupflag = 1;
    }
    @Override
    public RecyclerWishListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.cardview_wishlist, parent, false);
        RecyclerWishListHolder viewHolder=new RecyclerWishListHolder(v);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(RecyclerWishListHolder holder, final int position) {

        final int pos=position;
        holder.txtView.setText(""+mapList.get(position).getItemcode());
        holder.txtView.setBackground(context.getResources().getDrawable(R.drawable.textview_background_round));

        holder.txtViewStock.setText(mapList.get(position).getItemStock());
        holder.txtViewStock.setTextColor(context.getResources().getColor(R.color.Green));
        holder.txtViewStock.setBackground(context.getResources().getDrawable(R.drawable.textview_background_round));

        holder.txtTotalcolor.setText(""+(mapList.get(position).getTotalcolor()==null ? "0" : mapList.get(position).getTotalcolor()));
        holder.txtTotalcolor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            try{
                if(mapList.size()>0) {
                    //CustomDialog(mapList.get(position).getItemID(),mapList.get(position).getItemcode(),mapList.get(position).getRate());
                }
            }catch (Exception e){
                Log.e("Error","Exception:"+e.getMessage());
            }
            }
        });
        if(mapList.get(position).getRate()==""){
            holder.txtViewPrice.setVisibility(View.GONE);
            holder.txtViewPrice.setText("" + mapList.get(position).getRate());
            holder.txtViewPrice.setBackground(context.getResources().getDrawable(R.drawable.textview_background_round));
        }
        else{
            holder.txtViewPrice.setText("" + mapList.get(position).getRate());
            holder.txtViewPrice.setBackground(context.getResources().getDrawable(R.drawable.textview_background_round));
        }
        if(flag==0) {
            holder.imageView.getLayoutParams().width = width;
            holder.imageView.getLayoutParams().height = height;
        }else if (flag==1){
            holder.imageView.getLayoutParams().width = width;//RelativeLayout.LayoutParams.MATCH_PARENT;
            holder.imageView.getLayoutParams().height = height;//RelativeLayout.LayoutParams.MATCH_PARENT;
        }
        Picasso.with(context).load(mapList.get(position).getItemImage()).placeholder(R.drawable.placeholder_new).resize(width, height).into(holder.imageView);
        holder.cardView.setTag(mapList.get(position).getItemcode());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupWishListActivity.groupflag = 2;
                String status = NetworkUtils.getConnectivityStatusString(context);
                if (!status.contentEquals("No Internet Connection")) {
                    LoginActivity obj = new LoginActivity();
                    String[] str = obj.GetSharePreferenceSession(context);
                } else {
                    MessageDialog.MessageDialog(context, "", status);
                }
            }
        });
        holder.btnAddToBox.setTag(mapList.get(position).getItemcode());
        holder.btnAddToBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupWishListActivity.groupflag = 2;
                String status = NetworkUtils.getConnectivityStatusString(context);
                if (!status.contentEquals("No Internet Connection")) {
                    LoginActivity obj = new LoginActivity();
                    String[] str = obj.GetSharePreferenceSession(context);
                } else {
                    MessageDialog.MessageDialog(context, "", status);
                }
            }
        });
        holder.btnRemoveFromWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if(mapList.size()>0) {
                        String status = NetworkUtils.getConnectivityStatusString(context);
                        if (!status.contentEquals("No Internet Connection")) {
                            LoginActivity obj=new LoginActivity();
                            String[] str = obj.GetSharePreferenceSession(context);
                            if (str!=null) {
                                //TODO: Call Volley Wishlist Clear
                                CallVolleyWishlistClear(str[3], str[0], str[14], str[4], str[5], str[3], str[4],mapList.get(pos).getItemID(),"","","","","","","","","","0",""+CommanStatic.AppType,"1",position);
                            }
                        }else{
                            Snackbar.make(v,status,Snackbar.LENGTH_LONG).show();
                        }

                    }
                }catch (Exception e){
                    Log.e("Error","Exception:"+e.getMessage());
                }
            }
        });
    }
    @Override
    public int getItemCount() {

        return mapList==null?0:mapList.size();
    }
    public void delete(int postion){
        mapList.remove(postion);
        notifyItemRemoved(postion);
        notifyDataSetChanged();
    }
    public class RecyclerWishListHolder extends RecyclerView.ViewHolder {

        TextView txtView, txtViewPrice, txtViewStock,txtTotalcolor,btnAddToBox,btnRemoveFromWishlist;
        ImageView imageView;
        CardView cardView;

        public RecyclerWishListHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            imageView = (ImageView) itemView.findViewById(R.id.imageView_icon);
            txtView = (TextView) itemView.findViewById(R.id.textView_item_code);
            txtViewPrice = (TextView) itemView.findViewById(R.id.textView_item_price);
            txtViewStock = (TextView) itemView.findViewById(R.id.textView_item_stock);
            txtTotalcolor = (TextView) itemView.findViewById(R.id.textView_Color);
            btnAddToBox = (TextView) itemView.findViewById(R.id.btn_AddToBox);
            btnRemoveFromWishlist = (TextView) itemView.findViewById(R.id.btn_removeFromWishlist);
        }
    }
    private void CustomDialog(String ItemID,String Itemcode,String rate){
        Dialog dialog = new Dialog(new ContextThemeWrapper(context, R.style.DialogSlideAnim));
        dialog.setContentView(R.layout.dialog_color_similar);
        android.view.Display display = ((WindowManager)context.getSystemService(context.WINDOW_SERVICE)).getDefaultDisplay();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = (int) (display.getHeight() * 55 / 100);
        lp.gravity = Gravity.BOTTOM;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setTitle("Color Option");
        dialog.show();
        TextView txtViewItemCode=(TextView) dialog.findViewById(R.id.txtView_Itemcode);
        String Rate=(StaticValues.rateFlag==1)?"\t\tPrice:" + rate:"";
        txtViewItemCode.setText("Itemcode: " + Itemcode +Rate);
        recyclerView=(RecyclerView) dialog.findViewById(R.id.recyclerView_Similar);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        //recyclerView.addItemDecoration(new DividerItemDecoration(context,LinearLayoutManager.HORIZONTAL));
        //TODO: ColorOption List Execute
        String status = NetworkUtils.getConnectivityStatusString(context);
        if (!status.contentEquals("No Internet Connection")) {
            if(!CommanStatic.DeviceID.isEmpty() && !CommanStatic.DevicePassword.isEmpty() && !CommanStatic.PriceListID.isEmpty()) {
                CallRetrofitColorOption(CommanStatic.DeviceID, CommanStatic.DevicePassword, ItemID, CommanStatic.PriceListID);
            }else{
                MessageDialog.MessageDialog(context,"","Empty");
            }
        }else{
            Snackbar.make(recyclerView,status,Snackbar.LENGTH_LONG).show();
        }
    }
    private void showDialog() {
        spotsDialog.show();
    }
    private void hideDialog() {
        spotsDialog.dismiss();
    }
    //TodO: CallRetrofitAddRemoveWishlist
    private void CallVolleyWishlistClear(final String DeviceID, final String SessionID, final String CompanyID, final String UserID, final String DivisionID,final String ToDeviceID, final String ToUserID, final String ItemID, final String SubItemID, final String ColorID, final String SizeID, final String GroupID, final String PartyID, final String SubPartyID, final String RefName, final String Remarks, final String MasterID, final String MasterType, final String AppType, final String DelFlag,final int position){
        showDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"AddRemoveSG_WishListItem", new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                // response
                Log.d("Response", response);
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    int Status=(jsonObject.getString("status")==null)?0:jsonObject.getInt("status");
                    String Msg=(jsonObject.getString("msg")==null)?"Server is not responding":jsonObject.getString("msg");
                    if (Status == 1) {
                        DBHandler.DeleteItemFromWishlist(ItemID);
                        delete(position);
                        Toast.makeText(context, "Item remove to wishlist", Toast.LENGTH_SHORT).show();
                        StaticValues.totalWishlistCount--;
                        Utils.setBadgeCount(context, MainGroupOrGroupActivity.iconWishlist, StaticValues.totalWishlistCount);
                    } else {
                        MessageDialog.MessageDialog(context,"",Msg);
                    }
                }catch (Exception e){
                    MessageDialog.MessageDialog(context,"Exception",""+e.toString());
                }
                hideDialog();
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", ""+error);
                MessageDialog.MessageDialog(context,"Error",""+error.toString());
                hideDialog();
            }
        } ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("DeviceID", DeviceID);
                params.put("SessionID", SessionID);
                params.put("CompanyID", CompanyID);
                params.put("UserID", UserID);
                params.put("DivisionID", DivisionID);
                params.put("ToDeviceID", ToDeviceID);
                params.put("ToUserID", ToUserID);
                params.put("ItemID", ItemID);
                params.put("SubItemID", SubItemID);
                params.put("ColorID", ColorID);
                params.put("SizeID", SizeID);
                params.put("GroupID", GroupID);
                params.put("PartyID", PartyID);
                params.put("SubPartyID", SubPartyID);
                params.put("RefName", RefName);
                params.put("Remarks", Remarks);
                params.put("MasterID", MasterID);
                params.put("MasterType", MasterType);
                params.put("AppType", AppType);
                params.put("DelFlag", DelFlag);
                Log.d(TAG,"Wishlist clear Parameters:"+params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
    }
    private void CallRetrofitColorOption(String AKey, String Password,String ItemID, String PriceListID){
        showDialog();
        ApiInterface apiService =  ApiClient.getClient(CommanStatic.BaseUrl).create(ApiInterface.class);
        Map<String, String>  params = new HashMap<String, String>();
        params.put("AKey", AKey);
        params.put("pwd", Password);
        params.put("PriceListID", PriceListID);
        params.put("ItemID", ItemID);
        Call<ResponseColorOptionDataset> call = apiService.getInventoryColorOptionList(params);
        call.enqueue(new Callback<ResponseColorOptionDataset>() {
            @Override
            public void onResponse(Call<ResponseColorOptionDataset> call, retrofit2.Response<ResponseColorOptionDataset> response) {
                try {
                    if (response.isSuccessful()) {
                        int status = response.body().getStatus();
                        String msg = response.body().getMsg();
                        if (status == 1) {
                            List<RecyclerColorOptionDataSet> DatasetList = response.body().getResult();
                            if (DatasetList != null) {
                                RecyclerColorOptionAdapter recyclerColorOptionAdapter = new RecyclerColorOptionAdapter(context, DatasetList,closeOrBookDataset);
                                recyclerView.setAdapter(recyclerColorOptionAdapter);
                            }
                        } else {
                            Toast.makeText(context, "" + msg, Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Color Option Exception:" + e.getMessage());
                }
                hideDialog();
            }

            @Override
            public void onFailure(Call<ResponseColorOptionDataset> call, Throwable t) {
                Log.e(TAG, "Failure:" + t.toString());
                Toast.makeText(context, "Color Option Failure", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        });
    }
}
