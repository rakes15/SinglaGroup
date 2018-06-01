package uploadimagesfiles.voucherdocupload;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import com.singlagroup.LoginActivity;
import com.singlagroup.R;
import com.singlagroup.customwidgets.MessageDialog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import DatabaseController.CommanStatic;
import orderbooking.StaticValues;
import retrofit.ApiClient;
import retrofit.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import services.NetworkUtils;
import uploadimagesfiles.voucherdocupload.adapter.RecyclerViewAttachedListAdapter;
import uploadimagesfiles.voucherdocupload.datasets.MasterDocumentDataset;
import uploadimagesfiles.voucherdocupload.datasets.VoucherDocumentDataset;
import uploadimagesfiles.voucherdocupload.responsedatasets.ResponseMasterDocumentDataset;
import uploadimagesfiles.voucherdocupload.responsedatasets.ResponseVoucherDocumentDataset;

/**
 * Created by Rakesh on 14-Jan-17.
 */
public class AttachmentActivity extends AppCompatActivity {
    private static String TAG = AttachmentActivity.class.getSimpleName();
    private Context context;
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    private ActionBar actionBar;
    private RecyclerViewAttachedListAdapter adapter;
    private String VType,VHeading,Type="";
    public static String FromDate="",ToDate="";
    private VoucherDocumentDataset voucherDocumentDataset;
    private MasterDocumentDataset masterDocumentDataset;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);} getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_recyclerview);
        Initialization();
        GetIntentMethod();
        CallApiMethod();
    }
    private void Initialization(){
        this.context = AttachmentActivity.this;
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
    }
    private void GetIntentMethod(){
        try {
            Type = getIntent().getExtras().getString("Type","");
            VType = getIntent().getExtras().getString("VType","");
            VHeading = getIntent().getExtras().getString("VHeading","");
            if (Type.equals("0") && !Type.isEmpty()) {
                voucherDocumentDataset = (VoucherDocumentDataset) getIntent().getExtras().get("VoucherDataset");
                actionBar.setTitle("Transaction Attachment List");
            }else if (Type.equals("1") && !Type.isEmpty()) {
                masterDocumentDataset = (MasterDocumentDataset) getIntent().getExtras().get("MasterDataset");
                actionBar.setTitle("Master Attachment List");
            }
//            //TODO: Load RecyclerView
//            LoadRecyclerView(voucherDocumentDataset, masterDocumentDataset, Type);
        }catch (Exception e){
            MessageDialog.MessageDialog(context,"Intent Exception",e.toString());
        }
    }
    private void CallApiMethod(){
        LoginActivity obj = new LoginActivity();
        String[] str = obj.GetSharePreferenceSession(context);
        String FromDate = this.FromDate;
        String ToDate = this.ToDate;
        String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
        if (!status.contentEquals("No Internet Connection")) {
            CallRetrofitSearchFilter(str[3], str[0], str[4], str[15], str[5],str[14], str[6], VType, FromDate, ToDate,Type);
        }else{
            MessageDialog.MessageDialog(context,"",status);
        }
    }
    private void LoadRecyclerView(VoucherDocumentDataset voucherDocumentDataset,MasterDocumentDataset masterDocumentDataset,String Type){
        List<Map<String, String>> mapList = new ArrayList<>();
        String DocNo="",VID="";
        if (Type.equals("0")) {
            mapList = voucherDocumentDataset.getAttechDet();
            DocNo = voucherDocumentDataset.getDocNo();
            VID = voucherDocumentDataset.getDocID();
            adapter = new RecyclerViewAttachedListAdapter(context,mapList,VID,DocNo,VType,VHeading,Type);
            recyclerView.setAdapter(adapter);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
        }else if (Type.equals("1")) {
            mapList = masterDocumentDataset.getAttechDet();
            //DocNo = masterDocumentDataset.get();
            VID = masterDocumentDataset.getDocID();
            adapter = new RecyclerViewAttachedListAdapter(context,mapList,VID,DocNo,VType,VHeading,Type);
            recyclerView.setAdapter(adapter);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        CallApiMethod();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            finish();
        }else if(keyCode == KeyEvent.KEYCODE_HOME ){
            Log.d("HomeKey","Home key pressed then restart app");
            finishAffinity();
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem filterSearch = menu.findItem(R.id.action_filter_search);
        filterSearch.setVisible(false);
        //TODO: Search view
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
                if(adapter!=null) {
                    //adapter.filter(newText);
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
                    //adapter.filter(query);
                }
//                System.out.println("on query submit: "+query);
                return true;
            }
        };
        searchView.setOnQueryTextListener(textChangeListener);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
    private void CallRetrofitSearchFilter(String DeviceID, String SessionID, String UserID, String BranchID, String DivisionID, String CompanyID, String GodownID, String VType, String FromDate, String ToDate, final String Type){
        showpDialog();
        ApiInterface apiService =  ApiClient.getClient(StaticValues.BASE_URL).create(ApiInterface.class);
        Map<String, String> params = new HashMap<String, String>();
        params.put("DeviceID", DeviceID);
        params.put("UserID", UserID);
        params.put("SessionID", SessionID);
        params.put("BranchID", BranchID);
        params.put("DivisionID", DivisionID);
        params.put("CompanyID", CompanyID);
        params.put("GodownID", GodownID);
        params.put("VType", VType);
        params.put("FromDt", FromDate);
        params.put("ToDt", ToDate);
        params.put("Type", Type);
        Log.d(TAG,"Search filter Parameters: "+params.toString());
        if (Type.equals("0")) {
            Call<ResponseVoucherDocumentDataset> call = apiService.getDocAttachVoucherList(params);
            call.enqueue(new Callback<ResponseVoucherDocumentDataset>() {
                @Override
                public void onResponse(Call<ResponseVoucherDocumentDataset> call, retrofit2.Response<ResponseVoucherDocumentDataset> response) {
                    try {
                        //List<MasterDocumentDataset> masterDocumentDatasets = new ArrayList<MasterDocumentDataset>();
                        if (response.isSuccessful()) {
                            int Status = response.body().getStatus();
                            String msg = response.body().getMsg();
                            if (Status == 1) {
                                List<VoucherDocumentDataset> datasetList = response.body().getResult();
                                //TODO: Call LoadData method
                                if (!datasetList.isEmpty()) {
                                    int c=0;
                                    for (int i=0; i<datasetList.size(); i++){
                                        if (datasetList.get(i).getDocID().equals(voucherDocumentDataset.getDocID())){
                                            c=1;
                                            voucherDocumentDataset = datasetList.get(i);
                                            break;
                                        }
                                    }
                                    if (c==1) {
                                        LoadRecyclerView(voucherDocumentDataset, masterDocumentDataset, Type);
                                    }
                                }
                            } else {
                                List<VoucherDocumentDataset> datasetList = new ArrayList<VoucherDocumentDataset>();
                                //TODO: Call LoadData method
                                LoadRecyclerView(voucherDocumentDataset, masterDocumentDataset, Type);
                                MessageDialog.MessageDialog(context, "", msg);
                            }
                        } else {
                            List<VoucherDocumentDataset> datasetList = new ArrayList<VoucherDocumentDataset>();
                            //TODO: Call LoadData method
                            LoadRecyclerView(voucherDocumentDataset, masterDocumentDataset, Type);
                            MessageDialog.MessageDialog(context, "Server response :", "" + response.code());
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Exception: " + e.getMessage());
                        MessageDialog.MessageDialog(context, "Exception", "" + e.getMessage());
                    }
                    hidepDialog();
                }

                @Override
                public void onFailure(Call<ResponseVoucherDocumentDataset> call, Throwable t) {
                    Log.e(TAG, "Failure: " + t.toString());
                    MessageDialog.MessageDialog(context, "Exception", "" + t.toString());
                    hidepDialog();
                }
            });
        }else if (Type.equals("1")){
            Call<ResponseMasterDocumentDataset> call = apiService.getDocAttachMasterList(params);
            call.enqueue(new Callback<ResponseMasterDocumentDataset>() {
                @Override
                public void onResponse(Call<ResponseMasterDocumentDataset> call, retrofit2.Response<ResponseMasterDocumentDataset> response) {
                    try {
                        List<MasterDocumentDataset> voucherDocumentDatasets = new ArrayList<>();
                        if (response.isSuccessful()) {
                            int Status = response.body().getStatus();
                            String msg = response.body().getMsg();
                            if (Status == 1) {
                                List<MasterDocumentDataset> datasetList = response.body().getResult();
                                //TODO: Call LoadData method
                                if (!datasetList.isEmpty()) {
                                    int c=0;
                                    for (int i=0; i<datasetList.size(); i++){
                                        if (datasetList.get(i).getDocID().equals(masterDocumentDataset.getDocID())){
                                            c=1;
                                            masterDocumentDataset = datasetList.get(i);
                                            break;
                                        }
                                    }
                                    if (c==1) {
                                        LoadRecyclerView(voucherDocumentDataset, masterDocumentDataset, Type);
                                    }
                                }
                            } else {
                                List<MasterDocumentDataset> datasetList = response.body().getResult();
                                //TODO: Call LoadData method
                                LoadRecyclerView(voucherDocumentDataset, masterDocumentDataset, Type);
                                MessageDialog.MessageDialog(context, "", msg);
                            }
                        } else {
                            List<MasterDocumentDataset> datasetList = response.body().getResult();
                            //TODO: Call LoadData method
                            LoadRecyclerView(voucherDocumentDataset, masterDocumentDataset, Type);
                            MessageDialog.MessageDialog(context, "Server response :", "" + response.code());
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Exception: " + e.getMessage());
                        MessageDialog.MessageDialog(context, "Exception", "" + e.getMessage());
                    }
                    hidepDialog();
                }
                @Override
                public void onFailure(Call<ResponseMasterDocumentDataset> call, Throwable t) {
                    Log.e(TAG, "Failure: " + t.toString());
                    MessageDialog.MessageDialog(context, "Exception", "" + t.toString());
                    hidepDialog();
                }
            });
        }
    }
    private void showpDialog() {
        if(progressDialog!=null) {
            progressDialog.show();
        }
    }
    private void hidepDialog() {
        if(progressDialog!=null) {
            progressDialog.dismiss();
        }
    }
}
