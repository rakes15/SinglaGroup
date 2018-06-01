package inventory.analysis.catalogue.wishlist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.singlagroup.AppController;
import com.singlagroup.BuildConfig;
import com.singlagroup.LoginActivity;
import com.singlagroup.R;
import com.singlagroup.customwidgets.FileOpenByIntent;
import com.singlagroup.customwidgets.MessageDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DatabaseController.CommanStatic;
import DatabaseController.DatabaseSqlLiteHandlerFilter;
import inventory.analysis.catalogue.Database_Sqlite.DatabaseSqlLiteHandlerAllGroups;
import inventory.analysis.catalogue.Database_Sqlite.DatabaseSqlLiteHandlerWishlist;
import inventory.analysis.catalogue.wishlist.adapter.RecyclerWishListGroupAdapter;
import inventory.analysis.catalogue.wishlist.dataset.RecyclerWishlistGroupDataset;
import inventory.analysis.catalogue.wishlist.dataset.ResponseWishListDataset;
import orderbooking.StaticValues;
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
public class GroupWishListActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    private ActionBar actionBar;
    private Context context;
    private ProgressDialog spotsDialog;
    private ProgressDialog pDialog;
    private DatabaseSqlLiteHandlerWishlist DBWishList;
    public static int groupflag = 0;
    public static String PreGroupID = "";
    private String FileExtn="";
    private CloseOrBookDataset closeOrBookDataset;
    private static String TAG = GroupWishListActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE) ;}
        setContentView(R.layout.recycler_item_details);
        Initialization();
        ReceiveKeyByIntent();
        CallApiMethod(1);
    }
    private void Initialization(){
        this.context = GroupWishListActivity.this;
        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Wishlist");
        recyclerView = (RecyclerView) findViewById(R.id.recycler_ItemDetails);
        spotsDialog = new ProgressDialog(context);
        spotsDialog.setMessage("Please wait...");
        spotsDialog.setCanceledOnTouchOutside(false);
        DBWishList = new DatabaseSqlLiteHandlerWishlist(context);
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Downloading file. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setMax(100);
        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pDialog.setCancelable(true);
    }
    private void ReceiveKeyByIntent(){
        try{
            //closeOrBookDataset = (CloseOrBookDataset) getIntent().getExtras().get("Key");
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
    //TODO: Call Volley Wishlist
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
        List<RecyclerWishlistGroupDataset> wishlist = DBWishList.getGroupList();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(GroupWishListActivity.this, 3);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerWishListGroupAdapter adapter = new RecyclerWishListGroupAdapter(GroupWishListActivity.this, wishlist, closeOrBookDataset);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(gridLayoutManager);
    }
    @Override
    public void onResume(){
        super.onResume();
        //TODO: CallRetrofitWishList
        CallApiMethod(0);
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_catalogue, menu);
        MenuItem itemGrid = menu.findItem(R.id.action_single_view);
        itemGrid.setVisible(false);
        MenuItem box = menu.findItem(R.id.action_box);
        box.setVisible(false);
        MenuItem wishlist = menu.findItem(R.id.action_wishlist);// TODO: Wishlist Clear
        wishlist.setVisible(true);
        wishlist.setIcon(getResources().getDrawable(R.drawable.ic_action_wishlist_clear));
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setVisible(false);
        MenuItem accountItem = menu.findItem(R.id.action_account); // TODO: PDf View and download
        accountItem.setVisible(true);
        accountItem.setIcon(getResources().getDrawable(R.drawable.ic_action_pdf));
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_wishlist: //TODO: Wishlist Clear
                if (!DBWishList.getGroupList().isEmpty()) {
                    WishlistClearAlertDialog();
                }else {
                    MessageDialog.MessageDialog(context,"","No Item in Wishlist");
                }
                break;
            case R.id.action_account: //TODO: Pdf
                String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
                if (!status.contentEquals("No Internet Connection")) {
                    LoginActivity obj=new LoginActivity();
                    String[] str = obj.GetSharePreferenceSession(context);
                    if (str!=null) {
                        //TODO: Call Volley Wishlist Clear
                        CallVolleyPdf(str[3], str[0], str[14], str[4], str[5], str[3], str[4],"","","","0");
                    }
                }else{
                    MessageDialog.MessageDialog(context,"",status);
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
                        CallVolleyWishlistClear(str[3], str[0], str[14], str[4], str[5], str[3], str[4],"","","","","","","","","","","0",""+CommanStatic.AppType,"1");
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
    private void CallVolleyPdf(final String DeviceID, final String SessionID, final String CompanyID, final String UserID, final String DivisionID,final String ToDeviceID, final String ToUserID, final String PartyID, final String SubPartyID, final String RefName, final String MasterType){
        showDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"SG_WishListItemsPDFLink", new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                // response
                Log.d("Response", response);
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    int Status=(jsonObject.getString("status")==null)? 0 :jsonObject.getInt("status");
                    String Msg=(jsonObject.getString("msg")==null)?"Server is not responding":jsonObject.getString("msg");
                    if (Status == 1) {
                        JSONObject jObjResult = jsonObject.getJSONObject("Result");
                        String Pdflink = (jObjResult.optString("Pdflink")==null ? "" : jObjResult.optString("Pdflink"));
                        if (!Pdflink.isEmpty()) {
                            //TODO: starting new Async Task
                            new DownloadFileFromURL().execute(Pdflink);
                        }else{
                            MessageDialog.MessageDialog(context,"","No record found");
                        }
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
                params.put("UserID", UserID);
                params.put("SessionID", SessionID);
                params.put("DivisionID", DivisionID);
                params.put("CompanyID", CompanyID);
                params.put("ToDeviceID", ToDeviceID);
                params.put("ToUserID", ToUserID);
                params.put("PartyID", PartyID);
                params.put("SubPartyID", SubPartyID);
                params.put("RefName", RefName);
                params.put("MasterType", MasterType);
                Log.d(TAG,"SG_WishListItemsPDFLink parameters:"+params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
    }

    //TODO: Download Report in PDF
    class DownloadFileFromURL extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showPDialog();
        }
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // getting file length
                int lenghtOfFile = conection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                String Path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SinglaGroups/temp.pdf";
                // Output stream to write file
                OutputStream output = new FileOutputStream(Path);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress(""+(int)((total*100)/lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", ""+e.toString());
            }

            return null;
        }
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }
        @Override
        protected void onPostExecute(String file_url) {
            hidePDialog();
            DialogViewShareOption();
        }

    }
    private void showPDialog() {
        if(pDialog!=null) {
            pDialog.show();
        }
    }
    private void hidePDialog() {
        if(pDialog!=null) {
            pDialog.dismiss();
        }
    }

    //TODO: View and Share option
    private void DialogViewShareOption(){
        final Dialog dialog = new Dialog(new ContextThemeWrapper(context, R.style.DialogSlideAnim));
        dialog.setContentView(R.layout.dialog_document_attachment_selection);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        RadioGroup radioGroupChangeOrShowroom = (RadioGroup) dialog.findViewById(R.id.RadioGroup_document_attachment_selection);
        RadioButton rbView = (RadioButton) dialog.findViewById(R.id.RadioButton_transaction);
        RadioButton rbShare = (RadioButton) dialog.findViewById(R.id.RadioButton_master);
        rbView.setText("View");
        rbView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_view_grey,0,0,0);
        rbShare.setText("Whats app");
        rbShare.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_whats_app,0,0,0);
        radioGroupChangeOrShowroom.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(radioGroup.getCheckedRadioButtonId()==R.id.RadioButton_transaction){
                    //TODO: View
                    //TODO: View PDF
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SinglaGroups/temp.pdf";
                    File file = new File(path);
                    if (file.exists()) {
                        Uri uri = null;
                        // So you have to use Provider
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
                        }else{
                            uri = Uri.fromFile(file);
                        }
                        FileOpenByIntent.FileOpen(context,path,uri);
                    }else{
                        MessageDialog.MessageDialog(context,"","File not exist");
                    }
                    dialog.dismiss();
                }else if(radioGroup.getCheckedRadioButtonId()==R.id.RadioButton_master){
                    //TODO: Share whats app
                    //TODO: View PDF
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SinglaGroups/temp.pdf";
                    File file = new File(path);
                    if (file.exists()) {
                        Uri uri = null;
                        // So you have to use Provider
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
                        }else{
                            uri = Uri.fromFile(file);
                        }
                        //FileOpenByIntent.(context,path,uri);
                        Intent intent1 = new Intent();
                        intent1.setAction(Intent.ACTION_SEND);
                        intent1.putExtra(Intent.EXTRA_STREAM, uri);
                        intent1.setType("application/pdf");
                        intent1.setPackage("com.whatsapp");
                        //intent1.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(intent1);

                    }else{
                        MessageDialog.MessageDialog(context,"","File not exist");
                    }
                    dialog.dismiss();
                }
            }
        });
    }
}
