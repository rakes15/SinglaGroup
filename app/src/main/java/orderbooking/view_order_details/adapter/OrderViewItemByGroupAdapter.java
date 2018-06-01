package orderbooking.view_order_details.adapter;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.singlagroup.AppController;
import com.singlagroup.LoginActivity;
import com.singlagroup.R;
import com.singlagroup.customwidgets.DateFormatsMethods;
import com.singlagroup.customwidgets.MessageDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import orderbooking.Database_Sqlite.DatabaseSqlLiteHandlerOrderBooking;
import orderbooking.Database_Sqlite.DatabaseSqlLiteHandlerOrderViewDetails;
import orderbooking.StaticValues;
import orderbooking.barcode_search.BarcodeSearchViewPagerActivity;
import orderbooking.barcode_search.OrderBookingActivity;
import orderbooking.barcode_search.SubItemActivity;
import orderbooking.barcode_search.WithoutSubItemActivity;
import orderbooking.customerlist.datasets.CloseOrBookDataset;
import orderbooking.customerlist.temp.BookOrderAdapter;
import orderbooking.print.PrintReportActivity;
import orderbooking.view_order_details.dataset.OrderViewItemByGroupDataset;
import services.NetworkUtils;

/**
 * Created by Rakesh on 18-May-17.
 */
public class OrderViewItemByGroupAdapter extends RecyclerView.Adapter<OrderViewItemByGroupAdapter.RecyclerItemByGroupHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<OrderViewItemByGroupDataset> mapList,filterItems;
    private int flag=0;
    android.view.Display display;
    int height,width;
    RecyclerView recyclerView;
    ProgressDialog spotsDialog;
    private DatabaseSqlLiteHandlerOrderViewDetails DBOrderView;
    private int MDApplicable;
    private int SubItemApplicable;
    private static String TAG = OrderViewItemByGroupAdapter.class.getSimpleName();
    public OrderViewItemByGroupAdapter(Context context, List<OrderViewItemByGroupDataset> mapList, int flag) {
        this.context=context;
        this.mapList=mapList;
        this.filterItems = new ArrayList<>();
        this.filterItems.addAll(this.mapList);
        this.flag=flag;
        this.DBOrderView = new DatabaseSqlLiteHandlerOrderViewDetails(context);
        this.inflater = LayoutInflater.from(context);
        display = ((WindowManager) context.getSystemService(context.WINDOW_SERVICE)).getDefaultDisplay();
        spotsDialog=new ProgressDialog(context);
        spotsDialog.setCanceledOnTouchOutside(false);
        if(flag==0){
            width= StaticValues.mViewWidth-5;
            height=StaticValues.mViewHeight-10;
        }
        else if(flag==1)
        {
            width=StaticValues.sViewWidth;
            height=StaticValues.sViewHeight;
        }
    }
    @Override
    public RecyclerItemByGroupHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.cardview_item_design_by_order_view, parent, false);
        RecyclerItemByGroupHolder viewHolder=new RecyclerItemByGroupHolder(v);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(RecyclerItemByGroupHolder holder, int position) {

        final int pos=position;
        MDApplicable = mapList.get(position).getMDApplicable();
        SubItemApplicable = mapList.get(position).getSubItemApplicable();

        holder.txtView.setText(mapList.get(position).getItemCode());
        holder.txtViewStock.setText(""+mapList.get(position).getItemStock()+" "+mapList.get(position).getUnit());
        if (MDApplicable == 1) {
            holder.txtColorName.setText("" + mapList.get(position).getColorName());
            holder.txtColor.setText("" + mapList.get(position).getTotalColor());
            holder.txtColor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CustomDialog(mapList.get(pos).getOrderID(),mapList.get(pos).getGroupID(),mapList.get(pos).getItemID(),mapList.get(pos).getItemCode(),mapList.get(pos).getRate());
                }
            });
        }else{
            if (SubItemApplicable == 1){
                holder.txtColorName.setText("" + mapList.get(position).getColorName());
                holder.txtColor.setText("" + mapList.get(position).getTotalColor());
                holder.txtColor.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_subitem_grey, 0, 0, 0);
                holder.txtColor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CustomDialog(mapList.get(pos).getOrderID(),mapList.get(pos).getGroupID(),mapList.get(pos).getItemID(),mapList.get(pos).getItemCode(),mapList.get(pos).getRate());
                    }
                });
            }else{
                holder.txtColor.setVisibility(View.GONE);
            }
        }
        holder.txtViewPrice.setText("₹" + mapList.get(position).getRate() +" / "+ "₹" + mapList.get(position).getMrp());
        if(flag==0) {
            holder.cardView.getLayoutParams().width = width;
            holder.cardView.getLayoutParams().height = height;
            Picasso.with(context).load(mapList.get(position).getItemImage()).placeholder(R.drawable.placeholder_new).into(holder.imageView);
            //Glide.with(context).load(mapList.get(position).getItemImage()).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.placeholder_new).into(holder.imageView);
        }else{
            holder.cardView.getLayoutParams().width= RelativeLayout.LayoutParams.MATCH_PARENT;
            holder.cardView.getLayoutParams().height = RelativeLayout.LayoutParams.MATCH_PARENT;
            //String Path = mapList.get(position).getItemImage().replaceAll("440x600", "1025x1400");
            Picasso.with(context).load(mapList.get(position).getItemImage()).placeholder(R.drawable.placeholder_new).into(holder.imageView);
            //Glide.with(context).load(mapList.get(position).getItemImage()).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.placeholder_new).into(holder.imageView);
        }
        holder.cardView.setTag(R.id.dataset,mapList.get(pos));
        holder.cardView.setTag(R.id.position,pos);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderViewItemByGroupDataset dataset = (OrderViewItemByGroupDataset) v.getTag(R.id.dataset);
                if (StaticValues.PushOrderFlag == 0) {
                    String status = NetworkUtils.getConnectivityStatusString(context);
                    if (!status.contentEquals("No Internet Connection")) {
                        LoginActivity obj = new LoginActivity();
                        String[] str = obj.GetSharePreferenceSession(context);
                        if (str != null)
                            BarcodeSearchViewPagerActivity.Barcode = dataset.getBarcode();
                        StaticValues.OrderViewIntentFlag = 1;
                        CallVolleyStyleOrBarcodeSearch(str[3], str[4], str[0], str[5], str[14], str[15], dataset.getItemCode(), dataset.getOrderID());
                    } else {
                        MessageDialog.MessageDialog(context, "", status);
                    }
                }
                else if (StaticValues.PushOrderFlag == 2) { //TODO: Catalogue Booking
                    //((Activity)context).finish();
                    String status = NetworkUtils.getConnectivityStatusString(context);
                    if (!status.contentEquals("No Internet Connection")) {
                        LoginActivity obj = new LoginActivity();
                        String[] str = obj.GetSharePreferenceSession(context);
                        if (str != null)
                            BarcodeSearchViewPagerActivity.Barcode = dataset.getBarcode();
                        StaticValues.OrderViewIntentFlag = 1;
                        CallVolleyStyleOrBarcodeSearch(str[3], str[4], str[0], str[5], str[14], str[15], dataset.getItemCode(), dataset.getOrderID());
                    } else {
                        MessageDialog.MessageDialog(context, "", status);
                    }
                }
            }
        });
        holder.imageInfo.setTag(R.id.dataset,mapList.get(pos));
        holder.imageInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final OrderViewItemByGroupDataset dataset = (OrderViewItemByGroupDataset) v.getTag(R.id.dataset);
                Intent intent = new Intent(context, PrintReportActivity.class);
                intent.putExtra("OrderID", dataset.getOrderID());
                intent.putExtra("GroupID", dataset.getGroupID());
                intent.putExtra("ItemID", dataset.getItemID());
                context.startActivity(intent);

            }
        });
        if (mapList.get(position).getColorID().isEmpty()){
            holder.imageViewExDatetime.setVisibility(View.VISIBLE);
        }else{
            holder.imageViewExDatetime.setVisibility(View.GONE);
        }
        holder.imageViewExDatetime.setTag(R.id.dataset,mapList.get(pos));
        holder.imageViewExDatetime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderViewItemByGroupDataset dataset = (OrderViewItemByGroupDataset) v.getTag(R.id.dataset);
                DialogExpectedDeliveryDatetimeUpdate(dataset);
            }
        });
    }
    @Override
    public int getItemCount() {

        return (null != filterItems ? filterItems.size() : 0);
    }
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return filterItems.get(position);
    }
    //TODO: Filter Search
    public void filter(final String text) {

        // Searching could be complex..so we will dispatch it to a different thread...
        new Thread(new Runnable() {
            @Override
            public void run() {

                // Clear the filter list
                filterItems.clear();

                // If there is no search value, then add all original list items to filter list
                if (TextUtils.isEmpty(text)) {

                    filterItems.addAll(mapList);

                } else {
                    System.out.println("Search:"+text);
                    // Iterate in the original List and add it to filter list...
                    for (OrderViewItemByGroupDataset item : mapList) {
                        if(item.getColorName()!=null && item.getItemCode()!=null && item.getItemName()!=null && item.getRate()!=null && item.getItemStock()!=null)
                            if (item.getColorName().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getItemCode().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getItemName().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getRate().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getItemStock().toLowerCase().contains(text.toLowerCase()) ||
                                    String.valueOf(item.getTotalColor()).toLowerCase().contains(text.toLowerCase()) ) {
                                // Adding Matched items
                                filterItems.add(item);
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
    private void CustomDialog(final String OrderID,final String GroupID,final String ItemID, String Itemcode, String rate){
        Dialog dialog = new Dialog(new ContextThemeWrapper(context, R.style.DialogSlideAnim));
        dialog.setContentView(R.layout.dialog_color_similar);
        android.view.Display display = ((WindowManager)context.getSystemService(context.WINDOW_SERVICE)).getDefaultDisplay();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
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
        String Rate="\t\tPrice:" + rate;
        txtViewItemCode.setText("ItemCode: " + Itemcode +Rate);
        recyclerView = (RecyclerView) dialog.findViewById(R.id.recyclerView_Similar);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        //TODO: ColorOption List Execute

        OrderViewColorOptionAdapter recyclerColorOptionAdapter = new OrderViewColorOptionAdapter(context, DBOrderView.getColorList(OrderID, GroupID, ItemID,MDApplicable,SubItemApplicable),dialog);
        recyclerView.setAdapter(recyclerColorOptionAdapter);
    }
    public class RecyclerItemByGroupHolder extends RecyclerView.ViewHolder {

        TextView txtView,txtViewPrice,txtViewStock,txtColor,txtColorName;
        ImageView imageView,imageInfo,imageViewExDatetime;
        CardView cardView;
        public RecyclerItemByGroupHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            imageView = (ImageView) itemView.findViewById(R.id.imageView_icon);
            imageInfo = (ImageView) itemView.findViewById(R.id.image_view_info);
            imageViewExDatetime = (ImageView) itemView.findViewById(R.id.imageView_expected_date);
            txtView = (TextView) itemView.findViewById(R.id.textView_title);
            txtViewPrice = (TextView) itemView.findViewById(R.id.textView_title_price);
            txtViewStock = (TextView) itemView.findViewById(R.id.textView_item_stock);
            txtColor = (TextView) itemView.findViewById(R.id.textView_Color);
            txtColorName = (TextView) itemView.findViewById(R.id.textView_Color_Name);
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
                                    map.put("Mrp"+x, (jsonArrayDetails.getJSONObject(i).optString("MRP"+x) == null ? "0" : jsonArrayDetails.getJSONObject(i).optString("MRP"+x)));
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
                                        map.put("Mrp"+x, (jsonArrayDetails.getJSONObject(i).optString("MRP"+x) == null ? "0" : jsonArrayDetails.getJSONObject(i).optString("MRP"+x)));
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
                                        map.put("Mrp"+x, (jsonArrayDetails.getJSONObject(i).optString("MRP"+x) == null ? "0" : jsonArrayDetails.getJSONObject(i).optString("MRP"+x)));
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
    private void DialogExpectedDeliveryDatetimeUpdate(final OrderViewItemByGroupDataset dataset){
        final Dialog dialog = new Dialog(new ContextThemeWrapper(context, R.style.DialogSlideAnim));
        dialog.setContentView(R.layout.dialog_expected_delivery_date);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        final EditText editTextDelDate = (EditText) dialog.findViewById(R.id.ex_del_date);
        final EditText editTextDelTime = (EditText) dialog.findViewById(R.id.ex_del_time);
        editTextDelDate.setInputType(InputType.TYPE_NULL);
        editTextDelTime.setInputType(InputType.TYPE_NULL);
        editTextDelDate.setText(""+DateFormatsMethods.DateFormat_DD_MM_YYYY(DateFormatsMethods.getDateTime().substring(0,10)));
        editTextDelTime.setText(""+DateFormatsMethods.getDateTime().substring(11,16));
        Button btnUpdate = (Button) dialog.findViewById(R.id.button_Update);
        Button btnCancel = (Button) dialog.findViewById(R.id.button_Cancel);
        editTextDelDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance(TimeZone.getDefault()); // Get current date
                // Create the DatePickerDialog instance
                DatePickerDialog datePicker = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        DecimalFormat formatter = new DecimalFormat("00");
                        String ExDelDate = DateFormatsMethods.PastDateNotSelect(formatter.format(dayOfMonth) + "-" + formatter.format(month + 1) + "-"+ year);
                        editTextDelDate.setText(ExDelDate);
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                datePicker.setTitle("Select the date");
                datePicker.show();
            }
        });
        editTextDelTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance(TimeZone.getDefault()); // Get current date
                TimePickerDialog timePicker = new TimePickerDialog(context,new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // TODO Auto-generated method stub
                        try{
                            DecimalFormat formatter = new DecimalFormat("00");
                            String ExDelTime = formatter.format(hourOfDay)+":"+formatter.format(minute);
                            editTextDelTime.setText(""+ExDelTime);
                        }catch (Exception e) {
                            // TODO: handle exception
                            Log.e("ERRor", ""+e.toString());
                        }
                    }
                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), DateFormat.is24HourFormat(context));
                timePicker.setTitle("Select the Time");
                timePicker.show();
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ExDate = editTextDelDate.getText().toString();
                String ExTime = editTextDelTime.getText().toString();
                if (!ExDate.isEmpty() && !ExTime.isEmpty()) {
                    String ExDateTime = DateFormatsMethods.DateFormat_YYYY_MM_DD(ExDate) + " " + ExTime + ":00";
                    String status = NetworkUtils.getConnectivityStatusString(context);
                    if (!status.contentEquals("No Internet Connection")) {
                        LoginActivity obj = new LoginActivity();
                        String[] str = obj.GetSharePreferenceSession(context);
                        if (str != null)
                            if (StaticValues.editFlag == 1) {
                                CallVolleyExpectedDelDatetimeUpdate(str[3], str[4], str[0], str[5], str[14], str[15], dataset.getOrderID(), dataset.getGroupID(), dataset.getItemID(), ExDateTime);
                                dialog.dismiss();
                            }else{
                                MessageDialog.MessageDialog(context,"Alert","You don't have edit permission of this module");
                            }
                    }else {
                        MessageDialog.MessageDialog(context,"",status);
                    }
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
    private void CallVolleyExpectedDelDatetimeUpdate(final String DeviceID, final String UserID, final String SessionID, final String DivisionID, final String CompanyID, final String BranchID , final String OrderID, final String GroupID, final String ItemID,final String ExpectedDatetime){
        showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"TempOrderGorupSubGroupItemExpectedDateUpdate", new Response.Listener<String>()
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
                        MessageDialog.MessageDialog(context,"",Msg);
                    } else {
                        MessageDialog.MessageDialog(context,"",Msg);
                    }
                }catch (Exception e){
                    MessageDialog.MessageDialog(context,"Exception",""+e.toString());

                }
                hidepDialog();
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
                params.put("OrderID", OrderID);
                params.put("GroupID", GroupID);
                params.put("ItemID", ItemID);
                params.put("ExpectedDelDate", ExpectedDatetime);
                Log.d(TAG,"Search barcode or style parameters:"+params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
    }
}
