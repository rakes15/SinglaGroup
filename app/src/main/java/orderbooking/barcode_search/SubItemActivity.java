package orderbooking.barcode_search;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kassisdion.library.ViewPagerWithIndicator;
import com.singlagroup.AppController;
import com.singlagroup.LoginActivity;
import com.singlagroup.R;
import com.singlagroup.customwidgets.DateFormatsMethods;
import com.singlagroup.customwidgets.MessageDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import DatabaseController.CommanStatic;
import DatabaseController.DatabaseSqlLiteHandlerUserInfo;
import orderbooking.Database_Sqlite.DatabaseSqlLiteHandlerOrderBooking;
import orderbooking.StaticValues;
import orderbooking.catalogue.SlidingImageActivity;
import orderbooking.catalogue.responsedataset.ResponseImageListDataset;
import orderbooking.customerlist.temp.BookOrderAdapter;
import orderbooking.print.InProductionReportActivity;
import orderbooking.view_order_details.OrderViewOrPushActivity;
import retrofit.ApiClient;
import retrofit.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import services.NetworkUtils;
import stockcheck.StockCheckActivity;

/**
 * Created by Rakesh on 02-Nov-16.
 */
public class SubItemActivity extends AppCompatActivity{
    private Context context;
    ActionBar actionBar;
    private TableLayout tableLayoutItemDetails,tableLayoutBooking,tableLayoutExDeldate;
    private EditText editTxtRemarks;
    private ViewPager mViewPager;
    private ViewPagerWithIndicator viewPagerWithIndicator;
    private ProgressBar progressBar;
    private ProgressDialog progressDialog;
    private DatabaseSqlLiteHandlerOrderBooking DBHandler;
    private EditText[][] editText;
    private List<Map<String,String>> OutOfStock;
    private String ItemID = "",ItemCode = "", ExDelDate = "" , ExDelTime = "";
    private static String TAG = SubItemActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);} getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.fragment_table_layout);
        Initialization();
        setActionBarHeader();
        SubItemDetailsDisplay();
        CustomerNameShow();
        if (!ItemID.isEmpty()) {
            SubItemPriceStockListDisplay(ItemID);
            AllExpectedDeliveryDateTimes();
        } else {
            MessageDialog.MessageDialog(context,"","ItemId is blank");
        }
    }
    private void Initialization(){
        this.context = SubItemActivity.this;
        this.DBHandler = new DatabaseSqlLiteHandlerOrderBooking(context);
        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        tableLayoutItemDetails = (TableLayout) findViewById(R.id.table_Layout_item_details);
        tableLayoutBooking = (TableLayout) findViewById(R.id.table_Layout_booking);
        tableLayoutExDeldate = (TableLayout) findViewById(R.id.table_Layout_ex_del_date);
        editTxtRemarks = (EditText) findViewById(R.id.editText_remarks);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPagerWithIndicator = (ViewPagerWithIndicator)findViewById(R.id.viewPagerWithIndicator);
        progressBar = (ProgressBar) findViewById(R.id.ProgressBar);
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
    }
    private void setActionBarHeader(){
        try{
            if (!BookOrderAdapter.listMultiCustomer.isEmpty()){
                String Header="";
                for (int i=0;i<BookOrderAdapter.listMultiCustomer.size();i++) {
                    String Party = BookOrderAdapter.listMultiCustomer.get(i).getPartyName();
                    String SubParty = BookOrderAdapter.listMultiCustomer.get(i).getSubParty();
                    String RefName = BookOrderAdapter.listMultiCustomer.get(i).getRefName();
                    if (!SubParty.isEmpty()) {
                        Header += SubParty +" ("+Party+"), ";
                    }else if (!RefName.isEmpty()) {
                        Header += RefName +" ("+Party+"), ";
                    }else if (!RefName.isEmpty() && !SubParty.isEmpty()) {
                        Header += RefName +" ("+SubParty+"-"+Party+"), ";
                    }else{
                        Header += Party+" , ";
                    }
                }
                LayoutInflater inflator = (LayoutInflater) this .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v = inflator.inflate(R.layout.action_bar_header, null);
                ImageView imageView = (ImageView) v.findViewById(R.id.imageView_Back);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO: Activity Intent to Flag wise
                        if (StaticValues.OrderViewIntentFlag == 0) {
                            //TODO: Activity Intent to Parent Caption
                            Intent intent = new Intent(getApplicationContext(), BarcodeSearchViewPagerActivity.class);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                        }else if (StaticValues.OrderViewIntentFlag == 1) {
                            //TODO: Activity Intent to Order view Group by Item
                            finish();
                            overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                        }
                    }
                });
                TextView textView = (TextView) v.findViewById(R.id.text_view_header);
                textView.setSelected(true);
                textView.setWidth(StaticValues.sViewWidth-300);
                textView.setText("" + Header.substring(0,Header.length()-2));
                actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
                actionBar.setCustomView(v);
            }else{
                MessageDialog.MessageDialog(context,"Book order adapter","Something went wrong");
            }
        }catch (Exception e){
            MessageDialog.MessageDialog(context, "Exception", "Intent:"+e.toString());
        }
    }
    private void SubItemDetailsDisplay(){
        Map<String,String> mapItemDetails = DBHandler.getSubItemDetails();
        if (mapItemDetails!=null){
            ItemID = mapItemDetails.get("ItemID");
            ItemCode = mapItemDetails.get("ItemCode");
            //TODO:Item Code
            View v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayoutItemDetails, false);
            //v.setBackgroundColor(context.getResources().getColor(R.color.red_10));
            TextView txtHeader= (TextView) v.findViewById(R.id.header);
            txtHeader.setText((mapItemDetails.get("Barcode").isEmpty() ? "Item Code:" :"Item Code / Barcode:"));

            String ItemCode = (mapItemDetails.get("ItemCode").equals(BarcodeSearchViewPagerActivity.Barcode) ? MessageDialog.getColoredSpanned(mapItemDetails.get("ItemCode"),"#990033") :  MessageDialog.getColoredSpanned(mapItemDetails.get("ItemCode"),"#606060"));
            String Slash = MessageDialog.getColoredSpanned((mapItemDetails.get("Barcode").isEmpty() ? "": " / "), "#606060");
            String Barcode = (mapItemDetails.get("Barcode").equals(BarcodeSearchViewPagerActivity.Barcode) ? MessageDialog.getColoredSpanned(mapItemDetails.get("Barcode"),"#990033") :  MessageDialog.getColoredSpanned(mapItemDetails.get("Barcode"),"#606060"));
            TextView txt= (TextView) v.findViewById(R.id.content);
            txt.setText(Html.fromHtml(ItemCode+Slash+Barcode));
            tableLayoutItemDetails.addView(v);
            //TODO:Item Name
            v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayoutItemDetails, false);
            //v.setBackgroundColor(context.getResources().getColor(R.color.red_10));
            txtHeader= (TextView) v.findViewById(R.id.header);
            txtHeader.setText("Item Name:");

            txt= (TextView) v.findViewById(R.id.content);
            txt.setText(""+(mapItemDetails.get("ItemName")==null || mapItemDetails.get("ItemName").equals("null") ? "" :mapItemDetails.get("ItemName")));
            tableLayoutItemDetails.addView(v);
            //TODO:Item Code
            v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayoutItemDetails, false);
            //v.setBackgroundColor(context.getResources().getColor(R.color.red_10));
            txtHeader= (TextView) v.findViewById(R.id.header);
            txtHeader.setText("MainGroup / Group:");

            txt= (TextView) v.findViewById(R.id.content);
            txt.setText(""+mapItemDetails.get("MainGroupName")+ " / "+mapItemDetails.get("GroupName"));
            tableLayoutItemDetails.addView(v);

            //TODO: Remarks
            editTxtRemarks.setText(""+mapItemDetails.get("Remarks"));
            editTxtRemarks.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    DBHandler.UpdateRemarks(BookOrderAdapter.listMultiCustomer.get(0).getOrderID(),ItemID,String.valueOf(s),0);
                }
            });
        }else{
            MessageDialog.MessageDialog(context,"","Something went wrong");
        }
    }
    private void CustomerNameShow(){
        if (!BookOrderAdapter.listMultiCustomer.isEmpty()){
            int id=0;
            //TODO: Table row
            TableRow tableRow = new TableRow(context);
            tableRow.setId(id);
            tableRow.setPadding(5,10,16,10);
            //TODO: Size
            TextView txtHeader = new  TextView(context);
            txtHeader.setId(id+1);
            txtHeader.setText("SubItem(Code)");
            txtHeader.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
            txtHeader.setPadding(16,10,16,10);
            txtHeader.setTypeface(null, Typeface.BOLD);
            txtHeader.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
            tableRow.addView(txtHeader);

            for (int i=0; i<BookOrderAdapter.listMultiCustomer.size(); i++){
                String PartyName = (BookOrderAdapter.listMultiCustomer.get(i).getPartyName()==null || BookOrderAdapter.listMultiCustomer.get(i).getPartyName().equals("null")) ? "" : BookOrderAdapter.listMultiCustomer.get(i).getPartyName();
                String SubPartyName = (BookOrderAdapter.listMultiCustomer.get(i).getSubParty()==null || BookOrderAdapter.listMultiCustomer.get(i).getSubParty().equals("null")) ? "" : BookOrderAdapter.listMultiCustomer.get(i).getSubParty();
                String RefName = (BookOrderAdapter.listMultiCustomer.get(i).getRefName()==null || BookOrderAdapter.listMultiCustomer.get(i).getRefName().equals("null")) ? "" : BookOrderAdapter.listMultiCustomer.get(i).getRefName();
                //TODO: Party Name
                txtHeader = new  TextView(context);
                txtHeader.setId(100+i);
                txtHeader.setWidth(160);
                txtHeader.setSelected(true);
                txtHeader.setSingleLine(true);
                txtHeader.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
                txtHeader.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                txtHeader.setPadding(16,10,16,10);
                txtHeader.setTypeface(null, Typeface.BOLD);
                txtHeader.setTextColor(context.getResources().getColor(R.color.Brown));
                if (RefName.isEmpty()) {
                    if (SubPartyName.isEmpty()) {
                        txtHeader.setText("" + PartyName);
                    }else{
                        txtHeader.setText(SubPartyName +"\n" + PartyName);
                    }
                }else {
                    if (SubPartyName.isEmpty()) {
                        txtHeader.setText(RefName +"\n" + PartyName);
                    }else{
                        txtHeader.setText(RefName+"\n"+SubPartyName +"\n" + PartyName);
                    }
                }
                tableRow.addView(txtHeader);
            }
            //TODO: Rate
            txtHeader = new  TextView(context);
            txtHeader.setId(id+10);
            txtHeader.setText("Rate");
            txtHeader.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
            txtHeader.setPadding(16,10,16,10);
            txtHeader.setTypeface(null, Typeface.BOLD);
            txtHeader.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
            tableRow.addView(txtHeader);
            //TODO: Discount Rate
            txtHeader = new  TextView(context);
            txtHeader.setId(id+100);
            txtHeader.setText("Rate(%)");
            txtHeader.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_discount_red,0,0,0);
            txtHeader.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
            txtHeader.setPadding(16,10,16,10);
            txtHeader.setTypeface(null, Typeface.BOLD);
            txtHeader.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
            tableRow.addView(txtHeader);
            //TODO: MRP
            txtHeader = new  TextView(context);
            txtHeader.setId(id+20);
            txtHeader.setText("MRP");
            txtHeader.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
            txtHeader.setPadding(16,10,16,10);
            txtHeader.setTypeface(null, Typeface.BOLD);
            txtHeader.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
            tableRow.addView(txtHeader);
            //TODO: Stock
            txtHeader = new  TextView(context);
            txtHeader.setId(id+11);
            txtHeader.setText("Stock");
            txtHeader.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
            txtHeader.setPadding(16,10,16,10);
            txtHeader.setTypeface(null, Typeface.BOLD);
            txtHeader.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
            txtHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO: Call Stock Check
                    StockCheck();
                }
            });
            tableRow.addView(txtHeader);
            //TODO: Reserve Stock
            txtHeader = new  TextView(context);
            txtHeader.setId(id+12);
            txtHeader.setText("Res-Stock");
            txtHeader.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
            txtHeader.setPadding(16,10,16,10);
            txtHeader.setTypeface(null, Typeface.BOLD);
            txtHeader.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
            tableRow.addView(txtHeader);
            //TODO: In Production
            final TextView txtHeaderInProduction = new  TextView(context);
            txtHeaderInProduction.setId(id+13);
            txtHeaderInProduction.setTag(ItemID);
            txtHeaderInProduction.setText("In-Production");
            txtHeaderInProduction.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
            txtHeaderInProduction.setPadding(16,10,16,10);
            txtHeaderInProduction.setTypeface(null, Typeface.BOLD);
            txtHeaderInProduction.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
            tableRow.addView(txtHeaderInProduction);
            txtHeaderInProduction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String ItemID = view.getTag().toString();
                    Intent intent = new Intent(context, InProductionReportActivity.class);
                    intent.putExtra("Header",txtHeaderInProduction.getText().toString());
                    intent.putExtra("ItemID",ItemID);
                    startActivity(intent);
                    //MessageDialog.MessageDialog(context,"Alert","Comming Soon...");
                }
            });
            tableLayoutBooking.addView(tableRow);
        }
    }
    private void SubItemPriceStockListDisplay(final String ItemID){
        List<Map<String,String>> SubItemList = DBHandler.getSubItemList(ItemID);
        //System.out.println("Sub:"+SubItemList.toString()+"Size:"+SubItemList.size());
        int ImageStatus = 0;
        List<String> imageUrl = new ArrayList<>();
        if (!SubItemList.isEmpty()){
            editText = new EditText[StaticValues.MultiOrderSize + 1][SubItemList.size() + 1];
            for (int i = 0; i<SubItemList.size(); i++){
                //TODO: Table row
                TableRow tableRow = new TableRow(context);
                tableRow.setId(i+200);
                tableRow.setPadding(5,10,16,10);
                //TODO: Size
                TextView txtHeader = new  TextView(context);
                txtHeader.setId(i+1);
                //txtHeader.setTextSize(16);
                if (BarcodeSearchViewPagerActivity.Barcode.equals(SubItemList.get(i).get("Barcode"))){
                    txtHeader.setTextColor(context.getResources().getColor(R.color.Maroon));
                }else{
                    txtHeader.setTextColor(context.getResources().getColor(R.color.Black));
                }
                txtHeader.setTag(SubItemList.get(i).get("SubItemID"));
                txtHeader.setText(SubItemList.get(i).get("SubItemName")+"("+SubItemList.get(i).get("SubItemCode")+")");
                txtHeader.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
                txtHeader.setPadding(16,10,16,10);
                txtHeader.setTypeface(null, Typeface.BOLD);
                tableRow.addView(txtHeader);
                try {
                    for (int x = 0; x < StaticValues.MultiOrderSize; x++) {
                        final int Xs = x;
                        List<Map<String, String>> mapListQuantity = DBHandler.getSubItemQuantityList(BookOrderAdapter.listMultiCustomer.get(x).getOrderID(), x, ItemID,SubItemList.get(i).get("SubItemID"));
                        if (!mapListQuantity.isEmpty()) {
                            int pos = 0;
                            final int Ys = i;
                            //TODO:Quantity
                            String qty = (mapListQuantity.get(pos).get("Ord" + x).equals("0") || mapListQuantity.get(pos).get("Ord" + x) == "0" ? "" : mapListQuantity.get(pos).get("Ord" + x));
                            String Tag = mapListQuantity.get(pos).get("OrderID" + x) + "/" + SubItemList.get(i).get("SubItemID") + "/" + mapListQuantity.get(pos).get("ExpectedDate" + x)+ "/" + SubItemList.get(i).get("Stock")+ "/" + SubItemList.get(i).get("ReserveStock");
                            editText[x][i] = new EditText(context);
                            editText[x][i].setId(i + x + 500);
                            editText[x][i].setWidth(160);
                            editText[x][i].setHint("Qty");
                            editText[x][i].setSelectAllOnFocus(true);
                            editText[x][i].setText("" + qty);
                            editText[x][i].setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
                            editText[x][i].setTag(Tag);
                            editText[x][i].setPadding(16, 10, 16, 10);
                            editText[x][i].setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
                            if (x == 0 && i == 0) {
                                editText[x][i].requestFocus();
                            }
                            editText[x][i].setBackground(context.getResources().getDrawable(R.drawable.edittext_border));
                            tableRow.addView(editText[x][i]);
                            editText[x][i].addTextChangedListener(new TextWatcher() {
                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    // TODO Auto-generated method stub
                                    //System.out.println("editTxt Value:"+s.toString());
                                }
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                    // TODO Auto-generated method stub

                                }
                                @Override
                                public void afterTextChanged(Editable s) {
                                    // TODO Auto-generated method stub
                                    String[] str = editText[Xs][Ys].getTag().toString().split("/");
                                    String OrderID = str[0];
                                    String SubItemID = str[1];
                                    String ExDelDatetime = (ExDelDate.isEmpty() ? "" : DateFormatsMethods.DateFormat_YYYY_MM_DD(ExDelDate)+" ")+(ExDelTime.isEmpty()? "" : ExDelTime+":00");//str[2];
                                    String Stock = str[3];
                                    String ReserveStock = str[4];
                                    String Remarks = editTxtRemarks.getText().toString();
                                    //System.out.println("OrderID:" + OrderID + "\nSubItemID:" + SubItemID + "\nExDelDate:" + ExDelDate + "\nQty:" + s);
                                    String edt=editText[Xs][Ys].getText().toString().trim();
                                    edt=(edt.equals(""))?"0":edt;
//                                    double cal = (Double.valueOf(Stock) + Double.valueOf(ReserveStock)) - Double.valueOf(edt);
//                                    if ((cal < 0) && (Double.valueOf(edt) > 0)){
//                                        int AdvCond = DBHandler.AdavanceBookingConditionSubItem(OrderID, ItemID, SubItemID, edt, Xs);
//                                        if (AdvCond != 1) {
//                                            AlertDialogForStockCheck(OrderID, ItemID, SubItemID, edt, ExDelDatetime, Xs, Ys,Remarks);
//                                        }
//                                    }else {
                                        DBHandler.updateSubItemQty(OrderID, ItemID,SubItemID,edt,ExDelDatetime,Xs,Remarks);
//                                    }
                                }
                            });
                        } else {
                            MessageDialog.MessageDialog(context, "", "Something went wrong");
                        }
                    }
                }catch (Exception e){
                    MessageDialog.MessageDialog(context,"Exception","EditText : "+e.toString());
                }
                List<Map<String,String>> mapListQuantity = DBHandler.getSubItemQuantityList(BookOrderAdapter.listMultiCustomer.get(0).getOrderID(), 0, ItemID,SubItemList.get(i).get("SubItemID"));
                if (!mapListQuantity.isEmpty()) {
                    int pos = 0;
                    String DisRate = "₹" + mapListQuantity.get(pos).get("DiscountRate0")+"("+mapListQuantity.get(pos).get("DisPercentage0")+"%)";
                    //TODO:Rate
                    txtHeader = new TextView(context);
                    txtHeader.setId(i + 3);
                    txtHeader.setTag(mapListQuantity.get(pos).get("OrderID0"));
                    txtHeader.setText("₹" + mapListQuantity.get(pos).get("Rate0"));
                    if (mapListQuantity.get(pos).get("Rate0").equals(mapListQuantity.get(pos).get("DiscountRate0"))) {
                        DisRate = "Nil.";
                    }else {
                        txtHeader.setPaintFlags(txtHeader.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG); //TODO: Cross
                    }
                    txtHeader.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
                    txtHeader.setPadding(16, 10, 16, 10);
                    txtHeader.setTypeface(null, Typeface.BOLD);
                    txtHeader.setTextColor(context.getResources().getColor(R.color.Black));
                    tableRow.addView(txtHeader);
                    //TODO:Discount Rate and Discount Percentage
                    txtHeader = new TextView(context);
                    txtHeader.setId(i + 3);
                    txtHeader.setTag(mapListQuantity.get(pos).get("OrderID0"));
                    txtHeader.setText(""+DisRate);
                    txtHeader.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
                    txtHeader.setPadding(16, 10, 16, 10);
                    txtHeader.setTypeface(null, Typeface.BOLD);
                    txtHeader.setTextColor(context.getResources().getColor(R.color.Black));
                    tableRow.addView(txtHeader);
                    //TODO:MRP
                    txtHeader = new TextView(context);
                    txtHeader.setId(i + 30);
                    txtHeader.setTag(mapListQuantity.get(pos).get("OrderID0"));
                    txtHeader.setText(""+mapListQuantity.get(pos).get("Mrp0"));
                    txtHeader.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
                    txtHeader.setPadding(16, 10, 16, 10);
                    txtHeader.setTypeface(null, Typeface.BOLD);
                    txtHeader.setTextColor(context.getResources().getColor(R.color.Black));
                    tableRow.addView(txtHeader);
                }
                //TODO:Stock
                txtHeader = new  TextView(context);
                txtHeader.setId(i+4);
                txtHeader.setTag(SubItemList.get(i).get("SubItemID"));
                txtHeader.setText(""+SubItemList.get(i).get("Stock"));
                txtHeader.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
                txtHeader.setPadding(16,10,16,10);
                txtHeader.setTypeface(null, Typeface.BOLD);
                txtHeader.setTextColor(context.getResources().getColor(R.color.Green));
                tableRow.addView(txtHeader);
                //TODO:Reserve Stock
                txtHeader = new  TextView(context);
                txtHeader.setId(i+4);
                txtHeader.setTag(SubItemList.get(i).get("SubItemID"));
                txtHeader.setText(""+SubItemList.get(i).get("ReserveStock"));
                txtHeader.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
                txtHeader.setPadding(16,10,16,10);
                txtHeader.setTypeface(null, Typeface.BOLD);
                txtHeader.setTextColor(context.getResources().getColor(R.color.Maroon));
                tableRow.addView(txtHeader);
                //TODO:In Production
                txtHeader = new  TextView(context);
                txtHeader.setId(i+4);
                txtHeader.setTag(SubItemList.get(i).get("SubItemID"));
                txtHeader.setText(""+SubItemList.get(i).get("InProduction"));
                txtHeader.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
                txtHeader.setPadding(16,10,16,10);
                txtHeader.setTypeface(null, Typeface.BOLD);
                txtHeader.setTextColor(context.getResources().getColor(R.color.Blue));
                tableRow.addView(txtHeader);

                tableLayoutBooking.addView(tableRow);

                ImageStatus = Integer.valueOf(SubItemList.get(i).get("ImageStatus"));
                if (ImageStatus == 1) {
                    imageUrl.add(SubItemList.get(i).get("ImageUrl"));
                }else{
                    imageUrl.add(SubItemList.get(i).get("ImageUrl"));
                }
            }
            //TODO: Call SetImageSlider
            SetImageSlider(imageUrl);
//            for(int i=0; i<imageUrl.size();i++){
//                //TODO: Image Slider
//                ImageView imageView = new ImageView(context);
//                imageView.setPadding(10, 10, 10, 10);
//                imageView.setId(i);
//                Picasso.with(context).load(imageUrl.get(i)).placeholder(R.mipmap.ic_launcher).memoryPolicy(MemoryPolicy.NO_CACHE).into(imageView);
//                LinearImageslider.addView(imageView);
//            }
        }else{
            MessageDialog.MessageDialog(context,"Size Details display","Somthing went wrong");
        }
    }
    private void AllExpectedDeliveryDateTimes(){
        tableLayoutExDeldate.removeAllViews();
        tableLayoutExDeldate.removeAllViewsInLayout();
        //TODO: Table row
        View v = LayoutInflater.from(context).inflate(R.layout.table_row_header_2_edittext, tableLayoutExDeldate, false);
        TextView txtHeader = (TextView) v.findViewById(R.id.header);
        txtHeader.setText((StaticValues.MultiOrderSize > 1 ? "All Expected Datetime:" : "Expected Datetime"));
        Map<String,String> mapExpectedDatetime = DBHandler.getExpectedDeliveryDatetime(BookOrderAdapter.listMultiCustomer.get(0).getOrderID(), 0, ItemID);
        //if (!mapExpectedDatetime.isEmpty()) {
            int pos = 0;
            //TODO:Expected Delivery Date & Time
            String ExpectedDate = (mapExpectedDatetime.get("ExpectedDate"+0)==null || mapExpectedDatetime.get("ExpectedDate"+0).isEmpty()) ? "" : DateFormatsMethods.DateFormat_DD_MM_YYYY(mapExpectedDatetime.get("ExpectedDate"+0)).substring(0,10);
            String ExpectedTime = (mapExpectedDatetime.get("ExpectedDate"+0)==null || mapExpectedDatetime.get("ExpectedDate"+0).isEmpty()) ? "" : mapExpectedDatetime.get("ExpectedDate"+0).substring(11,16);
            //System.out.println("ExDate:"+ExpectedDate+" "+ExpectedTime+":00");
            ExDelDate = ExpectedDate;
            ExDelTime = ExpectedTime;
            EditText editTextExDate = (EditText) v.findViewById(R.id.ex_del_date);
            editTextExDate.setInputType(InputType.TYPE_NULL);
            editTextExDate.setText(""+ExpectedDate);
            EditText editTextExTime = (EditText) v.findViewById(R.id.ex_del_time);
            editTextExTime.setInputType(InputType.TYPE_NULL);
            editTextExTime.setText(""+ExpectedTime);
            editTextExDate.setTag(BookOrderAdapter.listMultiCustomer.get(0).getOrderID()+"/"+0);
            editTextExTime.setTag(BookOrderAdapter.listMultiCustomer.get(0).getOrderID()+"/"+0);
            editTextExDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final EditText editText = (EditText)v;
                    String[] str = v.getTag().toString().split("/");
                    final String OrderID = str[0];
                    final int PostFix = Integer.valueOf(str[1]);
                    Calendar cal = Calendar.getInstance(TimeZone.getDefault()); // Get current date
                    // Create the DatePickerDialog instance
                    DatePickerDialog datePicker = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            DecimalFormat formatter = new DecimalFormat("00");
                            ExDelDate = PastDateNotSelect(formatter.format(dayOfMonth) + "-" + formatter.format(month + 1) + "-"+ year);
                            editText.setText(ExDelDate);
                            DBHandler.UpdateExpectedDelDateTime(OrderID, ItemID, (ExDelDate.isEmpty() ? "" : DateFormatsMethods.DateFormat_YYYY_MM_DD(ExDelDate)+" ")+(ExDelTime.isEmpty()? "" : ExDelTime+":00"),PostFix);
                            if (StaticValues.MultiOrderSize > 1 ) {
                                PartyWiseExpectedDeliveryDateTimes(ExDelDate, ExDelTime, 1);
                            }
                        }
                    }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                    datePicker.setTitle("Select the date");
                    datePicker.show();
                }
            });
            editTextExTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final EditText editText = (EditText)v;
                    String[] str = v.getTag().toString().split("/");
                    final String OrderID = str[0];
                    final int PostFix = Integer.valueOf(str[1]);
                    Calendar cal = Calendar.getInstance(TimeZone.getDefault()); // Get current date
                    TimePickerDialog timePicker = new TimePickerDialog(context,new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            // TODO Auto-generated method stub
                            try{
                                DecimalFormat formatter = new DecimalFormat("00");
                                ExDelTime = formatter.format(hourOfDay)+":"+formatter.format(minute);
                                editText.setText(""+ExDelTime);
                                DBHandler.UpdateExpectedDelDateTime(OrderID, ItemID, (ExDelDate.isEmpty() ? "" : DateFormatsMethods.DateFormat_YYYY_MM_DD(ExDelDate)+" ")+(ExDelTime.isEmpty()? "" : ExDelTime+":00"),PostFix);
                                if (StaticValues.MultiOrderSize > 1 ) {
                                    PartyWiseExpectedDeliveryDateTimes(ExDelDate, ExDelTime, 1);
                                }
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
            tableLayoutExDeldate.addView(v,pos);
            if (StaticValues.MultiOrderSize > 1 ){
                PartyWiseExpectedDeliveryDateTimes("","",0);
            }
        //}
    }
    private void PartyWiseExpectedDeliveryDateTimes(String ExDate,String ExTime,int flag){
        for (int i=0; i< StaticValues.MultiOrderSize; i++){
            if (flag == 1){tableLayoutExDeldate.removeViewAt(i+1);}
            String PartyName = (BookOrderAdapter.listMultiCustomer.get(i).getPartyName()==null || BookOrderAdapter.listMultiCustomer.get(i).getPartyName().equals("null")) ? "" : BookOrderAdapter.listMultiCustomer.get(i).getPartyName();
            String SubPartyName = (BookOrderAdapter.listMultiCustomer.get(i).getSubParty()==null || BookOrderAdapter.listMultiCustomer.get(i).getSubParty().equals("null")) ? "" : BookOrderAdapter.listMultiCustomer.get(i).getSubParty();
            String RefName = (BookOrderAdapter.listMultiCustomer.get(i).getRefName()==null || BookOrderAdapter.listMultiCustomer.get(i).getRefName().equals("null")) ? "" : BookOrderAdapter.listMultiCustomer.get(i).getRefName();
            //TODO: Table row
            View v = LayoutInflater.from(context).inflate(R.layout.table_row_header_2_edittext, tableLayoutExDeldate, false);
            TextView txtHeader = (TextView) v.findViewById(R.id.header);
            if (RefName.isEmpty()) {
                if (SubPartyName.isEmpty()) {
                    txtHeader.setText("" + PartyName);
                }else{
                    txtHeader.setText(SubPartyName +"\n" + PartyName);
                }
            }else {
                if (SubPartyName.isEmpty()) {
                    txtHeader.setText(RefName +"\n" + PartyName);
                }else{
                    txtHeader.setText(RefName+"\n"+SubPartyName +"\n" + PartyName);
                }
            }
            Map<String,String> mapExpectedDatetime = DBHandler.getExpectedDeliveryDatetime(BookOrderAdapter.listMultiCustomer.get(i).getOrderID(),i, ItemID);
            //if (!mapExpectedDatetime.isEmpty()) {
                int pos = 0;
                //TODO:Expected Delivery Date & Time
                String ExpectedDate = (mapExpectedDatetime.get("ExpectedDate"+i)==null || mapExpectedDatetime.get("ExpectedDate"+i).isEmpty()) ? "" : DateFormatsMethods.DateFormat_DD_MM_YYYY(mapExpectedDatetime.get("ExpectedDate"+i)).substring(0,10);
                String ExpectedTime = (mapExpectedDatetime.get("ExpectedDate"+i)==null || mapExpectedDatetime.get("ExpectedDate"+i).isEmpty()) ? "" : mapExpectedDatetime.get("ExpectedDate"+i).substring(11,16);
                ExDelDate = ExpectedDate;
                ExDelTime = ExpectedTime;
                EditText editTextExDate = (EditText) v.findViewById(R.id.ex_del_date);
                editTextExDate.setInputType(InputType.TYPE_NULL);
                editTextExDate.setText(""+(ExDate.isEmpty() ? ExpectedDate : ExDate));
                EditText editTextExTime = (EditText) v.findViewById(R.id.ex_del_time);
                editTextExTime.setInputType(InputType.TYPE_NULL);
                editTextExTime.setText(""+(ExTime.isEmpty() ? ExpectedTime : ExTime));
                editTextExDate.setTag(BookOrderAdapter.listMultiCustomer.get(i).getOrderID()+"/"+i);
                editTextExTime.setTag(BookOrderAdapter.listMultiCustomer.get(i).getOrderID()+"/"+i);
                editTextExDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final EditText editText = (EditText)v;
                        String[] str = v.getTag().toString().split("/");
                        final String OrderID = str[0];
                        final int PostFix = Integer.valueOf(str[1]);
                        Calendar cal = Calendar.getInstance(TimeZone.getDefault()); // Get current date
                        // Create the DatePickerDialog instance
                        DatePickerDialog datePicker = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                DecimalFormat formatter = new DecimalFormat("00");
                                String ExDate = PastDateNotSelect(formatter.format(dayOfMonth) + "-" + formatter.format(month + 1) + "-"+ year);
                                editText.setText(ExDate);
                                DBHandler.UpdateExpectedDelDateTime(OrderID, ItemID, (ExDelDate.isEmpty() ? "" : DateFormatsMethods.DateFormat_YYYY_MM_DD(ExDelDate)+" ")+(ExDelTime.isEmpty()? "" : ExDelTime+":00"),PostFix);
                            }
                        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                        datePicker.setTitle("Select the date");
                        datePicker.show();
                    }
                });
                editTextExTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final EditText editText = (EditText)v;
                        String[] str = v.getTag().toString().split("/");
                        final String OrderID = str[0];
                        final int PostFix = Integer.valueOf(str[1]);
                        Calendar cal = Calendar.getInstance(TimeZone.getDefault()); // Get current date
                        TimePickerDialog timePicker = new TimePickerDialog(context,new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                // TODO Auto-generated method stub
                                try{
                                    DecimalFormat formatter = new DecimalFormat("00");
                                    String ExTime = formatter.format(hourOfDay)+":"+formatter.format(minute);
                                    editText.setText(""+ExTime);
                                    DBHandler.UpdateExpectedDelDateTime(OrderID, ItemID, (ExDelDate.isEmpty() ? "" : DateFormatsMethods.DateFormat_YYYY_MM_DD(ExDelDate)+" ")+(ExDelTime.isEmpty()? "" : ExDelTime+":00"),PostFix);
                                    //Toast.makeText(context, ExpectedDeliveryDate+" "+ExpectedDeliveryTime, Toast.LENGTH_LONG).show();
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
            //}
            tableLayoutExDeldate.addView(v,i+1);
        }
    }
    private String PastDateNotSelect(String InputDate){
        String result = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String CurrentDate=sdf.format(new Date());
        Date date;
        Date Curdate;
        try {
            date = sdf.parse(InputDate);
            Curdate = sdf.parse(CurrentDate);
            Curdate.before(date);
            if(Curdate.before(date)==true){
                result=InputDate;
                System.out.println(" "+result);
            }
            else{
                result=CurrentDate;
                System.out.println(" "+result);
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }
    private void SetImageSlider(List<String> mImages){
        mViewPager.setAdapter(new CustomViewPagerAdapter(context,mImages));
        viewPagerWithIndicator.setViewPager(mViewPager);
    }
    private class CustomViewPagerAdapter extends PagerAdapter {

        @NonNull
        final List<String> IMAGES;
        @NonNull final Context mContext;

        public CustomViewPagerAdapter(@NonNull final Context context, @NonNull final List<String> IMAGES) {
            this.mContext = context;
            this.IMAGES = IMAGES;
        }

        @Override
        public int getCount() {
            return IMAGES.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            ImageView imageView = new ImageView(mContext);
            imageView.setTag(IMAGES.get(position));
            Picasso.with(context).load(IMAGES.get(position)).placeholder(R.drawable.placeholder_new).into(imageView);
            container.addView(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, SlidingImageActivity.class);
                    intent.putExtra("Path", v.getTag().toString());
                    intent.putExtra("SelectedPath", v.getTag().toString());
                    context.startActivity(intent);
                }
            });
            return imageView;
        }
    }
    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }
    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }
    private void CallRetrofitImageList(final String Path, final String SelectedPath){
        showProgressBar();
        ApiInterface apiService =  ApiClient.getClient(CommanStatic.BaseUrl).create(ApiInterface.class);
        Map<String, String>  params = new HashMap<String, String>();
        params.put("PrimaryImagePath", Path);
        Call<ResponseImageListDataset> call = apiService.getImageList(params);
        call.enqueue(new Callback<ResponseImageListDataset>() {
            @Override
            public void onResponse(Call<ResponseImageListDataset> call, retrofit2.Response<ResponseImageListDataset> response) {
                try {
                    if (response.isSuccessful()) {
                        int status = response.body().getStatus();
                        String msg = response.body().getMsg();
                        if (status == 1) {
                            String[] strList = response.body().getResult();
                            ArrayList<String> imageList = new ArrayList<String>();
                            for (int i = 0; i < strList.length; i++) {
//                                    if(SelectedPath.equals(strList[i])){
//                                        //selectedPage=i;
//                                    }
                                //String t = strList[i].replaceAll("1025x1400", "1025x1400");//"1025x1400"orignal
                                imageList.add(strList[i]);
                            }
                            SetImageSlider(imageList);
                        } else {
                            Toast.makeText(context, "" + msg, Toast.LENGTH_LONG).show();
                        }
                    }else{
                        ArrayList<String> imageList = new ArrayList<String>();
                        imageList.add(Path);
                        SetImageSlider(imageList);
                        Toast.makeText(context, "Server not responding", Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){
                    Log.e(TAG,"Image List Exception:"+e.getMessage());
                }
                hideProgressBar();
            }

            @Override
            public void onFailure(Call<ResponseImageListDataset> call, Throwable t) {
                Log.e(TAG,"Image List Failure:"+t.toString());
                Toast.makeText(context,"Image List Failure",Toast.LENGTH_LONG).show();
                hideProgressBar();
            }
        });
    }
    private void StockCheck(){
        if (!ItemCode.isEmpty()){
            DatabaseSqlLiteHandlerUserInfo DBInfo = new DatabaseSqlLiteHandlerUserInfo(context);
            Map<String,String> map = DBInfo.getModulePermissionByVtype(11);
            if (map != null && !map.isEmpty()) {
                Bundle bundle = new Bundle();
                bundle.putString("ItemCode",ItemCode);
                bundle.putString("Title",map.get("Name"));
                bundle.putInt("ViewFlag",Integer.valueOf(map.get("ViewFlag")));
                bundle.putInt("EditFlag",Integer.valueOf(map.get("EditFlag")));
                bundle.putInt("CreateFlag",Integer.valueOf(map.get("CreateFlag")));
                bundle.putInt("RemoveFlag",Integer.valueOf(map.get("RemoveFlag")));
                bundle.putInt("PrintFlag",Integer.valueOf(map.get("PrintFlag")));
                bundle.putInt("ImportFlag",Integer.valueOf(map.get("ImportFlag")));
                bundle.putInt("ExportFlag",Integer.valueOf(map.get("ExportFlag")));
                bundle.putInt("Vtype",Integer.valueOf(map.get("Vtype")));
                System.out.println("bundle print:"+bundle.toString());
                Intent in = new Intent(context, StockCheckActivity.class);
                in.putExtra("PermissionBundle", bundle);
                startActivity(in);
            }
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_HOME){
            // Stop your service here
            System.out.println("This app is close");
            finishAffinity();
        }else if(keyCode==KeyEvent.KEYCODE_BACK){

            if (StaticValues.OrderViewIntentFlag == 0) {
                //TODO: Activity Intent to Parent Caption
                Intent intent = new Intent(getApplicationContext(), BarcodeSearchViewPagerActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
            }else if (StaticValues.OrderViewIntentFlag == 1) {
                //TODO: Activity Intent to Order view Group by Item
                finish();
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_custom, menu);
        MenuItem searchItem = menu.findItem(R.id.Action_Search);
        searchItem.setVisible(false);
        MenuItem saveNext = menu.findItem(R.id.Save_Next);
        saveNext.setVisible(true);
        MenuItem StockCheck = menu.findItem(R.id.Push_Fca); //Stock Check
        StockCheck.setIcon(getResources().getDrawable(R.drawable.ic_action_stock_check_white));
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case android.R.id.home:
                //TODO: Activity Back
                if (StaticValues.OrderViewIntentFlag == 0) {
                    //TODO: Activity Intent to Parent Caption
                    Intent intent = new Intent(getApplicationContext(), BarcodeSearchViewPagerActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                }else if (StaticValues.OrderViewIntentFlag == 1) {
                    //TODO: Activity Intent to Order view Group by Item
                    finish();
                    overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                }
                break;
            case R.id.Save_Next:
                //TODO: Update Order to server
                if (DBHandler.getSumAllQuantity() == 0) {
                    if (StaticValues.createFlag == 1) {
                        if (!DBHandler.getAllOrderSubItemDetails().isEmpty()) {
                            String status = NetworkUtils.getConnectivityStatusString(context);
                            if (!status.contentEquals("No Internet Connection")) {
                                LoginActivity obj = new LoginActivity();
                                String[] str = obj.GetSharePreferenceSession(context);
                                if (str != null) {
                                    OutOfStock = new ArrayList<>();
                                    for (int i = 0; i < DBHandler.getAllOrderSubItemDetails().size(); i++) {
                                        CallVolleyUpdateBookedOrderToServer(str[3], str[4], str[0], str[5], str[14], str[15], DBHandler.getAllOrderSubItemDetails().get(i).get("OrderID"), DBHandler.getAllOrderSubItemDetails().get(i).get("ItemID"), DBHandler.getAllOrderSubItemDetails().get(i).get("ItemCode"), DBHandler.getAllOrderSubItemDetails().get(i).get("SubItemID"), DBHandler.getAllOrderSubItemDetails().get(i).get("SubItemName"), DBHandler.getAllOrderSubItemDetails().get(i).get("SubItemCode"), DBHandler.getAllOrderSubItemDetails().get(i).get("ExpectedDate"), "1", DBHandler.getAllOrderSubItemDetails().get(i).get("BookQty"), DBHandler.getAllOrderSubItemDetails().get(i).get("Rate"), DBHandler.getAllOrderSubItemDetails().get(i).get("Mrp"), DBHandler.getAllOrderSubItemDetails().get(i).get("DiscountRate"), DBHandler.getAllOrderSubItemDetails().get(i).get("DisPercentage"), DBHandler.getAllOrderSubItemDetails().get(i).get("Remarks"), DBHandler.getAllOrderSubItemDetails().size(), i);
                                        //final String OrderID,final String ItemID,final String ColorID,final String SizeID,final String ExpectedDate,final String BookFrom,final String BookQty,final String Remarks
                                    }
                                }
                            } else {
                                MessageDialog.MessageDialog(context, "", "" + status);
                            }
                        } else {
                            MessageDialog.MessageDialog(context, "", "Please enter any quatity then Save it.");
                        }
                    } else {
                        MessageDialog.MessageDialog(context, "Alert", "You don't have Create permission of this module");
                    }
                }else {
                    if (StaticValues.editFlag == 1) {
                        if (!DBHandler.getAllOrderSubItemDetails().isEmpty()) {
                            String status = NetworkUtils.getConnectivityStatusString(context);
                            if (!status.contentEquals("No Internet Connection")) {
                                LoginActivity obj = new LoginActivity();
                                String[] str = obj.GetSharePreferenceSession(context);
                                if (str != null) {
                                    OutOfStock = new ArrayList<>();
                                    for (int i = 0; i < DBHandler.getAllOrderSubItemDetails().size(); i++) {
                                        CallVolleyUpdateBookedOrderToServer(str[3], str[4], str[0], str[5], str[14], str[15], DBHandler.getAllOrderSubItemDetails().get(i).get("OrderID"), DBHandler.getAllOrderSubItemDetails().get(i).get("ItemID"), DBHandler.getAllOrderSubItemDetails().get(i).get("ItemCode"), DBHandler.getAllOrderSubItemDetails().get(i).get("SubItemID"), DBHandler.getAllOrderSubItemDetails().get(i).get("SubItemName"), DBHandler.getAllOrderSubItemDetails().get(i).get("SubItemCode"), DBHandler.getAllOrderSubItemDetails().get(i).get("ExpectedDate"), "1", DBHandler.getAllOrderSubItemDetails().get(i).get("BookQty"), DBHandler.getAllOrderSubItemDetails().get(i).get("Rate"), DBHandler.getAllOrderSubItemDetails().get(i).get("Mrp"), DBHandler.getAllOrderSubItemDetails().get(i).get("DiscountRate"), DBHandler.getAllOrderSubItemDetails().get(i).get("DisPercentage"), DBHandler.getAllOrderSubItemDetails().get(i).get("Remarks"), DBHandler.getAllOrderSubItemDetails().size(), i);
                                        //final String OrderID,final String ItemID,final String ColorID,final String SizeID,final String ExpectedDate,final String BookFrom,final String BookQty,final String Remarks
                                    }
                                }
                            } else {
                                MessageDialog.MessageDialog(context, "", "" + status);
                            }
                        } else {
                            MessageDialog.MessageDialog(context, "", "Please enter any quatity then Save it.");
                        }
                    } else {
                        MessageDialog.MessageDialog(context, "Alert", "You don't have Edit permission of this module");
                    }
                }
                //System.out.println("OrderSize:"+DBHandler.getAllOrderSubItemDetails().size()+" \nOrder:"+DBHandler.getAllOrderSubItemDetails().toString());
                break;
            case R.id.Report_View:
                //TODO: Report view
                Intent intent = new Intent(context, OrderViewOrPushActivity.class);
                startActivity(intent);
                break;
            case R.id.Push_Fca://TODO: Stock check
                StockCheck();
        }
        return super.onOptionsItemSelected(item);
    }
    //TODO: Call Volley Order Update to server
    private void CallVolleyUpdateBookedOrderToServer(final String DeviceID, final String UserID,final String SessionID,final String DivisionID,final String CompanyID,final String BranchID,final String OrderID,final String ItemID,final String ItemCode,final String SubItemID,final String SubItemName,final String SubItemCode,final String ExpectedDate,final String BookFrom,final String BookQty,final String Rate,final String Mrp, final String DiscountRate, final String DisPercentage,final String Remarks,final int size,final int i){
        showpDialog();
        String PartialUrl = "";
        if (StaticValues.AdvanceOrBookOrder == 0){
            PartialUrl = "UpdateTempOrderItems";
        }else if (StaticValues.AdvanceOrBookOrder == 1){
            PartialUrl = "UpdateTempOrderItemsAdvance";
        }
        //String PartialUrl = (StaticValues.AdvanceOrBookOrder == 1) ?  "UpdateTempOrderItemsAdvance" : "UpdateTempOrderItems";
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL + PartialUrl, new Response.Listener<String>()
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
                        if (OutOfStock.isEmpty() && size==(i+1)) {
                            MessageDialogByIntent(context, "", Msg);
                        }else if (!OutOfStock.isEmpty() && size==(i+1)){
                            MessageDialog.MessageDialog(context,"",""+Msg);
                        }
                    } else {
                        Object objResult = jsonObject.get("Result");
                        if (objResult instanceof JSONArray) {
                            // It's an array
                            //JSONArray jsonArray = (JSONArray)objResult;
                            MessageDialog.MessageDialog(context,"",""+Msg);
                        }else if (objResult instanceof JSONObject) {
                            // It's an object
                            //interventionObject = (JSONObject)objResult;
                            JSONObject jsonObjectStock = (JSONObject) objResult;
                            String Stock = (jsonObjectStock.optString("Stock") == null ? "-00000" : jsonObjectStock.optString("Stock"));
                            if (!Stock.equals("-00000")) {
                                Map<String, String> map = new HashMap<>();
                                map.put("OrderID", OrderID);
                                map.put("ItemID", ItemID);
                                map.put("ItemCode", ItemCode);
                                map.put("SubItemID", SubItemID);
                                map.put("SubItemName", SubItemName);
                                map.put("SubItemCode", SubItemCode);
                                map.put("ExpectedDate", ExpectedDate);
                                map.put("Stock", Stock);
                                map.put("BookQty", BookQty);
                                map.put("Rate", Rate);
                                map.put("Mrp", Mrp);
                                map.put("DiscountRate", DiscountRate);
                                map.put("DisPercentage", DisPercentage);
                                map.put("Remarks", Remarks);
                                OutOfStock.add(map);
                                if (!OutOfStock.isEmpty() && size == (i + 1)) {
                                    DialogInsertToServer(context, OutOfStock);
                                }
                            } else {
                                MessageDialog.MessageDialog(context, "", "" + Msg);
                            }
                        }
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
                params.put("ItemID", ItemID);
                params.put("SubItemID", SubItemID);
                params.put("ExpectedDate", ExpectedDate);
                params.put("BookFrom", BookFrom);
                params.put("BookQty", BookQty);
                params.put("Remarks", Remarks);
                Log.d(TAG,"Update order to server parameters:"+params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
    }
    private void showpDialog() {
        if(progressDialog!=null) {
            progressDialog.show();
        }
    }
    private void hidepDialog() {
        if(progressDialog!=null) {
            progressDialog.dismiss();
        }
    }
    public void MessageDialogByIntent(final Context context, String Title, String Mesaage){
        try {
            final Dialog dialog=new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.cardview_message_box);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            TextView txtViewMessageTitle = (TextView) dialog.findViewById(R.id.textView_messageTitle);
            TextView txtViewMessage = (TextView) dialog.findViewById(R.id.textView_message);
            Button btnOK = (Button) dialog.findViewById(R.id.Button_OK);
            txtViewMessageTitle.setText(Title);
            txtViewMessage.setText(Mesaage);
            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    if (StaticValues.OrderViewIntentFlag == 0) {
                        //TODO: Activity Intent to Parent Caption
                        Intent intent = new Intent(getApplicationContext(), BarcodeSearchViewPagerActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                    }else if (StaticValues.OrderViewIntentFlag == 1) {
                        //TODO: Activity Intent to Order view Group by Item
                        finish();
                        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                    }
                }
            });
            dialog.show();
        }catch (Exception e){
            Log.e("TAG","MessageDialogException2"+e.toString());
        }
    }
    private void DialogInsertToServer(Context contexts,final List<Map<String,String>> OutOfStockList) {
        DBHandler.deleteOutOfStockTablesData();
        DBHandler.insertOutOfStockSubItemTable(OutOfStockList);
        List<Map<String,String>> OutOfStockDetails = DBHandler.getAllDetailsOOSSubItem();
        final Dialog dialog = new Dialog(contexts);
        dialog.setContentView(R.layout.dialog_out_of_stock_table_layout);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.TOP;
        dialog.getWindow().setAttributes(lp);
//        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        TableLayout tableLayoutBooking = (TableLayout) dialog.findViewById(R.id.table_Layout_booking);
        Button btnSave= (Button) dialog.findViewById(R.id.button_SaveNext);
        Button btnCancel= (Button) dialog.findViewById(R.id.button_Cancel);
        //final EditText[][] editTxt;
        tableLayoutBooking.removeAllViews();
        tableLayoutBooking.removeAllViewsInLayout();
        CustomerNameShow(tableLayoutBooking);
        SizePriceStockListDisplay(tableLayoutBooking,OutOfStockDetails);
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //TODO: Update Order to server
                if (!DBHandler.getOutOfStockSubItemDetails().isEmpty()){
                    String status = NetworkUtils.getConnectivityStatusString(context);
                    if (!status.contentEquals("No Internet Connection")) {
                        LoginActivity obj=new LoginActivity();
                        String[] str = obj.GetSharePreferenceSession(context);
                        if (str!=null) {
                            OutOfStock = new ArrayList<Map<String, String>>();
                            String BookFrom = (StaticValues.ColorWise == 1 ? "2" : "1");
                            for(int i = 0; i < DBHandler.getOutOfStockSubItemDetails().size(); i++){
                                CallVolleyUpdateBookedOrderToServer(str[3], str[4], str[0], str[5], str[14], str[15], DBHandler.getOutOfStockSubItemDetails().get(i).get("OrderID"), DBHandler.getOutOfStockSubItemDetails().get(i).get("ItemID"), DBHandler.getOutOfStockSubItemDetails().get(i).get("ItemCode"), DBHandler.getOutOfStockSubItemDetails().get(i).get("SubItemID"), DBHandler.getOutOfStockSubItemDetails().get(i).get("SubItemName"),DBHandler.getOutOfStockSubItemDetails().get(i).get("SubItemCode"), DBHandler.getOutOfStockSubItemDetails().get(i).get("ExpectedDate"), BookFrom, DBHandler.getOutOfStockSubItemDetails().get(i).get("BookQty"), DBHandler.getOutOfStockSubItemDetails().get(i).get("Rate"), DBHandler.getOutOfStockSubItemDetails().get(i).get("Mrp"), DBHandler.getOutOfStockSubItemDetails().get(i).get("DiscountRate"), DBHandler.getOutOfStockSubItemDetails().get(i).get("DisPercentage"), DBHandler.getOutOfStockSubItemDetails().get(i).get("Remarks"),DBHandler.getOutOfStockSubItemDetails().size(),i);
                            }
                        }
                    } else {
                        MessageDialog.MessageDialog(context,"",""+status);
                    }
                }else{
                    MessageDialog.MessageDialog(context,"","Please enter any quatity then Save it.");
                }
                dialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent=new Intent(context, BarcodeSearchViewPagerActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
            }
        });
    }
    private void CustomerNameShow(TableLayout tableLayoutBooking){
        if (!BookOrderAdapter.listMultiCustomer.isEmpty()){
            int id=0;
            //TODO: Table row
            TableRow tableRow = new TableRow(context);
            tableRow.setId(id);
            tableRow.setPadding(5,10,16,10);
            //TODO: Size
            TextView txtHeader = new  TextView(context);
            txtHeader.setId(id+1);
            txtHeader.setText("Size");
            txtHeader.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
            txtHeader.setPadding(16,10,16,10);
            txtHeader.setTypeface(null, Typeface.BOLD);
            txtHeader.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
            tableRow.addView(txtHeader);

            for (int i=0; i<BookOrderAdapter.listMultiCustomer.size(); i++){
                String PartyName = (BookOrderAdapter.listMultiCustomer.get(i).getPartyName()==null || BookOrderAdapter.listMultiCustomer.get(i).getPartyName().equals("null")) ? "" : BookOrderAdapter.listMultiCustomer.get(i).getPartyName();
                String SubPartyName = (BookOrderAdapter.listMultiCustomer.get(i).getSubParty()==null || BookOrderAdapter.listMultiCustomer.get(i).getSubParty().equals("null")) ? "" : BookOrderAdapter.listMultiCustomer.get(i).getSubParty();
                String RefName = (BookOrderAdapter.listMultiCustomer.get(i).getRefName()==null || BookOrderAdapter.listMultiCustomer.get(i).getRefName().equals("null")) ? "" : BookOrderAdapter.listMultiCustomer.get(i).getRefName();
                //TODO: Party Name
                txtHeader = new  TextView(context);
                txtHeader.setId(100+i);
                txtHeader.setWidth(160);
                txtHeader.setSelected(true);
                txtHeader.setSingleLine(true);
                txtHeader.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
                txtHeader.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                txtHeader.setPadding(16,10,16,10);
                txtHeader.setTypeface(null, Typeface.BOLD);
                txtHeader.setTextColor(context.getResources().getColor(R.color.Brown));
                if (RefName.isEmpty()) {
                    if (SubPartyName.isEmpty()) {
                        txtHeader.setText("" + PartyName);
                    }else{
                        txtHeader.setText(SubPartyName +"\n" + PartyName);
                    }
                }else {
                    if (SubPartyName.isEmpty()) {
                        txtHeader.setText(RefName +"\n" + PartyName);
                    }else{
                        txtHeader.setText(RefName+"\n"+SubPartyName +"\n" + PartyName);
                    }
                }
                tableRow.addView(txtHeader);
            }
            //TODO: Rate
            txtHeader = new  TextView(context);
            txtHeader.setId(id+10);
            txtHeader.setText("Rate");
            txtHeader.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
            txtHeader.setPadding(16,10,16,10);
            txtHeader.setTypeface(null, Typeface.BOLD);
            txtHeader.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
            tableRow.addView(txtHeader);
            //TODO: Discount Rate
            txtHeader = new  TextView(context);
            txtHeader.setId(id+100);
            txtHeader.setText("Rate(%)");
            txtHeader.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_discount_red,0,0,0);
            txtHeader.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
            txtHeader.setPadding(16,10,16,10);
            txtHeader.setTypeface(null, Typeface.BOLD);
            txtHeader.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
            tableRow.addView(txtHeader);
            //TODO: MRP
            txtHeader = new  TextView(context);
            txtHeader.setId(id+100);
            txtHeader.setText("MRP");
            txtHeader.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
            txtHeader.setPadding(16,10,16,10);
            txtHeader.setTypeface(null, Typeface.BOLD);
            txtHeader.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
            tableRow.addView(txtHeader);
            //TODO: Stock
            txtHeader = new  TextView(context);
            txtHeader.setId(id+11);
            txtHeader.setText("Stock");
            txtHeader.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
            txtHeader.setPadding(16,10,16,10);
            txtHeader.setTypeface(null, Typeface.BOLD);
            txtHeader.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
            tableRow.addView(txtHeader);
            //TODO: Color & ItemCode
            txtHeader = new  TextView(context);
            txtHeader.setId(id+12);
            txtHeader.setText("SubItem Code");
            txtHeader.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
            txtHeader.setPadding(16,10,16,10);
            txtHeader.setTypeface(null, Typeface.BOLD);
            txtHeader.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
            tableRow.addView(txtHeader);
            tableLayoutBooking.addView(tableRow);
        }
    }
    private void SizePriceStockListDisplay(TableLayout tableLayoutBooking, final List<Map<String,String>> OutOfStockList){
        if (!OutOfStockList.isEmpty()){
            //System.out.println("OutOfStockList :"+OutOfStockList.toString());
            final EditText[][] editText = new EditText[StaticValues.MultiOrderSize + 1][OutOfStockList.size() + 1];
            for (int i = 0; i<OutOfStockList.size(); i++){
                //TODO: Table row
                TableRow tableRow = new TableRow(context);
                tableRow.setId(i+200);
                tableRow.setPadding(5,10,16,10);
                //TODO: Size
                TextView txtHeader = new  TextView(context);
                txtHeader.setId(i+1);
                txtHeader.setTextSize(18);
                txtHeader.setTag(OutOfStockList.get(i).get("SubItemID"));
                txtHeader.setText(""+OutOfStockList.get(i).get("SubItemName"));
                txtHeader.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
                txtHeader.setPadding(16,10,16,10);
                txtHeader.setTypeface(null, Typeface.BOLD);
                txtHeader.setTextColor(context.getResources().getColor(R.color.Black));
                tableRow.addView(txtHeader);
                try {
                    for (int x = 0; x < StaticValues.MultiOrderSize; x++) {
                        final int Xs = x;
                        final int Ys = i;
                        String OrderID = BookOrderAdapter.listMultiCustomer.get(x).getOrderID();
                        List<Map<String,String>> QtyList = DBHandler.getOrderDetailsOOSSubItem(OrderID,OutOfStockList.get(i).get("ItemID"),OutOfStockList.get(i).get("SubItemID"));
                        //TODO:Quantity
                        String QtyStr = (QtyList.isEmpty() ? "0" : QtyList.get(0).get("BookQty"));
                        String qty = (QtyStr.equals("0") || QtyStr == "0" ? "" : QtyStr);
                        editText[x][i] = new EditText(context);
                        editText[x][i].setId(i + x + 500);
                        editText[x][i].setWidth(160);
                        editText[x][i].setHint("Qty");
                        editText[x][i].setSelectAllOnFocus(true);
                        editText[x][i].setText("" + qty);
                        editText[x][i].setTag(OrderID+"/"+OutOfStockList.get(i).get("ItemID")+"/"+OutOfStockList.get(i).get("SubItemID"));
                        editText[x][i].setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
                        editText[x][i].setPadding(16, 10, 16, 10);
                        editText[x][i].setInputType(InputType.TYPE_CLASS_NUMBER);
                        if (x == 0 && i == 0) {
                            editText[x][i].requestFocus();
                        }
                        editText[x][i].setBackground(context.getResources().getDrawable(R.drawable.edittext_border));
                        tableRow.addView(editText[x][i]);
                        editText[x][i].addTextChangedListener(new TextWatcher() {
                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                // TODO Auto-generated method stub
                                //System.out.println("editTxt Value:"+s.toString());
                            }
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                // TODO Auto-generated method stub

                            }
                            @Override
                            public void afterTextChanged(Editable s) {
                                // TODO Auto-generated method stub
                                String[] str = editText[Xs][Ys].getTag().toString().split("/");
                                String OrderID = str[0];
                                String ItemID = str[1];
                                String SubItemID = str[2];
                                String edt=editText[Xs][Ys].getText().toString().trim();
                                edt=(edt.equals(""))?"0":edt;
                                //System.out.print("OrderID:"+OrderID+"\nColorID:"+ColorID+"\n"+SizeID+"\nedt:"+edt);
                                DBHandler.updateQtyOOSSubItem(OrderID,ItemID,SubItemID,edt);
                            }
                        });
                    }
                }catch (Exception e){
                    MessageDialog.MessageDialog(context,"Exception","EditText : "+e.toString());
                }
                String DisRate = "₹" + OutOfStockList.get(i).get("DiscountRate")+"("+OutOfStockList.get(i).get("DisPercentage")+"%)";
                //TODO:Rate
                txtHeader = new TextView(context);
                txtHeader.setId(i + 3);
                txtHeader.setTag(OutOfStockList.get(i).get("SubItemID"));
                txtHeader.setText("₹" + OutOfStockList.get(i).get("Rate"));
                if (OutOfStockList.get(i).get("Rate").equals(OutOfStockList.get(i).get("DiscountRate"))) {
                    DisRate = "Nil.";
                }else {
                    txtHeader.setPaintFlags(txtHeader.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG); //TODO: Cross
                }
                txtHeader.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
                txtHeader.setPadding(16, 10, 16, 10);
                txtHeader.setTypeface(null, Typeface.BOLD);
                txtHeader.setTextColor(context.getResources().getColor(R.color.Black));
                tableRow.addView(txtHeader);
                //TODO:Discount Rate and Discount Percentage
                txtHeader = new TextView(context);
                txtHeader.setId(i + 10);
                txtHeader.setTag(OutOfStockList.get(i).get("SubItemID"));
                txtHeader.setText(""+DisRate);
                txtHeader.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
                txtHeader.setPadding(16, 10, 16, 10);
                txtHeader.setTypeface(null, Typeface.BOLD);
                txtHeader.setTextColor(context.getResources().getColor(R.color.Black));
                tableRow.addView(txtHeader);
                //TODO:MRP
                txtHeader = new TextView(context);
                txtHeader.setId(i + 100);
                txtHeader.setTag(OutOfStockList.get(i).get("SubItemID"));
                txtHeader.setText(""+OutOfStockList.get(i).get("Mrp"));
                txtHeader.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
                txtHeader.setPadding(16, 10, 16, 10);
                txtHeader.setTypeface(null, Typeface.BOLD);
                txtHeader.setTextColor(context.getResources().getColor(R.color.Black));
                tableRow.addView(txtHeader);
                //TODO:Stock
                txtHeader = new  TextView(context);
                txtHeader.setId(i+4);
                txtHeader.setTag(OutOfStockList.get(i).get("SubItemID"));
                txtHeader.setText(""+OutOfStockList.get(i).get("Stock"));
                txtHeader.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
                txtHeader.setPadding(16,10,16,10);
                txtHeader.setTypeface(null, Typeface.BOLD);
                txtHeader.setTextColor(context.getResources().getColor(R.color.Green));
                tableRow.addView(txtHeader);
                //TODO:Color
                txtHeader = new  TextView(context);
                txtHeader.setId(i+4);
                txtHeader.setTag(OutOfStockList.get(i).get("SubItemID"));
                txtHeader.setText(OutOfStockList.get(i).get("SubItemCode"));
                txtHeader.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
                txtHeader.setPadding(16,10,16,10);
                txtHeader.setTypeface(null, Typeface.BOLD);
                txtHeader.setTextColor(context.getResources().getColor(R.color.Maroon));
                tableRow.addView(txtHeader);

                tableLayoutBooking.addView(tableRow);
            }
        }else{
            MessageDialog.MessageDialog(context,"Size Details display","Somthing went wrong");
        }
    }
    private void AlertDialogForStockCheck(final String OrderID, final String ItemID, final String  SubItemID, final String edt, final String ExDelDate, final int Xs, final int Ys, final String Remarks){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage("Are you sure,You want to take Advance Order");
        alertDialogBuilder.setPositiveButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                editText[Xs][Ys].setText("");
                dialog.dismiss();

            }
        });
        alertDialogBuilder.setNegativeButton("YES",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DBHandler.updateSubItemQty(OrderID, ItemID,SubItemID,edt,ExDelDate,Xs,Remarks);
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        alertDialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    editText[Xs][Ys].setText("");
                    dialog.dismiss();
                }
                return true;
            }
        });
    }
}
