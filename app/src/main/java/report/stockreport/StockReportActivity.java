package report.stockreport;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

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
import orderbooking.view_order_details.adapter.OrderViewGroupAdapter;
import print.godown_group_order_with_rate.GodownOrGroupListActivity;
import print.godown_group_order_with_rate.model.Order;
import report.pendingdispatch.adapter.ShowroomAdapter;
import report.showroomitemcheck.adapter.GroupAdapter;
import report.showroomitemcheck.model.Group;
import services.NetworkChangeReceiver;
import services.NetworkListener;
import services.NetworkUtils;

/**
 * Created by Rakesh on 28-Aug-17.
 */
public class StockReportActivity extends AppCompatActivity{

    private ActionBar actionBar;
    private Context context;
    RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    //private SwipeRefreshLayout swipeRefreshLayout;
    private int colorSizeStockQty=0,Godown_Group_Order_Flag=0;
    private GroupAdapter adapter;
    private static String TAG = StockReportActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);} getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_recyclerview);
        Initialization();
        ModulePermission();
    }
    private void Initialization(){
        this.actionBar = getSupportActionBar();
        this.actionBar.setDisplayHomeAsUpEnabled(true);
        this.context = StockReportActivity.this;
        //swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_Refresh_Layout);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        this.progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
//        swipeRefreshLayout.setVisibility(View.VISIBLE);
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                //ModulePermission();
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
            //TODO: Filter Search
            DialogSearchFilter(context);
        }catch (Exception e){
            MessageDialog.MessageDialog(context,"Exception",e.toString());
        }
    }
    private void CallApiMethod(final String FromDate, final String ToDate, final String AppType){
        OrderViewGroupAdapter.listGroup = new ArrayList<>();
        //TODO: Closed Order Request
        NetworkChangeReceiver.bindNetworkListener(new NetworkListener() {
            @Override
            public void networkReceived(String status) {
                if (!status.contentEquals("No Internet Connection")) {
                    LoginActivity obj=new LoginActivity();
                    String[] str = obj.GetSharePreferenceSession(context);
                    if (str!=null) {
                        CallVolleyGodownOrGodownWisePrint(str[3], str[4], str[0], str[5], str[14], str[15],FromDate,ToDate,AppType);
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
                CallVolleyGodownOrGodownWisePrint(str[3], str[4], str[0], str[5], str[14], str[15],FromDate,ToDate,AppType);
            }
        } else {
            MessageDialog.MessageDialog(context,"",status);
        }
    }
    //TODO: Dialog Filter Search
    private void DialogSearchFilter(final Context context){
        final Dialog dialog = new Dialog(new ContextThemeWrapper(context, R.style.DialogSlideAnim));
        dialog.setContentView(R.layout.dialog_showroom_item_check_search_filter);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.TOP;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        //TODO: Declarations
        final EditText edtQty = (EditText) dialog.findViewById(R.id.editText_Qty);
        final ImageView imgMinus = (ImageView) dialog.findViewById(R.id.ImageView_Minus);
        final ImageView imgPlus = (ImageView) dialog.findViewById(R.id.ImageView_Plus);
        Spinner spnShowroom = (Spinner) dialog.findViewById(R.id.spinner_Showroom);
        Spinner spnGodown = (Spinner) dialog.findViewById(R.id.spinner_Godown);
        Button btnApply = (Button) dialog.findViewById(R.id.btn_apply);
        //TODO: Showroom Spinner AND Godown Spinner
        LoginActivity obj=new LoginActivity();
        final String[] str=obj.GetSharePreferenceSession(context);
        if (str!=null) {
            DatabaseSqlLiteHandlerUserInfo DBHandler = new DatabaseSqlLiteHandlerUserInfo(context);
            //TODO: Showroom Spinner
            final List<GodownDataset> ShowroomDatasetList = DBHandler.getReserveGodownList(str[14], str[5], str[15]);
            spnShowroom.setAdapter(new ShowroomAdapter(context, ShowroomDatasetList));
            spnShowroom.setSelection(2);
            spnShowroom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
                    //ShowroomID = ShowroomDatasetList.get(position).getGodownID();
                    //Showroom = ShowroomDatasetList.get(position).getGodownName();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
            //TODO: Godown Spinner
            final List<GodownDataset> godownDatasetList = DBHandler.getReserveGodownList(str[14], str[5], str[15]);
            spnGodown.setAdapter(new ShowroomAdapter(context, godownDatasetList));
            spnGodown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
//                    ShowroomID = godownDatasetList.get(position).getGodownID();
//                    Showroom = godownDatasetList.get(position).getGodownName();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        }
        //TODO: Color Size Stock
        colorSizeStockQty = 0;
        edtQty.setSelectAllOnFocus(true);
        imgMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Qty = edtQty.getText().toString().trim();
                if(!Qty.isEmpty()){
                    int qty = Integer.valueOf(Qty);
                    if (qty > 0){
                        colorSizeStockQty = qty;
                        colorSizeStockQty--;
                        edtQty.setText(""+colorSizeStockQty);
                    }
                }else{
                    //MessageDialog.MessageDialog(context,"","Color Size Empty");
                    edtQty.setText("0");
                }
            }
        });
        imgPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Qty = edtQty.getText().toString().trim();
                if(!Qty.isEmpty()){
                    int qty = Integer.valueOf(Qty);
                    if (qty < 9999) {
                        colorSizeStockQty = qty;
                        colorSizeStockQty++;
                        edtQty.setText("" + colorSizeStockQty);
                    }
                }else{
                    edtQty.setText("0");
                }
            }
        });
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Filter Apply
//                String FromDate = edtFromDate.getText().toString();
//                String ToDate = edtToDate.getText().toString();
//                if (!FromDate.isEmpty() && !ToDate.isEmpty()) {
//                    if (StaticValues.viewFlag == 1 || StaticValues.editFlag == 1 || StaticValues.createFlag == 1 || StaticValues.removeFlag == 1 || StaticValues.printFlag == 1 || StaticValues.importFlag == 1 || StaticValues.exportFlag == 1){
//                        //CallApiMethod(DateFormatsMethods.DateFormat_YYYY_MM_DD(FromDate)+StaticValues.FromTime,DateFormatsMethods.DateFormat_YYYY_MM_DD(ToDate)+StaticValues.ToTime,String.valueOf(appType));
//                        dialog.dismiss();
//                    }else {
//                        MessageDialog.MessageDialog(context,"Alert","You don't have permission of this module");
//                    }
//                }else{
//                    MessageDialog.MessageDialog(context,"","From Date and To Date cann't be blank!!!");
//                }
                dialog.dismiss();
            }
        });
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
        //CallApiMethod();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem actionSearch = menu.findItem(R.id.action_search);
        actionSearch.setVisible(true);
        MenuItem actionFilterSearch = menu.findItem(R.id.action_filter_search);
        actionFilterSearch.setVisible(true);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(actionSearch);
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
    private void CallVolleyGodownOrGodownWisePrint(final String DeviceID, final String UserID,final String SessionID,final String DivisionID,final String CompanyID,final String BranchID,final String FromDate,final String ToDate,final String AppType){
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
                    if (Status == 1) {
                        List<Group> mapList = new ArrayList<>();
                        JSONArray jsonArrayResult = jsonObject.getJSONArray("Result");
                        for (int z = 0; z<jsonArrayResult.length(); z++) {
                            String OrderID = jsonArrayResult.getJSONObject(z).getString("OrderID");
                            JSONArray jsonArrayDetails = jsonArrayResult.getJSONObject(z).getJSONArray("Details");
                            //DetailsLength = jsonArrayDetails.length();
//                            for (int i = 0; i<jsonArrayDetails.length(); i++) {
//                                mapList.add(new OrderViewGroupDataset(OrderID,
//                                        jsonArrayDetails.getJSONObject(i).getString("MainGroupID"),
//                                        jsonArrayDetails.getJSONObject(i).getString("MainGroup"),
//                                        jsonArrayDetails.getJSONObject(i).getString("GroupID"),
//                                        jsonArrayDetails.getJSONObject(i).getString("UserName"),
//                                        jsonArrayDetails.getJSONObject(i).getString("FullName"),
//                                        jsonArrayDetails.getJSONObject(i).getString("EmpCVType"),
//                                        jsonArrayDetails.getJSONObject(i).getString("EmpCVName"),
//                                        jsonArrayDetails.getJSONObject(i).getString("GroupImage"),
//                                        jsonArrayDetails.getJSONObject(i).getString("GroupName"),
//                                        jsonArrayDetails.getJSONObject(i).getString("SubGroupID"),
//                                        jsonArrayDetails.getJSONObject(i).getString("SubGroup"),
//                                        TotalQty,
//                                        TotalStyle,
//                                        TotalAmount,
//                                        jsonArrayDetails.getJSONObject(i).getString("LastBookDateTime")
//                                ));
//                            }
                        }
                        LoadRecyclerView(mapList);
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
                params.put("FromDate", FromDate);
                params.put("ToDate", ToDate);
                params.put("AppType", AppType);
                Log.d(TAG," Godown || Group parameters:"+params.toString());
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
        if(progressDialog!=null ){// || swipeRefreshLayout!=null) {
            progressDialog.dismiss();
            //swipeRefreshLayout.setRefreshing(false);
        }
    }
    //TODO:Load Recycler View
    private void LoadRecyclerView(List<Group> orderList){
        adapter = new GroupAdapter(context,orderList);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Order dataset = (Order) adapter.getItem(position);
                Intent intent = new Intent(getApplicationContext(), GodownOrGroupListActivity.class);
                intent.putExtra("Key",dataset);
                intent.putExtra("Group",Godown_Group_Order_Flag);
                startActivity(intent);
            }
        });
    }
}