package report.showroomitemcheck;

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
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.singlagroup.AppController;
import com.singlagroup.LoginActivity;
import com.singlagroup.R;
import com.singlagroup.adapters.GodownFilterableAdapter;
import com.singlagroup.customwidgets.ItemClickSupport;
import com.singlagroup.customwidgets.MessageDialog;
import com.singlagroup.customwidgets.MultiSelectionSpinner;
import com.singlagroup.datasets.GodownDataset;
import com.singlagroup.datasets.ShowroomDataset;
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
import report.DatabaseSqlite.DBSqlLiteHandlerShowroomItemCheck;
import report.showroomitemcheck.adapter.GodownMultiSelectionAdapter;
import report.showroomitemcheck.adapter.GroupAdapter;
import report.showroomitemcheck.adapter.ShowroomAdapter;
import report.showroomitemcheck.model.GodownCheckBox;
import report.showroomitemcheck.model.Group;
import services.NetworkChangeReceiver;
import services.NetworkListener;
import services.NetworkUtils;

/**
 * Created by Rakesh on 28-Aug-17.
 */
public class ShowroomItemCheckActivity extends AppCompatActivity{

    private ActionBar actionBar;
    private Context context;
    RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    private int colorSizeStockQty=0,ItemCategory=0,greaterOrLessthan=0,Godown_Group_Order_Flag=0;
    private String ShowroomID="",Showroom="",GodownID="",Godown="";
    private GroupAdapter adapter;
    private static String TAG = ShowroomItemCheckActivity.class.getSimpleName();
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
        this.context = ShowroomItemCheckActivity.this;
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        this.progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
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
    private void CallApiMethod(final String ShowroomID, final String GodownID, final String ItemCategory, final String StockQty, final String StockFlag){
        OrderViewGroupAdapter.listGroup = new ArrayList<>();
        //TODO: Closed Order Request
        NetworkChangeReceiver.bindNetworkListener(new NetworkListener() {
            @Override
            public void networkReceived(String status) {
                if (!status.contentEquals("No Internet Connection")) {
                    LoginActivity obj=new LoginActivity();
                    String[] str = obj.GetSharePreferenceSession(context);
                    if (str!=null) {
                        CallVolleyShowroomItemCheck(str[3], str[4], str[0], str[5], str[14], str[15],ShowroomID,GodownID,ItemCategory,StockQty,StockFlag);
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
                CallVolleyShowroomItemCheck(str[3], str[4], str[0], str[5], str[14], str[15],ShowroomID,GodownID,ItemCategory,StockQty,StockFlag);
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
        Spinner spnShowroom = (Spinner) dialog.findViewById(R.id.spinner_Showroom);
        //Spinner spnGodown = (Spinner) dialog.findViewById(R.id.spinner_Godown);
        MultiSelectionSpinner spnGodown = (MultiSelectionSpinner) dialog.findViewById(R.id.spinner_Godown_multi_selection);
        Spinner spnItemCategory = (Spinner) dialog.findViewById(R.id.spinner_ItemCategory);
        final EditText edtQty = (EditText) dialog.findViewById(R.id.editText_Qty);
        final ImageView imgMinus = (ImageView) dialog.findViewById(R.id.ImageView_Minus);
        final ImageView imgPlus = (ImageView) dialog.findViewById(R.id.ImageView_Plus);
        Spinner spnGreaterOrLessthan = (Spinner) dialog.findViewById(R.id.spinner_Color_Size_Stock);
        Button btnApply = (Button) dialog.findViewById(R.id.btn_apply);
        //TODO: Showroom Spinner AND Godown Spinner
        LoginActivity obj=new LoginActivity();
        final String[] str=obj.GetSharePreferenceSession(context);
        if (str!=null) {
            DatabaseSqlLiteHandlerUserInfo DBHandler = new DatabaseSqlLiteHandlerUserInfo(context);
            //TODO: Showroom Spinner
            final List<ShowroomDataset> ShowroomDatasetList = DBHandler.getShowroomList(str[14], str[5], str[15]);
            spnShowroom.setAdapter(new ShowroomAdapter(context, ShowroomDatasetList));
            spnShowroom.setSelection(2);
            spnShowroom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
                    ShowroomID = ShowroomDatasetList.get(position).getShowroomID();
                    Showroom = ShowroomDatasetList.get(position).getShowroomName();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
            //TODO: Godown Spinner
            final List<GodownDataset> godownDatasetList = DBHandler.getGodownList(str[14], str[5], str[15]);
            //final List<GodownCheckBox> godownCheckBoxList = new ArrayList<>();
            List<String> listItem = new ArrayList<>();
            for (int i=0; i<godownDatasetList.size(); i++){
                listItem.add(godownDatasetList.get(i).getGodownName());
//                GodownCheckBox checkBox = new GodownCheckBox();
//                checkBox.setID(godownDatasetList.get(i).getGodownID());
//                checkBox.setTitle(godownDatasetList.get(i).getGodownName());
//                checkBox.setSelected(false);
//                godownCheckBoxList.add(checkBox);
            }
            spnGodown.setItems(listItem);
            spnGodown.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
                @Override
                public void selectedIndices(List<Integer> indices) {}
                @Override
                public void selectedStrings(List<String> strings) {
                    GodownID = "";
                    for (int i=0; i<godownDatasetList.size(); i++){
                        String GID = godownDatasetList.get(i).getGodownID();
                        String Godown = godownDatasetList.get(i).getGodownName();
                        for (int j=0; j<strings.size(); j++){
                            if (Godown.toLowerCase().trim().equals(strings.get(j).toLowerCase().trim())){
                                GodownID+="'"+GID+"',";
                            }
                        }
                    }
                    GodownID = (GodownID.length() > 2 ? (GodownID.contains("''") ? GodownID.substring(3,GodownID.length()-1) : GodownID.substring(0,GodownID.length()-1)) : "");
                    //MessageDialog.MessageDialog(context,"",""+GodownID);
                }
            });

            //spnGodown.setAdapter(new GodownMultiSelectionAdapter(context,0,godownCheckBoxList));
//            spnGodown.setAdapter(new GodownFilterableAdapter(context, godownDatasetList,godownDatasetList));
//            spnGodown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
////                    GodownCheckBox godownCheckBox = (GodownCheckBox)parent.getAdapter().getItem(position);
////                    Toast.makeText(context,"Title:"+godownCheckBox.getTitle(),Toast.LENGTH_SHORT).show();
////                    GodownID = godownCheckBoxList.get(position).getID();
////                    Godown = godownCheckBoxList.get(position).getTitle();
//                }
//                @Override
//                public void onNothingSelected(AdapterView<?> parent) {}
//            });
        }
        //TODO: Item Category
        spnItemCategory.setSelection(1);
        spnItemCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ItemCategory = parent.getSelectedItemPosition();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
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
        //TODO: Greater than Or Less than Spinner
        spnGreaterOrLessthan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                greaterOrLessthan = parent.getSelectedItemPosition();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Filter Apply
                if (!ShowroomID.isEmpty() && !GodownID.isEmpty()){
                    if (StaticValues.viewFlag == 1 || StaticValues.editFlag == 1 || StaticValues.createFlag == 1 || StaticValues.removeFlag == 1 || StaticValues.printFlag == 1 || StaticValues.importFlag == 1 || StaticValues.exportFlag == 1){
                        CallApiMethod(ShowroomID,GodownID,String.valueOf(ItemCategory),String.valueOf(colorSizeStockQty), String.valueOf(greaterOrLessthan));
                        dialog.dismiss();
                    }else {
                        MessageDialog.MessageDialog(context,"Alert","You don't have permission of this module");
                    }
                }else{
                    if (ShowroomID.isEmpty()){
                        MessageDialog.MessageDialog(context,"Alert","Please select showroom");
                    }else if (GodownID.isEmpty()){
                        MessageDialog.MessageDialog(context,"Alert","Please select godown");
                    }
                }
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
    private void CallVolleyShowroomItemCheck(final String DeviceID, final String UserID,final String SessionID,final String DivisionID,final String CompanyID,final String BranchID,final String ShowroomID,final String GodownID,final String ItemCategory,final String StockQty,final String StockType){
        showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"ShowroomVsGodownCompareItem", new Response.Listener<String>()
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
                        List<Map<String,String>> mapList = new ArrayList<>();
                        JSONArray jsonArrayResult = jsonObject.getJSONArray("Result");
                        for (int i = 0; i<jsonArrayResult.length(); i++) {
                            Map<String,String> map = new HashMap<>();
                            map.put("MainGroupID",jsonArrayResult.getJSONObject(i).getString("MainGroupName"));
                            map.put("MainGroupName",jsonArrayResult.getJSONObject(i).getString("MainGroupName"));
                            map.put("GroupID",jsonArrayResult.getJSONObject(i).getString("GroupName"));
                            map.put("GroupName",jsonArrayResult.getJSONObject(i).getString("GroupName"));
                            map.put("SubGroupID",jsonArrayResult.getJSONObject(i).getString("SubGroupName"));
                            map.put("SubGroupName",jsonArrayResult.getJSONObject(i).getString("SubGroupName"));
                            map.put("ItemID",jsonArrayResult.getJSONObject(i).getString("ItemID"));
                            map.put("ItemName",jsonArrayResult.getJSONObject(i).getString("ItemName"));
                            map.put("ItemCode",jsonArrayResult.getJSONObject(i).getString("ItemCode"));
                            map.put("ColorID",(jsonArrayResult.getJSONObject(i).optString("ColorID")==null || jsonArrayResult.getJSONObject(i).optString("ColorID").equals("null") ? "" : jsonArrayResult.getJSONObject(i).optString("ColorID")));
                            map.put("Color",(jsonArrayResult.getJSONObject(i).optString("Color")==null || jsonArrayResult.getJSONObject(i).optString("Color").equals("null") ? "" : jsonArrayResult.getJSONObject(i).optString("Color")));
                            map.put("Stock",jsonArrayResult.getJSONObject(i).getString("Stock"));
                            map.put("GodownID",jsonArrayResult.getJSONObject(i).getString("GodownID"));
                            map.put("Godown",jsonArrayResult.getJSONObject(i).getString("Godown"));
                            map.put("SubItemID",(jsonArrayResult.getJSONObject(i).optString("SubItemID")==null || jsonArrayResult.getJSONObject(i).optString("SubItemID").equals("null") ? "" : jsonArrayResult.getJSONObject(i).optString("SubItemID")));
                            map.put("SubItemName",(jsonArrayResult.getJSONObject(i).optString("SubItemName")==null || jsonArrayResult.getJSONObject(i).optString("SubItemName").equals("null") ? "" : jsonArrayResult.getJSONObject(i).optString("SubItemName")));
                            map.put("SubItemCode",(jsonArrayResult.getJSONObject(i).optString("SubItemCode")==null || jsonArrayResult.getJSONObject(i).optString("SubItemCode").equals("null") ? "" : jsonArrayResult.getJSONObject(i).optString("SubItemCode")));
                            map.put("ItemSubItemStock",jsonArrayResult.getJSONObject(i).getString("ItemSubItemStock"));
                            map.put("SubItemApplicable",jsonArrayResult.getJSONObject(i).getString("SubItemApplicable"));
                            map.put("MDApplicable",jsonArrayResult.getJSONObject(i).getString("MDApplicable"));
                            mapList.add(map);
                        }
                        context.deleteDatabase(DBSqlLiteHandlerShowroomItemCheck.DATABASE_NAME);
                        DBSqlLiteHandlerShowroomItemCheck DBHandler = new DBSqlLiteHandlerShowroomItemCheck(context);
                        DBHandler.deleteShowroomVsGodown();
                        DBHandler.insertShowroomReportData(mapList);
                        if (!DBHandler.getGroupList().isEmpty()) {
                            LoadRecyclerView(DBHandler.getGroupList());
                        }else{
                            LoadRecyclerView(new ArrayList<Group>());
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
                params.put("BranchID", BranchID);
                params.put("ShowroomID", ShowroomID);
                params.put("GodownID", GodownID);
                params.put("ItemCategory", ItemCategory);
                params.put("StockQty", StockQty);
                params.put("StockType", StockType);
                Log.d(TAG,"Showroom Vs Godown Compare parameters:"+params.toString());
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
    private void LoadRecyclerView(List<Group> groupList){
        adapter = new GroupAdapter(context,groupList);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Group dataset = (Group) adapter.getItem(position);
                Intent intent = new Intent(context, ItemDetailsListActivity.class);
                intent.putExtra("Key",dataset);
                startActivity(intent);
            }
        });
    }
}