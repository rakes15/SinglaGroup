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
import android.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.singlagroup.LoginActivity;
import com.singlagroup.R;
import com.singlagroup.adapters.CommonSearchSpinnerFilterableAdapter;
import com.singlagroup.customwidgets.CommonSearchableSpinner;
import com.singlagroup.customwidgets.MessageDialog;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import uploadimagesfiles.voucherdocupload.adapter.RecyclerViewVoucherDocumentAdapter;
import uploadimagesfiles.voucherdocupload.datasets.MasterDocumentDataset;
import uploadimagesfiles.voucherdocupload.datasets.VoucherDocumentDataset;
import uploadimagesfiles.voucherdocupload.datasets.VoucherDocumentSpinnerDataset;
import uploadimagesfiles.voucherdocupload.responsedatasets.ResponseMasterDocumentDataset;
import uploadimagesfiles.voucherdocupload.responsedatasets.ResponseVoucherDocumentDataset;
import uploadimagesfiles.voucherdocupload.responsedatasets.ResponseVoucherDocumentSpinnerDataset;
/**
 * Created by Rakesh on 14-Jan-17.
 */
public class VoucherDocumentActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = VoucherDocumentActivity.class.getSimpleName();
    private Context context;
    private RecyclerView recyclerView;
    private Dialog dialog;
    private ProgressDialog progressDialog;
    private ActionBar actionBar;
    private Calendar cal;
    private int day;
    private int month;
    private int year;
    private EditText edtFromDate,edtToDate;
    private CommonSearchSpinnerFilterableAdapter commonAdapter;
    private RecyclerViewVoucherDocumentAdapter adapter;
    private String VType,VHeading,Type="0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);} getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_recyclerview);
        Initialization();
        ModulePermission();
    }
    private void Initialization(){
        try {
            this.context = VoucherDocumentActivity.this;
            actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
            cal = Calendar.getInstance();
            day = cal.get(Calendar.DAY_OF_MONTH);
            month = cal.get(Calendar.MONTH);
            year = cal.get(Calendar.YEAR);
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
        }catch (Exception e){
            MessageDialog.MessageDialog(this,"Initialization",""+e.toString());
        }
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
            actionBar.setTitle(Title);
            if (StaticValues.viewFlag == 1 || StaticValues.editFlag == 1 || StaticValues.createFlag == 1 || StaticValues.removeFlag == 1 || StaticValues.printFlag == 1 || StaticValues.importFlag == 1 || StaticValues.exportFlag == 1){
                DialogDocumentAttachmentTypeSelection();
            }else {
                MessageDialog.MessageDialog(VoucherDocumentActivity.this,"Alert","You don't have permission of "+Title+" module");
            }
        }catch (Exception e){
            MessageDialog.MessageDialog(VoucherDocumentActivity.this,"Exception",e.toString());
        }
    }
    private void DialogDocumentAttachmentTypeSelection(){
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
        radioGroupChangeOrShowroom.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(radioGroup.getCheckedRadioButtonId()==R.id.RadioButton_transaction){
                    //TODO: Transaction Type
                    Type = "0";
                    DialogSearchFilter(context,Type);
                    dialog.dismiss();
                }else if(radioGroup.getCheckedRadioButtonId()==R.id.RadioButton_master){
                    //TODO: Master Type
                    Type = "1";
                    DialogSearchFilter(context,Type);
                    dialog.dismiss();
                }
            }
        });
//        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
//            @Override
//            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
//                if(keyCode == KeyEvent.KEYCODE_BACK)
//                {
//                    return false;
//                }
//                return false;
//            }
//        });

    }
    private void LoadRecyclerView(List<VoucherDocumentDataset> VoucherList,List<MasterDocumentDataset> masterList,String Type){
        if (Type.equals("0")) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            adapter = new RecyclerViewVoucherDocumentAdapter(context, VoucherList,masterList, VType, VHeading,Type);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(linearLayoutManager);
        }else if (Type.equals("1")) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            adapter = new RecyclerViewVoucherDocumentAdapter(context,VoucherList, masterList, VType, VHeading,Type);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(linearLayoutManager);
        }
    }
    private void DialogSearchFilter(Context context,String Type){
        dialog = new Dialog(new ContextThemeWrapper(context, R.style.DialogSlideAnim));
        dialog.setContentView(R.layout.dialog_voucher_document_search_filter);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.TOP;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        //TODO: Declarations
        LinearLayout LinearLayoutDate = (LinearLayout) dialog.findViewById(R.id.LinearLayout_Date);
        edtFromDate = (EditText) dialog.findViewById(R.id.EditText_FromDate);
        edtToDate = (EditText) dialog.findViewById(R.id.EditText_ToDate);
        TextView textType = (TextView) dialog.findViewById(R.id.text_type);
        TextView textView = (TextView) dialog.findViewById(R.id.text_view);
        ProgressBar progressBar = (ProgressBar) dialog.findViewById(R.id.ProgressBar);
        CommonSearchableSpinner spnVoucherType = (CommonSearchableSpinner) dialog.findViewById(R.id.spinner_VoucherType);
        Button btnApply = (Button) dialog.findViewById(R.id.btn_apply);
        //TODO: Get Current Date
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String CurrentDate = df.format(c.getTime());
        edtFromDate.setInputType(InputType.TYPE_NULL);
        //TODO: Get Yesterday Date
        c.add(Calendar.DATE, -1);
        String YesterdayDate = df.format(c.getTime());
        edtFromDate.setText(YesterdayDate);
        edtFromDate.setOnClickListener(this);
        edtToDate.setInputType(InputType.TYPE_NULL);
        edtToDate.setText(CurrentDate);
        edtToDate.setOnClickListener(this);
        btnApply.setOnClickListener(this);
        if (Type.equals("0")){
            LinearLayoutDate.setVisibility(View.VISIBLE);
            textType.setText("Voucher Type");
            textView.setText("Select Voucher Type");
        }else if (Type.equals("1")){
            LinearLayoutDate.setVisibility(View.GONE);
            textType.setText("Master Type");
            textView.setText("Select Master Type");
        }
        LoginActivity obj=new LoginActivity();
        String[] str = obj.GetSharePreferenceSession(VoucherDocumentActivity.this);
        String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
        if (!status.contentEquals("No Internet Connection")) {
            CallRetrofitVoucherList(progressBar, spnVoucherType, textView, str[3], str[0], str[4], str[6], str[5], str[14],Type);
        }else{
            MessageDialog.MessageDialog(VoucherDocumentActivity.this,"",status);
        }
        VType=null;
        VHeading=null;
    }
    //TODO: Async Voucher type Spinner  Class
    private void CallRetrofitVoucherList(final ProgressBar progressBar, final CommonSearchableSpinner spnVoucher,final TextView textView, String DeviceID,String SessionID,String UserID,String GodownID,String DivisionID,String CompanyID,String Type){
        progressBar.setVisibility(View.VISIBLE);
        ApiInterface apiService =  ApiClient.getClient(StaticValues.BASE_URL).create(ApiInterface.class);

        Map<String, String> params = new HashMap<String, String>();
        params.put("DeviceID", DeviceID);
        params.put("UserID", UserID);
        params.put("SessionID", SessionID);
        params.put("GodownID", GodownID);
        params.put("DivisionID", DivisionID);
        params.put("CompanyID", CompanyID);
        params.put("Type", Type);
        Log.d(TAG,"Voucher list Parameters: "+params.toString()+" Url:"+StaticValues.BASE_URL);
        Call<ResponseVoucherDocumentSpinnerDataset> call = apiService.getDocAttachVTypeList(params);
        call.enqueue(new Callback<ResponseVoucherDocumentSpinnerDataset>() {
            @Override
            public void onResponse(Call<ResponseVoucherDocumentSpinnerDataset> call, retrofit2.Response<ResponseVoucherDocumentSpinnerDataset> response) {
                try {
                    if (response.isSuccessful()) {
                        int Status = response.body().getStatus();
                        String msg = response.body().getMsg();
                        if (Status == 1) {
                            final List<VoucherDocumentSpinnerDataset> datasetList = response.body().getResult();
                            final ArrayList<Map<String,String>> mapArrayList = new ArrayList<Map<String, String>>();
                            for (int i= 0; i<datasetList.size();i++){
                                Map<String,String> map=new HashMap<String, String>();
                                map.put("ID",datasetList.get(i).getID());
                                map.put("Name",datasetList.get(i).getVoucherHeading());
                                map.put("Type",datasetList.get(i).getVtype());
                                mapArrayList.add(map);
                            }
                            VoucherDocumentActivity.this.commonAdapter = new CommonSearchSpinnerFilterableAdapter(VoucherDocumentActivity.this, mapArrayList, mapArrayList);
                            spnVoucher.setAdapter(commonAdapter);
                            spnVoucher.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    textView.setVisibility(View.GONE);
                                    Map<String,String> map = (Map<String,String>)parent.getAdapter().getItem(position);
                                    String ID = map.get("ID");
                                    VHeading = map.get("Name");
                                    VType = map.get("Type");
                                    //Toast.makeText(getApplicationContext(),"Name:"+Name+"\nID:"+ID+"\nType:"+VType,Toast.LENGTH_SHORT).show();
                                }
                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                }
                            });
                        } else {
                            MessageDialog.MessageDialog(VoucherDocumentActivity.this, "" , msg);
                        }
                    }else {
                        MessageDialog.MessageDialog(VoucherDocumentActivity.this,"Server response :",""+response.code());
                    }
                }catch (Exception e){
                    Log.e(TAG,"Exception: "+e.getMessage());
                    MessageDialog.MessageDialog(VoucherDocumentActivity.this,"Exception",""+e.getMessage());
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ResponseVoucherDocumentSpinnerDataset> call, Throwable t) {
                Log.e(TAG,"Failure: "+t.toString());
                MessageDialog.MessageDialog(VoucherDocumentActivity.this,"Exception",""+t.toString());
                progressBar.setVisibility(View.GONE);
            }
        });
    }
    //TODO: Call Retrofit Search Filter
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
                        List<MasterDocumentDataset> masterDocumentDatasets = new ArrayList<MasterDocumentDataset>();
                        if (response.isSuccessful()) {
                            int Status = response.body().getStatus();
                            String msg = response.body().getMsg();
                            if (Status == 1) {
                                List<VoucherDocumentDataset> datasetList = response.body().getResult();

                                //TODO: Call LoadData method
                                if (datasetList.isEmpty()) {
                                    MessageDialog.MessageDialog(VoucherDocumentActivity.this, "", "No voucher found!!!");
                                } else {
                                    LoadRecyclerView(datasetList,masterDocumentDatasets,Type);
                                }
                            } else {
                                String Title = getIntent().getExtras().getString("Title");
                                List<VoucherDocumentDataset> datasetList = response.body().getResult();
                                //TODO: Call LoadData method
                                LoadRecyclerView(datasetList,masterDocumentDatasets,Type);
                                MessageDialog.MessageDialog(VoucherDocumentActivity.this, Title, msg);
                            }
                        } else {
                            List<VoucherDocumentDataset> datasetList = new ArrayList<VoucherDocumentDataset>();
                            //TODO: Call LoadData method
                            LoadRecyclerView(datasetList,masterDocumentDatasets,Type);
                            MessageDialog.MessageDialog(VoucherDocumentActivity.this, "Server response :", "" + response.code());
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Exception: " + e.getMessage());
                        MessageDialog.MessageDialog(VoucherDocumentActivity.this, "Exception", "" + e.getMessage());
                    }
                    hidepDialog();
                }

                @Override
                public void onFailure(Call<ResponseVoucherDocumentDataset> call, Throwable t) {
                    Log.e(TAG, "Failure: " + t.toString());
                    MessageDialog.MessageDialog(VoucherDocumentActivity.this, "Exception", "" + t.toString());
                    hidepDialog();
                }
            });
        }else if (Type.equals("1")){
            Call<ResponseMasterDocumentDataset> call = apiService.getDocAttachMasterList(params);
            call.enqueue(new Callback<ResponseMasterDocumentDataset>() {
                @Override
                public void onResponse(Call<ResponseMasterDocumentDataset> call, retrofit2.Response<ResponseMasterDocumentDataset> response) {
                    try {
                        List<VoucherDocumentDataset> voucherDocumentDatasets = new ArrayList<VoucherDocumentDataset>();
                        if (response.isSuccessful()) {
                            int Status = response.body().getStatus();
                            String msg = response.body().getMsg();
                            if (Status == 1) {
                                List<MasterDocumentDataset> datasetList = response.body().getResult();
                                //TODO: Call LoadData method
                                if (datasetList.isEmpty()) {
                                    MessageDialog.MessageDialog(context, "", "No voucher found!!!");
                                } else {
                                    LoadRecyclerView(voucherDocumentDatasets,datasetList,Type);
                                }
                            } else {
                                String Title = getIntent().getExtras().getString("Title");
                                List<MasterDocumentDataset> datasetList = response.body().getResult();
                                //TODO: Call LoadData method
                                LoadRecyclerView(voucherDocumentDatasets,datasetList,Type);
                                MessageDialog.MessageDialog(VoucherDocumentActivity.this, Title, msg);
                            }
                        } else {
                            List<MasterDocumentDataset> datasetList = new ArrayList<>();
                            //TODO: Call LoadData method
                            LoadRecyclerView(voucherDocumentDatasets,datasetList,Type);
                            MessageDialog.MessageDialog(VoucherDocumentActivity.this, "Server response :", "" + response.code());
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Exception: " + e.getMessage());
                        MessageDialog.MessageDialog(VoucherDocumentActivity.this, "Exception", "" + e.getMessage());
                    }
                    hidepDialog();
                }
                @Override
                public void onFailure(Call<ResponseMasterDocumentDataset> call, Throwable t) {
                    Log.e(TAG, "Failure: " + t.toString());
                    MessageDialog.MessageDialog(VoucherDocumentActivity.this, "Exception", "" + t.toString());
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
    private void CallSearchFilterApplyMethod(int flag,String Type){
        if (VType!=null && VHeading!=null) {
            LoginActivity obj = new LoginActivity();
            String[] str = obj.GetSharePreferenceSession(VoucherDocumentActivity.this);
            String FromDate = edtFromDate.getText().toString();
            String ToDate = edtToDate.getText().toString();
            String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
            if (!status.contentEquals("No Internet Connection")) {
                AttachmentActivity.FromDate = FromDate;
                AttachmentActivity.ToDate = ToDate;
                CallRetrofitSearchFilter(str[3], str[0], str[4], str[15], str[5],str[14], str[6], VType, FromDate, ToDate,Type);
                if (dialog !=null) { dialog.dismiss(); }
            }else{
                MessageDialog.MessageDialog(VoucherDocumentActivity.this,"",status);
            }
        }else{
            String TypeMsg = (Type.equals("0") ? "Please select Voucher type" : "Please select Master type");
            if (flag ==1)
            MessageDialog.MessageDialog(VoucherDocumentActivity.this,getIntent().getExtras().getString("Title","Document Attachment"),""+TypeMsg);
        }
    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_apply:
                //TODO: Filter Apply
                CallSearchFilterApplyMethod(1,Type);
                break;
            case R.id.EditText_FromDate:
                //TODO: Date Picker Call
                showDialog(0);
                break;
            case R.id.EditText_ToDate:
                //TODO: Date Picker Call
                showDialog(1);
                break;
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        CallSearchFilterApplyMethod(0,Type);
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
        MenuItem logout = menu.findItem(R.id.action_filter_search);
        logout.setVisible(true);
        //TODO: Search view
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_search:
                //Snackbar.make(recyclerView,"Comming Soon....Search",Snackbar.LENGTH_LONG).show();
                break;
            case R.id.action_filter_search:
                DialogDocumentAttachmentTypeSelection();
                break;
        }
        return true;
    }
    @Override
    protected Dialog onCreateDialog(int id) {
        if(id==0)
        {
            return new DatePickerDialog(this, datePickerListener1, year, month, day);
        }
        else if(id==1)
        {
            return new DatePickerDialog(this, datePickerListener2, year, month, day);
        }
        return null;
    }
    private DatePickerDialog.OnDateSetListener datePickerListener1 = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear,int selectedMonth, int selectedDay)
        {
            edtFromDate.setText(selectedYear + "-" + (selectedMonth + 1) + "-"+ selectedDay);
        }
    };
    private DatePickerDialog.OnDateSetListener datePickerListener2 = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear,int selectedMonth, int selectedDay)
        {
            edtToDate.setText(selectedYear + "-" + (selectedMonth + 1) + "-"+ selectedDay);
        }
    };
}
