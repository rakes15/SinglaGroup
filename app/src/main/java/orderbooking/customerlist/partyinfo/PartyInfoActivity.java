package orderbooking.customerlist.partyinfo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.singlagroup.AppController;
import com.singlagroup.LoginActivity;
import com.singlagroup.R;
import com.singlagroup.customwidgets.DateFormatsMethods;
import com.singlagroup.customwidgets.FileOpenByIntent;
import com.singlagroup.customwidgets.FiscalDate;
import com.singlagroup.customwidgets.MessageDialog;

import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import DatabaseController.CommanStatic;
import DatabaseController.DatabaseSqlLiteHandlerUserInfo;
import orderbooking.StaticValues;
import orderbooking.customerlist.partyinfo.adapter.CustomAdapter;
import orderbooking.customerlist.partyinfo.adapter.PartyCommonDetailsListAdapter;
import orderbooking.customerlist.partyinfo.adapter.SubPartyCommonDetailsListAdapter;
import orderbooking.customerlist.partyinfo.model.ChildInfo;
import orderbooking.customerlist.partyinfo.model.GroupInfo;
import orderbooking.customerlist.partyinfo.model.PartyCompleteInfo;
import orderbooking.customerlist.partyinfo.model.PartyInfo;
import orderbooking.customerlist.partyinfo.model.SubPartyCompleteInfo;
import retrofit.ApiClient;
import retrofit.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import services.NetworkChangeReceiver;
import services.NetworkListener;
import services.NetworkUtils;

public class PartyInfoActivity extends AppCompatActivity {
    private ActionBar actionBar;
    private LinkedHashMap<String, GroupInfo> subjects = new LinkedHashMap<String, GroupInfo>();
    private ArrayList<GroupInfo> dataList = new ArrayList<GroupInfo>();
    private CustomAdapter listAdapter;
    private ExpandableListView simpleExpandableListView;
    private Context context;
    private String PartyID="",SubPartyID="";
    private int SubPartyApplicable = 0;
    private DatabaseSqlLiteHandlerUserInfo DBHandler;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog progressDialog;
    private static String TAG = PartyInfoActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);} getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_expandable_listview);
        Initialization();
        ModulePermission();
    }
    private void Initialization(){
        try {
            this.context = PartyInfoActivity.this;
            actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            simpleExpandableListView = (ExpandableListView) findViewById(R.id.simpleExpandableListView);
            swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_Refresh_Layout);
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
            this.DBHandler = new DatabaseSqlLiteHandlerUserInfo(context);
        }catch (Exception e){
            MessageDialog.MessageDialog(PartyInfoActivity.this,"Initialization Exception",""+e.toString());
        }
    }
    private void ModulePermission(){
        try {
            PartyID = getIntent().getExtras().getString("PartyID", "");
            SubPartyID = getIntent().getExtras().getString("SubPartyID", "");
            if (PartyID.isEmpty()){
                Intent intent = new Intent(context,PartyInfoSelectCustomerForOrderActivity.class);
                startActivity(intent);
                finish();
            }else{
                Map<String,String> map = DBHandler.getModulePermissionByVtype(7);
                if (!map.isEmpty() || map!=null) {
                    String Name = (map.get("Name") == null ? getResources().getString(R.string.app_name) : map.get("Name"));
                    StaticValues.viewFlag = (map.get("ViewFlag") == null ? 0 : Integer.valueOf(map.get("ViewFlag")));
                    StaticValues.editFlag = (map.get("EditFlag") == null ? 0 : Integer.valueOf(map.get("EditFlag")));
                    StaticValues.createFlag = (map.get("CreateFlag") == null ? 0 : Integer.valueOf(map.get("CreateFlag")));
                    StaticValues.removeFlag = (map.get("RemoveFlag") == null ? 0 : Integer.valueOf(map.get("RemoveFlag")));
                    StaticValues.printFlag = (map.get("PrintFlag") == null ? 0 : Integer.valueOf(map.get("PrintFlag")));
                    StaticValues.importFlag = (map.get("ImportFlag") == null ? 0 : Integer.valueOf(map.get("ImportFlag")));
                    StaticValues.exportFlag = (map.get("ExportFlag") == null ? 0 : Integer.valueOf(map.get("ExportFlag")));
                    StaticValues.Vtype = (map.get("Vtype") == null ? 0 : Integer.valueOf(map.get("Vtype")));

                    final String Title = (SubPartyID.isEmpty() ? Name : "Sub " + Name);
                    actionBar.setTitle(Title);
                    if (StaticValues.viewFlag == 1 || StaticValues.editFlag == 1 || StaticValues.createFlag == 1 || StaticValues.removeFlag == 1 || StaticValues.printFlag == 1 || StaticValues.importFlag == 1 || StaticValues.exportFlag == 1) {
                        CallApiMethod(SubPartyID);
                    } else {
                        MessageDialog.MessageDialog(context, "Alert", "You don't have permission of " + Title + " module");
                    }

                    //TODO: Swipe to refresh
                    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            if (StaticValues.viewFlag == 1 || StaticValues.editFlag == 1 || StaticValues.createFlag == 1 || StaticValues.removeFlag == 1 || StaticValues.printFlag == 1 || StaticValues.importFlag == 1 || StaticValues.exportFlag == 1) {
                                CallApiMethod(SubPartyID);
                            } else {
                                MessageDialog.MessageDialog(context, "Alert", "You don't have permission of " + Title + " module");
                            }
                        }
                    });
                }
            }
        }catch (Exception e){
            MessageDialog.MessageDialog(context,"Exception",e.toString());
        }
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
    private void CallApiMethod(final String SubPartyID){
        //TODO: Book Orders Request
        NetworkChangeReceiver.bindNetworkListener(new NetworkListener() {
            @Override
            public void networkReceived(String status) {
                if (!status.contentEquals("No Internet Connection")) {
                    LoginActivity obj=new LoginActivity();
                    String[] str = obj.GetSharePreferenceSession(context);
                    if (str!=null && !PartyID.isEmpty()) {
                        if (SubPartyID.isEmpty()) {
                            //CallVolleyPartyInfo(str[3], str[0], str[14], str[4], str[5], PartyID);
                            CallRetrofitPartyInfo(str[3], str[0], str[14], str[4], str[5], PartyID);
                        }else{
                            CallRetrofitSubPartyInfo(str[3], str[0], str[14], str[4], str[5], PartyID,SubPartyID);
                        }
                    }else {
                        MessageDialog.MessageDialog(context,"","Something went wrong");
                    }
                } else {
                    MessageDialog.MessageDialog(context,"",status);
                }
            }
        });
        String status = NetworkUtils.getConnectivityStatusString(context);
        if (!status.contentEquals("No Internet Connection")) {
            LoginActivity obj=new LoginActivity();
            String[] str = obj.GetSharePreferenceSession(context);
            if (str!=null && !PartyID.isEmpty()) {
                if (SubPartyID.isEmpty()) {
                    CallRetrofitPartyInfo(str[3], str[0], str[14], str[4], str[5], PartyID);
                    //CallVolleyPartyInfo(str[3], str[0], str[14], str[4], str[5], PartyID);
                }else{
                    CallRetrofitSubPartyInfo(str[3], str[0], str[14], str[4], str[5], PartyID,SubPartyID);
                }
            }else {
                MessageDialog.MessageDialog(context,"","Something went wrong");
            }
        } else {
            MessageDialog.MessageDialog(context,"",status);
        }
    }
    private void CallVolleyPartyInfo(final String DeviceID,final String SessionID,final String CompanyID, final String UserID,final String DivisionID,final String PartyID){
        showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"PartyCompleteInfo", new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                // response
                Log.d("Response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int Status=(jsonObject.getString("status")==null)?0:jsonObject.getInt("status");
                    String Msg=(jsonObject.getString("msg")==null)?"Server is not responding":jsonObject.getString("msg");
                    //dataList = new ArrayList<>();
                    if (Status == 1) {
                        PartyInfo partyInfo=null;
                        JSONObject jsonObjectResult = jsonObject.getJSONObject("Result");

                        JSONObject jsonObjectBasicInfo = jsonObjectResult.optJSONObject("BasicInfo");
//                        JSONArray jaPartyInfo = jsonObjectBasicInfo.optJSONArray("PartyInfo");
//                        if (jaPartyInfo!=null) {
//                            Gson gson = new Gson();
//                            Type type = new TypeToken<List<PartyCompleteInfo.PartyInfo>>(){}.getType();
//                            TypeToken<List<PartyCompleteInfo.PartyInfo>> partyInfos = gson.fromJson(jaPartyInfo.toString(), type);
//                            System.out.println("PartyInfo:"+partyInfos.toString());
//                        }
                        //List<PartyCompleteInfo.ContactInfo> contactInfos = new ObjectMapper().readValue(jsonObjectBasicInfo.optString("ContactInfo"),List.class);
                        JSONArray jaContactInfo = jsonObjectBasicInfo.optJSONArray("ContactInfo");
                        if (jaContactInfo!=null && jaContactInfo.length()>0) {
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<PartyCompleteInfo.ContactInfo>>(){}.getType();
//                            TypeToken<List<PartyCompleteInfo.ContactInfo>> contactInfos = gson.fromJson(jaContactInfo.toString(), type);
//                            System.out.println("Contact Info:"+contactInfos.toString());
                        }else{
                            MessageDialog.MessageDialog(context,"","Empty");
                        }
//                        for (int i=0; i< jaPartyInfo.length(); i++){
//                                    partyInfo = new PartyInfo(jaPartyInfo.getJSONObject(i).getInt("B2BDeviceCount"),jaPartyInfo.getJSONObject(i).getInt("B2BDeviceStatus"),jaPartyInfo.getJSONObject(i).getString("WorkingSince"),jaPartyInfo.getJSONObject(i).getString("PartyID"),jaPartyInfo.getJSONObject(i).getString("PartyName"),jaPartyInfo.getJSONObject(i).getString("agent"),jaPartyInfo.getJSONObject(i).getString("TypeName"),jaPartyInfo.getJSONObject(i).getString("CellNo"),jaPartyInfo.getJSONObject(i).getString("PhoneNo"),jaPartyInfo.getJSONObject(i).getString("Address1"),jaPartyInfo.getJSONObject(i).getString("Address2"),jaPartyInfo.getJSONObject(i).getString("Addres3"),jaPartyInfo.getJSONObject(i).getString("City"),jaPartyInfo.getJSONObject(i).getString("State"),jaPartyInfo.getJSONObject(i).getString("Country"),jaPartyInfo.getJSONObject(i).getString("Pincode"),jaPartyInfo.getJSONObject(i).getInt("SubPartyApplicable"),jaPartyInfo.getJSONObject(i).getString("Remark"));
//                                }
//                                JSONArray jsonArrayContactInfo = jsonObjectBasicInfo.getJSONArray("ContactInfo");
//
//                            JSONArray jsonArrayBasicInfo = jsonObjectResult.getJSONArray("AccountInfo");
//                            JSONObject jsonObjectSalesInfo = jsonObjectResult.getJSONObject("SalesInfo");
//                                JSONArray jsonArraySummary = jsonObjectSalesInfo.getJSONArray("Summary");

                        
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
                params.put("SessionID", SessionID);
                params.put("CompanyID", CompanyID);
                params.put("UserID", UserID);
                params.put("DivisionID", DivisionID);
                params.put("PartyID", PartyID);
                Log.d(TAG,"Party Info parameters:"+params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
    }
    private void CallRetrofitPartyInfo(final String DeviceID,final String SessionID,final String CompanyID, final String UserID,final String DivisionID,final String PartyID){
        showpDialog();
        ApiInterface apiService =  ApiClient.getClient(StaticValues.BASE_URL).create(ApiInterface.class);
        Map<String, String> params = new HashMap<String, String>();
        params.put("DeviceID", DeviceID);
        params.put("SessionID", SessionID);
        params.put("CompanyID", CompanyID);
        params.put("UserID", UserID);
        params.put("DivisionID", DivisionID);
        params.put("PartyID", PartyID);
        Log.d(TAG,"Party Info parameters:"+params.toString());
        Call<PartyCompleteInfo> call = apiService.PartyCompleteInfo(params);
        call.enqueue(new Callback<PartyCompleteInfo>() {
            @Override
            public void onResponse(Call<PartyCompleteInfo> call, retrofit2.Response<PartyCompleteInfo> response) {
                try {
                    if (response.isSuccessful()) {
                        int Status = response.body().getStatus();
                        String msg = response.body().getMsg();
                        subjects = new LinkedHashMap<String, GroupInfo>();
                        dataList = new ArrayList<GroupInfo>();
                        if (Status == 1) {
                            PartyCompleteInfo.Result Result =  response.body().getResult();
                            setPartyInfoRecyclerView(Result);
                        } else {
                            MessageDialog.MessageDialog(context,"",""+msg);
                        }
                    }else{
                        subjects = new LinkedHashMap<String, GroupInfo>();
                        dataList = new ArrayList<GroupInfo>();
                        MessageDialog.MessageDialog(context,"","No Record Found");
                    }
                }catch (Exception e){
                    MessageDialog.MessageDialog(context,"Exception",""+e.toString());
                    Log.e(TAG,"Exception:"+e.toString());
                }
                hidepDialog();
            }

            @Override
            public void onFailure(Call<PartyCompleteInfo> call, Throwable t) {
                MessageDialog.MessageDialog(context,"",t.toString());
                hidepDialog();
            }
        });
    }
    private void CallRetrofitSubPartyInfo(final String DeviceID,final String SessionID,final String CompanyID, final String UserID,final String DivisionID,final String PartyID,final String SubPartyID){
        showpDialog();
        ApiInterface apiService =  ApiClient.getClient(StaticValues.BASE_URL).create(ApiInterface.class);
        Map<String, String> params = new HashMap<String, String>();
        params.put("DeviceID", DeviceID);
        params.put("SessionID", SessionID);
        params.put("CompanyID", CompanyID);
        params.put("UserID", UserID);
        params.put("DivisionID", DivisionID);
        params.put("PartyID", PartyID);
        params.put("SubPartyID", SubPartyID);
        Log.d(TAG,"SubParty Info parameters:"+params.toString());
        Call<SubPartyCompleteInfo> call = apiService.SubPartyCompleteInfo(params);
        call.enqueue(new Callback<SubPartyCompleteInfo>() {
            @Override
            public void onResponse(Call<SubPartyCompleteInfo> call, retrofit2.Response<SubPartyCompleteInfo> response) {
                try {
                    if (response.isSuccessful()) {
                        int Status = response.body().getStatus();
                        String msg = response.body().getMsg();
                        subjects = new LinkedHashMap<String, GroupInfo>();
                        dataList = new ArrayList<GroupInfo>();
                        if (Status == 1) {
                            SubPartyCompleteInfo.Result Result =  response.body().getResult();
                            setSubPartyInfoRecyclerView(Result);
                        } else {
                            MessageDialog.MessageDialog(context,"",""+msg);
                        }
                    }else{
                        subjects = new LinkedHashMap<String, GroupInfo>();
                        dataList = new ArrayList<GroupInfo>();
                        MessageDialog.MessageDialog(context,"","No Record Found");
                    }
                }catch (Exception e){
                    MessageDialog.MessageDialog(context,"Exception",""+e.toString());
                }
                hidepDialog();
            }

            @Override
            public void onFailure(Call<SubPartyCompleteInfo> call, Throwable t) {
                MessageDialog.MessageDialog(context,"",t.toString());
                hidepDialog();
            }
        });
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
    private void setPartyInfoRecyclerView(final PartyCompleteInfo.Result Result){
        Calendar calendar = Calendar.getInstance();
        final PartyCompleteInfo.BasicInfo basicInfo = Result.getBasicInfo();
        //TODO: Basic Info
            List<PartyCompleteInfo.PartyInfo>  partyInfo = basicInfo.getPartyInfo();
            if (!partyInfo.isEmpty()) {
                //TODO: Party Info Header
                addContent("Basic Info", "Party Info");
                //TODO: Party Info Content
                SubPartyApplicable = 0;//partyInfo.get(0).getSubPartyApplicable();
                addContent("Basic Info", "Party Name:" + partyInfo.get(0).getPartyName());
                addContent("Basic Info", "ID Name:" + partyInfo.get(0).getIDName());
                addContent("Basic Info", "Agent:" + partyInfo.get(0).getAgent());
                addContent("Basic Info", "Cell No:" + partyInfo.get(0).getCellNo());
                addContent("Basic Info", "Phone No:" + partyInfo.get(0).getPhoneNo());
                addContent("Basic Info", "Email:" + partyInfo.get(0).getEmail());
                addContent("Basic Info", "Address :" + partyInfo.get(0).getAddress1()+", "+partyInfo.get(0).getAddress2()+", "+partyInfo.get(0).getAddress3());
                addContent("Basic Info", "City,State,Country,Pincode:" + partyInfo.get(0).getCity()+", "+partyInfo.get(0).getState()+", "+partyInfo.get(0).getCountry()+","+partyInfo.get(0).getPincode());
                addContent("Basic Info", "Remark:" + partyInfo.get(0).getRemark());
                addContent("Basic Info", "Type Name:" + partyInfo.get(0).getTypeName());
                if (partyInfo.get(0).getWorkingSince()==null || partyInfo.get(0).getWorkingSince().equals("null") || partyInfo.get(0).getWorkingSince().isEmpty()) {
                    addContent("Basic Info", "Working Since: ");
                }else{
                    String[] str = partyInfo.get(0).getWorkingSince().substring(0, 10).split("-");
                    calendar.set(Integer.valueOf(str[0]), Integer.valueOf(str[1]), Integer.valueOf(str[2]));
                    addContent("Basic Info", "Working Since:" + DateFormatsMethods.getDateDifferenceInDDMMYYYY(calendar.getTime(), new Date()));
                }
                String B2BDevices = "";
                if (partyInfo.get(0).getB2BDeviceCount()==0){
                    if (partyInfo.get(0).getB2BDeviceCountPending()>0 && partyInfo.get(0).getB2BDeviceRejectCount()>0){
                        B2BDevices = partyInfo.get(0).getB2BDeviceCountPending()+" Pending"+","+partyInfo.get(0).getB2BDeviceRejectCount()+" Rejected";
                    }else if (partyInfo.get(0).getB2BDeviceCountPending()>0){
                        B2BDevices = partyInfo.get(0).getB2BDeviceCountPending()+" Pending";//(partyInfo.get(0).getB2BDeviceCountPending()==1 ? "1 Pending" : partyInfo.get(0).getB2BDeviceCountPending()+" Pending");
                    }else if (partyInfo.get(0).getB2BDeviceRejectCount()>0){
                        B2BDevices = partyInfo.get(0).getB2BDeviceRejectCount()+" Rejected";//(partyInfo.get(0).getB2BDeviceRejectCount()==1 ? "1 Rejected" : partyInfo.get(0).getB2BDeviceRejectCount()+" Rejected");
                    }else {
                        B2BDevices = "No Device found";
                    }
                }else if (partyInfo.get(0).getB2BDeviceCount()==1){
                    if (partyInfo.get(0).getB2BDeviceCountPending()>0 && partyInfo.get(0).getB2BDeviceRejectCount()>0){
                        B2BDevices = partyInfo.get(0).getB2BDeviceCount()+" Active"+","+partyInfo.get(0).getB2BDeviceCountPending()+" Pending"+","+partyInfo.get(0).getB2BDeviceRejectCount()+" Rejected";
                    }else if (partyInfo.get(0).getB2BDeviceCountPending()>0){
                        B2BDevices = partyInfo.get(0).getB2BDeviceCount()+" Active"+","+partyInfo.get(0).getB2BDeviceCountPending()+" Pending";//(partyInfo.get(0).getB2BDeviceCountPending()==1 ? partyInfo.get(0).getB2BDeviceCount()+" Active"+",1 Pending" : partyInfo.get(0).getB2BDeviceCountPending()+" Devices are Pending");
                    }else if (partyInfo.get(0).getB2BDeviceRejectCount()>0){
                        B2BDevices = partyInfo.get(0).getB2BDeviceCount()+" Active"+","+partyInfo.get(0).getB2BDeviceCountPending()+" Rejected";//(partyInfo.get(0).getB2BDeviceRejectCount()==1 ? "1 Device is Rejected" : partyInfo.get(0).getB2BDeviceRejectCount()+" Devices are Rejected");
                    }else {
                        B2BDevices = partyInfo.get(0).getB2BDeviceCount()+" Active";
                    }
                }else if (partyInfo.get(0).getB2BDeviceCount()>1){
                    if (partyInfo.get(0).getB2BDeviceCountPending()>0 && partyInfo.get(0).getB2BDeviceRejectCount()>0){
                        B2BDevices = partyInfo.get(0).getB2BDeviceCount()+" Active"+","+partyInfo.get(0).getB2BDeviceCountPending()+" Pending"+","+partyInfo.get(0).getB2BDeviceRejectCount()+" Rejected";
                    }else if (partyInfo.get(0).getB2BDeviceCountPending()>0){
                        B2BDevices = partyInfo.get(0).getB2BDeviceCount()+" Active"+","+partyInfo.get(0).getB2BDeviceCountPending()+" Pending";//(partyInfo.get(0).getB2BDeviceCountPending()==1 ? partyInfo.get(0).getB2BDeviceCount()+" Active"+",1 Pending" : partyInfo.get(0).getB2BDeviceCountPending()+" Devices are Pending");
                    }else if (partyInfo.get(0).getB2BDeviceRejectCount()>0){
                        B2BDevices = partyInfo.get(0).getB2BDeviceCount()+" Active"+","+partyInfo.get(0).getB2BDeviceCountPending()+" Rejected";//(partyInfo.get(0).getB2BDeviceRejectCount()==1 ? "1 Device is Rejected" : partyInfo.get(0).getB2BDeviceRejectCount()+" Devices are Rejected");
                    }else {
                        B2BDevices = partyInfo.get(0).getB2BDeviceCount()+" Active";
                    }
                }
                addContent("Basic Info", "B2B Devices:" + B2BDevices);
                //addContent("Basic Info", "B2B Device Status:" + partyInfo.get(0).getB2BDeviceStatus());
            }
            final List<PartyCompleteInfo.ContactInfo>  contactInfo = basicInfo.getContactInfo();
            if (!contactInfo.isEmpty()) {
                //TODO: Contact Info Header
                addContent("Basic Info", "Contact Info");
                //TODO: Contact Info Content
                addContent("Basic Info", "Primary Contact:" + (contactInfo.get(0).getIsDefault()==0 ? "No" : "Yes"));
                addContent("Basic Info", "Name:" + contactInfo.get(0).getName());
                addContent("Basic Info", "Designation:" + contactInfo.get(0).getDesignation());
                addContent("Basic Info", "Cell No:" + contactInfo.get(0).getCellNo());
                addContent("Basic Info", "Phone No:" + contactInfo.get(0).getPhoneNo());
                addContent("Basic Info", "Email:" + contactInfo.get(0).getEmail());
                addContent("Basic Info", "Address :" + contactInfo.get(0).getAddress1()+", " + contactInfo.get(0).getAddress2()+", " + contactInfo.get(0).getAddress3());
                addContent("Basic Info","$More Contacts");
            }
        //TODO: Account Info
        List<PartyCompleteInfo.AccountInfo>  accountInfos = Result.getAccountInfo();
        if (!accountInfos.isEmpty()) {
            Calendar calendar1 = Calendar.getInstance();
            calendar1.get(Calendar.YEAR);
            //TODO: Account Info Content
            addContent("Account Info", "Credit Limit:₹" + accountInfos.get(0).getCreditLimit());
            addContent("Account Info", "Credit Days:" + accountInfos.get(0).getCreditDays());
            addContent("Account Info", "Discount Policy:" + accountInfos.get(0).getDiscountPolicy());
            addContent("Account Info", "Over Due Amount:₹" + accountInfos.get(0).getOverDueAmount());
            addContent("Account Info", "Avg Over Due Days:" + accountInfos.get(0).getAvgOverDueDays());
            addContent("Account Info", "DueAmount:₹" + accountInfos.get(0).getDueAmount());
            addContent("Account Info", "Avg Due Days:" + accountInfos.get(0).getAvgDueDays());
            addContent("Account Info", "Avg Pay Days ("+ FiscalDate.getFinancialYear(calendar1)+"):" + accountInfos.get(0).getFncrAvgDays0());
            addContent("Account Info", "Avg Pay Days ("+ FiscalDate.getPreviousFinancialYear(calendar1)+"):" + accountInfos.get(0).getFncrAvgDays1());
            addContent("Account Info", "Avg Pay Days ("+ FiscalDate.getPreviuosToPreviuosFinancialYear(calendar1)+"):" + accountInfos.get(0).getFncrAvgDays2());
            addContent("Account Info", "Discount ("+ FiscalDate.getFinancialYear(calendar1)+"):" + accountInfos.get(0).getDiscountFnYr0() + "%");
            addContent("Account Info", "Discount ("+ FiscalDate.getPreviousFinancialYear(calendar1)+"):" + accountInfos.get(0).getDiscountFnYr1() + "%");
            addContent("Account Info", "Discount ("+ FiscalDate.getPreviuosToPreviuosFinancialYear(calendar1)+"):" + accountInfos.get(0).getDiscountFnYr2() + "%");

            //TODO: Avg Payment Days
            PartyCommonDetailsListAdapter.mapAvgPayDays = new HashMap<String, String>();
            PartyCommonDetailsListAdapter.mapAvgPayDays.put("Name", "Avg Payment Days");
            PartyCommonDetailsListAdapter.mapAvgPayDays.put("AvgPayDays0", String.valueOf(accountInfos.get(0).getFncrAvgDays0()));
            PartyCommonDetailsListAdapter.mapAvgPayDays.put("AvgPayDays1", String.valueOf(accountInfos.get(0).getFncrAvgDays1()));
            PartyCommonDetailsListAdapter.mapAvgPayDays.put("AvgPayDays2", String.valueOf(accountInfos.get(0).getFncrAvgDays2()));
            //TODO: Discount
            PartyCommonDetailsListAdapter.mapDiscount = new HashMap<String, String>();
            PartyCommonDetailsListAdapter.mapDiscount.put("Name", "Discount");
            PartyCommonDetailsListAdapter.mapDiscount.put("Discount0", String.valueOf(accountInfos.get(0).getDiscountFnYr0()+ "%"));
            PartyCommonDetailsListAdapter.mapDiscount.put("Discount1", String.valueOf(accountInfos.get(0).getDiscountFnYr1()+ "%"));
            PartyCommonDetailsListAdapter.mapDiscount.put("Discount2", String.valueOf(accountInfos.get(0).getDiscountFnYr2()+ "%"));
        }
        //TODO: Sales Info
        final PartyCompleteInfo.SalesInfo  salesInfo = Result.getSalesInfo();
//            if (salesInfo != null) {
//                //TODO: Summary Header
//                List<PartyCompleteInfo.Summary> summary = salesInfo.getSummary();
//                if (!summary.isEmpty()) {
//                    addContent("Sales Info", "Summary");
//                    //TODO: Summary Content
//                    addContent("Sales Info", "Sales Share 1:" + summary.get(0).getSalesShare0());
//                    addContent("Sales Info", "Sales Share 2:" + summary.get(0).getSalesShare1());
//                    addContent("Sales Info", "Sales Share 3:" + summary.get(0).getSalesShare2());
//                    addContent("Sales Info", "Party Avg Invoice 1:" + summary.get(0).getPartyAvgInv0());
//                    addContent("Sales Info", "Party Avg Invoice 2:" + summary.get(0).getPartyAvgInv1());
//                    addContent("Sales Info", "Party Avg Invoice 3:" + summary.get(0).getPartyAvgInv2());
//                    addContent("Sales Info", "No Of Invoice 1:" + summary.get(0).getNoOfInv0());
//                    addContent("Sales Info", "No Of Invoice 2:" + summary.get(0).getNoOfInv1());
//                    addContent("Sales Info", "No Of Invoice 3:" + summary.get(0).getNoOfInv2());
//                    addContent("Sales Info", "Sale Return 1:" + summary.get(0).getSaleReturn0());
//                    addContent("Sales Info", "Sale Return 2:" + summary.get(0).getSaleReturn1());
//                    addContent("Sales Info", "Sale Return 3:" + summary.get(0).getSaleReturn2());
//                }
//            }
        //TODO: GroupWise Header
        //final List<PartyCompleteInfo.GroupWise>  GroupWise = salesInfo.getGroupWise();
        if (salesInfo.getSummary()!=null && !salesInfo.getSummary().isEmpty()) {
            addContent("Sales Info", "#Summary");
            //TODO: GroupWise Header
            //final List<PartyCompleteInfo.GroupWise>  GroupWise = salesInfo.getGroupWise();
            addContent("Sales Info", "#Group Wise Sales");
            //TODO: PartyMonthWiseSales Header
            //List<PartyCompleteInfo.PartyMonthWiseSale>  PartyMonthWiseSales = salesInfo.getPartyMonthWiseSales();
            addContent("Sales Info", "#Party Month Wise Sales");
            //TODO: ShowroomWiseSale Header
            //final List<PartyCompleteInfo.ShowroomWiseSale>  ShowroomWiseSale = salesInfo.getShowroomWiseSales();
            addContent("Sales Info", "#Showroom Wise Sales");
            //TODO: ApplicationWiseSale Header
            //List<PartyCompleteInfo.ApplicationWiseSale>  ApplicationWiseSale = salesInfo.getApplicationWiseSales();
            addContent("Sales Info", "#Application Wise Sales");
            //TODO: FairDeliveryParcent Header
            //List<PartyCompleteInfo.FairDeliveryParcent>  FairDeliveryParcent = salesInfo.getFairDeliveryParcent();
            addContent("Sales Info", "#Fair Delivery Percent");
        }
        //TODO: Pending Order Info
        List<PartyCompleteInfo.PendingOrderInfo>  pendingOrderInfos = Result.getPendingOrderInfo();
        if (!pendingOrderInfos.isEmpty()) {
            addContent("Pending Order Info", "$Pending Orders");
        }
        //TODO: Party Attechment
        List<PartyCompleteInfo.PartyAttechment>  partyAttechment = Result.getPartyAttechment();
        if (!partyAttechment.isEmpty()) {
            for (int i=0; i<partyAttechment.size(); i++) {
                addContent("Party Attachment", "Attached File "+(i+1)+" :File Type - "+ FileOpenByIntent.FileType(partyAttechment.get(i).getFileName())+"\nDescription - "+partyAttechment.get(i).getDescription()+":" + partyAttechment.get(i).getFilepath());
            }
        }
        listAdapter = new CustomAdapter(context, dataList);
        // attach the adapter to the expandable list view
        simpleExpandableListView.setAdapter(listAdapter);
        simpleExpandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousItem = -1;
            @Override
            public void onGroupExpand(int groupPosition) {
                if(groupPosition != previousItem )
                    simpleExpandableListView.collapseGroup(previousItem );
                previousItem = groupPosition;
            }
        });
        simpleExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                //get the group header
                GroupInfo headerInfo = dataList.get(groupPosition);
                //get the child info
                ChildInfo detailInfo =  headerInfo.getProductList().get(childPosition);
                //display it or do something with it
                if (detailInfo.getName().contains("#")){
                    Bundle args = new Bundle();
                    args.putString("Title",detailInfo.getName().substring(1));
                    args.putSerializable("Content",(Serializable)salesInfo);
                    args.putInt("SubPartyApplicable",SubPartyApplicable);

                    Intent intent = new Intent(context,CommonDetailsListActivity.class);
                    intent.putExtra("Bundle",args);
                    startActivity(intent);
                }else if (detailInfo.getName().contains("$")){
                    Bundle args = new Bundle();
                    args.putString("Title",detailInfo.getName().substring(1));
                    args.putSerializable("Content",(Serializable)Result);
                    args.putInt("SubPartyApplicable",SubPartyApplicable);

                    Intent intent = new Intent(context,MoreContactInfoActivity.class);
                    intent.putExtra("Bundle",args);
                    startActivity(intent);
                }
                return false;
            }
        });
        simpleExpandableListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                swipeRefreshLayout.setEnabled(firstVisibleItem == 0);
            }
        });
    }
    private void setSubPartyInfoRecyclerView(final SubPartyCompleteInfo.Result Result){
        Calendar calendar = Calendar.getInstance();
        final SubPartyCompleteInfo.BasicInfo basicInfo = Result.getBasicInfo();
        //TODO: Basic Info
        List<SubPartyCompleteInfo.PartyInfo>  partyInfo = basicInfo.getPartyInfo();
        if (!partyInfo.isEmpty()) {
            //TODO: Party Info Header
            addContent("Basic Info", "Party Info");
            //TODO: Party Info Content
            SubPartyApplicable = partyInfo.get(0).getSubPartyApplicable();
            addContent("Basic Info", "Party Name:" + partyInfo.get(0).getPartyName());
            addContent("Basic Info", "Sub Party Name:" + partyInfo.get(0).getSubPartyName());
            addContent("Basic Info", "ID Name:" + partyInfo.get(0).getIDName());
            addContent("Basic Info", "Agent:" + partyInfo.get(0).getAgent());
            addContent("Basic Info", "Cell No:" + partyInfo.get(0).getCellNo());
            addContent("Basic Info", "Phone No:" + partyInfo.get(0).getPhoneNo());
            addContent("Basic Info", "Email:" + partyInfo.get(0).getEmail());
            addContent("Basic Info", "Address :" + partyInfo.get(0).getAddress1()+", "+partyInfo.get(0).getAddress2()+", "+partyInfo.get(0).getAddress3());
            addContent("Basic Info", "City,State,Country,Pincode:" + partyInfo.get(0).getCity()+", "+partyInfo.get(0).getState()+", "+partyInfo.get(0).getCountry()+","+partyInfo.get(0).getPincode());
            addContent("Basic Info", "Remark:" + partyInfo.get(0).getRemark());
            addContent("Basic Info", "Type Name:" + partyInfo.get(0).getTypeName());
            if (partyInfo.get(0).getWorkingSince()==null || partyInfo.get(0).getWorkingSince().equals("null") || partyInfo.get(0).getWorkingSince().isEmpty()) {
                addContent("Basic Info", "Working Since: ");
            }else{
                String[] str = partyInfo.get(0).getWorkingSince().substring(0, 10).split("-");
                calendar.set(Integer.valueOf(str[0]), Integer.valueOf(str[1]), Integer.valueOf(str[2]));
                addContent("Basic Info", "Working Since:" + DateFormatsMethods.getDateDifferenceInDDMMYYYY(calendar.getTime(), new Date()));
            }
            String B2BDevices = "";
            if (partyInfo.get(0).getB2BDeviceCount()==0){
                if (partyInfo.get(0).getB2BDeviceCountPending()>0 && partyInfo.get(0).getB2BDeviceRejectCount()>0){
                    B2BDevices = partyInfo.get(0).getB2BDeviceCountPending()+" Pending"+","+partyInfo.get(0).getB2BDeviceRejectCount()+" Rejected";
                }else if (partyInfo.get(0).getB2BDeviceCountPending()>0){
                    B2BDevices = partyInfo.get(0).getB2BDeviceCountPending()+" Pending";//(partyInfo.get(0).getB2BDeviceCountPending()==1 ? "1 Pending" : partyInfo.get(0).getB2BDeviceCountPending()+" Pending");
                }else if (partyInfo.get(0).getB2BDeviceRejectCount()>0){
                    B2BDevices = partyInfo.get(0).getB2BDeviceRejectCount()+" Rejected";//(partyInfo.get(0).getB2BDeviceRejectCount()==1 ? "1 Rejected" : partyInfo.get(0).getB2BDeviceRejectCount()+" Rejected");
                }else {
                    B2BDevices = "No Device found";
                }
            }else if (partyInfo.get(0).getB2BDeviceCount()==1){
                if (partyInfo.get(0).getB2BDeviceCountPending()>0 && partyInfo.get(0).getB2BDeviceRejectCount()>0){
                    B2BDevices = partyInfo.get(0).getB2BDeviceCount()+" Active"+","+partyInfo.get(0).getB2BDeviceCountPending()+" Pending"+","+partyInfo.get(0).getB2BDeviceRejectCount()+" Rejected";
                }else if (partyInfo.get(0).getB2BDeviceCountPending()>0){
                    B2BDevices = partyInfo.get(0).getB2BDeviceCount()+" Active"+","+partyInfo.get(0).getB2BDeviceCountPending()+" Pending";//(partyInfo.get(0).getB2BDeviceCountPending()==1 ? partyInfo.get(0).getB2BDeviceCount()+" Active"+",1 Pending" : partyInfo.get(0).getB2BDeviceCountPending()+" Devices are Pending");
                }else if (partyInfo.get(0).getB2BDeviceRejectCount()>0){
                    B2BDevices = partyInfo.get(0).getB2BDeviceCount()+" Active"+","+partyInfo.get(0).getB2BDeviceCountPending()+" Rejected";//(partyInfo.get(0).getB2BDeviceRejectCount()==1 ? "1 Device is Rejected" : partyInfo.get(0).getB2BDeviceRejectCount()+" Devices are Rejected");
                }else {
                    B2BDevices = partyInfo.get(0).getB2BDeviceCount()+" Active";
                }
            }else if (partyInfo.get(0).getB2BDeviceCount()>1){
                if (partyInfo.get(0).getB2BDeviceCountPending()>0 && partyInfo.get(0).getB2BDeviceRejectCount()>0){
                    B2BDevices = partyInfo.get(0).getB2BDeviceCount()+" Active"+","+partyInfo.get(0).getB2BDeviceCountPending()+" Pending"+","+partyInfo.get(0).getB2BDeviceRejectCount()+" Rejected";
                }else if (partyInfo.get(0).getB2BDeviceCountPending()>0){
                    B2BDevices = partyInfo.get(0).getB2BDeviceCount()+" Active"+","+partyInfo.get(0).getB2BDeviceCountPending()+" Pending";//(partyInfo.get(0).getB2BDeviceCountPending()==1 ? partyInfo.get(0).getB2BDeviceCount()+" Active"+",1 Pending" : partyInfo.get(0).getB2BDeviceCountPending()+" Devices are Pending");
                }else if (partyInfo.get(0).getB2BDeviceRejectCount()>0){
                    B2BDevices = partyInfo.get(0).getB2BDeviceCount()+" Active"+","+partyInfo.get(0).getB2BDeviceCountPending()+" Rejected";//(partyInfo.get(0).getB2BDeviceRejectCount()==1 ? "1 Device is Rejected" : partyInfo.get(0).getB2BDeviceRejectCount()+" Devices are Rejected");
                }else {
                    B2BDevices = partyInfo.get(0).getB2BDeviceCount()+" Active";
                }
            }
            addContent("Basic Info", "B2B Devices:" + B2BDevices);
        }
        //TODO: Account Info
        List<SubPartyCompleteInfo.ContactInfo>  contactInfo = basicInfo.getContactInfo();
        if (!contactInfo.isEmpty()) {
            //TODO: Contact Info Header
            addContent("Basic Info", "Contact Info");
            //TODO: Contact Info Content
            addContent("Basic Info", "Primary Contact:" + (contactInfo.get(0).getIsDefault()==0 ? "No" : "Yes"));
            addContent("Basic Info", "Name:" + contactInfo.get(0).getName());
            addContent("Basic Info", "Designation:" + contactInfo.get(0).getDesignation());
            addContent("Basic Info", "Cell No:" + contactInfo.get(0).getCellNo());
            addContent("Basic Info", "Phone No:" + contactInfo.get(0).getPhoneNo());
            addContent("Basic Info", "Email:" + contactInfo.get(0).getEmail());
            addContent("Basic Info", "Address :" + contactInfo.get(0).getAddress1()+", " + contactInfo.get(0).getAddress2()+", " + contactInfo.get(0).getAddress3());
            addContent("Basic Info","$More Contacts");
        }
        //TODO: Account Info
        List<SubPartyCompleteInfo.AccountInfo>  accountInfos = Result.getAccountInfo();
        if (!accountInfos.isEmpty()) {
            Calendar calendar1 = Calendar.getInstance();
            calendar1.get(Calendar.YEAR);
            //TODO: Account Info Content
            addContent("Account Info", "Credit Limit:₹" + accountInfos.get(0).getCreditLimit());
            addContent("Account Info", "Credit Days:" + accountInfos.get(0).getCreditDays());
            addContent("Account Info", "Discount Policy:" + accountInfos.get(0).getDiscountPolicy());
            addContent("Account Info", "Over Due Amount:₹" + accountInfos.get(0).getOverDueAmount());
            addContent("Account Info", "Avg Over Due Days:" + accountInfos.get(0).getAvgOverDueDays());
            addContent("Account Info", "DueAmount:₹" + accountInfos.get(0).getDueAmount());
            addContent("Account Info", "Avg Due Days:" + accountInfos.get(0).getAvgDueDays());
            addContent("Account Info", "Avg Pay Days ("+ FiscalDate.getFinancialYear(calendar1)+"):" + accountInfos.get(0).getFncrAvgDays0());
            addContent("Account Info", "Avg Pay Days ("+ FiscalDate.getPreviousFinancialYear(calendar1)+"):" + accountInfos.get(0).getFncrAvgDays1());
            addContent("Account Info", "Avg Pay Days ("+ FiscalDate.getPreviuosToPreviuosFinancialYear(calendar1)+"):" + accountInfos.get(0).getFncrAvgDays2());
            addContent("Account Info", "Discount ("+ FiscalDate.getFinancialYear(calendar1)+"):" + accountInfos.get(0).getDiscountFnYr0() + "%");
            addContent("Account Info", "Discount ("+ FiscalDate.getPreviousFinancialYear(calendar1)+"):" + accountInfos.get(0).getDiscountFnYr1() + "%");
            addContent("Account Info", "Discount ("+ FiscalDate.getPreviuosToPreviuosFinancialYear(calendar1)+"):" + accountInfos.get(0).getDiscountFnYr2() + "%");

            //TODO: Avg Payment Days
            SubPartyCommonDetailsListAdapter.mapAvgPayDays = new HashMap<String, String>();
            SubPartyCommonDetailsListAdapter.mapAvgPayDays.put("Name", "Avg Payment Days");
            SubPartyCommonDetailsListAdapter.mapAvgPayDays.put("AvgPayDays0", String.valueOf(accountInfos.get(0).getFncrAvgDays0()));
            SubPartyCommonDetailsListAdapter.mapAvgPayDays.put("AvgPayDays1", String.valueOf(accountInfos.get(0).getFncrAvgDays1()));
            SubPartyCommonDetailsListAdapter.mapAvgPayDays.put("AvgPayDays2", String.valueOf(accountInfos.get(0).getFncrAvgDays2()));
            //TODO: Discount
            SubPartyCommonDetailsListAdapter.mapDiscount = new HashMap<String, String>();
            SubPartyCommonDetailsListAdapter.mapDiscount.put("Name", "Discount");
            SubPartyCommonDetailsListAdapter.mapDiscount.put("Discount0", String.valueOf(accountInfos.get(0).getDiscountFnYr0()+ "%"));
            SubPartyCommonDetailsListAdapter.mapDiscount.put("Discount1", String.valueOf(accountInfos.get(0).getDiscountFnYr1()+ "%"));
            SubPartyCommonDetailsListAdapter.mapDiscount.put("Discount2", String.valueOf(accountInfos.get(0).getDiscountFnYr2()+ "%"));
        }
        //TODO: Sales Info
        final SubPartyCompleteInfo.SalesInfo  salesInfo = Result.getSalesInfo();
        if (salesInfo.getSummary()!=null && !salesInfo.getSummary().isEmpty()) {
            addContent("Sales Info", "#Summary");
            //TODO: GroupWise Header
            //final List<PartyCompleteInfo.GroupWise>  GroupWise = salesInfo.getGroupWise();
            addContent("Sales Info", "#Group Wise Sales");
            //TODO: PartyMonthWiseSales Header
            //List<PartyCompleteInfo.PartyMonthWiseSale>  PartyMonthWiseSales = salesInfo.getPartyMonthWiseSales();
            addContent("Sales Info", "#Party Month Wise Sales");
            //TODO: ShowroomWiseSale Header
            //final List<PartyCompleteInfo.ShowroomWiseSale>  ShowroomWiseSale = salesInfo.getShowroomWiseSales();
            addContent("Sales Info", "#Showroom Wise Sales");
            //TODO: ApplicationWiseSale Header
            //List<PartyCompleteInfo.ApplicationWiseSale>  ApplicationWiseSale = salesInfo.getApplicationWiseSales();
            addContent("Sales Info", "#Application Wise Sales");
            //TODO: FairDeliveryParcent Header
            //List<PartyCompleteInfo.FairDeliveryParcent>  FairDeliveryParcent = salesInfo.getFairDeliveryParcent();
            addContent("Sales Info", "#Fair Delivery Percent");
        }
        //TODO: Pending Order Info
        List<SubPartyCompleteInfo.PendingOrderInfo>  pendingOrderInfos = Result.getPendingOrderInfo();
        if (!pendingOrderInfos.isEmpty()) {
            addContent("Pending Order Info", "$Pending Orders");
        }
        //TODO: Party Attechment
        List<SubPartyCompleteInfo.PartyAttechment>  partyAttechment = Result.getPartyAttechment();
        if (!partyAttechment.isEmpty()) {
            for (int i=0; i<partyAttechment.size(); i++) {
                addContent("Party Attachment", "Attached File "+(i+1)+" :File Type - "+ FileOpenByIntent.FileType(partyAttechment.get(i).getFileName())+"\nDescription - "+partyAttechment.get(i).getDescription()+":" + partyAttechment.get(i).getFilepath());
            }
        }
        listAdapter = new CustomAdapter(context, dataList);
        // attach the adapter to the expandable list view
        simpleExpandableListView.setAdapter(listAdapter);
        simpleExpandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousItem = -1;
            @Override
            public void onGroupExpand(int groupPosition) {
                if(groupPosition != previousItem )
                    simpleExpandableListView.collapseGroup(previousItem );
                previousItem = groupPosition;
            }
        });
        simpleExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                //get the group header
                GroupInfo headerInfo = dataList.get(groupPosition);
                //get the child info
                ChildInfo detailInfo =  headerInfo.getProductList().get(childPosition);
                //display it or do something with it
                if (detailInfo.getName().contains("#")){
                    Bundle args = new Bundle();
                    args.putString("Title",detailInfo.getName().substring(1));
                    args.putSerializable("Content",(Serializable)salesInfo);
                    args.putInt("SubPartyApplicable",SubPartyApplicable);

                    Intent intent = new Intent(context,CommonDetailsListActivity.class);
                    intent.putExtra("Bundle",args);
                    startActivity(intent);
                }else if (detailInfo.getName().contains("$")){
                    Bundle args = new Bundle();
                    args.putString("Title",detailInfo.getName().substring(1));
                    args.putSerializable("Content",(Serializable)Result);
                    args.putInt("SubPartyApplicable",SubPartyApplicable);

                    Intent intent = new Intent(context,MoreContactInfoActivity.class);
                    intent.putExtra("Bundle",args);
                    startActivity(intent);
                }
                return false;
            }
        });
        simpleExpandableListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                swipeRefreshLayout.setEnabled(firstVisibleItem == 0);
            }
        });
    }
    //here we maintain our Contents in various Headers
    private int addContent(String Header, String Content){

        int groupPosition = 0;

        //check the hash map if the group already exists
        GroupInfo headerInfo = subjects.get(Header);
        //add the group if doesn't exists
        if(headerInfo == null){
            headerInfo = new GroupInfo();
            headerInfo.setName(Header);
            subjects.put(Header, headerInfo);
            dataList.add(headerInfo);
        }

        //get the children for the group
        ArrayList<ChildInfo> ContentList = headerInfo.getProductList();
        //size of the children list
        int listSize = ContentList.size();
        //add to the counter
        listSize++;

        //create a new child and add that to the group
        ChildInfo detailInfo = new ChildInfo();
        detailInfo.setSequence(String.valueOf(listSize));
        detailInfo.setName(Content);
        ContentList.add(detailInfo);
        headerInfo.setProductList(ContentList);

        //find the group position inside the list
        groupPosition = dataList.indexOf(headerInfo);
        return groupPosition;
    }
}
