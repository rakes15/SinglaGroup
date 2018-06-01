package orderbooking.catalogue;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.singlagroup.LoginActivity;
import com.singlagroup.R;
import com.singlagroup.customwidgets.CommonSearchableSpinner;
import com.singlagroup.customwidgets.MessageDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DatabaseController.CommanStatic;
import DatabaseController.DatabaseSqlLiteHandlerAddToBox;
import DatabaseController.DatabaseSqlLiteHandlerAllGroups;
import DatabaseController.DatabaseSqlLiteHandlerFilter;
import DatabaseController.DatabaseSqlLiteHandlerWishlist;
import orderbooking.StaticValues;
import orderbooking.catalogue.adapter.RecyclerItemDataset;
import orderbooking.catalogue.adapter.RecyclerItemGridAdapter;
import orderbooking.catalogue.adapter.SearchRecyclerItemGridAdapter;
import orderbooking.catalogue.addtobox.adapter.RecyclerBoxGroupAdapter;
import orderbooking.catalogue.addtobox.dataset.RecyclerBoxGroupDataset;
import orderbooking.catalogue.addtobox.dataset.ResponseBoxListDataset;
import orderbooking.catalogue.responsedataset.ResponseItemListMainDataset;
import orderbooking.catalogue.responsedataset.ResponseItemListSubDataset;
import orderbooking.catalogue.wishlist.GroupWishListActivity;
import orderbooking.catalogue.wishlist.adapter.RecyclerWishListGroupAdapter;
import orderbooking.catalogue.wishlist.dataset.RecyclerWishlistGroupDataset;
import orderbooking.catalogue.wishlist.dataset.ResponseWishListDataset;
import orderbooking.customerlist.datasets.CloseOrBookDataset;
import orderbooking.customerlist.temp.BookOrderAdapter;
import orderbooking.view_order_details.OrderViewOrPushActivity;
import retrofit.ApiClient;
import retrofit.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import services.NetworkChangeReceiver;
import services.NetworkListener;
import services.NetworkUtils;
import stockcheck.StockCheckActivity;

/**
 * Created by Rakesh on 21-Feb-17.
 */
public class SearchedItemListActivity extends AppCompatActivity {
    private Context context;
    private ActionBar actionBar;
    private CloseOrBookDataset closeOrBookDataset;
    RecyclerView gridViewItemList;
    ProgressBar progressBar;
    TextView txtViewGroupName,txtViewTotalStyle;
    Button btnFilter,btnSort;
    DatabaseSqlLiteHandlerFilter DBHandler;
    int priceFlag=0,stockFlag=0;
    int flag=0,v=0,filterLoadStatus=0,searchFlag=0;
    int minSeekVal=0,maxSeekVal=0,minSeekStock=0,maxSeekStock=0;
    //List<Map<String,String>> mapListTemp=null;
    List<RecyclerItemDataset> DatasetList;
    int TotalStyle=0;
    ListView listViewTab,listViewCheckBox;
    String[] strFilter=new String[12];
    private int lastItem=0;
    public static int start=1,end=30;
    GridLayoutManager gridLayoutManager;
    SearchRecyclerItemGridAdapter recyclerItemGridAdapter;
    String Keyword="",MainGroupID="",GroupID="",Search="",PartyID="",SubPartyID="",RefName="";;
    private static String TAG = SearchedItemListActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);} getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_itemlist);
        Initialization();
        GetDataByIntentMethod();
        DatasetList = new ArrayList<RecyclerItemDataset>();
        //TODO: AsyncItemList Execute
        String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
        if (!status.contentEquals("No Internet Connection")) {
            //TODO: CallRetrofit Item List
            if(!CommanStatic.DeviceID.isEmpty() && !CommanStatic.DevicePassword.isEmpty() && !CommanStatic.PriceListID.isEmpty()) {
                CallRetrofit(CommanStatic.DeviceID, CommanStatic.DevicePassword, MainGroupID,GroupID, CommanStatic.PriceListID, String.valueOf(start), String.valueOf(end), "1",Search);
            }
        }else{
            Snackbar.make(gridViewItemList,status+"",Snackbar.LENGTH_LONG).show();
        }
        //TODO: Network Receiver
        NetworkChangeReceiver.bindNetworkListener(new NetworkListener() {
            @Override
            public void networkReceived(String status) {
                if (!status.contentEquals("No Internet Connection")) {
                    //TODO: CallRetrofit Item List
                    if(!CommanStatic.DeviceID.isEmpty() && !CommanStatic.DevicePassword.isEmpty() && !CommanStatic.PriceListID.isEmpty()) {
                        CallRetrofit(CommanStatic.DeviceID, CommanStatic.DevicePassword, MainGroupID,GroupID, CommanStatic.PriceListID, String.valueOf(start), String.valueOf(end), "1",Search);
                    }
                }else{
                    Snackbar.make(gridViewItemList,status+"",Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }
    private void Initialization(){
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        gridViewItemList = (RecyclerView) findViewById(R.id.recyclerGrid);
        progressBar = (ProgressBar) findViewById(R.id.progressBar_Circle);
        txtViewGroupName = (TextView) findViewById(R.id.textView_GroupName);
        txtViewGroupName.setVisibility(View.GONE);
        txtViewTotalStyle = (TextView) findViewById(R.id.textView_TotalStyle);
    }
    private void GetDataByIntentMethod(){
        try {
            MainGroupID = getIntent().getExtras().getString("MainGroupID","");
            GroupID = getIntent().getExtras().getString("GroupID","");
            Search = getIntent().getExtras().getString("SearchQuery","");
            closeOrBookDataset = (CloseOrBookDataset) getIntent().getExtras().get("PartyDetails");
            if (closeOrBookDataset != null) {
                PartyID = closeOrBookDataset.getPartyID();
                CommanStatic.PriceListID = closeOrBookDataset.getPricelistID();
                //SubPartyID = selectCustomerForOrderDataset.getPartyID();
                //RefName = selectCustomerForOrderDataset.getPartyID();
                actionBar.setTitle(closeOrBookDataset.getPartyName());
            } else {
                MessageDialog.MessageDialog(context, "Intent", "Get intent is null");
            }
        }catch (Exception e){
            MessageDialog.MessageDialog(context,"Intent Exception",""+e.toString());
        }
    }
    //TODO:Asyc MainGroup
    private void CallRetrofit(String AKey, String Password,String MainGroupID, String GroupID, String PriceListID, String start, String end, String TSFlg, String Search){
        showpDialog();
        ApiInterface apiService =  ApiClient.getClient(CommanStatic.BaseUrl).create(ApiInterface.class);
        Map<String, String>  params = new HashMap<String, String>();
        params.put("AKey", AKey);
        params.put("pwd", Password);
        params.put("MainGroupID", MainGroupID);
        params.put("GroupID", GroupID);
        params.put("PriceListID", PriceListID);
        params.put("Start", start);
        params.put("End", end);
        params.put("TSFlg", TSFlg);
        params.put("Search", Search);
        Log.d(TAG,"Parameters: "+params.toString());
        Call<ResponseItemListMainDataset> call = apiService.getSearchItemList(params);
        call.enqueue(new Callback<ResponseItemListMainDataset>() {
            @Override
            public void onResponse(Call<ResponseItemListMainDataset> call, retrofit2.Response<ResponseItemListMainDataset> response) {
                try {
                    TotalStyle = 0;
                    DatasetList = new ArrayList<RecyclerItemDataset>();
                    if (response.isSuccessful()) {
                        int status = response.body().getStatus();
                        String msg = response.body().getMsg();
                        if (status == 1) {
                            ResponseItemListSubDataset subDataset = response.body().getResult();
                            TotalStyle = subDataset.getTotalStyle();
                            DatasetList = subDataset.getItemList();
                            ItemViewChanger(DatasetList, TotalStyle);
                        } else {
                            Toast.makeText(getApplicationContext(), "" + msg, Toast.LENGTH_LONG).show();
                        }
                    }else {
                        ItemViewChanger(DatasetList, TotalStyle);
                        Snackbar.make(gridViewItemList,""+response.message()+" Response:"+response.code(),Snackbar.LENGTH_LONG).show();
                    }
                    Log.e(TAG,"ItemList :"+DatasetList.get(0).getItemcode().toString());
                }catch (Exception e){
                    Log.e(TAG,"ItemList Exception:"+e.getMessage());
                }
                hidepDialog();
            }
            @Override
            public void onFailure(Call<ResponseItemListMainDataset> call, Throwable t) {
                Log.e(TAG,"Failure:"+t.toString());
                Toast.makeText(getApplicationContext(),"Item List Failure",Toast.LENGTH_LONG).show();
                hidepDialog();
            }
        });
    }
    private void CallRetrofitOnScroll(String AKey, String Password,String MainGroupID, String GroupID, String PriceListID, String start, String end, String TSFlg, String Search){
        showpDialog();
        ApiInterface apiService =  ApiClient.getClient(CommanStatic.BaseUrl).create(ApiInterface.class);
        Map<String, String>  params = new HashMap<String, String>();
        params.put("AKey", AKey);
        params.put("pwd", Password);
        params.put("MainGroupID", MainGroupID);
        params.put("GroupID", GroupID);
        params.put("PriceListID", PriceListID);
        params.put("Start", start);
        params.put("End", end);
        params.put("TSFlg", TSFlg);
        params.put("Search", Search);
        Log.d(TAG,"Parameters: "+params.toString());
        Call<ResponseItemListMainDataset> call = apiService.getSearchItemList(params);
        call.enqueue(new Callback<ResponseItemListMainDataset>() {
            @Override
            public void onResponse(Call<ResponseItemListMainDataset> call, retrofit2.Response<ResponseItemListMainDataset> response) {
                try {
                    if(response.body()!=null) {
                        int status = response.body().getStatus();
                        String msg = response.body().getMsg();
                        if (status == 1) {
                            ResponseItemListSubDataset subDataset = response.body().getResult();
                            DatasetList.addAll(subDataset.getItemList());
                            int curSize = recyclerItemGridAdapter.getItemCount();
                            recyclerItemGridAdapter.notifyItemRangeInserted(curSize, DatasetList.size() - 1);
                        } else {
                            Toast.makeText(getApplicationContext(), "" + msg, Toast.LENGTH_LONG).show();
                        }
                    }
                }catch (Exception e){
                    Log.e(TAG,"ItemList Exception:"+e.getMessage());
                }
                hidepDialog();
            }

            @Override
            public void onFailure(Call<ResponseItemListMainDataset> call, Throwable t) {
                Log.e(TAG,"Failure:"+t.toString());
                Toast.makeText(getApplicationContext(),"Item List Failure",Toast.LENGTH_LONG).show();
                hidepDialog();
            }
        });
    }
    private void showpDialog() {
        progressBar.setVisibility(View.VISIBLE);
    }
    private void hidepDialog() {
        progressBar.setVisibility(View.GONE);
    }
    @Override
    public void onResume(){
        super.onResume();
        actionBar.invalidateOptionsMenu();
    }
    @Override
    public void onPause(){
        super.onPause();
        actionBar.invalidateOptionsMenu();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_catalogue, menu);
        MenuItem menuItem = menu.findItem(R.id.action_account);
        menuItem.setVisible(false);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
//        MenuItemCompat.setOnActionExpandListener(searchItem, new SearchViewExpandListener(this));
        MenuItemCompat.setActionView(searchItem, searchView);
        searchView.setQueryHint("Search...");
        SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextChange(String newText){
                // this is your adapter that will be filtered
                return true;
            }
            @Override
            public boolean onQueryTextSubmit(String query){
                // this is your adapter that will be filtered
                //recyclerItemGridAdapter.getFilter().filter(query);
                StaticValues.SearchQuery="";
                StaticValues.SearchQuery=query;
                final String MacID = CommanStatic.MAC_ID;
                //TODO: AsyncItemList Execute
//                if(cd.isConnectingToInternet()==true) {
//                    new AsyncItemList().execute("'" + MacID + "'", String.valueOf(start), String.valueOf(end));
//                }else{
//                    setContentView(R.layout.no_internet_connection);
//                    ImageView imageViewReload=(ImageView)findViewById(R.id.imageView_Reload);
//                    imageViewReload.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            new AsyncItemList().execute("'" + MacID + "'", String.valueOf(start), String.valueOf(end));
//                        }
//                    });
//                }
                return true;
            }
        };
        searchView.setOnQueryTextListener(textChangeListener);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                StaticValues.SearchQuery="";
                final String MacID = CommanStatic.MAC_ID;
                //TODO: AsyncItemList Execute
//                if(cd.isConnectingToInternet()==true) {
//                    new AsyncItemList().execute("'" + MacID + "'", String.valueOf(start), String.valueOf(end));
//                }else{
//                    setContentView(R.layout.no_internet_connection);
//                    ImageView imageViewReload=(ImageView)findViewById(R.id.imageView_Reload);
//                    imageViewReload.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            new AsyncItemList().execute("'" + MacID + "'", String.valueOf(start), String.valueOf(end));
//                        }
//                    });
//                }
                return false;
            }
        });
        MenuItem box = menu.findItem(R.id.action_box);
        CatalogueActivity.iconBox = (LayerDrawable) box.getIcon();
        // Update LayerDrawable's BadgeDrawable
        Utils.setBadgeCount(SearchedItemListActivity.this, CatalogueActivity.iconBox, StaticValues.totalBoxCount);

        MenuItem wishlist = menu.findItem(R.id.action_wishlist);
        CatalogueActivity.iconWishlist = (LayerDrawable) wishlist.getIcon();
        // Update LayerDrawable's BadgeDrawable
        Utils.setBadgeCount(SearchedItemListActivity.this, CatalogueActivity.iconWishlist, StaticValues.totalWishlistCount);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case android.R.id.home:
//                 Intent intent = new Intent(SearchedItemListActivity.this, MainGroupOrGroupActivity.class);
//                 startActivity(intent);
                 overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                 finish();
                 Utils.setBadgeCount(SearchedItemListActivity.this, CatalogueActivity.iconWishlist, StaticValues.totalWishlistCount);
                 Utils.setBadgeCount(SearchedItemListActivity.this, CatalogueActivity.iconBox, StaticValues.totalBoxCount);
                 break;
            case R.id.action_search:

                break;
            case R.id.action_single_view:
                if(flag==0 && !DatasetList.isEmpty())
                {
                    flag=1;
                    ItemViewChanger(DatasetList,TotalStyle);
                    item.setIcon(R.drawable.ic_multi_view_light);
                }
                else if (flag==1 && !DatasetList.isEmpty())
                {
                    flag=0;
                    ItemViewChanger(DatasetList,TotalStyle);
                    item.setIcon(R.drawable.ic_single_view_light);
                }
                 break;
            case R.id.action_wishlist:

                if (!BookOrderAdapter.listMultiCustomer.isEmpty()) {
                    //StaticValues.PushOrderFlag = 2; //TODO: For Catalogue booking
                    Intent intent = new Intent(context, GroupWishListActivity.class);
                    intent.putExtra("Key",closeOrBookDataset);
                    startActivity(intent);
                }else{
                    MessageDialog.MessageDialog(context,"","Please select Running Customer first");
                }
                 break;
            case R.id.action_box:
                if (!BookOrderAdapter.listMultiCustomer.isEmpty()) {
                    StaticValues.PushOrderFlag = 2; //TODO: For Catalogue booking
                    Intent intent = new Intent(context, OrderViewOrPushActivity.class);
                    startActivity(intent);
                }else{
                    MessageDialog.MessageDialog(context,"","Please select ruuning Customer first");
                }
                 //CustomDialogBox(1,"My Box", 1);
                 break;
//            case R.id.action_account:
//                DialogLoginCatalogue(SearchedItemListActivity.this);
//                break;
        }
        return super.onOptionsItemSelected(item);
    }
    // Append more data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    private void ItemViewChanger(final List<RecyclerItemDataset> list, final int totalstyle){
        recyclerItemGridAdapter=new SearchRecyclerItemGridAdapter(SearchedItemListActivity.this,list,flag,closeOrBookDataset);
        Context context= SearchedItemListActivity.this;
        gridViewItemList.setAdapter(recyclerItemGridAdapter);
        if(flag==0){
            gridLayoutManager=new GridLayoutManager(context,2);
            gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
            gridViewItemList.setLayoutManager(gridLayoutManager);
            gridViewItemList.setHasFixedSize(true);
            gridViewItemList.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if(searchFlag==0){
                        String totalStyle = (list.size() == 0) ? "0 Style" : totalstyle + " Styles";
                        txtViewTotalStyle.setText(gridLayoutManager.findLastVisibleItemPosition() + 1 + "/" + totalStyle);
                    }else if(searchFlag==1){
                        txtViewTotalStyle.setText(gridLayoutManager.findLastVisibleItemPosition() + 1 + "/" +gridLayoutManager.getItemCount()+ " Styles");
                    }
                }
            });
            gridViewItemList.addOnScrollListener(new EndlessRecyclerViewScrollListener(gridLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount) {
                    final String GroupID = getIntent().getExtras().getString("GroupID");
                    start = end + 1;
                    end = end + 30;
                    //TODO: onScroll Execute filter
                    String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
                    if (!status.contentEquals("No Internet Connection")) {
                        if (!CommanStatic.DeviceID.isEmpty() && !CommanStatic.DevicePassword.isEmpty() && !CommanStatic.PriceListID.isEmpty()) {
                            CallRetrofitOnScroll(CommanStatic.DeviceID, CommanStatic.DevicePassword, MainGroupID,GroupID, CommanStatic.PriceListID, String.valueOf(start), String.valueOf(end), "1",Search);
                        }
                    }else{
                        Snackbar.make(gridViewItemList,status+"",Snackbar.LENGTH_LONG).show();
                    }
                }
            });
        }
        else if(flag==1) {
            gridLayoutManager = new GridLayoutManager(context, 1);
            gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
            gridViewItemList.setLayoutManager(gridLayoutManager);
            if(searchFlag==0){
                String totalStyle = (list.size() == 0) ? "0 Style" : totalstyle + " Styles";
                txtViewTotalStyle.setText("1/" + totalStyle);
            }else if(searchFlag==1){
                txtViewTotalStyle.setText(gridLayoutManager.findLastVisibleItemPosition() + 1 + "/" +gridLayoutManager.getItemCount()+ " Styles");
            }
            gridViewItemList.addOnScrollListener(new EndlessRecyclerViewScrollListener(gridLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount) {
                    final String GroupID = getIntent().getExtras().getString("GroupID");
                    start = end + 1;
                    end = end + 30;
                    //TODO: AsyncItemScroll Execute filter
                    String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
                    if (!status.contentEquals("No Internet Connection")) {
                        if(!CommanStatic.DeviceID.isEmpty() && !CommanStatic.DevicePassword.isEmpty() && !CommanStatic.PriceListID.isEmpty()) {
                            CallRetrofitOnScroll(CommanStatic.DeviceID, CommanStatic.DevicePassword, MainGroupID,GroupID, CommanStatic.PriceListID, String.valueOf(start), String.valueOf(end), "1",Search);
                        }
                    }else{
                        Snackbar.make(gridViewItemList,status+"",Snackbar.LENGTH_LONG).show();
                    }
                }
            });
        }
        if(list.isEmpty()){
            txtViewTotalStyle.setText("");
            Snackbar.make(gridViewItemList,"No result found"+Keyword,Snackbar.LENGTH_LONG).show();
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            Intent intent = new Intent(SearchedItemListActivity.this, MainGroupOrGroupActivity.class);
//            startActivity(intent);
            overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
            finish();
            Utils.setBadgeCount(SearchedItemListActivity.this, CatalogueActivity.iconWishlist, StaticValues.totalWishlistCount);
            Utils.setBadgeCount(SearchedItemListActivity.this, CatalogueActivity.iconBox, StaticValues.totalBoxCount);
            finish();
        }else if(keyCode == KeyEvent.KEYCODE_HOME ){
            Log.d("HomeKey","Home key pressed then restart app");
            finishAffinity();
        }
        return super.onKeyDown(keyCode, event);
    }
    private void DialogLoginCatalogue(Context context){
        Dialog dialog = new Dialog(new android.support.v7.view.ContextThemeWrapper(context, R.style.DialogSlideAnim));
        dialog.setContentView(R.layout.dialog_catalogue_login);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.TOP;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        //TODO: Declarations
        final LinearLayout linearLayoutSignIn = (LinearLayout)  dialog.findViewById(R.id.Linear_SignIn);
        final LinearLayout linearLayoutSignOut = (LinearLayout)  dialog.findViewById(R.id.Linear_SignOut);
        TextView textViewUserName = (TextView) dialog.findViewById(R.id.text_UserName);
        TextView textViewNewCustomer = (TextView) dialog.findViewById(R.id.text_new_customer);
        ProgressBar progressBar = (ProgressBar) dialog.findViewById(R.id.ProgressBar);
        CommonSearchableSpinner spnRunningCustomer = (CommonSearchableSpinner) dialog.findViewById(R.id.spinner_running_customer);
        final Button btnApply = (Button) dialog.findViewById(R.id.btn_apply);
        textViewUserName.setText("Rakesh Sharma");
        if(CatalogueActivity.loginFlag == 0){
            linearLayoutSignOut.setVisibility(View.GONE);
            linearLayoutSignIn.setVisibility(View.VISIBLE);
            btnApply.setText("Sign in");
        }else if(CatalogueActivity.loginFlag == 1){
            linearLayoutSignOut.setVisibility(View.VISIBLE);
            linearLayoutSignIn.setVisibility(View.GONE);
            btnApply.setText("Sign out");
        }
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CatalogueActivity.loginFlag==0) {
                    CatalogueActivity.loginFlag = 1;
                    linearLayoutSignOut.setVisibility(View.VISIBLE);
                    linearLayoutSignIn.setVisibility(View.GONE);
                    btnApply.setText("Sign out");
                }else if (CatalogueActivity.loginFlag==1){
                    CatalogueActivity.loginFlag = 0;
                    linearLayoutSignOut.setVisibility(View.GONE);
                    linearLayoutSignIn.setVisibility(View.VISIBLE);
                    btnApply.setText("Sign in");
                }
            }
        });
        LoginActivity obj=new LoginActivity();
        String[] str = obj.GetSharePreferenceSession(SearchedItemListActivity.this);
        String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
        if (!status.contentEquals("No Internet Connection")) {
            //CallRetrofitVoucherList(progressBar, spnVoucherType, textView, str[3], str[0], str[4], str[6], str[5]);
        }else{
            MessageDialog.MessageDialog(SearchedItemListActivity.this,"",status);
        }
    }
}
