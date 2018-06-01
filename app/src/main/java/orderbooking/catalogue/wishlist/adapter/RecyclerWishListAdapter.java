package orderbooking.catalogue.wishlist.adapter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
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
import DatabaseController.DatabaseSqlLiteHandlerWishlist;
import orderbooking.Database_Sqlite.DatabaseSqlLiteHandlerOrderBooking;
import orderbooking.StaticValues;
import orderbooking.barcode_search.BarcodeSearchViewPagerActivity;
import orderbooking.barcode_search.OrderBookingActivity;
import orderbooking.barcode_search.SubItemActivity;
import orderbooking.barcode_search.WithoutSubItemActivity;
import orderbooking.catalogue.CatalogueActivity;
import orderbooking.catalogue.Utils;
import orderbooking.catalogue.adapter.RecyclerColorOptionAdapter;
import orderbooking.catalogue.adapter.RecyclerColorOptionDataSet;
import orderbooking.catalogue.responsedataset.ResponseAddOrRemoveDataset;
import orderbooking.catalogue.responsedataset.ResponseColorOptionDataset;
import orderbooking.catalogue.wishlist.GroupWishListActivity;
import orderbooking.catalogue.wishlist.dataset.RecyclerWishlistDataset;
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
    public RecyclerWishListAdapter(Context context, List<RecyclerWishlistDataset> mapList,int flag,CloseOrBookDataset closeOrBookDataset) {
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
        holder.txtViewStock.setText(mapList.get(position).getItemStock());
        holder.txtViewStock.setTextColor(context.getResources().getColor(R.color.Green));
        holder.txtTotalcolor.setText(""+(mapList.get(position).getTotalcolor()==null ? "0" : mapList.get(position).getTotalcolor()));
        holder.txtTotalcolor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            try{
                if(mapList.size()>0) {
                    CustomDialog(mapList.get(position).getItemID(),mapList.get(position).getItemcode(),mapList.get(position).getRate());
                }
            }catch (Exception e){
                Log.e("Error","Exception:"+e.getMessage());
            }
            }
        });
        if(mapList.get(position).getRate()==""){
            holder.txtViewPrice.setVisibility(View.GONE);
            holder.txtViewPrice.setText("" + mapList.get(position).getRate());
        }
        else{
            holder.txtViewPrice.setText("" + mapList.get(position).getRate());
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
                    if (str != null)
                        BarcodeSearchViewPagerActivity.Barcode = v.getTag().toString();
                    StaticValues.OrderViewIntentFlag = 1;
                    StaticValues.CatalogueFlag = 1;
                    CallVolleyStyleOrBarcodeSearch(str[3], str[4], str[0], str[5], str[14], str[15], BarcodeSearchViewPagerActivity.Barcode, closeOrBookDataset.getOrderID());
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
                    if (str != null)
                        BarcodeSearchViewPagerActivity.Barcode = v.getTag().toString();
                    StaticValues.OrderViewIntentFlag = 1;
                    StaticValues.CatalogueFlag = 1;
                    CallVolleyStyleOrBarcodeSearch(str[3], str[4], str[0], str[5], str[14], str[15], BarcodeSearchViewPagerActivity.Barcode, closeOrBookDataset.getOrderID());
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
                            if(!CommanStatic.DeviceID.isEmpty() && !CommanStatic.DevicePassword.isEmpty() && !mapList.get(pos).getItemID().isEmpty()) {
                                CallRetrofitAddRemoveWishlist(CommanStatic.DeviceID, CommanStatic.DevicePassword, mapList.get(pos).getItemID(), position);
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
    private void showpDialog() {
        spotsDialog.show();
    }
    private void hidepDialog() {
        spotsDialog.dismiss();
    }
    //TodO: CallRetrofitAddRemoveWishlist
    private void CallRetrofitAddRemoveWishlist(String AKey, String Password, final String ItemID, final int position){
        showpDialog();
        ApiInterface apiService =  ApiClient.getClient(CommanStatic.BaseUrl).create(ApiInterface.class);
        final Map<String, String> params = new HashMap<String, String>();
        params.put("AKey", AKey);
        params.put("pwd", Password);
        params.put("ItemID", ItemID);
        params.put("WishlistFlag", "0");
        Call<ResponseAddOrRemoveDataset> call = apiService.getStatusForWishListAddOrDelete(params);
        call.enqueue(new Callback<ResponseAddOrRemoveDataset>() {
            @Override
            public void onResponse(Call<ResponseAddOrRemoveDataset> call, retrofit2.Response<ResponseAddOrRemoveDataset> response) {
                try {
                    if (response.isSuccessful()) {
                        int status = response.body().getStatus();
                        String msg = response.body().getMsg();
                        if (status == 1) {
                            DBHandler.DeleteItemFromWishlist(ItemID);
                            delete(position);
                            Toast.makeText(context, "Item remove to wishlist", Toast.LENGTH_SHORT).show();
                            StaticValues.totalWishlistCount--;
                            Utils.setBadgeCount(context, CatalogueActivity.iconWishlist, StaticValues.totalWishlistCount);
                        } else {
                            Toast.makeText(context, "" + msg, Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Wishlist Remove Exception:" + e.getMessage());
                }
                hidepDialog();
            }

            @Override
            public void onFailure(Call<ResponseAddOrRemoveDataset> call, Throwable t) {
                Log.e(TAG, "Wishlist Remove Failure:" + t.toString());
                Toast.makeText(context, "Wishlist Remove Failure", Toast.LENGTH_LONG).show();
                hidepDialog();
            }
        });
    }
    private void CallRetrofitColorOption(String AKey, String Password,String ItemID, String PriceListID){
        showpDialog();
        ApiInterface apiService =  ApiClient.getClient(CommanStatic.BaseUrl).create(ApiInterface.class);
        Map<String, String>  params = new HashMap<String, String>();
        params.put("AKey", AKey);
        params.put("pwd", Password);
        params.put("PriceListID", PriceListID);
        params.put("ItemID", ItemID);
        Call<ResponseColorOptionDataset> call = apiService.getColorOptionList(params);
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
                hidepDialog();
            }

            @Override
            public void onFailure(Call<ResponseColorOptionDataset> call, Throwable t) {
                Log.e(TAG, "Failure:" + t.toString());
                Toast.makeText(context, "Color Option Failure", Toast.LENGTH_LONG).show();
                hidepDialog();
            }
        });
    }
    //TODO: Call Style or Barcode search
    private void CallVolleyStyleOrBarcodeSearch(final String DeviceID, final String UserID,final String SessionID,final String DivisionID,final String CompanyID,final String BranchID,final String Barcode,final String OrderID){
        showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"ItemDetailsForBooking", new Response.Listener<String>()
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
                        StaticValues.ColorWise = 0;
                        List<Map<String,String>> mapList = new ArrayList<>();
                        List<Map<String,String>> mapListSizeSet = new ArrayList<>();
                        JSONObject jsonObjectResult = jsonObject.getJSONObject("Result");
                        String MainGroup = jsonObjectResult.getString("MainGroup");
                        String MainGroupID = jsonObjectResult.getString("MainGroupID");
                        String GroupName = jsonObjectResult.getString("GroupName");
                        String GroupID = jsonObjectResult.getString("GroupID");
                        String SubGroup = jsonObjectResult.getString("SubGroup");
                        String SubGroupID = jsonObjectResult.getString("SubGroupID");
                        String ItemID = jsonObjectResult.getString("ItemID");
                        String ItemName = jsonObjectResult.getString("ItemName");
                        String ItemCode = jsonObjectResult.getString("ItemCode");
                        int SubItemApplicable = jsonObjectResult.getInt("SubItemApplicable");
                        int MdApplicable = jsonObjectResult.getInt("MdApplicable");
                        StaticValues.MDApplicable = MdApplicable;
                        StaticValues.SubItemApplicable = SubItemApplicable;
                        StaticValues.StockCheck = jsonObjectResult.getInt("StockCheck");
                        if (MdApplicable == 1){
                            //TODO: Multi Details only
                            //TODO SizeSet Array
                            JSONArray jsonArraySizeSet = jsonObjectResult.getJSONArray("SizeSet");
                            //System.out.println("SizeSet:"+jsonArraySizeSet);
                            //TODO Details Array
                            JSONArray jsonArrayDetails = jsonObjectResult.getJSONArray("Details");
                            //System.out.println("Details:"+jsonArrayDetails);
                            for (int i=0; i< jsonArrayDetails.length(); i++) {
                                Map<String, String> map = new HashMap<>();
                                map.put("MainGroup", MainGroup);
                                map.put("MainGroupID", MainGroupID);
                                map.put("GroupName", GroupName);
                                map.put("GroupID", GroupID);
                                map.put("SubGroup", SubGroup);
                                map.put("SubGroupID", SubGroupID);
                                map.put("ItemID", ItemID);
                                map.put("ItemName", ItemName);
                                map.put("ItemCode", ItemCode);
                                map.put("SubItemApplicable", String.valueOf(SubItemApplicable));
                                map.put("MdApplicable", String.valueOf(MdApplicable));
                                map.put("InProduction", jsonArrayDetails.getJSONObject(i).getString("InProduction"));
                                map.put("Barcode", jsonArrayDetails.getJSONObject(i).getString("Barcode"));
                                map.put("ItemID", jsonArrayDetails.getJSONObject(i).getString("ItemID"));
                                map.put("ColorFamily", jsonArrayDetails.getJSONObject(i).getString("ColorFamily"));
                                map.put("Color", jsonArrayDetails.getJSONObject(i).getString("Color"));
                                map.put("ColorCode", jsonArrayDetails.getJSONObject(i).getString("ColorCode"));
                                map.put("Size", jsonArrayDetails.getJSONObject(i).getString("Size"));
                                map.put("SizeSequence", jsonArrayDetails.getJSONObject(i).getString("SizeSequence"));
                                map.put("ColorID", jsonArrayDetails.getJSONObject(i).getString("ColorID"));
                                map.put("SizeID", jsonArrayDetails.getJSONObject(i).getString("SizeID"));
                                map.put("Stock", jsonArrayDetails.getJSONObject(i).getString("Stock"));
                                map.put("ReserveStock", jsonArrayDetails.getJSONObject(i).getString("ReserveStock"));
                                map.put("Remarks", "");//(jsonArrayDetails.getJSONObject(i).getString("Remarks") == null || jsonArrayDetails.getJSONObject(i).getString("Remarks").equals("null")) ? "" : jsonArrayDetails.getJSONObject(i).getString("Remarks"));
                                map.put("ImageStatus", jsonArrayDetails.getJSONObject(i).getString("ImageStatus"));
                                map.put("ImageUrl", jsonArrayDetails.getJSONObject(i).getString("ImageUrl"));
                                for (int x = 0; x < StaticValues.MultiOrderSize; x++) {
                                    map.put("ExpectedDate"+x, (jsonArrayDetails.getJSONObject(i).getString("ExpectedDate"+x) == null || jsonArrayDetails.getJSONObject(i).getString("ExpectedDate"+x).equals("null")) ? DateFormatsMethods.getDateTime() : jsonArrayDetails.getJSONObject(i).getString("ExpectedDate"+x));
                                    map.put("Rate"+x, (jsonArrayDetails.getJSONObject(i).getString("Rate"+x) == null || jsonArrayDetails.getJSONObject(i).getString("Rate"+x).equals("null")) ? "0" : jsonArrayDetails.getJSONObject(i).getString("Rate"+x));
                                    map.put("DiscountRate"+x, (jsonArrayDetails.getJSONObject(i).optString("DiscountRate"+x) == null || jsonArrayDetails.getJSONObject(i).optString("DiscountRate"+x).isEmpty()) ? jsonArrayDetails.getJSONObject(i).getString("Rate"+x) : jsonArrayDetails.getJSONObject(i).optString("DiscountRate"+x));
                                    map.put("DiscountPercentage"+x, (jsonArrayDetails.getJSONObject(i).optString("DiscountPercentage"+x) == null || jsonArrayDetails.getJSONObject(i).optString("DiscountPercentage"+x).isEmpty()) ? "0" : jsonArrayDetails.getJSONObject(i).optString("DiscountPercentage"+x));
                                    map.put("Ord"+x, (jsonArrayDetails.getJSONObject(i).getString("Ord"+x) == null || jsonArrayDetails.getJSONObject(i).getString("Ord"+x).equals("null")) ? "0" : jsonArrayDetails.getJSONObject(i).getString("Ord"+x));
                                    map.put("OrderID"+x, (jsonArrayDetails.getJSONObject(i).getString("OrderID"+x) == null || jsonArrayDetails.getJSONObject(i).getString("OrderID"+x).equals("null")) ? "" : jsonArrayDetails.getJSONObject(i).getString("OrderID"+x));
                                }
                                mapList.add(map);
                            }
                            //TODO: Size set
                            for (int i=0; i<jsonArraySizeSet.length(); i++){
                                Map<String,String> map = new HashMap<>();
                                map.put("MainGroupID", MainGroupID);
                                map.put("GroupID", GroupID);
                                map.put("SizeCount", jsonArraySizeSet.getJSONObject(i).getString("SizeCount"));
                                map.put("Required", jsonArraySizeSet.getJSONObject(i).getString("Required"));
                                mapListSizeSet.add(map);
                            }
                            context.deleteDatabase(DatabaseSqlLiteHandlerOrderBooking.DATABASE_NAME);
                            DatabaseSqlLiteHandlerOrderBooking DBOrder = new DatabaseSqlLiteHandlerOrderBooking(context);
                            DBOrder.deleteTablesData();
                            DBOrder.deleteOutOfStockTablesData();
                            DBOrder.insertOrderBookingTable(mapList);
                            DBOrder.insertSizeSetTable(mapListSizeSet);
                            hidepDialog();

                            Intent intent = new Intent(context,OrderBookingActivity.class);
                            context.startActivity(intent);
//                            getActivity().finish();
                        }else {
                            //TODO: SubItem Only
                            if (SubItemApplicable == 1){
                                //TODO SizeSet Array
                                JSONArray jsonArraySizeSet = jsonObjectResult.getJSONArray("SizeSet");
                                //System.out.println("SizeSet:"+jsonArraySizeSet);
                                //TODO Details Array
                                JSONArray jsonArrayDetails = jsonObjectResult.getJSONArray("Details");
                                //System.out.println("Details:"+jsonArrayDetails);
                                for (int i=0; i< jsonArrayDetails.length(); i++) {
                                    Map<String, String> map = new HashMap<>();
                                    map.put("MainGroup", MainGroup);
                                    map.put("MainGroupID", MainGroupID);
                                    map.put("GroupName", GroupName);
                                    map.put("GroupID", GroupID);
                                    map.put("SubGroup", SubGroup);
                                    map.put("SubGroupID", SubGroupID);
                                    map.put("ItemID", ItemID);
                                    map.put("ItemName", ItemName);
                                    map.put("ItemCode", ItemCode);
                                    map.put("InProduction", jsonArrayDetails.getJSONObject(i).getString("InProduction"));
                                    map.put("Barcode", jsonArrayDetails.getJSONObject(i).getString("Barcode"));
                                    map.put("ItemID", jsonArrayDetails.getJSONObject(i).getString("ItemID"));
                                    map.put("SubItemID", jsonArrayDetails.getJSONObject(i).getString("SubItemID"));
                                    map.put("SubItemName", jsonArrayDetails.getJSONObject(i).getString("SubItemName"));
                                    map.put("SubItemCode", jsonArrayDetails.getJSONObject(i).getString("SubItemCode"));
                                    map.put("Stock", jsonArrayDetails.getJSONObject(i).getString("Stock"));
                                    map.put("ReserveStock", jsonArrayDetails.getJSONObject(i).getString("ReserveStock"));
                                    map.put("Remarks", "");//(jsonArrayDetails.getJSONObject(i).getString("Remarks") == null || jsonArrayDetails.getJSONObject(i).getString("Remarks").equals("null")) ? "" : jsonArrayDetails.getJSONObject(i).getString("Remarks"));
                                    map.put("ImageStatus", jsonArrayDetails.getJSONObject(i).getString("ImageStatus"));
                                    map.put("ImageUrl", jsonArrayDetails.getJSONObject(i).getString("ImageUrl"));
                                    for (int x = 0; x < StaticValues.MultiOrderSize; x++) {
                                        map.put("ExpectedDate"+x, (jsonArrayDetails.getJSONObject(i).getString("ExpectedDate"+x) == null || jsonArrayDetails.getJSONObject(i).getString("ExpectedDate"+x).equals("null")) ? DateFormatsMethods.getDateTime() : jsonArrayDetails.getJSONObject(i).getString("ExpectedDate"+x));
                                        map.put("Rate"+x, (jsonArrayDetails.getJSONObject(i).getString("Rate"+x) == null || jsonArrayDetails.getJSONObject(i).getString("Rate"+x).equals("null")) ? "0" : jsonArrayDetails.getJSONObject(i).getString("Rate"+x));
                                        map.put("DiscountRate"+x, (jsonArrayDetails.getJSONObject(i).optString("DiscountRate"+x) == null || jsonArrayDetails.getJSONObject(i).optString("DiscountRate"+x).isEmpty()) ? jsonArrayDetails.getJSONObject(i).getString("Rate"+x) : jsonArrayDetails.getJSONObject(i).optString("DiscountRate"+x));
                                        map.put("DiscountPercentage"+x, (jsonArrayDetails.getJSONObject(i).optString("DiscountPercentage"+x) == null || jsonArrayDetails.getJSONObject(i).optString("DiscountPercentage"+x).isEmpty()) ? "0" : jsonArrayDetails.getJSONObject(i).optString("DiscountPercentage"+x));
                                        map.put("Ord"+x, (jsonArrayDetails.getJSONObject(i).getString("Ord"+x) == null || jsonArrayDetails.getJSONObject(i).getString("Ord"+x).equals("null")) ? "0" : jsonArrayDetails.getJSONObject(i).getString("Ord"+x));
                                        map.put("OrderID"+x, (jsonArrayDetails.getJSONObject(i).getString("OrderID"+x) == null || jsonArrayDetails.getJSONObject(i).getString("OrderID"+x).equals("null")) ? "" : jsonArrayDetails.getJSONObject(i).getString("OrderID"+x));
                                    }
                                    mapList.add(map);
                                }
                                //TODO: Size set
                                for (int i=0; i<jsonArraySizeSet.length(); i++){
                                    Map<String,String> map = new HashMap<>();
                                    map.put("MainGroupID", MainGroupID);
                                    map.put("GroupID", GroupID);
                                    map.put("SizeCount", jsonArraySizeSet.getJSONObject(i).getString("SizeCount"));
                                    map.put("Required", jsonArraySizeSet.getJSONObject(i).getString("Required"));
                                    mapListSizeSet.add(map);
                                }
                                context.deleteDatabase(DatabaseSqlLiteHandlerOrderBooking.DATABASE_NAME);
                                DatabaseSqlLiteHandlerOrderBooking DBOrder = new DatabaseSqlLiteHandlerOrderBooking(context);
                                DBOrder.deleteTablesData();
                                DBOrder.insertOrderBookingSubItemTable(mapList);
                                DBOrder.insertSizeSetTable(mapListSizeSet);
                                hidepDialog();

                                Intent intent = new Intent(context,SubItemActivity.class);
                                context.startActivity(intent);
                                //getActivity().finish();
                            }else{
                                //TODO: Without SubItem Or Item only
                                //TODO SizeSet Array
                                JSONArray jsonArraySizeSet = jsonObjectResult.getJSONArray("SizeSet");
                                //System.out.println("SizeSet:"+jsonArraySizeSet);
                                //TODO Details Array
                                JSONArray jsonArrayDetails = jsonObjectResult.getJSONArray("Details");
                                //System.out.println("Details:"+jsonArrayDetails);
                                for (int i=0; i< jsonArrayDetails.length(); i++) {
                                    Map<String, String> map = new HashMap<>();
                                    map.put("MainGroup", MainGroup);
                                    map.put("MainGroupID", MainGroupID);
                                    map.put("GroupName", GroupName);
                                    map.put("GroupID", GroupID);
                                    map.put("SubGroup", SubGroup);
                                    map.put("SubGroupID", SubGroupID);
                                    map.put("ItemID", ItemID);
                                    map.put("ItemName", ItemName);
                                    map.put("ItemCode", ItemCode);
                                    map.put("InProduction", jsonArrayDetails.getJSONObject(i).getString("InProduction"));
                                    map.put("Barcode", jsonArrayDetails.getJSONObject(i).getString("Barcode"));
                                    map.put("ItemID", jsonArrayDetails.getJSONObject(i).getString("ItemID"));
                                    map.put("Stock", jsonArrayDetails.getJSONObject(i).getString("Stock"));
                                    map.put("ReserveStock", jsonArrayDetails.getJSONObject(i).getString("ReserveStock"));
                                    map.put("Remarks", "");//(jsonArrayDetails.getJSONObject(i).getString("Remarks") == null || jsonArrayDetails.getJSONObject(i).getString("Remarks").equals("null")) ? "" : jsonArrayDetails.getJSONObject(i).getString("Remarks"));
                                    map.put("ImageStatus", jsonArrayDetails.getJSONObject(i).getString("ImageStatus"));
                                    map.put("ImageUrl", jsonArrayDetails.getJSONObject(i).getString("ImageUrl"));
                                    for (int x = 0; x < StaticValues.MultiOrderSize; x++) {
                                        map.put("ExpectedDate"+x, (jsonArrayDetails.getJSONObject(i).getString("ExpectedDate"+x) == null || jsonArrayDetails.getJSONObject(i).getString("ExpectedDate"+x).equals("null")) ? DateFormatsMethods.getDateTime() : jsonArrayDetails.getJSONObject(i).getString("ExpectedDate"+x));
                                        map.put("Rate"+x, (jsonArrayDetails.getJSONObject(i).getString("Rate"+x) == null || jsonArrayDetails.getJSONObject(i).getString("Rate"+x).equals("null")) ? "0" : jsonArrayDetails.getJSONObject(i).getString("Rate"+x));
                                        map.put("DiscountRate"+x, (jsonArrayDetails.getJSONObject(i).optString("DiscountRate"+x) == null || jsonArrayDetails.getJSONObject(i).optString("DiscountRate"+x).isEmpty()) ? jsonArrayDetails.getJSONObject(i).getString("Rate"+x) : jsonArrayDetails.getJSONObject(i).optString("DiscountRate"+x));
                                        map.put("DiscountPercentage"+x, (jsonArrayDetails.getJSONObject(i).optString("DiscountPercentage"+x) == null || jsonArrayDetails.getJSONObject(i).optString("DiscountPercentage"+x).isEmpty()) ? "0" : jsonArrayDetails.getJSONObject(i).optString("DiscountPercentage"+x));
                                        map.put("Ord"+x, (jsonArrayDetails.getJSONObject(i).getString("Ord"+x) == null || jsonArrayDetails.getJSONObject(i).getString("Ord"+x).equals("null")) ? "0" : jsonArrayDetails.getJSONObject(i).getString("Ord"+x));
                                        map.put("OrderID"+x, (jsonArrayDetails.getJSONObject(i).getString("OrderID"+x) == null || jsonArrayDetails.getJSONObject(i).getString("OrderID"+x).equals("null")) ? "" : jsonArrayDetails.getJSONObject(i).getString("OrderID"+x));
                                    }
                                    mapList.add(map);
                                }
                                //TODO: Size set
                                for (int i=0; i<jsonArraySizeSet.length(); i++){
                                    Map<String,String> map = new HashMap<>();
                                    map.put("MainGroupID", MainGroupID);
                                    map.put("GroupID", GroupID);
                                    map.put("SizeCount", jsonArraySizeSet.getJSONObject(i).getString("SizeCount"));
                                    map.put("Required", jsonArraySizeSet.getJSONObject(i).getString("Required"));
                                    mapListSizeSet.add(map);
                                }
                                context.deleteDatabase(DatabaseSqlLiteHandlerOrderBooking.DATABASE_NAME);
                                DatabaseSqlLiteHandlerOrderBooking DBOrder = new DatabaseSqlLiteHandlerOrderBooking(context);
                                DBOrder.deleteTablesData();
                                DBOrder.insertOrderBookingWithoutSubItemTable(mapList);
                                DBOrder.insertSizeSetTable(mapListSizeSet);
                                hidepDialog();

                                Intent intent = new Intent(context,WithoutSubItemActivity.class);
                                context.startActivity(intent);
                                //getActivity().finish();
                            }
                        }
                        //System.out.println("List:"+mapList.toString());
                        //System.out.println("ListSizeSet:"+mapListSizeSet.toString());
                    } else {
                        MessageDialog.MessageDialog(context,"",Msg);
                        hidepDialog();
                    }
                }catch (Exception e){
                    MessageDialog.MessageDialog(context,"Exception",""+e.toString());
                    hidepDialog();
                }

            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", ""+error);
                MessageDialog.MessageDialog(context,"Error",""+error.toString());
                hidepDialog();
            }
        } ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("DeviceID", DeviceID);
                params.put("UserID", UserID);
                params.put("SessionID", SessionID);
                params.put("DivisionID", DivisionID);
                params.put("CompanyID", CompanyID);
                params.put("BranchID", BranchID);
                params.put("Barcode", Barcode);
                params.put("OrderID", OrderID);
                Log.d(TAG,"Search barcode or style parameters:"+params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
    }
}
