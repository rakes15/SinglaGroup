package administration;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.singlagroup.AppController;
import com.singlagroup.LoginActivity;
import com.singlagroup.R;
import com.singlagroup.customwidgets.ItemClickSupport;
import com.singlagroup.customwidgets.MessageDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DatabaseController.CommanStatic;
import administration.adapter.RequestForApprovalAdapter;
import administration.adapter.VoucherAuthorizeListAdapter;
import administration.database.DatabaseSqlLiteHandlerVoucherAuthorization;
import administration.datasets.VTypeDetailsDataset;
import administration.datasets.VoucherAuthorizeListDataset;
import orderbooking.StaticValues;
import services.NetworkChangeReceiver;
import services.NetworkListener;
import services.NetworkUtils;
import uploadimagesfiles.FileImageUplodingAcitvity;

/**
 * Created by Rakesh on 13-Oct-16.
 */

public class RequestForApprovalActivity extends AppCompatActivity {
    ActionBar actionBar;
    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RequestForApprovalAdapter adapter;
    private ProgressDialog progressDialog;
    private String Heading = "",VType = "";
    private static String TAG = RequestForApprovalActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);} getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_recyclerview_swipe_refresh_layout);
        Initialization();
        getIntentKey();
    }
    private void Initialization(){
        this.context = RequestForApprovalActivity.this;
        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_Refresh_Layout);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getIntentKey();
            }
        });
    }
    private void getIntentKey(){
        try{
            Heading = getIntent().getExtras().getString("Heading");
            VType = getIntent().getExtras().getString("VType");
            if (!VType.isEmpty() && !Heading.isEmpty()){
                actionBar.setTitle(Heading);
                CallApiMethod();
            }
        }catch (Exception e){
            MessageDialog.MessageDialog(context,"",""+e.toString());
        }
    }
//    private void CallApiMethod(){
//        DatabaseSqlLiteHandlerVoucherAuthorization DBAuthorization = new DatabaseSqlLiteHandlerVoucherAuthorization(context);
//        adapter=new RequestForApprovalAdapter(context,DBAuthorization.getVTypeDetails(Integer.valueOf(VType)));
//        recyclerView.setAdapter(adapter);
//        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context);
//        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        recyclerView.setLayoutManager(linearLayoutManager);
//        if(swipeRefreshLayout!=null) {
//            swipeRefreshLayout.setRefreshing(false);
//        }
//    }
    private void CallApiMethod(){
        //final String UserID = "05A15CA6-7DC5-4FAC-98EC-B617C3C1DBF6"; //str[4];
        //TODO: Select Customer for order Request
        NetworkChangeReceiver.bindNetworkListener(new NetworkListener() {
            @Override
            public void networkReceived(String status) {
                if (!status.contentEquals("No Internet Connection")) {
                    LoginActivity obj=new LoginActivity();
                    String[] str = obj.GetSharePreferenceSession(context);
                    if (str!=null) {
                        CallVolleyRequestForApproval(str[3], str[0], str[14], str[4], str[5]);
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
                CallVolleyRequestForApproval(str[3], str[0], str[14], str[4], str[5]);
            }
        } else {
            MessageDialog messageDialog=new MessageDialog();
            messageDialog.MessageDialog(context,"","","No Internet Connection");
        }
    }
    private void LoadRecyclerView(List<Map<String,String>> mapList){
        if (!mapList.isEmpty()) {
            context.deleteDatabase(DatabaseSqlLiteHandlerVoucherAuthorization.DATABASE_NAME);
            DatabaseSqlLiteHandlerVoucherAuthorization DBAuthorization = new DatabaseSqlLiteHandlerVoucherAuthorization(context);
            DBAuthorization.deleteAuthoData();
            DBAuthorization.insertAuthorizationTable(mapList);
            adapter=new RequestForApprovalAdapter(context,DBAuthorization.getVTypeDetails(Integer.valueOf(VType)));
            recyclerView.setAdapter(adapter);
            LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            if(swipeRefreshLayout!=null) {
                swipeRefreshLayout.setRefreshing(false);
            }
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
            List<VTypeDetailsDataset> dataList = new ArrayList<>();
            adapter=new RequestForApprovalAdapter(context,dataList);
            recyclerView.setAdapter(adapter);
            LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            if(swipeRefreshLayout!=null) {
                swipeRefreshLayout.setRefreshing(false);
            }
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
        if(swipeRefreshLayout!=null) { swipeRefreshLayout.setRefreshing(false); }
    }
    @Override
    protected void onResume() {
        super.onResume();
        //CallApiMethod();
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
            case R.id.action_logout: //TODO : Approved
                DatabaseSqlLiteHandlerVoucherAuthorization DBAuth = new DatabaseSqlLiteHandlerVoucherAuthorization(context);
                List<VTypeDetailsDataset> datasetList = DBAuth.getVTypeDetails(Integer.valueOf(VType));
                if (!datasetList.isEmpty()){
                    String Status = NetworkUtils.getConnectivityStatusString(context);
                    if (!Status.contentEquals("No Internet Connection")) {
                        LoginActivity obj=new LoginActivity();
                        String[] str = obj.GetSharePreferenceSession(context);
                        if (str!=null) {
                            for (int i=0; i<datasetList.size(); i++){
                                int status = RequestForApprovalAdapter.SpnStatus[i];//datasetList.get(i).getStatus();
                                if(status == 2 || status == 3){
                                    if (!RequestForApprovalAdapter.StrRemarks[i].isEmpty()){
                                        CallVolleyApprove(str[3], str[4], str[0],str[14] , str[5],""+datasetList.get(i).getVType(), datasetList.get(i).getDocID(),RequestForApprovalAdapter.StrRemarks[i],""+status,datasetList.size(),i);
                                    }
                                }else {
                                    if (status > 0)
                                    CallVolleyApprove(str[3], str[4], str[0],str[14] , str[5],""+datasetList.get(i).getVType(), datasetList.get(i).getDocID(),RequestForApprovalAdapter.StrRemarks[i],""+status,datasetList.size(),i);
                                }
                            }
                        }
                    } else {
                        MessageDialog.MessageDialog(context,"",Status);
                    }
                }
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
        MenuItem approvedItem = menu.findItem(R.id.action_logout);//tODO: Approved
        approvedItem.setIcon(getResources().getDrawable(R.drawable.ic_action_approved));
        approvedItem.setVisible(true);
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
    private void CallVolleyApprove(final String DeviceID, final String UserID, final String SessionID, final String CompanyID, final String DivisionID, final String VType, final String VID, final String Remarks, final String ApprovalStatus, final int size, final int index){
        if (index==0){ showpDialog(); }
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"VoucherAuthorisation", new Response.Listener<String>()
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
                        //JSONObject jsonObjectJSONObject = jsonObject.getJSONObject("Result");
                        //MessageDialog.MessageDialog(context,"Approved",""+jsonObjectJSONObject.getString("Msg"));
                        if (size==(index+1)) { MessageDialogByIntent(context, "", Msg); }
                    } else {
                        if (size==(index+1)) { MessageDialogByIntent(context, "", Msg); }
                    }
                }catch (Exception e){
                    MessageDialog.MessageDialog(context,"Exception",""+e.toString());
                }
                if (size==(index+1)) { hidepDialog(); }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", ""+error);
                MessageDialog.MessageDialog(context,"Error",""+error.toString());
                if (size==(index+1)) { hidepDialog(); }
            }
        } ) {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String>  params = new HashMap<String, String>();
                params.put("DeviceID", DeviceID);
                params.put("UserID", UserID);
                params.put("SessionID", SessionID);
                params.put("DivisionID", DivisionID);
                params.put("CompanyID", CompanyID);
                params.put("VType", VType);
                params.put("VID", VID);
                params.put("Remarks", Remarks);
                params.put("ApprovalStatus", ApprovalStatus);
                Log.d(TAG,"Voucher Authorisation parameters:"+params.toString());
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
                    Intent intent = getIntent();
                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                    finish();
                }
            });
            dialog.show();
        }catch (Exception e){
            Log.e("TAG","MessageDialogException2"+e.toString());
        }
    }
}
