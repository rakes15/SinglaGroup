package orderbooking.customerlist;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.singlagroup.AppController;
import com.singlagroup.LoginActivity;
import com.singlagroup.R;
import com.singlagroup.adapters.GodownFilterableAdapter;
import com.singlagroup.customwidgets.CursorColor;
import com.singlagroup.customwidgets.ItemClickSupport;
import com.singlagroup.customwidgets.MessageDialog;
import com.singlagroup.datasets.GodownDataset;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import DatabaseController.CommanStatic;
import DatabaseController.DatabaseSqlLiteHandlerCloseOrderList;
import DatabaseController.DatabaseSqlLiteHandlerUserInfo;
import orderbooking.Database_Sqlite.DatabaseSqlLiteHandlerChangeParty;
import orderbooking.Database_Sqlite.DatabaseSqlLiteHandlerOrderBooking;
import orderbooking.StaticValues;
import orderbooking.customerlist.adapter.RunningFairAdapter;
import orderbooking.customerlist.datasets.CloseOrBookDataset;
import orderbooking.customerlist.temp.ChangePartyAdapter;
import orderbooking.customerlist.temp.CloseOrderAdapter;
import orderbooking.customerlist.temp.EventItem;
import orderbooking.customerlist.temp.HeaderItem;
import orderbooking.customerlist.temp.ListItem;
import services.NetworkChangeReceiver;
import services.NetworkListener;
import services.NetworkUtils;

/**
 * Created by rakes on 28-Mar-17.
 */

public class ChangePartyActivity extends AppCompatActivity {
    ActionBar actionBar;
    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ChangePartyAdapter adapter;
    private ProgressDialog progressDialog;
    private String GodownID="",Godown="",FairID="";
    private Spinner spnRunningFair;
    private LinearLayout linearLayoutRunningFair;
    private DatabaseSqlLiteHandlerUserInfo DBHandler;
    @NonNull
    private List<ListItem> items = new ArrayList<>();
    private static String TAG = ChangePartyActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);} getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_recyclerview_swipe_refresh_layout);
        Initialization();
        ModulePermission();
    }
    private void Initialization(){
        this.context = ChangePartyActivity.this;
        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_Refresh_Layout);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        this.DBHandler = new DatabaseSqlLiteHandlerUserInfo(this);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                CallApiMethod();
            }
        });

    }
    private void ModulePermission(){
        try {
            Bundle bundle = getIntent().getBundleExtra("PermissionBundle");
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
                CallApiMethod();
            }else {
                MessageDialog.MessageDialog(context,"Alert","You don't have permission of "+Title+" module");
            }
        }catch (Exception e){
            MessageDialog.MessageDialog(context,"Exception",e.toString());
        }
    }
    private void CallApiMethod(){
        //TODO: Closed Order Request
        NetworkChangeReceiver.bindNetworkListener(new NetworkListener() {
            @Override
            public void networkReceived(String status) {
                if (!status.contentEquals("No Internet Connection")) {
                    LoginActivity obj=new LoginActivity();
                    String[] str = obj.GetSharePreferenceSession(context);
                    if (str!=null) {
                        CallVolleyCloseOrder(str[2], str[3], str[4], str[0], str[16], str[5],str[14],str[15]);
                    }
                } else {
                    MessageDialog messageDialog=new MessageDialog();
                    messageDialog.MessageDialog(context,"","",status);
                }
            }
        });
        String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
        if (!status.contentEquals("No Internet Connection")) {
            LoginActivity obj=new LoginActivity();
            String[] str = obj.GetSharePreferenceSession(context);
            if (str!=null) {
                CallVolleyCloseOrder(str[2], str[3], str[4], str[0], str[16], str[5],str[14],str[15]);
            }
        } else {
            MessageDialog messageDialog=new MessageDialog();
            messageDialog.MessageDialog(context,"","",status);
        }
    }
    private void CallVolleyCloseOrder(final String MasterType, final String DeviceID, final String UserID,final String SessionID,final String MasterID,final String DivisionID,final String CompanyID,final String BranchID){
        showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"ApprovedPartyList", new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                // response
                Log.d("Response", response);
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    int Status=(jsonObject.getString("status")==null)?0:jsonObject.getInt("status");
                    String Msg=(jsonObject.getString("msg")==null)?"Server is not responding":jsonObject.getString("msg");
                    items = new ArrayList<>();
                    if (Status == 1) {
                        JSONArray jsonArrayScfo = jsonObject.getJSONArray("Result");
                        context.deleteDatabase(DatabaseSqlLiteHandlerChangeParty.DATABASE_NAME);

                        List<Map<String,String>> mapList = new ArrayList<>();
                        for (int i=0; i< jsonArrayScfo.length(); i++){
                            //list.add(new CloseOrBookDataset(jsonArrayScfo.getJSONObject(i).getString("PartyID"),jsonArrayScfo.getJSONObject(i).getString("PartyName"),jsonArrayScfo.getJSONObject(i).getString("SubPartyID"),jsonArrayScfo.getJSONObject(i).getString("SubParty"),jsonArrayScfo.getJSONObject(i).getString("AgentID"),jsonArrayScfo.getJSONObject(i).getString("AgentName"),jsonArrayScfo.getJSONObject(i).getString("Mobile"),jsonArrayScfo.getJSONObject(i).getString("City"),jsonArrayScfo.getJSONObject(i).getString("State"),jsonArrayScfo.getJSONObject(i).getString("Address"),jsonArrayScfo.getJSONObject(i).getString("OrderNo"),jsonArrayScfo.getJSONObject(i).getString("OrderDate"),jsonArrayScfo.getJSONObject(i).getInt("SubPartyApplicable"),jsonArrayScfo.getJSONObject(i).getString("Godown"),jsonArrayScfo.getJSONObject(i).getString("UserName")));
                            Map<String,String> map = new HashMap<>();
                            map.put("OrderID",jsonArrayScfo.getJSONObject(i).getString("OrderID"));
                            map.put("OrderNo",jsonArrayScfo.getJSONObject(i).getString("OrderNo"));
                            map.put("OrderDate",jsonArrayScfo.getJSONObject(i).getString("OrderDate"));
                            map.put("Godown",jsonArrayScfo.getJSONObject(i).getString("Godown"));
                            map.put("GodownID",jsonArrayScfo.getJSONObject(i).getString("Godown"));
                            map.put("PartyID",jsonArrayScfo.getJSONObject(i).getString("PartyID"));
                            map.put("PartyName",jsonArrayScfo.getJSONObject(i).getString("PartyName"));
                            map.put("SubPartyID",(jsonArrayScfo.getJSONObject(i).getString("SubPartyID")==null || jsonArrayScfo.getJSONObject(i).getString("SubPartyID").equals("null"))?"":jsonArrayScfo.getJSONObject(i).getString("SubPartyID"));
                            map.put("SubParty",(jsonArrayScfo.getJSONObject(i).getString("SubParty")==null || jsonArrayScfo.getJSONObject(i).getString("SubParty").equals("null"))?"":jsonArrayScfo.getJSONObject(i).getString("SubParty"));
                            map.put("SubPartyApplicable",jsonArrayScfo.getJSONObject(i).getString("SubPartyApplicable"));
                            map.put("RefName",jsonArrayScfo.getJSONObject(i).getString("RefName"));
                            map.put("Remarks",jsonArrayScfo.getJSONObject(i).getString("Remarks"));
                            map.put("UserName",jsonArrayScfo.getJSONObject(i).getString("UserName"));
                            map.put("AgentID",(jsonArrayScfo.getJSONObject(i).getString("AgentID")==null || jsonArrayScfo.getJSONObject(i).getString("AgentID").equals("null"))?"":jsonArrayScfo.getJSONObject(i).getString("AgentID"));
                            map.put("AgentName",(jsonArrayScfo.getJSONObject(i).getString("AgentName")==null || jsonArrayScfo.getJSONObject(i).getString("AgentName").equals("null"))?"":jsonArrayScfo.getJSONObject(i).getString("AgentName"));
                            map.put("City",jsonArrayScfo.getJSONObject(i).getString("City"));
                            map.put("State",jsonArrayScfo.getJSONObject(i).getString("State"));
                            map.put("Mobile",jsonArrayScfo.getJSONObject(i).getString("Mobile"));
                            map.put("FairName",(jsonArrayScfo.getJSONObject(i).getString("FairName")==null || jsonArrayScfo.getJSONObject(i).getString("FairName").equals("null"))?"":jsonArrayScfo.getJSONObject(i).getString("FairName"));
                            map.put("ItemCount",jsonArrayScfo.getJSONObject(i).getString("ItemCount"));
                            map.put("TotalBookQty",jsonArrayScfo.getJSONObject(i).getString("TBookQty"));
                            map.put("TotalAmount",jsonArrayScfo.getJSONObject(i).getString("TotalAmount"));
                            map.put("LastBookDate",(jsonArrayScfo.getJSONObject(i).getString("LastBookDate")==null || jsonArrayScfo.getJSONObject(i).getString("LastBookDate").equals("null"))? jsonArrayScfo.getJSONObject(i).getString("EntryDate") : jsonArrayScfo.getJSONObject(i).getString("LastBookDate"));
                            map.put("EmpCVName",jsonArrayScfo.getJSONObject(i).getString("EmpCVName"));
                            map.put("EmpCVType",jsonArrayScfo.getJSONObject(i).getString("EmpCVType"));
                            map.put("CreditDays",jsonArrayScfo.getJSONObject(i).getString("CreditDays"));
                            map.put("CreditLimit",jsonArrayScfo.getJSONObject(i).getString("CreditLimit"));
                            map.put("TotalDueAmt",jsonArrayScfo.getJSONObject(i).getString("TotalDueAmt"));
                            map.put("TotalOverDueAmt",jsonArrayScfo.getJSONObject(i).getString("TotalOverDueAmt"));
                            map.put("ExceedAmt",jsonArrayScfo.getJSONObject(i).getString("ExceedAmt"));
                            map.put("EntryDate",jsonArrayScfo.getJSONObject(i).getString("EntryDate"));
                            map.put("Email",jsonArrayScfo.getJSONObject(i).getString("Email"));
                            map.put("AccountNo",jsonArrayScfo.getJSONObject(i).getString("AccountNo"));
                            map.put("AccountHolderName",jsonArrayScfo.getJSONObject(i).getString("AccountHolderName"));
                            map.put("IFSCCOde",jsonArrayScfo.getJSONObject(i).getString("IFSCCOde"));
                            map.put("IDName",jsonArrayScfo.getJSONObject(i).getString("IDName"));
                            map.put("GSTIN",(jsonArrayScfo.getJSONObject(i).getString("GSTIN")==null || jsonArrayScfo.getJSONObject(i).getString("GSTIN").equals("null"))? "" : jsonArrayScfo.getJSONObject(i).getString("GSTIN"));
                            map.put("AvgOverDueDays",jsonArrayScfo.getJSONObject(i).getString("AvgOverDueDays"));
                            map.put("AvgDueDays",jsonArrayScfo.getJSONObject(i).getString("AvgDueDays"));
                            map.put("PricelistID",jsonArrayScfo.getJSONObject(i).getString("PricelistID"));
                            map.put("Label",(jsonArrayScfo.getJSONObject(i).optString("Extin1")==null || jsonArrayScfo.getJSONObject(i).optString("Extin1").isEmpty())? "" : jsonArrayScfo.getJSONObject(i).optString("Extin1"));
                            map.put("UnderName",(jsonArrayScfo.getJSONObject(i).optString("UnderName")==null || jsonArrayScfo.getJSONObject(i).optString("UnderName").isEmpty())? "" : jsonArrayScfo.getJSONObject(i).optString("UnderName"));
                            mapList.add(map);
                        }
                        DatabaseSqlLiteHandlerCloseOrderList DB= new DatabaseSqlLiteHandlerCloseOrderList(context);
                        DB.CloseOrderTableDelete();
                        DB.insertCloseOrderTable(mapList);
                        //adapter=new BookOrdersAdapter(context,list,0);
                        Map<String, List<CloseOrBookDataset>> events = toMap(loadEvents());

                        for (String date : events.keySet()) {
                            HeaderItem header = new HeaderItem(date);
                            items.add(header);
                            for (CloseOrBookDataset event : events.get(date)) {
                                EventItem item = new EventItem(event);
                                items.add(item);
                            }
                        }
                        adapter = new ChangePartyAdapter(items,context);
                        recyclerView.setAdapter(adapter);
                        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context);
                        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                        recyclerView.setLayoutManager(linearLayoutManager);
                        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                            @Override
                            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                                if (recyclerView.getAdapter().getItemViewType(position) == 1){
                                    EventItem eventItem = (EventItem)adapter.getItem(position);
                                    if (StaticValues.createFlag == 1) {
                                        DialogChangePartyOrShowroomOption(eventItem.getEvent());
                                    }else{
                                        MessageDialog.MessageDialog(context,"Alert","You don't have update permission of this module");
                                    }
                                }
                            }
                        });
                    } else {
                        MessageDialog.MessageDialog(context,"",Msg);
                        adapter = new ChangePartyAdapter(items,context);
                        recyclerView.setAdapter(adapter);
                        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context);
                        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                        recyclerView.setLayoutManager(linearLayoutManager);
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
                params.put("MasterType", MasterType);
                params.put("UserID", UserID);
                params.put("SessionID", SessionID);
                params.put("MasterID", MasterID);
                params.put("DivisionID", DivisionID);
                params.put("CompanyID", CompanyID);
                params.put("BranchID", BranchID);
                params.put("CurDateFlag", "0");
                Log.d(TAG,"Book order parameters:"+params.toString());
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
    @NonNull
    private List<CloseOrBookDataset> loadEvents() {

        List<CloseOrBookDataset> events = new ArrayList<>();
        DatabaseSqlLiteHandlerCloseOrderList db=new DatabaseSqlLiteHandlerCloseOrderList(context);
        events = db.getOrderList("","");
        return events;
    }
    @NonNull
    private Map<String, List<CloseOrBookDataset>> toMap(@NonNull List<CloseOrBookDataset> events) {
        Map<String, List<CloseOrBookDataset>> map = new TreeMap<>();
        for (CloseOrBookDataset event : events) {
            List<CloseOrBookDataset> value = map.get(event.getGodown());
            if (value == null) {
                value = new ArrayList<>();
                map.put(event.getGodown(), value);
            }
            value.add(event);
        }
        return map;
    }
    @Override
    public void onResume(){
        super.onResume();
        CallApiMethod();
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
    private void DialogChangePartyOrShowroomOption(final CloseOrBookDataset Dataset){
        final Dialog dialog = new Dialog(new ContextThemeWrapper(context, R.style.DialogSlideAnim));
        dialog.setContentView(R.layout.dialog_change_password);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        final TextView txtViewTitle = (TextView) dialog.findViewById(R.id.text_Title);
        final TextView txtViewMsg = (TextView) dialog.findViewById(R.id.text_Msg);
        RadioGroup radioGroupChangeOrShowroom = (RadioGroup) dialog.findViewById(R.id.RadioGroup_NewForgot);
        TextInputLayout OldPasswordWrapper = (TextInputLayout) dialog.findViewById(R.id.editText_OldPassword);
        TextInputLayout NewPasswordWrapper = (TextInputLayout) dialog.findViewById(R.id.editText_NewPassword);
        TextInputLayout ConfirmPasswordWrapper = (TextInputLayout) dialog.findViewById(R.id.editText_confirmPassword);
        Button btnSubmit = (Button) dialog.findViewById(R.id.btn_submit);
        RadioButton radioButtonChangeParty = (RadioButton) dialog.findViewById(R.id.RadioButton_NewUser);
        radioButtonChangeParty.setText("Party");
        RadioButton radioButtonChangeShowroom = (RadioButton) dialog.findViewById(R.id.RadioButton_ForgotPassword);
        radioButtonChangeShowroom.setText("Showroom");
        CursorColor.CursorColor(OldPasswordWrapper.getEditText());
        CursorColor.CursorColor(NewPasswordWrapper.getEditText());
        CursorColor.CursorColor(ConfirmPasswordWrapper.getEditText());
        txtViewTitle.setText("Change Party Or Showroom");
        radioGroupChangeOrShowroom.setVisibility(View.VISIBLE);
        OldPasswordWrapper.setVisibility(View.GONE);
        NewPasswordWrapper.setVisibility(View.GONE);
        ConfirmPasswordWrapper.setVisibility(View.GONE);
        btnSubmit.setVisibility(View.GONE);
        radioGroupChangeOrShowroom.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(radioGroup.getCheckedRadioButtonId()==R.id.RadioButton_NewUser){
                    dialog.dismiss();
                    //TODO: Change Party
                    DatabaseSqlLiteHandlerChangeParty DBChangeParty = new DatabaseSqlLiteHandlerChangeParty(context);
                    DBChangeParty.deleteChangePartyOLD();
                    DBChangeParty.insertChangePartyOldData(Dataset.getPartyID(),Dataset.getSubPartyID(),Dataset.getRefName());
                    Intent intent = new Intent(context, ChangePartySelectCustomerForOrderActivity.class);
                    intent.putExtra("Key",Dataset);
                    context.startActivity(intent);
                }else if(radioGroup.getCheckedRadioButtonId()==R.id.RadioButton_ForgotPassword){
                    dialog.dismiss();
                    //TODO: Change Showroom
                    MsgDialogFunction(context, Dataset);
                }
            }
        });

    }
    private void MsgDialogFunction(final Context context, final CloseOrBookDataset dataset){
        final Dialog dialog = new Dialog(context);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_fair_normal);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(lp);
        dialog.setTitle("Are you sure to Approved ? ");
        linearLayoutRunningFair=(LinearLayout)dialog.findViewById(R.id.Linear_RunningFair);
        spnRunningFair=(Spinner)dialog.findViewById(R.id.spinner_running_fair);
        Spinner spnGodown=(Spinner)dialog.findViewById(R.id.spinner_Godown);
        LoginActivity obj=new LoginActivity();
        final String[] str=obj.GetSharePreferenceSession(context);
        if (str!=null) {
            final List<GodownDataset> godownDatasetList = DBHandler.getReserveGodownList(str[14], str[5], str[15]);
            GodownFilterableAdapter adapter = new GodownFilterableAdapter(context, godownDatasetList, godownDatasetList);
            spnGodown.setAdapter(adapter);
            spnGodown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position>0) {
                        Godown = godownDatasetList.get(position).getGodownName();
                        if (Godown.equals("Fair")) {
                            GodownID = godownDatasetList.get(position).getGodownID();
                            Godown = godownDatasetList.get(position).getGodownName();
                            linearLayoutRunningFair.setVisibility(View.VISIBLE);
                            CallVolleyRunningFair(str[3], str[4], str[0], str[14], str[5], str[15]);
                        } else {
                            GodownID = godownDatasetList.get(position).getGodownID();
                            Godown = godownDatasetList.get(position).getGodownName();
                            linearLayoutRunningFair.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            spnRunningFair.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position>0) {
                        Map<String, String> map = (Map<String, String>) parent.getAdapter().getItem(position);
                        FairID = map.get("ID");
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }
        Button ok=(Button)dialog.findViewById(R.id.btn_ok);
        Button cancel=(Button)dialog.findViewById(R.id.btn_cancel);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = NetworkUtils.getConnectivityStatusString(context);
                if (!status.contentEquals("No Internet Connection")) {
                    LoginActivity obj = new LoginActivity();
                    String[] str = obj.GetSharePreferenceSession(context);
                    if (str != null && !GodownID.isEmpty() && !Godown.isEmpty()) {
                        if (StaticValues.createFlag == 1 || StaticValues.editFlag == 1){
                            if (Godown.equals("Fair")) {
                                if (FairID.length()>0 && !FairID.isEmpty()) {
                                    CallVolleyChangeShowroom(str[3], str[4], str[0], str[14], str[5], str[15], dataset.getOrderID(), GodownID, Godown,FairID,"1");
                                    dialog.dismiss();
                                }else{
                                    MessageDialog.MessageDialog(context,"No fair available","Please select another showroom");
                                }
                            }else{
                                CallVolleyChangeShowroom(str[3], str[4], str[0], str[14], str[5], str[15], dataset.getOrderID(), GodownID, Godown,FairID,"1");
                                dialog.dismiss();
                            }
                        }else {
                            MessageDialog.MessageDialog(context,"Alert","You don't have permission to approve the party");
                        }
                    }
                }else {
                    MessageDialog.MessageDialog(context,"",""+status);
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
    private void AlertDialogMethod(final String OrderID,final String GodownID,final String Godown,final String FairID){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage("Are you sure You want to change this Party");
        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                String status = NetworkUtils.getConnectivityStatusString(context);
                if (!status.contentEquals("No Internet Connection")) {
                    LoginActivity obj = new LoginActivity();
                    String[] str = obj.GetSharePreferenceSession(context);
                    if (str != null)
                        CallVolleyChangeShowroom(str[3], str[4], str[0], str[14], str[5], str[15], OrderID, GodownID, Godown,FairID,"1");
                }else{
                    MessageDialog.MessageDialog(context,"",""+status);
                }
            }
        });
        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    private void CallVolleyRunningFair(final String DeviceID, final String UserID,final String SessionID,final String CompanyID,final String DivisionID,final String BranchID){
        showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"RunningFair", new Response.Listener<String>()
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
                        JSONArray jsonArrayRunningFair = jsonObject.getJSONArray("Result");
                        int c=0;
                        List<Map<String,String>> list = new ArrayList<>();
                        Map<String,String> map = new HashMap<>();
                        map.put("ID","");
                        map.put("Name","Select running fair");
                        list.add(c,map);
                        for (int i=0; i< jsonArrayRunningFair.length(); i++){
                            c++;
                            map = new HashMap<>();
                            map.put("ID",jsonArrayRunningFair.getJSONObject(i).getString("ID"));
                            map.put("Name",jsonArrayRunningFair.getJSONObject(i).getString("Name"));
                            list.add(c,map);
                        }
                        spnRunningFair.setAdapter(new RunningFairAdapter(context,list,list));
                    } else {
                        MessageDialog.MessageDialog(context,"",Msg);
                        linearLayoutRunningFair.setVisibility(View.GONE);
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
                Log.d(TAG,"Running fair parameters:"+params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
    }
    private void CallVolleyChangeShowroom(final String DeviceID, final String UserID,final String SessionID,final String CompanyID,final String DivisionID,final String BranchID,final String OrderID,final String GodownID,final String GodownName,final String FairID,final String InfoTypeFlag){
        showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"TempOrderInfoUpdate", new Response.Listener<String>()
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
                params.put("GodownID", GodownID);
                params.put("GodownName", GodownName);
                params.put("FairID", FairID);
                params.put("InfoTypeFlag",InfoTypeFlag);
                Log.d(TAG,"Change Showroom parameters:"+params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
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
                    onResume();
                }
            });
            dialog.show();
        }catch (Exception e){
            Log.e("TAG","MessageDialogException2"+e.toString());
        }
    }
}
