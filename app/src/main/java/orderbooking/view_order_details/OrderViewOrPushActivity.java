package orderbooking.view_order_details;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonArray;
import com.singlagroup.AppController;
import com.singlagroup.BuildConfig;
import com.singlagroup.LoginActivity;
import com.singlagroup.R;
import com.singlagroup.customwidgets.ConditionLibrary;
import com.singlagroup.customwidgets.DateFormatsMethods;
import com.singlagroup.customwidgets.FileOpenByIntent;
import com.singlagroup.customwidgets.ItemClickSupport;
import com.singlagroup.customwidgets.MessageDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Logger;

import DatabaseController.CommanStatic;
import orderbooking.StaticValues;
import orderbooking.customerlist.temp.BookOrderAdapter;
import orderbooking.print.PdfDocumentAdapter;
import orderbooking.print.PrintReportActivity;
import orderbooking.view_order_details.adapter.OrderViewGroupAdapter;
import orderbooking.view_order_details.dataset.OrderViewGroupDataset;
import services.NetworkChangeReceiver;
import services.NetworkListener;
import services.NetworkUtils;

/**
 * Created by Rakesh on 05-Nov-16.
 */
public class OrderViewOrPushActivity extends AppCompatActivity{

    private ActionBar actionBar;
    private Context context;
    RecyclerView recyclerView;
    private TextView txtTotalGroupHeader;
    private ProgressDialog progressDialog,pDialog;;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String OrderID = "";
    private List<OrderViewGroupDataset> datasetList;

    private static String TAG = OrderViewOrPushActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);} getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_recyclerview_swipe_refresh_layout);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        Initialization();
        setActionBarHeader();
        CallApiMethod();
    }
    private void Initialization(){
        this.actionBar = getSupportActionBar();
        this.actionBar.setDisplayHomeAsUpEnabled(true);
        this.context = OrderViewOrPushActivity.this;
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_Refresh_Layout);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        txtTotalGroupHeader = (TextView) findViewById(R.id.text_showroom);

        this.progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Downloading file. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setMax(100);
        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pDialog.setCancelable(true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                CallApiMethod();
            }
        });
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
                        finish();
                        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                    }
                });
                TextView textView = (TextView) v.findViewById(R.id.text_view_header);
                textView.setSelected(true);
                textView.setWidth(StaticValues.sViewWidth-300);
                textView.setText("" + Header.substring(0,Header.length()-2));
                actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
                actionBar.setCustomView(v);
                OrderID = BookOrderAdapter.listMultiCustomer.get(0).getOrderID();
            }else{
                MessageDialog.MessageDialog(context,"Book order adapter","Something went wrong");
            }
        }catch (Exception e){
            MessageDialog.MessageDialog(context, "Exception", "Intent:"+e.toString());
        }
    }
    private void CallApiMethod(){
        OrderViewGroupAdapter.listGroup = new ArrayList<>();
        //TODO: Closed Order Request
        NetworkChangeReceiver.bindNetworkListener(new NetworkListener() {
            @Override
            public void networkReceived(String status) {
                if (!status.contentEquals("No Internet Connection")) {
                    LoginActivity obj=new LoginActivity();
                    String[] str = obj.GetSharePreferenceSession(context);
                    if (str!=null) {
                        CallVolleyViewBookedOrder(str[3], str[4], str[0], str[5], str[14], str[15],OrderID);
                    }
                } else {
                    MessageDialog.MessageDialog(context,"",status);
                }
            }
        });
        String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
        if (!status.contentEquals("No Internet Connection")) {
            LoginActivity obj=new LoginActivity();
            String[] str = obj.GetSharePreferenceSession(context);
            if (str!=null) {
                CallVolleyViewBookedOrder(str[3], str[4], str[0], str[5], str[14], str[15],OrderID);
            }
        } else {
            MessageDialog messageDialog=new MessageDialog();
            messageDialog.MessageDialog(context,"","",status);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case android.R.id.home:
                //TODO: Activity Intent to Parent Caption
                finish();
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                break;
            case R.id.Push_Fca:
                DialogPushOrderRemarks(context);
                //MessageDialog.MessageDialog(context,""+GroupAdapter.listGroup.size(),""+GroupAdapter.listGroup.toString());
                break;
            case R.id.Expected_Datetime:
                if (!OrderID.isEmpty())
                    DialogExpectedDeliveryDatetimeUpdate(OrderID);
                break;
            case R.id.Print:
                if (!this.datasetList.isEmpty() && this.datasetList!=null) {
                    if (StaticValues.printFlag == 1) {
//                        Intent intent = new Intent(context, PrintReportActivity.class);
//                        intent.putExtra("OrderID", OrderID);
//                        intent.putExtra("GroupID", "");
//                        intent.putExtra("ItemID", "");
//                        context.startActivity(intent);
                        String status = NetworkUtils.getConnectivityStatusString(context);
                        if (!status.contentEquals("No Internet Connection")) {
                            LoginActivity obj=new LoginActivity();
                            String[] str = obj.GetSharePreferenceSession(context);
                            if (str!=null) {
                                CallVolleyViewBookedOrderByGroup(str[3], str[4], str[0], str[5], str[14], str[15],OrderID,"","");
                            }
                        } else {
                            MessageDialog.MessageDialog(context,"",status);
                        }

                    } else {
                        MessageDialog.MessageDialog(context, "Alert", "You don't have print permission of this module");
                    }
                } else {
                    MessageDialog.MessageDialog(context, "Alert", "Your cart is blank");
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            // Stop your service here
            System.out.println("This app is close");
            finishAffinity();
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            //TODO: Activity Finish
            finish();
            overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onResume() {
        super.onResume();
        CallApiMethod();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_custom, menu);
        MenuItem Save = menu.findItem(R.id.Save_Next);
        Save.setVisible(false);
        MenuItem view = menu.findItem(R.id.Report_View);
        view.setVisible(false);
        MenuItem Push = menu.findItem(R.id.Push_Fca);
        if (StaticValues.PushOrderFlag == 0) {
            Push.setVisible(false);
        }else {
            Push.setVisible(true);
        }
        MenuItem exDateUpdate = menu.findItem(R.id.Expected_Datetime);
        exDateUpdate.setVisible(true);
        MenuItem print = menu.findItem(R.id.Print);
        print.setVisible(true);
        return super.onCreateOptionsMenu(menu);
    }
    private void CallVolleyViewBookedOrder(final String DeviceID, final String UserID,final String SessionID,final String DivisionID,final String CompanyID,final String BranchID,final String OrderID){
        showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"PartyTempOrderDetailsGroupwise", new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                // response
                Log.d("Response", response);
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    int Status=(jsonObject.getString("status")==null)?0:jsonObject.getInt("status");
                    String Msg=(jsonObject.getString("msg")==null)?"Server is not responding":jsonObject.getString("msg");
                    List<OrderViewGroupDataset> mapList = new ArrayList<>();
                    if (Status == 1) {
                        int DetailsLength = 0;
                        double TGroupStyle = 0,TGroupQty = 0,TGroupAmt = 0;
                        JSONArray jsonArrayResult = jsonObject.getJSONArray("Result");
                        for (int z = 0; z<jsonArrayResult.length(); z++) {
                            String OrderID = jsonArrayResult.getJSONObject(z).getString("OrderID");
                            JSONArray jsonArrayDetails = jsonArrayResult.getJSONObject(z).getJSONArray("Details");
                            DetailsLength = jsonArrayDetails.length();
                            for (int i = 0; i<jsonArrayDetails.length(); i++) {
                                double TotalStyle = (jsonArrayDetails.getJSONObject(i).optString("TotalStyle") == null ? 0: jsonArrayDetails.getJSONObject(i).optDouble("TotalStyle"));
                                double TotalQty = (jsonArrayDetails.getJSONObject(i).optString("TotalQty") == null ? 0: jsonArrayDetails.getJSONObject(i).optDouble("TotalQty"));
                                double TotalAmount = (jsonArrayDetails.getJSONObject(i).optString("TotalAmount") == null ? 0 : jsonArrayDetails.getJSONObject(i).optDouble("TotalAmount"));
                                TGroupStyle+= TotalStyle;
                                TGroupQty+= TotalQty;
                                TGroupAmt+= TotalAmount;
                                mapList.add(new OrderViewGroupDataset(OrderID,
                                        jsonArrayDetails.getJSONObject(i).getString("MainGroupID"),
                                        jsonArrayDetails.getJSONObject(i).getString("MainGroup"),
                                        jsonArrayDetails.getJSONObject(i).getString("GroupID"),
                                        jsonArrayDetails.getJSONObject(i).getString("UserName"),
                                        jsonArrayDetails.getJSONObject(i).getString("FullName"),
                                        jsonArrayDetails.getJSONObject(i).getString("EmpCVType"),
                                        jsonArrayDetails.getJSONObject(i).getString("EmpCVName"),
                                        jsonArrayDetails.getJSONObject(i).getString("GroupImage"),
                                        jsonArrayDetails.getJSONObject(i).getString("GroupName"),
                                        jsonArrayDetails.getJSONObject(i).getString("SubGroupID"),
                                        jsonArrayDetails.getJSONObject(i).getString("SubGroup"),
                                        ConditionLibrary.ConvertDoubleToString(TotalQty),
                                        ConditionLibrary.ConvertDoubleToString(TotalStyle),
                                        ConditionLibrary.ConvertDoubleToString(TotalAmount),
                                        jsonArrayDetails.getJSONObject(i).getString("LastBookDateTime")
                                ));
                            }
                        }
                        LoadRecyclerView(mapList);
                        txtTotalGroupHeader.setVisibility(View.VISIBLE);
                        txtTotalGroupHeader.setText("TotalStyle: "+ConditionLibrary.ConvertDoubleToString(TGroupStyle)+"   TotalQty: "+ConditionLibrary.ConvertDoubleToString(TGroupQty)+"   TotalAmt: "+ConditionLibrary.ConvertDoubleToString(TGroupAmt));
                        if (DetailsLength == 0){
                            MessageDialog.MessageDialog(context,"","No item in cart");
                            txtTotalGroupHeader.setVisibility(View.GONE);
                            txtTotalGroupHeader.setText("TotalStyle: "+ConditionLibrary.ConvertDoubleToString(TGroupStyle)+"   TotalQty: "+ConditionLibrary.ConvertDoubleToString(TGroupQty)+"   TotalAmt: "+ConditionLibrary.ConvertDoubleToString(TGroupAmt));
                        }
                    } else {
                        MessageDialog.MessageDialog(context,"",Msg);
                        LoadRecyclerView(mapList);
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
                params.put("BookType", (StaticValues.BookType == 0? "0" : StaticValues.AdvanceOrBookOrder == 0 ? "1" : "2"));
                Log.d(TAG,"Order view Group parameters:"+params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
    }
    private void CallVolleyViewBookedOrderByGroup(final String DeviceID, final String UserID,final String SessionID,final String DivisionID,final String CompanyID,final String BranchID,final String OrderID,final String GroupID,final String SubGroupID){
        showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"PartyTempOrderDetails", new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                // response
                Log.d("Response", response);
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    int Status = jsonObject.getInt("status");
                    String Msg = (jsonObject.optString("msg")==null)?"Server is not responding":jsonObject.optString("msg");
                    if (Status == 1) {
                        JSONArray jsonArrayResult = jsonObject.getJSONArray("Result");
                        if (jsonArrayResult.length() > 0) {
                            String UrlPdf = (jsonArrayResult.getJSONObject(0).optString("OrdPDFLink") == null) ? "" : jsonArrayResult.getJSONObject(0).optString("OrdPDFLink");
                            if (!UrlPdf.isEmpty()) {
                                new DownloadFileFromURL().execute(UrlPdf);
                            } else {
                                MessageDialog.MessageDialog(context, "", "PDF file removed");
                            }
                        }
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
                params.put("SubGroupID", SubGroupID);
                params.put("BookType", (StaticValues.BookType == 0? "0" : StaticValues.AdvanceOrBookOrder == 0 ? "1" : "2"));
                Log.d(TAG,"Order view details parameters:"+params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
    }
    private void CallVolleyPushOrder(final String DeviceID, final String UserID,final String SessionID,final String DivisionID,final String CompanyID,final String BranchID,final String OrderID,final String GroupID,final String Remarks){
        showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"OrderPushInFCA", new Response.Listener<String>()
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
                        MessageDialogByIntent(context, "", Msg);
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
                params.put("Remarks", Remarks);
                Log.d(TAG,"Order view Group parameters:"+params.toString());
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
        if(progressDialog!=null || swipeRefreshLayout!=null) {
            progressDialog.dismiss();
            swipeRefreshLayout.setRefreshing(false);
        }
    }
    //TODO:AsycTask of Order Details
    private void LoadRecyclerView(List<OrderViewGroupDataset> datasetList){
        this.datasetList = datasetList;
        OrderViewGroupAdapter adapter = new OrderViewGroupAdapter(context,datasetList);
        recyclerView.setAdapter(adapter);
        GridLayoutManager gridLayoutManager=new GridLayoutManager(OrderViewOrPushActivity.this,2);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
    }
    private void DialogPushOrderRemarks(final Context context){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_name_mobile);
        dialog.setCanceledOnTouchOutside(false);
        //dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setTitle("Enter Order push Remarks");
        final EditText edtRemarks=(EditText)dialog.findViewById(R.id.editTxt_name);
        EditText edtMobile=(EditText)dialog.findViewById(R.id.editTxt_Moble);
        edtRemarks.setHint("Remarks");
        edtMobile.setVisibility(View.GONE);
        Button approve=(Button)dialog.findViewById(R.id.button_Approve);
        approve.setVisibility(View.VISIBLE);
        approve.setText("Push");
        Button cancel=(Button)dialog.findViewById(R.id.button_Cancel);
        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
                if (!status.contentEquals("No Internet Connection")) {
                    String Remarks = edtRemarks.getText().toString().trim();
                    LoginActivity obj = new LoginActivity();
                    String[] str = obj.GetSharePreferenceSession(context);
                    if (str != null)
                        CallVolleyPushOrder(str[3], str[4], str[0], str[5], str[14], str[15], OrderID, "", Remarks);
                    dialog.dismiss();
                } else {
                    MessageDialog.MessageDialog(context,"",status);
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
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
                    //TODO: Activity finish
                    finish();
                    overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                }
            });
            dialog.show();
        }catch (Exception e){
            Log.e("TAG","MessageDialogException2"+e.toString());
        }
    }
    private void DialogExpectedDeliveryDatetimeUpdate(final String  OrderID){
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
                                CallVolleyExpectedDelDatetimeUpdate(str[3], str[4], str[0], str[5], str[14], str[15], OrderID, ExDateTime);
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
    private void CallVolleyExpectedDelDatetimeUpdate(final String DeviceID, final String UserID, final String SessionID, final String DivisionID, final String CompanyID, final String BranchID , final String OrderID,final String ExpectedDatetime){
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
                params.put("ExpectedDelDate", ExpectedDatetime);
                Log.d(TAG,"Search barcode or style parameters:"+params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
    }

    private void showPDialog() {
        if(pDialog!=null) {
            pDialog.show();
        }
    }
    private void hidePDialog() {
        if(pDialog!=null) {
            pDialog.dismiss();
        }
    }
    //TODO: Download Report in PDF
    class DownloadFileFromURL extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showPDialog();
        }
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // getting file length
                int lenghtOfFile = conection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                String Path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SinglaGroups/printPdf.pdf";
                // Output stream to write file
                OutputStream output = new FileOutputStream(Path);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress(""+(int)((total*100)/lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", ""+e.toString());
            }

            return null;
        }
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }
        @Override
        protected void onPostExecute(String file_url) {
            hidePDialog();
            //TODO: View PDF
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SinglaGroups/printPdf.pdf";
            File file = new File(path);
            if (file.exists()) {
                Uri uri = null;
                // So you have to use Provider
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
                }else{
                    uri = Uri.fromFile(file);
                }
                DialogPdfShareOptions();
                //FileOpenByIntent.FileOpen(context,path,uri);
            }else{
                MessageDialog.MessageDialog(context,"","File not exist");
            }
        }

    }
    private void DialogPdfShareOptions(){
        final Dialog dialog = new Dialog(new ContextThemeWrapper(context, R.style.DialogSlideAnim));
        dialog.setContentView(R.layout.dialog_pdf_share_option);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        RadioGroup radioGroupChangeOrShowroom = (RadioGroup) dialog.findViewById(R.id.RadioGroup_shareOption);
        radioGroupChangeOrShowroom.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(radioGroup.getCheckedRadioButtonId()==R.id.RadioButton_print){
                    dialog.dismiss();
                    //TODO: Share  to print
                    if (StaticValues.printFlag == 1) {
                        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SinglaGroups" + "/printPdf.pdf");
                        if (file.exists()) {
                            Uri uri = null;
                            // So you have to use Provider
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
                            } else {
                                uri = Uri.fromFile(file);
                            }
                            //FileOpenByIntent.FileOpen(context, file.getPath().toString(), uri);
                            PrintManager printManager=(PrintManager) context.getSystemService(Context.PRINT_SERVICE);
                            try {
                                PrintDocumentAdapter printAdapter = new PdfDocumentAdapter(context,file.getPath());
                                printManager.print("Document", printAdapter,new PrintAttributes.Builder().build());
                            }catch (Exception e){
                                Log.e(TAG , "Exception:"+e.toString());
                            }
                        }
                    }else{
                        MessageDialog.MessageDialog(context,"Alert","You don't have print permission of this module");
                    }
                }else if(radioGroup.getCheckedRadioButtonId()==R.id.RadioButton_mail){
                    dialog.dismiss();
                    //TODO: Share to mail
                    if (StaticValues.exportFlag == 1) {
                        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SinglaGroups" + "/printPdf.pdf");
                        if (file.exists()) {
                            Uri uri = null;
                            // So you have to use Provider
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
                            }else{
                                uri = Uri.fromFile(file);
                            }
                            Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                            emailIntent.setType("*/*");
                            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Order Print");
                            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
                            //Log.v(getClass().getSimpleName(), "sPhotoUri=" + Uri.parse("file:///" + sPhotoFileName));
                            emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
                            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                        } else {
                            MessageDialog.MessageDialog(context, "", "File not exist");
                        }
                    }else{
                        MessageDialog.MessageDialog(context,"Alert","You don't have export permission of this module");
                    }
                }else if(radioGroup.getCheckedRadioButtonId()==R.id.RadioButton_whatsapp){
                    dialog.dismiss();
                    //TODO: share to whatsapp
                    if (StaticValues.exportFlag == 1) {
                        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SinglaGroups" + "/printPdf.pdf");
                        if (file.exists()) {
                            Uri uri = null;
                            // So you have to use Provider
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
                            }else{
                                uri = Uri.fromFile(file);
                            }
                            Intent share = new Intent();
                            share.setAction(Intent.ACTION_SEND);
                            share.setType("application/pdf");
                            share.putExtra(Intent.EXTRA_STREAM, uri);
                            share.setPackage("com.whatsapp");
                            startActivity(share);
                        } else {
                            MessageDialog.MessageDialog(context, "", "File not exist");
                        }
                    }else{
                        MessageDialog.MessageDialog(context,"Alert","You don't have export permission of this module");
                    }
                }
            }
        });

    }
}