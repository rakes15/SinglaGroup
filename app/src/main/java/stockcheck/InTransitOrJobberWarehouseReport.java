package stockcheck;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
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
import com.singlagroup.BuildConfig;
import com.singlagroup.LoginActivity;
import com.singlagroup.R;
import com.singlagroup.customwidgets.MessageDialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RadioGroup;

import org.json.JSONArray;
import org.json.JSONObject;

import DatabaseController.CommanStatic;
import orderbooking.StaticValues;
import services.NetworkUtils;
import stockcheck.DatabaseSqLite.DatabaseSqlLiteHandlerInTransit;
import stockcheck.DatabaseSqLite.DatabaseSqlLiteHandlerJobberWarehouseReport;
import stockcheck.model.Godown;

public class InTransitOrJobberWarehouseReport extends AppCompatActivity {
	ActionBar actionBar;
	private Context context;
	private WebView webView;
	DatabaseSqlLiteHandlerInTransit DBInTransit;
	DatabaseSqlLiteHandlerJobberWarehouseReport DBJobberWarehouse;
	private ProgressDialog progressDialog;
	private Godown godown;
	int MDApplicable=0,SubItemApplicable=0,Type=0;
	private static String TAG = InTransitOrJobberWarehouseReport.class.getSimpleName();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (CommanStatic.Screenshot == 0) { getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE); }
		setContentView(R.layout.activity_web_view);
		Initialization();
		GetIntent();
	}
	private void Initialization() {
		this.context = InTransitOrJobberWarehouseReport.this;
		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		webView = (WebView) findViewById(R.id.web_view);
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("Please wait...");
		progressDialog.setCanceledOnTouchOutside(false);
		DBInTransit = new DatabaseSqlLiteHandlerInTransit(context);
		DBJobberWarehouse = new DatabaseSqlLiteHandlerJobberWarehouseReport(context);
	}
	private void GetIntent(){
		try {
			Godown godown = (Godown) getIntent().getExtras().get("Godown");
			MDApplicable=getIntent().getExtras().getInt("MDApplicable",0);
			SubItemApplicable=getIntent().getExtras().getInt("SubItemApplicable",0);
			Type=getIntent().getExtras().getInt("Type");
			System.out.println("Type:"+Type);
			String Title = (Type == 1 ?  "In Transit" : "Jobber Warehouse");
			actionBar.setTitle(Title);
			if (godown!=null){
				String status = NetworkUtils.getConnectivityStatusString(context);
				if (!status.contentEquals("No Internet Connection")) {
					LoginActivity obj=new LoginActivity();
					String[] str = obj.GetSharePreferenceSession(context);
					if (str!=null) {
						CallVolleyInTransitOrJobberWarehouseReport(str[3], str[0], str[4], str[5], str[14],godown.getItemID(),String.valueOf(Type));
					}
				} else {
					MessageDialog.MessageDialog(context,"","No Internet Connection");
				}
			}
		}catch (Exception e){
			MessageDialog.MessageDialog(context,"Exception",""+e.toString());
		}
	}
	private void CallMainMethod(int Type){
		if (Type == 1) {
			List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
			dataList = DBInTransit.getInTransitDetails();
			if (!dataList.isEmpty()) {
				webView.setWebViewClient(new WebViewClient() {
					public boolean shouldOverrideUrlLoading(WebView view, String url) {
						return false;
					}

					@Override
					public void onPageFinished(WebView view, String url) {

					}
				});
				//InTransitReport Method Call
				InTransitReport(dataList);
			}
		} else if (Type == 0) {
			List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
			dataList = DBJobberWarehouse.getPartyList();
			if (!dataList.isEmpty()) {
				webView.setWebViewClient(new WebViewClient() {
					public boolean shouldOverrideUrlLoading(WebView view, String url) {
						return false;
					}

					@Override
					public void onPageFinished(WebView view, String url) {

					}
				});
				//JobberWarehouseReport Method Call
				JobberWarehouseReport(dataList);
			}
		}
	}
	//TODO: CallVolley Intransit and Jobber warehouse
	private void CallVolleyInTransitOrJobberWarehouseReport(final String DeviceID,final String SessionID,final String UserID,final String DivisionID,final String CompanyID,final String ItemID,final String Type){
		showpDialog();
		StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"StockDetInTransitOrJobberGodown", new Response.Listener<String>()
		{
			@Override
			public void onResponse(String response) {
				// response
				Log.d("Response", response);
				List<Map<String,String>> mapList = new ArrayList<>();
				try{
					JSONObject jsonObject = new JSONObject(response);
					int Status=(jsonObject.getString("status")==null)?0:jsonObject.getInt("status");
					String Msg=(jsonObject.getString("msg")==null)?"Server is not responding":jsonObject.getString("msg");
					if (Status == 1) {
						if(Type.equals("0")) {
							//TODO: Jobber warehouse
							JSONArray jsonArrayResult = jsonObject.getJSONArray("Result");
							for (int i = 0; i < jsonArrayResult.length(); i++) {
								Map<String, String> map = new HashMap<>();
								map.put("DivisionID",jsonArrayResult.getJSONObject(i).optString("DivisionID") == null ? "" : jsonArrayResult.getJSONObject(i).optString("DivisionID"));
								map.put("Division",jsonArrayResult.getJSONObject(i).optString("Division") == null ? "" : jsonArrayResult.getJSONObject(i).optString("Division"));
								map.put("GodownID",jsonArrayResult.getJSONObject(i).optString("GodownID") == null ? "" : jsonArrayResult.getJSONObject(i).optString("GodownID"));
								map.put("Godown",jsonArrayResult.getJSONObject(i).optString("Godown") == null ? "" : jsonArrayResult.getJSONObject(i).optString("Godown"));
								map.put("PartyID",jsonArrayResult.getJSONObject(i).optString("PartyID") == null ? "" : jsonArrayResult.getJSONObject(i).optString("PartyID"));
								map.put("PartyName",jsonArrayResult.getJSONObject(i).optString("PartyName") == null ? "" : jsonArrayResult.getJSONObject(i).optString("PartyName"));
								map.put("SubGroup",jsonArrayResult.getJSONObject(i).optString("SubGroup") == null ? "" : jsonArrayResult.getJSONObject(i).optString("SubGroup"));
								map.put("ItemID",jsonArrayResult.getJSONObject(i).optString("ItemID") == null ? "" : jsonArrayResult.getJSONObject(i).optString("ItemID"));
								map.put("ItemName",jsonArrayResult.getJSONObject(i).optString("ItemName") == null ? "" : jsonArrayResult.getJSONObject(i).optString("ItemName"));
								map.put("ItemCode",jsonArrayResult.getJSONObject(i).optString("ItemCode") == null ? "" : jsonArrayResult.getJSONObject(i).optString("ItemCode"));
								map.put("SubItemID",jsonArrayResult.getJSONObject(i).optString("SubItemID") == null ? "" : jsonArrayResult.getJSONObject(i).optString("SubItemID"));
								map.put("SubItem",jsonArrayResult.getJSONObject(i).optString("SubItem") == null ? "" : jsonArrayResult.getJSONObject(i).optString("SubItem"));
								map.put("SubDetAttribID1",jsonArrayResult.getJSONObject(i).optString("SubDetAttribID1") == null ? "" : jsonArrayResult.getJSONObject(i).optString("SubDetAttribID1"));
								map.put("SubDetails",jsonArrayResult.getJSONObject(i).optString("SubDetails") == null ? "" : jsonArrayResult.getJSONObject(i).optString("SubDetails"));
								map.put("ItemStock",jsonArrayResult.getJSONObject(i).optString("ItemStock") == null ? "" : jsonArrayResult.getJSONObject(i).optString("ItemStock"));
								map.put("ColorID",jsonArrayResult.getJSONObject(i).optString("ColorID") == null ? "" : jsonArrayResult.getJSONObject(i).optString("ColorID"));
								map.put("Color",jsonArrayResult.getJSONObject(i).optString("Color") == null ? "" : jsonArrayResult.getJSONObject(i).optString("Color"));
								map.put("SizeID",jsonArrayResult.getJSONObject(i).optString("SizeID") == null ? "" : jsonArrayResult.getJSONObject(i).optString("SizeID"));
								map.put("Size",jsonArrayResult.getJSONObject(i).optString("Size") == null ? "" : jsonArrayResult.getJSONObject(i).optString("Size"));
								map.put("MDStock",jsonArrayResult.getJSONObject(i).optString("MDStock") == null ? "" : jsonArrayResult.getJSONObject(i).optString("MDStock"));
								map.put("Barcode",jsonArrayResult.getJSONObject(i).optString("Barcode") == null ? "" : jsonArrayResult.getJSONObject(i).optString("Barcode"));
								map.put("SubItemApplicable",jsonArrayResult.getJSONObject(i).optString("SubItemApplicable") == null ? "" : jsonArrayResult.getJSONObject(i).optString("SubItemApplicable"));
								map.put("MDApplicable",jsonArrayResult.getJSONObject(i).optString("MDApplicable") == null ? "" : jsonArrayResult.getJSONObject(i).optString("MDApplicable"));
								map.put("LastMoveDays",jsonArrayResult.getJSONObject(i).optString("LastMoveDays") == null ? "" : jsonArrayResult.getJSONObject(i).optString("LastMoveDays"));
								mapList.add(map);
							}
							context.deleteDatabase(DatabaseSqlLiteHandlerJobberWarehouseReport.DATABASE_NAME);
							DBJobberWarehouse.deleteJobberwarehouse();
							DBJobberWarehouse.insertJobberwarehouseData(mapList);
							JobberWarehouseReport(DBJobberWarehouse.getPartyList());
						}else if (Type.equals("1")){
							//TODO: In Transit
							JSONArray jsonArrayResult = jsonObject.getJSONArray("Result");
							for (int i = 0; i < jsonArrayResult.length(); i++) {
								Map<String, String> map = new HashMap<>();
								map.put("IBINo", jsonArrayResult.getJSONObject(i).optString("IBINo") == null ? "" : jsonArrayResult.getJSONObject(i).optString("IBINo"));
								map.put("VDate", jsonArrayResult.getJSONObject(i).optString("VDate") == null ? "" : jsonArrayResult.getJSONObject(i).optString("VDate"));
								map.put("BranchName", jsonArrayResult.getJSONObject(i).optString("BranchName") == null ? "" : jsonArrayResult.getJSONObject(i).optString("BranchName"));
								map.put("ToBranchName", jsonArrayResult.getJSONObject(i).optString("ToBranchName") == null ? "" : jsonArrayResult.getJSONObject(i).optString("ToBranchName"));
								map.put("DivisionID", jsonArrayResult.getJSONObject(i).optString("DivisionID") == null ? "" : jsonArrayResult.getJSONObject(i).optString("DivisionID"));
								map.put("OverDueDay", jsonArrayResult.getJSONObject(i).optString("OverDueDay") == null ? "" : jsonArrayResult.getJSONObject(i).optString("OverDueDay"));
								map.put("ItemCOde", jsonArrayResult.getJSONObject(i).optString("ItemCOde") == null ? "" : jsonArrayResult.getJSONObject(i).optString("ItemCOde"));
								map.put("ItemName", jsonArrayResult.getJSONObject(i).optString("ItemName") == null ? "" : jsonArrayResult.getJSONObject(i).optString("ItemName"));
								map.put("SubGroupName", jsonArrayResult.getJSONObject(i).optString("SubGroupName") == null ? "" : jsonArrayResult.getJSONObject(i).optString("SubGroupName"));
								map.put("SubItemName", jsonArrayResult.getJSONObject(i).optString("SubItemName") == null ? "" : jsonArrayResult.getJSONObject(i).optString("SubItemName"));
								map.put("UnitName", jsonArrayResult.getJSONObject(i).optString("UnitName") == null ? "" : jsonArrayResult.getJSONObject(i).optString("UnitName"));
								map.put("StockQty", jsonArrayResult.getJSONObject(i).optString("StockQty") == null ? "" : jsonArrayResult.getJSONObject(i).optString("StockQty"));
								map.put("Barcode", jsonArrayResult.getJSONObject(i).optString("Barcode") == null ? "" : jsonArrayResult.getJSONObject(i).optString("Barcode"));
								map.put("GodownName", jsonArrayResult.getJSONObject(i).optString("GodownName") == null ? "" : jsonArrayResult.getJSONObject(i).optString("GodownName"));
								map.put("ToGodownName", jsonArrayResult.getJSONObject(i).optString("ToGodownName") == null ? "" : jsonArrayResult.getJSONObject(i).optString("ToGodownName"));
								map.put("IBRNo", jsonArrayResult.getJSONObject(i).optString("IBRNo") == null ? "" : jsonArrayResult.getJSONObject(i).optString("IBRNo"));
								map.put("IBRStockQty", jsonArrayResult.getJSONObject(i).optString("IBRStockQty") == null ? "" : jsonArrayResult.getJSONObject(i).optString("IBRStockQty"));
								map.put("PendingQty", jsonArrayResult.getJSONObject(i).optString("PendingQty") == null ? "" : jsonArrayResult.getJSONObject(i).optString("PendingQty"));
								map.put("SubDetAttrib1", jsonArrayResult.getJSONObject(i).optString("SubDetAttrib1") == null ? "" : jsonArrayResult.getJSONObject(i).optString("SubDetAttrib1"));
								map.put("ItemID", jsonArrayResult.getJSONObject(i).optString("ItemID") == null ? "" : jsonArrayResult.getJSONObject(i).optString("ItemID"));
								map.put("ColorName", jsonArrayResult.getJSONObject(i).optString("ColorName") == null ? "" : jsonArrayResult.getJSONObject(i).optString("ColorName"));
								map.put("SizeName", jsonArrayResult.getJSONObject(i).optString("SizeName") == null ? "" : jsonArrayResult.getJSONObject(i).optString("SizeName"));
								map.put("SubItemID", jsonArrayResult.getJSONObject(i).optString("SubItemID") == null ? "" : jsonArrayResult.getJSONObject(i).optString("SubItemID"));
								map.put("ColorID", jsonArrayResult.getJSONObject(i).optString("ColorID") == null ? "" : jsonArrayResult.getJSONObject(i).optString("ColorID"));
								map.put("SizeID", jsonArrayResult.getJSONObject(i).optString("SizeID") == null ? "" : jsonArrayResult.getJSONObject(i).optString("SizeID"));
								map.put("SubItemApplicable",String.valueOf(MDApplicable));
								map.put("MDApplicable",String.valueOf(SubItemApplicable));
								mapList.add(map);
							}
							context.deleteDatabase(DatabaseSqlLiteHandlerInTransit.DATABASE_NAME);
							DBInTransit.deleteInTransit();
							DBInTransit.insertInTransitData(mapList);
							InTransitReport(DBInTransit.getInTransitDetails());
						}
						CallMainMethod(Integer.valueOf(Type));
					} else {
						MessageDialog.MessageDialog(context,"",""+Msg);
					}
				}catch (Exception e){
					MessageDialog.MessageDialog(context,"Exception",""+e.toString());
					//SetRecyclerView(mapList);
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
				params.put("ItemID", ItemID);
				params.put("Type", Type);
				Log.d(TAG,"Jobber and In Transit parameters:"+params.toString());
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
	private void InTransitReport(List<Map<String,String>> dataList){
		
		String str2 = "<html><body><div style=\"width:100%; margin:0 auto; border:1px solid #EEE;\">"
						+"<p align=\"center\" style=\"font-size:12px;\">"
						+"<strong>In Transit</strong></p>";
	    str2+="<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"1\" bordercolor=\"#666666\" style=\"font-size:12px;\">";
	    str2+="<tr><td width=\"30%\"></td><td width=\"40%\"></td><td width=\"30%\"></td></tr>";
	    String OverDueDays = (dataList.get(0).get("OverDueDays").equals("0")?"  Today":"  "+dataList.get(0).get("OverDueDays")+" Days");
	    String str = dataList.get(0).get("VDate").substring(0, 10);
	    Date date = null;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd").parse(str);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    // format the java.util.Date object to the desired format
	    String formattedDate = new SimpleDateFormat("dd-MM-yyyy").format(date);
	    str2+="<tr><td align=\"center\" width=\"30%\">Date:</td> <td colspan=\"2\" align=\"center\" style=\"color:#006; font-weight:bolder;\">"+formattedDate+OverDueDays+"</td></tr>";
	    str2+="<tr><td align=\"center\" width=\"30%\">IBI NO:</td> <td colspan=\"2\" align=\"center\" style=\"color:#006; font-weight:bolder;\">"+dataList.get(0).get("IBINO")+"</td></tr>";
	    str2+="<tr><td align=\"center\" width=\"30%\">From Branch:<br/>Godown:</td> <td colspan=\"2\" align=\"center\" style=\"color:#006; font-weight:bolder;\">"+dataList.get(0).get("BranchName")+"<br/>"+dataList.get(0).get("Godown")+"</td></tr>";
	    str2+="<tr><td align=\"center\" width=\"30%\">To Branch:<br/>Godown:</td> <td colspan=\"2\" align=\"center\" style=\"color:#006; font-weight:bolder;\">"+dataList.get(0).get("ToBranchName")+"<br/>"+dataList.get(0).get("ToGodown")+"</td></tr>";
	    str2+="<tr><td align=\"center\" width=\"30%\">Sub Group:<br/>Item Code:</td> <td colspan=\"2\" align=\"center\" style=\"color:#006; font-weight:bolder;\">"+dataList.get(0).get("SubGroupName")+"<br/>"+dataList.get(0).get("ItemCode")+"</td></tr>";
	    str2+="<tr><td colspan=\"3\" align=\"center\" style=\"color:#006; font-weight:bolder;\">"+dataList.get(0).get("ItemName")+"</td></tr>";
    	float groupTotalQty=0;
    	for(int i=0;i<dataList.size();i++)
    	{
    		String ItemID=dataList.get(i).get("ItemID");
    		int k=0,l=0;
			float stock=0;
			float matrixQTY=0;
    		int MDApplicable = Integer.valueOf(dataList.get(i).get("MDApplicable"));
    		int SubItemApplicable = Integer.valueOf(dataList.get(i).get("SubItemApplicable"));
    		
    		if(MDApplicable == 1){
    			//MD Applicable Grid
	    		List<Map<String,String>> matrix=DBInTransit.getInTransitSizeList(ItemID);	  	 
	    		List<Map<String,String>> matrixColor=DBInTransit.getInTransitColorList(ItemID);
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
				float[] GrandTotal = new float[l];
	    		for(int x=0;x<matrixColor.size();x++)
	      		{
	      			String ColorID=matrixColor.get(x).get("ColorID");
	      			str2+="<tr><td>"+matrixColor.get(x).get("ColorName")+"</td>";
	      			stock=0;
	      			for(int z=0;z<l;z++)
	      			{
	      				
	      				matrixQTY=DBInTransit.getBookQty(ItemID,sizeArr[z],ColorID);
	      				GrandTotal[z]+=matrixQTY;
	      				stock+=matrixQTY;
	      				str2+="<td align=\"center\">"+matrixQTY+"</td>";
	      			}
	      			groupTotalQty+=stock;
	      			str2+="<td align=\"center\">"+stock+"</td>";
	      		}
	    		str2+="</tr>";
	    		str2+="<tr><td>Total </td>";
				float tot=0;
	    		for(int x=0;x<l;x++)
	      		{
	      			str2+="<td align=\"center\">"+GrandTotal[x]+" </td>";
	      			tot+=GrandTotal[x];
	      		}
	    		str2+="<td align=\"center\">"+tot+"</td></tr>";
	    		str2+="</table></td></tr>";
    			
    		}else if(MDApplicable == 0) {
    			//Non MD Applicable
    			if(SubItemApplicable == 1){
    				//Sub Item Applicable Grid
		    		str2+="<tr><td colspan=\"3\" align=\"center\"><table  width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"1\" bordercolor=\"#666666\" style=\"font-size:12px;\">";
    				List<Map<String,String>> subItemList = DBInTransit.getSubItemList(ItemID);
    				str2+="<tr><td align=\"center\">SubItem</td><td align=\"center\">Stock</td><td align=\"center\">Barcode</td><td align=\"center\">Last Move Days</td></tr>";
    				for(int c=0;c<subItemList.size();c++){
    					str2+="<tr><td align=\"center\">"+subItemList.get(c).get("SubItemName")+"</td><td align=\"center\">"+subItemList.get(c).get("PendingQty")+"</td><td align=\"center\">"+subItemList.get(c).get("Barcode")+"</td><td align=\"center\">"+subItemList.get(c).get("OverDueDays")+"</td></tr>";
						float subItemStock = Float.valueOf(subItemList.get(c).get("PendingQty"));
    					groupTotalQty+=subItemStock;
    				}
    				str2+="</table></td></tr>";
    			}else if(SubItemApplicable == 0){
    				//Non Sub Item Applicable Grid
    				str2+="<tr><td colspan=\"3\" align=\"center\"><table  width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"1\" bordercolor=\"#666666\" style=\"font-size:12px;\">";
    				List<Map<String,String>> WithoutSubItemList = DBInTransit.getWithoutSubItemList(ItemID);
    				str2+="<tr><td align=\"center\">Item Name</td><td align=\"center\">Stock</td><td align=\"center\">Last Move Days</td></tr>";
    				for(int c=0;c<WithoutSubItemList.size();c++){
    					str2+="<tr><td align=\"center\">"+WithoutSubItemList.get(c).get("ItemName")+"</td><td align=\"center\">"+WithoutSubItemList.get(c).get("PendingQty")+"</td><td align=\"center\">"+WithoutSubItemList.get(c).get("OverDueDays")+"</td></tr>";
						float withoutSubItemStock = Float.valueOf(WithoutSubItemList.get(c).get("ItemStock"));
    					groupTotalQty+=withoutSubItemStock;
    				}
    				str2+="</table></td></tr>";
    			}
    		}
    	}
	    str2+="</table><p align=\"center\">Note: This is tablet generated slip</p>";
	    str2+="<p align=\"center\">	 Tablet: "+CommanStatic.MAC_ID+"</p></div></body></html>";
	    webView.loadDataWithBaseURL(null, str2, "text/HTML", "UTF-8", null);
	    webView = webView;
	}
	private void JobberWarehouseReport(List<Map<String,String>> dataList){
		
		String str2 = "<html><body><div style=\"width:100%; margin:0 auto; border:1px solid #EEE;\">";
	    str2+="<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"1\" bordercolor=\"#666666\" style=\"font-size:12px;\">";
	    str2+="<tr><td width=\"30%\"></td><td width=\"40%\"></td><td width=\"30%\"></td></tr>";
	    float grandTotQty=0;
	    for(int g=0;g<dataList.size();g++)
        {
	    	str2+="<tr><td colspan=\"3\" align=\"center\" style=\"color:#006; font-weight:bolder;\"><strong>"+dataList.get(g).get("PartyName")+"</strong></td></tr>";
	    	String PartyID=dataList.get(g).get("PartyID");
	    	List<Map<String,String>> itemList=new ArrayList<Map<String,String>>();
			itemList=DBJobberWarehouse.getItemList(PartyID);
	    	String ItemID;
	    	float groupTotalQty=0;
	    	for(int i=0;i<itemList.size();i++)
	    	{
	    		ItemID=itemList.get(i).get("ItemID");
	    		int k=0,l=0;
	    		float stock=0;
	    		float matrixQTY=0;
	    		str2+="<tr><td align=\"center\" style=\"color:#006; font-weight:bolder;\"><strong>"+itemList.get(i).get("ItemCode")+"</strong></td><td align=\"center\" colspan=\"2\" style=\"color:#006; font-weight:bolder;\"><strong>"+itemList.get(i).get("SubGroupName")+"</strong></td></tr>";
	    		str2+="<tr><td colspan=\"3\" align=\"center\" style=\"color:#006; font-weight:bolder;\"><strong>"+itemList.get(i).get("ItemName")+"</strong></td></tr>";
	    		int MDApplicable = Integer.valueOf(itemList.get(i).get("MDApplicable"));
	    		int SubItemApplicable = Integer.valueOf(itemList.get(i).get("SubItemApplicable"));
	    		
	    		if(MDApplicable == 1){
	    			//MD Applicable Grid
		    		List<Map<String,String>> matrix=DBJobberWarehouse.getSizeList(PartyID, ItemID);	  	 
		    		List<Map<String,String>> matrixColor=DBJobberWarehouse.getColorList(PartyID, ItemID);
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
		    		float[] GrandTotal = new float[l];
		    		for(int x=0;x<matrixColor.size();x++)
		      		{
		      			String ColorID=matrixColor.get(x).get("ColorID");
		      			str2+="<tr><td>"+matrixColor.get(x).get("ColorName")+"</td>";
		      			stock=0;
		      			for(int z=0;z<l;z++)
		      			{
		      				
		      				matrixQTY=DBJobberWarehouse.getBookQty(PartyID,ItemID,sizeArr[z],ColorID);
		      				GrandTotal[z]+=matrixQTY;
		      				stock+=matrixQTY;
		      				str2+="<td align=\"center\">"+matrixQTY+"</td>";
		      			}
		      			groupTotalQty+=stock;
		      			str2+="<td align=\"center\">"+stock+"</td>";
		      		}
		    		str2+="</tr>";
		    		str2+="<tr><td>Total </td>";
		    		float tot=0;
		    		for(int x=0;x<l;x++)
		      		{
		      			str2+="<td align=\"center\">"+GrandTotal[x]+" </td>";
		      			tot+=GrandTotal[x];
		      		}
		    		str2+="<td align=\"center\">"+tot+"</td></tr>";
		    		str2+="</table></td></tr>";
	    			
	    		}else if(MDApplicable == 0) {
	    			//Non MD Applicable
	    			if(SubItemApplicable == 1){
	    				//Sub Item Applicable Grid
			    		str2+="<tr><td colspan=\"3\" align=\"center\"><table  width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"1\" bordercolor=\"#666666\" style=\"font-size:12px;\">";
	    				List<Map<String,String>> subItemList = DBJobberWarehouse.getSubItemList(PartyID,ItemID);
	    				str2+="<tr><td align=\"center\" style=\"color:#006; font-weight:bolder;\">SubItem</td><td align=\"center\" style=\"color:#006; font-weight:bolder;\">Stock</td><td align=\"center\" style=\"color:#006; font-weight:bolder;\">Barcode</td><td align=\"center\" style=\"color:#006; font-weight:bolder;\">Last Move Days</td></tr>";
	    				for(int c=0;c<subItemList.size();c++){
	    					str2+="<tr><td align=\"center\">"+subItemList.get(c).get("SubItemName")+"</td><td align=\"center\">"+subItemList.get(c).get("ItemStock")+"</td><td align=\"center\">"+subItemList.get(c).get("Barcode")+"</td><td align=\"center\">"+subItemList.get(c).get("LastMoveDays")+"</td></tr>";
	    					float subItemStock = Float.valueOf(subItemList.get(c).get("ItemStock"));
	    					groupTotalQty+=subItemStock;
	    				}
	    				str2+="</table></td></tr>";
	    			}else if(SubItemApplicable == 0){
	    				//Non Sub Item Applicable Grid
	    				str2+="<tr><td colspan=\"3\" align=\"center\"><table  width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"1\" bordercolor=\"#666666\" style=\"font-size:12px;\">";
	    				List<Map<String,String>> WithoutSubItemList = DBJobberWarehouse.getWithoutSubItemList(PartyID,ItemID);
	    				str2+="<tr><td align=\"center\" style=\"color:#006; font-weight:bolder;\">Item Name</td><td align=\"center\" style=\"color:#006; font-weight:bolder;\">Stock</td><td align=\"center\" style=\"color:#006; font-weight:bolder;\">Last Move Days</td></tr>";
	    				for(int c=0;c<WithoutSubItemList.size();c++){
	    					str2+="<tr><td align=\"center\">"+WithoutSubItemList.get(c).get("ItemName")+"</td><td align=\"center\">"+WithoutSubItemList.get(c).get("ItemStock")+"</td><td align=\"center\">"+WithoutSubItemList.get(c).get("LastMoveDays")+"</td></tr>";
	    					float withoutSubItemStock = Float.valueOf(WithoutSubItemList.get(c).get("ItemStock"));
	    					groupTotalQty+=withoutSubItemStock;
	    				}
	    				str2+="</table></td></tr>";
	    			}
	    		}
	    	}
	    	str2+="<tr><td align=\"center\"><strong>Party Total</strong></td>";
	    	str2+="<td  align=\"center\">"+groupTotalQty+"</td></tr>";
	    	grandTotQty+=groupTotalQty;
        }
	    str2+="<tr><td align=\"center\"><strong>Grand Total</strong></td><td align=\"center\"><strong>"+grandTotQty+"</strong></td></tr>";
	    str2+="</table><p align=\"center\">Note: This is tablet generated slip</p>";
	    str2+="<p align=\"center\">	 Tablet: "+ CommanStatic.MAC_ID+"</p></div></body></html>";
	    webView.loadDataWithBaseURL(null, str2, "text/HTML", "UTF-8", null);
	    webView = webView;
	}
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	  // Handle presses on the action bar items
	  switch(item.getItemId()){
		  case android.R.id.home:
			  //TODO: Activity finish
			  finish();
			  break;
		  case R.id.Print:
			  if (webView!=null)
				  print(webView);
			  //DialogPdfShareOptions();
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
						Uri uri = null;
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
							uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
						}else {
							uri = Uri.fromFile(file);
						}
						if (file.exists()) {
							Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
							emailIntent.setType("*/*");
							emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Print");
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
						Uri uri = null;
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
							uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
						}else {
							uri = Uri.fromFile(file);
						}
						if (file.exists()) {
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
