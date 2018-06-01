package inventory.analysis.catalogue.wishlist;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.singlagroup.AppController;
import com.singlagroup.LoginActivity;
import com.singlagroup.R;
import com.singlagroup.customwidgets.MessageDialog;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import DatabaseController.CommanStatic;
import inventory.analysis.catalogue.Database_Sqlite.DatabaseSqlLiteHandlerAllGroups;
import inventory.analysis.catalogue.Database_Sqlite.DatabaseSqlLiteHandlerWishlist;
import inventory.analysis.catalogue.wishlist.adapter.RecyclerWishListAdapter;
import inventory.analysis.catalogue.wishlist.dataset.RecyclerWishlistDataset;
import orderbooking.StaticValues;
import orderbooking.customerlist.datasets.CloseOrBookDataset;
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
    private DatabaseSqlLiteHandlerWishlist DBWishList;
    private static String TAG = WishListActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE) ;}
        setContentView(R.layout.recycler_item_details);
        //flag= StaticValues.SingleOrMultiWise;
        Initialization();
        ReceiveKeyByIntent();
        CallApiMethod(1);
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
        DBWishList = new DatabaseSqlLiteHandlerWishlist(context);
    }
    private void ReceiveKeyByIntent(){
        try{
            closeOrBookDataset = (CloseOrBookDataset) getIntent().getExtras().get("Key");
        }catch (Exception e){
            MessageDialog.MessageDialog(context,"Exception Intent",""+e.toString());
        }
    }
    private void CallApiMethod(final int flag){
        //TODO: CallRetrofitWishList
        String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
        if (!status.contentEquals("No Internet Connection")) {
            LoginActivity obj=new LoginActivity();
            String[] str = obj.GetSharePreferenceSession(context);
            if (str!=null) {
                //TODO: Call Volley Wishlist
                CallVolleyWishlist(str[3], str[0], str[14], str[4], str[5],str[3],str[4],"","","","0",CommanStatic.AppType,flag);
            }
        }else{
            Snackbar.make(recyclerView,status,Snackbar.LENGTH_LONG).show();
        }
        //TODO: Network Receiver
        NetworkChangeReceiver.bindNetworkListener(new NetworkListener() {
            @Override
            public void networkReceived(String status) {
                if (!status.contentEquals("No Internet Connection")) {
                    LoginActivity obj=new LoginActivity();
                    String[] str = obj.GetSharePreferenceSession(context);
                    if (str!=null) {
                        //TODO: Call Volley Wishlist
                        CallVolleyWishlist(str[3], str[0], str[14], str[4], str[5],str[3],str[4],"","","","0",CommanStatic.AppType,flag);
                    }
                }else{
                    Snackbar.make(recyclerView,status,Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }
    private void showDialog() {
        spotsDialog.show();
    }
    private void hideDialog() {
        spotsDialog.dismiss();
    }
    //TODO: CallRetrofitWishList
    private void CallVolleyWishlist(final String DeviceID, final String SessionID, final String CompanyID, final String UserID, final String DivisionID,final String ToDeviceID, final String ToUserID, final String PartyID, final String SubPartyID, final String RefName, final String MasterType, final String AppType,final int flag){
        showDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"SG_WishListItemsList", new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                // response
                Log.d("Response", response);
                DatabaseSqlLiteHandlerWishlist DBWishList = new DatabaseSqlLiteHandlerWishlist(getApplicationContext());
                DBWishList.WishlistTableDelete();
                List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    int Status=(jsonObject.getString("status")==null)?0:jsonObject.getInt("status");
                    String Msg=(jsonObject.getString("msg")==null)?"Server is not responding":jsonObject.getString("msg");
                    if (Status == 1) {
                        String[] Str = new String[2];
                        JSONArray jAResult = new JSONArray(jsonObject.getString("Result"));
                        DatabaseSqlLiteHandlerAllGroups DBGroups = new DatabaseSqlLiteHandlerAllGroups(getApplicationContext());
                        for (int i = 0; i < jAResult.length(); i++) {
                            Str = DBGroups.getGroupImage(jAResult.getJSONObject(i).optString("GroupID") == null ? "" : jAResult.getJSONObject(i).optString("GroupID"));
                            String Stock =  jAResult.getJSONObject(i).optDouble("Stock")+ " Pcs";
                            Map<String, String> map = new HashMap<>();
                            map.put("GroupID", jAResult.getJSONObject(i).optString("GroupID") == null ? "" : jAResult.getJSONObject(i).optString("GroupID"));
                            map.put("GroupName", jAResult.getJSONObject(i).optString("GroupName") == null ? "" : jAResult.getJSONObject(i).optString("GroupName"));
                            map.put("GroupImage", Str[0]);
                            map.put("MainGroup", Str[1]);
                            map.put("ItemID", jAResult.getJSONObject(i).optString("ItemID") == null ? "" : jAResult.getJSONObject(i).optString("ItemID"));
                            map.put("ItemCode", jAResult.getJSONObject(i).optString("ItemCode") == null ? "" : jAResult.getJSONObject(i).optString("ItemCode"));
                            map.put("ItemName", jAResult.getJSONObject(i).optString("ItemName") == null ? "" : jAResult.getJSONObject(i).optString("ItemName"));
                            map.put("ItemImage", jAResult.getJSONObject(i).optString("ImageUrl") == null ? "" : jAResult.getJSONObject(i).optString("ImageUrl"));
                            map.put("ItemStock", jAResult.getJSONObject(i).optDouble("Stock")+ " Pcs");
                            map.put("Rate", "₹"+jAResult.getJSONObject(i).optInt("Rate"));
                            map.put("TotalColor", ""+jAResult.getJSONObject(i).optInt("TotalColor"));
                            map.put("Unit", jAResult.getJSONObject(i).optString("Unit") == null ? "Pcs" : jAResult.getJSONObject(i).optString("Unit"));
                            mapList.add(map);
                        }
                        DBWishList.insertWishListTable(mapList);
                        LoadWishListData();
                    } else {
                        if (flag == 1) {    MessageDialog.MessageDialog(context,"",Msg);    }
                        DBWishList.insertWishListTable(mapList);
                        LoadWishListData();
                    }
                }catch (Exception e){
                    MessageDialog.MessageDialog(context,"Exception",""+e.toString());
                }
                hideDialog();
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", ""+error);
                MessageDialog.MessageDialog(context,"Error",""+error.toString());
                hideDialog();
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
                params.put("ToDeviceID", ToDeviceID);
                params.put("ToUserID", ToUserID);
                params.put("PartyID", PartyID);
                params.put("SubPartyID", SubPartyID);
                params.put("RefName", RefName);
                params.put("MasterType", MasterType);
                params.put("AppType", AppType);
                Log.d(TAG,"Wishlist Parameters:"+params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
    }
    private void LoadWishListData(){
        DatabaseSqlLiteHandlerWishlist DBWishList = new DatabaseSqlLiteHandlerWishlist(getApplicationContext());
        if (!GroupWishListActivity.PreGroupID.isEmpty() && GroupWishListActivity.PreGroupID!=null){
            ItemWishList(DBWishList.getWishlistItemsGroupBy(GroupWishListActivity.PreGroupID));
        }else {
            onBackPressed();
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
        invalidateOptionsMenu();
        //TODO: CallRetrofitWishList
       CallApiMethod(0);
    }
    @Override
    public void onPause(){
        super.onPause();
        invalidateOptionsMenu();
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
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
        MenuItem wishlist = menu.findItem(R.id.action_wishlist);// TODO: Wishlist Clear
        wishlist.setVisible(true);
        wishlist.setIcon(getResources().getDrawable(R.drawable.ic_action_wishlist_clear));
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
            case R.id.action_wishlist: //TODO: Wishlist Clear
                if (!GroupWishListActivity.PreGroupID.isEmpty() && GroupWishListActivity.PreGroupID!=null){
                    WishlistClearAlertDialog();
                }else {
                    MessageDialog.MessageDialog(context,"","No Item in this Wishlist Group");
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void WishlistClearAlertDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure?,You wanted to clear wishlist");
        alertDialogBuilder.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
                if (!status.contentEquals("No Internet Connection")) {
                    LoginActivity obj=new LoginActivity();
                    String[] str = obj.GetSharePreferenceSession(context);
                    if (str!=null) {
                        //TODO: Call Volley Wishlist Clear
                        CallVolleyWishlistClear(str[3], str[0], str[14], str[4], str[5], str[3], str[4],"","","","",""+GroupWishListActivity.PreGroupID,"","","","","","0",""+CommanStatic.AppType,"1");
                        dialog.dismiss();
                    }
                }else{
                    MessageDialog.MessageDialog(context,"",status);
                }
            }
        });
        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    private void CallVolleyWishlistClear(final String DeviceID, final String SessionID, final String CompanyID, final String UserID, final String DivisionID,final String ToDeviceID, final String ToUserID, final String ItemID, final String SubItemID, final String ColorID, final String SizeID, final String GroupID, final String PartyID, final String SubPartyID, final String RefName, final String Remarks, final String MasterID, final String MasterType, final String AppType, final String DelFlag){
        showDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"AddRemoveSG_WishListItem", new Response.Listener<String>()
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
                        Toast.makeText(context, ""+Msg, Toast.LENGTH_LONG).show();
                        CallApiMethod(1);
                    } else {
                        MessageDialog.MessageDialog(context,"",Msg);
                    }
                }catch (Exception e){
                    MessageDialog.MessageDialog(context,"Exception",""+e.toString());
                }
                hideDialog();
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", ""+error);
                MessageDialog.MessageDialog(context,"Error",""+error.toString());
                hideDialog();
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
                params.put("ToDeviceID", ToDeviceID);
                params.put("ToUserID", ToUserID);
                params.put("ItemID", ItemID);
                params.put("SubItemID", SubItemID);
                params.put("ColorID", ColorID);
                params.put("SizeID", SizeID);
                params.put("GroupID", GroupID);
                params.put("PartyID", PartyID);
                params.put("SubPartyID", SubPartyID);
                params.put("RefName", RefName);
                params.put("Remarks", Remarks);
                params.put("MasterID", MasterID);
                params.put("MasterType", MasterType);
                params.put("AppType", AppType);
                params.put("DelFlag", DelFlag);
                Log.d(TAG,"Wishlist clear Parameters:"+params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
    }
}
