package stockcheck;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.singlagroup.AppController;
import com.singlagroup.LoginActivity;
import com.singlagroup.R;
import com.singlagroup.customwidgets.MessageDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import DatabaseController.CommanStatic;
import orderbooking.StaticValues;
import services.NetworkChangeReceiver;
import services.NetworkListener;
import services.NetworkUtils;
import stockcheck.DatabaseSqLite.DatabaseSqlLiteHandlerStockCheck;
import stockcheck.adapter.GodownWiseStockMatrixAdapter;
import stockcheck.model.Godown;

public class StockCheckGodownWiseActivity extends AppCompatActivity {
	private Context context;
	private ActionBar actionBar;
	private RecyclerView recyclerView;
	private String ItemID,Barcode;
	private int MDApplicable,SubItemApplicable,Flag;
	private ProgressDialog progressDialog;
	private DatabaseSqlLiteHandlerStockCheck DBHandler;
	private static String TAG = StockCheckActivity.class.getSimpleName();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(CommanStatic.Screenshot == 0 ){getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);} getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_recyclerview);
		Initialization();
		GetIntent();
	}
	private void Initialization(){
		this.context = StockCheckGodownWiseActivity.this;
		this.actionBar=getSupportActionBar();
		this.actionBar.setDisplayHomeAsUpEnabled(true);
		this.actionBar.setTitle("Godown Wise Stock");
		this.recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
		this.progressDialog = new ProgressDialog(context);
		this.progressDialog.setMessage("Please wait...");
		this.progressDialog.setCanceledOnTouchOutside(false);
		this.DBHandler = new DatabaseSqlLiteHandlerStockCheck(context);
	}
	private void GetIntent(){
		try {
			ItemID = getIntent().getExtras().getString("ItemID","");
			Barcode = getIntent().getExtras().getString("Barcode","");
			MDApplicable=getIntent().getExtras().getInt("MDApplicable",0);
			SubItemApplicable=getIntent().getExtras().getInt("SubItemApplicable",0);
			Flag=getIntent().getExtras().getInt("Flag",0);
			if (!ItemID.isEmpty()){
				CallApiMethod();
			}
		}catch (Exception e){
			MessageDialog.MessageDialog(context,"Exception",""+e.toString());
		}
	}
	private void CallApiMethod(){
		//TODO: Call APi
		NetworkChangeReceiver.bindNetworkListener(new NetworkListener() {
			@Override
			public void networkReceived(String status) {
				if (!status.contentEquals("No Internet Connection")) {
					LoginActivity obj=new LoginActivity();
					String[] str = obj.GetSharePreferenceSession(context);
					if (str!=null) {
						CallVolleyStockCheckGodownWise(str[3], str[0], str[4], str[5], str[14],ItemID);
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
			if (str!=null) {
				CallVolleyStockCheckGodownWise(str[3], str[0], str[4], str[5], str[14],ItemID);
			}
		} else {
			MessageDialog.MessageDialog(context,"","No Internet Connection");
		}
	}
	private void SetRecyclerView(List<Godown> godownList){
		GodownWiseStockMatrixAdapter adapter = new GodownWiseStockMatrixAdapter(context, godownList, MDApplicable, SubItemApplicable,Barcode,Flag);
		LinearLayoutManager layoutManager=new LinearLayoutManager(context);
		layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		recyclerView.setAdapter(adapter);
		recyclerView.setLayoutManager(layoutManager);
	}
	//TODO: CallVolley Stock Check Godown Wise
	private void CallVolleyStockCheckGodownWise(final String DeviceID,final String SessionID,final String UserID,final String DivisionID,final String CompanyID,final String ItemID){
		showpDialog();
		StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"StockCheckGodownWise", new Response.Listener<String>()
		{
			@Override
			public void onResponse(String response) {
				// response
				Log.d("Response", response);
				List<Godown> godownList = new ArrayList<>();
				try{
					JSONObject jsonObject = new JSONObject(response);
					int Status=(jsonObject.getString("status")==null)?0:jsonObject.getInt("status");
					String Msg=(jsonObject.getString("msg")==null)?"Server is not responding":jsonObject.getString("msg");
					if (Status == 1) {
						List<Map<String,String>> mapList = new ArrayList<>();
						JSONArray jsonArrayResult = jsonObject.getJSONArray("Result");
						for (int i=0; i< jsonArrayResult.length(); i++){
							Map<String,String> map = new HashMap<>();
							map.put("GodownID",jsonArrayResult.getJSONObject(i).optString("GodownID")==null ? "": jsonArrayResult.getJSONObject(i).optString("GodownID"));
							map.put("GodownName",jsonArrayResult.getJSONObject(i).optString("Godown")==null ? "": jsonArrayResult.getJSONObject(i).optString("Godown"));
							map.put("SubItemID",jsonArrayResult.getJSONObject(i).optString("SubItemID")==null ? "": jsonArrayResult.getJSONObject(i).optString("SubItemID"));
							map.put("ItemID",jsonArrayResult.getJSONObject(i).optString("ItemID")==null ? "": jsonArrayResult.getJSONObject(i).optString("ItemID"));
							map.put("ColorID",jsonArrayResult.getJSONObject(i).optString("ColorID")==null ? "": jsonArrayResult.getJSONObject(i).optString("ColorID"));
							map.put("SizeID",jsonArrayResult.getJSONObject(i).optString("SizeID")==null ? "": jsonArrayResult.getJSONObject(i).optString("SizeID"));
							map.put("Stock",jsonArrayResult.getJSONObject(i).optString("Stock")==null ? "": jsonArrayResult.getJSONObject(i).optString("Stock"));
							mapList.add(map);
						}
						DBHandler.deleteGodownStocks();
						DBHandler.insertStockCheckGodownWiseTable(mapList);
						SetRecyclerView(DBHandler.getBarcodeScannerGodownDetails(ItemID));
					} else {
						MessageDialog.MessageDialog(context,"",""+Msg);
						SetRecyclerView(godownList);
					}
				}catch (Exception e){
					MessageDialog.MessageDialog(context,"Exception",""+e.toString());
					SetRecyclerView(godownList);
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
}