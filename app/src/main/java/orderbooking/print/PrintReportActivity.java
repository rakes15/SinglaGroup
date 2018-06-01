package orderbooking.print;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.drawable.PictureDrawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.singlagroup.AppController;
import com.singlagroup.BuildConfig;
import com.singlagroup.LoginActivity;
import com.singlagroup.R;
import com.singlagroup.customwidgets.DateFormatsMethods;
import com.singlagroup.customwidgets.MessageDialog;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import DatabaseController.CommanStatic;
import DatabaseController.DatabaseSqlLiteHandlerUserInfo;
import orderbooking.Database_Sqlite.DatabaseSqlLiteHandlerOrderPrintReport;
import orderbooking.StaticValues;
import orderbooking.view_order_details.adapter.OrderViewGroupAdapter;
import services.NetworkChangeReceiver;
import services.NetworkListener;
import services.NetworkUtils;

/**
 * Created by Rakesh on 13-Oct-16.
 */

public class PrintReportActivity extends AppCompatActivity {
    ActionBar actionBar;
    private Context context;
    private WebView webView;
    private ImageView imageView;
    private Bitmap bm;
    private String HtmlContent = "";
    private String OrderID = "", GroupID = "", ItemID = "", DivisionName = "";
    private ProgressDialog progressDialog;
    DatabaseSqlLiteHandlerOrderPrintReport DBHandler;
    private static String TAG = PrintReportActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (CommanStatic.Screenshot == 0) { getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE); }
        setContentView(R.layout.activity_web_view);
        Initialization();
        ModulePermission();
    }
    private void Initialization() {
        this.context = PrintReportActivity.this;
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        webView = (WebView) findViewById(R.id.web_view);
        imageView = (ImageView) findViewById(R.id.image_view);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
    }
    private void ModulePermission() {
        try {
            OrderID = getIntent().getExtras().getString("OrderID", "");
            GroupID = getIntent().getExtras().getString("GroupID", "");
            ItemID = getIntent().getExtras().getString("ItemID", "");
            if (StaticValues.printFlag == 1) {
                CallApiMethod();
            } else {
                MessageDialog.MessageDialog(PrintReportActivity.this, "Alert", "You don't have print permission of this module");
            }
        } catch (Exception e) {
            MessageDialog.MessageDialog(context, "", "" + e.toString());
        }
    }
    private void CallApiMethod() {
        //TODO: Closed Order Request
        NetworkChangeReceiver.bindNetworkListener(new NetworkListener() {
            @Override
            public void networkReceived(String status) {
                if (!status.contentEquals("No Internet Connection")) {
                    LoginActivity obj = new LoginActivity();
                    String[] str = obj.GetSharePreferenceSession(PrintReportActivity.this);
                    if (str != null) {
                        CallVolleyPrintReport(str[3], str[4], str[0], str[5], str[14], str[15], OrderID, GroupID, ItemID);
                        DatabaseSqlLiteHandlerUserInfo DBInfo = new DatabaseSqlLiteHandlerUserInfo(context);
                        DivisionName = DBInfo.getDefaultDivision(str[14])[1];
                    }
                } else {
                    MessageDialog.MessageDialog(PrintReportActivity.this,"", status);
                }
            }
        });
        String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
        if (!status.contentEquals("No Internet Connection")) {
            LoginActivity obj = new LoginActivity();
            String[] str = obj.GetSharePreferenceSession(PrintReportActivity.this);
            if (str != null) {
                CallVolleyPrintReport(str[3], str[4], str[0], str[5], str[14], str[15], OrderID, GroupID, ItemID);
                DatabaseSqlLiteHandlerUserInfo DBInfo = new DatabaseSqlLiteHandlerUserInfo(context);
                DivisionName = DBInfo.getDefaultDivision(str[14])[1];
            }
        } else {
            MessageDialog messageDialog = new MessageDialog();
            messageDialog.MessageDialog(PrintReportActivity.this, "", "", status);
        }
    }
    private void CallVolleyPrintReport(final String DeviceID, final String UserID, final String SessionID, final String DivisionID, final String CompanyID, final String BranchID, final String OrderID, final String GroupID, final String ItemID) {
        showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL + "PartyTempOrderDetails", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // response
                Log.d("Response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int Status = (jsonObject.getString("status") == null) ? 0 : jsonObject.getInt("status");
                    String Msg = (jsonObject.getString("msg") == null) ? "Server is not responding" : jsonObject.getString("msg");
                    if (Status == 1) {
                        List<Map<String, String>> mapList = new ArrayList<>();
                        JSONArray jsonArrayResult = jsonObject.getJSONArray("Result");
                        for (int z = 0; z < jsonArrayResult.length(); z++) {
                            String OrderID = jsonArrayResult.getJSONObject(z).getString("OrderID");
                            JSONArray jsonArrayDetails = jsonArrayResult.getJSONObject(z).getJSONArray("Details");
                            for (int i = 0; i < jsonArrayDetails.length(); i++) {
                                Map<String, String> map = new HashMap<>();
//                                MDApplicable = jsonArrayDetails.getJSONObject(i).getInt("MDApplicable");
//                                SubItemApplicable = jsonArrayDetails.getJSONObject(i).getInt("SubItemApplicable");
                                map.put("OrderID", OrderID);
                                map.put("PartyID", jsonArrayDetails.getJSONObject(i).getString("PartyID"));
                                map.put("PartyName", jsonArrayDetails.getJSONObject(i).getString("PartyName"));
                                map.put("SubPartyID", (jsonArrayDetails.getJSONObject(i).getString("SubPartyID") == null || jsonArrayDetails.getJSONObject(i).getString("SubPartyID").equals("null")) ? "" : jsonArrayDetails.getJSONObject(i).getString("SubPartyID"));
                                map.put("SubParty", (jsonArrayDetails.getJSONObject(i).getString("SubParty") == null || jsonArrayDetails.getJSONObject(i).getString("SubParty").equals("null")) ? "" : jsonArrayDetails.getJSONObject(i).getString("SubParty"));
                                map.put("RefName", jsonArrayDetails.getJSONObject(i).getString("RefName"));
                                map.put("Remarks", jsonArrayDetails.getJSONObject(i).getString("Remarks"));
                                map.put("OrderDate", jsonArrayDetails.getJSONObject(i).getString("OrderDate"));
                                map.put("OrderNo", jsonArrayDetails.getJSONObject(i).getString("OrderNo"));
                                map.put("MainGroupID", jsonArrayDetails.getJSONObject(i).getString("MainGroupID"));
                                map.put("MainGroup", jsonArrayDetails.getJSONObject(i).getString("MainGroup"));
                                map.put("GroupID", jsonArrayDetails.getJSONObject(i).getString("GroupID"));
                                map.put("GroupName", jsonArrayDetails.getJSONObject(i).getString("GroupName"));
                                map.put("GroupImage", "");
                                map.put("SubGroupID", jsonArrayDetails.getJSONObject(i).getString("SubGroupID"));
                                map.put("SubGroup", jsonArrayDetails.getJSONObject(i).getString("SubGroup"));
                                map.put("ItemID", jsonArrayDetails.getJSONObject(i).getString("ItemID"));
                                map.put("ItemName", jsonArrayDetails.getJSONObject(i).getString("ItemName"));
                                map.put("ItemCode", jsonArrayDetails.getJSONObject(i).getString("ItemCode"));
                                map.put("SubItemID", (jsonArrayDetails.getJSONObject(i).getString("SubItemID") == null || jsonArrayDetails.getJSONObject(i).getString("SubItemID").equals("null")) ? "" : jsonArrayDetails.getJSONObject(i).getString("SubItemID"));
                                map.put("SubItemName", (jsonArrayDetails.getJSONObject(i).getString("SubItemName") == null || jsonArrayDetails.getJSONObject(i).getString("SubItemName").equals("null")) ? "" : jsonArrayDetails.getJSONObject(i).getString("SubItemName"));
                                map.put("SubItemCode", (jsonArrayDetails.getJSONObject(i).getString("SubItemCode") == null || jsonArrayDetails.getJSONObject(i).getString("SubItemCode").equals("null")) ? "" : jsonArrayDetails.getJSONObject(i).getString("SubItemCode"));
                                map.put("ImageStatus", jsonArrayDetails.getJSONObject(i).getString("ImageStatus"));
                                map.put("ImageUrl", jsonArrayDetails.getJSONObject(i).getString("ImageUrl"));
                                map.put("ColorFamilyID", jsonArrayDetails.getJSONObject(i).getString("ColorFamilyID"));
                                map.put("ColorFamily", jsonArrayDetails.getJSONObject(i).getString("ColorFamily"));
                                map.put("ColorID", jsonArrayDetails.getJSONObject(i).getString("ColorID"));
                                map.put("Color", jsonArrayDetails.getJSONObject(i).getString("Color"));
                                map.put("SizeID", jsonArrayDetails.getJSONObject(i).getString("SizeID"));
                                map.put("SizeName", jsonArrayDetails.getJSONObject(i).getString("SizeName"));
                                map.put("BookQty", jsonArrayDetails.getJSONObject(i).getString("bookQty"));
                                map.put("BookFrom", jsonArrayDetails.getJSONObject(i).getString("BookFrom"));
                                map.put("ExcepDelDt", jsonArrayDetails.getJSONObject(i).getString("ExcepDelDt"));
                                map.put("Rate", jsonArrayDetails.getJSONObject(i).getString("Rate"));
                                map.put("MDApplicable", jsonArrayDetails.getJSONObject(i).getString("MDApplicable"));
                                map.put("SubItemApplicable", jsonArrayDetails.getJSONObject(i).getString("SubItemApplicable"));
                                map.put("CreatedDate", jsonArrayDetails.getJSONObject(i).getString("CreatedDate"));
                                map.put("TBookedAmt", jsonArrayDetails.getJSONObject(i).getString("Amount"));

                                map.put("Barcode", jsonArrayDetails.getJSONObject(i).getString("Barcode"));
                                map.put("Unit", jsonArrayDetails.getJSONObject(i).getString("Unit"));
                                map.put("UserName", jsonArrayDetails.getJSONObject(i).getString("UserName"));
                                map.put("UserFullName", jsonArrayDetails.getJSONObject(i).getString("UserFullName"));
                                map.put("EmpCVType", jsonArrayDetails.getJSONObject(i).getString("EmpCVType"));
                                map.put("EmpCVName", jsonArrayDetails.getJSONObject(i).getString("EmpCVName"));

                                map.put("AttribID1", jsonArrayDetails.getJSONObject(i).getString("AttribID1"));
                                map.put("AttribID2", jsonArrayDetails.getJSONObject(i).getString("AttribID2"));
                                map.put("AttribID3", jsonArrayDetails.getJSONObject(i).getString("AttribID3"));
                                map.put("AttribID4", jsonArrayDetails.getJSONObject(i).getString("AttribID4"));
                                map.put("AttribID5", jsonArrayDetails.getJSONObject(i).getString("AttribID5"));
                                map.put("AttribID6", jsonArrayDetails.getJSONObject(i).getString("AttribID6"));
                                map.put("AttribID7", jsonArrayDetails.getJSONObject(i).getString("AttribID7"));
                                map.put("AttribID8", jsonArrayDetails.getJSONObject(i).getString("AttribID8"));
                                map.put("AttribID9", jsonArrayDetails.getJSONObject(i).getString("AttribID9"));
                                map.put("AttribID10", jsonArrayDetails.getJSONObject(i).getString("AttribID10"));

                                map.put("AttribName1", jsonArrayDetails.getJSONObject(i).getString("AttribName1"));
                                map.put("AttribName2", jsonArrayDetails.getJSONObject(i).getString("AttribName2"));
                                map.put("AttribName3", jsonArrayDetails.getJSONObject(i).getString("AttribName3"));
                                map.put("AttribName4", jsonArrayDetails.getJSONObject(i).getString("AttribName4"));
                                map.put("AttribName5", jsonArrayDetails.getJSONObject(i).getString("AttribName5"));
                                map.put("AttribName6", jsonArrayDetails.getJSONObject(i).getString("AttribName6"));
                                map.put("AttribName7", jsonArrayDetails.getJSONObject(i).getString("AttribName7"));
                                map.put("AttribName8", jsonArrayDetails.getJSONObject(i).getString("AttribName8"));
                                map.put("AttribName9", jsonArrayDetails.getJSONObject(i).getString("AttribName9"));
                                map.put("AttribName10", jsonArrayDetails.getJSONObject(i).getString("AttribName10"));
                                mapList.add(map);
                            }
                        }
                        context.deleteDatabase(DatabaseSqlLiteHandlerOrderPrintReport.DATABASE_NAME);
                        DBHandler = new DatabaseSqlLiteHandlerOrderPrintReport(context);
                        DBHandler.deleteOrderDetails();
                        DBHandler.insertOrderDetails(mapList);
                        if (!OrderID.isEmpty()) {
                            if (GroupID.isEmpty()) {
                                System.out.println("Group blank");
                                //TODO: Order Wise Print
                                if (OrderViewGroupAdapter.listGroup.isEmpty()) {
                                    PrintWebView(DBHandler.getGroupMainGroupList(OrderID), OrderID);
                                } else {
                                    List<Map<String, String>> groupList = new ArrayList<>();
                                    for (int i = 0; i < OrderViewGroupAdapter.listGroup.size(); i++) {
                                        Map<String, String> map = new HashMap<String, String>();
                                        map.put("GroupID", OrderViewGroupAdapter.listGroup.get(i).getGroupID());
                                        map.put("GroupName", OrderViewGroupAdapter.listGroup.get(i).getGroupName());
                                        map.put("MainGroupID", OrderViewGroupAdapter.listGroup.get(i).getMainGroupID());
                                        map.put("MainGroup", OrderViewGroupAdapter.listGroup.get(i).getMainGroup());
                                        groupList.add(map);
                                    }
                                    PrintWebView(groupList, OrderID);
                                }
                            } else {
                                if (ItemID.isEmpty()) {
                                    //TODO: Group Wise Print
                                    System.out.println("Item blank");
                                    if (OrderViewGroupAdapter.listGroup.isEmpty()) {
                                        PrintWebView(DBHandler.getGroupMainGroupList(OrderID), OrderID);
                                    } else {
                                        List<Map<String, String>> groupList = new ArrayList<>();
                                        for (int i = 0; i < OrderViewGroupAdapter.listGroup.size(); i++) {
                                            Map<String, String> map = new HashMap<String, String>();
                                            map.put("GroupID", OrderViewGroupAdapter.listGroup.get(i).getGroupID());
                                            map.put("GroupName", OrderViewGroupAdapter.listGroup.get(i).getGroupName());
                                            map.put("MainGroupID", OrderViewGroupAdapter.listGroup.get(i).getMainGroupID());
                                            map.put("MainGroup", OrderViewGroupAdapter.listGroup.get(i).getMainGroup());
                                            groupList.add(map);
                                        }
                                        PrintWebView(groupList, OrderID);
                                    }
                                } else {
                                    System.out.println("Item");
                                    //TODO: Item Wise Print
                                    if (OrderViewGroupAdapter.listGroup.isEmpty()) {
                                        PrintWebView(DBHandler.getGroupMainGroupList(OrderID), OrderID);
                                    } else {
                                        List<Map<String, String>> groupList = new ArrayList<>();
                                        for (int i = 0; i < OrderViewGroupAdapter.listGroup.size(); i++) {
                                            Map<String, String> map = new HashMap<String, String>();
                                            map.put("GroupID", OrderViewGroupAdapter.listGroup.get(i).getGroupID());
                                            map.put("GroupName", OrderViewGroupAdapter.listGroup.get(i).getGroupName());
                                            map.put("MainGroupID", OrderViewGroupAdapter.listGroup.get(i).getMainGroupID());
                                            map.put("MainGroup", OrderViewGroupAdapter.listGroup.get(i).getMainGroup());
                                            groupList.add(map);
                                        }
                                        PrintWebView(groupList, OrderID);
                                    }
                                }
                            }
                        }
                    } else {
                        MessageDialog.MessageDialog(context, "", Msg);
                    }
                } catch (Exception e) {
                    MessageDialog.MessageDialog(context, "Exception", "" + e.toString());
                }
                hidepDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", "" + error);
                MessageDialog.MessageDialog(context, "Error", "" + error.toString());
                hidepDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("DeviceID", DeviceID);
                params.put("UserID", UserID);
                params.put("SessionID", SessionID);
                params.put("DivisionID", DivisionID);
                params.put("CompanyID", CompanyID);
                params.put("BranchID", BranchID);
                params.put("OrderID", OrderID);
                params.put("GroupID", GroupID);
                Log.d(TAG, "Order view details parameters:" + params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
    }
    private void showpDialog() {
        if (progressDialog != null) {
            progressDialog.show();
        }
    }
    private void hidepDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        //ModulePermission();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case android.R.id.home:
                //TODO: Activity finish
                finish();
                break;
            case R.id.Print:
                //DialogPdfShareOptions();
                takeScreenshot();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            // Stop your service here
            System.out.println("This app is close");
            finishAffinity();
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {

            //TODO: Activity finish
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_custom, menu);
        MenuItem Save = menu.findItem(R.id.Save_Next);
        Save.setVisible(false);
        MenuItem view = menu.findItem(R.id.Report_View);
        view.setVisible(false);
        MenuItem Push = menu.findItem(R.id.Push_Fca);
        Push.setVisible(false);
        MenuItem exDateUpdate = menu.findItem(R.id.Expected_Datetime);
        exDateUpdate.setVisible(false);
        MenuItem print = menu.findItem(R.id.Print);
        print.setVisible(true);
        return super.onCreateOptionsMenu(menu);
    }
    private void PrintWebView(List<Map<String, String>> mapList, String OrderID) {
        if (!mapList.isEmpty()) {
            //webView.setHorizontalScrollBarEnabled(true);
            //webView.setVerticalScrollBarEnabled(true);
            webView.setWebViewClient(new WebViewClient() {
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    return false;
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    //print(view);
                    //imageView.setImageBitmap(bm);
                    //webView = null;
//                    if (!HtmlContent.isEmpty())
//                        HtmltoPDF(HtmlContent);
//                    else
//                        MessageDialog.MessageDialog(context,"","Html Content Empty");
                    //createPdf();
                }
            });
//            String SubPartyName = (DBHandler.getPartyDetails().get("SubPartyName").isEmpty()) ? "" : "<tr><td width=\"20%\" style=\"padding:5px 5px 5px 20px;\">SubParty Name : </td><td width=\"30%\" style=\"padding:5px 5px 5px 20px;color:#006;\"><strong>" + DBHandler.getPartyDetails().get("SubPartyName") + "</strong></td></tr>";
//            String RefName = (DBHandler.getPartyDetails().get("RefName").isEmpty()) ? "" : "<tr><td width=\"20%\" style=\"padding:5px 5px 5px 20px;\">Reference Name : </td><td width=\"30%\" style=\"padding:5px 5px 5px 20px;color:#006;\"><strong>" + DBHandler.getPartyDetails().get("RefName") + "</strong></td></tr>";
//            String str2 = "<html><body><div style=\"width:90%; margin:0 auto; border:1px solid #333;\">"
//                    + "<p align=\"right\" style=\"font-size:8px; margin-right:2px;\">Print by: " + DateFormatsMethods.DateFormat_DD_MM_YYYY(DateFormatsMethods.getDateTime().substring(0, 10)) + " " + DateFormatsMethods.getDateTime().substring(11) + "</p>"
//                    + "<p align=\"center\" style=\"font-size:18px; margin-bottom:0px; margin-top:0px;\">"
//                    + "<strong>" + DivisionName + "</strong></p>"
//                    + "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-size:14px;\" border=\"1\" bordercolor=\"#CCCCCC\">"
//                    + "<tr><td width=\"20%\" style=\"padding:5px 5px 5px 20px;\">Order No : </td><td width=\"30%\" style=\"padding:5px 5px 5px 20px;color:red;\"><strong>" + DBHandler.getPartyDetails().get("OrderNo") + "</strong></td></tr>"
//                    + "<tr><td width=\"20%\" style=\"padding:5px 5px 5px 20px;\">Order Date : </td><td width=\"30%\" style=\"padding:5px 5px 5px 20px;\"><strong>" + DateFormatsMethods.DateFormat_DD_MM_YYYY(DBHandler.getPartyDetails().get("OrderDate").substring(0, 10)) + "</strong></td></tr>"
//                    + "<tr><td width=\"20%\" style=\"padding:5px 5px 5px 20px;\">Party Name : </td><td width=\"30%\" style=\"padding:5px 5px 5px 20px;color:#006;\"><strong>" + DBHandler.getPartyDetails().get("PartyName") + "</strong></td></tr>"
//                    + SubPartyName + RefName;
//            //*Remarks: P/B/D (P-Pending/B-Booked/D-Delivered)
//            //str2+="<tr><td colspan=\"2\" width=\"100%\" style=\"padding:5px 5px 5px 20px;\">*Remarks: B/D (B-Booked/D-Delivered)</td></tr> ";
//            str2 += "<tr><td colspan=\"2\"><table width=\"100%\" style=\"float:left\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-size:12px;\" border=\"1\" bordercolor=\"#CCCCCC\">";
//            int grandTotal = 0;
//            for (int g = 0; g < mapList.size(); g++) {
//                str2 += "<tr><td align=\"center\" style=\"font-size:12px; color:blue\" ><strong>Group : <br/> MainGroup : </strong></td><td align=\"center\" style=\"font-size:12px; color:blue\"><strong>" + mapList.get(g).get("GroupName") + " <br/> " + mapList.get(g).get("MainGroup") + " </strong> </td></tr>";
//                String GroupID = mapList.get(g).get("GroupID");
//                List<Map<String, String>> dataListItem = DBHandler.getItemListWithMDOrSubItemApplicable(OrderID, GroupID);
//                String itemId;
//                for (int i = 0; i < dataListItem.size(); i++) {
//                    itemId = dataListItem.get(i).get("ItemID");
//                    int MDApplicable = Integer.valueOf(dataListItem.get(i).get("MDApplicable"));
//                    int SubItemApplicable = Integer.valueOf(dataListItem.get(i).get("SubItemApplicable"));
//                    if (MDApplicable == 1) {
//                        //TODO: MDApplicable
//                        List<Map<String, String>> SizeList = DBHandler.getSizeList(OrderID, GroupID, itemId);
//                        List<Map<String, String>> ColorList = DBHandler.getColorList(OrderID, GroupID, itemId);
//                        Map<String, String> mapQty = new HashMap<>();
//
//                        str2 += "<tr><td colspan=\"" + SizeList.size() + 2 + "\"><table width=\"100%\" style=\"float:left\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-size:14px;\" border=\"1\" bordercolor=\"#CCCCCC\"> ";
//
//                        int bookedQty = 0;
//                        int[][] rowTotArr = new int[SizeList.size() + 1][1];
//                        int[] columnTotaltArr = new int[1];
//
//                        str2 += "<tr><td align=\"center\"><strong>" + dataListItem.get(i).get("ItemCode") + "</strong></td><td colspan=\"" + SizeList.size() + 1 + "\" align=\"center\">" + dataListItem.get(i).get("Attr2") + "," + dataListItem.get(i).get("Attr6") + (dataListItem.get(i).get("Attr7") == null ? "" : "," + dataListItem.get(i).get("Attr7")) + "," + dataListItem.get(i).get("Attr8") + "</td></tr>";
//
//                        str2 += "<tr><td align=\"center\">Color </td>";
//                        for (int x = 0; x < SizeList.size(); x++) {
//                            str2 += "<td align=\"center\">" + SizeList.get(x).get("SizeName") + " </td>";
//                        }
//                        str2 += "<td align=\"center\">Total</td></tr>";
//                        int z = 0;
//                        for (int x = 0; x < ColorList.size(); x++) {
//                            str2 += "<tr><td align=\"center\">" + ColorList.get(x).get("ColorName") + "</td>";
//                            columnTotaltArr[0] = 0;
//                            for (z = 0; z < SizeList.size(); z++) {
//                                try {
//                                    mapQty = DBHandler.getMDQauntity(OrderID, GroupID, itemId, ColorList.get(x).get("ColorID"), SizeList.get(z).get("SizeID"));
//                                    if (!mapQty.isEmpty()) {
//                                        bookedQty = Integer.parseInt(mapQty.get("BookedQty"));
//                                        columnTotaltArr[0] += bookedQty;
//                                    } else {
//                                        bookedQty = 0;
//                                    }
//                                    str2 += "<td align=\"center\">" + bookedQty + "</td>";
//
//                                    rowTotArr[z][0] += bookedQty;
//
//                                } catch (Exception e) {
//                                    Log.e("Exception:", "" + e.getMessage());
//                                }
//                            }
//                            rowTotArr[z][0] += columnTotaltArr[0];
//
//                            str2 += "<td align=\"center\">" + columnTotaltArr[0] + "</td>";
//                            str2 += "</tr>";
//                        }
//                        str2 += "<tr><td align=\"center\">Total </td>";
//
//                        for (int x = 0; x < SizeList.size() + 1; x++) {
//                            str2 += "<td align=\"center\"> <strong>" + rowTotArr[x][0] + "</strong></td>";
//                            if (x == SizeList.size()) {
//                                grandTotal += rowTotArr[x][0];
//                            }
//                        }
//                        Map<String, String> mapUserDetails = DBHandler.getUserDetails(OrderID, GroupID, itemId);
//                        str2 += "</tr>";
//                        str2 += "<tr><td colspan=\"2\" style=\"font-size:12px;\">Remarks :</td><td colspan=\"4\" align=\"center\"><strong>" + dataListItem.get(i).get("Remarks") + "</strong></td></tr>";
//                        str2 += "<tr><td colspan=\"2\" style=\"font-size:12px;\">Expacted Delivery date :</td><td colspan=\"4\" align=\"center\" style=\"font-size:10px;\">" + DateFormatsMethods.DateFormat_DD_MM_YYYY(mapUserDetails.get("ExpectedDate").substring(0, 10)) + " " + mapUserDetails.get("ExpectedDate").substring(11) + "</td></tr>";
//                        str2 += "<tr><td colspan=\"2\" style=\"font-size:12px;\">UserName ( ID ) :</td><td colspan=\"4\" align=\"center\" style=\"font-size:10px;\"><strong>" + mapUserDetails.get("UserFullName") + " ( " + mapUserDetails.get("UserName") + " )</strong></td></tr>";
//                        str2 += "</table>";
//                        str2 += "</td>";
//                        str2 += "<td><table  width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"1\" bordercolor=\"#CCCCCC\" style=\"font-size:12px;\">";
//                        str2 += "<tr><td align=\"center\">Price</td></tr>";//<td align=\"center\">Qty</td><td align=\"center\">Amt</td>
//                        int price = 0;
//                        List<Map<String, String>> matrixPrice = DBHandler.getRateList(OrderID, GroupID, itemId);
//                        for (int m = 0; m < matrixPrice.size(); m++) {
//                            price = Integer.parseInt(matrixPrice.get(m).get("Rate"));
//                            str2 += "<tr><td align=\"center\">" + price + "</td></tr>";//<td align=\"center\">"+pQt+"</td><td align=\"center\">"+pAt+"</td>
//                        }
//                        //str2+="<tr><td colspan=\"2\" align=\"center\">Total</td><td align=\"center\">"+tAmt+"</td></tr>";
//                        str2 += "</table></td></tr>";
//                    } else {
//                        if (SubItemApplicable == 1) {
//                            //TODO: SubItemApplicable
//                            List<Map<String, String>> SubItemList = DBHandler.getSubItemList(OrderID, GroupID, itemId);
//                            Map<String, String> mapQty = new HashMap<>();
//
//                            str2 += "<tr><td colspan=\"2\"><table width=\"100%\" style=\"float:left\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-size:14px;\" border=\"1\" bordercolor=\"#CCCCCC\"> ";
//
//                            int bookedQty = 0;
//                            int[][] rowTotArr = new int[2][1];
//                            int[] columnTotaltArr = new int[1];
//
//                            str2 += "<tr><td align=\"center\"><strong>" + dataListItem.get(i).get("ItemCode") + "</strong></td></tr>";
//
//                            str2 += "<tr><td align=\"center\">SubItem </td>";
//                            str2 += "<td align=\"center\"> Qty </td>";
//                            for (int x = 0; x < SubItemList.size(); x++) {
//                                str2 += "<tr><td align=\"center\">" + SubItemList.get(x).get("SubItemName") + "</td>";
//                                columnTotaltArr[0] = 0;
//                                try {
//                                    mapQty = DBHandler.getSubItemQauntity(OrderID, GroupID, itemId, SubItemList.get(x).get("SubItemID"));
//                                    if (!mapQty.isEmpty()) {
//                                        bookedQty = Integer.parseInt(mapQty.get("BookedQty"));
//                                        columnTotaltArr[0] += bookedQty;
//                                    } else {
//                                        bookedQty = 0;
//                                    }
//                                    str2 += "<td align=\"center\">" + bookedQty + "</td>";
//
//                                    rowTotArr[x][0] += bookedQty;
//
//                                } catch (Exception e) {
//                                    Log.e("Exception:", "" + e.getMessage());
//                                }
//                                rowTotArr[x][0] += columnTotaltArr[0];
//                                str2 += "</tr>";
//                            }
//                            str2 += "<tr><td align=\"center\">Total </td>";
//                            str2 += "<td align=\"center\"><strong>" + rowTotArr[0][0] + "</strong></td>";
//                            Map<String, String> mapUserDetails = DBHandler.getUserDetails(OrderID, GroupID, itemId);
//                            str2 += "</tr>";
//                            str2 += "<tr><td colspan=\"2\" style=\"font-size:12px;\">Remarks :</td><td colspan=\"4\" align=\"center\"><strong>" + dataListItem.get(i).get("Remarks") + "</strong></td></tr>";
//                            str2 += "<tr><td colspan=\"2\" style=\"font-size:12px;\">Expacted Delivery date :</td><td colspan=\"4\" align=\"center\" style=\"font-size:10px;\">" + DateFormatsMethods.DateFormat_DD_MM_YYYY(mapUserDetails.get("ExpectedDate").substring(0, 10)) + " " + mapUserDetails.get("ExpectedDate").substring(11) + "</td></tr>";
//                            str2 += "<tr><td colspan=\"2\" style=\"font-size:12px;\">UserName ( ID ) :</td><td colspan=\"4\" align=\"center\" style=\"font-size:10px;\"><strong>" + mapUserDetails.get("UserFullName") + " ( " + mapUserDetails.get("UserName") + " )</strong></td></tr>";
//                            str2 += "</table>";
//                            str2 += "</td>";
//                            str2 += "<td><table  width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"1\" bordercolor=\"#CCCCCC\" style=\"font-size:12px;\">";
//                            str2 += "<tr><td align=\"center\">Price</td></tr>";//<td align=\"center\">Qty</td><td align=\"center\">Amt</td>
//                            int price = 0;
//                            List<Map<String, String>> matrixPrice = DBHandler.getRateList(OrderID, GroupID, itemId);
//                            for (int m = 0; m < matrixPrice.size(); m++) {
//                                price = Integer.parseInt(matrixPrice.get(m).get("Rate"));
//                                str2 += "<tr><td align=\"center\">" + price + "</td></tr>";//<td align=\"center\">"+pQt+"</td><td align=\"center\">"+pAt+"</td>
//                            }
//                            //str2+="<tr><td colspan=\"2\" align=\"center\">Total</td><td align=\"center\">"+tAmt+"</td></tr>";
//                            str2 += "</table></td></tr>";
//                            grandTotal += rowTotArr[0][0];
//                        } else {
//                            //TODO: Only ItemApplicable
//                            Map<String, String> mapQty = DBHandler.getItemOnlyQauntity(OrderID, GroupID, itemId);
//                            int bookedQty = (mapQty.get("BookedQty") == null ? 0 : Integer.valueOf(mapQty.get("BookedQty")));
//                            str2 += "<tr><td colspan=\"2\"><table width=\"100%\" style=\"float:left\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-size:14px;\" border=\"1\" bordercolor=\"#CCCCCC\"> ";
//                            str2 += "<tr><td align=\"center\"><strong>" + dataListItem.get(i).get("ItemCode") + "</strong></td><td align=\"center\"><strong>" + bookedQty + "</strong></td></tr>";
//
//                            Map<String, String> mapUserDetails = DBHandler.getUserDetails(OrderID, GroupID, itemId);
//                            str2 += "<tr><td colspan=\"2\" style=\"font-size:12px;\">Remarks :</td><td colspan=\"4\" align=\"center\"><strong>" + dataListItem.get(i).get("Remarks") + "</strong></td></tr>";
//                            str2 += "<tr><td colspan=\"2\" style=\"font-size:12px;\">Expacted Delivery date :</td><td colspan=\"4\" align=\"center\" style=\"font-size:10px;\">" + DateFormatsMethods.DateFormat_DD_MM_YYYY(mapUserDetails.get("ExpectedDate").substring(0, 10)) + " " + mapUserDetails.get("ExpectedDate").substring(11) + "</td></tr>";
//                            str2 += "<tr><td colspan=\"2\" style=\"font-size:12px;\">UserName ( ID ) :</td><td colspan=\"4\" align=\"center\" style=\"font-size:10px;\"><strong>" + mapUserDetails.get("UserFullName") + " ( " + mapUserDetails.get("UserName") + " )</strong></td></tr>";
//                            str2 += "</table>";
//                            str2 += "</td>";
//                            str2 += "<td><table  width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"1\" bordercolor=\"#CCCCCC\" style=\"font-size:12px;\">";
//                            str2 += "<tr><td align=\"center\">Price</td></tr>";//<td align=\"center\">Qty</td><td align=\"center\">Amt</td>
//                            int price = 0;
//                            List<Map<String, String>> matrixPrice = DBHandler.getRateList(OrderID, GroupID, itemId);
//                            for (int m = 0; m < matrixPrice.size(); m++) {
//                                price = Integer.parseInt(matrixPrice.get(m).get("Rate"));
//                                str2 += "<tr><td align=\"center\">" + price + "</td></tr>";//<td align=\"center\">"+pQt+"</td><td align=\"center\">"+pAt+"</td>
//                            }
//                            //str2+="<tr><td colspan=\"2\" align=\"center\">Total</td><td align=\"center\">"+tAmt+"</td></tr>";
//                            str2 += "</table></td></tr>";
//                            grandTotal += bookedQty;
//                        }
//                    }
//                }
//            }
//            str2 += "<tr><td align=\"center\" colspan=\"3\">Grand Total </td><td align=\"center\" colspan=\"2\">" + grandTotal + "</td>";
//            str2 += "</table></td></tr></table><p align=\"center\">Note: This is tablet generated slip. Tablet: " + CommanStatic.MAC_ID + "</p></div></body></html>";
            HtmlContent = HTMLStringToPDF(mapList,OrderID);
            //HtmltoPDF(HtmlContent);
            webView.loadDataWithBaseURL(null, HtmlContent, "text/HTML", "UTF-8", null);
            HtmltoPDF(HtmlContent);
        }
    }
    public void print(WebView webView) {
        try {
            //PrintManager
            String PRINT_SERVICE = (String) Context.class.getDeclaredField("PRINT_SERVICE").get(null);
            Object printManager = getSystemService(PRINT_SERVICE);

            //PrintDocumentAdapter
            Class<?> printDocumentAdapterClass = Class.forName("android.print.PrintDocumentAdapter");
            Method createPrintDocumentAdapterMethod = webView.getClass().getMethod("createPrintDocumentAdapter");
            Object printAdapter = createPrintDocumentAdapterMethod.invoke(webView);

            //PrintAttributes
            Class<?> printAttributesBuilderClass = Class.forName("android.print.PrintAttributes$Builder");
            Constructor<?> ctor = printAttributesBuilderClass.getConstructor();
            Object printAttributes = ctor.newInstance(new Object[]{});
            Method buildMethod = printAttributes.getClass().getMethod("build");
            Object printAttributesBuild = buildMethod.invoke(printAttributes);

            //PrintJob
            String jobName = "My Document";
            Method printMethod = printManager.getClass().getMethod("print", String.class, printDocumentAdapterClass, printAttributesBuild.getClass());
            Object printJob = printMethod.invoke(printManager, jobName, printAdapter, printAttributesBuild);

            // Save the job object for later status checking
            List<Object> mPrintJobs = new ArrayList<>();
            mPrintJobs.add(printJob);
        } catch (Exception e) {
            MessageDialog.MessageDialog(context, "", "" + e.toString());
        }
    }
    private void SavePDfFile(WebView webView) {
        webView.setPictureListener(new WebView.PictureListener() {

            public void onNewPicture(WebView view, Picture picture) {
                if (picture != null) {
                    try {
                        bm = pictureDrawable2Bitmap(new PictureDrawable(picture));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    private static Bitmap pictureDrawable2Bitmap(PictureDrawable pictureDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(pictureDrawable.getIntrinsicWidth(), pictureDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawPicture(pictureDrawable.getPicture());
        return bitmap;
    }
    public void SimplePDFTable() {
        try {
            File direct = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SinglaGroups");
            if (!direct.exists()) {
                if (direct.mkdir()) {
                    Toast.makeText(context, "Folder Is created in sd card", Toast.LENGTH_SHORT).show();
                }
            }
            String test = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SinglaGroups";
            Document document = new Document();

            PdfWriter.getInstance(document, new FileOutputStream(test + "/mypdf.pdf"));

            document.open();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            Image image = Image.getInstance(byteArray);


            image.scaleToFit(PageSize.A4.getHeight(), PageSize.A4.getWidth());
            document.add(image);

            document.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private String HTMLStringToPDF(List<Map<String, String>> mapList, String OrderID) {
        String str2="";
        if (!mapList.isEmpty()) {
            String Advance = StaticValues.AdvanceOrBookOrder == 0 ? "" : "<p align=\"right\" style=\"font-size:8px; margin-right:2px;\">Advance Order</p>";
            String SubPartyName = (DBHandler.getPartyDetails().get("SubPartyName").isEmpty()) ? "" : "<tr><td width=\"20%\" style=\"padding:5px 5px 5px 20px;\">SubParty Name : </td><td width=\"30%\" style=\"padding:5px 5px 5px 20px;color:#006;\"><strong>" + DBHandler.getPartyDetails().get("SubPartyName") + "</strong></td></tr>";
            String RefName = (DBHandler.getPartyDetails().get("RefName").isEmpty()) ? "" : "<tr><td width=\"20%\" style=\"padding:5px 5px 5px 20px;\">Reference Name : </td><td width=\"30%\" style=\"padding:5px 5px 5px 20px;color:#006;\"><strong>" + DBHandler.getPartyDetails().get("RefName") + "</strong></td></tr>";
            str2 = "<html><body><div style=\"width:100%; margin:0 auto; border:1px solid #333;\">"
                    + "<p align=\"right\" style=\"font-size:8px; margin-right:2px;\">Print by: " + DateFormatsMethods.DateFormat_DD_MM_YYYY(DateFormatsMethods.getDateTime().substring(0, 10)) + " " + DateFormatsMethods.getDateTime().substring(11) + "</p>"
                    + Advance
                    + "<p align=\"center\" style=\"font-size:18px; margin-bottom:0px; margin-top:0px;\">"
                    + "<strong>" + DivisionName + "</strong></p>"
                    + "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-size:14px;\" border=\"1\" bordercolor=\"#CCCCCC\">"
                    + "<tr><td width=\"20%\" style=\"padding:5px 5px 5px 20px;\">Order No : </td><td width=\"30%\" style=\"padding:5px 5px 5px 20px;color:red;\"><strong>" + DBHandler.getPartyDetails().get("OrderNo") + "</strong></td></tr>"
                    + "<tr><td width=\"20%\" style=\"padding:5px 5px 5px 20px;\">Order Date : </td><td width=\"30%\" style=\"padding:5px 5px 5px 20px;\"><strong>" + DateFormatsMethods.DateFormat_DD_MM_YYYY(DBHandler.getPartyDetails().get("OrderDate").substring(0, 10)) + "</strong></td></tr>"
                    + "<tr><td width=\"20%\" style=\"padding:5px 5px 5px 20px;\">Party Name : </td><td width=\"30%\" style=\"padding:5px 5px 5px 20px;color:#006;\"><strong>" + DBHandler.getPartyDetails().get("PartyName") + "</strong></td></tr>"
                    + SubPartyName + RefName;
            //*Remarks: P/B/D (P-Pending/B-Booked/D-Delivered)
            //str2+="<tr><td colspan=\"2\" width=\"100%\" style=\"padding:5px 5px 5px 20px;\">*Remarks: B/D (B-Booked/D-Delivered)</td></tr> ";
            str2 += "<tr><td colspan=\"2\"><table width=\"100%\" style=\"float:left\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-size:12px;\" border=\"1\" bordercolor=\"#CCCCCC\">";
            int grandTotal = 0;
            for (int g = 0; g < mapList.size(); g++) {
                str2 += "<tr><td align=\"center\" style=\"font-size:12px; color:blue\" ><strong>Group : <br/> MainGroup : </strong></td><td align=\"center\" style=\"font-size:12px; color:blue\"><strong>" + mapList.get(g).get("GroupName") + " <br/> " + mapList.get(g).get("MainGroup") + " </strong> </td></tr>";
                String GroupID = mapList.get(g).get("GroupID");
                List<Map<String, String>> dataListItem = DBHandler.getItemListWithMDOrSubItemApplicable(OrderID, GroupID);
                String itemId,imageUrl;
                for (int i = 0; i < dataListItem.size(); i++) {
                    itemId = dataListItem.get(i).get("ItemID");
                    imageUrl = dataListItem.get(i).get("ImageUrl");
                    int MDApplicable = Integer.valueOf(dataListItem.get(i).get("MDApplicable"));
                    int SubItemApplicable = Integer.valueOf(dataListItem.get(i).get("SubItemApplicable"));
                    if (MDApplicable == 1) {
                        //TODO: MDApplicable
                        List<Map<String, String>> SizeList = DBHandler.getSizeList(OrderID, GroupID, itemId);
                        List<Map<String, String>> ColorList = DBHandler.getColorList(OrderID, GroupID, itemId);
                        Map<String, String> mapQty = new HashMap<>();

                       str2 += "<tr><td colspan=\"" + SizeList.size() + 2 + "\"><table width=\"100%\" style=\"float:left\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-size:14px;\" border=\"1\" bordercolor=\"#CCCCCC\">";

                        int bookedQty = 0;
                        int[][] rowTotArr = new int[SizeList.size() + 1][1];
                        int[] columnTotaltArr = new int[1];

                        str2 += "<tr><td colspan=\"" + SizeList.size()+1+ "\" align=\"center\"><strong>" + dataListItem.get(i).get("ItemCode") + "</strong></td><td colspan=\"" + SizeList.size()+2+ "\" align=\"center\">" + dataListItem.get(i).get("Attr2") + "," + dataListItem.get(i).get("Attr6") + (dataListItem.get(i).get("Attr7") == null ? "" : "," + dataListItem.get(i).get("Attr7")) + "," + dataListItem.get(i).get("Attr8") + "</td></tr>";
                        str2 += "<tr><td colspan=\"" + SizeList.size() + 2 + "\" align=\"center\"><table width=\"100%\" style=\"float:left\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-size:14px;\" border=\"1\" bordercolor=\"#CCCCCC\">";
                            str2 += "<tr><td align=\"center\">Color </td>";
                            for (int x = 0; x < SizeList.size(); x++) {
                                str2 += "<td align=\"center\">" + SizeList.get(x).get("SizeName") + " </td>";
                            }
                            str2 += "<td align=\"center\">Total</td></tr>";
                            int z = 0;
                            for (int x = 0; x < ColorList.size(); x++) {
                                str2 += "<tr><td align=\"center\">" + ColorList.get(x).get("ColorName") + "</td>";
                                columnTotaltArr[0] = 0;
                                for (z = 0; z < SizeList.size(); z++) {
                                    try {
                                        mapQty = DBHandler.getMDQauntity(OrderID, GroupID, itemId, ColorList.get(x).get("ColorID"), SizeList.get(z).get("SizeID"));
                                        if (!mapQty.isEmpty()) {
                                            bookedQty = Integer.parseInt(mapQty.get("BookedQty"));
                                            columnTotaltArr[0] += bookedQty;
                                        } else {
                                            bookedQty = 0;
                                        }
                                        str2 += "<td align=\"center\">" + bookedQty + "</td>";

                                        rowTotArr[z][0] += bookedQty;

                                    } catch (Exception e) {
                                        Log.e("Exception:", "" + e.getMessage());
                                    }
                                }
                                rowTotArr[z][0] += columnTotaltArr[0];

                                str2 += "<td align=\"center\">" + columnTotaltArr[0] + "</td>";
                                str2 += "</tr>";
                            }
                            str2 += "<tr><td align=\"center\">Total </td>";
                            for (int x = 0; x < SizeList.size() + 1; x++) {
                                str2 += "<td align=\"center\"><strong>" + rowTotArr[x][0] + "</strong></td>";
                                if (x == SizeList.size()) {
                                    grandTotal += rowTotArr[x][0];
                                }
                            }
                            str2 += "</tr>";
                        str2 += "</table></td>";
                        str2 += "<td><table  width=\"50%\" cellpadding=\"0\" cellspacing=\"0\" border=\"1\" bordercolor=\"#CCCCCC\" style=\"font-size:12px;\">";
                        str2 += "<tr><td align=\"center\">Price</td><td align=\"center\">Qty</td><td align=\"center\">Total</td></tr>";
                        int price = 0,qty = 0,total = 0;
                        List<Map<String, String>> matrixPrice = DBHandler.getRateList(OrderID, GroupID, itemId);
                        for (int m = 0; m < matrixPrice.size(); m++) {
                            price = Integer.parseInt(matrixPrice.get(m).get("Rate"));
                            qty = DBHandler.getQtyByRate(OrderID, GroupID, itemId,matrixPrice.get(m).get("Rate"));
                            total = price*qty;
                            str2 += "<tr><td align=\"center\">" + price + "</td><td align=\"center\">" + qty + "</td><td align=\"center\">" + total + "</td></tr>";
                        }
                        str2 += "</table></td><td><img src="+imageUrl+" style=\"width:50px;height:70px;\"></td></tr>";

                        Map<String, String> mapUserDetails = DBHandler.getUserDetails(OrderID, GroupID, itemId);
                        str2 += "<tr><td colspan=\"" + SizeList.size() +1+ "\" style=\"font-size:12px;\">Remarks :</td><td colspan=\"" + SizeList.size() +2+ "\" align=\"center\"><strong>" + dataListItem.get(i).get("Remarks") + "</strong></td></tr>";
                        str2 += "<tr><td colspan=\"" + SizeList.size() +1+ "\" style=\"font-size:12px;\">Expacted Delivery date :</td><td colspan=\"" + SizeList.size() + 2 + "\" align=\"center\" style=\"font-size:10px;\">" + DateFormatsMethods.DateFormat_DD_MM_YYYY(mapUserDetails.get("ExpectedDate").substring(0, 10)) + " " + mapUserDetails.get("ExpectedDate").substring(11) + "</td></tr>";
                        str2 += "<tr><td colspan=\"" + SizeList.size() +1+"\" style=\"font-size:12px;\">UserName ( ID ) :</td><td colspan=\"" + SizeList.size() + 2 + "\" align=\"center\" style=\"font-size:10px;\"><strong>" + mapUserDetails.get("UserFullName") + " ( " + mapUserDetails.get("UserName") + " )</strong></td></tr>";
                        str2 += "</table></td></tr>";
                    } else {
                        if (SubItemApplicable == 1) {
                            //TODO: SubItemApplicable
                            List<Map<String, String>> SubItemList = DBHandler.getSubItemList(OrderID, GroupID, itemId);
                            Map<String, String> mapQty = new HashMap<>();

                            str2 += "<tr><td colspan=\"" + SubItemList.size() + 2 + "\"><table width=\"100%\" style=\"float:left\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-size:14px;\" border=\"1\" bordercolor=\"#CCCCCC\">";

                            int bookedQty = 0;
                            int[][] rowTotArr = new int[2][1];
                            int[] columnTotaltArr = new int[1];

                            //str2 += "<tr><td align=\"center\"><strong>" + dataListItem.get(i).get("ItemCode") + "</strong></td></tr>";
                            str2 += "<tr><td colspan=\"" + SubItemList.size()+1+ "\" align=\"center\"><strong>" + dataListItem.get(i).get("ItemCode") + "</strong></td></tr>";
                            str2 += "<tr><td colspan=\"" + SubItemList.size()+2+ "\" align=\"center\"><table width=\"100%\" style=\"float:left\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-size:14px;\" border=\"1\" bordercolor=\"#CCCCCC\">";
                                str2 += "<tr><td align=\"center\">SubItem </td><td align=\"center\"> Qty </td></tr>";
                                for (int x = 0; x < SubItemList.size(); x++) {
                                    str2 += "<tr><td align=\"center\">" + SubItemList.get(x).get("SubItemName") + "</td>";
                                    columnTotaltArr[0] = 0;
                                    try {
                                        mapQty = DBHandler.getSubItemQauntity(OrderID, GroupID, itemId, SubItemList.get(x).get("SubItemID"));
                                        if (!mapQty.isEmpty()) {
                                            bookedQty = Integer.parseInt(mapQty.get("BookedQty"));
                                            columnTotaltArr[0] += bookedQty;
                                        } else {
                                            bookedQty = 0;
                                        }
                                        str2 += "<td align=\"center\">" + bookedQty + "</td>";

                                        rowTotArr[x][0] += bookedQty;

                                    } catch (Exception e) {
                                        Log.e("Exception:", "" + e.getMessage());
                                    }
                                    rowTotArr[x][0] += columnTotaltArr[0];
                                    str2 += "</tr>";
                                }

                                str2 += "<tr><td align=\"center\">Total </td>";
                                str2 += "<td align=\"center\"><strong>" + rowTotArr[0][0] + "</strong></td> </tr>";
                                str2 += "</table></td>";
                            str2 += "<td><table  width=\"50%\" cellpadding=\"0\" cellspacing=\"0\" border=\"1\" bordercolor=\"#CCCCCC\" style=\"font-size:12px;\">";
                            str2 += "<tr><td align=\"center\">Price</td></tr>";
                            int price = 0;
                            List<Map<String, String>> matrixPrice = DBHandler.getRateList(OrderID, GroupID, itemId);
                            for (int m = 0; m < matrixPrice.size(); m++) {
                                price = Integer.parseInt(matrixPrice.get(m).get("Rate"));
                                str2 += "<tr><td align=\"center\">" + price + "</td></tr>";
                            }
                            str2 += "</table></td></tr>";
//
                            Map<String, String> mapUserDetails = DBHandler.getUserDetails(OrderID, GroupID, itemId);
                            str2 += "<tr><td colspan=\"" + SubItemList.size() +1+ "\" style=\"font-size:12px;\">Remarks :</td><td colspan=\"" + SubItemList.size() +2+ "\" align=\"center\"><strong>" + dataListItem.get(i).get("Remarks") + "</strong></td></tr>";
                            str2 += "<tr><td colspan=\"" + SubItemList.size() +1+ "\" style=\"font-size:12px;\">Expacted Delivery date :</td><td colspan=\"" + SubItemList.size() + 2 + "\" align=\"center\" style=\"font-size:10px;\">" + DateFormatsMethods.DateFormat_DD_MM_YYYY(mapUserDetails.get("ExpectedDate").substring(0, 10)) + " " + mapUserDetails.get("ExpectedDate").substring(11) + "</td></tr>";
                            str2 += "<tr><td colspan=\"" + SubItemList.size() +1+"\" style=\"font-size:12px;\">UserName ( ID ) :</td><td colspan=\"" + SubItemList.size() + 2 + "\" align=\"center\" style=\"font-size:10px;\"><strong>" + mapUserDetails.get("UserFullName") + " ( " + mapUserDetails.get("UserName") + " )</strong></td></tr>";
                            str2 += "</table></td></tr>";
                            grandTotal += rowTotArr[0][0];
                        } else {
                            //TODO: Only ItemApplicable
                            Map<String, String> mapQty = DBHandler.getItemOnlyQauntity(OrderID, GroupID, itemId);
                            int bookedQty = (mapQty.get("BookedQty") == null ? 0 : Integer.valueOf(mapQty.get("BookedQty")));
                            str2 += "<tr><td colspan=\"2\"><table width=\"100%\" style=\"float:left\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-size:14px;\" border=\"1\" bordercolor=\"#CCCCCC\"> ";
                            str2 += "<tr><td colspan=\"2\" align=\"center\"><strong>" + dataListItem.get(i).get("ItemCode") + "</strong></td><td align=\"center\"><strong>" + bookedQty + "</strong></td></tr>";

                            Map<String, String> mapUserDetails = DBHandler.getUserDetails(OrderID, GroupID, itemId);
                            str2 += "<tr><td colspan=\"2\" style=\"font-size:12px;\">Remarks :</td><td colspan=\"4\" align=\"center\"><strong>" + dataListItem.get(i).get("Remarks") + "</strong></td></tr>";
                            str2 += "<tr><td colspan=\"2\" style=\"font-size:12px;\">Expacted Delivery date :</td><td colspan=\"4\" align=\"center\" style=\"font-size:10px;\">" + DateFormatsMethods.DateFormat_DD_MM_YYYY(mapUserDetails.get("ExpectedDate").substring(0, 10)) + " " + mapUserDetails.get("ExpectedDate").substring(11) + "</td></tr>";
                            str2 += "<tr><td colspan=\"2\" style=\"font-size:12px;\">UserName ( ID ) :</td><td colspan=\"4\" align=\"center\" style=\"font-size:10px;\"><strong>" + mapUserDetails.get("UserFullName") + " ( " + mapUserDetails.get("UserName") + " )</strong></td></tr>";
                            str2 += "</table>";
                            str2 += "</td>";
                            str2 += "<td><table  width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"1\" bordercolor=\"#CCCCCC\" style=\"font-size:12px;\">";
                            str2 += "<tr><td align=\"center\">Price</td></tr>";//<td align=\"center\">Qty</td><td align=\"center\">Amt</td>
                            int price = 0;
                            List<Map<String, String>> matrixPrice = DBHandler.getRateList(OrderID, GroupID, itemId);
                            for (int m = 0; m < matrixPrice.size(); m++) {
                                price = Integer.parseInt(matrixPrice.get(m).get("Rate"));
                                str2 += "<tr><td align=\"center\">" + price + "</td></tr>";//<td align=\"center\">"+pQt+"</td><td align=\"center\">"+pAt+"</td>
                            }
                            //str2+="<tr><td colspan=\"2\" align=\"center\">Total</td><td align=\"center\">"+tAmt+"</td></tr>";
                            str2 += "</table></td></tr>";
                            grandTotal += bookedQty;
                        }
                    }
                }
            }
            str2 += "<tr><td align=\"center\" colspan=\"4\">Grand Total </td><td align=\"center\" colspan=\"2\">" + grandTotal + "</td></tr>";
            str2 += "</table></td></tr></table><p align=\"center\">Note: This is tablet generated slip. Tablet: " + CommanStatic.MAC_ID + "</p></div></body></html>";
        }
        return str2;
    }
    private void HtmltoPDF(String html){
        try{
            //your_html_here is a variable with all the dynamic HTML stuff
            //Open a new document instance
            Document doc = new Document();
            //We convert the string to a byte array, so we can input it to the XMLWorker instance
            InputStream in = new ByteArrayInputStream(html.getBytes());
            System.out.println(""+html);
            //We write the file to a app accesbile location
            PdfWriter pdf = PdfWriter.getInstance(doc, new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SinglaGroups" + "/printPdf.pdf"));
            //open the document to write
            doc.open();
            //parser and write the file
            XMLWorkerHelper.getInstance().parseXHtml(pdf,doc,in);
            //close things before it gets messey
            doc.close();
            in.close();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    private void DialogPdfShareOptions(){
        final Dialog dialog = new Dialog(new ContextThemeWrapper(context, R.style.DialogSlideAnim));
        dialog.setContentView(R.layout.dialog_pdf_share_option);
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
                if(radioGroup.getCheckedRadioButtonId()==R.id.RadioButton_print){
                    dialog.dismiss();
                    //TODO: Share  to print
                    if (StaticValues.printFlag == 1) {
                        if (webView!=null)
                            print(webView);
                    }else{
                        MessageDialog.MessageDialog(context,"Alert","You don't have print permission of this module");
                    }
                }else if(radioGroup.getCheckedRadioButtonId()==R.id.RadioButton_mail){
                    dialog.dismiss();
                    //TODO: Share to mail
                    if (StaticValues.exportFlag == 1) {
                        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SinglaGroups" + "/printPdf.pdf");
                        if (file.exists()) {
                            Uri uri = null;
                            // So you have to use Provider
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
                            }else{
                                uri = Uri.fromFile(file);
                            }
                            Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                            emailIntent.setType("*/*");
                            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Order Print");
                            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
                            //Log.v(getClass().getSimpleName(), "sPhotoUri=" + Uri.parse("file:///" + sPhotoFileName));
                            emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
                            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                        } else {
                            MessageDialog.MessageDialog(context, "", "File not exist");
                        }
                    }else{
                        MessageDialog.MessageDialog(context,"Alert","You don't have export permission of this module");
                    }
                }else if(radioGroup.getCheckedRadioButtonId()==R.id.RadioButton_whatsapp){
                    dialog.dismiss();
                    //TODO: share to whatsapp
                    if (StaticValues.exportFlag == 1) {
                        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SinglaGroups" + "/printPdf.pdf");
                        if (file.exists()) {
                            Uri uri = null;
                            // So you have to use Provider
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
                            }else{
                                uri = Uri.fromFile(file);
                            }
                            Intent share = new Intent();
                            share.setAction(Intent.ACTION_SEND);
                            share.setType("application/pdf");
                            share.putExtra(Intent.EXTRA_STREAM, uri);
                            share.setPackage("com.whatsapp");
                            startActivity(share);
                        } else {
                            MessageDialog.MessageDialog(context, "", "File not exist");
                        }
                    }else{
                        MessageDialog.MessageDialog(context,"Alert","You don't have export permission of this module");
                    }
                }
            }
        });

    }
    private void createPdf(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // create a new document
            PdfDocument document = new PdfDocument();

            // crate a page description
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(100, 100, 1).create();

            // start a page
            PdfDocument.Page page = document.startPage(pageInfo);

            Canvas canvas = page.getCanvas();

            float w, h, cx, cy;
            w = canvas.getWidth();
            h = canvas.getHeight();
            cx = w/2;
            cy = h/2;

            canvas.drawBitmap(getScreenBitmap(), cx, cy, null);

            // finish the page
            document.finishPage(page);

//            // Create Page 2
//            pageInfo = new PdfDocument.PageInfo.Builder(500, 500, 2).create();
//            page = document.startPage(pageInfo);
//            canvas = page.getCanvas();
//            paint = new Paint();
//            paint.setColor(Color.BLUE);
//            canvas.drawCircle(200, 200, 100, paint);
//            document.finishPage(page);

            // write the document content
            String targetPdf = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SinglaGroups" + "/printPdf.pdf";
            File filePath = new File(targetPdf);
            try {
                document.writeTo(new FileOutputStream(filePath));
                Toast.makeText(this, "Done", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something wrong: " + e.toString(),
                        Toast.LENGTH_LONG).show();
            }

            // close the document
            document.close();
        }
    }
    public Bitmap getScreenBitmap() {
        View v= findViewById(android.R.id.content).getRootView();
        v.setDrawingCacheEnabled(true);
        v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());

        v.buildDrawingCache(true);
        Bitmap b = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false); // clear drawing cache
        return b;
    }

    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            openScreenshot(imageFile);
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }
    private void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }
}
