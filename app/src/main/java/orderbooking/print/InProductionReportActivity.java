package orderbooking.print;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.singlagroup.AppController;
import com.singlagroup.LoginActivity;
import com.singlagroup.R;
import com.singlagroup.customwidgets.DateFormatsMethods;
import com.singlagroup.customwidgets.MessageDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import DatabaseController.CommanStatic;
import orderbooking.Database_Sqlite.DatabaseSqlLiteHandlerInProductionReport;
import orderbooking.StaticValues;
import services.NetworkChangeReceiver;
import services.NetworkListener;
import services.NetworkUtils;

public class InProductionReportActivity extends AppCompatActivity {
	ActionBar actionBar;
	private Context context;
	private WebView webView;
	private String ItemID="";
	private ProgressDialog progressDialog;
	DatabaseSqlLiteHandlerInProductionReport DBHandler;
	private static String TAG = PrintReportActivity.class.getSimpleName();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (CommanStatic.Screenshot == 0) { getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE); }
		setContentView(R.layout.activity_web_view);
		Initialization();
		GetIntentMethod();
	}
	private void Initialization() {
		this.context = InProductionReportActivity.this;
		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		webView = (WebView) findViewById(R.id.web_view);
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("Please wait...");
		progressDialog.setCanceledOnTouchOutside(false);
		DBHandler=new DatabaseSqlLiteHandlerInProductionReport(context);
	}
	private void GetIntentMethod() {
		try {
			String Header = getIntent().getExtras().getString("Header", "");
			ItemID = getIntent().getExtras().getString("ItemID", "");
			actionBar.setTitle(Header);
			if (!ItemID.isEmpty())
				CallApiMethod();
		} catch (Exception e) {
			MessageDialog.MessageDialog(context, "Intent", "Exception: " + e.toString());
		}
	}
	private void CallApiMethod() {
		//TODO: Call Api
		NetworkChangeReceiver.bindNetworkListener(new NetworkListener() {
			@Override
			public void networkReceived(String status) {
				if (!status.contentEquals("No Internet Connection")) {
					LoginActivity obj = new LoginActivity();
					String[] str = obj.GetSharePreferenceSession(context);
					if (str != null) {
						CallVolleyInProduction(str[3], str[4], str[0], str[5], str[14], str[15],  ItemID);
					}
				} else {
					MessageDialog.MessageDialog(context, "", status);
				}
			}
		});
		String status = NetworkUtils.getConnectivityStatusString(getApplicationContext());
		if (!status.contentEquals("No Internet Connection")) {
			LoginActivity obj = new LoginActivity();
			String[] str = obj.GetSharePreferenceSession(context);
			if (str != null) {
				CallVolleyInProduction(str[3], str[4], str[0], str[5], str[14], str[15],  ItemID);
			}
		} else {
			MessageDialog.MessageDialog(context, "", status);
		}
	}
	private void CallVolleyInProduction(final String DeviceID, final String UserID, final String SessionID, final String DivisionID, final String CompanyID, final String BranchID, final String ItemID) {
		showpDialog();
		StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL + "ItemProductionDetReport", new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				// response
				Log.d("Response", response);
				List<Map<String, String>> mapList = new ArrayList<>();
				try {
					JSONObject jsonObject = new JSONObject(response);
					int Status = (jsonObject.getString("status") == null) ? 0 : jsonObject.getInt("status");
					String Msg = (jsonObject.getString("msg") == null) ? "Server is not responding" : jsonObject.getString("msg");
					if (Status == 1) {
						JSONArray jsonArrayResult = jsonObject.getJSONArray("Result");
						if (jsonArrayResult.length() > 0) {
							for (int i = 0; i < jsonArrayResult.length(); i++) {
								String OrderID = jsonArrayResult.getJSONObject(i).optString("OrderID");
								Map<String, String> map = new HashMap<>();
								map.put("DivisionID", jsonArrayResult.getJSONObject(i).optString("DivisionID") == null ? "" : jsonArrayResult.getJSONObject(i).optString("DivisionID"));
								map.put("OrderID", jsonArrayResult.getJSONObject(i).optString("OrderID")== null ? "" : jsonArrayResult.getJSONObject(i).optString("OrderID"));
								map.put("CombinedVNo", jsonArrayResult.getJSONObject(i).optString("CombinedVNo")== null ? "" : jsonArrayResult.getJSONObject(i).optString("CombinedVNo"));
								map.put("VDate", jsonArrayResult.getJSONObject(i).optString("VDate")== null ? "" : jsonArrayResult.getJSONObject(i).optString("VDate"));
								map.put("PartyID", jsonArrayResult.getJSONObject(i).optString("PartyID")== null ? "" : jsonArrayResult.getJSONObject(i).optString("PartyID"));
								map.put("PartyName", jsonArrayResult.getJSONObject(i).optString("PartyName")== null ? "" : jsonArrayResult.getJSONObject(i).optString("PartyName"));
								map.put("SubPartyID", jsonArrayResult.getJSONObject(i).optString("SubPartyID")== null ? "" : jsonArrayResult.getJSONObject(i).optString("SubPartyID"));
								map.put("SubPartyName", jsonArrayResult.getJSONObject(i).optString("SubPartyName")== null ? "" : jsonArrayResult.getJSONObject(i).optString("SubPartyName"));
								map.put("PendingSince", jsonArrayResult.getJSONObject(i).optString("PendingSince")== null ? "" : jsonArrayResult.getJSONObject(i).optString("PendingSince"));
								map.put("ExpectedDeliveryDate", jsonArrayResult.getJSONObject(i).optString("ExpectedDeliveryDate") == null ? "" : jsonArrayResult.getJSONObject(i).optString("ExpectedDeliveryDate"));
								map.put("ExpectedDeliveryDays", jsonArrayResult.getJSONObject(i).optString("ExpectedDeliveryDays") == null ? "" : jsonArrayResult.getJSONObject(i).optString("ExpectedDeliveryDays"));
								map.put("IssueItem", jsonArrayResult.getJSONObject(i).optString("IssueItem")== null ? "" : jsonArrayResult.getJSONObject(i).optString("IssueItem"));
								map.put("ItemID", jsonArrayResult.getJSONObject(i).optString("ItemID")== null ? "" : jsonArrayResult.getJSONObject(i).optString("ItemID"));
								map.put("ItemName", jsonArrayResult.getJSONObject(i).optString("ItemName")== null ? "" : jsonArrayResult.getJSONObject(i).optString("ItemName"));
								map.put("ItemCode", jsonArrayResult.getJSONObject(i).optString("ItemCode")== null ? "" : jsonArrayResult.getJSONObject(i).optString("ItemCode"));
								map.put("SubItemID", jsonArrayResult.getJSONObject(i).optString("SubItemID")== null ? "" : jsonArrayResult.getJSONObject(i).optString("SubItemID"));
								map.put("SubItem", jsonArrayResult.getJSONObject(i).optString("SubItem")== null ? "" : jsonArrayResult.getJSONObject(i).optString("SubItem"));
								map.put("ProductionQty", jsonArrayResult.getJSONObject(i).optString("ProductionQty") == null ? "0" : jsonArrayResult.getJSONObject(i).optString("ProductionQty"));
								map.put("ReptStockQty", jsonArrayResult.getJSONObject(i).optString("ReptStockQty") == null ? "0" : jsonArrayResult.getJSONObject(i).optString("ReptStockQty"));
								map.put("ItemDocQty", jsonArrayResult.getJSONObject(i).optString("ItemDocQty") == null ? "0" : jsonArrayResult.getJSONObject(i).optString("ItemDocQty"));
								map.put("ReptItemStockQty", jsonArrayResult.getJSONObject(i).optString("ReptItemStockQty") == null ? "0" : jsonArrayResult.getJSONObject(i).optString("ReptItemStockQty"));
								map.put("SizeID", jsonArrayResult.getJSONObject(i).optString("SizeID")== null ? "" : jsonArrayResult.getJSONObject(i).optString("SizeID"));
								map.put("SizeName", jsonArrayResult.getJSONObject(i).optString("SizeName")== null ? "" : jsonArrayResult.getJSONObject(i).optString("SizeName"));
								map.put("ColorID", jsonArrayResult.getJSONObject(i).optString("ColorID")== null ? "" : jsonArrayResult.getJSONObject(i).optString("ColorID"));
								map.put("ColorName", jsonArrayResult.getJSONObject(i).optString("ColorName")== null ? "" : jsonArrayResult.getJSONObject(i).optString("ColorName"));
								map.put("SubGroupID", jsonArrayResult.getJSONObject(i).optString("SubGroupID")== null ? "" : jsonArrayResult.getJSONObject(i).optString("SubGroupID"));
								map.put("SubGroup", jsonArrayResult.getJSONObject(i).optString("SubGroup")== null ? "" : jsonArrayResult.getJSONObject(i).optString("SubGroup"));
								map.put("Group", jsonArrayResult.getJSONObject(i).optString("GroupName")== null ? "" : jsonArrayResult.getJSONObject(i).optString("GroupName"));
								map.put("MainGroup", jsonArrayResult.getJSONObject(i).optString("MainGroupName")== null ? "" : jsonArrayResult.getJSONObject(i).optString("MainGroupName"));
								map.put("SubItemApplicable", jsonArrayResult.getJSONObject(i).optString("SubItemApplicable")== null ? "0" : jsonArrayResult.getJSONObject(i).optString("SubItemApplicable"));
								map.put("MDApplicable", jsonArrayResult.getJSONObject(i).optString("MDApplicable")== null ? "0" : jsonArrayResult.getJSONObject(i).optString("MDApplicable"));
								mapList.add(map);
							}
						}
						context.deleteDatabase(DatabaseSqlLiteHandlerInProductionReport.DATABASE_NAME);
						DBHandler = new DatabaseSqlLiteHandlerInProductionReport(context);
						DBHandler.deleteInProduction();
						DBHandler.insertInProductionTable(mapList);
						if (!DBHandler.getOrdersList().isEmpty()) {
							InProductionReport(DBHandler.getOrdersList());
						}else {
							MessageDialog.MessageDialog(context, "", Msg);
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
				params.put("ItemID", ItemID);
				Log.d(TAG, "In Production parameters:" + params.toString());
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
	private void InProductionReport(List<Map<String,String>> dataList){
		webView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view,String url)
			{
				return false;
			}
			@Override
			public void onPageFinished(WebView view, String url){}
		});
		String str2 = "<html><body><div style=\"width:100%; margin:0 auto; border:1px solid #EEE;\">"
						+"<p align=\"center\" style=\"font-size:14px;\">"
						+"<strong>In Production</strong></p>";
	    str2+="<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"1\" bordercolor=\"#666666\" style=\"font-size:12px;\">";
	    str2+="<tr><td width=\"30%\"></td><td width=\"40%\"></td><td width=\"30%\"></td></tr>";
	    //int grandTotQty=0;
	    for(int g=0;g<dataList.size();g++)
        {
	    	String VDate = (dataList.get(g).get("VDate")==null || dataList.get(g).get("VDate").isEmpty())?"": DateFormatsMethods.DateFormat_DD_MM_YYYY(dataList.get(g).get("VDate").substring(0, 10));
			String ExDelDate = (dataList.get(g).get("ExDelDate")==null || dataList.get(g).get("ExDelDate").isEmpty())?"No ex date / No ex days": DateFormatsMethods.DateFormat_DD_MM_YYYY(dataList.get(g).get("ExDelDate").substring(0, 10))+ " Days";
			String SubPartyName = (dataList.get(g).get("SubPartyName")==null || dataList.get(g).get("SubPartyName").equals("null"))?"":"<tr><td align=\"center\"> SubParty Name: </td><td colspan=\"2\" align=\"center\" style=\"color:#006; font-weight:bolder;\"><strong>"+dataList.get(g).get("SubPartyName")+"</strong></td></tr>";

	    	str2+="<tr><td align=\"center\"> Date: </td><td colspan=\"2\" align=\"center\" style=\"color:#006; font-weight:bolder;\"><strong>"+VDate+" / "+dataList.get(g).get("PendingSince")+" days</strong></td></tr>";
	    	str2+="<tr><td align=\"center\"> Expected Delivery Date / Days: </td><td colspan=\"2\" align=\"center\" style=\"color:#006; font-weight:bolder;\"><strong>"+ExDelDate+"</strong></td></tr>";
	    	str2+="<tr><td align=\"center\"> Order No: </td><td colspan=\"2\" align=\"center\" style=\"color:#006; font-weight:bolder;\"><strong>"+dataList.get(g).get("CombinedVNo")+"</strong></td></tr>";
	    	str2+="<tr><td align=\"center\"> Party Name: </td><td colspan=\"2\" align=\"center\" style=\"color:#006; font-weight:bolder;\"><strong>"+dataList.get(g).get("PartyName")+"</strong></td></tr>";
	    	str2+=SubPartyName;
	    	String OrderID=dataList.get(g).get("OrderID");
	    	List<Map<String,String>> itemList=new ArrayList<Map<String,String>>();
			itemList=DBHandler.getItemList(OrderID);
	    	String ItemID;
	    	//int groupTotalQty=0;
	    	for(int i=0;i<itemList.size();i++)
	    	{
	    		ItemID=itemList.get(i).get("ItemID");
	    		int k=0,l=0;
	    		int pending=0,production=0;
	    		int pendingQty=0,productionQty=0;
	    		str2+="<tr><td align=\"center\" style=\"color:#006; font-weight:bolder;\"><strong>"+itemList.get(i).get("ItemCode")+"</strong></td><td align=\"center\" colspan=\"2\" style=\"color:#006; font-weight:bolder;\"><strong>"+itemList.get(i).get("SubGroup")+"</strong></td></tr>";
	    		str2+="<tr><td colspan=\"3\" align=\"center\" style=\"color:#006; font-weight:bolder;\"><strong>"+itemList.get(i).get("ItemName")+"<br/>*Remark - P/O means Pending/Order</strong></td></tr>";
	    		int MDApplicable = Integer.valueOf(itemList.get(i).get("MDApplicable"));
	    		int SubItemApplicable = Integer.valueOf(itemList.get(i).get("SubItemApplicable"));
	    		
	    		if(MDApplicable == 1){
	    			//MD Applicable Grid
		    		List<Map<String,String>> matrix=DBHandler.getSizeList(OrderID, ItemID);	  	 
		    		List<Map<String,String>> matrixColor=DBHandler.getColorList(OrderID, ItemID);
		    		List<Map<String,String>> dataListPO = new ArrayList<Map<String,String>>();
		    		String[] sizeArr=new String[matrix.size()+1];
		    		//MD Grid Views
		    		str2+="<tr><td colspan=\"3\" align=\"center\"><table  width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"1\" bordercolor=\"#666666\" style=\"font-size:12px;\">";
		    		str2+="<tr><td>Color</td>";
		    		for(int x=0;x<matrix.size();x++)
		      		{
		      			String SizeID=matrix.get(x).get("SizeID");
		      			sizeArr[l++]=SizeID;
		      			str2+="<td align=\"center\">"+matrix.get(x).get("SizeName")+" </td>";
		      		}
		    		str2+="<td align=\"center\">Total</td></tr>";
		    		int[] GrandTotal = new int[l];
		    		int[] GrandTotal2 = new int[l];
		    		for(int x=0;x<matrixColor.size();x++)
		      		{
		      			String ColorID=matrixColor.get(x).get("ColorID");
		      			str2+="<tr><td>"+matrixColor.get(x).get("ColorName")+"</td>";
		      			pending=0;
		      			production=0;
		      			for(int z=0;z<l;z++)
		      			{
		      				try {
		      					dataListPO = DBHandler.getProductionPendingQty(OrderID,ItemID,sizeArr[z],ColorID);
			      				if(dataListPO.isEmpty()){
			      					pendingQty = 0;
			      					productionQty = 0;
			      				}else{
			      					pendingQty = Integer.valueOf(dataListPO.get(0).get("MDPendingQty"));
			      					productionQty = Integer.valueOf(dataListPO.get(0).get("ProductionQty"));
			      				}
							} catch (Exception e) {
								Log.e("Error", e.toString());
							}
		      				//pendingQty=DBHandler.getMDPendingQty(OrderID,ItemID,sizeArr[z],ColorID);
		      				GrandTotal[z]+=pendingQty;
		      				GrandTotal2[z]+=productionQty;
		      				pending+=pendingQty;
		      				production+=productionQty;
		      				str2+="<td align=\"center\">"+pendingQty+"/"+productionQty+"</td>";
		      			}
		      			//groupTotalQty+=pending;
		      			str2+="<td align=\"center\">"+pending+"/"+production+"</td>";
		      		}
		    		str2+="</tr>";
		    		str2+="<tr><td>Total </td>";
		    		int tot=0,tot2=0;
		    		for(int x=0;x<l;x++)
		      		{
		      			str2+="<td align=\"center\">"+GrandTotal[x]+"/"+GrandTotal2[x]+" </td>";
		      			tot+=GrandTotal[x];
		      			tot2+=GrandTotal2[x];
		      		}
		    		str2+="<td align=\"center\">"+tot+"/"+tot2+"</td></tr>";
		    		str2+="</table></td></tr>";
	    			
	    		}else if(MDApplicable == 0) {
	    			//Non MD Applicable
	    			if(SubItemApplicable == 1){
	    				//Sub Item Applicable Grid
			    		str2+="<tr><td colspan=\"3\" align=\"center\"><table  width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"1\" bordercolor=\"#666666\" style=\"font-size:12px;\">";
	    				List<Map<String,String>> subItemList = DBHandler.getSubItemList(OrderID,ItemID);
	    				str2+="<tr><td align=\"center\" style=\"color:#006; font-weight:bolder;\">SubItem</td><td align=\"center\" style=\"color:#006; font-weight:bolder;\">PendingQty</td></tr>";
	    				for(int c=0;c<subItemList.size();c++){
	    					str2+="<tr><td align=\"center\">"+subItemList.get(c).get("SubItemName")+"</td><td align=\"center\">"+subItemList.get(c).get("PendingQty")+"</td></tr>";
	    					int subItempending = Integer.valueOf(subItemList.get(c).get("PendingQty")); 
	    					//groupTotalQty+=subItempending;
	    				}
	    				str2+="</table></td></tr>";
	    			}else if(SubItemApplicable == 0){
	    				//Non Sub Item Applicable Grid
	    				str2+="<tr><td colspan=\"3\" align=\"center\"><table  width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"1\" bordercolor=\"#666666\" style=\"font-size:12px;\">";
	    				List<Map<String,String>> WithoutSubItemList = DBHandler.getWithoutSubItemList(OrderID,ItemID);
	    				str2+="<tr><td align=\"center\" style=\"color:#006; font-weight:bolder;\">Item Name</td><td align=\"center\" style=\"color:#006; font-weight:bolder;\">PendingQty</td></tr>";
	    				for(int c=0;c<WithoutSubItemList.size();c++){
	    					str2+="<tr><td align=\"center\">"+WithoutSubItemList.get(c).get("ItemName")+"</td><td align=\"center\">"+WithoutSubItemList.get(c).get("PendingQty")+"</td></tr>";
	    					int withoutSubItempending = Integer.valueOf(WithoutSubItemList.get(c).get("PendingQty")); 
	    					//groupTotalQty+=withoutSubItempending;
	    				}
	    				str2+="</table></td></tr>";
	    			}
	    		}
	    	}
	    	//str2+="<tr><td align=\"center\"><strong>Order Total</strong></td>";
	    	//str2+="<td  align=\"center\">"+groupTotalQty+"</td></tr>";
	    	//grandTotQty+=groupTotalQty;
        }
	    //str2+="<tr><td align=\"center\"><strong>Grand Total</strong></td><td align=\"center\"><strong>"+grandTotQty+"</strong></td></tr>";
	    str2+="</table><p align=\"center\">Note: This is tablet generated slip</p>";
	    str2+="<p align=\"center\">	 Tablet: "+CommanStatic.MAC_ID+"</p></div></body></html>";
	    webView.loadDataWithBaseURL(null, str2, "text/HTML", "UTF-8", null);
	    webView = webView;
	}
	//TODO: Print
	public void print(WebView webView) {
		try {
			//if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				//PrintManager
				String PRINT_SERVICE = (String) Context.class.getDeclaredField("PRINT_SERVICE").get(null);
				Object printManager = getSystemService(Context.PRINT_SERVICE);

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
			//}
		} catch (Exception e) {
			MessageDialog.MessageDialog(context, "", "" + e.toString());
		}
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	      // Handle presses on the action bar items
		switch(item.getItemId()){
		  case android.R.id.home:
			   finish();
			   break;
		  case R.id.Print:
			  if (StaticValues.printFlag == 1) {
					if (webView!=null)
						print(webView);
			  } else {
				  MessageDialog.MessageDialog(context, "Alert", "You don't have print permission of this module");
			  }
			  break;
		}
		return super.onOptionsItemSelected(item);
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
}

