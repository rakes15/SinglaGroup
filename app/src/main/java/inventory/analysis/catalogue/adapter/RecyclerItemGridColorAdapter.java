package inventory.analysis.catalogue.adapter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import inventory.analysis.catalogue.MainGroupOrGroupActivity;
import inventory.analysis.catalogue.Utils;
import inventory.analysis.catalogue.dataset.RecyclerSimilarItemsDataSet;
import inventory.analysis.catalogue.responsedataset.ResponseAddOrRemoveDataset;
import inventory.analysis.catalogue.responsedataset.ResponseColorOptionDataset;
import inventory.analysis.catalogue.responsedataset.ResponseSimilarColorDataset;
import inventory.analysis.catalogue.responseitemlistcolor.ItemList;
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
public class RecyclerItemGridColorAdapter extends RecyclerView.Adapter<RecyclerItemGridColorAdapter.RecyclerItemGridHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<ItemList> mapList;
    private int flag=0;
    private CloseOrBookDataset closeOrBookDataset;
    android.view.Display display;
    int height,width;
    RecyclerView recyclerView;
    ProgressDialog spotsDialog;
    private static String TAG = RecyclerItemGridColorAdapter.class.getSimpleName();
    public RecyclerItemGridColorAdapter(Context context, List<ItemList> mapList, int flag, CloseOrBookDataset closeOrBookDataset) {
        this.context=context;
        this.mapList=mapList;
        this.flag=flag;
        this.closeOrBookDataset=closeOrBookDataset;
        this.inflater = LayoutInflater.from(context);
        display = ((WindowManager) context.getSystemService(context.WINDOW_SERVICE)).getDefaultDisplay();
        spotsDialog=new ProgressDialog(context);
        spotsDialog.setMessage("Please wait...");
        spotsDialog.setCanceledOnTouchOutside(false);
        if(flag==0){
            width= StaticValues.mViewWidth;
            height=StaticValues.mViewHeight;
        }
        else if(flag==1)
        {
            width=StaticValues.sViewWidth;
            height=StaticValues.sViewHeight;
        }
    }
    @Override
    public RecyclerItemGridHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.cardview_item_design, parent, false);
        RecyclerItemGridHolder viewHolder=new RecyclerItemGridHolder(v);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(RecyclerItemGridHolder holder, final int position) {

        final int pos=position;
        if (mapList.get(position).getCartStatus()==1){
            holder.imageCartIndicator.setVisibility(View.VISIBLE);
        }else {
            holder.imageCartIndicator.setVisibility(View.GONE);
        }
        holder.txtView.setText(mapList.get(position).getItemCode());
        holder.txtColorName.setText((mapList.get(position).getColor()==null ? "" : mapList.get(position).getColor()));
        int stock = mapList.get(position).getStock();
        String ItemStock=(StaticValues.stockFlag==0)?"":((StaticValues.stockFlag==1)?""+stock+" "+mapList.get(position).getUnit():((stock>=StaticValues.stockFormula)?"> "+StaticValues.stockFormula:""+stock+" "+mapList.get(position).getUnit()));
        holder.txtViewStock.setText(""+ItemStock);
        holder.txtColor.setText(""+mapList.get(position).getTotalColor());
        int rate=mapList.get(position).getRate();
        String Rate=(StaticValues.rateFlag==0)?"":((rate<0)?"Not available":(rate ==0)?"":"₹"+ rate);
        if(Rate==""){
            holder.txtViewPrice.setVisibility(View.GONE);
            holder.txtViewPrice.setText("" + Rate);
        }else{
            holder.txtViewPrice.setText("" + Rate);
        }
        if(flag==0) {
            holder.cardView.getLayoutParams().width = width;
            holder.cardView.getLayoutParams().height = height;
            Picasso.with(context).load(mapList.get(position).getItemImage()).placeholder(R.drawable.placeholder_new).into(holder.imageView);
        }else{
            holder.cardView.getLayoutParams().width= RelativeLayout.LayoutParams.MATCH_PARENT;
            holder.cardView.getLayoutParams().height = RelativeLayout.LayoutParams.MATCH_PARENT;
            String Path = mapList.get(position).getItemImage().replaceAll("440x600", "1025x1400");
            Picasso.with(context).load(Path).placeholder(R.drawable.placeholder_new).into(holder.imageView);
        }
        holder.imageView.setTag(mapList.get(position).getItemCode());
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = NetworkUtils.getConnectivityStatusString(context);
                if (!status.contentEquals("No Internet Connection")) {
                    LoginActivity obj = new LoginActivity();
                    String[] str = obj.GetSharePreferenceSession(context);
                    //if (str != null)

                } else {
                    MessageDialog.MessageDialog(context, "", status);
                }
            }
        });
        holder.checkBox.setChecked((mapList.get(position).getWishlistStatus()==0)?false:true);
        holder.checkBox.setTag(mapList.get(position));
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                ItemList itemList = (ItemList) cb.getTag();
                itemList.setWishlistStatus((cb.isChecked()==true)?1:0);
                mapList.get(pos).setWishlistStatus((cb.isChecked()==true)?1:0);
                String status = NetworkUtils.getConnectivityStatusString(context);
                if(cb.isChecked()){
                    if (!status.contentEquals("No Internet Connection")) {
                        LoginActivity obj=new LoginActivity();
                        String[] str = obj.GetSharePreferenceSession(context);
                        if (str!=null  && !mapList.get(pos).getItemID().isEmpty()) {
                            //TODO: Call Volley Wishlist
                            CallVolleyAddRemoveWishlist(str[3], str[0], str[14], str[4], str[5],str[3],str[4], ""+mapList.get(pos).getItemID(), "",""+mapList.get(pos).getColorID(),"","","","","","","","0",""+CommanStatic.AppType,"0");
                        }else{
                            Snackbar.make(cb,"Something wrong!!!",Snackbar.LENGTH_LONG).show();
                            cb.setChecked(false);
                        }
                    }else{
                        Snackbar.make(cb,status,Snackbar.LENGTH_LONG).show();
                        cb.setChecked(false);
                    }
                } else {
                    if (!status.contentEquals("No Internet Connection")) {
                        LoginActivity obj=new LoginActivity();
                        String[] str = obj.GetSharePreferenceSession(context);
                        if (str!=null  && !mapList.get(pos).getItemID().isEmpty()) {
                            //TODO: Call Volley Wishlist
                            CallVolleyAddRemoveWishlist(str[3], str[0], str[14], str[4], str[5],str[3],str[4], ""+mapList.get(pos).getItemID(), "",""+mapList.get(pos).getColorID(),"","","","","","","","0",""+CommanStatic.AppType,"1");
                        }else{
                            Snackbar.make(cb,"Something wrong!!!",Snackbar.LENGTH_LONG).show();
                            cb.setChecked(true);
                        }
                    }else{
                        Snackbar.make(cb,status,Snackbar.LENGTH_LONG).show();
                        cb.setChecked(true);
                    }
                }
            }
        });
        holder.txtColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog(mapList.get(position).getItemID(),mapList.get(position).getItemName(),mapList.get(position).getItemCode(),mapList.get(position).getRate(),0);
            }
        });
        holder.imageViewSimilarItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog(mapList.get(position).getItemID(),mapList.get(position).getItemName(),mapList.get(position).getItemCode(),mapList.get(position).getRate(),1);
            }
        });
    }
    @Override
    public int getItemCount() {

        return mapList==null?0:mapList.size();
    }
    private void CustomDialog(final String ItemID, final String ItemName, String Itemcode, int rate, int flag){
        Dialog dialog = new Dialog(new ContextThemeWrapper(context, R.style.DialogSlideAnim));
        dialog.setContentView(R.layout.dialog_color_similar);
        android.view.Display display = ((WindowManager)context.getSystemService(context.WINDOW_SERVICE)).getDefaultDisplay();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        if(flag==0) {
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            DisplayMetrics dm = this.context.getResources().getDisplayMetrics();
            float screenWidth  = StaticValues.mViewWidth / dm.xdpi;
            if(screenWidth > 1.5) {
                int w = (int)(1.5*dm.xdpi);
                lp.height = (int)(w*1.82);
            }else{
                lp.height = (int) (display.getHeight() * 55 / 100);
            }
            lp.gravity = Gravity.BOTTOM;
            dialog.getWindow().setAttributes(lp);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setTitle("Color Option");
            dialog.show();
            TextView txtViewItemCode = (TextView) dialog.findViewById(R.id.txtView_Itemcode);
            String Rate=(StaticValues.rateFlag==1)?"\t\tPrice:" + rate:"";
            txtViewItemCode.setText("Itemcode: " + Itemcode +Rate);
            recyclerView = (RecyclerView) dialog.findViewById(R.id.recyclerView_Similar);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView.setLayoutManager(layoutManager);
//            recyclerView.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.HORIZONTAL));
            //TODO: ColorOption List Execute
            String status = NetworkUtils.getConnectivityStatusString(context);
            if (!status.contentEquals("No Internet Connection")) {
                if(!CommanStatic.DeviceID.isEmpty() && !CommanStatic.DevicePassword.isEmpty() && !CommanStatic.PriceListID.isEmpty()) {
                    CallRetrofit(CommanStatic.DeviceID, CommanStatic.DevicePassword, ItemID, ItemName, CommanStatic.PriceListID,0);
                }
            }else{
                Snackbar.make(recyclerView,""+status,Snackbar.LENGTH_LONG).show();
            }
        }
        else if(flag==1){
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            DisplayMetrics dm = this.context.getResources().getDisplayMetrics();
            float screenWidth  = StaticValues.mViewWidth / dm.xdpi;
            if(screenWidth > 1.5) {
                int w = (int)(1.5*dm.xdpi);
                lp.height = (int)(w*1.7);
            }else{
                lp.height = (int) (display.getHeight() * 50 / 100);
            }
            lp.gravity = Gravity.BOTTOM;
            dialog.getWindow().setAttributes(lp);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setTitle("Similar Items");
            dialog.show();
            TextView txtViewItemCode = (TextView) dialog.findViewById(R.id.txtView_Itemcode);
            txtViewItemCode.setVisibility(View.GONE);
            recyclerView = (RecyclerView) dialog.findViewById(R.id.recyclerView_Similar);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView.setLayoutManager(layoutManager);
//            recyclerView.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
//            recyclerView.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.HORIZONTAL));
            //TODO: ColorOption List Execute
            String status = NetworkUtils.getConnectivityStatusString(context);
            if (!status.contentEquals("No Internet Connection")) {
                if(!CommanStatic.DeviceID.isEmpty() && !CommanStatic.DevicePassword.isEmpty() && !CommanStatic.PriceListID.isEmpty()) {
                    CallRetrofit(CommanStatic.DeviceID, CommanStatic.DevicePassword, ItemID, ItemName, CommanStatic.PriceListID,1);
                }
            }else{
                Snackbar.make(recyclerView,""+status,Snackbar.LENGTH_LONG).show();
            }
        }
    }
    public class RecyclerItemGridHolder extends RecyclerView.ViewHolder {

        TextView txtView,txtViewPrice,txtViewStock,txtColor,txtColorName;
        ImageView imageView,imageViewSimilarItems,imageCartIndicator;
        CheckBox checkBox;
        CardView cardView;
        public RecyclerItemGridHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            imageView = (ImageView) itemView.findViewById(R.id.imageView_icon);
            imageViewSimilarItems = (ImageView) itemView.findViewById(R.id.imageViewSimilarItems);
            imageCartIndicator = (ImageView) itemView.findViewById(R.id.imageView_CartIndicator);
            txtView = (TextView) itemView.findViewById(R.id.textView_title);
            txtViewPrice = (TextView) itemView.findViewById(R.id.textView_title_price);
            txtViewStock = (TextView) itemView.findViewById(R.id.textView_item_stock);
            txtColor = (TextView) itemView.findViewById(R.id.textView_Color);
            txtColorName = (TextView) itemView.findViewById(R.id.textView_Color_Name);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox_wishlist);
        }
    }
    private void CallRetrofit(String AKey, String Password,String ItemID,String ItemName, String PriceListID,int flag){
        showDialog();
        ApiInterface apiService =  ApiClient.getClient(CommanStatic.BaseUrl).create(ApiInterface.class);
        Map<String, String>  params = new HashMap<String, String>();
        params.put("AKey", AKey);
        params.put("pwd", Password);
        params.put("PriceListID", PriceListID);
        if(flag==0) {
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
        }else if(flag==1){
            params.put("ItemName", ItemName);
            Call<ResponseSimilarColorDataset> call = apiService.getInventorySimilarItemList(params);
            call.enqueue(new Callback<ResponseSimilarColorDataset>() {
                @Override
                public void onResponse(Call<ResponseSimilarColorDataset> call, retrofit2.Response<ResponseSimilarColorDataset> response) {
                    try {
                        int status = response.body().getStatus();
                        String msg = response.body().getMsg();
                        if (status == 1) {
                            List<RecyclerSimilarItemsDataSet> DatasetList = response.body().getResult();
                            if(DatasetList!=null) {
                                if(DatasetList.isEmpty()){
                                    Toast.makeText(context, "No Similar Items!!!", Toast.LENGTH_LONG).show();
                                }else {
                                    RecyclerSimilarItemsAdapter recyclerSimilarItemsAdapter = new RecyclerSimilarItemsAdapter(context, DatasetList,closeOrBookDataset);
                                    recyclerView.setAdapter(recyclerSimilarItemsAdapter);
                                }
                            }
                        } else {
                            Toast.makeText(context, "" + msg, Toast.LENGTH_LONG).show();
                        }
                        //}
                    } catch (Exception e) {
                        Log.e(TAG, "Color Option Exception:" + e.getMessage());
                    }
                    hideDialog();
                }

                @Override
                public void onFailure(Call<ResponseSimilarColorDataset> call, Throwable t) {
                    Log.e(TAG, "Failure:" + t.toString());
                    Toast.makeText(context, "Color Option Failure", Toast.LENGTH_LONG).show();
                    hideDialog();
                }
            });

        }
    }
    private void CallVolleyAddRemoveWishlist(final String DeviceID, final String SessionID, final String CompanyID, final String UserID, final String DivisionID,final String ToDeviceID, final String ToUserID, final String ItemID, final String SubItemID, final String ColorID, final String SizeID, final String GroupID, final String PartyID, final String SubPartyID, final String RefName, final String Remarks, final String MasterID, final String MasterType, final String AppType, final String DelFlag){
        //showDialog();
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
                        if (DelFlag.equals("0")) {
                            Toast.makeText(context, "Item add to wishlist", Toast.LENGTH_SHORT).show();
                            StaticValues.totalWishlistCount++;
                            Utils.setBadgeCount(context, MainGroupOrGroupActivity.iconWishlist, StaticValues.totalWishlistCount);
                        } else if (DelFlag.equals("1")) {
                            Toast.makeText(context, "Item remove to wishlist", Toast.LENGTH_SHORT).show();
                            StaticValues.totalWishlistCount--;
                            Utils.setBadgeCount(context, MainGroupOrGroupActivity.iconWishlist, StaticValues.totalWishlistCount);
                        }
                    } else {
                        MessageDialog.MessageDialog(context,"",Msg);
                    }
                }catch (Exception e){
                    MessageDialog.MessageDialog(context,"Exception",""+e.toString());
                }
                //hideDialog();
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", ""+error);
                MessageDialog.MessageDialog(context,"Error",""+error.toString());
                //hideDialog();
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
                Log.d(TAG,"Wishlist Add or remove Parameters:"+params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
    }
    private void showDialog() {
        spotsDialog.show();
    }
    private void hideDialog() {
        spotsDialog.dismiss();
    }
}
