package callcenter.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
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
import com.singlagroup.customwidgets.CustomTextView;
import com.singlagroup.customwidgets.MessageDialog;
import com.singlagroup.customwidgets.MyWebViewClient;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import callcenter.AgentLSActivity;
import callcenter.model.AgentLS;
import orderbooking.StaticValues;
import services.NetworkUtils;

/**
 * Created by Rakesh on 13-Dec-17.
 */
public class AgentLSAdapter extends RecyclerView.Adapter<AgentLSAdapter.RecyclerViewHolder>{

    private Context context;
    private LayoutInflater inflater;
    private List<AgentLS> datasetList,filterDatasetList;
    private ProgressDialog progressDialog;
    private static String TAG = AgentLSAdapter.class.getSimpleName();
    public AgentLSAdapter(Context context, List<AgentLS> datasetList) {
        this.context=context;
        this.datasetList=datasetList;
        this.filterDatasetList = new ArrayList<AgentLS>();
        this.filterDatasetList.addAll(this.datasetList);
        this.inflater = LayoutInflater.from(context);
        this.progressDialog = new ProgressDialog(context);
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

        final AgentLS dataset = filterDatasetList.get(position);
        //TODO: call TableLayout method
        setTableLayout(dataset,holder.tableLayout,holder.tableLayout2,holder.tableLayout3,holder.webView);
    }
    @Override
    public int getItemCount() {
        return (null != filterDatasetList ? filterDatasetList.size() : 0);
    }
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return filterDatasetList.get(position);
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
                    for (AgentLS item : datasetList) {
                        if(item.getAgentName()!=null && item.getCallNumber()!=null && item.getParty()!=null && item.getExtentionNo()!=null && item.getSubParty()!=null && item.getState()!=null)
                            if (item.getAgentName().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getCallNumber().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getParty().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getExtentionNo().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getSubParty().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getState().toLowerCase().contains(text.toLowerCase())) {
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
        TableLayout tableLayout,tableLayout2,tableLayout3;
        ImageView imageViewInfo;
        WebView webView;
        public RecyclerViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.card_view);
            tableLayout = (TableLayout) itemView.findViewById(R.id.table_Layout);
            tableLayout2 = (TableLayout) itemView.findViewById(R.id.table_Layout2);
            tableLayout3 = (TableLayout) itemView.findViewById(R.id.table_Layout3);
            imageViewInfo = (ImageView) itemView.findViewById(R.id.imageview_info);
            webView = (WebView) itemView.findViewById(R.id.web_view);
        }
    }
    //TODO: Display TableLayout
    private void setTableLayout(AgentLS dataset, TableLayout tableLayout, TableLayout tableLayout2, TableLayout tableLayout3,final WebView webView){
        //TODO: tableLayout set
        tableLayout.removeAllViewsInLayout();
        tableLayout.removeAllViews();
        View v = inflater.inflate(R.layout.table_row_5_column, tableLayout, false);
        // TODO: ImageView Icon
        ImageView imageView = (ImageView) v.findViewById(R.id.content1);
        if (dataset.getPBXLogin() == 0) {
            imageView.setBackgroundColor(context.getResources().getColor(R.color.red_75));
        }else if (dataset.getPBXLogin() == 1) {
            imageView.setBackgroundColor(context.getResources().getColor(R.color.Green_70));
        }

        // TODO: Skill Level
        TextView txt = (TextView) v.findViewById(R.id.content2);
        txt.setTextSize(12);
        txt.setTextColor(context.getResources().getColor(R.color.TextViewContent));
        //txt.setTypeface(null, Typeface.BOLD);
        txt.setGravity(Gravity.CENTER_HORIZONTAL);
        txt.setText(""+(dataset.getSkillLevel()==null || dataset.getSkillLevel().equals("null")  ? "" : dataset.getSkillLevel()));
        txt.setBackgroundColor(SetBackgroundColorWithCondition(context,txt.getText().toString(),2));
        // TODO: Extension No
        txt = (TextView) v.findViewById(R.id.content3);
        txt.setTextSize(12);
        txt.setTextColor(context.getResources().getColor(R.color.Black));
        txt.setTypeface(null, Typeface.BOLD);
        txt.setGravity(Gravity.CENTER_HORIZONTAL);
        txt.setText(""+(dataset.getExtentionNo()));
        txt.setBackgroundColor(SetBackgroundColorWithCondition(context,txt.getText().toString(),0));
        // TODO: Phone Status
        txt = (TextView) v.findViewById(R.id.content4);
        txt.setTextSize(12);
        txt.setTextColor(context.getResources().getColor(R.color.TextViewContent));
        //txt.setTypeface(null, Typeface.BOLD);
        txt.setGravity(Gravity.CENTER_HORIZONTAL);
        txt.setText(""+(dataset.getState()==null || dataset.getState().equals("null")  ? "" : dataset.getState().toLowerCase().equals("intercom ring") ? "Intercom Dialing" : dataset.getState()));
        txt.setBackgroundColor(SetBackgroundColorWithCondition(context,txt.getText().toString(),0));
        // TODO: Duration
        txt = (TextView) v.findViewById(R.id.content5);
        txt.setTextSize(12);
        txt.setTextColor(context.getResources().getColor(R.color.TextViewContent));
        //txt.setTypeface(null, Typeface.BOLD);
        txt.setGravity(Gravity.CENTER_HORIZONTAL);
        txt.setText(""+(dataset.getCallDurration()==null || dataset.getCallDurration().equals("null")  ? "" : dataset.getCallDurration()));
        txt.setBackgroundColor(SetBackgroundColorWithCondition(context,txt.getText().toString(),0));

        tableLayout.addView(v);

        //TODO: tableLayout2 set
        tableLayout2.removeAllViewsInLayout();
        tableLayout2.removeAllViews();
        v = inflater.inflate(R.layout.table_row_3_column, tableLayout2, false);
        // TODO: Agent Name
        txt = (TextView) v.findViewById(R.id.content_column_1);
        txt.setTextSize(12);
        txt.setTextColor(context.getResources().getColor(R.color.TextViewContent));
        //txt.setTypeface(null, Typeface.BOLD);
        txt.setGravity(Gravity.CENTER_HORIZONTAL);
        txt.setText(""+(dataset.getAgFullName()==null || dataset.getAgFullName().equals("null")  ? "" : dataset.getAgFullName()));
        txt.setBackgroundColor(SetBackgroundColorWithCondition(context,txt.getText().toString(),0));
        // TODO: Call Number
        txt = (TextView) v.findViewById(R.id.content_column_2);
        txt.setTextSize(12);
        txt.setTextColor(context.getResources().getColor(R.color.TextViewContent));
        //txt.setTypeface(null, Typeface.BOLD);
        txt.setGravity(Gravity.CENTER_HORIZONTAL);
        txt.setText(""+(dataset.getCallNumber()==null || dataset.getCallNumber().equals("null")  ? "" : dataset.getCallNumber()));
        txt.setBackgroundColor(SetBackgroundColorWithCondition(context,txt.getText().toString(),0));
        // TODO: Type Name
        txt = (TextView) v.findViewById(R.id.content_column_3);
        txt.setTextSize(12);
        txt.setTextColor(context.getResources().getColor(R.color.TextViewContent));
        //txt.setTypeface(null, Typeface.BOLD);
        txt.setGravity(Gravity.CENTER_HORIZONTAL);
        txt.setText(""+(dataset.getTypeName()==null || dataset.getTypeName().equals("null")   ? "" : dataset.getTypeName()));
        txt.setBackgroundColor(SetBackgroundColorWithCondition(context,txt.getText().toString(),0));
        tableLayout2.addView(v);
        //TODO: 3rd Row
        v = inflater.inflate(R.layout.table_row_3_column, tableLayout2, false);
        // TODO: Party Name /SubParty Name
        txt = (TextView) v.findViewById(R.id.content_column_1);
        txt.setTextSize(12);
        txt.setTextColor(context.getResources().getColor(R.color.TextViewContent));
        //txt.setTypeface(null, Typeface.BOLD);
        txt.setGravity(Gravity.CENTER_HORIZONTAL);
        String Party = dataset.getParty()==null || dataset.getParty().equals("null")  ? "" : dataset.getParty();
        String SubParty = dataset.getSubParty()==null || dataset.getSubParty().equals("null")  ? "" : "\n"+dataset.getSubParty();
        txt.setText(Party+SubParty);
        txt.setBackgroundColor(SetBackgroundColorWithCondition(context,txt.getText().toString(),0));
        // TODO: CRM State
        txt = (TextView) v.findViewById(R.id.content_column_2);
        txt.setTextSize(12);
        txt.setTextColor(context.getResources().getColor(R.color.TextViewContent));
        //txt.setTypeface(null, Typeface.BOLD);
        txt.setGravity(Gravity.CENTER_HORIZONTAL);
        txt.setText(""+(dataset.getCRMState()==null || dataset.getCRMState().equals("null")  ? "" : dataset.getCRMState()));
        txt.setBackgroundColor(SetBackgroundColorWithCondition(context,txt.getText().toString(),1));
        // TODO: CP Name
        txt = (TextView) v.findViewById(R.id.content_column_3);
        txt.setTextSize(12);
        txt.setTextColor(context.getResources().getColor(R.color.TextViewContent));
        //txt.setTypeface(null, Typeface.BOLD);
        txt.setGravity(Gravity.CENTER_HORIZONTAL);
        txt.setText(""+(dataset.getCPName()==null || dataset.getCPName().equals("null") ? "" : dataset.getCPName()));
        txt.setBackgroundColor(SetBackgroundColorWithCondition(context,txt.getText().toString(),0));
        tableLayout2.addView(v);

//        tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"Agent",(dataset.getAgFullName()==null || dataset.getAgFullName().equals("null") ?"":dataset.getAgFullName())));
//        //TODO: Party Name
//        v = inflater.inflate(R.layout.table_row, tableLayout, false);
//        tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"Party ",(dataset.getParty()==null || dataset.getParty().equals("null") ?"":dataset.getParty())));
//        //TODO: Sub Party Name
//        String SubParty = (dataset.getSubParty()==null || dataset.getSubParty().equals("null") ?"":dataset.getSubParty());
//        if (!SubParty.isEmpty()) {
//            v = inflater.inflate(R.layout.table_row, tableLayout, false);
//            tableLayout.addView(CustomTextView.setTableRow2Columns(context, v, "Sub Party", SubParty));
//        }
//        //TODO: Contact Person
//        v = inflater.inflate(R.layout.table_row, tableLayout, false);
//        tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"Contact Person",(dataset.getCPName()==null || dataset.getCPName().equals("null") ?"":dataset.getCPName())));
//        //TODO: Call Number
//        v = inflater.inflate(R.layout.table_row, tableLayout, false);
//        tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"Call Number",(dataset.getCallNumber()==null || dataset.getCallNumber().equals("null") ?"":dataset.getCallNumber())));
//        //TODO: Ext No
//        v = inflater.inflate(R.layout.table_row, tableLayout, false);
//        tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"Ext. No",(dataset.getExtentionNo()==null  || dataset.getExtentionNo().equals("null") ?"":dataset.getExtentionNo())));
//        //TODO: State
//        v = inflater.inflate(R.layout.table_row, tableLayout, false);
//        tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"State",(dataset.getState()==null  || dataset.getState().equals("null") ?"":dataset.getState())));
//        //TODO: CRM State
//        v = inflater.inflate(R.layout.table_row, tableLayout, false);
//        tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"CRM State",(dataset.getCRMState()==null || dataset.getCRMState().equals("null") ? "":dataset.getCRMState())));
//        //TODO: Remarks
//        v = inflater.inflate(R.layout.table_row, tableLayout, false);
//        tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"Call Duration",(dataset.getCallDurration()==null || dataset.getCallDurration().equals("null") ? "":dataset.getCallDurration())));


        //TODO: tableLayout3 set
        tableLayout3.removeAllViewsInLayout();
        tableLayout3.removeAllViews();
        //TODO:  Buttons
        v = inflater.inflate(R.layout.table_row_4_column, tableLayout3, false);
        // TODO: B
        txt = (TextView) v.findViewById(R.id.content_column_1);
        txt.setTextColor(context.getResources().getColor(R.color.White));
        txt.setBackgroundColor(context.getResources().getColor(R.color.red));
        txt.setTypeface(null, Typeface.BOLD);
        txt.setGravity(Gravity.CENTER_HORIZONTAL);
        txt.setText("B");
        txt.setTag(dataset);
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AgentLS agentLS = (AgentLS) view.getTag();
                String status = NetworkUtils.getConnectivityStatusString(context);
                if (!status.contentEquals("No Internet Connection")) {
                    LoginActivity obj = new LoginActivity();
                    String[] str = obj.GetSharePreferenceSession(context);
                    if (str != null) {
                        CallVolleyAgentsLogoutBWC(str[3], str[4], str[0], str[5],str[14],agentLS.getAgentName(),agentLS.getExtentionNo(),"1");
                    }
                } else {
                    MessageDialog.MessageDialog(context,"","No Internet Connection");
                }
            }
        });

        // TODO: Wisper
        txt = (TextView) v.findViewById(R.id.content_column_2);
        txt.setTextColor(context.getResources().getColor(R.color.White));
        txt.setBackgroundColor(context.getResources().getColor(R.color.red));
        txt.setTypeface(null, Typeface.BOLD);
        txt.setGravity(Gravity.CENTER_HORIZONTAL);
        txt.setText("W");
        txt.setTag(dataset);
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AgentLS agentLS = (AgentLS) view.getTag();
                String status = NetworkUtils.getConnectivityStatusString(context);
                if (!status.contentEquals("No Internet Connection")) {
                    LoginActivity obj = new LoginActivity();
                    String[] str = obj.GetSharePreferenceSession(context);
                    if (str != null) {
                        CallVolleyAgentsLogoutBWC(str[3], str[4], str[0], str[5],str[14],agentLS.getAgentName(),agentLS.getExtentionNo(),"2");
                    }
                } else {
                    MessageDialog.MessageDialog(context,"","No Internet Connection");
                }
            }
        });
        // TODO: Conference
        txt = (TextView) v.findViewById(R.id.content_column_3);
        txt.setTextColor(context.getResources().getColor(R.color.White));
        txt.setBackgroundColor(context.getResources().getColor(R.color.red));
        txt.setTypeface(null, Typeface.BOLD);
        txt.setGravity(Gravity.CENTER_HORIZONTAL);
        txt.setText("C");
        txt.setTag(dataset);
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AgentLS agentLS = (AgentLS) view.getTag();
                String status = NetworkUtils.getConnectivityStatusString(context);
                if (!status.contentEquals("No Internet Connection")) {
                    LoginActivity obj = new LoginActivity();
                    String[] str = obj.GetSharePreferenceSession(context);
                    if (str != null) {
                        CallVolleyAgentsLogoutBWC(str[3], str[4], str[0], str[5],str[14],agentLS.getAgentName(),agentLS.getExtentionNo(),"3");
                    }
                } else {
                    MessageDialog.MessageDialog(context,"","No Internet Connection");
                }
            }
        });
        // TODO: Logout
        txt = (TextView) v.findViewById(R.id.content_column_4);
        txt.setTextColor(context.getResources().getColor(R.color.White));
        txt.setBackgroundColor(context.getResources().getColor(R.color.red));
        txt.setTypeface(null, Typeface.BOLD);
        txt.setGravity(Gravity.CENTER_HORIZONTAL);
        txt.setText("Logout");
        txt.setTag(dataset);
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AgentLS agentLS = (AgentLS) view.getTag();
                String status = NetworkUtils.getConnectivityStatusString(context);
                if (!status.contentEquals("No Internet Connection")) {
                    LoginActivity obj = new LoginActivity();
                    String[] str = obj.GetSharePreferenceSession(context);
                    if (str != null) {
                        CallVolleyAgentsLogoutBWC(str[3], str[4], str[0], str[5],str[14],agentLS.getAgentName(),agentLS.getExtentionNo(),"0");
                    }
                } else {
                    MessageDialog.MessageDialog(context,"","No Internet Connection");
                }
            }
        });
        tableLayout3.addView(v);
    }
    private int SetBackgroundColorWithCondition(Context context,String text,int flag){
        int color = context.getResources().getColor(R.color.row_active);
        //Condtion for text means Phone State flag 0
        if (flag == 0) {
            if (text.toLowerCase().equals("intercom ringing") || text.toLowerCase().equals("ringing") ||text.toLowerCase().equals("ring")) {
                color = context.getResources().getColor(R.color.red);
            }else if (text.toLowerCase().equals("intercom incoming") || text.toLowerCase().equals("incoming")) {
                color = context.getResources().getColor(R.color.Pink);
            }else if (text.toLowerCase().equals("intercom dialing") || text.toLowerCase().equals("dialing")) {
                color = context.getResources().getColor(R.color.Yellow);
            }else if (text.toLowerCase().equals("intercom outgoing") || text.toLowerCase().equals("outgoing")) {
                color = context.getResources().getColor(R.color.Orange);
            }else if (text.toLowerCase().equals("idle")) {
                color = context.getResources().getColor(R.color.Green_70);
            }
        } //Condtion for text means CRM State flag 1
        else if (flag == 1) {
            if (text.toLowerCase().equals("idle")) {
                color = context.getResources().getColor(R.color.Green_70);
            }else if (text.toLowerCase().equals("manual") || text.toLowerCase().equals("mannual")) {
                color = context.getResources().getColor(R.color.Yellow);
            }if (text.toLowerCase().equals("wrapup") || text.toLowerCase().equals("wrap up")) {
                color = context.getResources().getColor(R.color.Purple_70);
            }if (text.toLowerCase().equals("ringing")) {
                color = context.getResources().getColor(R.color.red_75);
            }if (text.toLowerCase().equals("incoming")) {
                color = context.getResources().getColor(R.color.Pink);
            }if (text.toLowerCase().equals("break")) {
                color = context.getResources().getColor(R.color.colorPrimary);
            }
        } //Condtion for text means Skill Level  flag 2
        else if (flag == 2) {
            if (text.isEmpty()) {
                color = context.getResources().getColor(R.color.red_75);
            } else {
                color = context.getResources().getColor(R.color.row_active);
            }
        }
        return color;
    }

    private void CallVolleyAgentsLogoutBWC(final String DeviceID, final String UserID,final String SessionID,final String DivisionID,final String CompanyID,final String AgName,final String Ext,final String Flg){
        showDialog();
        String Url = Flg.equals("0") ?  "CallCenterAgLogOut" : "CallCenterAgLiveCallAction";
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+Url, new Response.Listener<String>()
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
                        MessageDialog.MessageDialog(context, "", Msg);
                    } else {
                        MessageDialog.MessageDialog(context, "", Msg);
                    }
                    hideDialog();
                }catch (Exception e){
                    MessageDialog.MessageDialog(context,"Exception",""+e.toString());
                    hideDialog();
                }
                //hideDialog();
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", ""+error);
                MessageDialog.MessageDialog(context,"Error",""+error.toString());
                hideDialog();
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
                params.put("AgName", AgName);
                params.put("Ext", Ext);
                params.put("Flg", Flg);
                Log.d(TAG,"Call Center parameters:"+params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
    }
    private void showDialog() {
        if(progressDialog!=null) {
            progressDialog.show();
        }
    }
    private void hideDialog() {
        if(progressDialog!=null) {
            progressDialog.dismiss();
        }
    }
}
