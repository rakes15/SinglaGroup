package whatsapp;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.singlagroup.R;
import com.singlagroup.customwidgets.MessageDialog;
import com.singlagroup.customwidgets.MyWebViewClient;

import DatabaseController.CommanStatic;
import orderbooking.AccountInfoSend.AccountInfoSendSelectCustomerForOrderActivity;
import orderbooking.StaticValues;
import services.NetworkUtils;

/**
 * Created by rakes on 02-Dec-17.
 */

public class DirectlyWhatsAppActivity extends AppCompatActivity {
    ActionBar actionBar;
    private Context context;
    private WebView mWebView;
    private TextInputLayout edtWhatsApp, edtMessage;
    private Button btnSend,btnCheck;
    private ProgressDialog progressDialog;
    private static String TAG = DirectlyWhatsAppActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);} getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_directly_whatsapp);
        Initialization();
        ModulePermission();
    }
    private void Initialization(){
        this.context = DirectlyWhatsAppActivity.this;
        this.actionBar = getSupportActionBar();
        this.actionBar.setDisplayHomeAsUpEnabled(true);
        edtWhatsApp = (TextInputLayout) findViewById(R.id.editTxt_WhatsApp);
        edtMessage = (TextInputLayout) findViewById(R.id.editTxt_Message);
        btnSend = (Button) findViewById(R.id.Button_Send);
        btnCheck = (Button) findViewById(R.id.Button_check);
        btnCheck.setVisibility(View.GONE);
        mWebView = (WebView) findViewById(R.id.web_view);
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
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
            actionBar.setTitle(Title);
            if (StaticValues.viewFlag == 1 || StaticValues.editFlag == 1 || StaticValues.createFlag == 1 || StaticValues.removeFlag == 1 || StaticValues.printFlag == 1 || StaticValues.importFlag == 1 || StaticValues.exportFlag == 1){
                WhatsAppSend();
                WhatsAppNoCheck();
            }else {
                MessageDialog.MessageDialog(context,"Alert","You don't have permission of "+Title+" module");
            }
        }catch (Exception e){
            MessageDialog.MessageDialog(context,"Exception",e.toString());
        }
    }
    private void WhatsAppSend(){
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Mobile = edtWhatsApp.getEditText().getText().toString().trim().replaceFirst("^0*","");
                edtWhatsApp.setError(null);
                boolean cancel = false;
                View focusView = null;
                //TODO Check for a Whats App No Validation
                if (TextUtils.isEmpty(Mobile)) {
                    edtWhatsApp.setError("Whats app No is mandatory!!!");
                    focusView = edtWhatsApp;
                    cancel = true;
                }
                if (cancel) {
                    // There was an error; don't attempt login and focus the first
                    // form field with an error.
                    focusView.requestFocus();
                } else {
                    if (Mobile.length() == 10){
                        String WhatsAppNo = "91"+Mobile;
                        String Message = edtMessage.getEditText().getText().toString();
                        mWebView.setWebViewClient(new MyWebViewClient(context));
                        mWebView.getSettings().setJavaScriptEnabled(true);
                        mWebView.loadUrl("https://api.whatsapp.com/send?phone="+WhatsAppNo+"&text="+Message);
                    }else{
                        edtWhatsApp.setError("Please enter valid Whats app No!!!");
                        focusView = edtWhatsApp;
                    }
                }
            }
        });
    }
    private void WhatsAppNoCheck(){
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Mobile = edtWhatsApp.getEditText().getText().toString().trim();
                boolean Msg = hasWhatsapp(Mobile);
                MessageDialog.MessageDialog(context,"",""+Msg);
            }
        });
    }
    public boolean hasWhatsapp(String contactID) {
        String rowContactId = null;
        boolean hasWhatsApp=false;

        String[] projection = new String[]{ContactsContract.RawContacts._ID};
        Log.i(TAG, "projection:"+projection);
        String selection = ContactsContract.Data.CONTACT_ID + " = ? AND account_type IN (?)";
        Log.i(TAG, "selection:"+selection);
        String[] selectionArgs = new String[]{contactID, "com.whatsapp"};
        Log.i(TAG, "selectionArgs:"+selectionArgs);
        Cursor cursor = getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI, projection, selection, selectionArgs, null);
        Log.i(TAG, "cursor:"+cursor);
        if (cursor != null) {
            hasWhatsApp = cursor.moveToNext();
//            if (hasWhatsApp) {
//                rowContactId = cursor.getString(0);
//            }
            cursor.close();
        }
        return hasWhatsApp;
    }
    private void Show(){
        if (progressDialog!=null) {
            progressDialog.show();
        }
    }
    private void Hide(){
        if (progressDialog!=null) {
            progressDialog.dismiss();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        Hide();
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
//                if(adapter!=null) {
//                    adapter.filter(newText);
//                }
//                System.out.println("on text chnge text: "+newText);
                return true;
            }
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                // this is your adapter that will be filtered
//                if(adapter!=null)
//                {
//                    adapter.filter(query);
//                }
//                System.out.println("on query submit: "+query);
                return true;
            }
        };
        searchView.setOnQueryTextListener(textChangeListener);
        return super.onCreateOptionsMenu(menu);
    }
}
