package inventory.analysis.catalogue.adapter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
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
import android.widget.TableLayout;
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
import com.singlagroup.customwidgets.ConditionLibrary;
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
import inventory.analysis.catalogue.responseitemlist.ItemList;
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
public class RecyclerItemGridAdapter extends RecyclerView.Adapter<RecyclerItemGridAdapter.RecyclerItemGridHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<ItemList> mapList;
    private int flag=0,StockType=1;
    private CloseOrBookDataset closeOrBookDataset;
    android.view.Display display;
    int height,width;
    RecyclerView recyclerView;
    ProgressDialog spotsDialog;
    private static String TAG = RecyclerItemGridAdapter.class.getSimpleName();
    public RecyclerItemGridAdapter(Context context, List<ItemList> mapList, int flag, CloseOrBookDataset closeOrBookDataset,int StockType) {
        this.context=context;
        this.mapList=mapList;
        this.flag=flag;
        this.closeOrBookDataset=closeOrBookDataset;
        this.StockType = StockType;
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
        View v = inflater.inflate(R.layout.cardview_item_design_for_inventory, parent, false);
        RecyclerItemGridHolder viewHolder=new RecyclerItemGridHolder(v);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(RecyclerItemGridHolder holder, final int position) {

        final int pos=position;
        //TODO: Set Cart Indicator
        if (mapList.get(position).getCartStatus()==1){
            holder.imageCartIndicator.setVisibility(View.VISIBLE);
        }else {
            holder.imageCartIndicator.setVisibility(View.GONE);
        }
        //TODO: Set Item Code
        holder.txtView.setText(mapList.get(position).getItemCode());
        holder.txtView.setBackground(context.getResources().getDrawable(R.drawable.textview_background_round));
        //TODO: Set Stock
        if (StockType == 0){
            String ItemStock = ConditionLibrary.ConvertStringToString(mapList.get(position).getStock())+" "+mapList.get(position).getUnitName();//(StaticValues.stockFlag==0)?"":((StaticValues.stockFlag==1)?""+stock+" "+mapList.get(position).getUnitName():((stock>=StaticValues.stockFormula)?"> "+StaticValues.stockFormula:""+stock+" "+mapList.get(position).getUnitName()));
            holder.txtViewStock.setText("HND "+ItemStock);
        }else if (StockType == 1){
            String ItemStock = ConditionLibrary.ConvertStringToString(mapList.get(position).getSalebleStock())+" "+mapList.get(position).getUnitName();//(StaticValues.stockFlag==0)?"":((StaticValues.stockFlag==1)?""+stock+" "+mapList.get(position).getUnitName():((stock>=StaticValues.stockFormula)?"> "+StaticValues.stockFormula:""+stock+" "+mapList.get(position).getUnitName()));
            holder.txtViewStock.setText("SAL "+ItemStock);
        }else if (StockType == 2){
            String ItemStock = ConditionLibrary.ConvertStringToString(mapList.get(position).getReserveStock())+" "+mapList.get(position).getUnitName();//(StaticValues.stockFlag==0)?"":((StaticValues.stockFlag==1)?""+stock+" "+mapList.get(position).getUnitName():((stock>=StaticValues.stockFormula)?"> "+StaticValues.stockFormula:""+stock+" "+mapList.get(position).getUnitName()));
            holder.txtViewStock.setText("RES "+ItemStock);
        }else if (StockType == 3){
            String ItemStock = ConditionLibrary.ConvertStringToString(mapList.get(position).getRejetionStock())+" "+mapList.get(position).getUnitName();//(StaticValues.stockFlag==0)?"":((StaticValues.stockFlag==1)?""+stock+" "+mapList.get(position).getUnitName():((stock>=StaticValues.stockFormula)?"> "+StaticValues.stockFormula:""+stock+" "+mapList.get(position).getUnitName()));
            holder.txtViewStock.setText("REJ "+ItemStock);
        }else if (StockType == 4){
            String ItemStock = ConditionLibrary.ConvertStringToString(mapList.get(position).getProdPurchStock())+" "+mapList.get(position).getUnitName();//(StaticValues.stockFlag==0)?"":((StaticValues.stockFlag==1)?""+stock+" "+mapList.get(position).getUnitName():((stock>=StaticValues.stockFormula)?"> "+StaticValues.stockFormula:""+stock+" "+mapList.get(position).getUnitName()));
            holder.txtViewStock.setText("PPP "+ItemStock);
        }else if (StockType == 5){
            String ItemStock = ConditionLibrary.ConvertStringToString(mapList.get(position).getPendingDel())+" "+mapList.get(position).getUnitName();//(StaticValues.stockFlag==0)?"":((StaticValues.stockFlag==1)?""+stock+" "+mapList.get(position).getUnitName():((stock>=StaticValues.stockFormula)?"> "+StaticValues.stockFormula:""+stock+" "+mapList.get(position).getUnitName()));
            holder.txtViewStock.setText("PDL "+ItemStock);
        }
        holder.txtViewStock.setBackground(context.getResources().getDrawable(R.drawable.textview_background_round));
        //TODO: Total Calor
        if (mapList.get(position).getTotalColor()==null) {
            holder.txtColor.setText("");
            holder.txtColor.setVisibility(View.GONE);
        }else{
            holder.txtColor.setText("" + (mapList.get(position).getTotalColor() == null ? "" : mapList.get(position).getTotalColor()));
            holder.txtColor.setVisibility(View.VISIBLE);
        }
        //TODO: Set Discount
        double discount = (mapList.get(position).getDisc1()==null ? 0 : mapList.get(position).getDisc1());
        if (discount == 0){
            holder.txtDiscount.setVisibility(View.GONE);
        }else{
            holder.txtDiscount.setText(""+ConditionLibrary.ConvertDoubleToString(discount));
            holder.txtDiscount.setVisibility(View.VISIBLE);
        }
        //TODO: Set In Quantity
        if (mapList.get(position).getTotalProd()==0) {
            holder.txtInQty.setText("");
        }else {
            holder.txtInQty.setText("InQ\n" + ConditionLibrary.ConvertDoubleToString(mapList.get(position).getTotalProd()));
            holder.txtInQty.setBackground(context.getResources().getDrawable(R.drawable.textview_background_round));
        }
        //TODO: Set Out Quantity
        if (mapList.get(position).getTotalSale()==0) {
            holder.txtOutQty.setText("");
        }else {
            holder.txtOutQty.setText("OutQ\n" + ConditionLibrary.ConvertDoubleToString(mapList.get(position).getTotalSale()));
            holder.txtOutQty.setBackground(context.getResources().getDrawable(R.drawable.textview_background_round));
        }
        //TODO: Set Last In Days
        if (mapList.get(position).getLastInDays()==0) {
            holder.txtLastInDays.setText("Today");
            holder.txtLastInDays.setBackground(context.getResources().getDrawable(R.drawable.textview_background_round));
        }else {
            if (mapList.get(position).getLastInDays()< 0) {
                holder.txtLastInDays.setText("");
            }else {
                holder.txtLastInDays.setText("LIn\n" + DateFormatsMethods.YearsMonthsDaysCount(DateFormatsMethods.daysAgo(mapList.get(position).getLastInDays())));
                holder.txtLastInDays.setBackground(context.getResources().getDrawable(R.drawable.textview_background_round));
            }
        }
        //TODO: Set Last Out Days
        if (mapList.get(position).getLastOutDays()==0) {
            holder.txtLastOutDays.setText("Today");
            holder.txtLastOutDays.setBackground(context.getResources().getDrawable(R.drawable.textview_background_round));
        }else {
            if (mapList.get(position).getLastOutDays()< 0) {
                holder.txtLastOutDays.setText("");
            }else {
                holder.txtLastOutDays.setText("LOut\n" + DateFormatsMethods.YearsMonthsDaysCount(DateFormatsMethods.daysAgo(mapList.get(position).getLastOutDays())));
                holder.txtLastOutDays.setBackground(context.getResources().getDrawable(R.drawable.textview_background_round));
            }
        }
        //TODO: Set Item Create Days
        if (mapList.get(position).getItemCreateDays()==0) {
            holder.txtItemCreateDays.setText("");
        }else {
            holder.txtItemCreateDays.setText("CR:" + DateFormatsMethods.YearsMonthsDaysCount(DateFormatsMethods.daysAgo(mapList.get(position).getItemCreateDays())));
            holder.txtItemCreateDays.setBackground(context.getResources().getDrawable(R.drawable.textview_background_round));
        }
        //TODO: Set Ageing Days
        if (mapList.get(position).getAgeingDays()==0) {
            holder.txtAgeingDays.setText("");
        }else {
            holder.txtAgeingDays.setText("AG:" + DateFormatsMethods.YearsMonthsDaysCount(DateFormatsMethods.daysAgo(mapList.get(position).getAgeingDays())));
            holder.txtAgeingDays.setBackground(context.getResources().getDrawable(R.drawable.textview_background_round));
        }
        //TODO: Set Rate and Discount Rate
        String Rate = mapList.get(position).getRate();//(StaticValues.rateFlag==0)?"":((rate<0)?"Not available":"₹"+ rate);
        String DisRate = mapList.get(position).getDiscountRate();
        holder.txtViewPrice.setBackground(context.getResources().getDrawable(R.drawable.textview_background_round));
        if (!Rate.equals(DisRate)){
            holder.txtViewPrice.setText("₹" + Rate);
            holder.txtViewPrice.setPaintFlags(holder.txtViewPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.txtViewDiscountedPrice.setText("₹" + DisRate);
            holder.txtViewDiscountedPrice.setTextColor(context.getResources().getColor(R.color.Maroon));
            holder.txtViewDiscountedPrice.setBackground(context.getResources().getDrawable(R.drawable.textview_background_round));
            holder.txtViewDiscountedPrice.setVisibility(View.VISIBLE);
        }else{
            holder.txtViewPrice.setText("₹" + Rate);
            holder.txtViewDiscountedPrice.setText("₹" + DisRate);
            holder.txtViewDiscountedPrice.setVisibility(View.GONE);
        }
        //TODO: Set All Stock Table Layout
        setTableLayout(mapList.get(position),holder.tableLayout);
        //TODO: Set Image and card view size
        if(flag==0) {
            holder.imageView.getLayoutParams().width = width;
            holder.imageView.getLayoutParams().height = height;
            holder.cardView.getLayoutParams().width = width;
            holder.cardView.getLayoutParams().height = height+100;
            Picasso.with(context).load(mapList.get(position).getImageUrl()).placeholder(R.drawable.placeholder_new).into(holder.imageView);
        }else{
            holder.imageView.getLayoutParams().width = width;
            holder.imageView.getLayoutParams().height = height-100;
            holder.cardView.getLayoutParams().width= RelativeLayout.LayoutParams.MATCH_PARENT;
            holder.cardView.getLayoutParams().height = RelativeLayout.LayoutParams.MATCH_PARENT;
            String Path = mapList.get(position).getImageUrl();//.replaceAll("440x600", "1025x1400");
            Picasso.with(context).load(Path).placeholder(R.drawable.placeholder_new).into(holder.imageView);
        }
        //TODO: Set OnClick of image view
        holder.imageView.setTag(mapList.get(position).getItemCode());
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String status = NetworkUtils.getConnectivityStatusString(context);
//                if (!status.contentEquals("No Internet Connection")) {
//                    LoginActivity obj = new LoginActivity();
//                    String[] str = obj.GetSharePreferenceSession(context);
//                    if (str != null)
//                        BarcodeSearchViewPagerActivity.Barcode = v.getTag().toString();
//                        StaticValues.OrderViewIntentFlag = 1;
//                        StaticValues.CatalogueFlag = 1;
//                        CallVolleyStyleOrBarcodeSearch(str[3], str[4], str[0], str[5], str[14], str[15], BarcodeSearchViewPagerActivity.Barcode, closeOrBookDataset.getOrderID());
//                } else {
//                    MessageDialog.MessageDialog(context, "", status);
//                }
//                Intent intent=new Intent(context, ItemDetailsActivity.class);
//                intent.putExtra("ItemID", v.getTag().toString());
//                intent.putExtra("ColorID", "ColorID");
//                intent.putExtra("ImagePath", mapList.get(position).getItemImage());
//                intent.putExtra("PartyDetails", closeOrBookDataset);
//                context.startActivity(intent);
            }
        });
        //TODO: Set Wishlist Checkbox
        holder.checkBox.setChecked((mapList.get(position).getWishlistStatus()==0)?false:true);
        holder.checkBox.setTag(mapList.get(position));
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                ItemList recyclerItemDataset = (ItemList) cb.getTag();
                recyclerItemDataset.setWishlistStatus((cb.isChecked()==true)?1:0);
                mapList.get(pos).setWishlistStatus((cb.isChecked()==true)?1:0);
                String status = NetworkUtils.getConnectivityStatusString(context);
                if(cb.isChecked()){
                    if (!status.contentEquals("No Internet Connection")) {
                        LoginActivity obj=new LoginActivity();
                        String[] str = obj.GetSharePreferenceSession(context);
                        if (str!=null  && !mapList.get(pos).getItemID().isEmpty()) {
                            //TODO: Call Volley Wishlist
                            CallVolleyAddRemoveWishlist(str[3], str[0], str[14], str[4], str[5],str[3],str[4], ""+mapList.get(pos).getItemID(), "","","","","","","","","","0",""+CommanStatic.AppType,"0");
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
                            CallVolleyAddRemoveWishlist(str[3], str[0], str[14], str[4], str[5],str[3],str[4], ""+mapList.get(pos).getItemID(), "","","","","","","","","","0",""+CommanStatic.AppType,"1");
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
        //TODO: Set OnClick of Totol Color and Total SubItem
        holder.txtColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CustomDialog(mapList.get(position).getItemID(),mapList.get(position).getItemName(),mapList.get(position).getItemCode(),mapList.get(position).getRate(),0);
            }
        });
        //TODO: Set OnClick of Similar Items
        holder.imageViewSimilarItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CustomDialog(mapList.get(position).getItemID(),mapList.get(position).getItemName(),mapList.get(position).getItemCode(),mapList.get(position).getRate(),1);
            }
        });
    }
    @Override
    public int getItemCount() {

        return mapList==null?0:mapList.size();
    }
    public class RecyclerItemGridHolder extends RecyclerView.ViewHolder {

        TextView txtView,txtViewPrice,txtViewDiscountedPrice,txtViewStock,txtColor,txtDiscount,txtLastInDays,txtLastOutDays,txtItemCreateDays,txtAgeingDays,txtInQty,txtOutQty;
        ImageView imageView,imageViewSimilarItems,imageCartIndicator;
        CheckBox checkBox;
        CardView cardView;
        TableLayout tableLayout;
        public RecyclerItemGridHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            imageView = (ImageView) itemView.findViewById(R.id.imageView_icon);
            imageViewSimilarItems = (ImageView) itemView.findViewById(R.id.imageViewSimilarItems);
            imageCartIndicator = (ImageView) itemView.findViewById(R.id.imageView_CartIndicator);
            txtView = (TextView) itemView.findViewById(R.id.textView_title);
            txtViewPrice = (TextView) itemView.findViewById(R.id.textView_title_price);
            txtViewDiscountedPrice = (TextView) itemView.findViewById(R.id.textView_title_price_discounted_rate);
            txtViewStock = (TextView) itemView.findViewById(R.id.textView_item_stock);
            txtColor = (TextView) itemView.findViewById(R.id.textView_Color);
            txtDiscount = (TextView) itemView.findViewById(R.id.textView_Discount);
            txtLastInDays = (TextView) itemView.findViewById(R.id.textView_last_in_days);
            txtLastOutDays = (TextView) itemView.findViewById(R.id.textView_last_out_days);
            txtItemCreateDays = (TextView) itemView.findViewById(R.id.textView_item_create_days);
            txtAgeingDays = (TextView) itemView.findViewById(R.id.textView_ageing_days);
            txtInQty = (TextView) itemView.findViewById(R.id.textView_in_quantity);
            txtOutQty = (TextView) itemView.findViewById(R.id.textView_out_quantity);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox_wishlist);
            tableLayout = (TableLayout) itemView.findViewById(R.id.table_Layout);
        }
    }
    //TODO: Display TableLayout
    private void setTableLayout(ItemList dataset, TableLayout tableLayout) {

        //TODO: TableLayout set
        tableLayout.removeAllViewsInLayout();
        tableLayout.removeAllViews();
        View v = inflater.inflate(R.layout.table_row_3_column_autofit_text, tableLayout, false);
        TextView txtHeader = (TextView) v.findViewById(R.id.content_column_1);
        TextView txt = (TextView) v.findViewById(R.id.content_column_2);
        TextView txt1 = (TextView) v.findViewById(R.id.content_column_3);
//        txtHeader.setTextSize(8);
//        txt.setTextSize(8);
//        txt1.setTextSize(8);
        if (!dataset.getSalebleStock().equals("0.00") && !dataset.getSalebleStock().equals(".00")  && !dataset.getSalebleStock().equals("0")) {
            //TODO: 1st Row
            txtHeader.setText("SAL " + dataset.getSalebleStock());
        }
        if (!dataset.getStock().equals("0.00") && !dataset.getStock().equals(".00")  && !dataset.getStock().equals("0")) {
            txt.setText("HND " + dataset.getStock());
        }
        if (!dataset.getReserveStock().equals("0.00")  && !dataset.getReserveStock().equals(".00")  && !dataset.getReserveStock().equals("0")) {
            txt1.setText("RES " + dataset.getReserveStock());
        }
        tableLayout.addView(v);
        //TODO: 2nd Row
        v = inflater.inflate(R.layout.table_row_3_column_autofit_text, tableLayout, false);
        txtHeader = (TextView) v.findViewById(R.id.content_column_1);
        txt = (TextView) v.findViewById(R.id.content_column_2);
        txt1 = (TextView) v.findViewById(R.id.content_column_3);
//        txtHeader.setTextSize(8);
//        txt.setTextSize(8);
//        txt1.setTextSize(8);
        if (!dataset.getRejetionStock().equals("0.00")  && !dataset.getRejetionStock().equals(".00")  && !dataset.getRejetionStock().equals("0")) {
            txtHeader.setText("REJ " + dataset.getRejetionStock());
        }
        if (!dataset.getProdPurchStock().equals("0.00")  && !dataset.getProdPurchStock().equals(".00")  && !dataset.getProdPurchStock().equals("0")) {
            txt.setText("PPP " + dataset.getProdPurchStock());
        }
        if (!dataset.getPendingDel().equals("0.00")  && !dataset.getPendingDel().equals(".00")  && !dataset.getPendingDel().equals("0")) {
            txt1.setText("PDL " + dataset.getPendingDel());
        }
        tableLayout.addView(v);

    }
    private void CallVolleyAddRemoveWishlist(final String DeviceID, final String SessionID, final String CompanyID, final String UserID, final String DivisionID, final String ToDeviceID, final String ToUserID, final String ItemID, final String SubItemID, final String ColorID, final String SizeID, final String GroupID, final String PartyID, final String SubPartyID, final String RefName, final String Remarks, final String MasterID, final String MasterType, final String AppType, final String DelFlag){
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
