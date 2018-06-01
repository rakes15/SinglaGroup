package report.forceclose;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.singlagroup.AppController;
import com.singlagroup.LoginActivity;
import com.singlagroup.R;
import com.singlagroup.customwidgets.DateFormatsMethods;
import com.singlagroup.customwidgets.ItemClickSupport;
import com.singlagroup.customwidgets.MessageDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DatabaseController.CommanStatic;
import orderbooking.StaticValues;
import orderbooking.catalogue.CatalogueActivity;
import orderbooking.customerlist.datasets.CloseOrBookDataset;
import orderbooking.customerlist.datasets.SelectCustomerWithSubCustomerForOrderDataset;
import report.DatabaseSqlite.DBSqlLiteForceClose;
import report.forceclose.adapter.ItemDetailsAdapter;
import report.forceclose.model.GroupWise;
import report.forceclose.model.ItemDetails;
import report.forceclose.model.OrderWise;
import report.forceclose.model.PartyWise;
import services.NetworkUtils;

/**
 * Created by Rakesh on 13-Oct-16.
 */

public class ItemDetailsListByAllWiseActivity extends AppCompatActivity {
    ActionBar actionBar;
    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView txtHeader;
    private RecyclerView recyclerView;
    private ItemDetailsAdapter adapter;
    private ProgressDialog progressDialog;
    private int sort=0,flag=0;
    private String GroupID="",PartyID="",OrderID="";
    private DBSqlLiteForceClose DBHandler;
    private static String TAG = ItemDetailsListByAllWiseActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);} getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_recyclerview_swipe_refresh_layout);
        Initialization();
        GetIntentMethod();
    }
    private void Initialization(){
        this.context = ItemDetailsListByAllWiseActivity.this;
        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_Refresh_Layout);
        txtHeader = (TextView) findViewById(R.id.text_showroom);//TODO: Header
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
            GroupWise groupWise = (GroupWise) getIntent().getExtras().get("Key");
            if(groupWise!=null) {
                GroupID = groupWise.getGroupID();
                PartyID = groupWise.getPartyID();
                OrderID = groupWise.getOrderID();
                actionBar.setTitle("" + groupWise.getGroupName() + " ( "+groupWise.getMainGroup() + " ) ");
                if (!groupWise.getPartyID().isEmpty() && !groupWise.getGroupID().isEmpty()) {
                    flag = 2;
                    LoadRecyclerViewPartyWise(groupWise.getGroupID(), PartyID, groupWise.getSubPartyID(),groupWise.getRefName(),flag);
                } else if (!groupWise.getOrderID().isEmpty() && !groupWise.getGroupID().isEmpty()) {
                    flag = 1;
                    LoadRecyclerView(groupWise.getGroupID(), PartyID ,OrderID,flag);
                } else if (!groupWise.getGroupID().isEmpty()) {
                    flag = 0;
                    LoadRecyclerView(groupWise.getGroupID(), PartyID , OrderID,flag);
                }
            }else {
                MessageDialog.MessageDialog(context,"Intent","Dataset is null");
            }
        }catch (Exception e){
            MessageDialog.MessageDialog(context,"Exception",e.toString());
        }
    }
    private void LoadRecyclerView(String GroupID, final String PartyID, final String OrderID, int flag){
        //TODO: Load Recycler View
       if (!OrderID.isEmpty() && OrderID!=null && !GroupID.isEmpty() && GroupID!=null) {
            adapter = new ItemDetailsAdapter(context,DBHandler.getItemListByOrderWise(GroupID,OrderID),flag);
            recyclerView.setAdapter(adapter);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
        }else if (!GroupID.isEmpty() && GroupID!=null) {
            adapter = new ItemDetailsAdapter(context,DBHandler.getItemListByGroupWise(GroupID,flag),flag);
            recyclerView.setAdapter(adapter);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            if (flag == 0) {
                txtHeader.setText("All");
                txtHeader.setVisibility(View.VISIBLE);
            }else if (flag == 1) {
                txtHeader.setText("Less than Stock");
                txtHeader.setVisibility(View.VISIBLE);
            }else if (flag == 2) {
                txtHeader.setText("Stock Available");
                txtHeader.setVisibility(View.VISIBLE);
            }
        }
        hideDialog();
    }
    private void LoadRecyclerViewPartyWise(String GroupID, final String PartyID, final String SubPartyID,String RefName, int flag){
        //TODO: Load Recycler View
        if (!PartyID.isEmpty() && PartyID!=null && !GroupID.isEmpty() && GroupID!=null) {
            adapter = new ItemDetailsAdapter(context,DBHandler.getItemListByPartyWise(GroupID,PartyID,SubPartyID,RefName),flag);
            recyclerView.setAdapter(adapter);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
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
            case R.id.action_logout://TODO: Sort (PendingQty > StockQty and PendingQty < StockQty)
                if (sort == 0){ //TODO: Sort (PendingQty > StockQty)
                    sort = 1;
                    LoadRecyclerView(GroupID,PartyID,OrderID,sort);
                }else if (sort == 1){ //TODO: Sort (PendingQty < StockQty)
                    sort = 2;
                    LoadRecyclerView(GroupID,PartyID,OrderID,sort);
                }else if (sort == 2){ //TODO: Sort (ItemCode ASC)
                    sort = 0;
                    LoadRecyclerView(GroupID,PartyID,OrderID,sort);
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
        MenuItem sortItem = menu.findItem(R.id.action_logout);//TODO: Sort Group Wise
        if ((PartyID.isEmpty() || PartyID==null) && (OrderID.isEmpty() || OrderID==null)) {
            sortItem.setVisible(true);
        }
        sortItem.setIcon(getResources().getDrawable(R.drawable.ic_action_sort));
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
