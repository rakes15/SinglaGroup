package stockcheck;

import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.singlagroup.AppController;
import com.singlagroup.GlobleValues;
import com.singlagroup.LoginActivity;
import com.singlagroup.R;
import com.singlagroup.customwidgets.BarcodeScanner;
import com.singlagroup.customwidgets.ConditionLibrary;
import com.singlagroup.customwidgets.MessageDialog;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

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
import services.NetworkChangeReceiver;
import services.NetworkListener;
import services.NetworkUtils;
import stockcheck.DatabaseSqLite.DatabaseSqlLiteHandlerStockCheck;
import uploadimagesfiles.ImageUplodingAcitvity;
import uploadimagesfiles.responsedatasets.ResponseItemInfoDataset;

/**
 * Created by Rakesh on 25-Oct-16.
 */
public class StockCheckActivity extends AppCompatActivity{
    private ActionBar actionBar;
    private TabLayout tabLayout;
    private ViewPager mViewPager;
    private ArrayList<String> Header;
    static String StyleCode="";
    private static String TAG = StockCheckActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);} getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.viewpager_design);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        Header=new ArrayList<String>();
        Header.add("Barcode Wise");
        Header.add("Design Wise");
        Header.add("Color Wise");
        Initialization();
        ModulePermission();
    }
    private void Initialization(){
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.pager);

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
            StyleCode = bundle.getString("ItemCode","");
            actionBar.setTitle(Title);
            if (StaticValues.viewFlag == 1 || StaticValues.editFlag == 1 || StaticValues.createFlag == 1 || StaticValues.removeFlag == 1 || StaticValues.printFlag == 1 || StaticValues.importFlag == 1 || StaticValues.exportFlag == 1){
                ViewPagerMethod();
            }else {
                MessageDialog.MessageDialog(StockCheckActivity.this,"Alert","You don't have permission of "+Title+" module");
            }
        }catch (Exception e){
            MessageDialog.MessageDialog(StockCheckActivity.this,"Exception",e.toString());
        }
    }
    private void ViewPagerMethod(){
        ViewPagerAdapter mPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager(),Header);
        mViewPager.setAdapter(mPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.setCurrentItem(1,true);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mViewPager.setCurrentItem(position,true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
    public class ViewPagerAdapter extends FragmentPagerAdapter {

        List<Map<String, String>> data=null;
        ArrayList<String> Header;
        public ViewPagerAdapter(FragmentManager fragmentManager, ArrayList<String> Header) {
            super(fragmentManager);
            this.Header=Header;
        }
        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                {
                    return BarcodeWiseFragment.newInstance("BarcodeWise");
                }
                case 1:
                {
                    return DesignWiseFragment.newInstance("DesignWise");
                }
                case 2:
                {
                    return ColorWiseFragment.newInstance("ColorWise");
                }
            }
            return null;
        }
        public CharSequence getPageTitle(int position) {
            return Header.get(position);
        }
        @Override
        public int getCount() {
            return Header.size();
        }
    }
    //TODO: Barcode wise fragment
    public static class BarcodeWiseFragment extends Fragment{
        private static final String BARCODE_WISE = "BarcodeWise";
        private Context context;
        private Button btnScan;
        private WebView webView;
        private EditText edtBarcode;
        private ProgressDialog progressDialog;
        private String Barcode="",ItemID="",scanContent=null;
        private int MDApplicable,SubItemApplicable;
        private DatabaseSqlLiteHandlerStockCheck DBHandler;
        public static BarcodeWiseFragment newInstance(String BarcodeWise) {
            BarcodeWiseFragment fragment = new BarcodeWiseFragment();
            Bundle args = new Bundle();
            args.putString(BARCODE_WISE, BarcodeWise);
            fragment.setArguments(args);
            return fragment;
        }
        public BarcodeWiseFragment() {}
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle paramBundle) {
            View rootView = inflater.inflate(R.layout.stockcheck_barcodewise, null);
            DBHandler=new DatabaseSqlLiteHandlerStockCheck(getActivity());
            Initialization(rootView);
            return rootView;
        }
        private void Initialization(View view){
            this.context = getActivity();
            webView = (WebView) view.findViewById(R.id.web_view);
            btnScan = (Button) view.findViewById(R.id.scan_button);
            edtBarcode = (EditText) view.findViewById(R.id.editText_barcode);
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
            btnScan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, BarcodeScanner.class);
                    startActivityForResult(intent,100);
                }
            });
            edtBarcode.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    // If the event is a key-down event on the "enter" button
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                        // Perform action on Enter key press
                        Barcode=edtBarcode.getText().toString().toUpperCase().trim();
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(edtBarcode.getWindowToken(), 0);
                        if(!Barcode.isEmpty()) {
                            CallApiMethod();
                        }else{
                            MessageDialog.MessageDialog(context,"","Please enter the Barcode");
                        }
                        edtBarcode.setText("");
                        return true;
                    }
                    return false;
                }
            });
        }
        private void CallApiMethod(){
            //TODO: Select Customer for order Request
            NetworkChangeReceiver.bindNetworkListener(new NetworkListener() {
                @Override
                public void networkReceived(String status) {
                    if (!status.contentEquals("No Internet Connection")) {
                        LoginActivity obj=new LoginActivity();
                        String[] str = obj.GetSharePreferenceSession(context);
                        if (str!=null && (!Barcode.isEmpty() || Barcode!=null)) {
                            CallVolleyStockCheck(str[3], str[0], str[4], str[5], str[14],Barcode);
                        }
                    } else {
                        MessageDialog.MessageDialog(context,"",status);
                    }
                }
            });
            String status = NetworkUtils.getConnectivityStatusString(context);
            if (!status.contentEquals("No Internet Connection")) {
                LoginActivity obj=new LoginActivity();
                String[] str = obj.GetSharePreferenceSession(context);
                if (str!=null && (!Barcode.isEmpty() || Barcode!=null)) {
                    CallVolleyStockCheck(str[3], str[0], str[4], str[5], str[14],Barcode);
                }
            } else {
                MessageDialog messageDialog=new MessageDialog();
                messageDialog.MessageDialog(context,"","","No Internet Connection");
            }
        }
        //TODO: CallVolley Stock Check
        private void CallVolleyStockCheck(final String DeviceID,final String SessionID,final String UserID,final String DivisionID,final String CompanyID,final String Barcode){
            showpDialog();
            StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"StockCheck", new Response.Listener<String>()
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
                            ItemID = "";
                            JSONArray jsonArrayResult = jsonObject.getJSONArray("Result");
                            List<Map<String,String>> mapList = new ArrayList<>();
                            for (int i=0; i< jsonArrayResult.length(); i++){
                                MDApplicable = jsonArrayResult.getJSONObject(i).getInt("MDApplicable");
                                SubItemApplicable = jsonArrayResult.getJSONObject(i).getInt("SubItemApplicable");
                                ItemID = jsonArrayResult.getJSONObject(i).getString("ItemID");
                                Map<String,String> map = new HashMap<>();
                                map.put("Barcode",jsonArrayResult.getJSONObject(i).getString("Barcode"));
                                map.put("ItemID",jsonArrayResult.getJSONObject(i).getString("ItemID"));
                                map.put("ItemCode",jsonArrayResult.getJSONObject(i).getString("ItemCode"));
                                map.put("ItemName",(jsonArrayResult.getJSONObject(i).optString("ItemName")==null ? "": jsonArrayResult.getJSONObject(i).optString("ItemName")));
                                map.put("MainGroup",jsonArrayResult.getJSONObject(i).getString("MainGroup"));
                                map.put("Group",jsonArrayResult.getJSONObject(i).getString("GroupName"));
                                map.put("ColorID",jsonArrayResult.getJSONObject(i).getString("ColorID"));
                                map.put("Color",jsonArrayResult.getJSONObject(i).getString("Color"));
                                map.put("SizeID",jsonArrayResult.getJSONObject(i).getString("SizeID"));
                                map.put("Size",jsonArrayResult.getJSONObject(i).getString("Size"));
                                map.put("Sequence",jsonArrayResult.getJSONObject(i).getString("Sequence"));
                                map.put("MDStock",jsonArrayResult.getJSONObject(i).getString("MDStock"));
                                map.put("MDReserveStock",jsonArrayResult.getJSONObject(i).getString("MDReserveStock"));
                                map.put("MDRejectionStock",jsonArrayResult.getJSONObject(i).getString("MDRejectionStock"));
                                map.put("MDSaleableStock",(jsonArrayResult.getJSONObject(i).optString("MDSaleableStock")==null) ? "0" : jsonArrayResult.getJSONObject(i).optString("MDSaleableStock"));
                                map.put("MDSrvOrdStock",(jsonArrayResult.getJSONObject(i).optString("MDSrvOrdStock")==null) ? "0" : jsonArrayResult.getJSONObject(i).optString("MDSrvOrdStock"));
                                map.put("Price",jsonArrayResult.getJSONObject(i).getString("Rate"));
                                map.put("MDApplicable",jsonArrayResult.getJSONObject(i).getString("MDApplicable"));
                                map.put("SubItemApplicable",jsonArrayResult.getJSONObject(i).getString("SubItemApplicable"));
                                map.put("SubItemID",jsonArrayResult.getJSONObject(i).getString("SubItemID"));
                                map.put("SubItemName",jsonArrayResult.getJSONObject(i).getString("SubItemName"));
                                map.put("SubItemCode",(jsonArrayResult.getJSONObject(i).optString("SubItemCode")==null) ? "" : jsonArrayResult.getJSONObject(i).optString("SubItemCode"));
                                map.put("Stock",jsonArrayResult.getJSONObject(i).getString("Stock"));
                                map.put("ReserveStock",jsonArrayResult.getJSONObject(i).getString("ReserveStock"));
                                map.put("RejectionStock",jsonArrayResult.getJSONObject(i).getString("RejectionStock"));
                                map.put("SaleableStock",(jsonArrayResult.getJSONObject(i).optString("SaleableStock")==null) ? "0" : jsonArrayResult.getJSONObject(i).optString("SaleableStock"));
                                map.put("SrvOrdStock",(jsonArrayResult.getJSONObject(i).optString("SrvOrdStock")==null) ? "0" : jsonArrayResult.getJSONObject(i).optString("SrvOrdStock"));
                                map.put("GodownID",(jsonArrayResult.getJSONObject(i).optString("GodownID")==null ? "": jsonArrayResult.getJSONObject(i).optString("GodownID")));
                                map.put("GodownName",(jsonArrayResult.getJSONObject(i).optString("GodownName")==null ? "": jsonArrayResult.getJSONObject(i).optString("GodownName")));
                                mapList.add(map);
                            }
                            context.deleteDatabase(DatabaseSqlLiteHandlerStockCheck.DATABASE_NAME);
                            DBHandler.deleteStocks();
                            //DBHandler.deleteGodownStocks();
                            DBHandler.insertStockCheckTable(mapList);
                            if (!mapList.isEmpty()) {
                                webView.setVisibility(View.VISIBLE);
                                if (MDApplicable == 1) {
                                    if (!ItemID.isEmpty()) LoadWebView(ItemID);
                                }
                            }else{
                                webView.setVisibility(View.GONE);
                                MessageDialog.MessageDialog(context,"","Entered Barcode is wrong");

                            }
                        } else {
                            webView.setVisibility(View.GONE);
                            MessageDialog.MessageDialog(context,"",""+Msg);
                        }
                    }catch (Exception e){
                        MessageDialog.MessageDialog(context,"Exception",""+e.toString());
                        webView.setVisibility(View.GONE);
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
                    params.put("SessionID", SessionID);
                    params.put("UserID", UserID);
                    params.put("DivisionID", DivisionID);
                    params.put("CompanyID", CompanyID);
                    params.put("Barcode", Barcode);
                    Log.d(TAG,"Stock Check parameters:"+params.toString());
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
                //swipeRefreshLayout.setRefreshing(false);
            }
        }
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data){
            if (requestCode == 100) {
                if (resultCode == Activity.RESULT_OK) {
                    Barcode = data.getExtras().getString("Barcode");
                    if (!Barcode.isEmpty()) {
                        //CallApiMethod();
                    } else {
                        MessageDialog.MessageDialog(context, "", "Please Scan a Barcode");
                    }
                }
                if (resultCode == Activity.RESULT_CANCELED) {
                    //Write your code if there's no result
                }
            }
        }
        private void LoadWebView(String ItemID){
            webView.setWebViewClient(new WebViewClient() {
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    return false;
                }
                @Override
                public void onPageFinished(WebView view, String url) {}
            });
            DBHandler.getBarcodeScannerDetails(ItemID);
            List<Map<String,String>> GodownList = DBHandler.getBarcodeWiseGodownDetails(Barcode);
            String ItemName = DBHandler.getBarcodeScannerDetails(ItemID).isEmpty() ? "": DBHandler.getBarcodeScannerDetails(ItemID).get(0).get("ItemName");
            String ItemCode = DBHandler.getBarcodeScannerDetails(ItemID).isEmpty() ? "": DBHandler.getBarcodeScannerDetails(ItemID).get(0).get("ItemCode");
            String MainGroup = DBHandler.getBarcodeScannerDetails(ItemID).isEmpty() ? "": DBHandler.getBarcodeScannerDetails(ItemID).get(0).get("MainGroup");
            String Group = DBHandler.getBarcodeScannerDetails(ItemID).isEmpty() ? "": DBHandler.getBarcodeScannerDetails(ItemID).get(0).get("Group");
            String Price = DBHandler.getBarcodeScannerDetails(ItemID).isEmpty() ? "": DBHandler.getBarcodeScannerDetails(ItemID).get(0).get("Price");
            String str2 = "<html><body><div style=\"width:100%; margin:0 auto; border:1px solid #EEE;\">";
            str2+="<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"1\" bordercolor=\"#666666\" style=\"font-size:12px;\">";
            str2+="<tr><table width=\"90%\" cellpadding=\"0\" cellspacing=\"0\" border=\"1\" bordercolor=\"#666666\" style=\"font-size:12px;\">\n" +
                    "\t<tbody>\n" +
                    "\t\t<tr>\n" +
                    "\t\t\t<td><strong>Item Name</strong></td><td>"+ItemName+"</td>\n" +
                    "\t\t\t<td><strong>Item Code</strong></td><td>"+ItemCode+"</td>\n" +
                    "\t\t</tr>\n" +
                    "\t\t<tr>\n" +
                    "\t\t\t<td><strong>Main Group</strong></td><td>"+MainGroup+"</td>\n" +
                    "\t\t\t<td><strong>Group Name</strong></td><td>"+Group+"</td>\n" +
                    "\t\t</tr>\n" +
                    "\t\t<tr>\n" +
                    "\t\t\t<td><strong>Item Rate</strong></td><td>"+Price+"</td>\n" +
                    "\t\t\t<td></td><td></td>\n" +
                    "\t\t</tr>\n" +
                    "\t</tbody>\n" +
                    "</table></tr>";
            str2+="<table width=\"90%\" cellpadding=\"0\" cellspacing=\"0\" border=\"1\" bordercolor=\"#666666\" style=\"font-size:12px;\">\n" +
                    "    <tbody>\n" +
                    "\t\t<tr>\n" +
                    "\t        <th><strong>Godown Name</strong></th>\n" +
                    "\t        <th><strong>Item Details</strong></th>\n" +
                    "       \t</tr>";
            for (int i=0; i<GodownList.size(); i++) {
                str2 += "\t\t<tr>\n" +
                        "\t\t\t<td><strong>" + GodownList.get(i).get("GodownName") + "</strong></td>\n" +
                        "            <td>\n" +
                        "\t\t\t\t<table width=\"40%\" cellpadding=\"0\" cellspacing=\"0\" border=\"1\" bordercolor=\"#666666\" style=\"font-size:12px;\">\n" +
                        "\t\t\t\t\t<tbody>\n" +
                        "\t\t\t\t\t\t<tr>\n" +
                        "\t\t\t\t\t\t\t<th>Color</th>\n" +
                        "              <th>M</th>\n" +
                        "\t\t\t\t\t\t</tr>\n" +
                        "\t\t\t\t        <tr>\n" +
                        "\t\t\t\t\t\t\t<td><strong>Gray</strong></td>\n" +
                        "                \t\t\t<td>0</td>\n" +
                        "\t\t\t\t\t\t</tr>\n" +
                        "\t\t\t\t\t</tbody>\n" +
                        "\t\t\t\t</table>\n" +
                        "\t\t\t</td>\n" +
                        "\t\t</tr>    \n";
            }
            str2+="\t\t<tr>\n" +
                    "\t\t\t<td align=\"center\"><strong>Grand Total</strong></td>\n" +
                    "\t\t\t<td align=\"right\">0</td>\n" +
                    "\t\t</tr>\n" +
                    "\t</tbody>\n" +
                    "</table>";

            str2+="</table></div></body></html>";

            webView.loadDataWithBaseURL(null, str2, "text/HTML", "UTF-8", null);
        }
    }
    //TODO: Desgin wise fragment
    public static class DesignWiseFragment extends Fragment {
        private static final String DESIGN_WISE = "DesignWise";
        private Context context;
        private ProgressDialog progressDialog;
        private ScrollView scrollView;
        Button btnScan,btnGodownWise,btnMoreDetails;
        EditText edtStyleOrBarcode;
        TableLayout tableLayout,tableLayoutItemDetails;
        private String Barcode = "",ItemID = "";
        private int MDApplicable = 0,SubItemApplicable = 0;
        DatabaseSqlLiteHandlerStockCheck DBHandler;
        public static DesignWiseFragment newInstance(String DesignWise) {
            DesignWiseFragment fragment = new DesignWiseFragment();
            Bundle args = new Bundle();
            args.putString(DESIGN_WISE, DesignWise);
            fragment.setArguments(args);
            return fragment;
        }
        public DesignWiseFragment() {}
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle paramBundle) {
            View rootView = inflater.inflate(R.layout.stockcheck_designwise, null);
            Initialization(rootView);
            if(!StockCheckActivity.StyleCode.isEmpty()){
                edtStyleOrBarcode.setText(StockCheckActivity.StyleCode);
                Barcode = StockCheckActivity.StyleCode;
                StockCheckActivity.StyleCode = "";
                CallApiMethod();
                edtStyleOrBarcode.setText("");
            }
            return rootView;
        }
        private void Initialization(View view){
            this.context = getActivity();
            this.DBHandler=new  DatabaseSqlLiteHandlerStockCheck(getActivity());
            btnScan = (Button) view.findViewById(R.id.scan_button);
            btnGodownWise = (Button) view.findViewById(R.id.button_GodownWise);
            btnMoreDetails = (Button) view.findViewById(R.id.button_More_Details);
            edtStyleOrBarcode = (EditText) view.findViewById(R.id.editText_style_or_barcode);
            tableLayout = (TableLayout) view.findViewById(R.id.tableLayout1);
            tableLayoutItemDetails = (TableLayout) view.findViewById(R.id.tableLayout);
            scrollView = (ScrollView) view.findViewById(R.id.scrollView);
            scrollView.setVisibility(View.GONE);
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
            btnScan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, BarcodeScanner.class);
                    startActivityForResult(intent,100);
                }
            });
            edtStyleOrBarcode.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    // If the event is a key-down event on the "enter" button
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        // Perform action on Enter key press
                        Barcode = edtStyleOrBarcode.getText().toString().toUpperCase().trim();
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(edtStyleOrBarcode.getWindowToken(), 0);
                        if(!Barcode.isEmpty()) {
                            CallApiMethod();
                        }else{
                            MessageDialog.MessageDialog(context,"","Please enter the Barcode/Stylecode");
                        }
                        edtStyleOrBarcode.setText("");
                        return true;
                    }
                    return false;
                }
            });
            btnMoreDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!Barcode.isEmpty()){
                        //DialogMoreDetails(ItemID,MDApplicable,SubItemApplicable);
                        String status = NetworkUtils.getConnectivityStatusString(context);
                        if (!status.contentEquals("No Internet Connection")) {
                            LoginActivity obj= new LoginActivity();
                            String[] str = obj.GetSharePreferenceSession(context);
                            if(str!=null)
                                CallRetrofitGetItemInfo(str[3], str[0], str[4],str[5],str[14],Barcode);
                        }else{
                            MessageDialog.MessageDialog(context,"",status);
                        }
                    }else {
                        MessageDialog.MessageDialog(context,"","Please enter Style/Barcode");
                    }
                }
            });
            btnGodownWise.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map<String,String> mapItemDetails = DBHandler.GetItemBasicDetails();
                    if (mapItemDetails!=null && !mapItemDetails.isEmpty()) {
                        Intent intent = new Intent(context, StockCheckGodownWiseActivity.class);
                        intent.putExtra("ItemID", mapItemDetails.get("ItemID"));
                        intent.putExtra("Barcode", Barcode);
                        intent.putExtra("MDApplicable", Integer.valueOf(mapItemDetails.get("MDApplicable")));
                        intent.putExtra("SubItemApplicable", Integer.valueOf(mapItemDetails.get("SubItemApplicable")));
                        intent.putExtra("Flag", 0); //TODO: 0 means Design Wise and 1 Means Color Wise
                        startActivity(intent);
                    }else {
                        MessageDialog.MessageDialog(context,"","Please search an Item then check it.");
                    }

                }
            });
        }
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data){
            if (requestCode == 100) {
                if (resultCode == Activity.RESULT_OK) {
                    Barcode = data.getExtras().getString("Barcode");
                    if (!Barcode.isEmpty()) {
                        CallApiMethod();
                    } else {
                        MessageDialog.MessageDialog(context, "", "Please Scan a Barcode");
                    }
                }
                if (resultCode == Activity.RESULT_CANCELED) {
                    //Write your code if there's no result
                }
            }
        }
        private void CallApiMethod(){
            //TODO: Select Customer for order Request
            NetworkChangeReceiver.bindNetworkListener(new NetworkListener() {
                @Override
                public void networkReceived(String status) {
                    if (!status.contentEquals("No Internet Connection")) {
                        LoginActivity obj=new LoginActivity();
                        String[] str = obj.GetSharePreferenceSession(context);
                        if (str!=null && (!Barcode.isEmpty() || Barcode!=null)) {
                            CallVolleyStockCheck(str[3], str[0], str[4], str[5], str[14],Barcode);
                        }
                    } else {
                        MessageDialog.MessageDialog(context,"",status);
                    }
                }
            });
            String status = NetworkUtils.getConnectivityStatusString(context);
            if (!status.contentEquals("No Internet Connection")) {
                LoginActivity obj=new LoginActivity();
                String[] str = obj.GetSharePreferenceSession(context);
                if (str!=null && (!Barcode.isEmpty() || Barcode!=null)) {
                    CallVolleyStockCheck(str[3], str[0], str[4], str[5], str[14],Barcode);
                }
            } else {
                MessageDialog messageDialog=new MessageDialog();
                messageDialog.MessageDialog(context,"","","No Internet Connection");
            }
        }
        //TODO: CallVolley Stock Check
        private void CallVolleyStockCheck(final String DeviceID,final String SessionID,final String UserID,final String DivisionID,final String CompanyID,final String Barcode){
            showpDialog();
            StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"StockCheck", new Response.Listener<String>()
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
                            ItemID = "";
                            JSONArray jsonArrayResult = jsonObject.getJSONArray("Result");
                            List<Map<String,String>> mapList = new ArrayList<>();
                            for (int i=0; i< jsonArrayResult.length(); i++){
                                MDApplicable = jsonArrayResult.getJSONObject(i).getInt("MDApplicable");
                                SubItemApplicable = jsonArrayResult.getJSONObject(i).getInt("SubItemApplicable");
                                ItemID = jsonArrayResult.getJSONObject(i).getString("ItemID");
                                Map<String,String> map = new HashMap<>();
                                map.put("Barcode",jsonArrayResult.getJSONObject(i).getString("Barcode"));
                                map.put("ItemID",jsonArrayResult.getJSONObject(i).getString("ItemID"));
                                map.put("ItemCode",jsonArrayResult.getJSONObject(i).getString("ItemCode"));
                                map.put("ItemName",(jsonArrayResult.getJSONObject(i).optString("ItemName")==null ? "": jsonArrayResult.getJSONObject(i).optString("ItemName")));
                                map.put("MainGroup",jsonArrayResult.getJSONObject(i).getString("MainGroup"));
                                map.put("Group",jsonArrayResult.getJSONObject(i).getString("GroupName"));
                                map.put("ColorID",jsonArrayResult.getJSONObject(i).getString("ColorID"));
                                map.put("Color",jsonArrayResult.getJSONObject(i).getString("Color"));
                                map.put("SizeID",jsonArrayResult.getJSONObject(i).getString("SizeID"));
                                map.put("Size",jsonArrayResult.getJSONObject(i).getString("Size"));
                                map.put("Sequence",jsonArrayResult.getJSONObject(i).getString("Sequence"));
                                map.put("MDStock",jsonArrayResult.getJSONObject(i).getString("MDStock"));
                                map.put("MDReserveStock",jsonArrayResult.getJSONObject(i).getString("MDReserveStock"));
                                map.put("MDRejectionStock",jsonArrayResult.getJSONObject(i).getString("MDRejectionStock"));
                                map.put("MDSaleableStock",(jsonArrayResult.getJSONObject(i).optString("MDSaleableStock")==null) ? "0" : jsonArrayResult.getJSONObject(i).optString("MDSaleableStock"));
                                map.put("MDSrvOrdStock",(jsonArrayResult.getJSONObject(i).optString("MDSrvOrdStock")==null) ? "0" : jsonArrayResult.getJSONObject(i).optString("MDSrvOrdStock"));
                                map.put("Price",jsonArrayResult.getJSONObject(i).getString("Rate"));
                                map.put("MDApplicable",jsonArrayResult.getJSONObject(i).getString("MDApplicable"));
                                map.put("SubItemApplicable",jsonArrayResult.getJSONObject(i).getString("SubItemApplicable"));
                                map.put("SubItemID",jsonArrayResult.getJSONObject(i).getString("SubItemID"));
                                map.put("SubItemName",jsonArrayResult.getJSONObject(i).getString("SubItemName"));
                                map.put("SubItemCode",(jsonArrayResult.getJSONObject(i).optString("SubItemCode")==null) ? "" : jsonArrayResult.getJSONObject(i).optString("SubItemCode"));
                                map.put("Stock",jsonArrayResult.getJSONObject(i).getString("Stock"));
                                map.put("ReserveStock",jsonArrayResult.getJSONObject(i).getString("ReserveStock"));
                                map.put("RejectionStock",jsonArrayResult.getJSONObject(i).getString("RejectionStock"));
                                map.put("SaleableStock",(jsonArrayResult.getJSONObject(i).optString("SaleableStock")==null) ? "0" : jsonArrayResult.getJSONObject(i).optString("SaleableStock"));
                                map.put("SrvOrdStock",(jsonArrayResult.getJSONObject(i).optString("SrvOrdStock")==null) ? "0" : jsonArrayResult.getJSONObject(i).optString("SrvOrdStock"));
                                map.put("GodownID",(jsonArrayResult.getJSONObject(i).optString("GodownID")==null ? "": jsonArrayResult.getJSONObject(i).optString("GodownID")));
                                map.put("GodownName",(jsonArrayResult.getJSONObject(i).optString("GodownName")==null ? "": jsonArrayResult.getJSONObject(i).optString("GodownName")));
                                mapList.add(map);
                            }
                            context.deleteDatabase(DatabaseSqlLiteHandlerStockCheck.DATABASE_NAME);
                            DBHandler.deleteStocks();
                            //DBHandler.deleteGodownStocks();
                            DBHandler.insertStockCheckTable(mapList);
                            if (!mapList.isEmpty()) {
                                scrollView.setVisibility(View.VISIBLE);
                                //TODO: Get Item Details
                                GetItemDetails();
                                if (MDApplicable == 1) {
                                    if (!ItemID.isEmpty()) StockMatrixMDDetails(tableLayout,ItemID);
                                } else {
                                    if (SubItemApplicable == 1) {
                                        if (!ItemID.isEmpty()) SubItemDetails(tableLayout,ItemID);
                                    } else {
                                        if (!ItemID.isEmpty()) ItemOnlyDetails(tableLayout,ItemID);
                                    }
                                }
                            }else{
                                scrollView.setVisibility(View.GONE);
                                MessageDialog.MessageDialog(context,"","Entered Style or Barcode is wrong");

                            }
                        } else {
                            scrollView.setVisibility(View.GONE);
                            MessageDialog.MessageDialog(context,"",""+Msg);
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
                    params.put("SessionID", SessionID);
                    params.put("UserID", UserID);
                    params.put("DivisionID", DivisionID);
                    params.put("CompanyID", CompanyID);
                    params.put("Barcode", Barcode);
                    Log.d(TAG,"Stock Check parameters:"+params.toString());
                    return params;
                }
            };
            postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            AppController.getInstance().addToRequestQueue(postRequest);
        }
        private void CallRetrofitGetItemInfo( String DeviceID, String SessionID, String UserID,String DivisionID,String CompanyID,final String ItemCode){
            showpDialog();
            final ApiInterface apiService =  ApiClient.getClient(StaticValues.BASE_URL).create(ApiInterface.class);
            Map<String, String> params = new HashMap<>();
            params.put("DeviceID", DeviceID);
            params.put("SessionID", SessionID);
            params.put("UserID", UserID);
            params.put("DivisionID", DivisionID);
            params.put("CompanyID", CompanyID);
            params.put("ItemCode", ItemCode);
            Log.d(TAG,"Parameters:"+params.toString());
            Call<ResponseItemInfoDataset> call = apiService.getItemInfoWithImage(params);
            call.enqueue(new Callback<ResponseItemInfoDataset>() {
                @Override
                public void onResponse(Call<ResponseItemInfoDataset> call, retrofit2.Response<ResponseItemInfoDataset> response) {
                    try {
                        if (response.isSuccessful()) {
                            int Status = response.body().getStatus();
                            String msg = response.body().getMsg();
                            if (Status == 1) {
                                Map<String,String> map = response.body().getResult();
                                if (map!=null && map.get("ImageStatus").equals("1") && !map.get("ItemImage").isEmpty()) {
                                    DialogViewImage(context, map.get("ItemImage"));
                                }else {
                                    MessageDialog.MessageDialog(context,"","Image not available");
                                }
                            } else {
                                MessageDialog.MessageDialog(context,"",msg);
                            }
                        }else {
                            MessageDialog.MessageDialog(context,"Server response",""+response.code());
                        }
                    }catch (Exception e){
                        Log.e(TAG," Exception:"+e.getMessage());
                        MessageDialog.MessageDialog(context,"Item image API",e.toString());
                    }
                    hidepDialog();
                }

                @Override
                public void onFailure(Call<ResponseItemInfoDataset> call, Throwable t) {
                    Log.e(TAG,"Failure: "+t.toString());
                    MessageDialog.MessageDialog(context,"Failure","Item image API"+t.toString());
                    hidepDialog();
                }
            });
        }
        private void showpDialog() {
            if(progressDialog!=null) {
                progressDialog.show();
            }
        }
        private void hidepDialog() {
            if(progressDialog!=null) {
                progressDialog.dismiss();
                //swipeRefreshLayout.setRefreshing(false);
            }
        }
        private void GetItemDetails(){
            tableLayoutItemDetails.removeAllViews();
            tableLayoutItemDetails.removeAllViewsInLayout();
            Map<String,String> mapItemDetails = DBHandler.GetItemBasicDetails();
            if (mapItemDetails!=null){
                //TODO:Item Code
                View v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayoutItemDetails, false);
                //v.setBackgroundColor(context.getResources().getColor(R.color.red_10));
                TextView txtHeader= (TextView) v.findViewById(R.id.header);
                txtHeader.setText("Barcode / Style:");

                TextView txt= (TextView) v.findViewById(R.id.content);
                String ItemCode = (mapItemDetails.get("ItemCode")==null || mapItemDetails.get("ItemCode").equals("null") ? "" :mapItemDetails.get("ItemCode"));
                if (Barcode.equals(ItemCode)) {
                    txtHeader.setText("Style:");
                    txt.setText(Barcode+"");
                }else{
                    txtHeader.setText("Barcode / Style:");
                    txt.setText(Barcode + " / " +ItemCode);
                }
                tableLayoutItemDetails.addView(v);
                //TODO:Item Name
                v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayoutItemDetails, false);
                //v.setBackgroundColor(context.getResources().getColor(R.color.red_10));
                txtHeader= (TextView) v.findViewById(R.id.header);
                txtHeader.setText("Item Name:");

                txt= (TextView) v.findViewById(R.id.content);
                txt.setText(""+(mapItemDetails.get("ItemName")==null || mapItemDetails.get("ItemName").equals("null") ? "" :mapItemDetails.get("ItemName")));
                tableLayoutItemDetails.addView(v);
                //TODO:Rate
                v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayoutItemDetails, false);
                //v.setBackgroundColor(context.getResources().getColor(R.color.red_10));
                txtHeader= (TextView) v.findViewById(R.id.header);
                txtHeader.setText("Rate:");

                txt= (TextView) v.findViewById(R.id.content);
                txt.setText(""+(mapItemDetails.get("Price")==null || mapItemDetails.get("Price").equals("null") ? "" : StaticValues.Rupees+mapItemDetails.get("Price")));
                tableLayoutItemDetails.addView(v);
                //TODO:Main Group
                v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayoutItemDetails, false);
                //v.setBackgroundColor(context.getResources().getColor(R.color.red_10));
                txtHeader= (TextView) v.findViewById(R.id.header);
                txtHeader.setText("MainGroup / Group:");

                txt= (TextView) v.findViewById(R.id.content);
                txt.setText(""+mapItemDetails.get("MainGroup")+ " / "+mapItemDetails.get("Group"));
                tableLayoutItemDetails.addView(v);
                //TODO:Remark
                v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayoutItemDetails, false);
                //v.setBackgroundColor(context.getResources().getColor(R.color.red_10));
                txtHeader= (TextView) v.findViewById(R.id.header);
                txtHeader.setText("*Remark:");
                txtHeader.setTextColor(Color.RED);

                txt= (TextView) v.findViewById(R.id.content);
                txt.setText("Stock | Reserve | Rejection | Saleable");
                tableLayoutItemDetails.addView(v);

            }else{
                MessageDialog.MessageDialog(context,"","Something went wrong");
            }
        }
        private void DialogViewImage(final Context mContext,String Url) {
            final Dialog dialog = new Dialog(new ContextThemeWrapper(mContext, R.style.DialogSlideAnim));
            dialog.setContentView(R.layout.dialog_view_image);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            lp.gravity = Gravity.TOP;
            dialog.getWindow().setAttributes(lp);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.show();
            //TODO: Declarations
            ImageView imageView = (ImageView) dialog.findViewById(R.id.Image_View);
            WebView webView = (WebView) dialog.findViewById(R.id.web_view);
            Button btnOK = (Button) dialog.findViewById(R.id.btn_ok);
            imageView.setVisibility(View.VISIBLE);
            Picasso.with(mContext).load(Url).placeholder(R.mipmap.ic_launcher).memoryPolicy(MemoryPolicy.NO_CACHE).into(imageView);
            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
        //TODO: Display Stock Grid of Multi Details
        private void StockMatrixMDDetails(TableLayout tableLayout,String ItemID){
            tableLayout.removeAllViews();
            tableLayout.removeAllViewsInLayout();
            //TODO: Grid of Size by Stock and bookQty
            List<Map<String,String>> sizeList = DBHandler.getBarcodeScannerMatrixsize(ItemID);
            List<Map<String,String>> colorList = DBHandler.getBarcodeScannerMatrixColor(ItemID);
            List<Map<String, String>> mdQtyList = new ArrayList<>();
            int[][] rowTotArr=new  int[sizeList.size()+1][4];
            int[] columnTotaltArr=new  int[4];
            int Stock=0,ReserveStock=0,RejectedStock=0,SlableStock=0;

            int i=0;

            TableRow tableRow1=new TableRow(getActivity());
            tableRow1.setId(i+100);
            tableRow1.setBackgroundColor(Color.LTGRAY);

            TextView txt = new TextView(getActivity());
            txt.setId(i+11);
            txt.setText("Color");
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setGravity(Gravity.LEFT);
            txt.setTextColor(Color.BLACK);
            txt.setPadding(5, 5, 5, 5);
            tableRow1.addView(txt);// add the column to the table row here

            for(int k=0;k<sizeList.size();k++)
            {
                TextView tv = new TextView(getActivity());
                tv.setId(k);// define id that must be unique
                tv.setTag(sizeList.get(k).get("SizeID"));
                tv.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
                tv.setGravity(Gravity.CENTER_HORIZONTAL);
                tv.setText(sizeList.get(k).get("SizeName")); // set the text for the header
                tv.setTextColor(Color.BLUE); // set the color
                tv.setPadding(10, 5, 10, 5); // set the padding (if required)
                tableRow1.addView(tv); // add the column to the table row here
            }
            TextView tv = new TextView(getActivity());
            tv.setId(i+15);// define id that must be unique
            tv.setText("Total"); // set the text for the header
            tv.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
            tv.setTextColor(Color.BLACK); // set the color
            tv.setPadding(10, 5, 10, 5); // set the padding (if required)
            tableRow1.addView(tv); // add the column to the table row here
            tableLayout.addView(tableRow1);
            int j = 0,dynamicID=0;
            for (int m = 0; m < colorList.size(); m++)
            {
                //TODO; Row 2
                TableRow tableRow2=new TableRow(getActivity());
                tableRow2.setId(m+20);
                tableRow2.setBackgroundColor(Color.WHITE);

                columnTotaltArr[0]=0;
                try {
                    TextView tvColor = new TextView(getActivity());
                    tvColor.setId(89+i);// define id that must be unique
                    tvColor.setText(colorList.get(m).get("ColorName")); // set the text for the header
                    tvColor.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
                    tvColor.setGravity(Gravity.LEFT);
                    tvColor.setTextColor(Color.BLACK); // set the color
                    tvColor.setPadding(5, 5, 5, 5); // set the padding (if required)
                    tableRow2.addView(tvColor); // add the column to the table row here
                } catch (Exception e) {
                    Log.e("Exception", "Color:"+e.getMessage());
                }
                // inner for loop
                for (j = 0; j < sizeList.size(); j++)
                {
                    try {
                        mdQtyList=DBHandler.getStockItemTotal(colorList.get(m).get("ColorID"), sizeList.get(j).get("SizeID"),ItemID);
                        if(mdQtyList.isEmpty()){
                            Stock=0;
                            ReserveStock=0;
                            RejectedStock=0;
                            SlableStock=0;
                        }else {
                            Stock=Integer.valueOf(mdQtyList.get(0).get("Stock"));
                            ReserveStock=Integer.valueOf(mdQtyList.get(0).get("ReserveStock"));
                            RejectedStock=Integer.valueOf(mdQtyList.get(0).get("RejectedStock"));
                            SlableStock=Integer.valueOf(mdQtyList.get(0).get("SlableStock"));
                            columnTotaltArr[0]+=Stock;//Add value
                            columnTotaltArr[1]+=ReserveStock;//Add value
                            columnTotaltArr[2]+=RejectedStock;//Add value
                            columnTotaltArr[3]+=SlableStock;//Add value
                        }
                        tv = new TextView(getActivity());
                        tv.setId(dynamicID);// define id that must be unique
                        tv.setText(String.valueOf(Stock)+" | "+String.valueOf(ReserveStock)+" | "+String.valueOf(RejectedStock)+" | "+String.valueOf(SlableStock)); // set the text for the header
                        tv.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
                        tv.setGravity(Gravity.CENTER_HORIZONTAL);
                        tv.setTextColor(Color.BLACK); // set the color
                        tv.setPadding(10, 5, 10, 5); // set the padding (if required)
                        tableRow2.addView(tv); // add the column to the table row here

                        rowTotArr[j][0]+=Stock;
                        rowTotArr[j][1]+=ReserveStock;
                        rowTotArr[j][2]+=RejectedStock;
                        rowTotArr[j][3]+=SlableStock;
                        dynamicID++;
                    } catch (Exception e) {
                        Log.e("Exception", "Qty:"+e.getMessage());
                    }
                }
                rowTotArr[j][0]+=columnTotaltArr[0];
                rowTotArr[j][1]+=columnTotaltArr[1];
                rowTotArr[j][2]+=columnTotaltArr[2];
                rowTotArr[j][3]+=columnTotaltArr[3];

                tv = new TextView(getActivity());
                tv.setId(i+20);// define id that must be unique
                tv.setText(String.valueOf(columnTotaltArr[0])+" | "+String.valueOf(columnTotaltArr[1])+" | "+String.valueOf(columnTotaltArr[2])+" | "+String.valueOf(columnTotaltArr[3])); // set the text for the header
                tv.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
                tv.setGravity(Gravity.CENTER_HORIZONTAL);
                tv.setTextColor(Color.BLUE); // set the color
                tv.setPadding(10, 5, 10, 5); // set the padding (if required)
                tableRow2.addView(tv); // add the column to the table row here

                tableLayout.addView(tableRow2);
            }
            //TODO: Row3
            TableRow tableRow3=new TableRow(getActivity());
            tableRow3.setId(i+200);
            tableRow3.setBackgroundColor(Color.LTGRAY);

            tv = new TextView(getActivity());
            tv.setId(i+30);// define id that must be unique
            tv.setText("Grand Total"); // set the text for the header
            tv.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            tv.setGravity(Gravity.LEFT);
            tv.setTextColor(Color.RED); // set the color
            tv.setPadding(10, 5, 10, 5); // set the padding (if required)
            tableRow3.addView(tv); // add the column to the table row here
            for(int f=0;f<sizeList.size()+1;f++)
            {
                tv = new TextView(getActivity());
                tv.setId(f+40);// define id that must be unique
                tv.setText(String.valueOf(rowTotArr[f][0])+" | "+String.valueOf(rowTotArr[f][1])+" | "+String.valueOf(rowTotArr[f][2])+" | "+String.valueOf(rowTotArr[f][3])); // set the text for the header
                tv.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
                tv.setGravity(Gravity.CENTER_HORIZONTAL);
                tv.setTextColor(Color.RED); // set the color
                tv.setPadding(10, 5, 10, 5); // set the padding (if required)
                tableRow3.addView(tv); // add the column to the table row here
            }
            tableLayout.addView(tableRow3);

        }
        //TODO: Display Stock Grid of SubItem Details
        private void SubItemDetails(TableLayout tableLayout,String ItemID){
            tableLayout.removeAllViews();
            tableLayout.removeAllViewsInLayout();
            List<Map<String,String>> dataListSubItem=DBHandler.getSubItemDetails(ItemID);
            if(!dataListSubItem.isEmpty()) {
                int i=0;
                TableRow tableRow=new TableRow(getActivity());
                tableRow.setId(i+10);
                tableRow.setBackgroundColor(Color.DKGRAY);

                TextView txt = new TextView(getActivity());
                txt.setId(i+1);
                txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
                txt.setGravity(Gravity.LEFT);
                txt.setText("SubItem Code");
                txt.setTypeface(Typeface.DEFAULT_BOLD);
                txt.setTextColor(Color.WHITE);
                txt.setPadding(5, 5, 5, 5);
                tableRow.addView(txt);// add the column to the table row here

                txt = new TextView(getActivity());
                txt.setId(i+2);
                txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
                txt.setGravity(Gravity.CENTER_HORIZONTAL);
                txt.setText("SubItem Name");
                txt.setTypeface(Typeface.DEFAULT_BOLD);
                txt.setTextColor(Color.WHITE);
                txt.setPadding(5, 5, 5, 5);
                tableRow.addView(txt);// add the column to the table row here

                txt = new TextView(getActivity());
                txt.setId(i+3);
                txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
                txt.setGravity(Gravity.CENTER_HORIZONTAL);
                txt.setText("Stock | Reserve | Rejection | Saleble");
                txt.setTypeface(Typeface.DEFAULT_BOLD);
                txt.setTextColor(Color.WHITE);
                txt.setPadding(5, 5, 5, 5);
                tableRow.addView(txt);// add the column to the table row here

                tableLayout.addView(tableRow);

                for (int j=0; j<dataListSubItem.size(); j++) {
                    //TODO:Table Row2
                    tableRow = new TableRow(getActivity());
                    tableRow.setId(j + 20);
                    tableRow.setBackgroundColor(Color.TRANSPARENT);

                    txt = new TextView(getActivity());
                    txt.setId(j + 1);
                    txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
                    txt.setGravity(Gravity.CENTER_HORIZONTAL);
                    txt.setText("" + dataListSubItem.get(j).get("SubItemCode"));
                    txt.setTypeface(Typeface.DEFAULT_BOLD);
                    txt.setTextColor(Color.BLACK);
                    txt.setPadding(5, 5, 5, 5);
                    tableRow.addView(txt);// add the column to the table row here

                    txt = new TextView(getActivity());
                    txt.setId(j + 2);
                    txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
                    txt.setGravity(Gravity.CENTER_HORIZONTAL);
                    txt.setText("" + dataListSubItem.get(j).get("SubItemName"));
                    txt.setTypeface(Typeface.DEFAULT_BOLD);
                    txt.setTextColor(Color.BLUE);
                    txt.setPadding(5, 5, 5, 5);
                    tableRow.addView(txt);// add the column to the table row here

                    txt = new TextView(getActivity());
                    txt.setId(j + 3);
                    txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
                    txt.setGravity(Gravity.CENTER_HORIZONTAL);
                    txt.setText(ConditionLibrary.ConvertRoundOff2Decimal(dataListSubItem.get(j).get("Stock"))+" | " + ConditionLibrary.ConvertRoundOff2Decimal(dataListSubItem.get(j).get("ReserveStock"))+" | " + ConditionLibrary.ConvertRoundOff2Decimal(dataListSubItem.get(j).get("RejectedStock"))+" | " + ConditionLibrary.ConvertRoundOff2Decimal(dataListSubItem.get(j).get("SlableStock")));
                    txt.setTypeface(Typeface.DEFAULT_BOLD);
                    txt.setTextColor(Color.RED);
                    txt.setPadding(5, 5, 5, 5);
                    tableRow.addView(txt);// add the column to the table row here

                    tableLayout.addView(tableRow);
                }
            }
        }
        //TODO: Display Stock Grid of Item Only Details
        private void ItemOnlyDetails(TableLayout tableLayout,String ItemID){
            tableLayout.removeAllViews();
            tableLayout.removeAllViewsInLayout();
            List<Map<String,String>> dataListItem=DBHandler.getWithoutMDItemDetails(ItemID);
            if(!dataListItem.isEmpty())
            {
                int i=0;
                //TODO: 1st Row
                TableRow tableRow=new TableRow(getActivity());
                tableRow.setId(i+10);
                tableRow.setBackgroundColor(Color.LTGRAY);

                TextView txt = new TextView(getActivity());
                txt.setId(i+1);
                txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
                txt.setGravity(Gravity.CENTER_HORIZONTAL);
                txt.setText("Stock");
                txt.setTypeface(Typeface.DEFAULT_BOLD);
                txt.setTextColor(Color.BLUE);
                txt.setPadding(5, 5, 5, 5);
                tableRow.addView(txt);// add the column to the table row here

                txt = new TextView(getActivity());
                txt.setId(i+2);
                txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
                txt.setGravity(Gravity.CENTER_HORIZONTAL);
                txt.setText("Reserve");
                txt.setTypeface(Typeface.DEFAULT_BOLD);
                txt.setTextColor(Color.BLUE);
                txt.setPadding(5, 5, 5, 5);
                tableRow.addView(txt);// add the column to the table row here

                txt = new TextView(getActivity());
                txt.setId(i+3);
                txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
                txt.setGravity(Gravity.CENTER_HORIZONTAL);
                txt.setText("Rejection");
                txt.setTypeface(Typeface.DEFAULT_BOLD);
                txt.setTextColor(Color.BLUE);
                txt.setPadding(5, 5, 5, 5);
                tableRow.addView(txt);// add the column to the table row here

                txt = new TextView(getActivity());
                txt.setId(i+4);
                txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
                txt.setGravity(Gravity.CENTER_HORIZONTAL);
                txt.setText("Slable");
                txt.setTypeface(Typeface.DEFAULT_BOLD);
                txt.setTextColor(Color.BLUE);
                txt.setPadding(5, 5, 5, 5);
                tableRow.addView(txt);// add the column to the table row here

                tableLayout.addView(tableRow);
                //TODO: 2nd Row
                tableRow=new TableRow(getActivity());
                tableRow.setId(i+20);
                tableRow.setBackgroundColor(Color.TRANSPARENT);

                txt = new TextView(getActivity());
                txt.setId(i+11);
                txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
                txt.setGravity(Gravity.CENTER_HORIZONTAL);
                txt.setText(dataListItem.get(0).get("Stock")+"");
                txt.setTypeface(Typeface.DEFAULT_BOLD);
                txt.setTextColor(Color.BLACK);
                txt.setPadding(5, 5, 5, 5);
                tableRow.addView(txt);// add the column to the table row here

                txt = new TextView(getActivity());
                txt.setId(i+12);
                txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
                txt.setGravity(Gravity.CENTER_HORIZONTAL);
                txt.setText(""+dataListItem.get(0).get("ReserveStock"));
                txt.setTypeface(Typeface.DEFAULT_BOLD);
                txt.setTextColor(Color.BLACK);
                txt.setPadding(5, 5, 5, 5);
                tableRow.addView(txt);// add the column to the table row here

                txt = new TextView(getActivity());
                txt.setId(i+13);
                txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
                txt.setGravity(Gravity.CENTER_HORIZONTAL);
                txt.setText(""+dataListItem.get(0).get("RejectedStock"));
                txt.setTypeface(Typeface.DEFAULT_BOLD);
                txt.setTextColor(Color.BLACK);
                txt.setPadding(5, 5, 5, 5);
                tableRow.addView(txt);// add the column to the table row here

                txt = new TextView(getActivity());
                txt.setId(i+14);
                txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
                txt.setGravity(Gravity.CENTER_HORIZONTAL);
                txt.setText(""+ConditionLibrary.ConvertRoundOff2Decimal(dataListItem.get(0).get("SlableStock")));
                txt.setTypeface(Typeface.DEFAULT_BOLD);
                txt.setTextColor(Color.BLACK);
                txt.setPadding(5, 5, 5, 5);
                tableRow.addView(txt);// add the column to the table row here

                tableLayout.addView(tableRow);

            }
        }
        //TODO: Dialog More Details
        private void DialogMoreDetails(String ItemID,int MDApplicable,int SubItemApplicable){
            final Dialog dialog = new Dialog(new ContextThemeWrapper(context, R.style.FullHeightDialog));
            dialog.setContentView(R.layout.dialog_table_layout);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            lp.gravity = Gravity.CENTER;
            dialog.getWindow().setAttributes(lp);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.show();
            TableLayout tableLayout = (TableLayout) dialog.findViewById(R.id.table_Layout);
            Button btnOk= (Button) dialog.findViewById(R.id.button_ok);
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            if (MDApplicable == 1) {
                MoreStockMatrixMDDetails(tableLayout, ItemID);
            }else{
                if (SubItemApplicable == 1){
                    MoreSubItemDetails(tableLayout,ItemID);
                }else {
                    MoreItemOnlyDetails(tableLayout,ItemID);
                }
            }
        }
        //TODO: Display Stock Grid of Multi Details
        private void MoreStockMatrixMDDetails(TableLayout tableLayout,String ItemID){
            tableLayout.removeAllViews();
            tableLayout.removeAllViewsInLayout();
            //TODO: Grid of Size by Stock and bookQty
            List<Map<String,String>> sizeList = DBHandler.getBarcodeScannerMatrixsize(ItemID);
            List<Map<String,String>> colorList = DBHandler.getBarcodeScannerMatrixColor(ItemID);
            List<Map<String, String>> mdQtyList = new ArrayList<>();
            int[][] rowTotArr=new  int[sizeList.size()+1][4];
            int[] columnTotaltArr=new  int[4];
            int Stock=0,ReserveStock=0,RejectedStock=0,SlableStock=0;

            int i=0;

            TableRow tableRow1=new TableRow(getActivity());
            tableRow1.setId(i+100);
            tableRow1.setBackgroundColor(Color.LTGRAY);

            TextView txt = new TextView(getActivity());
            txt.setId(i+11);
            txt.setText("Color");
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setGravity(Gravity.LEFT);
            txt.setTextColor(Color.BLACK);
            txt.setPadding(5, 5, 5, 5);
            tableRow1.addView(txt);// add the column to the table row here

            for(int k=0;k<sizeList.size();k++)
            {
                TextView tv = new TextView(getActivity());
                tv.setId(k);// define id that must be unique
                tv.setTag(sizeList.get(k).get("SizeID"));
                tv.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
                tv.setGravity(Gravity.CENTER_HORIZONTAL);
                tv.setText(sizeList.get(k).get("SizeName")); // set the text for the header
                tv.setTextColor(Color.BLUE); // set the color
                tv.setPadding(10, 5, 10, 5); // set the padding (if required)
                tableRow1.addView(tv); // add the column to the table row here
            }
            TextView tv = new TextView(getActivity());
            tv.setId(i+15);// define id that must be unique
            tv.setText("Total"); // set the text for the header
            tv.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
            tv.setTextColor(Color.BLACK); // set the color
            tv.setPadding(10, 5, 10, 5); // set the padding (if required)
            tableRow1.addView(tv); // add the column to the table row here
            tableLayout.addView(tableRow1);
            int j = 0,dynamicID=0;
            for (int m = 0; m < colorList.size(); m++)
            {
                //TODO; Row 2
                TableRow tableRow2=new TableRow(getActivity());
                tableRow2.setId(m+20);
                tableRow2.setBackgroundColor(Color.WHITE);

                columnTotaltArr[0]=0;
                try {
                    TextView tvColor = new TextView(getActivity());
                    tvColor.setId(89+i);// define id that must be unique
                    tvColor.setText(colorList.get(m).get("ColorName")); // set the text for the header
                    tvColor.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
                    tvColor.setGravity(Gravity.LEFT);
                    tvColor.setTextColor(Color.BLACK); // set the color
                    tvColor.setPadding(5, 5, 5, 5); // set the padding (if required)
                    tableRow2.addView(tvColor); // add the column to the table row here
                } catch (Exception e) {
                    Log.e("Exception", "Color:"+e.getMessage());
                }
                // inner for loop
                for (j = 0; j < sizeList.size(); j++)
                {
                    try {
                        mdQtyList=DBHandler.getStockItemTotal(colorList.get(m).get("ColorID"), sizeList.get(j).get("SizeID"),ItemID);
                        if(mdQtyList.isEmpty()){
                            Stock=0;
                            ReserveStock=0;
                            RejectedStock=0;
                            SlableStock=0;
                        }else {
                            Stock=Integer.valueOf(mdQtyList.get(0).get("Stock"));
                            ReserveStock=Integer.valueOf(mdQtyList.get(0).get("ReserveStock"));
                            RejectedStock=Integer.valueOf(mdQtyList.get(0).get("RejectedStock"));
                            SlableStock=Integer.valueOf(mdQtyList.get(0).get("SlableStock"));
                            columnTotaltArr[0]+=Stock;//Add value
                            columnTotaltArr[1]+=ReserveStock;//Add value
                            columnTotaltArr[2]+=RejectedStock;//Add value
                            columnTotaltArr[3]+=SlableStock;//Add value
                        }
                        tv = new TextView(getActivity());
                        tv.setId(dynamicID);// define id that must be unique
                        tv.setText(String.valueOf(Stock)+" | "+String.valueOf(ReserveStock)+" | "+String.valueOf(RejectedStock)+" | "+String.valueOf(SlableStock)); // set the text for the header
                        tv.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
                        tv.setGravity(Gravity.CENTER_HORIZONTAL);
                        tv.setTextColor(Color.BLACK); // set the color
                        tv.setPadding(10, 5, 10, 5); // set the padding (if required)
                        tableRow2.addView(tv); // add the column to the table row here

                        rowTotArr[j][0]+=Stock;
                        rowTotArr[j][1]+=ReserveStock;
                        rowTotArr[j][2]+=RejectedStock;
                        rowTotArr[j][3]+=SlableStock;
                        dynamicID++;
                    } catch (Exception e) {
                        Log.e("Exception", "Qty:"+e.getMessage());
                    }
                }
                rowTotArr[j][0]+=columnTotaltArr[0];
                rowTotArr[j][1]+=columnTotaltArr[1];
                rowTotArr[j][2]+=columnTotaltArr[2];
                rowTotArr[j][3]+=columnTotaltArr[3];

                tv = new TextView(getActivity());
                tv.setId(i+20);// define id that must be unique
                tv.setText(String.valueOf(columnTotaltArr[0])+" | "+String.valueOf(columnTotaltArr[1])+" | "+String.valueOf(columnTotaltArr[2])+" | "+String.valueOf(columnTotaltArr[3])); // set the text for the header
                tv.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
                tv.setGravity(Gravity.CENTER_HORIZONTAL);
                tv.setTextColor(Color.BLUE); // set the color
                tv.setPadding(10, 5, 10, 5); // set the padding (if required)
                tableRow2.addView(tv); // add the column to the table row here

                tableLayout.addView(tableRow2);
            }
            //TODO: Row3
            TableRow tableRow3=new TableRow(getActivity());
            tableRow3.setId(i+200);
            tableRow3.setBackgroundColor(Color.LTGRAY);

            tv = new TextView(getActivity());
            tv.setId(i+30);// define id that must be unique
            tv.setText("Grand Total"); // set the text for the header
            tv.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            tv.setGravity(Gravity.LEFT);
            tv.setTextColor(Color.RED); // set the color
            tv.setPadding(10, 5, 10, 5); // set the padding (if required)
            tableRow3.addView(tv); // add the column to the table row here
            for(int f=0;f<sizeList.size()+1;f++)
            {
                tv = new TextView(getActivity());
                tv.setId(f+40);// define id that must be unique
                tv.setText(String.valueOf(rowTotArr[f][0])+" | "+String.valueOf(rowTotArr[f][1])+" | "+String.valueOf(rowTotArr[f][2])+" | "+String.valueOf(rowTotArr[f][3])); // set the text for the header
                tv.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
                tv.setGravity(Gravity.CENTER_HORIZONTAL);
                tv.setTextColor(Color.RED); // set the color
                tv.setPadding(10, 5, 10, 5); // set the padding (if required)
                tableRow3.addView(tv); // add the column to the table row here
            }
            tableLayout.addView(tableRow3);

        }
        //TODO: Display Stock Grid of SubItem Details
        private void MoreSubItemDetails(TableLayout tableLayout,String ItemID){
            tableLayout.removeAllViews();
            tableLayout.removeAllViewsInLayout();
            List<Map<String,String>> dataListSubItem=DBHandler.getSubItemDetails(ItemID);
            if(!dataListSubItem.isEmpty()) {
                int i=0;
                TableRow tableRow=new TableRow(getActivity());
                tableRow.setId(i+10);
                tableRow.setBackgroundColor(Color.DKGRAY);

                TextView txt = new TextView(getActivity());
                txt.setId(i+1);
                txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
                txt.setGravity(Gravity.LEFT);
                txt.setText("SubItem Code");
                txt.setTypeface(Typeface.DEFAULT_BOLD);
                txt.setTextColor(Color.WHITE);
                txt.setPadding(5, 5, 5, 5);
                tableRow.addView(txt);// add the column to the table row here

                txt = new TextView(getActivity());
                txt.setId(i+2);
                txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
                txt.setGravity(Gravity.CENTER_HORIZONTAL);
                txt.setText("SubItem Name");
                txt.setTypeface(Typeface.DEFAULT_BOLD);
                txt.setTextColor(Color.WHITE);
                txt.setPadding(5, 5, 5, 5);
                tableRow.addView(txt);// add the column to the table row here

                txt = new TextView(getActivity());
                txt.setId(i+3);
                txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
                txt.setGravity(Gravity.CENTER_HORIZONTAL);
                txt.setText("Stock | Reserve | Rejection | Saleble");
                txt.setTypeface(Typeface.DEFAULT_BOLD);
                txt.setTextColor(Color.WHITE);
                txt.setPadding(5, 5, 5, 5);
                tableRow.addView(txt);// add the column to the table row here

                tableLayout.addView(tableRow);

                for (int j=0; j<dataListSubItem.size(); j++) {
                    //TODO:Table Row2
                    tableRow = new TableRow(getActivity());
                    tableRow.setId(j + 20);
                    tableRow.setBackgroundColor(Color.TRANSPARENT);

                    txt = new TextView(getActivity());
                    txt.setId(j + 1);
                    txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
                    txt.setGravity(Gravity.CENTER_HORIZONTAL);
                    txt.setText("" + dataListSubItem.get(j).get("SubItemCode"));
                    txt.setTypeface(Typeface.DEFAULT_BOLD);
                    txt.setTextColor(Color.BLACK);
                    txt.setPadding(5, 5, 5, 5);
                    tableRow.addView(txt);// add the column to the table row here

                    txt = new TextView(getActivity());
                    txt.setId(j + 2);
                    txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
                    txt.setGravity(Gravity.CENTER_HORIZONTAL);
                    txt.setText("" + dataListSubItem.get(j).get("SubItemName"));
                    txt.setTypeface(Typeface.DEFAULT_BOLD);
                    txt.setTextColor(Color.BLUE);
                    txt.setPadding(5, 5, 5, 5);
                    tableRow.addView(txt);// add the column to the table row here

                    txt = new TextView(getActivity());
                    txt.setId(j + 3);
                    txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
                    txt.setGravity(Gravity.CENTER_HORIZONTAL);
                    txt.setText(ConditionLibrary.ConvertRoundOff2Decimal(dataListSubItem.get(j).get("Stock"))+" | " + ConditionLibrary.ConvertRoundOff2Decimal(dataListSubItem.get(j).get("ReserveStock"))+" | " + ConditionLibrary.ConvertRoundOff2Decimal(dataListSubItem.get(j).get("RejectedStock"))+" | " + ConditionLibrary.ConvertRoundOff2Decimal(dataListSubItem.get(j).get("SlableStock")));
                    txt.setTypeface(Typeface.DEFAULT_BOLD);
                    txt.setTextColor(Color.RED);
                    txt.setPadding(5, 5, 5, 5);
                    tableRow.addView(txt);// add the column to the table row here

                    tableLayout.addView(tableRow);
                }
            }
        }
        //TODO: Display Stock Grid of Item Only Details
        private void MoreItemOnlyDetails(TableLayout tableLayout,String ItemID){
            tableLayout.removeAllViews();
            tableLayout.removeAllViewsInLayout();
            List<Map<String,String>> dataListItem=DBHandler.getWithoutMDItemDetails(ItemID);
            if(!dataListItem.isEmpty())
            {
                int i=0;
                //TODO: 1st Row
                TableRow tableRow=new TableRow(getActivity());
                tableRow.setId(i+10);
                tableRow.setBackgroundColor(Color.LTGRAY);

                TextView txt = new TextView(getActivity());
                txt.setId(i+1);
                txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
                txt.setGravity(Gravity.CENTER_HORIZONTAL);
                txt.setText("Stock");
                txt.setTypeface(Typeface.DEFAULT_BOLD);
                txt.setTextColor(Color.BLUE);
                txt.setPadding(5, 5, 5, 5);
                tableRow.addView(txt);// add the column to the table row here

                txt = new TextView(getActivity());
                txt.setId(i+2);
                txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
                txt.setGravity(Gravity.CENTER_HORIZONTAL);
                txt.setText("Reserve");
                txt.setTypeface(Typeface.DEFAULT_BOLD);
                txt.setTextColor(Color.BLUE);
                txt.setPadding(5, 5, 5, 5);
                tableRow.addView(txt);// add the column to the table row here

                txt = new TextView(getActivity());
                txt.setId(i+3);
                txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
                txt.setGravity(Gravity.CENTER_HORIZONTAL);
                txt.setText("Rejection");
                txt.setTypeface(Typeface.DEFAULT_BOLD);
                txt.setTextColor(Color.BLUE);
                txt.setPadding(5, 5, 5, 5);
                tableRow.addView(txt);// add the column to the table row here

                txt = new TextView(getActivity());
                txt.setId(i+4);
                txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
                txt.setGravity(Gravity.CENTER_HORIZONTAL);
                txt.setText("Slable");
                txt.setTypeface(Typeface.DEFAULT_BOLD);
                txt.setTextColor(Color.BLUE);
                txt.setPadding(5, 5, 5, 5);
                tableRow.addView(txt);// add the column to the table row here

                tableLayout.addView(tableRow);
                //TODO: 2nd Row
                tableRow=new TableRow(getActivity());
                tableRow.setId(i+20);
                tableRow.setBackgroundColor(Color.TRANSPARENT);

                txt = new TextView(getActivity());
                txt.setId(i+11);
                txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
                txt.setGravity(Gravity.CENTER_HORIZONTAL);
                txt.setText(dataListItem.get(0).get("Stock")+"");
                txt.setTypeface(Typeface.DEFAULT_BOLD);
                txt.setTextColor(Color.BLACK);
                txt.setPadding(5, 5, 5, 5);
                tableRow.addView(txt);// add the column to the table row here

                txt = new TextView(getActivity());
                txt.setId(i+12);
                txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
                txt.setGravity(Gravity.CENTER_HORIZONTAL);
                txt.setText(""+dataListItem.get(0).get("ReserveStock"));
                txt.setTypeface(Typeface.DEFAULT_BOLD);
                txt.setTextColor(Color.BLACK);
                txt.setPadding(5, 5, 5, 5);
                tableRow.addView(txt);// add the column to the table row here

                txt = new TextView(getActivity());
                txt.setId(i+13);
                txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
                txt.setGravity(Gravity.CENTER_HORIZONTAL);
                txt.setText(""+dataListItem.get(0).get("RejectedStock"));
                txt.setTypeface(Typeface.DEFAULT_BOLD);
                txt.setTextColor(Color.BLACK);
                txt.setPadding(5, 5, 5, 5);
                tableRow.addView(txt);// add the column to the table row here

                txt = new TextView(getActivity());
                txt.setId(i+14);
                txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
                txt.setGravity(Gravity.CENTER_HORIZONTAL);
                txt.setText(""+ConditionLibrary.ConvertRoundOff2Decimal(dataListItem.get(0).get("SlableStock")));
                txt.setTypeface(Typeface.DEFAULT_BOLD);
                txt.setTextColor(Color.BLACK);
                txt.setPadding(5, 5, 5, 5);
                tableRow.addView(txt);// add the column to the table row here

                tableLayout.addView(tableRow);


            }
        }
    }
    //TODO: Color wise fragment
    public static class ColorWiseFragment extends Fragment{
        private static final String COLOR_WISE = "ColorWise";
        private Context context;
        private ProgressDialog progressDialog;
        private ScrollView scrollView;
        Button btnScan,btnGodownWise,btnMoreDetails;
        EditText edtStyleOrBarcode;
        TableLayout tableLayout,tableLayoutItemDetails;
        private String Barcode = "",ItemID = "";
        private int MDApplicable = 0,SubItemApplicable = 0;
        DatabaseSqlLiteHandlerStockCheck DBHandler;
        public static ColorWiseFragment newInstance(String ColorWise) {
            ColorWiseFragment fragment = new ColorWiseFragment();
            Bundle args = new Bundle();
            args.putString(COLOR_WISE, ColorWise);
            fragment.setArguments(args);
            return fragment;
        }
        public ColorWiseFragment() { }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle paramBundle) {
            View rootView = inflater.inflate(R.layout.stockcheck_designwise, null);
            DBHandler=new DatabaseSqlLiteHandlerStockCheck(getActivity());
            Initialization(rootView);
            return rootView;
        }
        private void Initialization(View view){
            this.context = getActivity();
            this.DBHandler=new  DatabaseSqlLiteHandlerStockCheck(getActivity());
            btnScan = (Button) view.findViewById(R.id.scan_button);
            btnGodownWise = (Button) view.findViewById(R.id.button_GodownWise);
            btnMoreDetails = (Button) view.findViewById(R.id.button_More_Details);
            edtStyleOrBarcode = (EditText) view.findViewById(R.id.editText_style_or_barcode);
            tableLayout = (TableLayout) view.findViewById(R.id.tableLayout1);
            tableLayoutItemDetails = (TableLayout) view.findViewById(R.id.tableLayout);
            scrollView = (ScrollView) view.findViewById(R.id.scrollView);
            scrollView.setVisibility(View.GONE);
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
            btnScan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, BarcodeScanner.class);
                    startActivityForResult(intent,100);
                }
            });
            edtStyleOrBarcode.setHint("Barcode");
            edtStyleOrBarcode.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    // If the event is a key-down event on the "enter" button
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        // Perform action on Enter key press
                        Barcode = edtStyleOrBarcode.getText().toString().toUpperCase().trim();
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(edtStyleOrBarcode.getWindowToken(), 0);
                        if(!Barcode.isEmpty()) {
                            CallApiMethod();
                        }else{
                            MessageDialog.MessageDialog(context,"","Please enter the Barcode");
                        }
                        edtStyleOrBarcode.setText("");
                        return true;
                    }
                    return false;
                }
            });
            btnMoreDetails.setVisibility(View.GONE);
            btnMoreDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!ItemID.isEmpty()){
                        //DialogMoreDetails(ItemID,MDApplicable,SubItemApplicable);
                    }else {
                        MessageDialog.MessageDialog(context,"","ItemID is empty");
                    }
                }
            });
            btnGodownWise.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map<String,String> mapItemDetails = DBHandler.GetItemBasicDetails();
                    if (mapItemDetails!=null && !mapItemDetails.isEmpty()) {
                        Intent intent = new Intent(context, StockCheckGodownWiseActivity.class);
                        intent.putExtra("ItemID", ItemID);
                        intent.putExtra("Barcode", Barcode);
                        intent.putExtra("MDApplicable", Integer.valueOf(mapItemDetails.get("MDApplicable")));
                        intent.putExtra("SubItemApplicable", Integer.valueOf(mapItemDetails.get("SubItemApplicable")));
                        intent.putExtra("Flag", 1); //TODO: 0 means Design Wise and 1 Means Color Wise
                        startActivity(intent);
                    }else {
                        MessageDialog.MessageDialog(context,"","Please search barcode then check it.");
                    }

                }
            });
        }
        private void CallApiMethod(){
            //TODO: Select Customer for order Request
            NetworkChangeReceiver.bindNetworkListener(new NetworkListener() {
                @Override
                public void networkReceived(String status) {
                    if (!status.contentEquals("No Internet Connection")) {
                        LoginActivity obj=new LoginActivity();
                        String[] str = obj.GetSharePreferenceSession(context);
                        if (str!=null && (!Barcode.isEmpty() || Barcode!=null)) {
                            CallVolleyStockCheck(str[3], str[0], str[4], str[5], str[14],Barcode);
                        }
                    } else {
                        MessageDialog.MessageDialog(context,"",status);
                    }
                }
            });
            String status = NetworkUtils.getConnectivityStatusString(context);
            if (!status.contentEquals("No Internet Connection")) {
                LoginActivity obj=new LoginActivity();
                String[] str = obj.GetSharePreferenceSession(context);
                if (str!=null && (!Barcode.isEmpty() || Barcode!=null)) {
                    CallVolleyStockCheck(str[3], str[0], str[4], str[5], str[14],Barcode);
                }
            } else {
                MessageDialog messageDialog=new MessageDialog();
                messageDialog.MessageDialog(context,"","","No Internet Connection");
            }
        }
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data){
            if (requestCode == 100) {
                if (resultCode == Activity.RESULT_OK) {
                    Barcode = data.getExtras().getString("Barcode");
                    if (!Barcode.isEmpty()) {
                        //CallApiMethod();
                    } else {
                        MessageDialog.MessageDialog(context, "", "Please Scan a Barcode");
                    }
                }
                if (resultCode == Activity.RESULT_CANCELED) {
                    //Write your code if there's no result
                }
            }
        }
        //TODO: CallVolley Stock Check
        private void CallVolleyStockCheck(final String DeviceID,final String SessionID,final String UserID,final String DivisionID,final String CompanyID,final String Barcode){
            showpDialog();
            StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"StockCheck", new Response.Listener<String>()
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
                            ItemID = "";
                            JSONArray jsonArrayResult = jsonObject.getJSONArray("Result");
                            List<Map<String,String>> mapList = new ArrayList<>();
                            for (int i=0; i< jsonArrayResult.length(); i++){
                                MDApplicable = jsonArrayResult.getJSONObject(i).getInt("MDApplicable");
                                SubItemApplicable = jsonArrayResult.getJSONObject(i).getInt("SubItemApplicable");
                                ItemID = jsonArrayResult.getJSONObject(i).getString("ItemID");
                                Map<String,String> map = new HashMap<>();
                                map.put("Barcode",jsonArrayResult.getJSONObject(i).getString("Barcode"));
                                map.put("ItemID",jsonArrayResult.getJSONObject(i).getString("ItemID"));
                                map.put("ItemCode",jsonArrayResult.getJSONObject(i).getString("ItemCode"));
                                map.put("ItemName",(jsonArrayResult.getJSONObject(i).optString("ItemName")==null ? "": jsonArrayResult.getJSONObject(i).optString("ItemName")));
                                map.put("MainGroup",jsonArrayResult.getJSONObject(i).getString("MainGroup"));
                                map.put("Group",jsonArrayResult.getJSONObject(i).getString("GroupName"));
                                map.put("ColorID",jsonArrayResult.getJSONObject(i).getString("ColorID"));
                                map.put("Color",jsonArrayResult.getJSONObject(i).getString("Color"));
                                map.put("SizeID",jsonArrayResult.getJSONObject(i).getString("SizeID"));
                                map.put("Size",jsonArrayResult.getJSONObject(i).getString("Size"));
                                map.put("Sequence",jsonArrayResult.getJSONObject(i).getString("Sequence"));
                                map.put("MDStock",jsonArrayResult.getJSONObject(i).getString("MDStock"));
                                map.put("MDReserveStock",jsonArrayResult.getJSONObject(i).getString("MDReserveStock"));
                                map.put("MDRejectionStock",jsonArrayResult.getJSONObject(i).getString("MDRejectionStock"));
                                map.put("MDSaleableStock",(jsonArrayResult.getJSONObject(i).optString("MDSaleableStock")==null) ? "0" : jsonArrayResult.getJSONObject(i).optString("MDSaleableStock"));
                                map.put("MDSrvOrdStock",(jsonArrayResult.getJSONObject(i).optString("MDSrvOrdStock")==null) ? "0" : jsonArrayResult.getJSONObject(i).optString("MDSrvOrdStock"));
                                map.put("Price",jsonArrayResult.getJSONObject(i).getString("Rate"));
                                map.put("MDApplicable",jsonArrayResult.getJSONObject(i).getString("MDApplicable"));
                                map.put("SubItemApplicable",jsonArrayResult.getJSONObject(i).getString("SubItemApplicable"));
                                map.put("SubItemID",jsonArrayResult.getJSONObject(i).getString("SubItemID"));
                                map.put("SubItemName",jsonArrayResult.getJSONObject(i).getString("SubItemName"));
                                map.put("SubItemCode",(jsonArrayResult.getJSONObject(i).optString("SubItemCode")==null) ? "" : jsonArrayResult.getJSONObject(i).optString("SubItemCode"));
                                map.put("Stock",jsonArrayResult.getJSONObject(i).getString("Stock"));
                                map.put("ReserveStock",jsonArrayResult.getJSONObject(i).getString("ReserveStock"));
                                map.put("RejectionStock",jsonArrayResult.getJSONObject(i).getString("RejectionStock"));
                                map.put("SaleableStock",(jsonArrayResult.getJSONObject(i).optString("SaleableStock")==null) ? "0" : jsonArrayResult.getJSONObject(i).optString("SaleableStock"));
                                map.put("SrvOrdStock",(jsonArrayResult.getJSONObject(i).optString("SrvOrdStock")==null) ? "0" : jsonArrayResult.getJSONObject(i).optString("SrvOrdStock"));
                                map.put("GodownID",(jsonArrayResult.getJSONObject(i).optString("GodownID")==null ? "": jsonArrayResult.getJSONObject(i).optString("GodownID")));
                                map.put("GodownName",(jsonArrayResult.getJSONObject(i).optString("GodownName")==null ? "": jsonArrayResult.getJSONObject(i).optString("GodownName")));
                                mapList.add(map);
                            }
                            context.deleteDatabase(DatabaseSqlLiteHandlerStockCheck.DATABASE_NAME);
                            DBHandler.deleteStocks();
                            //DBHandler.deleteGodownStocks();
                            DBHandler.insertStockCheckTable(mapList);
                            if (!mapList.isEmpty()) {
                                scrollView.setVisibility(View.VISIBLE);
                                //TODO: Get Item Details
                                GetItemDetails();
                                if (MDApplicable == 1) {
                                    if (!ItemID.isEmpty())
                                        StockMatrixMDDetails(tableLayout,ItemID);
                                }
                            }else{
                                MessageDialog.MessageDialog(context,"","Entered Barcode is wrong");

                            }
                        } else {
                            MessageDialog.MessageDialog(context,"",""+Msg);
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
                    params.put("SessionID", SessionID);
                    params.put("UserID", UserID);
                    params.put("DivisionID", DivisionID);
                    params.put("CompanyID", CompanyID);
                    params.put("Barcode", Barcode);
                    Log.d(TAG,"Stock Check parameters:"+params.toString());
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
                //swipeRefreshLayout.setRefreshing(false);
            }
        }
        private void GetItemDetails(){
            tableLayoutItemDetails.removeAllViews();
            tableLayoutItemDetails.removeAllViewsInLayout();
            Map<String,String> mapItemDetails = DBHandler.GetItemBasicDetails();
            if (mapItemDetails!=null){
                //TODO:Item Code
                View v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayoutItemDetails, false);
                //v.setBackgroundColor(context.getResources().getColor(R.color.red_10));
                TextView txtHeader= (TextView) v.findViewById(R.id.header);
                txtHeader.setText("Barcode / Style:");

                TextView txt= (TextView) v.findViewById(R.id.content);
                String ItemCode = (mapItemDetails.get("ItemCode")==null || mapItemDetails.get("ItemCode").equals("null") ? "" :mapItemDetails.get("ItemCode"));
                if (Barcode.equals(ItemCode)) {
                    txtHeader.setText("Style:");
                    txt.setText(Barcode+"");
                }else{
                    txtHeader.setText("Barcode / Style:");
                    txt.setText(Barcode + " / " +ItemCode);
                }
                tableLayoutItemDetails.addView(v);
                //TODO:Item Name
                v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayoutItemDetails, false);
                //v.setBackgroundColor(context.getResources().getColor(R.color.red_10));
                txtHeader= (TextView) v.findViewById(R.id.header);
                txtHeader.setText("Item Name:");

                txt= (TextView) v.findViewById(R.id.content);
                txt.setText(""+(mapItemDetails.get("ItemName")==null || mapItemDetails.get("ItemName").equals("null") ? "" :mapItemDetails.get("ItemName")));
                tableLayoutItemDetails.addView(v);
                //TODO:Item Code
                v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayoutItemDetails, false);
                //v.setBackgroundColor(context.getResources().getColor(R.color.red_10));
                txtHeader= (TextView) v.findViewById(R.id.header);
                txtHeader.setText("MainGroup / Group:");

                txt= (TextView) v.findViewById(R.id.content);
                txt.setText(""+mapItemDetails.get("MainGroup")+ " / "+mapItemDetails.get("Group"));
                tableLayoutItemDetails.addView(v);
                //TODO:Remark
                v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayoutItemDetails, false);
                //v.setBackgroundColor(context.getResources().getColor(R.color.red_10));
                txtHeader= (TextView) v.findViewById(R.id.header);
                txtHeader.setText("*Remark:");
                txtHeader.setTextColor(Color.RED);

                txt= (TextView) v.findViewById(R.id.content);
                txt.setText("Stock | Reserve | Rejection | Saleable");
                tableLayoutItemDetails.addView(v);

            }else{
                MessageDialog.MessageDialog(context,"","Something went wrong");
            }
        }
        //TODO: Display Stock Grid of Multi Details
        private void StockMatrixMDDetails(TableLayout tableLayout,String ItemID){
            tableLayout.removeAllViews();
            tableLayout.removeAllViewsInLayout();
            //TODO: Grid of Size by Stock and bookQty
            List<Map<String,String>> sizeList = DBHandler.getBarcodeWiseSizeList(ItemID);
            List<Map<String,String>> colorList = DBHandler.getBarcodeWiseColorList(Barcode);
            List<Map<String, String>> mdQtyList = new ArrayList<>();
            int[][] rowTotArr=new  int[sizeList.size()+1][4];
            int[] columnTotaltArr=new  int[4];
            int Stock=0,ReserveStock=0,RejectedStock=0,SlableStock=0;

            int i=0;

            TableRow tableRow1=new TableRow(getActivity());
            tableRow1.setId(i+100);
            tableRow1.setBackgroundColor(Color.LTGRAY);

            TextView txt = new TextView(getActivity());
            txt.setId(i+11);
            txt.setText("Color");
            txt.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            txt.setGravity(Gravity.LEFT);
            txt.setTextColor(Color.BLACK);
            txt.setPadding(5, 5, 5, 5);
            tableRow1.addView(txt);// add the column to the table row here

            for(int k=0;k<sizeList.size();k++)
            {
                TextView tv = new TextView(getActivity());
                tv.setId(k);// define id that must be unique
                tv.setTag(sizeList.get(k).get("SizeID"));
                tv.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
                tv.setGravity(Gravity.CENTER_HORIZONTAL);
                tv.setText(sizeList.get(k).get("SizeName")); // set the text for the header
                tv.setTextColor(Color.BLUE); // set the color
                tv.setPadding(10, 5, 10, 5); // set the padding (if required)
                tableRow1.addView(tv); // add the column to the table row here
            }
            TextView tv = new TextView(getActivity());
            tv.setId(i+15);// define id that must be unique
            tv.setText("Total"); // set the text for the header
            tv.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
            tv.setTextColor(Color.BLACK); // set the color
            tv.setPadding(10, 5, 10, 5); // set the padding (if required)
            tableRow1.addView(tv); // add the column to the table row here
            tableLayout.addView(tableRow1);
            int j = 0,dynamicID=0;
            for (int m = 0; m < colorList.size(); m++)
            {
                //TODO; Row 2
                TableRow tableRow2=new TableRow(getActivity());
                tableRow2.setId(m+20);
                tableRow2.setBackgroundColor(Color.WHITE);

                columnTotaltArr[0]=0;
                try {
                    TextView tvColor = new TextView(getActivity());
                    tvColor.setId(89+i);// define id that must be unique
                    tvColor.setText(colorList.get(m).get("ColorName")); // set the text for the header
                    tvColor.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
                    tvColor.setGravity(Gravity.LEFT);
                    tvColor.setTextColor(Color.BLACK); // set the color
                    tvColor.setPadding(5, 5, 5, 5); // set the padding (if required)
                    tableRow2.addView(tvColor); // add the column to the table row here
                } catch (Exception e) {
                    Log.e("Exception", "Color:"+e.getMessage());
                }
                // inner for loop
                for (j = 0; j < sizeList.size(); j++)
                {
                    try {
                        mdQtyList=DBHandler.getStockItemTotal(colorList.get(m).get("ColorID"), sizeList.get(j).get("SizeID"),ItemID);
                        if(mdQtyList.isEmpty()){
                            Stock=0;
                            ReserveStock=0;
                            RejectedStock=0;
                            SlableStock=0;
                        }else {
                            Stock=Integer.valueOf(mdQtyList.get(0).get("Stock"));
                            ReserveStock=Integer.valueOf(mdQtyList.get(0).get("ReserveStock"));
                            RejectedStock=Integer.valueOf(mdQtyList.get(0).get("RejectedStock"));
                            SlableStock=Integer.valueOf(mdQtyList.get(0).get("SlableStock"));
                            columnTotaltArr[0]+=Stock;//Add value
                            columnTotaltArr[1]+=ReserveStock;//Add value
                            columnTotaltArr[2]+=RejectedStock;//Add value
                            columnTotaltArr[3]+=SlableStock;//Add value
                        }
                        tv = new TextView(getActivity());
                        tv.setId(dynamicID);// define id that must be unique
                        tv.setText(String.valueOf(Stock)+" | "+String.valueOf(ReserveStock)+" | "+String.valueOf(RejectedStock)+" | "+String.valueOf(SlableStock)); // set the text for the header
                        tv.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
                        tv.setGravity(Gravity.CENTER_HORIZONTAL);
                        tv.setTextColor(Color.BLACK); // set the color
                        tv.setPadding(10, 5, 10, 5); // set the padding (if required)
                        tableRow2.addView(tv); // add the column to the table row here

                        rowTotArr[j][0]+=Stock;
                        rowTotArr[j][1]+=ReserveStock;
                        rowTotArr[j][2]+=RejectedStock;
                        rowTotArr[j][3]+=SlableStock;
                        dynamicID++;
                    } catch (Exception e) {
                        Log.e("Exception", "Qty:"+e.getMessage());
                    }
                }
                rowTotArr[j][0]+=columnTotaltArr[0];
                rowTotArr[j][1]+=columnTotaltArr[1];
                rowTotArr[j][2]+=columnTotaltArr[2];
                rowTotArr[j][3]+=columnTotaltArr[3];

                tv = new TextView(getActivity());
                tv.setId(i+20);// define id that must be unique
                tv.setText(String.valueOf(columnTotaltArr[0])+" | "+String.valueOf(columnTotaltArr[1])+" | "+String.valueOf(columnTotaltArr[2])+" | "+String.valueOf(columnTotaltArr[3])); // set the text for the header
                tv.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
                tv.setGravity(Gravity.CENTER_HORIZONTAL);
                tv.setTextColor(Color.BLUE); // set the color
                tv.setPadding(10, 5, 10, 5); // set the padding (if required)
                tableRow2.addView(tv); // add the column to the table row here

                tableLayout.addView(tableRow2);
            }
            //TODO: Row3
            TableRow tableRow3=new TableRow(getActivity());
            tableRow3.setId(i+200);
            tableRow3.setBackgroundColor(Color.LTGRAY);

            tv = new TextView(getActivity());
            tv.setId(i+30);// define id that must be unique
            tv.setText("Grand Total"); // set the text for the header
            tv.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
            tv.setGravity(Gravity.LEFT);
            tv.setTextColor(Color.RED); // set the color
            tv.setPadding(10, 5, 10, 5); // set the padding (if required)
            tableRow3.addView(tv); // add the column to the table row here
            for(int f=0;f<sizeList.size()+1;f++)
            {
                tv = new TextView(getActivity());
                tv.setId(f+40);// define id that must be unique
                tv.setText(String.valueOf(rowTotArr[f][0])+" | "+String.valueOf(rowTotArr[f][1])+" | "+String.valueOf(rowTotArr[f][2])+" | "+String.valueOf(rowTotArr[f][3])); // set the text for the header
                tv.setBackground(context.getResources().getDrawable(R.drawable.cell_border));
                tv.setGravity(Gravity.CENTER_HORIZONTAL);
                tv.setTextColor(Color.RED); // set the color
                tv.setPadding(10, 5, 10, 5); // set the padding (if required)
                tableRow3.addView(tv); // add the column to the table row here
            }
            tableLayout.addView(tableRow3);

        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
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
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            //Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }
    }
}
