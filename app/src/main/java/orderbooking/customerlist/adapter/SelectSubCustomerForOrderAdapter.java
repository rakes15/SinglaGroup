package orderbooking.customerlist.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.singlagroup.AppController;
import com.singlagroup.LoginActivity;
import com.singlagroup.R;
import com.singlagroup.adapters.GodownFilterableAdapter;
import com.singlagroup.customwidgets.CustomTextView;
import com.singlagroup.customwidgets.DateFormatsMethods;
import com.singlagroup.customwidgets.MessageDialog;
import com.singlagroup.customwidgets.MyWebViewClient;
import com.singlagroup.datasets.GodownDataset;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DatabaseController.DatabaseSqlLiteHandlerUserInfo;
import orderbooking.StaticValues;
import orderbooking.customerlist.datasets.SelectSubCustomerForOrderDataset;
import orderbooking.customerlist.responsedatasets.ResponsePartyInfoCheck;
import services.NetworkUtils;

/**
 * Created by Rakesh on 08-Oct-16.
 */
public class SelectSubCustomerForOrderAdapter extends RecyclerView.Adapter<SelectSubCustomerForOrderAdapter.RecyclerViewHolder>{

    private Context context;
    private ProgressDialog progressDialog;
    private LayoutInflater inflater;
    private List<SelectSubCustomerForOrderDataset> datasetList,filterDatasetList;
    private int NormalFair=0;
    private String GodownID="",Godown="",PartyID="",PartyName="",SubPartyID="",SubPartyName="",FairID="",RefName="";
    private Spinner spnRunningFair;
    private LinearLayout linearLayoutRunningFair;
    private DatabaseSqlLiteHandlerUserInfo DBHandler;
    private static String TAG = SelectSubCustomerForOrderAdapter.class.getSimpleName();
    public SelectSubCustomerForOrderAdapter(Context context, List<SelectSubCustomerForOrderDataset> datasetList) {
        this.context=context;
        this.datasetList=datasetList;
        this.filterDatasetList = new ArrayList<SelectSubCustomerForOrderDataset>();
        this.filterDatasetList.addAll(this.datasetList);
        this.inflater = LayoutInflater.from(context);
        this.DBHandler = new DatabaseSqlLiteHandlerUserInfo(this.context);
        this.progressDialog = new ProgressDialog(this.context);
        this.progressDialog.setMessage("Please wait...");
        this.progressDialog.setCanceledOnTouchOutside(false);
    }
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.cardview_all_customer_list, parent, false);
        RecyclerViewHolder viewHolder=new RecyclerViewHolder(v);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {

        final SelectSubCustomerForOrderDataset dataset = filterDatasetList.get(position);
        holder.cardView.setTag(dataset.getPartyID());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (StaticValues.editFlag == 1 || StaticValues.createFlag == 1 ) {
                    PartyID = dataset.getPartyID();
                    SubPartyID = dataset.getSubPartyID();
                    LoginActivity obj = new LoginActivity();
                    String[] str = obj.GetSharePreferenceSession(context);
                    if (str != null)
                        CallVolleyPartyInfoCheck(str[3], str[4], str[0], str[14], str[5], str[15],PartyID,SubPartyID,RefName);
                    //MsgDialogFunction(context);
                } else {
                    MessageDialog.MessageDialog(context,"Alert","You don't have permission to approve the sub party");
                }
            }
        });
        //TODO: call TableLayout method
        setTableLayout(dataset,holder.tableLayout,holder.webView);
    }
    @Override
    public int getItemCount() {
        return (null != filterDatasetList ? filterDatasetList.size() : 0);
    }
    //TODO: Filter Search
    public void filter(final String text) {

        // Searching could be complex..so we will dispatch it to a different thread...
        new Thread(new Runnable() {
            @Override
            public void run() {

                // Clear the filter list
                filterDatasetList.clear();

                // If there is no search value, then add all original list items to filter list
                if (TextUtils.isEmpty(text)) {

                    filterDatasetList.addAll(datasetList);

                } else {
                    // Iterate in the original List and add it to filter list...
                    for (SelectSubCustomerForOrderDataset item : datasetList) {
                        if(item.getSubPartyName()!=null && item.getSubPartyCode()!=null && item.getCity()!=null && item.getState()!=null && item.getAddress1()!=null && item.getAddress2()!=null && item.getMobile()!=null  && item.getEmail()!=null && item.getAccountNo()!=null && item.getAccountHolderName()!=null && item.getIFSCCOde()!=null && item.getIDName()!=null && item.getGSTIN()!=null)
                            if (item.getSubPartyName().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getSubPartyCode().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getCity().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getState().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getAddress1().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getAddress2().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getMobile().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getEmail().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getAccountNo().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getAccountHolderName().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getIFSCCOde().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getIDName().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getGSTIN().toLowerCase().contains(text.toLowerCase()) ) {
                                // Adding Matched items
                                filterDatasetList.add(item);
                            }
                    }
                }

                // Set on UI Thread
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Notify the List that the DataSet has changed...
                        notifyDataSetChanged();
                    }
                });

            }
        }).start();

    }
    //TODO RecyclerViewHolder
    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TableLayout tableLayout;
        ImageView imageViewInfo;
        WebView webView;
        public RecyclerViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.card_view);
            tableLayout = (TableLayout) itemView.findViewById(R.id.table_Layout);
            imageViewInfo = (ImageView) itemView.findViewById(R.id.imageview_info);
            webView = (WebView) itemView.findViewById(R.id.web_view);
        }
    }
    //TODO: Display TableLayout
    private void setTableLayout(SelectSubCustomerForOrderDataset dataset, TableLayout tableLayout,final WebView webView){

        //TODO: TableLayout set
        tableLayout.removeAllViewsInLayout();
        tableLayout.removeAllViews();
        //TODO: 1st Row
        View v = inflater.inflate(R.layout.table_row, tableLayout, false);
        TextView txtHeader= (TextView) v.findViewById(R.id.header);
        txtHeader.setText("Sub Party Name:");

        TextView txt= (TextView) v.findViewById(R.id.content);
        txt.setText(""+(dataset.getSubPartyName()==null  || dataset.getSubPartyName().equals("null") ?"":dataset.getSubPartyName()));
        tableLayout.addView(v);

        //TODO: Mobile Number
        String Mobile = (dataset.getMobile()==null || dataset.getMobile().equals("null") ?"":dataset.getMobile());
        v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"Mobile",""+Mobile));

        //TODO: Call and WhatsApp
        v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout, false);
        TextView txtHeader1= (TextView) v.findViewById(R.id.header);
        txtHeader1.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
        txtHeader1.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_call_grey, 0, 0, 0);
        //txtHeader1.setBackgroundColor(context.getResources().getColor(R.color.red_10));
        txtHeader1.setGravity(Gravity.CENTER_VERTICAL);
        txtHeader1.setText("Click to Call");
        txtHeader1.setTag(Mobile);

        TextView txt1= (TextView) v.findViewById(R.id.content);
        txt1.setTag(Mobile);
        txt1.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
        txt1.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_whats_app, 0, 0, 0);
        txt1.setGravity(Gravity.CENTER_VERTICAL);
        //txt1.setBackgroundColor(context.getResources().getColor(R.color.TransparentGreen));
        txt1.setText("Click to Whats App");
        tableLayout.addView(v);
        txtHeader1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Mobile = v.getTag().toString();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Mobile));
                context.startActivity(intent);
            }
        });
        txt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Mobile = v.getTag().toString();
                String Message = "";
                webView.setWebViewClient(new MyWebViewClient(context));
                webView.getSettings().setJavaScriptEnabled(true);
                webView.loadUrl("https://api.whatsapp.com/send?phone=91"+Mobile+"&text="+Message);
            }
        });
        //TODO: 3rd Row
        View v2 = inflater.inflate(R.layout.table_row, tableLayout, false);
        TextView txtHeader2= (TextView) v2.findViewById(R.id.header);
        txtHeader2.setText("Address1:");

        TextView txt2= (TextView) v2.findViewById(R.id.content);
        txt2.setText(""+(dataset.getAddress1()==null  || dataset.getAddress1().equals("null") ?"":dataset.getAddress1()));
        tableLayout.addView(v2);
        //TODO: 4rd Row
        View v3 = inflater.inflate(R.layout.table_row, tableLayout, false);
        TextView txtHeader3= (TextView) v3.findViewById(R.id.header);
        txtHeader3.setText("Address2:");

        TextView txt3= (TextView) v3.findViewById(R.id.content);
        txt3.setText(""+(dataset.getAddress2()==null || dataset.getAddress2().equals("null") ?"":dataset.getAddress2()));
        tableLayout.addView(v3);
        //TODO: 5th Row
        View v4 = inflater.inflate(R.layout.table_row, tableLayout, false);
        TextView txtHeader4= (TextView) v4.findViewById(R.id.header);
        txtHeader4.setText("City:");

        TextView txt4= (TextView) v4.findViewById(R.id.content);
        txt4.setText(""+(dataset.getCity()==null || dataset.getCity().equals("null") ?"":dataset.getCity()));
        tableLayout.addView(v4);
        //TODO: 6th Row
        View v5 = inflater.inflate(R.layout.table_row, tableLayout, false);
        TextView txtHeader5= (TextView) v5.findViewById(R.id.header);
        txtHeader5.setText("State:");

        TextView txt5= (TextView) v5.findViewById(R.id.content);
        txt5.setText(""+(dataset.getState()==null || dataset.getState().equals("null") ?"":dataset.getState()));
        tableLayout.addView(v5);
        //TODO: 7th Row
        View v6 = inflater.inflate(R.layout.table_row, tableLayout, false);
        TextView txtHeader6= (TextView) v6.findViewById(R.id.header);
        txtHeader6.setText("Sub Party Code:");

        TextView txt6= (TextView) v6.findViewById(R.id.content);
        txt6.setText(""+(dataset.getSubPartyCode()==null || dataset.getSubPartyCode().equals("null") ?"":dataset.getSubPartyCode()));
        tableLayout.addView(v6);

        //TODO: 7th Row
        v5 = inflater.inflate(R.layout.table_row, tableLayout, false);
        txtHeader5= (TextView) v5.findViewById(R.id.header);
        txtHeader5.setText("Email:");

        txt5= (TextView) v5.findViewById(R.id.content);
        txt5.setText(""+(dataset.getEmail()==null || dataset.getEmail().equals("null") ? "":dataset.getEmail()));
        tableLayout.addView(v5);

        //TODO: 8th Row
        v5 = inflater.inflate(R.layout.table_row, tableLayout, false);
        txtHeader5= (TextView) v5.findViewById(R.id.header);
        txtHeader5.setText("Account No:");

        txt5= (TextView) v5.findViewById(R.id.content);
        txt5.setText(""+(dataset.getAccountNo()==null || dataset.getAccountNo().equals("null") ? "":dataset.getAccountNo()));
        tableLayout.addView(v5);

        //TODO: 9th Row
        v5 = inflater.inflate(R.layout.table_row, tableLayout, false);
        txtHeader5= (TextView) v5.findViewById(R.id.header);
        txtHeader5.setText("A/c Holder Name:");

        txt5= (TextView) v5.findViewById(R.id.content);
        txt5.setText(""+(dataset.getAccountHolderName()==null || dataset.getAccountHolderName().equals("null") ? "":dataset.getAccountHolderName()));
        tableLayout.addView(v5);

        //TODO: 10th Row
        v5 = inflater.inflate(R.layout.table_row, tableLayout, false);
        txtHeader5= (TextView) v5.findViewById(R.id.header);
        txtHeader5.setText("IFSC Code:");

        txt5= (TextView) v5.findViewById(R.id.content);
        txt5.setText(""+(dataset.getIFSCCOde()==null || dataset.getIFSCCOde().equals("null") ? "":dataset.getIFSCCOde()));
        tableLayout.addView(v5);

        //TODO: 11th Row
        v5 = inflater.inflate(R.layout.table_row, tableLayout, false);
        txtHeader5= (TextView) v5.findViewById(R.id.header);
        txtHeader5.setText("ID Name:");

        txt5= (TextView) v5.findViewById(R.id.content);
        txt5.setText(""+(dataset.getIDName()==null || dataset.getIDName().equals("null") ? "":dataset.getIDName()));
        tableLayout.addView(v5);

        //TODO: 12th Row
        v5 = inflater.inflate(R.layout.table_row, tableLayout, false);
        txtHeader5= (TextView) v5.findViewById(R.id.header);
        txtHeader5.setText("GSTIN:");

        txt5= (TextView) v5.findViewById(R.id.content);
        txt5.setText(""+(dataset.getGSTIN()==null || dataset.getGSTIN().equals("null") ? "":dataset.getGSTIN()));
        tableLayout.addView(v5);
    }
    //TODO: Dialog Party info check
    private void DialogPartyInfoCheck(final Context context, String[][] str, final int approveFlag){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_party_info_check);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        final TextView txtViewMsg=(TextView) dialog.findViewById(R.id.text_msg);
        final TableLayout tableLayout=(TableLayout) dialog.findViewById(R.id.table_Layout);
        Button Yes=(Button)dialog.findViewById(R.id.Button_OK);
        Button No=(Button)dialog.findViewById(R.id.button_Cancel);
        txtViewMsg.setText(""+str[0][0]);
        setTableLayout(tableLayout,str);
        if (approveFlag == 1){
            Yes.setText("OK");
            No.setVisibility(View.GONE);
        }
        Yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (approveFlag == 1) {
                    dialog.dismiss();
                }else if (approveFlag == 2){
                    String status = NetworkUtils.getConnectivityStatusString(context);
                    if (!status.contentEquals("No Internet Connection")) {
                        LoginActivity obj = new LoginActivity();
                        String[] str = obj.GetSharePreferenceSession(context);
                        if (str != null && !GodownID.isEmpty() && !Godown.isEmpty()  && !PartyID.isEmpty()) {
                            if (StaticValues.createFlag == 1 || StaticValues.editFlag == 1){
                                FairID = ((FairID==null || FairID.equals("null"))?"":FairID);
                                CallVolleyApprove(str[3], str[4], str[0], str[14], str[5], str[15],GodownID,Godown,FairID,PartyID,SubPartyID,RefName);
                                dialog.dismiss();
                            }else {
                                MessageDialog.MessageDialog(context,"Alert","You don't have permission to approve the party");
                            }
                        }
                    }else {
                        MessageDialog.MessageDialog(context,"",""+status);
                    }
                    dialog.dismiss();
                }else {
                    MsgDialogFunction(context);
                    dialog.dismiss();
                }
            }
        });
        No.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private void setTableLayout(TableLayout tableLayout,String[][] str){
        //TODO: TableLayout set
        tableLayout.removeAllViewsInLayout();
        tableLayout.removeAllViews();
        for (int i = 1;i<str.length;i++){

            if ((!str[i][0].equals("") && !str[0][i].equals(""))) {
                View v = inflater.inflate(R.layout.table_row, tableLayout, false);
                TextView txtHeader = (TextView) v.findViewById(R.id.header);
                txtHeader.setText("" + str[i][0]);

                TextView txt = (TextView) v.findViewById(R.id.content);
                txt.setText("" + str[0][i]);
                tableLayout.addView(v);
            }
        }
    }
    //TODO: Approval confirmation message Dialog
    private void MsgDialogFunction(final Context context){
        final Dialog dialog = new Dialog(context);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_fair_normal);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(lp);
        dialog.setTitle("Are you sure to Approved ? ");
        linearLayoutRunningFair=(LinearLayout)dialog.findViewById(R.id.Linear_RunningFair);
        spnRunningFair=(Spinner)dialog.findViewById(R.id.spinner_running_fair);
        Spinner spnGodown=(Spinner)dialog.findViewById(R.id.spinner_Godown);
        LoginActivity obj=new LoginActivity();
        final String[] str=obj.GetSharePreferenceSession(context);
        if (str!=null) {
            final List<GodownDataset> godownDatasetList = DBHandler.getReserveGodownList(str[14], str[5], str[15]);
            GodownFilterableAdapter adapter = new GodownFilterableAdapter(context, godownDatasetList, godownDatasetList);
            spnGodown.setAdapter(adapter);
            spnGodown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position>0) {
                        Godown = godownDatasetList.get(position).getGodownName();
                        if (Godown.equals("Fair")) {
                            GodownID = godownDatasetList.get(position).getGodownID();
                            Godown = godownDatasetList.get(position).getGodownName();
                            linearLayoutRunningFair.setVisibility(View.VISIBLE);
                            CallVolleyRunningFair(str[3], str[4], str[0], str[14], str[5], str[15]);
                        } else {
                            GodownID = godownDatasetList.get(position).getGodownID();
                            Godown = godownDatasetList.get(position).getGodownName();
                            linearLayoutRunningFair.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            spnRunningFair.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position>0) {
                        Map<String, String> map = (Map<String, String>) parent.getAdapter().getItem(position);
                        FairID = map.get("ID");
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }
        Button ok=(Button)dialog.findViewById(R.id.btn_ok);
        Button cancel=(Button)dialog.findViewById(R.id.btn_cancel);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = NetworkUtils.getConnectivityStatusString(context);
                if (!status.contentEquals("No Internet Connection")) {
                    LoginActivity obj = new LoginActivity();
                    String[] str = obj.GetSharePreferenceSession(context);
                    if (str != null && !GodownID.isEmpty()  && !Godown.isEmpty()) {
                        if (StaticValues.createFlag == 1 || StaticValues.editFlag == 1){
                            if (Godown.equals("Fair")) {
                                if (FairID.length()>0 && !FairID.isEmpty()) {
                                    CallVolleyApprove(str[3], str[4], str[0], str[14], str[5], str[15], GodownID, Godown, FairID, PartyID, SubPartyID, RefName);
                                    dialog.dismiss();
                                }else{
                                    MessageDialog.MessageDialog(context,"No fair available","Please select another showroom");
                                }
                            }else{
                                CallVolleyApprove(str[3], str[4], str[0], str[14], str[5], str[15], GodownID, Godown, FairID, PartyID, SubPartyID, RefName);
                                dialog.dismiss();
                            }
                        }else {
                            MessageDialog.MessageDialog(context,"Alert","You don't have permission of this module");
                        }
                    }
                }else {
                    MessageDialog.MessageDialog(context,"",""+status);
                }

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private void CallVolleyRunningFair(final String DeviceID, final String UserID,final String SessionID,final String CompanyID,final String DivisionID,final String BranchID){
        showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"RunningFair", new Response.Listener<String>()
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
                        JSONArray jsonArrayRunningFair = jsonObject.getJSONArray("Result");
                        int c=0;
                        List<Map<String,String>> list = new ArrayList<>();
                        Map<String,String> map = new HashMap<>();
                        map.put("ID","");
                        map.put("Name","Select running fair");
                        list.add(c,map);
                        for (int i=0; i< jsonArrayRunningFair.length(); i++){
                            c++;
                            map = new HashMap<>();
                            map.put("ID",jsonArrayRunningFair.getJSONObject(i).getString("ID"));
                            map.put("Name",jsonArrayRunningFair.getJSONObject(i).getString("Name"));
                            list.add(c,map);
                        }
                        spnRunningFair.setAdapter(new RunningFairAdapter(context,list,list));
                    } else {
                        MessageDialog.MessageDialog(context,"",Msg);
                        linearLayoutRunningFair.setVisibility(View.GONE);
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
                params.put("UserID", UserID);
                params.put("SessionID", SessionID);
                params.put("DivisionID", DivisionID);
                params.put("CompanyID", CompanyID);
                params.put("BranchID", BranchID);
                Log.d(TAG,"Running fair parameters:"+params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
    }
    private void CallVolleyPartyInfoCheck(final String DeviceID, final String UserID,final String SessionID,final String CompanyID,final String DivisionID,final String BranchID,final String PartyID,final String SubPartyID,final String RefName){
        showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"PartyInfoCheck", new Response.Listener<String>()
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
                        JSONObject jsonObjectResult = jsonObject.getJSONObject("Result");
                        int NewParty = jsonObjectResult.getInt("NewParty");
                        String[][] str = new String[10][10];
                        if (NewParty == 0){
                            ResponsePartyInfoCheck result = new ResponsePartyInfoCheck(jsonObjectResult.getString("OrderID"),jsonObjectResult.getString("OrderNo"),jsonObjectResult.getString("OrderDate"),jsonObjectResult.getString("PartyID"),jsonObjectResult.getString("SubPartyID"),jsonObjectResult.getString("RefName"),jsonObjectResult.getString("Remarks"),jsonObjectResult.getString("EntryDatetime"),jsonObjectResult.getString("GodownID"),jsonObjectResult.getString("Godown"),jsonObjectResult.getString("DivisionID"),jsonObjectResult.getString("Division"),jsonObjectResult.getString("FairID"),jsonObjectResult.getString("Fair"),jsonObjectResult.getString("BranchID"),jsonObjectResult.getString("Branch"),jsonObjectResult.getInt("OldApprovedFlag"),jsonObjectResult.getInt("CreditDays"),jsonObjectResult.getString("CreditLimit"),jsonObjectResult.getString("TotalDueAmt"),jsonObjectResult.getInt("TotalOverDueAmt"),jsonObjectResult.getInt("ExceedAmt"));
                            if (result!=null){
                                if (result.getOldApprovedFlag()==1) {
                                    //TODO: Header
                                    str[0][0] = "Do you want to re-approve this party";
                                    str[1][0] =  "Order No.";
                                    str[2][0] =  "Order Date";
                                    str[3][0] =  "Party Name";
                                    str[4][0] =  "Sub Party Name";
                                    str[5][0] =  "Reference Name";
                                    str[6][0] =  "Showroom Name";
                                    str[7][0] =  "Fair Name";
                                    str[8][0] =  "Approved By";
                                    str[9][0] =  "Approved Time";

                                    //TODO: Data   Credit limit will exceed by $ amount
                                    str[0][1] =  (jsonObjectResult.getString("OrderNo")==null?"":jsonObjectResult.getString("OrderNo"));
                                    str[0][2] =  jsonObjectResult.getString("OrderDate");
                                    str[0][3] =  PartyName;
                                    str[0][4] =  SubPartyName;
                                    str[0][5] =  jsonObjectResult.getString("RefName");
                                    str[0][6] =  jsonObjectResult.getString("Godown");
                                    str[0][7] =  ((jsonObjectResult.getString("Fair")==null || jsonObjectResult.getString("Fair").equals("null"))?"":jsonObjectResult.getString("Fair"));
                                    str[0][8] =  jsonObjectResult.getString("LoginID")+"\n"+jsonObjectResult.getString("UserName")+"\n"+jsonObjectResult.getString("EmpCVName")+"\n"+jsonObjectResult.getString("EmpCVType");
                                    str[0][9] =  DateFormatsMethods.DaysHoursMinutesCount(jsonObjectResult.getString("EntryDatetime"));

                                    GodownID = jsonObjectResult.getString("GodownID");
                                    Godown = jsonObjectResult.getString("Godown");
                                    FairID = jsonObjectResult.getString("FairID");
                                    DialogPartyInfoCheck(context,str,2);
                                }else{
                                    //TODO: Header
                                    str[0][0] = "This party is already approved";
                                    str[1][0] =  "Order No.";
                                    str[2][0] =  "Order Date";
                                    str[3][0] =  "Party Name";
                                    str[4][0] =  "Sub Party Name";
                                    str[5][0] =  "Reference Name";
                                    str[6][0] =  "Showroom Name";
                                    str[7][0] =  "Fair Name";
                                    str[8][0] =  "Approved By";
                                    str[9][0] =  "Approved Time";
                                    //TODO: Data
                                    str[0][1] =  jsonObjectResult.getString("OrderNo");
                                    str[0][2] =  jsonObjectResult.getString("OrderDate");
                                    str[0][3] =  PartyName;
                                    str[0][4] =  SubPartyName;
                                    str[0][5] =  jsonObjectResult.getString("RefName");
                                    str[0][6] =  jsonObjectResult.getString("Godown");
                                    str[0][7] =  ((jsonObjectResult.getString("Fair")==null || jsonObjectResult.getString("Fair").equals("null"))?"":jsonObjectResult.getString("Fair"));
                                    str[0][8] =  jsonObjectResult.getString("LoginID")+"\n"+jsonObjectResult.getString("UserName")+"\n"+jsonObjectResult.getString("EmpCVName")+"\n"+jsonObjectResult.getString("EmpCVType");
                                    str[0][9] =  DateFormatsMethods.DaysHoursMinutesCount(jsonObjectResult.getString("EntryDatetime"));

                                    DialogPartyInfoCheck(context,str,1);
                                }
                            }
                        }else{
                            int TotalOverDueAmt = jsonObjectResult.getInt("TotalOverDueAmt");
                            int ExceedAmt = jsonObjectResult.getInt("ExceedAmt");
                            //TODO: Header
                            str[0][0] = "Contact account department,Do you want to approve?";
                            if (TotalOverDueAmt > 0 && ExceedAmt <=0){
                                //TODO:Header
                                str[1][0] =  "Over Due Amount";
                                str[2][0] =  "Total Due Amount";
                                str[3][0] =  "";
                                str[4][0] =  "";
                                str[5][0] =  "";
                                str[6][0] =  "";
                                str[7][0] =  "";
                                str[8][0] =  "";
                                str[9][0] =  "";
                                //TODO:Data
                                str[0][1] =  jsonObjectResult.getString("TotalOverDueAmt");
                                str[0][2] =  jsonObjectResult.getString("TotalDueAmt");
                                str[0][3] =  "";
                                str[0][4] =  "";
                                str[0][5] =  "";
                                str[0][6] =  "";
                                str[0][7] =  "";
                                str[0][8] =  "";
                                str[0][9] =  "";
                                DialogPartyInfoCheck(context,str,0);
                            }else if(ExceedAmt > 0 && TotalOverDueAmt <= 0){
                                //TODO:Header
                                str[1][0] =  "Credit Amount Exceed By";
                                str[2][0] =  "Credit Days";
                                str[3][0] =  "";
                                str[4][0] =  "";
                                str[5][0] =  "";
                                str[6][0] =  "";
                                str[7][0] =  "";
                                str[8][0] =  "";
                                str[9][0] =  "";
                                //TODO:Data
                                str[0][1] =  jsonObjectResult.getString("ExceedAmt");
                                str[0][2] =  jsonObjectResult.getString("CreditDays");
                                str[0][3] =  "";
                                str[0][4] =  "";
                                str[0][5] =  "";
                                str[0][6] =  "";
                                str[0][7] =  "";
                                str[0][8] =  "";
                                str[0][9] =  "";
                                DialogPartyInfoCheck(context,str,0);
                            }else if (TotalOverDueAmt > 0 && ExceedAmt > 0){
                                //TODO:Header
                                str[1][0] =  "Over Due Amount";
                                str[2][0] =  "Total Due Amount";
                                str[3][0] =  "Credit Amount Exceed By";
                                str[4][0] =  "Credit Days";
                                str[5][0] =  "";
                                str[6][0] =  "";
                                str[7][0] =  "";
                                str[8][0] =  "";
                                str[9][0] =  "";
                                //TODO:Data
                                str[0][1] =  jsonObjectResult.getString("TotalOverDueAmt");
                                str[0][2] =  jsonObjectResult.getString("TotalDueAmt");
                                str[0][3] =  jsonObjectResult.getString("ExceedAmt");
                                str[0][4] =  jsonObjectResult.getString("CreditDays");
                                str[0][5] =  "";
                                str[0][6] =  "";
                                str[0][7] =  "";
                                str[0][8] =  "";
                                str[0][9] =  "";
                                DialogPartyInfoCheck(context,str,0);
                            }else {
                                MsgDialogFunction(context);
                            }
                        }
                        //MessageDialog.MessageDialog(context,"",""+response);
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
                params.put("UserID", UserID);
                params.put("SessionID", SessionID);
                params.put("DivisionID", DivisionID);
                params.put("CompanyID", CompanyID);
                params.put("BranchID", BranchID);
                params.put("PartyID", PartyID);
                params.put("SubPartyID", SubPartyID);
                params.put("RefName", RefName);
                Log.d(TAG,"Party Info check parameters:"+params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
    }
    private void CallVolleyApprove(final String DeviceID, final String UserID,final String SessionID,final String CompanyID,final String DivisionID,final String BranchID,final String GodownID,final String GodownName,final String FairID,final String PartyID,final String SubPartyID,final String RefName){
        showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"ApprovedPartyForOrder", new Response.Listener<String>()
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
                        JSONObject jsonObjectJSONObject = jsonObject.getJSONObject("Result");
                        MessageDialog.MessageDialog(context,"Approved",""+jsonObjectJSONObject.getString("Msg"));
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
                params.put("UserID", UserID);
                params.put("SessionID", SessionID);
                params.put("DivisionID", DivisionID);
                params.put("CompanyID", CompanyID);
                params.put("BranchID", BranchID);
                params.put("GodownID", GodownID);
                params.put("GodownName", GodownName);
                params.put("FairID", FairID);
                params.put("PartyID", PartyID);
                params.put("SubPartyID", SubPartyID);
                params.put("RefName", RefName);
                params.put("VType", String.valueOf(StaticValues.Vtype));
                Log.d(TAG,"Approve parameters:"+params.toString());
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
}
