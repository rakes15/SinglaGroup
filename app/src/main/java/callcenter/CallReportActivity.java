package callcenter;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import com.singlagroup.adapters.CommonSearchSpinnerFilterableAdapter;
import com.singlagroup.adapters.DivisionFilterableAdapter;
import com.singlagroup.customwidgets.CommonSearchableSpinner;
import com.singlagroup.customwidgets.DateFormatsMethods;
import com.singlagroup.customwidgets.ItemClickSupport;
import com.singlagroup.customwidgets.MessageDialog;
import com.singlagroup.customwidgets.MultiSelectionSpinner;
import com.singlagroup.datasets.DivisionDataset;

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
import DatabaseController.DatabaseSqlLiteHandlerUserInfo;
import callcenter.adapter.IncomingCallsAdapter;
import callcenter.model.IncomingCalls;
import orderbooking.StaticValues;
import retrofit.ApiClient;
import retrofit.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import services.NetworkUtils;
import uploadimagesfiles.documentattachment.DocumentAttachmentUploadActivity;
import uploadimagesfiles.documentattachment.adapter.RecyclerViewDocumentAdapter;
import uploadimagesfiles.documentattachment.datasets.Result;
import uploadimagesfiles.documentattachment.responsedatasets.ResponseDocumentDataset;

/**
 * Created by Rakesh on 14-Jan-17.
 */
public class CallReportActivity extends AppCompatActivity {
    private static String TAG = CallReportActivity.class.getSimpleName();
    private Context context;
    private RecyclerView recyclerView;
    private Dialog dialog;
    private ProgressDialog progressDialog;
    private ActionBar actionBar;
    private CommonSearchSpinnerFilterableAdapter commonAdapter,commonAdapterSubParty;
    private IncomingCallsAdapter adapter;
    private String DivisionID="",Division="",PartyID="",PartyName="",SubPartyID="",SubPartyName="",AgentID="",CallStatus="";
    private int HandOverType=0;
    private String Title="";
    ArrayList<Map<String,String>> PartyList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);} getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_recyclerview);
        Initialization();
        ModulePermission();
    }
    private void Initialization(){
        try {
            this.context = CallReportActivity.this;
            actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
        }catch (Exception e){
            MessageDialog.MessageDialog(this,"Initialization",""+e.toString());
        }
    }
    private void ModulePermission(){
        try {
            Bundle bundle = getIntent().getBundleExtra("PermissionBundle");
            Title = bundle.getString("Title");
            StaticValues.viewFlag = bundle.getInt("ViewFlag");
            StaticValues.editFlag = bundle.getInt("EditFlag");
            StaticValues.createFlag = bundle.getInt("CreateFlag");
            StaticValues.removeFlag = bundle.getInt("RemoveFlag");
            StaticValues.printFlag = bundle.getInt("PrintFlag");
            StaticValues.importFlag = bundle.getInt("ImportFlag");
            StaticValues.exportFlag = bundle.getInt("ExportFlag");
            actionBar.setTitle(Title);
            if (StaticValues.viewFlag == 1 || StaticValues.editFlag == 1 || StaticValues.createFlag == 1 || StaticValues.removeFlag == 1 || StaticValues.printFlag == 1 || StaticValues.importFlag == 1 || StaticValues.exportFlag == 1){
                DialogSearchFilter(context);
            }else {
                MessageDialog.MessageDialog(CallReportActivity.this,"Alert","You don't have permission of "+Title+" module");
            }
        }catch (Exception e){
            MessageDialog.MessageDialog(CallReportActivity.this,"Exception",e.toString());
        }
    }
    private void LoadRecyclerView(List<IncomingCalls> incomingCallsList){
        adapter = new IncomingCallsAdapter(context, incomingCallsList);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
//        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
//            @Override
//            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
//                IncomingCalls incomingCalls = (IncomingCalls) adapter.getItem(position);
//            }
//        });
    }
    private void DialogSearchFilter(final Context context){
        dialog = new Dialog(new ContextThemeWrapper(context, R.style.FullHeightDialog));
        dialog.setContentView(R.layout.dialog_call_report_search_filter);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.TOP;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        //TODO: Declarations
        //TODO: EditTexts
        final EditText edtFromDate = (EditText) dialog.findViewById(R.id.EditText_FromDate);
        final EditText edtToDate = (EditText) dialog.findViewById(R.id.EditText_ToDate);
        //final EditText edtAgentName = (EditText) dialog.findViewById(R.id.editText_Agent_Name);
        //TODO: TextViews
        final TextView tVParty = (TextView) dialog.findViewById(R.id.text_view_party);
        final TextView tVSubParty = (TextView) dialog.findViewById(R.id.text_view_sub_party);
        //TODO: Spinners
        final MultiSelectionSpinner msSpnCallStatus = (MultiSelectionSpinner) dialog.findViewById(R.id.Spinner_Multi_Selection_Call_Status);
        final Spinner spnAgentList = (Spinner) dialog.findViewById(R.id.Spinner_Agent_list);
        final CommonSearchableSpinner spnPartyList = (CommonSearchableSpinner) dialog.findViewById(R.id.spinner_Party_List);
        final CommonSearchableSpinner spnSubPartyList = (CommonSearchableSpinner) dialog.findViewById(R.id.spinner_Sub_Party_List);
        //TODO: Progress Bars
        final ProgressBar pBcallStatus = (ProgressBar) dialog.findViewById(R.id.ProgressBar_call_status);
        final ProgressBar pBparty = (ProgressBar) dialog.findViewById(R.id.ProgressBar_party);
        final ProgressBar pBsubParty = (ProgressBar) dialog.findViewById(R.id.ProgressBar_sub_party);
        //TODO: Buttons
        Button btnApply = (Button) dialog.findViewById(R.id.btn_apply);
        //TODO: Layouts
        LinearLayout llSubParty = (LinearLayout) dialog.findViewById(R.id.Linear_Sub_Party);

        //TODO: Date Filter EditTexts
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
        //TODO:Spinner Party List
        String status = NetworkUtils.getConnectivityStatusString(context);
        if (!status.contentEquals("No Internet Connection")) {
            LoginActivity obj=new LoginActivity();
            String[] str = obj.GetSharePreferenceSession(context);
            if (str!=null)
                CallVolleyCallStatusFilter(pBcallStatus, msSpnCallStatus, spnAgentList, str[3], str[4], str[0], str[5], str[14]);
                CallVolleySelectCustomerForOrder(pBparty, spnPartyList, tVParty,pBsubParty, spnSubPartyList, tVSubParty,llSubParty, str[2], str[3], str[4], str[0], str[16], str[5], str[14]);
        } else {
            MessageDialog.MessageDialog(context, "", status);
        }
        //TODO: Button Apply
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String FromDt = edtFromDate.getText().toString();
                String ToDt = edtToDate.getText().toString();
                String status = NetworkUtils.getConnectivityStatusString(context);
                if (!status.contentEquals("No Internet Connection")) {
                        LoginActivity obj=new LoginActivity();
                        String[] str = obj.GetSharePreferenceSession(context);
                        if (str!=null)
                            CallCentreCallReport(str[3], str[4], str[0], str[5], str[14],AgentID, PartyID,SubPartyID,DateFormatsMethods.DateFormat_YYYY_MM_DD(FromDt),DateFormatsMethods.DateFormat_YYYY_MM_DD(ToDt),CallStatus);
                            dialog.dismiss();
                }else{
                    MessageDialog.MessageDialog(CallReportActivity.this,"",status);
                }
            }
        });
    }
    private void LoadMultiSelectionCallStatusSpinner(final ArrayList<Map<String,String>> mapArrayList, MultiSelectionSpinner spnMultiSelect){
        final List<String> listItem = new ArrayList<>();
        if (!mapArrayList.isEmpty()) {
            for (int i = 0; i < mapArrayList.size(); i++) {
                listItem.add(mapArrayList.get(i).get("Name") + "-" + mapArrayList.get(i).get("ID"));
            }
        }
        spnMultiSelect.setItems(listItem);
        spnMultiSelect.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
            @Override
            public void selectedIndices(List<Integer> indices) {}
            @Override
            public void selectedStrings(List<String> strings) {
                for (int i=0; i<mapArrayList.size(); i++){
                    String ID = mapArrayList.get(i).get("ID");
                    for (int j=0; j<strings.size(); j++){
                        String[] str = strings.get(j).split("-");
                        if (str.length > 0) {
                            if (ID.equals(str[1])) {
                                CallStatus +=   ID + ",";
                            }
                        }
                    }
                }
                CallStatus = (CallStatus.length() > 2 ? (CallStatus.contains(",") ? CallStatus.substring(0,CallStatus.length()-1) : CallStatus) : "");
            }
        });
    }
    private void LoadAgentListSpinner(ArrayList<Map<String,String>> mapArrayList, Spinner spnAgentList){
        spnAgentList.setAdapter(new CommonSearchSpinnerFilterableAdapter(context,mapArrayList,mapArrayList));
        spnAgentList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Map<String,String> map = (Map<String,String>) parent.getAdapter().getItem(position);
                AgentID = map.get("ID");
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
    //TODO: Call
    private void CallVolleyCallStatusFilter(final ProgressBar progressBar, final MultiSelectionSpinner spnMultiSelectCallStatus, final Spinner spnAgentList, final String DeviceID, final String UserID, final String SessionID, final String DivisionID, final String CompanyID){
        progressBar.setVisibility(View.VISIBLE);
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"CallCentreCallFilter", new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                // response
                Log.d("Response", response);
                ArrayList<Map<String,String>> maplistCallStatus = new ArrayList<>();
                ArrayList<Map<String,String>> maplistAgentList = new ArrayList<>();
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    int Status=(jsonObject.getString("status")==null)?0:jsonObject.getInt("status");
                    String Msg=(jsonObject.getString("msg")==null)?"Server is not responding":jsonObject.getString("msg");
                    if (Status == 1) {
                        JSONObject jsonObjResult = new JSONObject(jsonObject.getString("Result"));
                        //TODO: Call Status List
                        JSONArray jsonArrCallStatus = jsonObjResult.optJSONArray("CallStatusList")==null ? new JSONArray() : jsonObjResult.optJSONArray("CallStatusList");
                        if (jsonArrCallStatus.length()>0) {
                            for (int i = 0; i < jsonArrCallStatus.length(); i++) {
                                Map<String, String> map = new HashMap<>();
                                map.put("ID", jsonArrCallStatus.getJSONObject(i).optString("ID") == null ? "" : jsonArrCallStatus.getJSONObject(i).optString("ID"));
                                map.put("Name", jsonArrCallStatus.getJSONObject(i).optString("Name") == null ? "" : jsonArrCallStatus.getJSONObject(i).optString("Name"));
                                maplistCallStatus.add(map);
                            }
                        }
                        //TODO: Agent List
                        JSONArray jsonArrAgent = jsonObjResult.optJSONArray("AgentList") == null ? new JSONArray() : jsonObjResult.optJSONArray("AgentList");
                        if (jsonArrAgent.length()>0) {
                            for (int i = 0; i < jsonArrAgent.length(); i++) {
                                Map<String, String> map = new HashMap<>();
                                map.put("ID", jsonArrAgent.getJSONObject(i).optString("Agent_Name") == null ? "" : jsonArrAgent.getJSONObject(i).optString("Agent_Name"));
                                map.put("Name", jsonArrAgent.getJSONObject(i).optString("FullName") == null ? "" : jsonArrAgent.getJSONObject(i).optString("FullName"));
                                map.put("Type", jsonArrAgent.getJSONObject(i).optString("Agent_Name") == null ? "" : jsonArrAgent.getJSONObject(i).optString("Agent_Name"));
                                maplistAgentList.add(map);
                            }
                        }
                        //TODO: Load Multi Spinner
                        LoadMultiSelectionCallStatusSpinner(maplistCallStatus,spnMultiSelectCallStatus);
                        //TODO: Load Agent List
                        LoadAgentListSpinner(maplistAgentList,spnAgentList);
                    } else {
                        MessageDialog.MessageDialog(context,"",Msg);
                    }
                }catch (Exception e){
                    MessageDialog.MessageDialog(context,"Exception",""+e.toString());
                }
                progressBar.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", ""+error);
                MessageDialog.MessageDialog(context,"Error",""+error.toString());
                progressBar.setVisibility(View.GONE);
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
                Log.d(TAG,"Call Filter parameters:"+params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
    }
    //TODO: Call Volley Party List type Spinner  Class
    private void CallVolleySelectCustomerForOrder(final ProgressBar progressBar, final CommonSearchableSpinner spnPartyList, final TextView textView, final ProgressBar progressBarSP, final CommonSearchableSpinner spnSubPartyList, final TextView textViewSP,final LinearLayout llLayout, final String MasterType, final String DeviceID, final String UserID, final String SessionID, final String MasterID, final String DivisionID, final String CompanyID){
        progressBar.setVisibility(View.VISIBLE);
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
                        ArrayList<Map<String,String>> maplist = new ArrayList<>();
                        for (int i=0; i< jsonArrayScfo.length(); i++) {
                            Map<String, String> map = new HashMap<>();
                            map.put("ID", jsonArrayScfo.getJSONObject(i).optString("PartyID") == null ? "" : jsonArrayScfo.getJSONObject(i).optString("PartyID"));
                            map.put("Name", jsonArrayScfo.getJSONObject(i).optString("PartyName") == null ? "" : jsonArrayScfo.getJSONObject(i).optString("PartyName"));
                            map.put("Type", jsonArrayScfo.getJSONObject(i).optString("SubPartyApplicable") == null ? "0" : jsonArrayScfo.getJSONObject(i).optString("SubPartyApplicable"));
                            maplist.add(map);
                        }
                        CallReportActivity.this.commonAdapter = new CommonSearchSpinnerFilterableAdapter(CallReportActivity.this, maplist, maplist);
                        spnPartyList.setAdapter(commonAdapter);
                        spnPartyList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                textView.setVisibility(View.GONE);
                                Map<String, String> map = (Map<String, String>) parent.getAdapter().getItem(position);
                                PartyID = map.get("ID");
                                PartyName = map.get("Name");
                                String Type = map.get("Type") == null ? "0": map.get("Type");
                                if (Type.equals("1")){
                                    LoginActivity obj=new LoginActivity();
                                    String[] str = obj.GetSharePreferenceSession(context);
                                    if (str!=null && PartyID!=null)
                                        llLayout.setVisibility(View.VISIBLE);
                                        CallVolleySelectSubCustomerForOrder(progressBarSP, spnSubPartyList, textViewSP, str[2], str[3], str[4], str[0], str[16], str[5],str[14],PartyID);
                                }else{
                                    SubPartyID = "";
                                    SubPartyName = "";
                                    llLayout.setVisibility(View.GONE);
                                }
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {}
                        });
                    } else {
                        MessageDialog.MessageDialog(context,"",Msg);
                    }
                }catch (Exception e){
                    MessageDialog.MessageDialog(context,"Exception",""+e.toString());
                }
                progressBar.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", ""+error);
                MessageDialog.MessageDialog(context,"Error",""+error.toString());
                progressBar.setVisibility(View.GONE);
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
    private void CallVolleySelectSubCustomerForOrder( final ProgressBar progressBarSP, final CommonSearchableSpinner spnSubPartyList, final TextView textViewSP,final String MasterType, final String DeviceID, final String UserID,final String SessionID,final String MasterID,final String DivisionID,final String CompanyID,final String PartyID){
        progressBarSP.setVisibility(View.VISIBLE);
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"SubPartyList", new Response.Listener<String>()
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
                        ArrayList<Map<String,String>> maplist = new ArrayList<>();
                        for (int i=0; i< jsonArrayScfo.length(); i++) {
                            Map<String, String> map = new HashMap<>();
                            map.put("ID", jsonArrayScfo.getJSONObject(i).optString("SubPartyID") == null ? "" : jsonArrayScfo.getJSONObject(i).optString("SubPartyID"));
                            map.put("Name", jsonArrayScfo.getJSONObject(i).optString("SubPartyName") == null ? "" : jsonArrayScfo.getJSONObject(i).optString("SubPartyName"));
                            map.put("Type", jsonArrayScfo.getJSONObject(i).optString("SubPartyCode") == null ? "" : jsonArrayScfo.getJSONObject(i).optString("SubPartyCode"));
                            maplist.add(map);
                        }
                        CallReportActivity.this.commonAdapterSubParty = new CommonSearchSpinnerFilterableAdapter(CallReportActivity.this, maplist, maplist);
                        spnSubPartyList.setAdapter(commonAdapterSubParty);
                        spnSubPartyList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                textViewSP.setVisibility(View.GONE);
                                Map<String, String> map = (Map<String, String>) parent.getAdapter().getItem(position);
                                SubPartyID = map.get("ID");
                                SubPartyName = map.get("Name");
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {}
                        });
                    } else {
                        MessageDialog.MessageDialog(context,"",Msg);
                    }
                }catch (Exception e){
                    MessageDialog.MessageDialog(context,"Exception",""+e.toString());
                }
                progressBarSP.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", ""+error);
                MessageDialog.MessageDialog(context,"Error",""+error.toString());
                progressBarSP.setVisibility(View.GONE);
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
                params.put("PartyID", PartyID);
                Log.d(TAG,"Select sub customer for order parameters:"+params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
    }
    //TODO: Call Volley Search Filter
    private void CallCentreCallReport(final String DeviceID, final String UserID, final String SessionID, final String DivisionID, final String CompanyID, final String AgName, final String PartyID, final String SubPartyID, final String FromDt, final String ToDt, final String CallStatus){
        showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"CallCentreCallReport", new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                // response
                Log.d("Response", response);
                List<IncomingCalls> incomingCallsList = new ArrayList<>();
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    int Status=(jsonObject.getString("status")==null)?0:jsonObject.getInt("status");
                    String Msg=(jsonObject.getString("msg")==null)?"Server is not responding":jsonObject.getString("msg");
                    if (Status == 1) {
                        JSONArray jsonArray = new JSONArray(jsonObject.getString("Result"));
                        if(jsonArray.length() > 0){
                            for (int i=0; i<jsonArray.length(); i++){
                                IncomingCalls incomingCalls = new IncomingCalls();
                                incomingCalls.setParty(jsonArray.getJSONObject(i).optString("Party") == null ? "" : jsonArray.getJSONObject(i).optString("Party"));
                                incomingCalls.setSubparty(jsonArray.getJSONObject(i).optString("Subparty") == null ? "" : jsonArray.getJSONObject(i).optString("Subparty"));
                                incomingCalls.setTypeName(jsonArray.getJSONObject(i).optString("TypeName") == null ? "" : jsonArray.getJSONObject(i).optString("TypeName"));
                                incomingCalls.setCPName(jsonArray.getJSONObject(i).optString("CPName") == null ? "" : jsonArray.getJSONObject(i).optString("CPName"));
                                incomingCalls.setCalledNumber(jsonArray.getJSONObject(i).optString("CalledNumber") == null ? "" : jsonArray.getJSONObject(i).optString("CalledNumber"));
                                incomingCalls.setCallStatusMsg(jsonArray.getJSONObject(i).optString("CallStatusMsg") == null ? "" : jsonArray.getJSONObject(i).optString("CallStatusMsg"));
                                incomingCalls.setCallStatus(jsonArray.getJSONObject(i).optInt("CallStatus"));
                                incomingCalls.setHoldTime(jsonArray.getJSONObject(i).optString("HoldTime") == null ? "" : jsonArray.getJSONObject(i).optString("HoldTime"));
                                incomingCalls.setDuration(jsonArray.getJSONObject(i).optString("duration") == null ? "" : jsonArray.getJSONObject(i).optString("duration"));
                                incomingCalls.setSGAgentCount(jsonArray.getJSONObject(i).optInt("SG_AgentCount"));
                                incomingCalls.setAutoNo(jsonArray.getJSONObject(i).optString("AutoNo") == null ? "" : jsonArray.getJSONObject(i).optString("AutoNo"));
                                incomingCalls.setRecordFileName(jsonArray.getJSONObject(i).optString("RecordFileName") == null ? "" : jsonArray.getJSONObject(i).optString("RecordFileName"));
                                incomingCalls.setRecordFileUrl(jsonArray.getJSONObject(i).optString("RecordFileUrl") == null ? "" : jsonArray.getJSONObject(i).optString("RecordFileUrl"));

                                incomingCallsList.add(incomingCalls);
                            }
                            LoadRecyclerView(incomingCallsList);
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
                params.put("AgName", AgName);
                params.put("PartyID", PartyID);
                params.put("SubPartyID", SubPartyID);
                params.put("FromDt", FromDt);
                params.put("ToDt", ToDt);
                params.put("CallStatus", CallStatus);
                Log.d(TAG,"Call Report parameters:"+params.toString());
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
    private void CallSearchFilterApply(){
        LoginActivity obj=new LoginActivity();
        final String[] str = obj.GetSharePreferenceSession(context);
    }
    @Override
    public void onResume(){
        super.onResume();
        //CallSearchFilterApply();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            finish();
        }else if(keyCode == KeyEvent.KEYCODE_HOME ){
            Log.d("HomeKey","Home key pressed then restart app");
            finishAffinity();
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem logout = menu.findItem(R.id.action_filter_search);
        logout.setVisible(true);
        //TODO: Search view
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_search:
                //Snackbar.make(recyclerView,"Comming Soon....Search",Snackbar.LENGTH_LONG).show();
                break;
            case R.id.action_filter_search:
                DialogSearchFilter(context);
                break;
        }
        return true;
    }
}
