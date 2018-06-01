package orderbooking.catalogue.wishlist;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;
import com.singlagroup.R;
import com.singlagroup.customwidgets.MessageDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import DatabaseController.CommanStatic;
import DatabaseController.DatabaseSqlLiteHandlerAllGroups;
import DatabaseController.DatabaseSqlLiteHandlerWishlist;
import orderbooking.StaticValues;
import orderbooking.catalogue.wishlist.adapter.RecyclerWishListAdapter;
import orderbooking.catalogue.wishlist.dataset.RecyclerWishlistDataset;
import orderbooking.catalogue.wishlist.dataset.ResponseWishListDataset;
import orderbooking.customerlist.datasets.CloseOrBookDataset;
import retrofit.ApiClient;
import retrofit.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import services.NetworkChangeReceiver;
import services.NetworkListener;
import services.NetworkUtils;
/**
 * Created by Rakesh on 28-April-17.
 */
public class WishListActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    public ActionBar actionBar;
    private Context context;
    private ProgressDialog spotsDialog;
    private CloseOrBookDataset closeOrBookDataset;
    private int flag=0;
    private static String TAG = WishListActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE) ;}
        setContentView(R.layout.recycler_item_details);
        //flag= StaticValues.SingleOrMultiWise;
        Initialization();
        ReceiveKeyByIntent();
        CallApiMethod();
    }
    private void Initialization(){
        this.context = WishListActivity.this;
        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Wishlist");
        recyclerView = (RecyclerView) findViewById(R.id.recycler_ItemDetails);
        spotsDialog = new ProgressDialog(context);
        spotsDialog.setMessage("Please wait...");
        spotsDialog.setCanceledOnTouchOutside(false);
    }
    private void ReceiveKeyByIntent(){
        try{
            closeOrBookDataset = (CloseOrBookDataset) getIntent().getExtras().get("Key");
        }catch (Exception e){
            MessageDialog.MessageDialog(context,"Exception Intent",""+e.toString());
        }
    }
    private void CallApiMethod(){
        //TODO: CallRetrofitWishList
        String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
        if (!status.contentEquals("No Internet Connection")) {
            if (!CommanStatic.DeviceID.isEmpty() && !CommanStatic.DevicePassword.isEmpty() && !CommanStatic.PriceListID.isEmpty()) {
                CallRetrofitWishList(CommanStatic.DeviceID, CommanStatic.DevicePassword, CommanStatic.PriceListID);
            }
        }else{
            Snackbar.make(recyclerView,status,Snackbar.LENGTH_LONG).show();
        }
        //TODO: Network Receiver
        NetworkChangeReceiver.bindNetworkListener(new NetworkListener() {
            @Override
            public void networkReceived(String status) {
                if (!status.contentEquals("No Internet Connection")) {
                    if (!CommanStatic.DeviceID.isEmpty() && !CommanStatic.DevicePassword.isEmpty() && !CommanStatic.PriceListID.isEmpty()) {
                        CallRetrofitWishList(CommanStatic.DeviceID, CommanStatic.DevicePassword, CommanStatic.PriceListID);
                    }
                }else{
                    Snackbar.make(recyclerView,status,Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }
    private void showpDialog() {
        spotsDialog.show();
    }
    private void hidepDialog() {
        spotsDialog.dismiss();
    }
    //TODO: CallRetrofitWishList
    private void CallRetrofitWishList(String AKey,String pwd,String PriceListID){
        showpDialog();
        final ApiInterface apiService =  ApiClient.getClient(CommanStatic.BaseUrl).create(ApiInterface.class);
        Map<String, String>  params = new HashMap<String, String>();
        params.put("AKey", AKey);
        params.put("pwd", pwd);
        params.put("PriceListID", PriceListID);
        Call<ResponseWishListDataset> call = apiService.getWishList(params);
        call.enqueue(new Callback<ResponseWishListDataset>() {
            @Override
            public void onResponse(Call<ResponseWishListDataset> call, retrofit2.Response<ResponseWishListDataset> response) {
                try {
                    if (response.isSuccessful()) {
                        int Status = response.body().getStatus();
                        String msg = response.body().getMsg();
                        if (Status == 1) {
                            List<Map<String, String>> mapList = response.body().getResult();
                            List<Map<String, String>> mapListTemp = new ArrayList<Map<String, String>>();
                            String[] Str = new String[2];
                            DatabaseSqlLiteHandlerAllGroups DBGroups = new DatabaseSqlLiteHandlerAllGroups(getApplicationContext());
                            for (int i = 0; i < mapList.size(); i++) {
                                Str = DBGroups.getGroupImage(mapList.get(i).get("GroupID"));
                                int stock = Integer.valueOf((mapList.get(i).get("ItemStock")==null ? "0" : mapList.get(i).get("ItemStock")));
                                String Stock = (StaticValues.stockFlag == 0) ? "" : ((StaticValues.stockFlag == 1) ? "" + stock + " " + mapList.get(i).get("Unit") : ((stock >= StaticValues.stockFormula) ? "> " + StaticValues.stockFormula : "" + stock + " " + mapList.get(i).get("Unit")));
                                int rate = Integer.valueOf((mapList.get(i).get("Rate")==null ? "0" : mapList.get(i).get("Rate")));
                                String Rate = (StaticValues.rateFlag == 0) ? "" : ((rate < 0) ? "Not available" : "₹" + rate);

                                Map<String, String> map = new HashMap<String, String>();
                                map.put("GroupID", mapList.get(i).get("GroupID"));
                                map.put("GroupName", mapList.get(i).get("GroupName"));
                                map.put("GroupImage", Str[0]);
                                map.put("MainGroup", Str[1]);
                                map.put("ItemID", mapList.get(i).get("ItemID"));
                                map.put("ItemCode", mapList.get(i).get("Itemcode"));
                                map.put("ItemName", mapList.get(i).get("ItemName"));
                                map.put("ItemImage", mapList.get(i).get("ItemImage"));
                                map.put("ItemStock", Stock);
                                map.put("Rate", Rate);
                                map.put("TotalColor", mapList.get(i).get("TotalColor"));
                                map.put("Unit", mapList.get(i).get("Unit"));
                                mapListTemp.add(map);
                            }
                            DatabaseSqlLiteHandlerWishlist DBWishList = new DatabaseSqlLiteHandlerWishlist(getApplicationContext());
                            DBWishList.WishlistTableDelete();
                            DBWishList.insertWishListTable(mapListTemp);
                            LoadWishListData();
                        } else {
                            Toast.makeText(getApplicationContext(), "" + msg, Toast.LENGTH_LONG).show();
                        }
                    }
                }catch (Exception e){
                    Log.e(TAG,"WishList Exception:"+e.getMessage());
                }
                hidepDialog();
            }

            @Override
            public void onFailure(Call<ResponseWishListDataset> call, Throwable t) {
                Log.e(TAG,"Failure: "+t.toString());
                Toast.makeText(getApplicationContext(),"WishList Failure:"+t.toString(),Toast.LENGTH_LONG).show();
                hidepDialog();
            }
        });
    }
    private void LoadWishListData(){
        DatabaseSqlLiteHandlerWishlist DBWishList = new DatabaseSqlLiteHandlerWishlist(getApplicationContext());
        if (!GroupWishListActivity.PreGroupID.isEmpty() && GroupWishListActivity.PreGroupID!=null){
            ItemWishList(DBWishList.getWishlistItemsGroupBy(GroupWishListActivity.PreGroupID));
        }else {
//            Intent intent = new Intent(getApplicationContext(),GroupBoxActivity.class);
//            startActivity(intent);
//            finish();
            onBackPressed();
//            List<RecyclerWishlistGroupDataset> wishlist = DBWishList.getGroupList();
//            GridLayoutManager gridLayoutManager = new GridLayoutManager(BoxActivity.this, 3);
//            gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//            RecyclerWishListGroupAdapter adapter = new RecyclerWishListGroupAdapter(BoxActivity.this, wishlist, recyclerView);
//            recyclerView.setAdapter(adapter);
//            recyclerView.setLayoutManager(gridLayoutManager);
        }
    }
    private void ItemWishList(List<RecyclerWishlistDataset> list){

        if (flag==0){

            GridLayoutManager gridLayoutManager=new GridLayoutManager(context,2);
            gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            RecyclerWishListAdapter adapter=new RecyclerWishListAdapter(context,list,flag,closeOrBookDataset);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(gridLayoutManager);
        }else  if (flag==1){
            GridLayoutManager gridLayoutManager=new GridLayoutManager(context,1);
            gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            RecyclerWishListAdapter adapter=new RecyclerWishListAdapter(context,list,flag,closeOrBookDataset);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(gridLayoutManager);
        }
    }
    @Override
    public void onStart(){
        super.onStart();
    }
    @Override
    public void onResume(){
        super.onResume();
        actionBar.invalidateOptionsMenu();
        //TODO: CallRetrofitWishList
        String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
        if (!status.contentEquals("No Internet Connection")) {
            if (!CommanStatic.DeviceID.isEmpty() && !CommanStatic.DevicePassword.isEmpty() && !CommanStatic.PriceListID.isEmpty()) {
                CallRetrofitWishList(CommanStatic.DeviceID, CommanStatic.DevicePassword, CommanStatic.PriceListID);
            }
        }else{
            Snackbar.make(recyclerView,status,Snackbar.LENGTH_LONG).show();
        }
    }
    @Override
    public void onPause(){
        super.onPause();
        actionBar.invalidateOptionsMenu();
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_catalogue, menu);
        MenuItem itemGrid = menu.findItem(R.id.action_single_view);
        if(flag==0) {
            itemGrid.setIcon(R.drawable.ic_action_single_view_white);
        }else if(flag==1) {
            itemGrid.setIcon(R.drawable.ic_action_multi_view_white);
        }
        MenuItem box = menu.findItem(R.id.action_box);
        box.setVisible(false);
        MenuItem wishlist = menu.findItem(R.id.action_wishlist);
        wishlist.setVisible(false);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setVisible(false);
        MenuItem accountItem = menu.findItem(R.id.action_account);
        accountItem.setVisible(false);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_single_view:
                DatabaseSqlLiteHandlerWishlist DBWishList = new DatabaseSqlLiteHandlerWishlist(getApplicationContext());
                if (!GroupWishListActivity.PreGroupID.isEmpty() && GroupWishListActivity.PreGroupID!=null){
                    if(flag==0) {
                        flag=1;
                        item.setIcon(R.drawable.ic_action_multi_view_white);
                        ItemWishList(DBWishList.getWishlistItemsGroupBy(GroupWishListActivity.PreGroupID));
                    }else if(flag==1) {
                        flag=0;
                        item.setIcon(R.drawable.ic_action_single_view_white);
                        ItemWishList(DBWishList.getWishlistItemsGroupBy(GroupWishListActivity.PreGroupID));
                    }
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
