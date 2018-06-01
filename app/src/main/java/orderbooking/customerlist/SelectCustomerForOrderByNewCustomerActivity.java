package orderbooking.customerlist;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
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
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
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
import com.singlagroup.customwidgets.DateFormatsMethods;
import com.singlagroup.customwidgets.ItemClickSupport;
import com.singlagroup.customwidgets.MessageDialog;
import com.singlagroup.datasets.GodownDataset;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DatabaseController.CommanStatic;
import DatabaseController.DatabaseSqlLiteHandlerUserInfo;
import orderbooking.StaticValues;
import orderbooking.catalogue.CatalogueActivity;
import orderbooking.customerlist.adapter.RunningFairAdapter;
import orderbooking.customerlist.adapter.SelectCustomerForOrderAdapter;
import orderbooking.customerlist.adapter.SelectCustomerWithSubCustomerForOrderAdapter;
import orderbooking.customerlist.datasets.CloseOrBookDataset;
import orderbooking.customerlist.datasets.SelectCustomerForOrderDataset;
import orderbooking.customerlist.datasets.SelectCustomerWithSubCustomerForOrderDataset;
import orderbooking.customerlist.responsedatasets.ResponsePartyInfoCheck;
import services.NetworkChangeReceiver;
import services.NetworkListener;
import services.NetworkUtils;

/**
 * Created by Rakesh on 13-Oct-16.
 */

public class SelectCustomerForOrderByNewCustomerActivity extends AppCompatActivity {
    ActionBar actionBar;
    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private SelectCustomerForOrderAdapter adapter;
    private ProgressDialog progressDialog;
    private DatabaseSqlLiteHandlerUserInfo DBHandler;
    private SelectCustomerWithSubCustomerForOrderDataset datasetByNewCustomer;
    private String GodownID="",Godown="",FairID="",PartyID="",PartyName="",SubPartyID="",SubPartyName="",RefName="",UnderID="";
    private Spinner spnRunningFair;
    private LinearLayout linearLayoutRunningFair;
    private int activityFlag = 0;
    private static String TAG = SelectCustomerForOrderByNewCustomerActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);} getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_recyclerview_swipe_refresh_layout);
        Initialization();
        ModulePermission();
    }
    private void Initialization(){
        this.context = SelectCustomerForOrderByNewCustomerActivity.this;
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
                ModulePermission();
            }
        });
    }
    private void ModulePermission(){
        try {
            datasetByNewCustomer = (SelectCustomerWithSubCustomerForOrderDataset) getIntent().getExtras().get("Key");
            RefName = getIntent().getExtras().getString("RefName");
            Bundle bundle = getIntent().getBundleExtra("PermissionBundle");
            String Title = bundle.getString("Title",String.valueOf(R.string.app_name));
            StaticValues.viewFlag = bundle.getInt("ViewFlag",0);
            StaticValues.editFlag = bundle.getInt("EditFlag",0);
            StaticValues.createFlag = bundle.getInt("CreateFlag",0);
            StaticValues.removeFlag = bundle.getInt("RemoveFlag",0);
            StaticValues.printFlag = bundle.getInt("PrintFlag",0);
            StaticValues.importFlag = bundle.getInt("ImportFlag",0);
            StaticValues.exportFlag = bundle.getInt("ExportFlag",0);
            StaticValues.Vtype = bundle.getInt("Vtype",0);
            activityFlag = bundle.getInt("ActivityFlag",0);
            actionBar.setTitle(Title);
            if (StaticValues.viewFlag == 1 || StaticValues.editFlag == 1 || StaticValues.createFlag == 1 || StaticValues.removeFlag == 1 || StaticValues.printFlag == 1 || StaticValues.importFlag == 1 || StaticValues.exportFlag == 1){
                CallApiMethod();
            }else {
                MessageDialog.MessageDialog(SelectCustomerForOrderByNewCustomerActivity.this,"Alert","You don't have permission of "+Title+" module");
            }
        }catch (Exception e){
            MessageDialog.MessageDialog(SelectCustomerForOrderByNewCustomerActivity.this,"Exception",e.toString());
        }
    }
    private void CallApiMethod(){
        //TODO: Select Customer for order Request
        NetworkChangeReceiver.bindNetworkListener(new NetworkListener() {
            @Override
            public void networkReceived(String status) {
                if (!status.contentEquals("No Internet Connection")) {
                    LoginActivity obj=new LoginActivity();
                    String[] str = obj.GetSharePreferenceSession(SelectCustomerForOrderByNewCustomerActivity.this);
                    if (str!=null) {
                        CallVolleySelectCustomerForOrder(str[2], str[3], str[4], str[0], str[16], str[5],str[14]);
                    }
                } else {
                    MessageDialog.MessageDialog(SelectCustomerForOrderByNewCustomerActivity.this,"",status);
                }
            }
        });
        String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
        if (!status.contentEquals("No Internet Connection")) {
            LoginActivity obj=new LoginActivity();
            String[] str = obj.GetSharePreferenceSession(SelectCustomerForOrderByNewCustomerActivity.this);
            if (str!=null) {
                CallVolleySelectCustomerForOrder(str[2], str[3], str[4], str[0], str[16], str[5],str[14]);
            }
        } else {
            MessageDialog messageDialog=new MessageDialog();
            messageDialog.MessageDialog(SelectCustomerForOrderByNewCustomerActivity.this,"","","No Internet Connection");
        }
    }
    private void CallVolleySelectCustomerForOrder(final String MasterType, final String DeviceID, final String UserID,final String SessionID,final String MasterID,final String DivisionID,final String CompanyID){
        showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"PartyList", new Response.Listener<String>()
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
                        final List<SelectCustomerForOrderDataset> list = new ArrayList<>();
                        for (int i=0; i< jsonArrayScfo.length(); i++){
                            if (!jsonArrayScfo.getJSONObject(i).getString("PartyName").toLowerCase().trim().equals("new customer"))
                            list.add(new SelectCustomerForOrderDataset(jsonArrayScfo.getJSONObject(i).getString("PartyID"),jsonArrayScfo.getJSONObject(i).getString("PartyName"),jsonArrayScfo.getJSONObject(i).getString("AgentID"),jsonArrayScfo.getJSONObject(i).getString("AgentName"),jsonArrayScfo.getJSONObject(i).getString("Mobile"),jsonArrayScfo.getJSONObject(i).getString("City"),jsonArrayScfo.getJSONObject(i).getString("State"),jsonArrayScfo.getJSONObject(i).getString("Address"),jsonArrayScfo.getJSONObject(i).getString("Active"),jsonArrayScfo.getJSONObject(i).getInt("SubPartyApplicable"),jsonArrayScfo.getJSONObject(i).getInt("MultiOrder"),jsonArrayScfo.getJSONObject(i).getString("Email"),jsonArrayScfo.getJSONObject(i).getString("AccountNo"),jsonArrayScfo.getJSONObject(i).getString("AccountHolderName"),jsonArrayScfo.getJSONObject(i).getString("IFSCCOde"),jsonArrayScfo.getJSONObject(i).getString("IDName"),jsonArrayScfo.getJSONObject(i).getString("GSTIN"),jsonArrayScfo.getJSONObject(i).getString("Extin1")));
                        }
                        LoadRecyclerView(list);
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
                params.put("MasterType", MasterType);
                params.put("UserID", UserID);
                params.put("SessionID", SessionID);
                params.put("MasterID", MasterID);
                params.put("DivisionID", DivisionID);
                params.put("CompanyID", CompanyID);
                Log.d(TAG,"Select customer for order parameters:"+params.toString());
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
    private void LoadRecyclerView(List<SelectCustomerForOrderDataset> datasetList){
        adapter=new SelectCustomerForOrderAdapter(context,datasetList);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                SelectCustomerForOrderDataset dataset = (SelectCustomerForOrderDataset) adapter.getItem(position);
                PartyID = datasetByNewCustomer.getPartyID();//TODO: Party ID
                UnderID = dataset.getPartyID(); //TODO: Under ID
                if (dataset.getSubPartyApplicable() == 0 && dataset.getMultiOrder() == 1) {
                    if (StaticValues.editFlag == 1 || StaticValues.createFlag == 1 ) {
                        NameRemarksDialogFunction(context);
                    } else {
                        MessageDialog.MessageDialog(context,"Alert","You don't have permission to approve the party");
                    }
                }else {
                    if (StaticValues.editFlag == 1 || StaticValues.createFlag == 1) {
                        LoginActivity obj = new LoginActivity();
                        String[] str = obj.GetSharePreferenceSession(context);
                        if (str != null)
                            CallVolleyPartyInfoCheck(str[3], str[4], str[0], str[14], str[5], str[15], PartyID, SubPartyID, RefName, UnderID);
                    } else {
                        MessageDialog.MessageDialog(context, "Alert", "You don't have permission to approve the party");
                    }
                }
            }
        });
    }
    //TODO: Name Remark for Multi Order Dialog
    private void NameRemarksDialogFunction(final Context context){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_name_mobile);
        dialog.setCanceledOnTouchOutside(false);
        //dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setTitle("Enter Name and Mobile");
        final EditText edtName=(EditText)dialog.findViewById(R.id.editTxt_name);
        final EditText edtMobile=(EditText)dialog.findViewById(R.id.editTxt_Moble);
        Button approve=(Button)dialog.findViewById(R.id.button_Approve);
        Button cancel=(Button)dialog.findViewById(R.id.button_Cancel);
        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!edtName.getText().toString().trim().isEmpty() && !edtMobile.getText().toString().trim().isEmpty()){
                    RefName = edtName.getText().toString().trim() +" - "+edtMobile.getText().toString().trim();;
                    //MsgDialogFunction(context);
                    LoginActivity obj = new LoginActivity();
                    String[] str = obj.GetSharePreferenceSession(context);
                    if (str != null)
                        CallVolleyPartyInfoCheck(str[3], str[4], str[0], str[14], str[5], str[15],PartyID,SubPartyID,RefName,UnderID);
                    dialog.dismiss();
                } else {
                    MessageDialog.MessageDialog(context, "Name & Mobile dialog","Name & Mobile no are mandatory!!!");
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
    //TODO: Dialog Party info check
    private void DialogPartyInfoCheck(final Context context, String[][] str, final int approveFlag){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_party_info_check);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        final TextView txtViewMsg=(TextView) dialog.findViewById(R.id.text_msg);
        final TableLayout tableLayout=(TableLayout) dialog.findViewById(R.id.table_Layout);
        Button Yes=(Button)dialog.findViewById(R.id.Button_OK);
        Button No=(Button)dialog.findViewById(R.id.button_Cancel);
        txtViewMsg.setText(""+str[0][0]);
        setTableLayout(tableLayout,str);
        if (approveFlag == 1){
            Yes.setText("OK");
            No.setVisibility(View.GONE);
        }
        Yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (approveFlag == 1) {
                    dialog.dismiss();
                }else if (approveFlag == 2){
                    String status = NetworkUtils.getConnectivityStatusString(context);
                    if (!status.contentEquals("No Internet Connection")) {
                        LoginActivity obj = new LoginActivity();
                        String[] str = obj.GetSharePreferenceSession(context);
                        if (str != null && !GodownID.isEmpty() && !Godown.isEmpty()  && !PartyID.isEmpty()) {
                            if (StaticValues.createFlag == 1 || StaticValues.editFlag == 1){
                                FairID = ((FairID==null || FairID.equals("null"))?"":FairID);
                                CallVolleyApprove(str[3], str[4], str[0], str[14], str[5], str[15],GodownID,Godown,FairID,PartyID,SubPartyID,RefName,UnderID);
                                dialog.dismiss();
                            }else {
                                MessageDialog.MessageDialog(context,"Alert","You don't have permission to approve the party");
                            }
                        }
                    }else {
                        MessageDialog.MessageDialog(context,"",""+status);
                    }
                    dialog.dismiss();
                }else {
                    MsgDialogFunction(context);
                    dialog.dismiss();
                }
            }
        });
        No.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private void setTableLayout(TableLayout tableLayout,String[][] str){
        //TODO: TableLayout set
        tableLayout.removeAllViewsInLayout();
        tableLayout.removeAllViews();
        for (int i = 1;i<str.length;i++){

            if ((!str[i][0].equals("") && !str[0][i].equals(""))) {
                View v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout, false);
                TextView txtHeader = (TextView) v.findViewById(R.id.header);
                txtHeader.setText("" + str[i][0]);

                TextView txt = (TextView) v.findViewById(R.id.content);
                txt.setText("" + str[0][i]);
                tableLayout.addView(v);
            }
        }
    }
    //TODO: Approval confirmation message Dialog
    private void MsgDialogFunction(final Context context){
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
                    if (str != null && !GodownID.isEmpty() && !Godown.isEmpty()  && !PartyID.isEmpty()) {
                        if (StaticValues.createFlag == 1 || StaticValues.editFlag == 1){
                            if (Godown.equals("Fair")) {
                                if (FairID.length()>0 && !FairID.isEmpty()) {
                                    CallVolleyApprove(str[3], str[4], str[0], str[14], str[5], str[15], GodownID, Godown, FairID, PartyID, SubPartyID, RefName,UnderID);
                                    dialog.dismiss();
                                }else{
                                    MessageDialog.MessageDialog(context,"No fair available","Please select another showroom");
                                }
                            }else{
                                CallVolleyApprove(str[3], str[4], str[0], str[14], str[5], str[15], GodownID, Godown, FairID, PartyID, SubPartyID, RefName,UnderID);
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
    private void CallVolleyPartyInfoCheck(final String DeviceID, final String UserID,final String SessionID,final String CompanyID,final String DivisionID,final String BranchID,final String PartyID,final String SubPartyID,final String RefName,final String UnderID){
        showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"PartyInfoCheck", new Response.Listener<String>()
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
                        JSONObject jsonObjectResult = jsonObject.getJSONObject("Result");
                        int NewParty = jsonObjectResult.getInt("NewParty");
                        String[][] str = new String[10][10];
                        if (NewParty == 0){
                            ResponsePartyInfoCheck result = new ResponsePartyInfoCheck(jsonObjectResult.getString("OrderID"),jsonObjectResult.getString("OrderNo"),jsonObjectResult.getString("OrderDate"),jsonObjectResult.getString("PartyID"),jsonObjectResult.getString("SubPartyID"),jsonObjectResult.getString("RefName"),jsonObjectResult.getString("Remarks"),jsonObjectResult.getString("EntryDatetime"),jsonObjectResult.getString("GodownID"),jsonObjectResult.getString("Godown"),jsonObjectResult.getString("DivisionID"),jsonObjectResult.getString("Division"),jsonObjectResult.getString("FairID"),jsonObjectResult.getString("Fair"),jsonObjectResult.getString("BranchID"),jsonObjectResult.getString("Branch"),jsonObjectResult.getInt("OldApprovedFlag"),jsonObjectResult.getInt("CreditDays"),jsonObjectResult.getString("CreditLimit"),jsonObjectResult.getString("TotalDueAmt"),jsonObjectResult.getInt("TotalOverDueAmt"),jsonObjectResult.getInt("ExceedAmt"));
                            if (result!=null){
                                if (result.getOldApprovedFlag()==1) {
                                    //TODO: Header
                                    str[0][0] = "Do you want to re-approve this party";
                                    str[1][0] =  "Order No.";
                                    str[2][0] =  "Order Date";
                                    str[3][0] =  "Party Name";
                                    str[4][0] =  "Sub Party Name";
                                    str[5][0] =  "Reference Name";
                                    str[6][0] =  "Showroom Name";
                                    str[7][0] =  "Fair Name";
                                    str[8][0] =  "Approved By";
                                    str[9][0] =  "Approved Time";

                                    //TODO: Data   Credit limit will exceed by $ amount
                                    str[0][1] =  (jsonObjectResult.getString("OrderNo")==null?"":jsonObjectResult.getString("OrderNo"));
                                    str[0][2] =  jsonObjectResult.getString("OrderDate");
                                    str[0][3] =  PartyName;
                                    str[0][4] =  SubPartyName;
                                    str[0][5] =  jsonObjectResult.getString("RefName");
                                    str[0][6] =  jsonObjectResult.getString("Godown");
                                    str[0][7] =  ((jsonObjectResult.getString("Fair")==null || jsonObjectResult.getString("Fair").equals("null"))?"":jsonObjectResult.getString("Fair"));
                                    str[0][8] =  jsonObjectResult.getString("LoginID")+"\n"+jsonObjectResult.getString("UserName")+"\n"+jsonObjectResult.getString("EmpCVName")+"\n"+jsonObjectResult.getString("EmpCVType");
                                    str[0][9] =  DateFormatsMethods.DaysHoursMinutesCount(jsonObjectResult.getString("EntryDatetime"));

                                    GodownID = jsonObjectResult.getString("GodownID");
                                    Godown = jsonObjectResult.getString("Godown");
                                    FairID = jsonObjectResult.getString("FairID");
                                    DialogPartyInfoCheck(context,str,2);
                                }else{
                                    //TODO: Header
                                    str[0][0] = "This party is already approved";
                                    str[1][0] =  "Order No.";
                                    str[2][0] =  "Order Date";
                                    str[3][0] =  "Party Name";
                                    str[4][0] =  "Sub Party Name";
                                    str[5][0] =  "Reference Name";
                                    str[6][0] =  "Showroom Name";
                                    str[7][0] =  "Fair Name";
                                    str[8][0] =  "Approved By";
                                    str[9][0] =  "Approved Time";
                                    //TODO: Data
                                    str[0][1] =  jsonObjectResult.getString("OrderNo");
                                    str[0][2] =  jsonObjectResult.getString("OrderDate");
                                    str[0][3] =  PartyName;
                                    str[0][4] =  SubPartyName;
                                    str[0][5] =  jsonObjectResult.getString("RefName");
                                    str[0][6] =  jsonObjectResult.getString("Godown");
                                    str[0][7] =  ((jsonObjectResult.getString("Fair")==null || jsonObjectResult.getString("Fair").equals("null"))?"":jsonObjectResult.getString("Fair"));
                                    str[0][8] =  jsonObjectResult.getString("LoginID")+"\n"+jsonObjectResult.getString("UserName")+"\n"+jsonObjectResult.getString("EmpCVName")+"\n"+jsonObjectResult.getString("EmpCVType");
                                    str[0][9] =  DateFormatsMethods.DaysHoursMinutesCount(jsonObjectResult.getString("EntryDatetime"));

                                    DialogPartyInfoCheck(context,str,1);
                                }
                            }
                        }else{
                            int TotalOverDueAmt = jsonObjectResult.getInt("TotalOverDueAmt");
                            int ExceedAmt = jsonObjectResult.getInt("ExceedAmt");
                            //TODO: Header
                            str[0][0] = "Contact account department,Do you want to approve?";
                            if (TotalOverDueAmt > 0 && ExceedAmt <=0){
                                //TODO:Header
                                str[1][0] =  "Over Due Amount";
                                str[2][0] =  "Total Due Amount";
                                str[3][0] =  "";
                                str[4][0] =  "";
                                str[5][0] =  "";
                                str[6][0] =  "";
                                str[7][0] =  "";
                                str[8][0] =  "";
                                str[9][0] =  "";
                                //TODO:Data
                                str[0][1] =  jsonObjectResult.getString("TotalOverDueAmt");
                                str[0][2] =  jsonObjectResult.getString("TotalDueAmt");
                                str[0][3] =  "";
                                str[0][4] =  "";
                                str[0][5] =  "";
                                str[0][6] =  "";
                                str[0][7] =  "";
                                str[0][8] =  "";
                                str[0][9] =  "";
                                DialogPartyInfoCheck(context,str,0);
                            }else if(ExceedAmt > 0 && TotalOverDueAmt <= 0){
                                //TODO:Header
                                str[1][0] =  "Credit Amount Exceed By";
                                str[2][0] =  "Credit Days";
                                str[3][0] =  "";
                                str[4][0] =  "";
                                str[5][0] =  "";
                                str[6][0] =  "";
                                str[7][0] =  "";
                                str[8][0] =  "";
                                str[9][0] =  "";
                                //TODO:Data
                                str[0][1] =  jsonObjectResult.getString("ExceedAmt");
                                str[0][2] =  jsonObjectResult.getString("CreditDays");
                                str[0][3] =  "";
                                str[0][4] =  "";
                                str[0][5] =  "";
                                str[0][6] =  "";
                                str[0][7] =  "";
                                str[0][8] =  "";
                                str[0][9] =  "";
                                DialogPartyInfoCheck(context,str,0);
                            }else if (TotalOverDueAmt > 0 && ExceedAmt > 0){
                                //TODO:Header
                                str[1][0] =  "Over Due Amount";
                                str[2][0] =  "Total Due Amount";
                                str[3][0] =  "Credit Amount Exceed By";
                                str[4][0] =  "Credit Days";
                                str[5][0] =  "";
                                str[6][0] =  "";
                                str[7][0] =  "";
                                str[8][0] =  "";
                                str[9][0] =  "";
                                //TODO:Data
                                str[0][1] =  jsonObjectResult.getString("TotalOverDueAmt");
                                str[0][2] =  jsonObjectResult.getString("TotalDueAmt");
                                str[0][3] =  jsonObjectResult.getString("ExceedAmt");
                                str[0][4] =  jsonObjectResult.getString("CreditDays");
                                str[0][5] =  "";
                                str[0][6] =  "";
                                str[0][7] =  "";
                                str[0][8] =  "";
                                str[0][9] =  "";
                                DialogPartyInfoCheck(context,str,0);
                            }else {
                                MsgDialogFunction(context);
                            }
                        }
                        //MessageDialog.MessageDialog(context,"",""+response);
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
                params.put("PartyID", PartyID);
                params.put("SubPartyID", SubPartyID);
                params.put("RefName", RefName);
                Log.d(TAG,"Party Info check parameters:"+params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
    }
    private void CallVolleyApprove(final String DeviceID, final String UserID,final String SessionID,final String CompanyID,final String DivisionID,final String BranchID,final String GodownID,final String GodownName,final String FairID,final String PartyID,final String SubPartyID,final String RefName,final String UnderID){
        showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"ApprovedPartyForOrder", new Response.Listener<String>()
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
                        JSONObject jsonObjectJSONObject = jsonObject.getJSONObject("Result");
                        MessageDialog.MessageDialog(context,"Approved",""+jsonObjectJSONObject.getString("Msg"));
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
                params.put("GodownID", GodownID);
                params.put("GodownName", GodownName);
                params.put("FairID", FairID);
                params.put("PartyID", PartyID);
                params.put("SubPartyID", SubPartyID);
                params.put("RefName", RefName);
                params.put("VType", String.valueOf(StaticValues.Vtype));
                params.put("UnderID", UnderID);
                Log.d(TAG,"Approve parameters:"+params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
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
}
