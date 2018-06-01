package print.godown_group_order_with_rate.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
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
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
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
import com.singlagroup.customwidgets.CustomTextView;
import com.singlagroup.customwidgets.DateFormatsMethods;
import com.singlagroup.customwidgets.FileOpenByIntent;
import com.singlagroup.customwidgets.MessageDialog;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import inventory.analysis.catalogue.Database_Sqlite.DatabaseSqlLiteHandlerWishlist;
import inventory.analysis.catalogue.wishlist.GroupWishListActivity;
import orderbooking.StaticValues;
import print.godown_group_order_with_rate.model.Order;
import services.NetworkUtils;

/**
 * Created by Rakesh on 29-Aug-17.
 */
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.RecyclerViewHolder>{

    private Context context;
    private ProgressDialog spotsDialog;
    private ProgressDialog pDialog;
    private LayoutInflater inflater;
    private List<Order> datasetList,filterDatasetList;
    private static String TAG = OrderAdapter.class.getSimpleName();
    public OrderAdapter(Context context, List<Order> datasetList) {
        this.context=context;
        this.datasetList=datasetList;
        this.filterDatasetList = new ArrayList<>();
        this.filterDatasetList.addAll(this.datasetList);
        this.inflater = LayoutInflater.from(context);
        spotsDialog = new ProgressDialog(context);
        spotsDialog.setMessage("Please wait...");
        spotsDialog.setCanceledOnTouchOutside(false);
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Downloading file. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setMax(100);
        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pDialog.setCancelable(true);
    }
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.cardview_all_customer_list, parent, false);
        RecyclerViewHolder viewHolder=new RecyclerViewHolder(v);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {

        final Order dataset = filterDatasetList.get(position);
        //TODO: call TableLayout method
        setTableLayout(dataset,holder.tableLayout,holder.tableLayout2);
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
                    for (Order item : datasetList) {
                        if(item.getOrderNo()!=null && item.getOrderDate()!=null && item.getOrderStatus()!=null)
                            if (item.getOrderNo().toLowerCase().contains(text.toLowerCase()) || item.getOrderDate().toLowerCase().contains(text.toLowerCase()) || item.getOrderStatus().toLowerCase().contains(text.toLowerCase()) ) {
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
        public RecyclerViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.card_view);
            tableLayout = (TableLayout) itemView.findViewById(R.id.table_Layout);
            tableLayout2 = (TableLayout) itemView.findViewById(R.id.table_Layout2);
            imageViewInfo = (ImageView) itemView.findViewById(R.id.imageview_info);
        }
    }
    //TODO: Display TableLayout
    private void setTableLayout(Order dataset, TableLayout tableLayout, TableLayout tableLayout2){

        //TODO: TableLayout set
        tableLayout.removeAllViewsInLayout();
        tableLayout.removeAllViews();
        //TODO: 1st Row
        View v = inflater.inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context, v , "Order No.",dataset.getOrderNo() == null ? "" : dataset.getOrderNo()));
        //TODO: 2nd Row
        v = inflater.inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context, v , "Order Date",(dataset.getOrderDate()==null || dataset.getOrderDate().equals("null") ?"": DateFormatsMethods.DateFormat_DD_MM_YYYY(dataset.getOrderDate().substring(0,10)) + " ("+DateFormatsMethods.DaysHoursMinutesCount(dataset.getOrderDate()+ StaticValues.FromTime)+")")));
        //TODO: 3nd Row
        v = inflater.inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context, v , "Party Name",dataset.getPartyName() == null ? "" : dataset.getPartyName()));
        //TODO: 4nd Row
        String SubPartyName = dataset.getSubParty() == null ? "" : dataset.getSubParty();
        String RefName = dataset.getRefName() == null ? "" : dataset.getRefName();
        if (!RefName.isEmpty()) {
            v = inflater.inflate(R.layout.table_row, tableLayout, false);
            tableLayout.addView(CustomTextView.setTableRow2Columns(context, v, "Ref Name", dataset.getRefName() == null ? "" : dataset.getRefName()));
        }else if (!SubPartyName.isEmpty()) {
            v = inflater.inflate(R.layout.table_row, tableLayout, false);
            tableLayout.addView(CustomTextView.setTableRow2Columns(context, v, "SubParty Name", SubPartyName));
        }
        //TODO: table 2
        tableLayout2.removeAllViewsInLayout();
        tableLayout2.removeAllViews();
        //TODO: 1th Row
        View vt1 = LayoutInflater.from(context).inflate(R.layout.table_row_single_column, tableLayout2, false);

        TextView txtContent1= (TextView) vt1.findViewById(R.id.content);
        txtContent1.setText("Print View");
        txtContent1.setTag(dataset);
        txtContent1.setGravity(Gravity.CENTER_HORIZONTAL);
        txtContent1.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
        tableLayout2.addView(vt1);
        txtContent1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Click
                Order order = (Order) view.getTag();
                if (order!=null){
                    String status = NetworkUtils.getConnectivityStatusString(context);
                    if (!status.contentEquals("No Internet Connection")) {
                        LoginActivity obj=new LoginActivity();
                        String[] str = obj.GetSharePreferenceSession(context);
                        if (str!=null) {
                            //TODO: Call Volley Wishlist Clear
                            CallVolleyPdf(str[3], str[0], str[14], str[4], str[5],order.getOrderID() , "1");
                        }
                    }else{
                        MessageDialog.MessageDialog(context,"",status);
                    }
                }

            }
        });
    }
    private void showDialog() {
        spotsDialog.show();
    }
    private void hideDialog() {
        spotsDialog.dismiss();
    }
    private void CallVolleyPdf(final String DeviceID, final String SessionID, final String CompanyID, final String UserID, final String DivisionID,final String OrderID, final String view){
        showDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"SaleOrderPDFLink", new Response.Listener<String>()
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
                        JSONObject jObjResult = jsonObject.getJSONObject("Result");
                        String Pdflink = (jObjResult.optString("Pdflink")==null ? "" : jObjResult.optString("Pdflink"));
                        if (!Pdflink.isEmpty()) {
                            //TODO: starting new Async Task
                            new DownloadFileFromURL().execute(Pdflink);
                        }else{
                            MessageDialog.MessageDialog(context,"","No record found");
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
                params.put("OrderID", OrderID);
                params.put("view", view);
                Log.d(TAG,"Sale Order PDFLink parameters:"+params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(postRequest);
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

                String Path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SinglaGroups/temp.pdf";
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
            DialogViewShareOption();
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
    //TODO: View and Share option
    private void DialogViewShareOption(){
        final Dialog dialog = new Dialog(new ContextThemeWrapper(context, R.style.DialogSlideAnim));
        dialog.setContentView(R.layout.dialog_document_attachment_selection);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        RadioGroup radioGroupChangeOrShowroom = (RadioGroup) dialog.findViewById(R.id.RadioGroup_document_attachment_selection);
        RadioButton rbView = (RadioButton) dialog.findViewById(R.id.RadioButton_transaction);
        RadioButton rbShare = (RadioButton) dialog.findViewById(R.id.RadioButton_master);
        rbView.setText("View");
        rbView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_view_grey,0,0,0);
        rbShare.setText("Whats app");
        rbShare.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_whats_app,0,0,0);
        radioGroupChangeOrShowroom.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(radioGroup.getCheckedRadioButtonId()==R.id.RadioButton_transaction){
                    //TODO: View
                    //TODO: View PDF
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SinglaGroups/temp.pdf";
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
                    dialog.dismiss();
                }else if(radioGroup.getCheckedRadioButtonId()==R.id.RadioButton_master){
                    //TODO: Share whats app
                    //TODO: View PDF
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SinglaGroups/temp.pdf";
                    File file = new File(path);
                    if (file.exists()) {
                        Uri uri = null;
                        // So you have to use Provider
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
                        }else{
                            uri = Uri.fromFile(file);
                        }
                        //FileOpenByIntent.(context,path,uri);
                        Intent intent1 = new Intent();
                        intent1.setAction(Intent.ACTION_SEND);
                        intent1.putExtra(Intent.EXTRA_STREAM, uri);
                        intent1.setType("application/pdf");
                        intent1.setPackage("com.whatsapp");
                        //intent1.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        context.startActivity(intent1);

                    }else{
                        MessageDialog.MessageDialog(context,"","File not exist");
                    }
                    dialog.dismiss();
                }
            }
        });
    }
}
