package orderbooking.AccountInfoSend;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.IdRes;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.singlagroup.AppController;
import com.singlagroup.LoginActivity;
import com.singlagroup.R;
import com.singlagroup.customwidgets.ItemClickSupport;
import com.singlagroup.customwidgets.MessageDialog;
import com.singlagroup.customwidgets.MyWebViewClient;
import com.singlagroup.customwidgets.SendToOtherApps;
import com.singlagroup.customwidgets.ValidationMethods;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

import DatabaseController.CommanStatic;
import DatabaseController.DatabaseSqlLiteHandlerUserInfo;
import orderbooking.AccountInfoSend.adapter.AccountInfoSendSelectCustomerForOrderAdapter;
import orderbooking.StaticValues;
import orderbooking.catalogue.CatalogueActivity;
import orderbooking.customerlist.datasets.CloseOrBookDataset;
import orderbooking.customerlist.datasets.SelectCustomerForOrderDataset;
import services.NetworkChangeReceiver;
import services.NetworkListener;
import services.NetworkUtils;

/**
 * Created by Rakesh on 13-Oct-16.
 */

public class AccountInfoSendSelectCustomerForOrderActivity extends AppCompatActivity {
    ActionBar actionBar;
    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private Dialog dialog;
    private WebView mWebView;
    private AccountInfoSendSelectCustomerForOrderAdapter adapter;
    private ProgressDialog progressDialog;
    private DatabaseSqlLiteHandlerUserInfo DBHandler;
    private int activityFlag = 0;
    private int whatsapp=0;
    public static EditText edtSmsNo;
    private final int REQUEST_CODE=99;
    private static String TAG = AccountInfoSendSelectCustomerForOrderActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);} getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_recyclerview_swipe_refresh_layout);
        Initialization();
        //ModulePermission();
        CallApiMethod();
    }
    private void Initialization(){
        this.context = AccountInfoSendSelectCustomerForOrderActivity.this;
        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_Refresh_Layout);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        //mWebView = (WebView) findViewById(R.id.web_view);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        this.DBHandler = new DatabaseSqlLiteHandlerUserInfo(this);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ModulePermission();
            }
        });
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
            activityFlag = bundle.getInt("ActivityFlag",0);
            actionBar.setTitle(Title);
            if (StaticValues.viewFlag == 1 || StaticValues.editFlag == 1 || StaticValues.createFlag == 1 || StaticValues.removeFlag == 1 || StaticValues.printFlag == 1 || StaticValues.importFlag == 1 || StaticValues.exportFlag == 1){
                CallApiMethod();
            }else {
                MessageDialog.MessageDialog(AccountInfoSendSelectCustomerForOrderActivity.this,"Alert","You don't have permission of "+Title+" module");
            }
        }catch (Exception e){
            MessageDialog.MessageDialog(AccountInfoSendSelectCustomerForOrderActivity.this,"Exception",e.toString());
        }
    }
    private void CallApiMethod(){
        actionBar.setTitle("Party List");
        //TODO: Select Customer for order Request
        NetworkChangeReceiver.bindNetworkListener(new NetworkListener() {
            @Override
            public void networkReceived(String status) {
                if (!status.contentEquals("No Internet Connection")) {
                    LoginActivity obj=new LoginActivity();
                    String[] str = obj.GetSharePreferenceSession(AccountInfoSendSelectCustomerForOrderActivity.this);
                    if (str!=null) {
                        CallVolleySelectCustomerForOrder(str[2], str[3], str[4], str[0], str[16], str[5],str[14]);
                    }
                } else {
                    MessageDialog.MessageDialog(AccountInfoSendSelectCustomerForOrderActivity.this,"",status);
                }
            }
        });
        String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
        if (!status.contentEquals("No Internet Connection")) {
            LoginActivity obj=new LoginActivity();
            String[] str = obj.GetSharePreferenceSession(AccountInfoSendSelectCustomerForOrderActivity.this);
            if (str!=null) {
                CallVolleySelectCustomerForOrder(str[2], str[3], str[4], str[0], str[16], str[5],str[14]);
            }
        } else {
            MessageDialog.MessageDialog(AccountInfoSendSelectCustomerForOrderActivity.this,"","No Internet Connection");
        }
    }
    private void CallVolleySelectCustomerForOrder(final String MasterType, final String DeviceID, final String UserID,final String SessionID,final String MasterID,final String DivisionID,final String CompanyID){
        showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"PartyList", new Response.Listener<String>()
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
                        JSONArray jsonArrayScfo = jsonObject.getJSONArray("Result");
                        final List<SelectCustomerForOrderDataset> list = new ArrayList<>();
                        for (int i=0; i< jsonArrayScfo.length(); i++){
                            list.add(new SelectCustomerForOrderDataset(jsonArrayScfo.getJSONObject(i).getString("PartyID"),jsonArrayScfo.getJSONObject(i).getString("PartyName"),jsonArrayScfo.getJSONObject(i).getString("AgentID"),jsonArrayScfo.getJSONObject(i).getString("AgentName"),jsonArrayScfo.getJSONObject(i).getString("Mobile"),jsonArrayScfo.getJSONObject(i).getString("City"),jsonArrayScfo.getJSONObject(i).getString("State"),jsonArrayScfo.getJSONObject(i).getString("Address"),jsonArrayScfo.getJSONObject(i).getString("Active"),jsonArrayScfo.getJSONObject(i).getInt("SubPartyApplicable"),jsonArrayScfo.getJSONObject(i).getInt("MultiOrder"),jsonArrayScfo.getJSONObject(i).getString("Email"),jsonArrayScfo.getJSONObject(i).getString("AccountNo"),jsonArrayScfo.getJSONObject(i).getString("AccountHolderName"),jsonArrayScfo.getJSONObject(i).getString("IFSCCOde"),jsonArrayScfo.getJSONObject(i).getString("IDName"),jsonArrayScfo.getJSONObject(i).getString("GSTIN"),jsonArrayScfo.getJSONObject(i).getString("Extin1")));
                        }
                        adapter=new AccountInfoSendSelectCustomerForOrderAdapter(context,list);
                        recyclerView.setAdapter(adapter);
                        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context);
                        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                        recyclerView.setLayoutManager(linearLayoutManager);
                        recyclerView.setHasFixedSize(true);
                        if (activityFlag == 1){
                            ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                                @Override
                                public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                                    SelectCustomerForOrderDataset dataset = (SelectCustomerForOrderDataset) list.get(position);
                                    Intent intent = new Intent(getApplicationContext(), CatalogueActivity.class);
                                    intent.putExtra("Result",dataset);
                                    setResult(RESULT_OK,intent);
                                    finish();
                                }
                            });
                        }else {
                            ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                                @Override
                                public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                                    SelectCustomerForOrderDataset dataset = (SelectCustomerForOrderDataset) adapter.getItem(position);
                                    if (dataset.getSubPartyApplicable() == 1 && dataset.getMultiOrder() == 0) {
                                        //DialogPartyOrSubPartySelection(context,dataset);
                                        Intent intent = new Intent(context, AccountInfoSendSelectSubCustomerForOrderActivity.class);
                                        intent.putExtra("PartyID", dataset.getPartyID());
                                        intent.putExtra("PartyName", dataset.getPartyName());
                                        intent.putExtra("MultiOrder", dataset.getMultiOrder());
                                        context.startActivity(intent);
                                    } else if (dataset.getSubPartyApplicable() == 0 && dataset.getMultiOrder() == 1) {
                                        DialogShareOptions(dataset);
                                    } else if (dataset.getSubPartyApplicable() == 1 && dataset.getMultiOrder() == 1) {
                                        //DialogPartyOrSubPartySelection(context,dataset);
                                        Intent intent = new Intent(context, AccountInfoSendSelectSubCustomerForOrderActivity.class);
                                        intent.putExtra("PartyID", dataset.getPartyID());
                                        intent.putExtra("PartyName", dataset.getPartyName());
                                        intent.putExtra("MultiOrder", dataset.getMultiOrder());
                                        context.startActivity(intent);
                                    } else if (dataset.getSubPartyApplicable() == 0 && dataset.getMultiOrder() == 0) {
                                        DialogShareOptions(dataset);
                                    }
                                }
                            });
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
                params.put("MasterType", MasterType);
                params.put("UserID", UserID);
                params.put("SessionID", SessionID);
                params.put("MasterID", MasterID);
                params.put("DivisionID", DivisionID);
                params.put("CompanyID", CompanyID);
                Log.d(TAG,"Select customer for order parameters:"+params.toString());
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
        if(progressDialog!=null || swipeRefreshLayout!=null) {
            progressDialog.dismiss();
            swipeRefreshLayout.setRefreshing(false);
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        if (dialog!=null){
            dialog.dismiss();
            hidepDialog();
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
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        switch (reqCode) {
            case (REQUEST_CODE):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor c = getContentResolver().query(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                        String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        String hasNumber = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                        if (Integer.valueOf(hasNumber) >0) {

                            Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +ContactsContract.CommonDataKinds.Phone.TYPE + " = " +ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,new String[]{contactId},null);
                            //MessageDialog.MessageDialog(context,"",""+cursor.toString());
                            if (cursor.getCount()>0) {
                                List<String> NumbersList = new ArrayList<>();
                                while (cursor.moveToNext()) {
                                    //name=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                                    String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                    NumbersList.add(number);
                                }
                                if (!NumbersList.isEmpty()) {
                                    if (NumbersList.size() > 1) {
                                        ContactNumberSelect(name, NumbersList);
                                    } else {
                                        if (edtSmsNo != null) {
                                            String No = NumbersList.get(0).replaceAll("[()\\s-]+", "");
                                            edtSmsNo.setText("" + (No.length() > 10 ? No.substring(3) : No));
                                        } else {
                                            MessageDialog.MessageDialog(context, "Contact List", "Something is wrong");
                                        }
                                    }
                                }
                            }else{
                                MessageDialog.MessageDialog(context,"","No contact number picked");
                                if (edtSmsNo != null)  edtSmsNo.setText("");
                            }
                            cursor.close();
                        }else{
                            MessageDialog.MessageDialog(context,"","No Number fonud in this contact");
                            if (edtSmsNo != null)  edtSmsNo.setText("");
                        }
                    }
                    c.close();
                    break;
                }
        }
    }
    //TODO: Dialog share options
    private void ContactNumberSelect(String Name,List<String> NumbersList){
        final Dialog dialog = new Dialog(new ContextThemeWrapper(context, R.style.DialogSlideAnim));
        dialog.setContentView(R.layout.dialog_listview);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        dialog.getWindow().setAttributes(lp);
        //dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        TextView txtView = (TextView) dialog.findViewById(R.id.textView_Name);
        ListView listView = (ListView) dialog.findViewById(R.id.list_View);
        txtView.setText(""+Name);
        String[] array = new String[NumbersList.size()];
        NumbersList.toArray(array); // fill the array
        listView.setAdapter(new ArrayAdapter<>(context,android.R.layout.simple_list_item_1, android.R.id.text1,array));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String Number = (String) parent.getAdapter().getItem(position);
                //MessageDialog.MessageDialog(context,"",""+Number);
                if (edtSmsNo != null) {
                    String No = Number.replaceAll("[()\\s-]+", "");
                    edtSmsNo.setText("" + (No.length() > 10 ? No.substring(3) : No));
                    dialog.dismiss();
                } else {
                    MessageDialog.MessageDialog(context,"Contact List","Something is wrong");
                }
            }
        });
    }

    //TODO: Dialog share options
    private void DialogShareOptions(final SelectCustomerForOrderDataset dataset){
        final Dialog dialog = new Dialog(new ContextThemeWrapper(context, R.style.DialogSlideAnim));
        dialog.setContentView(R.layout.dialog_account_info_share_option);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        RadioGroup radioGroupChangeOrShowroom = (RadioGroup) dialog.findViewById(R.id.RadioGroup_shareOption);
        radioGroupChangeOrShowroom.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(radioGroup.getCheckedRadioButtonId()==R.id.RadioButton_sms){
                    dialog.dismiss();
                    //TODO: Share to SMS
                    EmailSMSWhatsAppDialogFunction(context,dataset,0);
                }else if(radioGroup.getCheckedRadioButtonId()==R.id.RadioButton_mail){
                    dialog.dismiss();
                    //TODO: Share to mail
                    EmailSMSWhatsAppDialogFunction(context,dataset,1);
                }else if(radioGroup.getCheckedRadioButtonId()==R.id.RadioButton_sms_mail){
                    dialog.dismiss();
                    //TODO: Share to sms and mail
                    EmailSMSWhatsAppDialogFunction(context,dataset,2);
                }else if(radioGroup.getCheckedRadioButtonId()==R.id.RadioButton_whatsapp){
                    dialog.dismiss();
                    //TODO: share to whatsapp
                    EmailSMSWhatsAppDialogFunction(context,dataset,3);
                }
            }
        });
    }
    //TODO: Name Remark for Multi Order Dialog
    private void EmailSMSWhatsAppDialogFunction(final Context context, final SelectCustomerForOrderDataset dataset, final int flag){
        dialog = new Dialog(new ContextThemeWrapper(context, R.style.DialogSlideAnim));
        dialog.setContentView(R.layout.dialog_share_details);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        final TableLayout tableLayout = (TableLayout)dialog.findViewById(R.id.table_Layout);
        final RadioGroup radioGroup = (RadioGroup)dialog.findViewById(R.id.RadioGroup_WhatsApp);
        final RadioButton radioButton_whats_No = (RadioButton) dialog.findViewById(R.id.RadioButton_whatsapp);
        final RadioButton radioButton_whats_List = (RadioButton) dialog.findViewById(R.id.RadioButton_whatsapp_list);
        LinearLayout linearLayoutContact = (LinearLayout) dialog.findViewById(R.id.Linear_contact);
        ImageView imageViewContact = (ImageView) dialog.findViewById(R.id.imageView_contact);
        edtSmsNo=(EditText)dialog.findViewById(R.id.editTxt_SmsNo);
        final EditText edtEmail=(EditText)dialog.findViewById(R.id.editTxt_Email);
        final EditText edtWhatsAppNo=(EditText)dialog.findViewById(R.id.editTxt_WhatsApp);
        mWebView = (WebView) dialog.findViewById(R.id.web_view);
        Button ok=(Button)dialog.findViewById(R.id.button_ok);
        Button cancel=(Button)dialog.findViewById(R.id.button_Cancel);
        //TODO: set Layout
        setTableLayoutAccountInfo(dataset,tableLayout);
        if (flag  == 0){ //SMS
            edtSmsNo.setText(""+(dataset == null ? "" : (dataset.getMobile().replaceFirst("^0*",""))));
            edtSmsNo.setVisibility(View.VISIBLE);
            linearLayoutContact.setVisibility(View.VISIBLE);
            edtEmail.setVisibility(View.GONE);
            edtWhatsAppNo.setVisibility(View.GONE);
            radioGroup.setVisibility(View.GONE);
            imageViewContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((Activity)context).startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE);
                }
            });
        }else if (flag == 1){//EMAIL
            edtEmail.setText(""+(dataset == null ? "" : (dataset.getEmail().equals("null") ? "" : dataset.getEmail())));
            edtSmsNo.setVisibility(View.GONE);
            linearLayoutContact.setVisibility(View.GONE);
            edtEmail.setVisibility(View.VISIBLE);
            edtWhatsAppNo.setVisibility(View.GONE);
            radioGroup.setVisibility(View.GONE);
        }else if (flag == 2){//SMS and EMAIL
            edtSmsNo.setText(""+(dataset == null ? "" : (dataset.getMobile().replaceFirst("^0*",""))));
            edtSmsNo.setVisibility(View.VISIBLE);
            linearLayoutContact.setVisibility(View.VISIBLE);
            edtEmail.setVisibility(View.VISIBLE);
            edtWhatsAppNo.setVisibility(View.GONE);
            radioGroup.setVisibility(View.GONE);
            imageViewContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((Activity)context).startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE);
                }
            });
        }else if (flag == 3){//Whats App
            edtWhatsAppNo.setText(""+(dataset == null ? "" : (dataset.getMobile().replaceFirst("^0*",""))));
            edtSmsNo.setVisibility(View.GONE);
            linearLayoutContact.setVisibility(View.GONE);
            edtEmail.setVisibility(View.GONE);
            edtWhatsAppNo.setVisibility(View.VISIBLE);
            radioGroup.setVisibility(View.VISIBLE);
            if (whatsapp == 0){
                radioButton_whats_No.setChecked(true);
                edtWhatsAppNo.setVisibility(View.VISIBLE);
            }else if (whatsapp == 1){
                radioButton_whats_List.setChecked(true);
                edtWhatsAppNo.setVisibility(View.GONE);
            }
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                    if(group.getCheckedRadioButtonId()==R.id.RadioButton_whatsapp){
                        whatsapp = 0;
                        edtWhatsAppNo.setVisibility(View.VISIBLE);
                    }else if(group.getCheckedRadioButtonId()==R.id.RadioButton_whatsapp_list){
                        whatsapp = 1;
                        //TODO: Whats App List
                        //SendToOtherApps.sendWhatsAppList(context);//,"91"+Mobile);
                        edtWhatsAppNo.setVisibility(View.GONE);
                    }
                }
            });
        }
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag == 0){
                    edtSmsNo.setError(null);
                    String Mobile = edtSmsNo.getText().toString();
                    boolean cancel = false;
                    View focusView = null;
                    //TODO Check for a Name Validation
                    if (TextUtils.isEmpty(Mobile)) {
                        edtSmsNo.setError("Mobile No is mandatory!!!");
                        focusView = edtSmsNo;
                        cancel = true;
                    }
                    if (cancel) {
                        // There was an error; don't attempt login and focus the first
                        // form field with an error.
                        focusView.requestFocus();
                    } else {
                        if (Mobile.length() == 10){
                            String status = NetworkUtils.getConnectivityStatusString(context);
                            if (!status.contentEquals("No Internet Connection")) {
                                CallVolleySend(Mobile,"","0",dataset.getAccountNo(),dataset.getIFSCCOde(),dataset.getAccountHolderName(),dataset.getPartyName());
                                dialog.dismiss();
                            }else{
                                MessageDialog.MessageDialog(context,"","No Internet Connection");
                            }
                        }else{
                            edtSmsNo.setError("Please enter valid Mobile No!!!");
                            focusView = edtSmsNo;
                        }
                    }
                }else if (flag == 1){
                    edtEmail.setError(null);
                    String Email = edtEmail.getText().toString();
                    boolean cancel = false;
                    View focusView = null;
                    //TODO Check for a Name Validation
                    if (TextUtils.isEmpty(Email)) {
                        edtEmail.setError("Email is mandatory!!!");
                        focusView = edtEmail;
                        cancel = true;
                    }
                    if (cancel) {
                        // There was an error; don't attempt login and focus the first
                        // form field with an error.
                        focusView.requestFocus();
                    } else {
                        if (ValidationMethods.isEmailValid(Email)){
                            String status = NetworkUtils.getConnectivityStatusString(context);
                            if (!status.contentEquals("No Internet Connection")) {
                                CallVolleySend("",Email,"1",dataset.getAccountNo(),dataset.getIFSCCOde(),dataset.getAccountHolderName(),dataset.getPartyName());
                                dialog.dismiss();
                            }else{
                                MessageDialog.MessageDialog(context,"","No Internet Connection");
                            }
                        }else{
                            edtEmail.setError("Please enter valid Email Id!!!");
                            focusView = edtEmail;
                        }
                    }
                }else if (flag == 2){ //SMS And MAil
                    edtEmail.setError(null);
                    edtSmsNo.setError(null);
                    String Mobile = edtSmsNo.getText().toString();
                    String Email = edtEmail.getText().toString();
                    boolean cancel = false;
                    View focusView = null;
                    //TODO Check for a Name Validation
                    if (TextUtils.isEmpty(Mobile)) {
                        edtSmsNo.setError("Mobile No is mandatory!!!");
                        focusView = edtSmsNo;
                        cancel = true;
                    }
                    if (TextUtils.isEmpty(Email)) {
                        edtEmail.setError("Email is mandatory!!!");
                        focusView = edtEmail;
                        cancel = true;
                    }
                    if (cancel) {
                        // There was an error; don't attempt login and focus the first
                        // form field with an error.
                        focusView.requestFocus();
                    } else {
                        if (Mobile.length() == 10 && ValidationMethods.isEmailValid(Email)){
                            String status = NetworkUtils.getConnectivityStatusString(context);
                            if (!status.contentEquals("No Internet Connection")) {
                                CallVolleySend(Mobile,Email,"2",dataset.getAccountNo(),dataset.getIFSCCOde(),dataset.getAccountHolderName(),dataset.getPartyName());
                                dialog.dismiss();
                            }else{
                                MessageDialog.MessageDialog(context,"","No Internet Connection");
                            }
                        }else if (Mobile.length() > 10 || Mobile.length() < 10) {
                            edtSmsNo.setError("Please enter valid Mobile No!!!");
                            focusView = edtSmsNo;
                        }else if (!ValidationMethods.isEmailValid(Email)) {
                            edtEmail.setError("Please enter valid Email Id!!!");
                            focusView = edtEmail;
                        }
                    }
                }else if (flag == 3){
                    if (whatsapp == 0) {
                        edtWhatsAppNo.setError(null);
                        String Mobile = edtWhatsAppNo.getText().toString().replaceFirst("^0*","");
                        boolean cancel = false;
                        View focusView = null;
                        //TODO Check for a Name Validation
                        if (TextUtils.isEmpty(Mobile)) {
                            edtWhatsAppNo.setError("Whats App No is mandatory!!!");
                            focusView = edtWhatsAppNo;
                            cancel = true;
                        }
                        if (cancel) {
                            // There was an error; don't attempt login and focus the first
                            // form field with an error.
                            focusView.requestFocus();
                        } else {
                            if (Mobile.length() == 10) {
//                                dialog.dismiss();
                                String Message = "PartyName: " +dataset.getPartyName() + "\n Please find below the RTGS/NEFT Bank Detail" +" \n\nName: " + dataset.getAccountHolderName() + " \nIFSCCode: " + dataset.getIFSCCOde() + " \nAccountNo: " + dataset.getAccountNo()+" \nBankName: ICICI Bank"+" \nBranch: RPC Delhi";
                                //SendToOtherApps.sendWhatsAppNumber(context,"91"+Mobile,Message);
                                String WhatsAppNo = "91"+edtWhatsAppNo.getText().toString().trim();
                                //TODO: Whats App No
                                //SendToOtherApps.sendWhatsAppNumber(context,"91"+edtWhatsAppNo.getText().toString());
                                mWebView.setWebViewClient(new MyWebViewClient(context));
                                mWebView.getSettings().setJavaScriptEnabled(true);
                                mWebView.loadUrl("https://api.whatsapp.com/send?phone=91"+Mobile+"&text="+Message.replaceAll("&",""));
                            } else {
                                edtWhatsAppNo.setError("Please enter valid Whats App No!!!");
                                focusView = edtWhatsAppNo;
                            }
                        }
                    }else {
                        String Message = "PartyName: " +dataset.getPartyName() + "\n Please find below the RTGS/NEFT Bank Detail" +" \n\nName: " + dataset.getAccountHolderName() + " \nIFSCCode: " + dataset.getIFSCCOde() + " \nAccountNo: " + dataset.getAccountNo()+" \nBankName: ICICI Bank"+" \nBranch: RPC Delhi";
                        SendToOtherApps.sendWhatsAppList(context,Message);
                    }
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
    private void setTableLayoutAccountInfo(SelectCustomerForOrderDataset dataset, TableLayout tableLayout){

        //TODO: TableLayout set
        tableLayout.removeAllViewsInLayout();
        tableLayout.removeAllViews();
        //TODO: 8th Row
        View v5 = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout, false);
        TextView txtHeader5= (TextView) v5.findViewById(R.id.header);
        txtHeader5.setText("Account No:");

        TextView txt5= (TextView) v5.findViewById(R.id.content);
        txt5.setText(""+(dataset.getAccountNo()==null || dataset.getAccountNo().equals("null") ? "":dataset.getAccountNo()));
        tableLayout.addView(v5);

        //TODO: 9th Row
        v5 = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout, false);
        txtHeader5= (TextView) v5.findViewById(R.id.header);
        txtHeader5.setText("A/c Holder Name:");

        txt5= (TextView) v5.findViewById(R.id.content);
        txt5.setText(""+(dataset.getAccountHolderName()==null || dataset.getAccountHolderName().equals("null") ? "":dataset.getAccountHolderName()));
        tableLayout.addView(v5);

        //TODO: 10th Row
        v5 = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout, false);
        txtHeader5= (TextView) v5.findViewById(R.id.header);
        txtHeader5.setText("IFSC Code:");

        txt5= (TextView) v5.findViewById(R.id.content);
        txt5.setText(""+(dataset.getIFSCCOde()==null || dataset.getIFSCCOde().equals("null") ? "":dataset.getIFSCCOde()));
        tableLayout.addView(v5);

        //TODO: 11th Row
        v5 = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout, false);
        txtHeader5= (TextView) v5.findViewById(R.id.header);
        txtHeader5.setText("Bank Name:");

        txt5= (TextView) v5.findViewById(R.id.content);
        txt5.setText("ICICI Bank");
        tableLayout.addView(v5);

        //TODO: 12th Row
        v5 = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout, false);
        txtHeader5= (TextView) v5.findViewById(R.id.header);
        txtHeader5.setText("Branch:");

        txt5= (TextView) v5.findViewById(R.id.content);
        txt5.setText("RPC Delhi");
        tableLayout.addView(v5);
    }
    //TODO: CallVolley Api Method
    private void CallVolleySend(final String Mobile, final String EmailID,final String SendFlag,final String AccountNo,final String IfscCode,final String AcHolderName,final String Name){
        showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"SendAccountDetailForPayPartySubParty", new Response.Listener<String>()
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
                        MessageDialog.MessageDialog(context,"",Msg);
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
                params.put("Mobile", Mobile);
                params.put("EmailID", EmailID);
                params.put("SendFlag", SendFlag);
                params.put("AccountNo", AccountNo);
                params.put("IfscCode", IfscCode);
                params.put("AcHolderName", AcHolderName);
                params.put("Name", Name);
                Log.d(TAG,"Account Info send parameters:"+params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
    }
}
