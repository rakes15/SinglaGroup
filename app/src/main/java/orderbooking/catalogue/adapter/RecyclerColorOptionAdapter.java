package orderbooking.catalogue.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

import orderbooking.Database_Sqlite.DatabaseSqlLiteHandlerOrderBooking;
import orderbooking.StaticValues;
import orderbooking.barcode_search.BarcodeSearchViewPagerActivity;
import orderbooking.barcode_search.OrderBookingActivity;
import orderbooking.barcode_search.SubItemActivity;
import orderbooking.barcode_search.WithoutSubItemActivity;
import orderbooking.customerlist.datasets.CloseOrBookDataset;
import services.NetworkUtils;

/**
 * Created by Rakesh on 15-Feb-16.
 */
public class RecyclerColorOptionAdapter extends RecyclerView.Adapter<RecyclerColorOptionAdapter.RecyclerSimilarColorHolder>{

    private Context context;
    private LayoutInflater inflater;
    private CloseOrBookDataset closeOrBookDataset;
    private List<RecyclerColorOptionDataSet> mapList;
    private ProgressDialog spotsDialog;
    int width,height;
    private static String TAG = RecyclerColorOptionAdapter.class.getSimpleName();
    public RecyclerColorOptionAdapter(Context context, List<RecyclerColorOptionDataSet> mapList,CloseOrBookDataset closeOrBookDataset) {
        this.context=context;
        this.mapList=mapList;
        this.closeOrBookDataset=closeOrBookDataset;
        this.inflater = LayoutInflater.from(context);
        spotsDialog=new ProgressDialog(context);
        spotsDialog.setMessage("Please wait...");
        spotsDialog.setCanceledOnTouchOutside(false);
        DisplayMetrics dm = this.context.getResources().getDisplayMetrics();
        float screenWidth  = StaticValues.mViewWidth / dm.xdpi;
        if(screenWidth > 1.5) {
            int w = (int)(1.5*dm.xdpi);
            this.width = w;
            this.height = (int)(w*1.366);
        }else if(screenWidth < 1.5){
            this.width = StaticValues.mViewWidth;
            this.height = StaticValues.mViewHeight;
        }

    }
    @Override
    public RecyclerSimilarColorHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.cardview_item_similar_color_design, parent, false);
        RecyclerSimilarColorHolder viewHolder=new RecyclerSimilarColorHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerSimilarColorHolder holder, final int position) {

        holder.cardView.getLayoutParams().width = width;
        holder.cardView.getLayoutParams().height = height;
        holder.txtViewColorName.setText(mapList.get(position).getColorName());
        int stock=Integer.valueOf(mapList.get(position).getColorStock());
        String ColorStock=(StaticValues.stockFlag==0)?"":((StaticValues.stockFlag==1)?""+stock+" "+mapList.get(position).getUnit():((stock>=StaticValues.stockFormula)?"> "+StaticValues.stockFormula:""+stock+" "+mapList.get(position).getUnit()));
        holder.txtViewColorStock.setText(""+ColorStock);
        String temp=mapList.get(position).getItemImage().replaceAll("440x600","300x410");
        Picasso.with(context).load(temp).placeholder(R.drawable.placeholder_new).into(holder.imageView);
        holder.imageView.setTag(mapList.get(position).getItemCode());
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
    }
    @Override
    public int getItemCount() {

        return mapList==null?0:mapList.size();
    }
    public class RecyclerSimilarColorHolder extends RecyclerView.ViewHolder {

        TextView txtView,txtViewPrice,txtViewColorName,txtViewColorStock;
        ImageView imageView;
        CardView cardView;

        public RecyclerSimilarColorHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.card_view);
            imageView = (ImageView) itemView.findViewById(R.id.imageView_icon);
//            txtView = (TextView) itemView.findViewById(R.id.textView_title);
//            txtViewPrice = (TextView) itemView.findViewById(R.id.textView_title_price);
            txtViewColorName = (TextView) itemView.findViewById(R.id.textView_ColorName);
            txtViewColorStock = (TextView) itemView.findViewById(R.id.textView_ColorStock);
        }
    }

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
    private void showpDialog() {
        spotsDialog.show();
    }
    private void hidepDialog() {
        spotsDialog.dismiss();
    }
}
