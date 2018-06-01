package orderbooking.AccountInfoSend.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.IdRes;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.singlagroup.customwidgets.SendToOtherApps;
import com.singlagroup.customwidgets.ValidationMethods;
import com.singlagroup.datasets.GodownDataset;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DatabaseController.DatabaseSqlLiteHandlerUserInfo;
import orderbooking.StaticValues;
import orderbooking.customerlist.adapter.RunningFairAdapter;
import orderbooking.customerlist.datasets.SelectCustomerForOrderDataset;
import orderbooking.customerlist.datasets.SelectSubCustomerForOrderDataset;
import orderbooking.customerlist.datasets.SelectSubCustomerForOrderDataset;
import orderbooking.customerlist.datasets.SelectSubCustomerForOrderDataset;
import orderbooking.customerlist.responsedatasets.ResponsePartyInfoCheck;
import services.NetworkUtils;

/**
 * Created by Rakesh on 08-Oct-16.
 */
public class AccountInfoSendSelectSubCustomerForOrderAdapter extends RecyclerView.Adapter<AccountInfoSendSelectSubCustomerForOrderAdapter.RecyclerViewHolder>{

    private Context context;
    private ProgressDialog progressDialog;
    private LayoutInflater inflater;
    private List<SelectSubCustomerForOrderDataset> datasetList,filterDatasetList;
    private static String TAG = AccountInfoSendSelectSubCustomerForOrderAdapter.class.getSimpleName();
    public AccountInfoSendSelectSubCustomerForOrderAdapter(Context context, List<SelectSubCustomerForOrderDataset> datasetList) {
        this.context=context;
        this.datasetList=datasetList;
        this.filterDatasetList = new ArrayList<SelectSubCustomerForOrderDataset>();
        this.filterDatasetList.addAll(this.datasetList);
        this.inflater = LayoutInflater.from(context);
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
                //DialogShareOptions(dataset);
            }
        });
        //TODO: call TableLayout method
        setTableLayout(dataset,holder.tableLayout,holder.webView);
    }
    @Override
    public int getItemCount() {
        return (null != filterDatasetList ? filterDatasetList.size() : 0);
    }
    public Object getItem(int position){
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
