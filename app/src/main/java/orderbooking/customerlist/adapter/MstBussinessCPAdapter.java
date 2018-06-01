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
import android.widget.EditText;
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
import orderbooking.customerlist.datasets.MstBussinessPartnerCpDataset;
import orderbooking.customerlist.responsedatasets.ResponsePartyInfoCheck;
import services.NetworkUtils;

/**
 * Created by Rakesh on 13-Dec-17.
 */
public class MstBussinessCPAdapter extends RecyclerView.Adapter<MstBussinessCPAdapter.RecyclerViewHolder>{

    private Context context;
    private ProgressDialog progressDialog;
    private LayoutInflater inflater;
    private List<MstBussinessPartnerCpDataset> datasetList,filterDatasetList;
    private String GodownID="",Godown="",FairID="",PartyID="",PartyName="",SubPartyID="",SubPartyName="",RefName="";
    private static String TAG = MstBussinessCPAdapter.class.getSimpleName();
    public MstBussinessCPAdapter(Context context, List<MstBussinessPartnerCpDataset> datasetList) {
        this.context=context;
        this.datasetList=datasetList;
        this.filterDatasetList = new ArrayList<MstBussinessPartnerCpDataset>();
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

        final MstBussinessPartnerCpDataset dataset = filterDatasetList.get(position);
        //TODO: call TableLayout method
        setTableLayout(dataset,position,holder.tableLayout,holder.tableLayout2,holder.webView);
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
                    for (MstBussinessPartnerCpDataset item : datasetList) {
                        if(item.getPartyName()!=null && item.getSubParty()!=null && item.getCity()!=null && item.getState()!=null && item.getAddress1()!=null && item.getCellNo()!=null && item.getEmail()!=null && item.getDesignation()!=null && item.getCity()!=null)
                            if (item.getPartyName().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getSubParty().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getCity().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getState().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getAddress1().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getCellNo().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getEmail().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getDesignation().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getCity().toLowerCase().contains(text.toLowerCase()) ) {
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
        TableLayout tableLayout,tableLayout2;
        ImageView imageViewInfo;
        WebView webView;
        public RecyclerViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.card_view);
            tableLayout = (TableLayout) itemView.findViewById(R.id.table_Layout);
            tableLayout2 = (TableLayout) itemView.findViewById(R.id.table_Layout2);
            imageViewInfo = (ImageView) itemView.findViewById(R.id.imageview_info);
            webView = (WebView) itemView.findViewById(R.id.web_view);
        }
    }
    //TODO: Display TableLayout
    private void setTableLayout(MstBussinessPartnerCpDataset dataset, final int pos, TableLayout tableLayout, TableLayout tableLayout2,final WebView webView){

        //TODO: TableLayout set
        tableLayout.removeAllViewsInLayout();
        tableLayout.removeAllViews();
        //TODO:  Name
        View v = inflater.inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"Name",(dataset.getName()==null || dataset.getName().equals("null") ?"":dataset.getName())));
        //TODO: Party Name
        v = inflater.inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"Party Name",(dataset.getPartyName()==null || dataset.getPartyName().equals("null") ?"":dataset.getPartyName())));
        //TODO: Sub Party Name
        String SubParty = (dataset.getSubParty()==null || dataset.getSubParty().equals("null") ?"":dataset.getSubParty());
        if (!SubParty.isEmpty()) {
            v = inflater.inflate(R.layout.table_row, tableLayout, false);
            tableLayout.addView(CustomTextView.setTableRow2Columns(context, v, "Party Name", SubParty));
        }
        //TODO: Designation
        v = inflater.inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"Designation",(dataset.getDesignation()==null || dataset.getDesignation().equals("null") ?"":dataset.getDesignation())));
        //TODO: Mobile
        v = inflater.inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"Mobile",(dataset.getCellNo()==null || dataset.getCellNo().equals("null") ?"":dataset.getCellNo())));
        //TODO: Call and WhatsApp
        v = LayoutInflater.from(context).inflate(R.layout.table_row, tableLayout, false);
        TextView txtHeader1= (TextView) v.findViewById(R.id.header);
        txtHeader1.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
        txtHeader1.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_call_grey, 0, 0, 0);
        txtHeader1.setGravity(Gravity.CENTER_VERTICAL);
        txtHeader1.setText("Click to Call");
        txtHeader1.setTag(dataset.getCellNo());

        TextView txt1= (TextView) v.findViewById(R.id.content);
        txt1.setTag(dataset.getCellNo());
        txt1.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
        txt1.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_whats_app, 0, 0, 0);
        txt1.setGravity(Gravity.CENTER_VERTICAL);
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
        //TODO: Address
        String Address = (dataset.getAddress1()==null || dataset.getAddress1().equals("null") ?"":dataset.getAddress1())+", "+(dataset.getAddress2()==null || dataset.getAddress2().equals("null") ?"":dataset.getAddress2())+", "+(dataset.getAddress3()==null || dataset.getAddress3().equals("null") ?"":dataset.getAddress3());
        v = inflater.inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"Address",Address));
        //TODO: City
        v = inflater.inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"City",(dataset.getCity()==null  || dataset.getCity().equals("null") ?"":dataset.getCity())));
        //TODO: State
        v = inflater.inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"State",(dataset.getState()==null  || dataset.getState().equals("null") ?"":dataset.getState())));
        //TODO: Email
        v = inflater.inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"Email",(dataset.getEmail()==null || dataset.getEmail().equals("null") ? "":dataset.getEmail())));
        //TODO: Remarks
        v = inflater.inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"Remarks",(dataset.getSG_Remarks()==null || dataset.getSG_Remarks().equals("null") ? "":dataset.getSG_Remarks())));

        //TODO: Table Layout 2
        tableLayout2.removeAllViewsInLayout();
        tableLayout2.removeAllViews();
        //TODO: Click to update
        View view = inflater.inflate(R.layout.table_row_single_column, tableLayout2, false);
        txt1= (TextView) view.findViewById(R.id.content);
        txt1.setTextColor(context.getResources().getColor(R.color.Color_Maroon));
        txt1.setText("Click to Update");
        txt1.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
        tableLayout2.addView(view);
    }
}
