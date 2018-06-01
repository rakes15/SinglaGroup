package report.showroomitemcheck;

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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import com.singlagroup.R;
import com.singlagroup.customwidgets.ItemClickSupport;
import com.singlagroup.customwidgets.MessageDialog;

import java.util.Map;

import DatabaseController.CommanStatic;
import DatabaseController.DatabaseSqlLiteHandlerUserInfo;
import report.DatabaseSqlite.DBSqlLiteHandlerShowroomItemCheck;
import report.showroomitemcheck.adapter.ItemDetailsAdapter;
import report.showroomitemcheck.model.Group;
import report.showroomitemcheck.model.ItemDetails;
import stockcheck.StockCheckActivity;

/**
 * Created by Rakesh on 13-Oct-16.
 */

public class ItemDetailsListActivity extends AppCompatActivity {
    ActionBar actionBar;
    private Context context;
    private RecyclerView recyclerView;
    private ItemDetailsAdapter adapter;
    private ProgressDialog progressDialog;
    private String GroupID="",MainGroupID="";
    private DBSqlLiteHandlerShowroomItemCheck DBHandler;
    private static String TAG = ItemDetailsListActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);} getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_recyclerview);
        Initialization();
        GetIntentMethod();
    }
    private void Initialization(){
        this.context = ItemDetailsListActivity.this;
        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        this.DBHandler = new DBSqlLiteHandlerShowroomItemCheck(context);
    }
    private void GetIntentMethod(){
        try {
            Group groupWise = (Group) getIntent().getExtras().get("Key");
            if(groupWise!=null) {
                GroupID = groupWise.getGroupID();
                MainGroupID = groupWise.getMainGroupID();
                actionBar.setTitle("" + groupWise.getGroupName() + " ( "+groupWise.getMainGroup() + " ) ");
                LoadRecyclerView(GroupID, MainGroupID);
            }else {
                MessageDialog.MessageDialog(ItemDetailsListActivity.this,"Intent","Dataset is null");
            }
        }catch (Exception e){
            MessageDialog.MessageDialog(ItemDetailsListActivity.this,"Exception",e.toString());
        }
    }
    private void LoadRecyclerView(String GroupID,String MainGroupID){
        //TODO: Load Recycler View
        adapter = new ItemDetailsAdapter(context,DBHandler.getItemDetailsByGroup(GroupID,MainGroupID));
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                ItemDetails dataset = (ItemDetails) adapter.getItem(position);
                //TODO: Stock Check Intent
                DatabaseSqlLiteHandlerUserInfo DBInfo = new DatabaseSqlLiteHandlerUserInfo(context);
                Map<String,String> map = DBInfo.getModulePermissionByVtype(11);
                if (map != null && !map.isEmpty()) {
                    Bundle bundle = new Bundle();
                    bundle.putString("ItemCode",dataset.getItemCode());
                    bundle.putString("Title",map.get("Name"));
                    bundle.putInt("ViewFlag",Integer.valueOf(map.get("ViewFlag")));
                    bundle.putInt("EditFlag",Integer.valueOf(map.get("EditFlag")));
                    bundle.putInt("CreateFlag",Integer.valueOf(map.get("CreateFlag")));
                    bundle.putInt("RemoveFlag",Integer.valueOf(map.get("RemoveFlag")));
                    bundle.putInt("PrintFlag",Integer.valueOf(map.get("PrintFlag")));
                    bundle.putInt("ImportFlag",Integer.valueOf(map.get("ImportFlag")));
                    bundle.putInt("ExportFlag",Integer.valueOf(map.get("ExportFlag")));
                    bundle.putInt("Vtype",Integer.valueOf(map.get("Vtype")));
                    Intent in = new Intent(context, StockCheckActivity.class);
                    in.putExtra("PermissionBundle", bundle);
                    startActivity(in);
                }
            }
        });
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
