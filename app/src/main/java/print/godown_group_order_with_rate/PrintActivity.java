package print.godown_group_order_with_rate;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.graphics.drawable.PictureDrawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import DatabaseController.CommanStatic;
import DatabaseController.DatabaseSqlLiteHandlerUserInfo;
import orderbooking.Database_Sqlite.DatabaseSqlLiteHandlerOrderPrintReport;
import orderbooking.StaticValues;
import print.Database_Sqlite.DatabaseSqlLiteHandlerGodownOrGroupPrintReport;
import print.godown_group_order_with_rate.adapter.GroupAdapter;
import print.godown_group_order_with_rate.model.Group;
import print.godown_group_order_with_rate.model.GroupOrGodown;
import print.godown_group_order_with_rate.model.Order;
import services.NetworkChangeReceiver;
import services.NetworkListener;
import services.NetworkUtils;

/**
 * Created by Rakesh on 13-Oct-16.
 */

public class PrintActivity extends AppCompatActivity {
    ActionBar actionBar;
    private Context context;
    private WebView webView;
    private String OrderID = "",Party = "",SubParty = "",Ref = "",DivisionName = "";
    private ProgressDialog progressDialog;
    DatabaseSqlLiteHandlerGodownOrGroupPrintReport DBHandler;
    private static String TAG = PrintActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (CommanStatic.Screenshot == 0) { getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE); }
        setContentView(R.layout.activity_web_view);
        Initialization();
        GetIntent();
    }
    private void Initialization() {
        this.context = PrintActivity.this;
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        webView = (WebView) findViewById(R.id.web_view);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        DBHandler = new DatabaseSqlLiteHandlerGodownOrGroupPrintReport(context);
    }
    private void GetIntent(){
        try {
            Group group = (Group) getIntent().getExtras().get("Group");
            GroupOrGodown godown = (GroupOrGodown) getIntent().getExtras().get("Godown");
            Order order = (Order) getIntent().getExtras().get("Order");
            if (group!=null && godown!=null && order!=null) {
                String Title = order.getOrderNo();
                actionBar.setTitle(Title);
                OrderID = order.getOrderID();
                Party = order.getPartyName();
                SubParty = order.getSubParty();
                Ref = order.getRefName();
                LoginActivity obj = new LoginActivity();
                String[] str = obj.GetSharePreferenceSession(context);
                if (str!=null){
                    DatabaseSqlLiteHandlerUserInfo DBInfo = new DatabaseSqlLiteHandlerUserInfo(context);
                    DivisionName = DBInfo.getDefaultDivision(str[14])[1];
                }
                CallMainMethod(order,godown,group);
                //MessageDialog.MessageDialog(context,"Exception",groupOrGodown.toString());
            }
        }catch (Exception e){
            MessageDialog.MessageDialog(context,"Exception",e.toString());
        }
    }
    private void CallMainMethod(Order order,GroupOrGodown godown,Group group) {
        //TODO: Order Wise Print
        if (GroupAdapter.listGroup.isEmpty()) {
            PrintWebView(DBHandler.getGroupMainGroupList(OrderID,godown.getGroupOrGodownID()), order,godown,group);
        } else {
            PrintWebView(GroupAdapter.listGroup, order,godown,group);
            //PrintWebView(groupList, OrderID,groupOrGodown);
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
                if (StaticValues.printFlag == 1) {
                    if (webView != null) print(webView);
                }else{
                    MessageDialog.MessageDialog(context,"Alert", "You don't have print permission");
                }
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
    private void PrintWebView(List<Group> mapList, Order order, GroupOrGodown godown, Group group) {
        if (!mapList.isEmpty()) {
            webView.setWebViewClient(new WebViewClient() {
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    return false;
                }
                @Override
                public void onPageFinished(WebView view, String url) {}
            });
            String OrderID = order.getOrderID();
            String GodownID = godown.getGroupOrGodownID();
            String OrderDate = order.getOrderDate().length() > 10 ? DateFormatsMethods.DateFormat_DD_MM_YYYY(order.getOrderDate().substring(0, 10)) : "";
            String SubPartyName = SubParty.isEmpty() ? "" : "<tr><td width=\"20%\" style=\"padding:5px 5px 5px 20px;\">SubParty Name : </td><td width=\"30%\" style=\"padding:5px 5px 5px 20px;color:#006;\"><strong>" + SubParty + "</strong></td></tr>";
            String RefName = this.Ref.isEmpty() ? "" : "<tr><td width=\"20%\" style=\"padding:5px 5px 5px 20px;\">Reference Name : </td><td width=\"30%\" style=\"padding:5px 5px 5px 20px;color:#006;\"><strong>" + Ref + "</strong></td></tr>";
            String str2 = "<html><body><div style=\"width:100%; margin:0 auto; border:1px solid #333;\">"
                    + "<p align=\"right\" style=\"font-size:8px; margin-right:2px;\">Print by: " + DateFormatsMethods.DateFormat_DD_MM_YYYY(DateFormatsMethods.getDateTime().substring(0, 10)) + " " + DateFormatsMethods.getDateTime().substring(11) + "</p>"
                    + "<p align=\"center\" style=\"font-size:18px; margin-bottom:0px; margin-top:0px;\">"
                    + "<strong>" + DivisionName + "</strong></p>"
                    + "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-size:14px;\" border=\"1\" bordercolor=\"#CCCCCC\">"
                    + "<tr><td width=\"20%\" style=\"padding:5px 5px 5px 20px;\">Order No : </td><td width=\"30%\" style=\"padding:5px 5px 5px 20px;color:red;\"><strong>" + order.getOrderNo() + "</strong></td></tr>"
                    + "<tr><td width=\"20%\" style=\"padding:5px 5px 5px 20px;\">Order Date : </td><td width=\"30%\" style=\"padding:5px 5px 5px 20px;\"><strong>" + OrderDate + "</strong></td></tr>"
                    + "<tr><td width=\"20%\" style=\"padding:5px 5px 5px 20px;\">Party Name : </td><td width=\"30%\" style=\"padding:5px 5px 5px 20px;color:#006;\"><strong>" + Party + "</strong></td></tr>"
                    + SubPartyName + RefName;
            //*Remarks: P/O/S (P-Pending/O-Order/S-Stock)
            str2+="<tr><td colspan=\"2\" width=\"100%\" style=\"padding:5px 5px 5px 20px;\">*Remarks: P/O/S (P-Pending / O-Order / S-Stock)</td></tr> ";
            str2 += "<tr><td  align=\"center\" style=\"font-size:12px; color:green\" ><strong>Godown (Type) :  </strong></td><td align=\"center\" style=\"font-size:12px; color:green\"><strong>" + godown.getGroupOrGodownName() + " ( "+godown.getMainGroup()+ " ) </strong> </td></tr>";
            str2 += "<tr><td colspan=\"2\"><table width=\"100%\" style=\"float:left\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-size:12px;\" border=\"1\" bordercolor=\"#CCCCCC\">";
            int groupTotalAmt=0,groupTotalQty=0;
            int grandTotal1 = 0,grandTotal2 = 0,grandTotal3 = 0;
            for (int g = 0; g < mapList.size(); g++) {
                String MainGroupID = mapList.get(g).getMainGroupID();
                String GroupID = mapList.get(g).getGroupID();
//                dataListGodown = GroupOrGodownAdapter1.listGroup1.isEmpty() ? DBHandler.getGodownList(OrderID,MainGroupID,GroupID) : dataListGodown;
//                for (int h = 0; h < dataListGodown.size(); h++) {
//                    String GodownID = dataListGodown.get(h).get("GodownID");
                    List<Map<String, String>> dataListItem = DBHandler.getItemListWithMDOrSubItemApplicable(OrderID, MainGroupID, GroupID, GodownID);
                    str2 += dataListItem.isEmpty() ? "" : "<tr><td colspan=\"2\" align=\"center\" style=\"font-size:12px; color:blue\" ><strong>Group : <br/> MainGroup : </strong></td><td  colspan=\"2\" align=\"center\" style=\"font-size:12px; color:blue\"><strong>" + mapList.get(g).getGroupName() + " <br/> " + mapList.get(g).getMainGroup() + " </strong> </td></tr>";
                    String itemId;
                    for (int i = 0; i < dataListItem.size(); i++) {
                        itemId = dataListItem.get(i).get("ItemID");
                        int MDApplicable = 1;//Integer.valueOf(dataListItem.get(i).get("MDApplicable"));
                        int SubItemApplicable = 0;//Integer.valueOf(dataListItem.get(i).get("SubItemApplicable"));
                        if (MDApplicable == 1) {
                            //TODO: MDApplicable
                            List<Map<String, String>> SizeList = DBHandler.getSizeList(OrderID, MainGroupID, GroupID, GodownID, itemId);
                            List<Map<String, String>> ColorList = DBHandler.getColorList(OrderID, MainGroupID, GroupID, GodownID, itemId);
                            Map<String, String> mapQty = new HashMap<>();

                            str2 += "<tr><td colspan=\"" + SizeList.size() + 2 + "\"><table width=\"100%\" style=\"float:left\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-size:14px;\" border=\"1\" bordercolor=\"#CCCCCC\"> ";

                            int pendingQty = 0;
                            int orderQty = 0;
                            int stockQty = 0;
                            int[][] rowTotArr = new int[SizeList.size() + 1][3];
                            int[] columnTotaltArr = new int[3];

                            str2 += "<tr><td align=\"center\"><strong>" + dataListItem.get(i).get("ItemCode") + "</strong></td><td colspan=\"" + SizeList.size() + 1 + "\" align=\"center\">" + dataListItem.get(i).get("Attr2") + "," + dataListItem.get(i).get("Attr6") + (dataListItem.get(i).get("Attr7") == null ? "" : "," + dataListItem.get(i).get("Attr7")) + "," + dataListItem.get(i).get("Attr8") + "</td></tr>";

                            str2 += "<tr><td align=\"center\">Color </td>";
                            for (int x = 0; x < SizeList.size(); x++) {
                                str2 += "<td align=\"center\">" + SizeList.get(x).get("SizeName") + " </td>";
                            }
                            str2 += "<td align=\"center\">Total</td></tr>";
                            int z = 0;
                            for (int x = 0; x < ColorList.size(); x++) {
                                str2 += "<tr><td align=\"center\">" + ColorList.get(x).get("ColorName") + "</td>";
                                columnTotaltArr[0] = 0;
                                columnTotaltArr[1] = 0;
                                columnTotaltArr[2] = 0;
                                for (z = 0; z < SizeList.size(); z++) {
                                    try {
                                        mapQty = DBHandler.getMDQauntity(OrderID, MainGroupID, GroupID, GodownID, itemId, ColorList.get(x).get("ColorID"), SizeList.get(z).get("SizeID"));
                                        if (!mapQty.isEmpty()) {
                                            pendingQty = Integer.parseInt(mapQty.get("PendingQty"));
                                            orderQty = Integer.parseInt(mapQty.get("OrderQty"));
                                            stockQty = Integer.parseInt(mapQty.get("StockQty"));
                                            columnTotaltArr[0] += pendingQty;
                                            columnTotaltArr[1] += orderQty;
                                            columnTotaltArr[2] += stockQty;
                                        } else {
                                            pendingQty = 0;
                                            orderQty = 0;
                                            stockQty = 0;
                                        }
                                        str2 += "<td align=\"center\">" + pendingQty + "/" + orderQty + "/" + stockQty + "</td>";

                                        rowTotArr[z][0] += pendingQty;
                                        rowTotArr[z][1] += orderQty;
                                        rowTotArr[z][2] += stockQty;

                                    } catch (Exception e) {
                                        Log.e("Exception:", "" + e.getMessage());
                                    }
                                }
                                rowTotArr[z][0] += columnTotaltArr[0];
                                rowTotArr[z][1] += columnTotaltArr[1];
                                rowTotArr[z][2] += columnTotaltArr[2];

                                str2 += "<td align=\"center\">" + columnTotaltArr[0] + "/" + columnTotaltArr[1] + "/" + columnTotaltArr[2] + "</td>";
                                str2 += "</tr>";
                            }
                            str2 += "<tr><td align=\"center\">Total </td>";

                            for (int x = 0; x < SizeList.size() + 1; x++) {
                                str2 += "<td align=\"center\"> <strong>" + rowTotArr[x][0] + "/" + rowTotArr[x][1] + "/" + rowTotArr[x][2] + "</strong></td>";
                                if (x == SizeList.size()) {
                                    grandTotal1 += rowTotArr[x][0];
                                    grandTotal2 += rowTotArr[x][1];
                                    grandTotal3 += rowTotArr[x][2];
                                }
                            }
                            str2 += "</tr></table></td>";
                            if (FilterOrderListActivity.Godown_Group_Order_Flag == 2) {
                                //TODO: Rate
                                str2 += "<td><table  width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"1\" bordercolor=\"#CCCCCC\" style=\"font-size:12px;\">";
                                str2 += "<tr><td align=\"center\">Price</td><td align=\"center\">Qty</td><td align=\"center\">Amt</td></tr>";
                                int price = 0, qty = 0, tAmt = 0, amt = 0;
                                List<Map<String, String>> matrixPrice = DBHandler.getRate(OrderID, MainGroupID, GroupID, GodownID, itemId);
                                for (int m = 0; m < matrixPrice.size(); m++) {
                                    price = 0;
                                    qty = 0;
                                    amt = 0;
                                    price = Integer.parseInt(matrixPrice.get(m).get("Rate"));
                                    qty = DBHandler.getQauntityByRate(OrderID, MainGroupID, GroupID, GodownID, itemId, matrixPrice.get(m).get("Rate")).get("OrderQty");
                                    amt = (price * qty);
                                    tAmt += amt;
                                    String pSt = (price == 0) ? "" : String.valueOf(price);
                                    String pQt = (qty == 0) ? "" : String.valueOf(qty);
                                    String pAt = (amt == 0) ? "" : String.valueOf(amt);
                                    str2 += "<tr><td align=\"center\">" + pSt + "</td><td align=\"center\">" + pQt + "</td><td align=\"center\">" + pAt + "</td></tr>";
                                }
                                groupTotalAmt += tAmt;
                                str2 += "<tr><td colspan=\"2\" align=\"center\">Total</td><td align=\"center\">" + tAmt + "</td></tr>";
                                str2 += "</table></td></tr>";
                            }else {
                                str2 += "</tr>";
                            }
                        } else {
                            if (SubItemApplicable == 1) {
                                //TODO: SubItemApplicable
                                List<Map<String, String>> SubItemList = DBHandler.getSubItemList(OrderID, MainGroupID, GroupID, GodownID, itemId);
                                Map<String, String> mapQty = new HashMap<>();

                                str2 += "<tr><td colspan=\"2\"><table width=\"100%\" style=\"float:left\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-size:14px;\" border=\"1\" bordercolor=\"#CCCCCC\"> ";

                                int pendingQty = 0;
                                int orderQty = 0;
                                int stockQty = 0;
                                int[][] rowTotArr = new int[2][3];
                                int[] columnTotaltArr = new int[3];

                                str2 += "<tr><td align=\"center\"><strong>" + dataListItem.get(i).get("ItemCode") + "</strong></td></tr>";

                                str2 += "<tr><td align=\"center\">SubItem </td>";
                                str2 += "<td align=\"center\"> Qty </td>";
                                for (int x = 0; x < SubItemList.size(); x++) {
                                    str2 += "<tr><td align=\"center\">" + SubItemList.get(x).get("SubItemName") + "</td>";
                                    columnTotaltArr[0] = 0;
                                    try {
                                        mapQty = DBHandler.getSubItemQauntity(OrderID, MainGroupID, GroupID, GodownID, itemId, SubItemList.get(x).get("SubItemID"));
                                        if (!mapQty.isEmpty()) {
                                            pendingQty = Integer.parseInt(mapQty.get("PendingQty"));
                                            orderQty = Integer.parseInt(mapQty.get("OrderQty"));
                                            stockQty = Integer.parseInt(mapQty.get("StockQty"));
                                            columnTotaltArr[0] += pendingQty;
                                            columnTotaltArr[1] += orderQty;
                                            columnTotaltArr[2] += stockQty;
                                        } else {
                                            pendingQty = 0;
                                            orderQty = 0;
                                            stockQty = 0;
                                        }
                                        str2 += "<td align=\"center\">" + pendingQty + "/" + orderQty + "/" + stockQty + "</td>";

                                        rowTotArr[x][0] += pendingQty;
                                        rowTotArr[x][1] += orderQty;
                                        rowTotArr[x][2] += stockQty;
                                    } catch (Exception e) {
                                        Log.e("Exception:", "" + e.getMessage());
                                    }
                                    rowTotArr[x][0] += columnTotaltArr[0];
                                    rowTotArr[x][1] += columnTotaltArr[1];
                                    rowTotArr[x][2] += columnTotaltArr[2];
                                    str2 += "</tr>";
                                }
                                str2 += "<tr><td align=\"center\">Total </td>";
                                str2 += "<td align=\"center\"><strong>" + rowTotArr[0][0] + "/" + rowTotArr[0][1] + "/" + rowTotArr[0][2] + "</strong></td>";
                                str2 += "</tr></table></td>";
                                grandTotal1 += rowTotArr[0][0];
                                grandTotal2 += rowTotArr[1][1];
                                grandTotal3 += rowTotArr[2][2];
                                if (FilterOrderListActivity.Godown_Group_Order_Flag == 2) {
                                    //TODO: Rate
                                    str2 += "<td><table  width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"1\" bordercolor=\"#CCCCCC\" style=\"font-size:12px;\">";
                                    str2 += "<tr><td align=\"center\">Price</td><td align=\"center\">Qty</td><td align=\"center\">Amt</td></tr>";
                                    int price = 0, qty = 0, tAmt = 0, amt = 0;
                                    List<Map<String, String>> matrixPrice = DBHandler.getRate(OrderID, MainGroupID, GroupID, GodownID, itemId);
                                    for (int m = 0; m < matrixPrice.size(); m++) {
                                        price = 0;
                                        qty = 0;
                                        amt = 0;
                                        price = Integer.parseInt(matrixPrice.get(m).get("Rate"));
                                        qty = DBHandler.getQauntityByRate(OrderID, MainGroupID, GroupID, GodownID, itemId, matrixPrice.get(m).get("Rate")).get("OrderQty");
                                        amt = (price * qty);
                                        tAmt += amt;
                                        String pSt = (price == 0) ? "" : String.valueOf(price);
                                        String pQt = (qty == 0) ? "" : String.valueOf(qty);
                                        String pAt = (amt == 0) ? "" : String.valueOf(amt);
                                        str2 += "<tr><td align=\"center\">" + pSt + "</td><td align=\"center\">" + pQt + "</td><td align=\"center\">" + pAt + "</td></tr>";
                                    }
                                    groupTotalAmt += tAmt;
                                    str2 += "<tr><td colspan=\"2\" align=\"center\">Total</td><td align=\"center\">" + tAmt + "</td></tr>";
                                    str2 += "</table></td></tr>";
                                }else {
                                    str2 += "</tr>";
                                }
                            } else {
                                //TODO: Only ItemApplicable
                                Map<String, String> mapQty = DBHandler.getItemOnlyQauntity(OrderID, MainGroupID, GroupID, GodownID, itemId);
                                int pendingQty = (mapQty.get("PendingQty") == null ? 0 : Integer.valueOf(mapQty.get("PendingQty")));
                                int orderQty = (mapQty.get("OrderQty") == null ? 0 : Integer.valueOf(mapQty.get("OrderQty")));
                                int stockQty = (mapQty.get("StockQty") == null ? 0 : Integer.valueOf(mapQty.get("StockQty")));
                                str2 += "<tr><td colspan=\"2\"><table width=\"100%\" style=\"float:left\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-size:14px;\" border=\"1\" bordercolor=\"#CCCCCC\"> ";
                                str2 += "<tr><td align=\"center\"><strong>" + dataListItem.get(i).get("ItemCode") + "</strong></td><td align=\"center\"><strong>" + pendingQty + "/"+ orderQty + "/"+ stockQty + "</strong></td></tr>";
                                str2 += "</table>";
                                str2 += "</td>";
                                grandTotal1 += pendingQty;
                                grandTotal2 += orderQty;
                                grandTotal3 += stockQty;
                                if (FilterOrderListActivity.Godown_Group_Order_Flag == 2) {
                                    //TODO: Rate
                                    str2 += "<td><table  width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"1\" bordercolor=\"#CCCCCC\" style=\"font-size:12px;\">";
                                    str2 += "<tr><td align=\"center\">Price</td><td align=\"center\">Qty</td><td align=\"center\">Amt</td></tr>";
                                    int price = 0, qty = 0, tAmt = 0, amt = 0;
                                    List<Map<String, String>> matrixPrice = DBHandler.getRate(OrderID, MainGroupID, GroupID, GodownID, itemId);
                                    for (int m = 0; m < matrixPrice.size(); m++) {
                                        price = 0;
                                        qty = 0;
                                        amt = 0;
                                        price = Integer.parseInt(matrixPrice.get(m).get("Rate"));
                                        qty = DBHandler.getQauntityByRate(OrderID, MainGroupID, GroupID, GodownID, itemId, matrixPrice.get(m).get("Rate")).get("OrderQty");
                                        amt = (price * qty);
                                        tAmt += amt;
                                        String pSt = (price == 0) ? "" : String.valueOf(price);
                                        String pQt = (qty == 0) ? "" : String.valueOf(qty);
                                        String pAt = (amt == 0) ? "" : String.valueOf(amt);
                                        str2 += "<tr><td align=\"center\">" + pSt + "</td><td align=\"center\">" + pQt + "</td><td align=\"center\">" + pAt + "</td></tr>";
                                    }
                                    groupTotalAmt += tAmt;
                                    str2 += "<tr><td colspan=\"2\" align=\"center\">Total</td><td align=\"center\">" + tAmt + "</td></tr>";
                                    str2 += "</table></td></tr>";
                                }else {
                                    str2 += "</tr>";
                                }
                            }
                        }
                    }
                //}
            }
            String GrandTotalAmt = (FilterOrderListActivity.Godown_Group_Order_Flag == 2 ? "<td align=\"center\" colspan=\"2\">" + groupTotalAmt + "</td>" : "");
            str2 += "<tr><td align=\"center\" colspan=\"3\">Grand Total </td><td align=\"center\" colspan=\"2\">" + grandTotal1 + "/" + grandTotal2 + "/" + grandTotal3 + "</td>"+GrandTotalAmt;
            str2 += "</table></td></tr></table><p align=\"center\">Note: This is tablet generated slip. Tablet: " + CommanStatic.MAC_ID + "</p></div></body></html>";
            webView.loadDataWithBaseURL(null, str2, "text/HTML", "UTF-8", null);
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
}
