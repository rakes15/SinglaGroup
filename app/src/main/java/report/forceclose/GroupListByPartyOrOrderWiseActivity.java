package report.forceclose;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import DatabaseController.CommanStatic;
import orderbooking.StaticValues;
import orderbooking.catalogue.CatalogueActivity;
import orderbooking.customerlist.datasets.CloseOrBookDataset;
import orderbooking.customerlist.datasets.SelectCustomerWithSubCustomerForOrderDataset;
import report.DatabaseSqlite.DBSqlLiteForceClose;
import report.forceclose.adapter.GroupWiseAdapter;
import report.forceclose.model.GroupWise;
import report.forceclose.model.OrderWise;
import report.forceclose.model.PartyWise;
import services.NetworkUtils;

/**
 * Created by Rakesh on 13-Oct-16.
 */

public class GroupListByPartyOrOrderWiseActivity extends AppCompatActivity {
    ActionBar actionBar;
    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private GroupWiseAdapter adapter;
    private ProgressDialog progressDialog;
    private DBSqlLiteForceClose DBHandler;
    private int urgencyLevel=0;
    private static String TAG = GroupListByPartyOrOrderWiseActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);} getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_recyclerview_swipe_refresh_layout);
        Initialization();
        GetIntentMethod();
    }
    private void Initialization(){
        this.context = GroupListByPartyOrOrderWiseActivity.this;
        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_Refresh_Layout);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        this.DBHandler = new DBSqlLiteForceClose(context);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                GetIntentMethod();
            }
        });
    }
    private void GetIntentMethod(){
        try {
            PartyWise partyWise = (PartyWise) getIntent().getExtras().get("PartyKey");
            OrderWise orderWise = (OrderWise) getIntent().getExtras().get("OrderKey");
            if(partyWise!=null) {
                actionBar.setTitle(""+partyWise.getPartyName());
                LoadRecyclerViewPartyWise(partyWise);
            }else if (orderWise!=null) {
                actionBar.setTitle(""+orderWise.getOrderNo());
                LoadRecyclerViewOrderWise(orderWise);
            }else {
                MessageDialog.MessageDialog(GroupListByPartyOrOrderWiseActivity.this,"Intent","Dataset is null");
            }
        }catch (Exception e){
            MessageDialog.MessageDialog(GroupListByPartyOrOrderWiseActivity.this,"Exception",e.toString());
        }
    }
    private void LoadRecyclerViewPartyWise(PartyWise partyWise){
        //TODO: Load Recycler View
        if (!partyWise.getPartyID().isEmpty() && partyWise.getPartyID()!=null) {
            adapter = new GroupWiseAdapter(context,DBHandler.getGroupListByPartyWise(partyWise.getPartyID(),partyWise.getSubPartyID(),partyWise.getRefName()));
            recyclerView.setAdapter(adapter);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                @Override
                public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                    GroupWise dataset = (GroupWise) adapter.getItem(position);
                    Intent intent = new Intent(context, ItemDetailsListByAllWiseActivity.class);
                    intent.putExtra("Key",dataset);
                    startActivity(intent);
                }
            });
        }
        hideDialog();
    }
    private void LoadRecyclerViewOrderWise(OrderWise orderWise){
        //TODO: Load Recycler View
        adapter = new GroupWiseAdapter(context,DBHandler.getGroupListByOrderWise(orderWise.getOrderID()));
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                GroupWise dataset = (GroupWise) adapter.getItem(position);
                Intent intent = new Intent(context, ItemDetailsListByAllWiseActivity.class);
                intent.putExtra("Key",dataset);
                startActivity(intent);
            }
        });
        //TODO: Expected Delivery Date Flag 2
        if (GlobleValues.multi_action_flag == 2) {
            ItemClickSupport.addTo(recyclerView).setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
                    GroupWise groupWise = (GroupWise) adapter.getItem(position);
                    if (StaticValues.Vtype == 39) {
                        DialogExpectedDeliveryDatetimeUpdate(groupWise);
                    }
                    return false;
                }
            });
        }
        hideDialog();
    }
    private void showDialog() {
        if(progressDialog!=null) {
            progressDialog.show();
        }
    }
    private void hideDialog() {
        if(progressDialog!=null || swipeRefreshLayout!=null) {
            progressDialog.dismiss();
            swipeRefreshLayout.setRefreshing(false);
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        GetIntentMethod();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch(item.getItemId()){
            case android.R.id.home:
                //TODO: Activity finish
                finish();
                break;
            case R.id.action_search:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_HOME){
            // Stop your service here
            System.out.println("This app is close");
            finishAffinity();
        }else if(keyCode==KeyEvent.KEYCODE_BACK){

            //TODO: Activity finish
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
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

    private void DialogExpectedDeliveryDatetimeUpdate(final GroupWise groupWise){
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
        editTextDelDate.setText(""+ DateFormatsMethods.DateFormat_DD_MM_YYYY(DateFormatsMethods.getDateTime().substring(0,10)));
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
                                CallVolleyExpectedDelDatetimeUpdate(str[3], str[4], str[0], str[5], str[14], str[15], groupWise.getOrderID(),groupWise.getGroupID(),"", ExDateTime,""+groupWise.getVType(),Remarks,"1",""+urgencyLevel,CommanStatic.LogIN_UserName);
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
                        DBHandler.UpdateExpDelDate(ExpectedDatetime,OrderID,GroupID,"");
                        GetIntentMethod();
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
}
