package administration.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.FileProvider;
import android.view.ContextThemeWrapper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.singlagroup.AppController;
import com.singlagroup.BuildConfig;
import com.singlagroup.LoginActivity;
import com.singlagroup.R;
import com.singlagroup.customwidgets.AeSimpleSHA1;
import com.singlagroup.customwidgets.CursorColor;
import com.singlagroup.customwidgets.DateFormatsMethods;
import com.singlagroup.customwidgets.FileOpenByIntent;
import com.singlagroup.customwidgets.MessageDialog;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import administration.database.DatabaseSqlLiteHandlerVoucherAuthorization;
import administration.datasets.VTypeDetailsDataset;
import orderbooking.StaticValues;
import services.NetworkUtils;

/**
 * Created by Rakesh on 08-Oct-16.
 */
public class RequestForApprovalAdapter extends RecyclerView.Adapter<RequestForApprovalAdapter.RecyclerViewHolder>{

    private Context context;
    private ProgressDialog progressDialog,pDialog;
    String FileExtn="";
    private LayoutInflater inflater;
    private List<VTypeDetailsDataset> datasetList,filterDatasetList;
    private DatabaseSqlLiteHandlerVoucherAuthorization DBAuth;
    public static int[] SpnStatus;
    public static String[] StrRemarks;
    private static String TAG = RequestForApprovalAdapter.class.getSimpleName();
    public RequestForApprovalAdapter(Context context, List<VTypeDetailsDataset> datasetList) {
        this.context=context;
        this.datasetList=datasetList;
        this.filterDatasetList = new ArrayList<>();
        this.filterDatasetList.addAll(this.datasetList);
        this.inflater = LayoutInflater.from(context);
        this.DBAuth = new DatabaseSqlLiteHandlerVoucherAuthorization(context);
        this.progressDialog = new ProgressDialog(this.context);
        this.progressDialog.setMessage("Please wait...");
        this.progressDialog.setCanceledOnTouchOutside(false);

        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Downloading file. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setMax(100);
        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pDialog.setCancelable(true);

        this.SpnStatus = new int[filterDatasetList.size()];
        for (int i=0; i<filterDatasetList.size(); i++){
            SpnStatus[i] = filterDatasetList.get(i).getStatus();
        }
        this.StrRemarks = new String[filterDatasetList.size()];
        for (int i=0; i<filterDatasetList.size(); i++){
            StrRemarks[i] = filterDatasetList.get(i).getRemark();
        }
    }
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.cardview_all_customer_list, parent, false);
        RecyclerViewHolder viewHolder=new RecyclerViewHolder(v);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {

        final VTypeDetailsDataset dataset = filterDatasetList.get(position);

        holder.cardView.setTag(dataset);
        //TODO: call TableLayout method
        setTableLayout(dataset,position,holder.tableLayout,holder.tableLayout2,holder.tableLayout3);
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
                    for (VTypeDetailsDataset item : datasetList) {
                        if(item.getPartyName()!=null && item.getDocDate()!=null && item.getDocNo()!=null && item.getBranch()!=null && item.getRequested()!=null && item.getAmendNo()!=null)
                        if (item.getPartyName().toLowerCase().contains(text.toLowerCase()) ||
                            item.getDocDate().toLowerCase().contains(text.toLowerCase()) ||
                            item.getDocNo().toLowerCase().contains(text.toLowerCase()) ||
                            item.getBranch().toLowerCase().contains(text.toLowerCase()) ||
                            item.getRequested().toLowerCase().contains(text.toLowerCase()) ||
                            item.getAmendNo().toLowerCase().contains(text.toLowerCase()) ) {
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
        CheckBox checkBox;
        TableLayout tableLayout,tableLayout2,tableLayout3;
        ImageView imageViewInfo;
        public RecyclerViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.card_view);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox_multi_customer);
            tableLayout = (TableLayout) itemView.findViewById(R.id.table_Layout);
            tableLayout2 = (TableLayout) itemView.findViewById(R.id.table_Layout2);
            tableLayout3 = (TableLayout) itemView.findViewById(R.id.table_Layout3);
            imageViewInfo = (ImageView) itemView.findViewById(R.id.imageview_info);
        }
    }
    //TODO: Display TableLayout
    private void setTableLayout(final VTypeDetailsDataset dataset, final int pos, final TableLayout tableLayout, final TableLayout tableLayout2, TableLayout tableLayout3){

        //TODO: TableLayout set
        tableLayout.removeAllViewsInLayout();
        tableLayout.removeAllViews();
       //TODO: 1st Row
        View v = inflater.inflate(R.layout.table_row, tableLayout, false);
        TextView txtHeader= (TextView) v.findViewById(R.id.header);
        txtHeader.setText("Date:");

        TextView txt= (TextView) v.findViewById(R.id.content);
        txt.setText(""+(dataset.getDocDate()==null  || dataset.getDocDate().equals("null") ?"": DateFormatsMethods.DateFormat_DD_MM_YYYY(dataset.getDocDate().substring(0,10))));
        tableLayout.addView(v);

        //TODO: 2nd Row
        View v1 = inflater.inflate(R.layout.table_row, tableLayout, false);
        TextView txtHeader1= (TextView) v1.findViewById(R.id.header);
        txtHeader1.setText("Branch:");

        TextView txt1= (TextView) v1.findViewById(R.id.content);
        txt1.setText(""+(dataset.getBranch()==null  || dataset.getBranch().equals("null") ?"":dataset.getBranch()));
        tableLayout.addView(v1);
        //TODO: 3rd Row
        View v2 = inflater.inflate(R.layout.table_row, tableLayout, false);
        TextView txtHeader2= (TextView) v2.findViewById(R.id.header);
        txtHeader2.setText("Doc No:");

        TextView txt2= (TextView) v2.findViewById(R.id.content);
        txt2.setText(""+(dataset.getDocNo()==null  || dataset.getDocNo().equals("null") ?"":dataset.getDocNo()));
        tableLayout.addView(v2);
        //TODO: 4th Row
        View v3 = inflater.inflate(R.layout.table_row, tableLayout, false);
        TextView txtHeader3= (TextView) v3.findViewById(R.id.header);
        txtHeader3.setText("Party Name:");

        TextView txt3= (TextView) v3.findViewById(R.id.content);
        txt3.setText(""+(dataset.getPartyName()==null  || dataset.getPartyName().equals("null") ?"":dataset.getPartyName()));
        tableLayout.addView(v3);
       //TODO: 5th Row
        View v4 = inflater.inflate(R.layout.table_row, tableLayout, false);
        TextView txtHeader4= (TextView) v4.findViewById(R.id.header);
        txtHeader4.setText("Narration:");

        TextView txt4= (TextView) v4.findViewById(R.id.content);
        txt4.setText(""+(dataset.getNarration()==null  || dataset.getNarration().equals("null") ?"":dataset.getNarration()));
        tableLayout.addView(v4);
        //TODO: 7th Row
        View v6 = inflater.inflate(R.layout.table_row, tableLayout, false);
        TextView txtHeader6= (TextView) v6.findViewById(R.id.header);
        txtHeader6.setText("Requested By:");

        TextView txt6= (TextView) v6.findViewById(R.id.content);
        txt6.setText(""+(dataset.getRequested()==null  || dataset.getRequested().equals("null") ?"":dataset.getRequested()));
        tableLayout.addView(v6);
        //TODO: 8th Row
        View v7 = inflater.inflate(R.layout.table_row, tableLayout, false);
        TextView txtHeader7= (TextView) v7.findViewById(R.id.header);
        txtHeader7.setText("Amount:");

        TextView txt7= (TextView) v7.findViewById(R.id.content);
        txt7.setText(""+(dataset.getAmount()==null  || dataset.getAmount().equals("null") ?"":"₹"+dataset.getAmount()));
        tableLayout.addView(v7);
        //TODO: 9th Row
        View v8 = inflater.inflate(R.layout.table_row, tableLayout, false);
        TextView txtHeader8= (TextView) v8.findViewById(R.id.header);
        txtHeader8.setText("Amend Revision No:");

        TextView txt8= (TextView) v8.findViewById(R.id.content);
        txt8.setText(""+(dataset.getAmendNo()==null  || dataset.getAmendNo().equals("null") ?"":dataset.getAmendNo()));
        tableLayout.addView(v8);
        //TODO: 6th Row
        View v5 = inflater.inflate(R.layout.table_row, tableLayout, false);
        TextView txtHeader5= (TextView) v5.findViewById(R.id.header);
        txtHeader5.setText("Remark:");

        StrRemarks[pos] = (dataset.getRemark()==null  || dataset.getRemark().equals("null") ?"":dataset.getRemark());
        TextView txt5= (TextView) v5.findViewById(R.id.content);
        txt5.setText(""+StrRemarks[pos]);
        tableLayout.addView(v5);
        //TODO: 1st Row
        View view = inflater.inflate(R.layout.table_row_header_with_spinner, tableLayout, false);
        txtHeader = (TextView) view.findViewById(R.id.header);
        txtHeader.setText("Status:");

        Spinner spinner = (Spinner) view.findViewById(R.id.Spinner);
        spinner.setSelection(SpnStatus[pos]);
        spinner.setTag(pos);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayAdapter arrayAdapter = (ArrayAdapter) parent.getAdapter();
                // MessageDialog(context,"",""+arrayAdapter.getItem(position));
                SpnStatus[pos] = position;
                if (position == 2 || position == 3) {
                    tableLayout2.setVisibility(View.VISIBLE);
                    //notifyDataSetChanged();
//                    if (tableLayout2.getChildCount()>0){
//
//                    }
//                    if (tableLayout2.getChildCount()>0) {
//                        String Remarks = "";
//                        for(int i = 0; i < tableLayout2.getChildCount(); i++){
//                            //Remember that .getChildAt() method returns a View, so you would have to cast a specific control.
//                            TableRow row = (TableRow) tableLayout2.getChildAt(i);
//                            //This will iterate through the table row.
//                            for(int j = 0; j < row.getChildCount(); j++){
//                                EditText editText = (EditText) row.getChildAt(j);
//                                //Do what you need to do.
//                                Remarks = editText.getText().toString();
//                            }
//                        }
//                        DialogRemarks( 0,""+Remarks, tableLayout2,pos);
//                        filterDatasetList.get(pos).setStatus(position);
//                        DBAuth.UpdateStatusRemarks(position,Remarks,dataset.getDocID());
//                    }else{
//                        DialogRemarks(1,"", tableLayout2,pos);
//                        filterDatasetList.get(pos).setStatus(position);
//                        DBAuth.UpdateStatusRemarks(position,"",dataset.getDocID());
//                    }
                } else {
                    tableLayout2.setVisibility(View.GONE);
                    //notifyDataSetChanged();
//                    if (tableLayout2.getChildCount()>0) {
//                        for(int i = 0; i < tableLayout2.getChildCount(); i++){
//                            //Remember that .getChildAt() method returns a View, so you would have to cast a specific control.
//                            TableRow row = (TableRow) tableLayout2.getChildAt(i);
//                            //This will iterate through the table row.
//                            for(int j = 0; j < row.getChildCount(); j++){
//                                //EditText editText = (EditText) row.getChildAt(j);
//                                row.removeAllViews();
//                            }
//                            tableLayout2.removeViewAt(i);
//                        }
//                        filterDatasetList.get(pos).setStatus(position);
//                        DBAuth.UpdateStatusRemarks(position,"",dataset.getDocID());
//                        notifyDataSetChanged();
//                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        tableLayout.addView(view);
        //TODO: TableLayout2 set
        tableLayout2.removeAllViewsInLayout();
        tableLayout2.removeAllViews();
        view = inflater.inflate(R.layout.table_row_edittext, tableLayout2, false);
        EditText editText = (EditText) view.findViewById(R.id.editText);
        editText.setHint("*Remarks");
        editText.setText(""+StrRemarks[pos]);
        tableLayout2.addView(view);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                StrRemarks[pos] = s.toString();
                //notifyDataSetChanged();
            }
        });
        //TODO: TableLayout3 set
        tableLayout3.removeAllViewsInLayout();
        tableLayout3.removeAllViews();
        //TODO: 2nd Row
        view = inflater.inflate(R.layout.table_row_button, tableLayout3, false);
        //TODO: Button View Report
        Button btnViewReport= (Button) view.findViewById(R.id.Button_View_Report);
        btnViewReport.setTag(R.id.dataset,dataset);
        btnViewReport.setTag(R.id.position,pos);
        btnViewReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VTypeDetailsDataset approvalDataset = (VTypeDetailsDataset) v.getTag(R.id.dataset);
                int position = (int) v.getTag(R.id.position);
                String status = NetworkUtils.getConnectivityStatusString(context);
                if (!status.contentEquals("No Internet Connection")) {
                    LoginActivity obj=new LoginActivity();
                    String[] str = obj.GetSharePreferenceSession(context);
                    if (str!=null) {
                        CallVolleyViewReport(str[3], str[4], str[0],str[14] , str[5], approvalDataset.getDocID(),String.valueOf(approvalDataset.getVType()));
                    }
                } else {
                    MessageDialog.MessageDialog(context,"",status);
                }
            }
        });
        //TODO: Button Ok
        Button btnOk= (Button) view.findViewById(R.id.button_ok);
        btnOk.setTag(R.id.dataset,dataset);
        btnOk.setTag(R.id.position,pos);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VTypeDetailsDataset approvalDataset = (VTypeDetailsDataset) v.getTag(R.id.dataset);
                int position = (int) v.getTag(R.id.position);
                String status = NetworkUtils.getConnectivityStatusString(context);
                if (!status.contentEquals("No Internet Connection")) {
                    LoginActivity obj=new LoginActivity();
                    String[] str = obj.GetSharePreferenceSession(context);
                    if (str!=null) {
                        String Remarks = StrRemarks[position];
                        String Status = ""+SpnStatus[position];
                        if (SpnStatus[position] == 2 || SpnStatus[position] == 3) {
                            if (!Remarks.isEmpty()) {
                                CallVolleyApprove(str[3], str[4], str[0], str[14], str[5], "" + approvalDataset.getVType(), approvalDataset.getDocID(), Remarks, Status);
                            }else{
                                MessageDialog.MessageDialog(context,"","*Remarks is mandatory");
                            }
                        }else{
                            if (SpnStatus[position]>0)
                            CallVolleyApprove(str[3], str[4], str[0], str[14], str[5], "" + approvalDataset.getVType(), approvalDataset.getDocID(), "", Status);
                        }
                    }
                } else {
                    MessageDialog.MessageDialog(context,"",status);
                }
            }
        });
        tableLayout3.addView(view);
    }
    private void CallVolleyViewReport(final String DeviceID, final String UserID,final String SessionID,final String CompanyID,final String DivisionID,final String VID,final String VType){
        showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"GetVoucherPdf", new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                // response
                Log.d("Response", response);
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    int Status=(jsonObject.getString("status")==null)? 0 :jsonObject.getInt("status");
                    String Msg=(jsonObject.getString("msg")==null)?"Server is not responding":jsonObject.getString("msg");
                    if (Status == 1) {
                        JSONArray jsonArray = jsonObject.getJSONArray("Result");
                        for (int i=0; i<jsonArray.length(); i++) {
                            String Path = jsonArray.getJSONObject(i).getString("FileName");
                            int p = Path.lastIndexOf('.');
                            if (p >= 0) {
                                FileExtn = Path.substring(p);
                            }
                            String Filepath = jsonArray.getJSONObject(i).getString("Filepath");
                            // starting new Async Task
                            new DownloadFileFromURL().execute(Filepath);
//                            break;
                        }
                    } else {
                        MessageDialog.MessageDialog(context,"",Msg);
                    }
                }catch (Exception e){
                    MessageDialog.MessageDialog(context,"Exception",""+e.toString());
                }
                hideDialog();
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
                params.put("VID", VID);
                params.put("VType", VType);
                Log.d(TAG,"Approve parameters:"+params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
    }
    private void CallVolleyApprove(final String DeviceID, final String UserID,final String SessionID,final String CompanyID,final String DivisionID,final String VType,final String VID,final String Remarks,final String ApprovalStatus){
        showpDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"VoucherAuthorisation", new Response.Listener<String>()
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
                        //JSONObject jsonObjectJSONObject = jsonObject.getJSONObject("Result");
                        //MessageDialog.MessageDialog(context,"Approved",""+jsonObjectJSONObject.getString("Msg"));
                        MessageDialogByIntent(context,"", Msg);
                    } else {
                        MessageDialog.MessageDialog(context,"",Msg);
                    }
                }catch (Exception e){
                    MessageDialog.MessageDialog(context,"Exception",""+e.toString());
                }
                hideDialog();
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
            protected Map<String, String> getParams(){
                Map<String, String>  params = new HashMap<String, String>();
                params.put("DeviceID", DeviceID);
                params.put("UserID", UserID);
                params.put("SessionID", SessionID);
                params.put("DivisionID", DivisionID);
                params.put("CompanyID", CompanyID);
                params.put("VType", VType);
                params.put("VID", VID);
                params.put("Remarks", Remarks);
                params.put("ApprovalStatus", ApprovalStatus);
                Log.e(TAG,"Voucher Authorisation parameters:"+params.toString());
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
    private void hideDialog() {
        if(progressDialog!=null) {
            progressDialog.dismiss();
        }
    }

    private void showPDialog() {
        if(pDialog!=null) {
            pDialog.show();
        }
    }
    private void hidePDialog() {
        if(pDialog!=null) {
            pDialog.dismiss();
        }
    }
    //TODO: Download Report in PDF
    class DownloadFileFromURL extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showPDialog();
        }
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // getting file length
                int lenghtOfFile = conection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                String Path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SinglaGroups/temp"+FileExtn;
                // Output stream to write file
                OutputStream output = new FileOutputStream(Path);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress(""+(int)((total*100)/lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", ""+e.toString());
            }

            return null;
        }
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }
        @Override
        protected void onPostExecute(String file_url) {
            hidePDialog();
            //TODO: View PDF
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SinglaGroups/temp"+FileExtn;
            File file = new File(path);
            if (file.exists()) {
                Uri uri = null;
                // So you have to use Provider
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
                }else{
                    uri = Uri.fromFile(file);
                }
                FileOpenByIntent.FileOpen(context,path,uri);
            }else{
                MessageDialog.MessageDialog(context,"","File not exist");
            }
        }

    }

    //TODO: Re-authentication password
    private void DialogRemarks(int flag, final String Rmks, final TableLayout tableLayout2, final int position){
        final Dialog dialog = new Dialog(new ContextThemeWrapper(context, R.style.DialogSlideAnim));
        dialog.setContentView(R.layout.dialog_authenticate_password);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        if (flag == 1) { dialog.show(); }

        final TextView txtViewTitle = (TextView) dialog.findViewById(R.id.text_Title);
        txtViewTitle.setText("*Remarks");
        final TextInputLayout RemarksWrapper = (TextInputLayout) dialog.findViewById(R.id.editText_Password);
        CursorColor.CursorColor(RemarksWrapper.getEditText());
        RemarksWrapper.getEditText().setHint("Enter Remarks");
        RemarksWrapper.getEditText().setInputType(InputType.TYPE_CLASS_TEXT);
        RemarksWrapper.getEditText().setText(""+Rmks);
        Button btnSubmit = (Button) dialog.findViewById(R.id.btn_submit);
        btnSubmit.setText("OK");
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Remarks
                String status = NetworkUtils.getConnectivityStatusString(context);
                if (!status.contentEquals("No Internet Connection")) {
                    RemarksWrapper.setError("");
                    String Remarks = RemarksWrapper.getEditText().getText().toString();
                    if(!Remarks.isEmpty()) {
                        if (tableLayout2.getChildCount()>0){
                            for(int i = 0; i < tableLayout2.getChildCount(); i++){
                                //Remember that .getChildAt() method returns a View, so you would have to cast a specific control.
                                TableRow row = (TableRow) tableLayout2.getChildAt(i);
                                //This will iterate through the table row.
                                for(int j = 0; j < row.getChildCount(); j++){
                                    if (i == (tableLayout2.getChildCount()-2) && j == 1) {
                                        TextView textView = (TextView) row.getChildAt(j);
                                        StrRemarks[position] = Remarks;
                                        textView.setText(""+StrRemarks[position]);
                                    }
                                }
                            }
                        }
//                        //TODO: TableLayout2 set
//                        tableLayout2.removeAllViewsInLayout();
//                        tableLayout2.removeAllViews();
//
//                        view = inflater.inflate(R.layout.table_row_edittext, tableLayout2, false);
//                        EditText editText = (EditText) view.findViewById(R.id.editText);
//                        editText.setHint("*Remarks");
//                        editText.setText(""+Remarks);
//                        tableLayout2.addView(view);
//                        StrRemarks[position] = Remarks;
                        notifyDataSetChanged();
                        dialog.dismiss();
                    }else {
                        RemarksWrapper.setError("Remarks cann't be blank");
                    }
                } else {
                    MessageDialog.MessageDialog(context,"","No Internet Connection");
                }
            }
        });
    }

    public void MessageDialogByIntent(final Context context, String Title, String Mesaage){
        try {
            final Dialog dialog=new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.cardview_message_box);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            TextView txtViewMessageTitle = (TextView) dialog.findViewById(R.id.textView_messageTitle);
            TextView txtViewMessage = (TextView) dialog.findViewById(R.id.textView_message);
            Button btnOK = (Button) dialog.findViewById(R.id.Button_OK);
            txtViewMessageTitle.setText(Title);
            txtViewMessage.setText(Mesaage);
            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    Activity activity = (Activity)context;
                    activity.finish();
                }
            });
            dialog.show();
        }catch (Exception e){
            Log.e("TAG","MessageDialogException2"+e.toString());
        }
    }
}
