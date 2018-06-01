package orderbooking.customerlist.partyinfo;

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
import android.view.WindowManager;

import com.singlagroup.R;
import com.singlagroup.customwidgets.MessageDialog;

import DatabaseController.CommanStatic;
import DatabaseController.DatabaseSqlLiteHandlerUserInfo;
import orderbooking.customerlist.partyinfo.adapter.PartyCommonDetailsListAdapter;
import orderbooking.customerlist.partyinfo.adapter.SubPartyCommonDetailsListAdapter;
import orderbooking.customerlist.partyinfo.model.PartyCompleteInfo;
import orderbooking.customerlist.partyinfo.model.SubPartyCompleteInfo;

/**
 * Created by Rakesh on 13-Oct-16.
 */

public class CommonDetailsListActivity extends AppCompatActivity {
    ActionBar actionBar;
    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private PartyCommonDetailsListAdapter partyadapter;
    private SubPartyCommonDetailsListAdapter subPartyadapter;
    private int SubPartyApplicable = 0;
    private ProgressDialog progressDialog;
    private DatabaseSqlLiteHandlerUserInfo DBHandler;
    private static String TAG = CommonDetailsListActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);} getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_recyclerview_swipe_refresh_layout);
        Initialization();
        GetIntentMethod();
    }
    private void Initialization(){
        this.context = CommonDetailsListActivity.this;
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
                GetIntentMethod();
            }
        });
    }
    private void GetIntentMethod(){
        try{
            Intent intent = getIntent();
            Bundle args = intent.getBundleExtra("Bundle");
            String Title = args.getString("Title","Singla Groups");
            SubPartyApplicable = args.getInt("SubPartyApplicable",0);

            actionBar.setTitle(Title);
            if (SubPartyApplicable == 0) {
                PartyCompleteInfo.SalesInfo SalesInfo = (PartyCompleteInfo.SalesInfo) args.getSerializable("Content");
                partyadapter = new PartyCommonDetailsListAdapter(context, Title, SalesInfo);
                recyclerView.setAdapter(partyadapter);
            }else{
                SubPartyCompleteInfo.SalesInfo SalesInfo = (SubPartyCompleteInfo.SalesInfo) args.getSerializable("Content");
                subPartyadapter = new SubPartyCommonDetailsListAdapter(context, Title, SalesInfo);
                recyclerView.setAdapter(subPartyadapter);
            }
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            swipeRefreshLayout.setRefreshing(false);
        }catch (Exception e){
            MessageDialog.MessageDialog(context,"",""+e.toString());
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setVisible(false);
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
                if(partyadapter!=null) {
                    //partyadapter.filter(newText);
                }
//                System.out.println("on text chnge text: "+newText);
                return true;
            }
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                // this is your adapter that will be filtered
                if(partyadapter!=null)
                {
                    //partyadapter.filter(query);
                }
//                System.out.println("on query submit: "+query);
                return true;
            }
        };
        searchView.setOnQueryTextListener(textChangeListener);
        return super.onCreateOptionsMenu(menu);
    }
}
