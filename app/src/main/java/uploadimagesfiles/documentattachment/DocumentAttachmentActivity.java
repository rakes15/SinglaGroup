package uploadimagesfiles.documentattachment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
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
import com.singlagroup.adapters.CommonSearchSpinnerFilterableAdapter;
import com.singlagroup.adapters.DivisionFilterableAdapter;
import com.singlagroup.customwidgets.AndroidID;
import com.singlagroup.customwidgets.CommonSearchableSpinner;
import com.singlagroup.customwidgets.DateFormatsMethods;
import com.singlagroup.customwidgets.ItemClickSupport;
import com.singlagroup.customwidgets.MessageDialog;
import com.singlagroup.datasets.DivisionDataset;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DatabaseController.CommanStatic;
import DatabaseController.DatabaseSqlLiteHandlerUserInfo;
import orderbooking.StaticValues;
import retrofit.ApiClient;
import retrofit.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import services.NetworkUtils;
import uploadimagesfiles.documentattachment.adapter.RecyclerViewDocumentAdapter;
import uploadimagesfiles.documentattachment.datasets.Result;
import uploadimagesfiles.documentattachment.responsedatasets.ResponseDocumentDataset;

/**
 * Created by Rakesh on 14-Jan-17.
 */
public class DocumentAttachmentActivity extends AppCompatActivity {
    private static String TAG = DocumentAttachmentActivity.class.getSimpleName();
    private Context context;
    private RecyclerView recyclerView;
    private Dialog dialog;
    private ProgressDialog progressDialog;
    private ActionBar actionBar;
    private CommonSearchSpinnerFilterableAdapter commonAdapter;
    private RecyclerViewDocumentAdapter adapter;
    private String DivisionID="",Division="",PartyID="",PartyName="";
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
            this.context = DocumentAttachmentActivity.this;
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
                MessageDialog.MessageDialog(DocumentAttachmentActivity.this,"Alert","You don't have permission of "+Title+" module");
            }
        }catch (Exception e){
            MessageDialog.MessageDialog(DocumentAttachmentActivity.this,"Exception",e.toString());
        }
    }
    private void LoadRecyclerView(List<Result> BillList){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        adapter = new RecyclerViewDocumentAdapter(context, BillList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Result results = (Result) adapter.getItem(position);
                Intent intent = new Intent(context, DocumentAttachmentUploadActivity.class);
                intent.putExtra("Title",Title);
                intent.putExtra("BillID",results.getID());
                intent.putExtra("DivisionID",DivisionID);
                intent.putExtra("PartyID",PartyID);
                startActivity(intent);
                finish();
            }
        });
    }
    private void DialogSearchFilter(Context context){
        dialog = new Dialog(new ContextThemeWrapper(context, R.style.DialogSlideAnim));
        dialog.setContentView(R.layout.dialog_document_search_filter);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.TOP;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        //TODO: Declarations
        final TextView textView = (TextView) dialog.findViewById(R.id.text_view);
        Spinner spnDivision = (Spinner) dialog.findViewById(R.id.Spinner_Division);
        Spinner spnHnadOverType = (Spinner) dialog.findViewById(R.id.Spinner_hand_over_type);
        final ProgressBar progressBar = (ProgressBar) dialog.findViewById(R.id.ProgressBar);
        final CommonSearchableSpinner spnPartyList = (CommonSearchableSpinner) dialog.findViewById(R.id.spinner_Party_List);
        Button btnApply = (Button) dialog.findViewById(R.id.btn_apply);
        DatabaseSqlLiteHandlerUserInfo DBUserInfo = new DatabaseSqlLiteHandlerUserInfo(context);
        LoginActivity obj=new LoginActivity();
        final String[] str = obj.GetSharePreferenceSession(context);
        //TODO:Spinner Division
        List<DivisionDataset> DivisionList = DBUserInfo.getUserInfoDivision(str[14]);
        spnDivision.setAdapter(new DivisionFilterableAdapter(context, DivisionList, DivisionList));
        spnDivision.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DivisionDataset divisionDataset = (DivisionDataset)parent.getAdapter().getItem(position);
                DivisionID = divisionDataset.getDivisionID();
                Division = divisionDataset.getDivisionName();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        //TODO:Spinner Hand Over Type
        spnHnadOverType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                HandOverType = position;
                //TODO:Spinner Party List
                if (PartyList.isEmpty()) {
                    String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
                    if (!status.contentEquals("No Internet Connection")) {
                        CallVolleySelectCustomerForOrder(progressBar, spnPartyList, textView, str[2], str[3], str[4], str[0], str[16], DivisionID, str[14]);
                    } else {
                        MessageDialog.MessageDialog(DocumentAttachmentActivity.this, "", status);
                    }
                }else {
                    ArrayList<Map<String,String>> maplist = new ArrayList<Map<String, String>>();
                    for (int i=0; i<PartyList.size(); i++){
                        if (HandOverType == Integer.valueOf(PartyList.get(i).get("Type"))) {
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("ID", PartyList.get(i).get("ID"));
                            map.put("Name", PartyList.get(i).get("Name"));
                            map.put("Type", PartyList.get(i).get("Type"));
                            maplist.add(map);
                        }
                    }
                    DocumentAttachmentActivity.this.commonAdapter = new CommonSearchSpinnerFilterableAdapter(DocumentAttachmentActivity.this, maplist, maplist);
                    spnPartyList.setAdapter(commonAdapter);
                    spnPartyList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            textView.setVisibility(View.GONE);
                            Map<String, String> map = (Map<String, String>) parent.getAdapter().getItem(position);
                            PartyID = map.get("ID");
                            PartyName = map.get("Name");
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {}
                    });
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        //TODO: Button Apply
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
                if (!status.contentEquals("No Internet Connection")) {
                    if (!PartyID.isEmpty()) {
                        CallRetrofitSearchFilter(str[3], str[0], str[4], str[15], DivisionID, str[14], PartyID);
                        dialog.dismiss();
                    }else {
                        MessageDialog.MessageDialog(DocumentAttachmentActivity.this, "", "Please Select Party");
                    }
                }else{
                    MessageDialog.MessageDialog(DocumentAttachmentActivity.this,"",status);
                }
            }
        });
    }
    //TODO: Call Volley Party List type Spinner  Class
    private void CallVolleySelectCustomerForOrder(final ProgressBar progressBar, final CommonSearchableSpinner spnPartyList,final TextView textView,final String MasterType, final String DeviceID, final String UserID,final String SessionID,final String MasterID,final String DivisionID,final String CompanyID){
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
                        for (int i=0; i< jsonArrayScfo.length(); i++){
                            if (HandOverType == jsonArrayScfo.getJSONObject(i).getInt("billClosedStatus")) {
                                Map<String, String> map = new HashMap<>();
                                map.put("ID", jsonArrayScfo.getJSONObject(i).getString("PartyID"));
                                map.put("Name", jsonArrayScfo.getJSONObject(i).getString("PartyName"));
                                map.put("Type", (jsonArrayScfo.getJSONObject(i).getString("billClosedStatus") == null ? "0" : jsonArrayScfo.getJSONObject(i).getString("billClosedStatus")));
                                maplist.add(map);
                            }
                            Map<String, String> map = new HashMap<>();
                            map.put("ID", jsonArrayScfo.getJSONObject(i).getString("PartyID"));
                            map.put("Name", jsonArrayScfo.getJSONObject(i).getString("PartyName"));
                            map.put("Type", (jsonArrayScfo.getJSONObject(i).getString("billClosedStatus") == null ? "0" : jsonArrayScfo.getJSONObject(i).getString("billClosedStatus")));
                            PartyList.add(map);
                        }
                        if (maplist.isEmpty()){
                            MessageDialog.MessageDialog(context,"","No Record found");
                            DocumentAttachmentActivity.this.commonAdapter = new CommonSearchSpinnerFilterableAdapter(DocumentAttachmentActivity.this, maplist, maplist);
                            spnPartyList.setAdapter(commonAdapter);
                            PartyID = "";
                            PartyName = "";
                        }else {
                            DocumentAttachmentActivity.this.commonAdapter = new CommonSearchSpinnerFilterableAdapter(DocumentAttachmentActivity.this, maplist, maplist);
                            spnPartyList.setAdapter(commonAdapter);
                            spnPartyList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    textView.setVisibility(View.GONE);
                                    Map<String, String> map = (Map<String, String>) parent.getAdapter().getItem(position);
                                    PartyID = map.get("ID");
                                    PartyName = map.get("Name");
                                }
                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {}
                            });
                        }
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
    //TODO: Call Retrofit Search Filter
    private void CallRetrofitSearchFilter(String DeviceID, String SessionID, String UserID, String BranchID, String DivisionID, String CompanyID, String PartyID){
        showpDialog();
        ApiInterface apiService =  ApiClient.getClient(StaticValues.BASE_URL).create(ApiInterface.class);
        Map<String, String> params = new HashMap<String, String>();
        params.put("DeviceID", DeviceID);
        params.put("UserID", UserID);
        params.put("SessionID", SessionID);
        params.put("BranchID", BranchID);
        params.put("DivisionID", DivisionID);
        params.put("CompanyID", CompanyID);
        params.put("PartyID", PartyID);
        Log.d(TAG,"Party Bill Parameters:"+params.toString());
        Call<ResponseDocumentDataset> call = apiService.getPartyBillList(params);
        call.enqueue(new Callback<ResponseDocumentDataset>() {
            @Override
            public void onResponse(Call<ResponseDocumentDataset> call, retrofit2.Response<ResponseDocumentDataset> response) {
                try {
                    if (response.isSuccessful()) {
                        int Status = response.body().getStatus();
                        String msg = response.body().getMsg();
                        if (Status == 1) {
                            List<Result> datasetList = response.body().getResult();
                            //TODO: Call LoadData method
                            if (datasetList.isEmpty()) {
                                MessageDialog.MessageDialog(DocumentAttachmentActivity.this, "", "No voucher found!!!");
                            } else {
                                List<Result> datasetList2 = new ArrayList<Result>();
                                Result result = new Result();
                                result.setID(StaticValues.ConstantValue);
                                result.setCombinedVNo("None");
                                result.setVDate(DateFormatsMethods.DateFormat_DD_MM_YYYY(DateFormatsMethods.getDateTime().substring(0,10)));
                                result.setBillNetAmount("None");
                                datasetList2.add(result);
                                datasetList2.addAll(datasetList);
                                LoadRecyclerView(datasetList2);
                            }
                        } else {
                            String Title = getIntent().getExtras().getString("Title");
                            List<Result> datasetList = response.body().getResult();
                            //TODO: Call LoadData method
                            LoadRecyclerView(datasetList);
                            MessageDialog.MessageDialog(DocumentAttachmentActivity.this, Title, msg);
                        }
                    } else {
                        List<Result> datasetList = new ArrayList<Result>();
                        //TODO: Call LoadData method
                        LoadRecyclerView(datasetList);
                        MessageDialog.MessageDialog(DocumentAttachmentActivity.this, "Server response :", "" + response.code());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Exception: " + e.getMessage());
                    MessageDialog.MessageDialog(DocumentAttachmentActivity.this, "Exception", "" + e.getMessage());
                }
                hidepDialog();
            }
            @Override
            public void onFailure(Call<ResponseDocumentDataset> call, Throwable t) {
                Log.e(TAG, "Failure: " + t.toString());
                MessageDialog.MessageDialog(DocumentAttachmentActivity.this, "Exception", "" + t.toString());
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
