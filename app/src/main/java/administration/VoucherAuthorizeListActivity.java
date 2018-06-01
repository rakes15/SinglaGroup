package administration;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.singlagroup.AppController;
import com.singlagroup.LoginActivity;
import com.singlagroup.R;
import com.singlagroup.customwidgets.AeSimpleSHA1;
import com.singlagroup.customwidgets.CursorColor;
import com.singlagroup.customwidgets.ItemClickSupport;
import com.singlagroup.customwidgets.MessageDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DatabaseController.CommanStatic;
import DatabaseController.DatabaseSqlLiteHandlerUserInfo;
import administration.adapter.VoucherAuthorizeListAdapter;
import administration.database.DatabaseSqlLiteHandlerVoucherAuthorization;
import administration.datasets.VoucherAuthorizeListDataset;
import orderbooking.StaticValues;
import services.NetworkChangeReceiver;
import services.NetworkListener;
import services.NetworkUtils;

/**
 * Created by Rakesh on 13-Oct-16.
 */

public class VoucherAuthorizeListActivity extends AppCompatActivity {
    ActionBar actionBar;
    private Context context;
    //private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private VoucherAuthorizeListAdapter adapter;
    private ProgressDialog progressDialog;
    private DatabaseSqlLiteHandlerUserInfo DBHandler;
    private Dialog dialog;
    private static String TAG = VoucherAuthorizeListActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);} getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_recyclerview);
        Initialization();
        ModulePermission();
    }
    private void Initialization(){
        this.context = VoucherAuthorizeListActivity.this;
        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_Refresh_Layout);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        this.DBHandler = new DatabaseSqlLiteHandlerUserInfo(this);
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                ModulePermission();
//            }
//        });
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
                if (!CommanStatic.LogIN_UserPassword.isEmpty()) {
                    DialogChangePassword(CommanStatic.LogIN_UserPassword);
                }else {
                    MessageDialog.MessageDialog(VoucherAuthorizeListActivity.this,"","Encripted Password is blank");
                }
            }else {
                MessageDialog.MessageDialog(VoucherAuthorizeListActivity.this,"Alert","You don't have permission of "+Title+" module");
            }
        }catch (Exception e){
            MessageDialog.MessageDialog(VoucherAuthorizeListActivity.this,"Exception",e.toString());
        }
    }
    private void CallApiMethod(){
        //final String UserID =  "05A15CA6-7DC5-4FAC-98EC-B617C3C1DBF6"; str[4]
        //TODO: Select Customer for order Request
        NetworkChangeReceiver.bindNetworkListener(new NetworkListener() {
            @Override
            public void networkReceived(String status) {
                if (!status.contentEquals("No Internet Connection")) {
                    LoginActivity obj=new LoginActivity();
                    String[] str = obj.GetSharePreferenceSession(VoucherAuthorizeListActivity.this);
                    if (str!=null) {
                        CallVolleyRequestForApproval(str[3], str[0], str[14], str[4], str[5]);
                    }
                } else {
                    MessageDialog.MessageDialog(VoucherAuthorizeListActivity.this,"",status);
                }
            }
        });
        String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
        if (!status.contentEquals("No Internet Connection")) {
            LoginActivity obj=new LoginActivity();
            String[] str = obj.GetSharePreferenceSession(VoucherAuthorizeListActivity.this);
            if (str!=null) {
                CallVolleyRequestForApproval(str[3], str[0], str[14], str[4], str[5]);
            }
        } else {
            MessageDialog messageDialog=new MessageDialog();
            messageDialog.MessageDialog(VoucherAuthorizeListActivity.this,"","","No Internet Connection");
        }
    }
    private void LoadRecyclerView(List<Map<String,String>> mapList){
        if (!mapList.isEmpty()) {
            context.deleteDatabase(DatabaseSqlLiteHandlerVoucherAuthorization.DATABASE_NAME);
            DatabaseSqlLiteHandlerVoucherAuthorization DBAuthorization = new DatabaseSqlLiteHandlerVoucherAuthorization(context);
            DBAuthorization.deleteAuthoData();
            DBAuthorization.insertAuthorizationTable(mapList);
            adapter = new VoucherAuthorizeListAdapter(context, DBAuthorization.getVoucherHeadingList());
            recyclerView.setAdapter(adapter);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
//                        recyclerView.setHasFixedSize(true);
//                        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
//                            @Override
//                            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
//                                //VoucherAuthorizeListDataset dataset = (VoucherAuthorizeListDataset) list.get(position);
////                                Intent intent = new Intent(getApplicationContext(), MainGroupOrGroupActivity.class);
////                                intent.putExtra("Result",dataset);
////                                setResult(RESULT_OK,intent);
////                                finish();
//                            }
//                        });
        }else{
            List<VoucherAuthorizeListDataset> dataList = new ArrayList<>();
            adapter = new VoucherAuthorizeListAdapter(context, dataList);
            recyclerView.setAdapter(adapter);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
        }
    }
    private void CallVolleyRequestForApproval(final String DeviceID,final String SessionID,final String CompanyID, final String UserID,final String DivisionID){
        showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"AuthorizationVoucherList", new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                // response
                Log.d("Response", response);
                List<Map<String,String>> mapList = new ArrayList<>();
                try{

                    JSONObject jsonObject = new JSONObject(response);
                    int Status=(jsonObject.getString("status")==null)?0:jsonObject.getInt("status");
                    String Msg=(jsonObject.getString("msg")==null)?"Server is not responding":jsonObject.getString("msg");
                    if (Status == 1) {
                        JSONArray jsonArrayResult = jsonObject.getJSONArray("Result");
                        for (int i = 0; i < jsonArrayResult.length(); i++) {
                            Map<String, String> map = new HashMap<>();
                            map.put("ID", jsonArrayResult.getJSONObject(i).getString("ID"));
                            map.put("DocumentID", jsonArrayResult.getJSONObject(i).getString("DocumentID"));
                            map.put("DocumentDate", jsonArrayResult.getJSONObject(i).getString("DocumentDate"));
                            map.put("DocumentNumber", jsonArrayResult.getJSONObject(i).getString("DocumentNumber"));
                            map.put("Amount", jsonArrayResult.getJSONObject(i).getString("Amount"));
                            map.put("AuthLevel", jsonArrayResult.getJSONObject(i).getString("AuthLevel"));
                            map.put("BranchName", jsonArrayResult.getJSONObject(i).getString("BranchName"));
                            map.put("Status", jsonArrayResult.getJSONObject(i).getString("Status"));
                            map.put("AuthRemark", jsonArrayResult.getJSONObject(i).getString("AuthRemark"));
                            map.put("RequestedUserName", jsonArrayResult.getJSONObject(i).getString("RequestedUserName"));
                            map.put("RequestedUserID", jsonArrayResult.getJSONObject(i).getString("RequestedUserID"));
                            map.put("PartyName", jsonArrayResult.getJSONObject(i).getString("PartyName"));
                            map.put("VType", jsonArrayResult.getJSONObject(i).getString("VType"));
                            map.put("DocumentName", jsonArrayResult.getJSONObject(i).getString("DocumentName"));
                            map.put("BranchID", jsonArrayResult.getJSONObject(i).getString("BranchID"));
                            map.put("DivisionID", jsonArrayResult.getJSONObject(i).getString("DivisionID"));
                            map.put("BrandID", jsonArrayResult.getJSONObject(i).getString("BrandID"));
                            map.put("DepartmentID", jsonArrayResult.getJSONObject(i).getString("DepartmentID"));
                            map.put("ProjectID", jsonArrayResult.getJSONObject(i).getString("ProjectID"));
                            map.put("ClassificationCodeID", jsonArrayResult.getJSONObject(i).getString("ClassificationCodeID"));
                            map.put("PartyID", jsonArrayResult.getJSONObject(i).getString("PartyID"));
                            map.put("ApprovalDate", jsonArrayResult.getJSONObject(i).getString("ApprovalDate"));
                            map.put("ApprovalTime", jsonArrayResult.getJSONObject(i).getString("ApprovalTime"));
                            map.put("NoOfLevels", jsonArrayResult.getJSONObject(i).getString("NoOfLevels"));
                            map.put("Narration", jsonArrayResult.getJSONObject(i).getString("Narration"));
                            map.put("AmendmendRevisionNo", jsonArrayResult.getJSONObject(i).getString("AmendmendRevisionNo"));
                            map.put("GodownName", jsonArrayResult.getJSONObject(i).getString("GodownName"));
                            map.put("GodownID", jsonArrayResult.getJSONObject(i).getString("GodownID"));
                            map.put("ItemGroupID", jsonArrayResult.getJSONObject(i).getString("ItemGroupID"));
                            map.put("ItemGroupName", jsonArrayResult.getJSONObject(i).getString("ItemGroupName"));
                            mapList.add(map);
                        }
                        LoadRecyclerView(mapList);
                    } else {
                        MessageDialog.MessageDialog(context,"",Msg);
                        LoadRecyclerView(mapList);
                    }
                }catch (Exception e){
                    MessageDialog.MessageDialog(context,"Exception",""+e.toString());
                    LoadRecyclerView(mapList);
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
                Log.d(TAG,"Voucher Autho list parameters:"+params.toString());
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
        if(progressDialog!=null) { progressDialog.dismiss(); }
        //if(swipeRefreshLayout!=null) { swipeRefreshLayout.setRefreshing(false); }
        if(dialog!=null) { dialog.dismiss(); }
    }
    @Override
    protected void onResume() {
        super.onResume();
        //ModulePermission();
        //hidepDialog();
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
    //TODO: Re-authentication password
    private void DialogChangePassword(final String EncrytedPassword){
        dialog = new Dialog(new ContextThemeWrapper(context, R.style.DialogSlideAnim));
        dialog.setContentView(R.layout.dialog_authenticate_password);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        final TextView txtViewTitle = (TextView) dialog.findViewById(R.id.text_Title);
        final TextInputLayout PasswordWrapper = (TextInputLayout) dialog.findViewById(R.id.editText_Password);
        CursorColor.CursorColor(PasswordWrapper.getEditText());
        Button btnSubmit = (Button) dialog.findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Password Authenticate
                String status = NetworkUtils.getConnectivityStatusString(context);
                if (!status.contentEquals("No Internet Connection")) {
                    PasswordWrapper.setError("");
                    String Pass = PasswordWrapper.getEditText().getText().toString();
                    if(!Pass.isEmpty()) {
                        try {
                            String Encripted = AeSimpleSHA1.SHA1(Pass);
                            if (Encripted.equals(EncrytedPassword)){
                                dialog.dismiss();
                                CallApiMethod();
                            }else {
                                MessageDialog.MessageDialog(context,"Password Authentication","Your password may be wrong!!!");
                            }
                        }catch (NoSuchAlgorithmException e){
                            MessageDialog.MessageDialog(context,"NoSuchAlgorithmException",""+e.toString());
                        }catch (UnsupportedEncodingException e){
                            MessageDialog.MessageDialog(context,"UnsupportedEncodingException",""+e.toString());
                        }
                    }else {
                        PasswordWrapper.setError("Password cann't be blank");
                    }
                } else {
                    MessageDialog.MessageDialog(context,"","No Internet Connection");
                }
            }
        });
    }
}
