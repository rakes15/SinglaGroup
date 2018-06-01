package com.singlagroup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.singlagroup.customwidgets.MessageDialog;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import DatabaseController.CommanStatic;
import DatabaseController.DatabaseSqlLiteHandlerUserInfo;
import de.hdodenhof.circleimageview.CircleImageView;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import orderbooking.StaticValues;
import services.NetworkUtils;
import uploadimagesfiles.adapter.ImageAdapter;

/**
 * Created by Rakesh on 10-Oct-16.
 */

public class DeviceInfoActivity extends AppCompatActivity {
    private ActionBar actionBar;
    private Context context;
    private TableLayout tableLayout;
    private ProgressDialog progressDialog;
    private DatabaseSqlLiteHandlerUserInfo DBHandler;
    CircleImageView circleImageView;
    private TextView txtPicChangeOrUpload,txtPicCancel;
    private int MAX_ATTACHMENT_COUNT = 1;
    private ArrayList<String> photoPaths = new ArrayList<>();
    private String ImageUrl = "";
    private static String TAG = DeviceInfoActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);}  getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_profile);
        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Device Info");
        this.DBHandler=new DatabaseSqlLiteHandlerUserInfo(getApplicationContext());
        Initialization();
    }
    private void Initialization(){
        this.context = DeviceInfoActivity.this;
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.relative_layout);
        relativeLayout.setVisibility(View.GONE);
        tableLayout = (TableLayout) findViewById(R.id.tableLayout_BasicInfo);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        if (CommanStatic.DeviceInfo!=null)
        LoadBasicInfoData(CommanStatic.DeviceInfo);
    }
    private void LoadBasicInfoData(String[][] BasicInfo){
        if(tableLayout!=null) {
            tableLayout.removeAllViews();
            tableLayout.removeAllViewsInLayout();
            int i=0;
            TableRow tableRow = new TableRow(this);
            tableRow.setId(i + 1);

            TextView txtHeader = new TextView(this);
            txtHeader.setId(i + 10);
            txtHeader.setText("Device info:");
            txtHeader.setTextColor(Color.BLUE);
            txtHeader.setPadding(5, 5, 5, 5);
            tableRow.addView(txtHeader);// add the column to the table row here
            tableLayout.addView(tableRow);

            for(i=1;i<12;i++) {
                String Header = BasicInfo[i][0];
                if (!Header.isEmpty()) {
                    tableRow = new TableRow(this);
                    tableRow.setId(i + 10);

                    txtHeader = new TextView(this);
                    txtHeader.setId(i + 30);
                    txtHeader.setText("" + BasicInfo[i][0]);
                    txtHeader.setTextColor(Color.BLACK);
                    txtHeader.setPadding(5, 5, 5, 5);
                    tableRow.addView(txtHeader);// add the column to the table row here

                    TextView txt = new TextView(this);
                    txt.setId(i + 100);
                    txt.setText("" + BasicInfo[0][i]);
                    txt.setTextColor(Color.GRAY);
                    txt.setPadding(5, 5, 5, 5);
                    tableRow.addView(txt);// add the column to the table row here

                    tableLayout.addView(tableRow);
                }
            }
            //TODO: Mapped User info
            tableRow = new TableRow(this);
            tableRow.setId(i + 100);

            txtHeader = new TextView(this);
            txtHeader.setId(i + 200);
            txtHeader.setText("Mapped user info:");
            txtHeader.setTextColor(Color.BLUE);
            txtHeader.setPadding(5, 5, 5, 5);
            tableRow.addView(txtHeader);// add the column to the table row here
            tableLayout.addView(tableRow);

            for(i=12;i<BasicInfo.length;i++) {
                String Header = BasicInfo[i][0];
                if (!Header.isEmpty()) {
                    tableRow = new TableRow(this);
                    tableRow.setId(i + 101);

                    txtHeader = new TextView(this);
                    txtHeader.setId(i + 30);
                    txtHeader.setText("" + BasicInfo[i][0]);
                    txtHeader.setTextColor(Color.BLACK);
                    txtHeader.setPadding(5, 5, 5, 5);
                    tableRow.addView(txtHeader);// add the column to the table row here

                    TextView txt = new TextView(this);
                    txt.setId(i + 100);
                    txt.setText("" + BasicInfo[0][i]);
                    txt.setTextColor(Color.GRAY);
                    txt.setPadding(5, 5, 5, 5);
                    tableRow.addView(txt);// add the column to the table row here

                    tableLayout.addView(tableRow);
                }
            }
        }
    }
    private void CallVolleyProfilePicUploadOrChange(final String DeviceID, final String UserID, final String SessionID,final String Image,final String CompanyID){
        showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"UserProfilePicUpdate", new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                // response
                Log.d("Response", response);
                try{
                    Intent intent = getIntent();
                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                    finish();
                }catch (Exception e){
                    MessageDialog.MessageDialog(DeviceInfoActivity.this,"Exception",""+e.toString());
                }
                hidepDialog();
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", ""+error);
                MessageDialog.MessageDialog(DeviceInfoActivity.this,"Error",""+error.toString());
                hidepDialog();
            }
        } ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("DeviceID", DeviceID);
                params.put("UserID", UserID);
                params.put("SessionID", SessionID);
                params.put("Image", Image);
                params.put("CompanyID", CompanyID);
                Log.d(TAG,"Parameters:"+params.toString());
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
        if(progressDialog!=null) {
            progressDialog.dismiss();
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            finish();
        }else if(keyCode == KeyEvent.KEYCODE_HOME ){
            Log.d("HomeKey","Home key pressed then restart app");
            finishAffinity();
        }
        return true;
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem menuItem = menu.findItem(R.id.action_attachment);
        menuItem.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    protected void onPause() {
        super.onPause();
        LoginActivity obj = new LoginActivity();
        String[] str = obj.GetSharePreferenceSession(getApplicationContext());
        //SessionManage.CallRetrofitSessionLogout(str[3],str[0],str[4],str[14]);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case FilePickerConst.REQUEST_CODE_PHOTO:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    photoPaths = new ArrayList<>();
                    photoPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
                }
                addThemToView(photoPaths);
                break;
        }
    }
    //TODO: Gallery Image View
    private void addThemToView(ArrayList<String> imagePaths) {
        ArrayList<String> filePaths = new ArrayList<>();
        if(imagePaths!=null)
            filePaths.addAll(imagePaths);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        if(recyclerView!=null) {
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL);
            layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
            recyclerView.setLayoutManager(layoutManager);

            ImageAdapter imageAdapter = new ImageAdapter(this, filePaths);

            recyclerView.setAdapter(imageAdapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        }
        try{
            if (!filePaths.isEmpty()) {
                File imgFile = new File(filePaths.get(0));
                if (imgFile.exists()) {
                    circleImageView.setImageBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
                    txtPicChangeOrUpload.setText("Upload");
                    txtPicCancel.setVisibility(View.VISIBLE);
                }
            }
        }catch (OutOfMemoryError e) {
            Log.e("ERRor", "Exception: "+e.toString());
            MessageDialog.MessageDialog(DeviceInfoActivity.this,"Gallery","Exception:"+e.toString());
        }catch (Exception e){
            MessageDialog.MessageDialog(DeviceInfoActivity.this,"Gallery","Exception:"+e.toString());
        }
        //Toast.makeText(this, "Num of files selected: "+ filePaths.size()+"\nPath:"+filePaths.get(0), Toast.LENGTH_SHORT).show();
    }
    private String EncodeIntoBase64(String Path){
        //showpDialog();
        String EncodedString="";
        try{
            System.out.println("Path: "+Path);
            InputStream inputStream = new FileInputStream(new File(Path));
            byte[] data;
            data = IOUtils.toByteArray(inputStream);
            System.out.println("covertByte: "+data.toString());
            // Encode Image to String
            EncodedString = Base64.encodeToString(data, 0);
        }catch (Exception e) {
            Log.i(TAG, ""+e.getMessage());
            MessageDialog.MessageDialog(DeviceInfoActivity.this,"Exception" ,"Convert into byte: "+e.toString());
        }
        return  EncodedString;
    }
}
