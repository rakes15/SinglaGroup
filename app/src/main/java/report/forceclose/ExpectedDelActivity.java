package report.forceclose;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.text.TextUtils;
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
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.singlagroup.AppController;
import com.singlagroup.GlobleValues;
import com.singlagroup.LoginActivity;
import com.singlagroup.R;
import com.singlagroup.customwidgets.DateFormatsMethods;
import com.singlagroup.customwidgets.ItemClickSupport;
import com.singlagroup.customwidgets.MessageDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import DatabaseController.CommanStatic;
import orderbooking.StaticValues;
import report.DatabaseSqlite.DBSqlLiteForceClose;
import report.forceclose.adapter.GroupWiseAdapter;
import report.forceclose.adapter.OrderWiseAdapter;
import report.forceclose.adapter.PartyWiseAdapter;
import report.forceclose.model.GroupWise;
import report.forceclose.model.OrderWise;
import report.forceclose.model.PartyWise;
import services.NetworkUtils;

/**
 * Created by Rakesh on 30-July-17.
 */

public class ExpectedDelActivity extends AppCompatActivity {
    private ActionBar actionBar;
    private RecyclerView recyclerView;
    private Context context;
    private ProgressDialog progressDialog;
    private Bundle bundle;
    private OrderWiseAdapter adapter;
    private DBSqlLiteForceClose DBForceClose;
    int Type = 0,urgencyLevel=0;
    private static String TAG = ExpectedDelActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);} getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_recyclerview_swipe_refresh_layout);
        DBForceClose= new DBSqlLiteForceClose(getApplicationContext());
        DBForceClose.deleteReportData();
        Initialization();
        ModulePermission();
    }
    private void Initialization(){
        this.context = ExpectedDelActivity.this;
        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
    }
    private void ModulePermission(){
        try {
            bundle = getIntent().getBundleExtra("PermissionBundle");
            String Title = bundle.getString("Title");
            StaticValues.viewFlag = bundle.getInt("ViewFlag");
            StaticValues.editFlag = bundle.getInt("EditFlag");
            StaticValues.createFlag = bundle.getInt("CreateFlag");
            StaticValues.removeFlag = bundle.getInt("RemoveFlag");
            StaticValues.printFlag = bundle.getInt("PrintFlag");
            StaticValues.importFlag = bundle.getInt("ImportFlag");
            StaticValues.exportFlag = bundle.getInt("ExportFlag");
            StaticValues.Vtype = bundle.getInt("Vtype");
            actionBar.setTitle(Title);
            if (StaticValues.viewFlag == 1 || StaticValues.editFlag == 1 || StaticValues.createFlag == 1 || StaticValues.removeFlag == 1 || StaticValues.printFlag == 1 || StaticValues.importFlag == 1 || StaticValues.exportFlag == 1){
                ConditionByVType(StaticValues.Vtype);
                //TODO: Get Current Date
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                String CurrentDate = df.format(c.getTime());
                //TODO: Get 6 Month Date
                c.add(Calendar.MONTH, -6);
                String YesterdayDate = df.format(c.getTime());
                CallApiMethod(DateFormatsMethods.DateFormat_YYYY_MM_DD(YesterdayDate),DateFormatsMethods.DateFormat_YYYY_MM_DD(CurrentDate),String.valueOf(Type), 1);
                //DialogSearchFilter(context);
            }else {
                MessageDialog.MessageDialog(context,"Alert","You don't have permission of "+Title+" module");
            }
        }catch (Exception e){
            MessageDialog.MessageDialog(context,"Exception",e.toString());
        }
    }
    private void ConditionByVType(int VType){
        //TODO: Expected Delivery Date  flag 2
        if (VType == 39){
            //TODO: Sales Order
            Type = 0;
            GlobleValues.multi_action_flag = 2;
        }else if (VType == 43){
            //TODO: Purchase Order
            Type = 1;
            GlobleValues.multi_action_flag = 2;
        }else if (VType == 44){
            //TODO: Producation Order
            Type = 2;
            GlobleValues.multi_action_flag = 2;
        }
    }
    private void LoadRecyclerView(){
        ///TODO: Expected Delivery Date  flag 2
        if (GlobleValues.multi_action_flag == 2){
            //TODO: Module Permission

            adapter = new OrderWiseAdapter(context, DBForceClose.getOrderWise());
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(linearLayoutManager);
            ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                @Override
                public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                    OrderWise orderWise = (OrderWise) adapter.getItem(position);
                    Intent intent = new Intent(context, GroupListByPartyOrOrderWiseActivity.class);
                    PartyWise partyWise = null;
                    intent.putExtra("PartyKey", partyWise);
                    intent.putExtra("OrderKey", orderWise);
                    startActivity(intent);
                }
            });
            ItemClickSupport.addTo(recyclerView).setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
                    OrderWise orderWise = (OrderWise) adapter.getItem(position);
                    DialogExpectedDeliveryDatetimeUpdate(orderWise);
                    return false;
                }
            });
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_HOME){
            // Stop your service here
            System.out.println("This app is close");
            finishAffinity();
        }else if(keyCode==KeyEvent.KEYCODE_BACK){
            //TODO: Activity finish
            context.deleteDatabase(DBSqlLiteForceClose.DATABASE_NAME);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch(item.getItemId()){
            case android.R.id.home:
                //TODO: Activity finish
                context.deleteDatabase(DBSqlLiteForceClose.DATABASE_NAME);
                finish();
                break;
            case R.id.action_search:
                break;
            case R.id.action_filter_search:
                //TODO: Filter Search
                DialogSearchFilter(context);
                break;

        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem filterSearchItem = menu.findItem(R.id.action_filter_search);
        filterSearchItem.setVisible(true);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setVisible(true);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setFocusable(true);
        searchView.setFocusableInTouchMode(true);
        searchView.requestFocus();
        searchView.requestFocusFromTouch();
        searchView.setQueryHint("Search...");
        SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextChange(String newText)
            {
                // this is your adapter that will be filtered
                if(adapter!=null) {
                    adapter.filter(newText);
                }
//                System.out.println("on text chnge text: "+newText);
                return true;
            }
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                // this is your adapter that will be filtered
                if(adapter!=null)
                {
                    adapter.filter(query);
                }
//                System.out.println("on query submit: "+query);
                return true;
            }
        };
        searchView.setOnQueryTextListener(textChangeListener);
        return super.onCreateOptionsMenu(menu);
    }
    private void DialogSearchFilter(final Context context){
        final Dialog dialog = new Dialog(new ContextThemeWrapper(context, R.style.DialogSlideAnim));
        dialog.setContentView(R.layout.dialog_force_close_search_filter);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.TOP;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        //TODO: Declarations
        final EditText edtFromDate = (EditText) dialog.findViewById(R.id.EditText_FromDate);
        final EditText edtToDate = (EditText) dialog.findViewById(R.id.EditText_ToDate);
        LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.Linear_Type);
        Spinner spnType = (Spinner) dialog.findViewById(R.id.spinner_Type);
        Button btnApply = (Button) dialog.findViewById(R.id.btn_apply);
        //TODO: Order Type Hide
        linearLayout.setVisibility(View.GONE);
        //TODO: Get Current Date
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String CurrentDate = df.format(c.getTime());
        edtFromDate.setInputType(InputType.TYPE_NULL);
        //TODO: Get Yesterday Date
        c.add(Calendar.DATE, -1);
        String YesterdayDate = df.format(c.getTime());
        edtFromDate.setText(YesterdayDate);
        edtToDate.setInputType(InputType.TYPE_NULL);
        edtToDate.setText(CurrentDate);
        edtFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance(TimeZone.getDefault()); // Get current date
                // Create the DatePickerDialog instance
                DatePickerDialog datePicker = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        DecimalFormat formatter = new DecimalFormat("00");
                        String Date = formatter.format(dayOfMonth) + "-" + formatter.format(month + 1) + "-"+ year;
                        edtFromDate.setText(Date);
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                datePicker.setTitle("Select From Date");
                datePicker.show();
            }
        });
        edtToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance(TimeZone.getDefault()); // Get current date
                // Create the DatePickerDialog instance
                DatePickerDialog datePicker = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        DecimalFormat formatter = new DecimalFormat("00");
                        String Date = formatter.format(dayOfMonth) + "-" + formatter.format(month + 1) + "-"+ year;
                        edtToDate.setText(Date);
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                datePicker.setTitle("Select To Date");
                datePicker.show();
            }
        });
        //TODO: Order Type Spinner
//        String[] arrayAppType = getResources().getStringArray(R.array.ForceCloseType);
//        for (int i=0; i<arrayAppType.length; i++) {
//            if (Type == i){
//                spnType.setSelection(i);
//                break;
//            }
//        }
//        spnType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
//                Type = position;
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {}
//        });
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Filter Apply
                String FromDate = edtFromDate.getText().toString();
                String ToDate = edtToDate.getText().toString();
                CallApiMethod(DateFormatsMethods.DateFormat_YYYY_MM_DD(FromDate),DateFormatsMethods.DateFormat_YYYY_MM_DD(ToDate),String.valueOf(Type), 1);
                dialog.dismiss();
            }
        });
    }
    private void DialogExpectedDeliveryDatetimeUpdate(final OrderWise orderWise){
        final Dialog dialog = new Dialog(new ContextThemeWrapper(context, R.style.DialogSlideAnim));
        dialog.setContentView(R.layout.dialog_expected_delivery_date);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        final EditText editTextDelDate = (EditText) dialog.findViewById(R.id.ex_del_date);
        final EditText editTextDelTime = (EditText) dialog.findViewById(R.id.ex_del_time);
        LinearLayout layoutUrgency = (LinearLayout) dialog.findViewById(R.id.Linear_Urgency);
        LinearLayout layoutRemarks = (LinearLayout) dialog.findViewById(R.id.Linear_Remarks);
        Spinner spnUrgencyLevel = (Spinner) dialog.findViewById(R.id.spinner_Urgency_Level);
        final EditText edtRemarks = (EditText) dialog.findViewById(R.id.editText_remarks);
        Button btnUpdate = (Button) dialog.findViewById(R.id.button_Update);
        Button btnCancel = (Button) dialog.findViewById(R.id.button_Cancel);
        //TODO: Linear Layout
        layoutUrgency.setVisibility(View.VISIBLE);
        layoutRemarks.setVisibility(View.VISIBLE);
        //TODO: Edit text
        editTextDelDate.setInputType(InputType.TYPE_NULL);
        editTextDelTime.setInputType(InputType.TYPE_NULL);
        editTextDelDate.setText(""+DateFormatsMethods.DateFormat_DD_MM_YYYY(DateFormatsMethods.getDateTime().substring(0,10)));
        editTextDelTime.setText(""+DateFormatsMethods.getDateTime().substring(11,16));
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
        //TODO: Spinner Urgency
        spnUrgencyLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                urgencyLevel = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        //TODO: Btn Click
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ExDate = editTextDelDate.getText().toString();
                String ExTime = editTextDelTime.getText().toString();
                String Remarks = edtRemarks.getText().toString().toString();
                if (!ExDate.isEmpty() && !ExTime.isEmpty() && urgencyLevel!=0) {
                    String ExDateTime = DateFormatsMethods.DateFormat_YYYY_MM_DD(ExDate) + " " + ExTime + ":00";
                    String status = NetworkUtils.getConnectivityStatusString(context);
                    if (!status.contentEquals("No Internet Connection")) {
                        LoginActivity obj = new LoginActivity();
                        String[] str = obj.GetSharePreferenceSession(context);
                        if (str != null)
                            if (StaticValues.editFlag == 1) {
                                CallVolleyExpectedDelDatetimeUpdate(str[3], str[4], str[0], str[5], str[14], str[15], orderWise.getOrderID(),"","", ExDateTime,""+orderWise.getVType(),Remarks,"2",""+urgencyLevel,CommanStatic.LogIN_UserName);
                                dialog.dismiss();
                            }else{
                                MessageDialog.MessageDialog(context,"Alert","You don't have edit permission of this module");
                            }
                    }else {
                        MessageDialog.MessageDialog(context,"",status);
                    }
                }else {
                    if (urgencyLevel == 0){MessageDialog.MessageDialog(context,"Alert","Please select urgency level");}
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
    private void CallVolleyExpectedDelDatetimeUpdate(final String DeviceID, final String UserID, final String SessionID, final String DivisionID, final String CompanyID, final String BranchID , final String OrderID, final String GroupID, final String ItemID,final String ExpectedDatetime, final String VType, final String Remarks, final String Type, final String UrgencyLevel,final String UserName){
        showDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"ExpDelDtUpdate", new Response.Listener<String>()
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
                        DBForceClose.UpdateExpDelDate(ExpectedDatetime,OrderID,"","");
                        LoadRecyclerView();
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
                params.put("UserID", UserID);
                params.put("SessionID", SessionID);
                params.put("DivisionID", DivisionID);
                params.put("CompanyID", CompanyID);
                params.put("BranchID", BranchID);
                params.put("OrderID", OrderID);
                params.put("GroupID", GroupID);
                params.put("ItemID", ItemID);
                params.put("ExpDelDt", ExpectedDatetime);
                params.put("VType", VType);
                params.put("Remarks", Remarks);
                params.put("Type", Type);
                params.put("UrgLab", UrgencyLevel);
                params.put("UserName", UserName);
                Log.d(TAG,"Update Expected Del datetime parameters:"+params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
    }
    //TODO: Call Api Method
    private void CallApiMethod(final String FromDt,final String ToDt, final String Type, final int flag){
        //TODO: Call Volley Api
        String status = NetworkUtils.getConnectivityStatusString(context);
        if (!status.contentEquals("No Internet Connection")) {
            LoginActivity obj=new LoginActivity();
            String[] str = obj.GetSharePreferenceSession(context);
            if (str!=null) {
                CallVolleyForceCloseReport(str[3], str[4], str[0], str[5],str[14],str[15],FromDt,ToDt,Type,flag);
            }
        } else {
            MessageDialog.MessageDialog(context,"",status);
        }
    }
    //TODO: Call Volley Order ForceClose
    public void CallVolleyForceCloseReport(final String DeviceID, final String UserID,final String SessionID,final String DivisionID,final String CompanyID,final String BranchID,final String FromDt,final String ToDt,final String Type,final int flag){
        showDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"VoucherListForOrderClosed", new Response.Listener<String>()
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
                        JSONArray jsonArrayScfo = jsonObject.getJSONArray("Result");
                        List<Map<String,String>> mapList = new ArrayList<>();
                        for (int i=0; i< jsonArrayScfo.length(); i++){
                            Map<String,String> map = new HashMap<>();
                            map.put("VType",(jsonArrayScfo.getJSONObject(i).optString("VType")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("VType")));
                            map.put("MainGroupName",(jsonArrayScfo.getJSONObject(i).optString("MainGroup")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("MainGroup")));
                            map.put("GroupID",(jsonArrayScfo.getJSONObject(i).optString("GroupID")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("GroupID")));
                            map.put("GroupName",(jsonArrayScfo.getJSONObject(i).optString("GroupName")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("GroupName")));
                            map.put("CustID",(jsonArrayScfo.getJSONObject(i).optString("PartyID")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("PartyID")));
                            map.put("CustName",(jsonArrayScfo.getJSONObject(i).optString("PartyName")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("PartyName")));
                            map.put("SubCustID",(jsonArrayScfo.getJSONObject(i).optString("SubPartyID")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("SubPartyID")));
                            map.put("SubCustName",(jsonArrayScfo.getJSONObject(i).optString("SubPartyName")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("SubPartyName")));
                            map.put("OrderID",(jsonArrayScfo.getJSONObject(i).optString("OrderID")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("OrderID")));
                            map.put("OrderNo",(jsonArrayScfo.getJSONObject(i).optString("OrderNo")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("OrderNo")));
                            map.put("OrderDate",(jsonArrayScfo.getJSONObject(i).optString("OrderDate")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("OrderDate")));
                            map.put("ItemID",(jsonArrayScfo.getJSONObject(i).optString("ItemID")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("ItemID")));
                            map.put("ItemCode",(jsonArrayScfo.getJSONObject(i).optString("ItemCode")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("ItemCode")));
                            map.put("ItemName",(jsonArrayScfo.getJSONObject(i).optString("ItemName")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("ItemName")));
                            map.put("SubItemName",(jsonArrayScfo.getJSONObject(i).optString("SubItemName")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("SubItemName")));
                            map.put("ColorID",(jsonArrayScfo.getJSONObject(i).optString("ColorID")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("ColorID")));
                            map.put("ColorName",(jsonArrayScfo.getJSONObject(i).optString("ColorName")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("ColorName")));
                            map.put("SizeID",(jsonArrayScfo.getJSONObject(i).optString("SizeID")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("SizeID")));
                            map.put("SizeName",(jsonArrayScfo.getJSONObject(i).optString("SizeName")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("SizeName")));
                            map.put("price",(jsonArrayScfo.getJSONObject(i).optString("Price")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("Price")));
                            map.put("stock",(jsonArrayScfo.getJSONObject(i).optString("ItemStock")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("ItemStock")));
                            map.put("pendingQty",(jsonArrayScfo.getJSONObject(i).optString("PendingQty")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("PendingQty")));
                            map.put("bookedQty",(jsonArrayScfo.getJSONObject(i).optString("CSBkQty")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("CSBkQty")));
                            map.put("DispatchQty",(jsonArrayScfo.getJSONObject(i).optString("DispatchQty")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("DispatchQty")));
                            map.put("colorSizeStock",(jsonArrayScfo.getJSONObject(i).optString("CSStk")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("CSStk")));
                            map.put("subItemStock",(jsonArrayScfo.getJSONObject(i).optString("SubItemStock")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("SubItemStock")));
                            map.put("ColorFamilyName",(jsonArrayScfo.getJSONObject(i).optString("CFName")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("CFName")));
                            map.put("AStatus",(jsonArrayScfo.getJSONObject(i).optString("AStatus")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("AStatus")));
                            map.put("RefrenceName",(jsonArrayScfo.getJSONObject(i).optString("RefrenceName")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("RefrenceName")));
                            map.put("OrderType",(jsonArrayScfo.getJSONObject(i).optString("OrderType")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("OrderType")));
                            map.put("OrderQty",(jsonArrayScfo.getJSONObject(i).optString("OrderQty")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("OrderQty")));
                            map.put("OrderTypeVal",(jsonArrayScfo.getJSONObject(i).optString("OrdTyVal")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("OrdTyVal")));
                            map.put("ShowroomID",(jsonArrayScfo.getJSONObject(i).optString("ShowroomID")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("ShowroomID")));
                            map.put("Showroom",(jsonArrayScfo.getJSONObject(i).optString("Showroom")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("Showroom")));
                            map.put("ExpectedDeliveryDate",(jsonArrayScfo.getJSONObject(i).optString("ExpDelDt")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("ExpDelDt")));
                            map.put("urgencyLevel",(jsonArrayScfo.getJSONObject(i).optString("urgencyLevel")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("urgencyLevel")));
                            map.put("Agent",(jsonArrayScfo.getJSONObject(i).optString("Agent")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("Agent")));
                            map.put("MDApplicable",(jsonArrayScfo.getJSONObject(i).optString("MDApp")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("MDApp")));
                            map.put("SubItemApplicable",(jsonArrayScfo.getJSONObject(i).optString("SIApp")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("SIApp")));

                            map.put("BranchName",(jsonArrayScfo.getJSONObject(i).optString("BranchName")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("BranchName")));
                            map.put("AppType",(jsonArrayScfo.getJSONObject(i).optString("AppType")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("AppType")));
                            map.put("ItemRowID",(jsonArrayScfo.getJSONObject(i).optString("ItemRowID")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("ItemRowID")));
                            map.put("ItemCat",(jsonArrayScfo.getJSONObject(i).optString("ItemCat")==null ? "" : jsonArrayScfo.getJSONObject(i).optString("ItemCat")));

                            //TODO: Add map into list
                            mapList.add(map);
                        }
                        context.deleteDatabase(DBSqlLiteForceClose.DATABASE_NAME);
                        DBSqlLiteForceClose DB= new DBSqlLiteForceClose(context);
                        DB.deleteReportData();
                        DB.insertForceCloseTable(mapList);
                        //TODO: Load View Pager
                        LoadRecyclerView();
                    } else {
                        //if (flag == 1)
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
                params.put("UserID", UserID);
                params.put("SessionID", SessionID);
                params.put("DivisionID", DivisionID);
                params.put("CompanyID", CompanyID);
                params.put("BranchID", BranchID);
                params.put("FromDt", FromDt);
                params.put("ToDt", ToDt);
                params.put("Type", Type);
                Log.i(TAG,"Force Close parameters:"+params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
    }
    private void showDialog() {
        if(progressDialog!=null) {
            progressDialog.show();
        }
    }
    private void hideDialog() {
        if(progressDialog!=null ) {
            progressDialog.dismiss();
        }
    }
}
