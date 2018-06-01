package uploadimagesfiles.voucherdocupload.adapter;

import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.view.ContextThemeWrapper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.singlagroup.AppController;
import com.singlagroup.BuildConfig;
import com.singlagroup.LoginActivity;
import com.singlagroup.R;
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
import orderbooking.StaticValues;
import services.NetworkUtils;
import uploadimagesfiles.voucherdocupload.VoucherDocumentUploadActivity;

public class RecyclerViewAttachedListAdapter extends RecyclerView.Adapter<RecyclerViewAttachedListAdapter.RecyclerViewHolder>{

    private Context mContext;
    private LayoutInflater inflater;
    private List<Map<String,String>> mapList, filterMapList;
    private String DocID,DocNo,VType,VHeading,Type,FileExtn="";
    private ProgressDialog progressDialog,pDialog;
    private static String TAG = RecyclerViewAttachedListAdapter.class.getSimpleName();
    public RecyclerViewAttachedListAdapter(Context mContext, List<Map<String,String>> mapList,String DocID,String DocNo,String VType,String VHeading,String Type) {
        this.mContext=mContext;
        this.mapList=mapList;
        this.filterMapList = new ArrayList<>();
        this.filterMapList.addAll(this.mapList);
        this.inflater = LayoutInflater.from(mContext);
        this.DocID = DocID;
        this.DocNo = DocNo;
        this.VType = VType;
        this.VHeading = VHeading;
        this.Type = Type;
        this.progressDialog = new ProgressDialog(mContext);
        this.progressDialog.setMessage("Please Wait...");
        this.progressDialog.setCanceledOnTouchOutside(false);

        pDialog = new ProgressDialog(mContext);
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

        Map<String,String> map = filterMapList.get(position);
        setTableLayout(map,holder.tableLayout,holder.tableLayout2);
    }
    @Override
    public int getItemCount() {
        return (null != filterMapList ? filterMapList.size() : 0);
    }
    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        TableLayout tableLayout,tableLayout2;
        CardView cardView;
        public RecyclerViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.card_view);
            tableLayout = (TableLayout) itemView.findViewById(R.id.table_Layout);
            tableLayout2 = (TableLayout) itemView.findViewById(R.id.table_Layout2);
        }
    }
    private void setTableLayout(final Map<String,String> dataset, TableLayout tableLayout, TableLayout tableLayout2){

        //TODO: TableLayout set
        tableLayout.removeAllViewsInLayout();
        tableLayout.removeAllViews();
        //TODO: 1st Row
        if (!DocNo.isEmpty()) {
            View v = inflater.inflate(R.layout.table_row, tableLayout, false);
            //TODO: Header
            TextView txtHeader = (TextView) v.findViewById(R.id.header);
            txtHeader.setText("Doc No:");
            //TODO: Content
            TextView txt = (TextView) v.findViewById(R.id.content);
            txt.setText("" + (DocNo == null ? "" : DocNo));
            tableLayout.addView(v);
        }
        //TODO: 2nd Row
        View v1 = inflater.inflate(R.layout.table_row, tableLayout, false);
        //TODO: Header
        TextView txtHeader1= (TextView) v1.findViewById(R.id.header);
        txtHeader1.setText("Doc Date:");
        //TODO: Content
        TextView txt1= (TextView) v1.findViewById(R.id.content);
        txt1.setText(""+(dataset.get("DocDate")==null?"": DateFormatsMethods.DateFormat_DD_MM_YYYY(dataset.get("DocDate"))));
        tableLayout.addView(v1);

        //TODO: 3rd Row
        View v2 = inflater.inflate(R.layout.table_row, tableLayout, false);
        //TODO: Header
        TextView txtHeader2= (TextView) v2.findViewById(R.id.header);
        txtHeader2.setText("User Name:");
        //TODO: Content
        TextView txt2= (TextView) v2.findViewById(R.id.content);
        txt2.setText(""+(dataset.get("UserFullName")==null?"":dataset.get("UserFullName")));
        tableLayout.addView(v2);

        //TODO: 4rd Row
        View v3 = inflater.inflate(R.layout.table_row, tableLayout, false);
        //TODO: Header
        TextView txtHeader3= (TextView) v3.findViewById(R.id.header);
        txtHeader3.setText("User ID:");
        //TODO: Content
        TextView txt3= (TextView) v3.findViewById(R.id.content);
        txt3.setText(""+(dataset.get("UserName")==null?"":dataset.get("UserName")));
        tableLayout.addView(v3);

        //TODO: 5rd Row
        View v4 = inflater.inflate(R.layout.table_row, tableLayout, false);
        //TODO: Header
        TextView txtHeader4= (TextView) v4.findViewById(R.id.header);
        txtHeader4.setText("Description:");
        //TODO: Content
        TextView txt4= (TextView) v4.findViewById(R.id.content);
        txt4.setText(""+(dataset.get("Description")==null?"":dataset.get("Description")));
        tableLayout.addView(v4);

//        //TODO: 6rd Row
//        View v5 = inflater.inflate(R.layout.table_row, tableLayout, false);
//        //TODO: Header
//        TextView txtHeader5= (TextView) v5.findViewById(R.id.header);
//        txtHeader5.setText("Remark:");
//        //TODO: Content
//        TextView txt5= (TextView) v5.findViewById(R.id.content);
//        txt5.setText(""+(dataset.get("Remark")==null?"":dataset.get("Remark")));
//        tableLayout.addView(v5);

        //TODO: 7rd Row
        View v6 = inflater.inflate(R.layout.table_row, tableLayout, false);
        //TODO: Header
        TextView txtHeader6= (TextView) v6.findViewById(R.id.header);
        txtHeader6.setText("File Name:");
        //TODO: Content
        TextView txt6= (TextView) v6.findViewById(R.id.content);
        txt6.setText(""+(dataset.get("FileName")==null?"":dataset.get("FileName")));
        tableLayout.addView(v6);


        //TODO: Second Table
        //TODO: TableLayout2 set
        tableLayout2.removeAllViewsInLayout();
        tableLayout2.removeAllViews();
        //TODO: 1st Row
        View view = inflater.inflate(R.layout.table_row_4_column, tableLayout, false);
        //TODO: View
        TextView txtView= (TextView) view.findViewById(R.id.content_column_1);
        txtView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_view_grey, 0, 0, 0);
        txtView.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
        txtView.setBackground(mContext.getResources().getDrawable(R.color.BurlyWood));
        txtView.setText("View");
        txtView.setTag(dataset);
        txtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String,String> dataset = (Map<String,String>) view.getTag();
                String FileViewUrl = dataset.get("FileViewUrl");
                String FileName = dataset.get("FileName");
                int p = FileName.lastIndexOf('.');
                if (p >= 0) {
                    FileExtn = FileName.substring(p);
                }
                String status = NetworkUtils.getConnectivityStatusString(mContext);
                if (!status.contentEquals("No Internet Connection")) {
                    if (FileViewUrl!=null && !FileViewUrl.isEmpty()) {
                        new DownloadFileFromURL().execute(FileViewUrl,"0");
                    }else{
                        MessageDialog.MessageDialog(mContext,"","File Url is not found!!!");
                    }
                }else{
                    MessageDialog.MessageDialog(mContext,"",status);
                }
            }
        });
        //TODO: View
        TextView txtShare= (TextView) view.findViewById(R.id.content_column_2);
        txtShare.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_share_grey, 0, 0, 0);
        txtShare.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
        txtShare.setBackground(mContext.getResources().getDrawable(R.color.BurlyWood));
        txtShare.setText("Share");
        txtShare.setTag(dataset);
        txtShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String,String> dataset = (Map<String,String>) view.getTag();
                String FileViewUrl = dataset.get("FileViewUrl");
                String FileName = dataset.get("FileName");
                int p = FileName.lastIndexOf('.');
                if (p >= 0) {
                    FileExtn = FileName.substring(p);
                }
                String status = NetworkUtils.getConnectivityStatusString(mContext);
                if (!status.contentEquals("No Internet Connection")) {
                    if (FileViewUrl!=null && !FileViewUrl.isEmpty()) {
                        new DownloadFileFromURL().execute(FileViewUrl,"1");
                    }else{
                        MessageDialog.MessageDialog(mContext,"","File Url is not found!!!");
                    }
                }else{
                    MessageDialog.MessageDialog(mContext,"",status);
                }
            }
        });
        //TODO: Edit
        TextView txtEdit= (TextView) view.findViewById(R.id.content_column_3);
        txtEdit.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_edit_grey, 0, 0, 0);
        txtEdit.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
        txtEdit.setBackground(mContext.getResources().getDrawable(R.color.BurlyWood));
        txtEdit.setText("Edit");
        txtEdit.setTag(dataset);
        txtEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String,String> dataset = (Map<String,String>) view.getTag();
                if (StaticValues.editFlag == 1) {
                    DilaogDeleteOrEditConfirmation(0, VType, VHeading, DocID, Type, dataset.get("ID"), dataset.get("Description"));
                }else{
                    MessageDialog.MessageDialog(mContext,"Alert","You don't have Edit permission");
                }
            }
        });
        //TODO: Delete
        TextView txtDelete= (TextView) view.findViewById(R.id.content_column_4);
        txtDelete.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_delete_grey, 0, 0, 0);
        txtDelete.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
        txtDelete.setBackground(mContext.getResources().getDrawable(R.color.BurlyWood));
        txtDelete.setText("Delete");
        txtDelete.setTag(dataset);
        txtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Map<String,String> dataset = (Map<String,String>) view.getTag();
            if (StaticValues.removeFlag == 1) {
                DilaogDeleteOrEditConfirmation(1,VType,VHeading,DocID,Type,dataset.get("ID"),"");
            }else{
                MessageDialog.MessageDialog(mContext,"Alert","You don't have Delete permission");
            }
            }
        });
        //TODO: Add View on TableLayout
        tableLayout2.addView(view);
    }
    private void DialogViewOrShareSelection(){
        final Dialog dialog = new Dialog(new ContextThemeWrapper(mContext, R.style.DialogSlideAnim));
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
        RadioButton radioButtonView = (RadioButton) dialog.findViewById(R.id.RadioButton_transaction);
        RadioButton radioButtonShare = (RadioButton) dialog.findViewById(R.id.RadioButton_master);
        radioButtonView.setText("View");
        radioButtonShare.setText("Share");
        radioGroupChangeOrShowroom.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(radioGroup.getCheckedRadioButtonId()==R.id.RadioButton_transaction){
                    //TODO: View
                    dialog.dismiss();
                }else if(radioGroup.getCheckedRadioButtonId()==R.id.RadioButton_master){
                    //TODO: Share

                    dialog.dismiss();
                }
            }
        });
//
    }
    private void DialogViewImage(final Context mContext,String Url) {
        final Dialog dialog = new Dialog(new ContextThemeWrapper(mContext, R.style.DialogSlideAnim));
        dialog.setContentView(R.layout.dialog_view_image);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.TOP;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        //TODO: Declarations
        ImageView imageView = (ImageView) dialog.findViewById(R.id.Image_View);
        WebView webView = (WebView) dialog.findViewById(R.id.web_view);
        Button btnOK = (Button) dialog.findViewById(R.id.btn_ok);
        webView.setVisibility(View.VISIBLE);
        webView.getSettings().setJavaScriptEnabled(true); // enable javascript
        webView.getSettings().setUseWideViewPort(true);
        webView.setInitialScale(1);
        webView.getSettings().setBuiltInZoomControls(true);
        webView .loadUrl(Url);
//            String pdf = "http://www.adobe.com/devnet/acrobat/pdfs/pdf_open_parameters.pdf";
//            webView.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url=" + pdf);
        webView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,String contentDisposition, String mimeType,long contentLength) {

                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setMimeType(mimeType);
                String cookies = CookieManager.getInstance().getCookie(url);
                request.addRequestHeader("cookie", cookies);
                request.addRequestHeader("User-Agent", userAgent);
                request.setDescription("Downloading file...");
                request.setTitle(URLUtil.guessFileName(url, contentDisposition,mimeType));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimeType));
                DownloadManager dm = (DownloadManager) mContext.getSystemService(mContext.DOWNLOAD_SERVICE);
                dm.enqueue(request);
                Toast.makeText(mContext, "Downloading File",Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
    private void DilaogDeleteOrEditConfirmation(final int flag, final String VType, final String VHeading, final String VID, final String Type, final String ID, final String Description){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setMessage("Are you sure? You want to "+(flag == 1 ? "Delete" : "Edit"));
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                if (flag == 1) { //TODO: Delete
                    String status = NetworkUtils.getConnectivityStatusString(mContext);
                    if (!status.contentEquals("No Internet Connection")) {
                        LoginActivity obj = new LoginActivity();
                        String[] str = obj.GetSharePreferenceSession(mContext);
                        if (str != null)
                            CallVolleyDeleteAttachment(str[3], str[0], str[4], str[14], str[5], str[15],VType,VHeading,VID,Type,ID);
                            dialog.dismiss();
                    } else {
                        MessageDialog.MessageDialog(mContext, "", status);
                    }
                }else{  //TODO: Edit
                    Intent intent = new Intent(mContext, VoucherDocumentUploadActivity.class);
                    intent.putExtra("VType", VType);
                    intent.putExtra("VHeading", VHeading);
                    intent.putExtra("VID", DocID);
                    intent.putExtra("Type", Type);
                    intent.putExtra("ID", ID);
                    intent.putExtra("Description", Description);
                    mContext.startActivity(intent);
                    //MessageDialog.MessageDialog(mContext,"","Comming soon");
                }
            }
        });
        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    private void CallVolleyDeleteAttachment(final String DeviceID,final String SessionID, final String UserID,final String CompanyID,final String DivisionID,final String BranchID,final String VType,final String VHeading,final String VID,final String Type,final String ID) {
        showDialog();
        StringRequest postRequest = new StringRequest(Request.Method.POST, StaticValues.BASE_URL+"DocAttechmentDel", new Response.Listener<String>()
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
                        MessageDialog.MessageDialogShowFinish(mContext,"",Msg);
                    } else {
                        MessageDialog.MessageDialog(mContext,"",Msg);
                    }
                }catch (Exception e){
                    MessageDialog.MessageDialog(mContext,"Exception",""+e.toString());
                }
                hideDialog();
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", ""+error);
                MessageDialog.MessageDialog(mContext,"Error",""+error.toString());
                hideDialog();
            }
        } ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();
                params.put("DeviceID", DeviceID);
                params.put("UserID", UserID);
                params.put("SessionID", SessionID);
                params.put("CompanyID", CompanyID);
                params.put("DivisionID", DivisionID);
                params.put("BranchID", BranchID);
                params.put("VType", VType);
                params.put("VHeading", VHeading);
                params.put("VID", VID);
                params.put("Type", Type);
                params.put("ID", ID);
                Log.d(TAG,"Delete Attachment parameters:"+params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(50000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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

    //TODO: Download in File from Url
    class DownloadFileFromURL extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog();
        }
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            String Flag = "";
            try {
                Flag = f_url[1];
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

            return Flag;
        }
//        protected void onProgressUpdate(String... progress) {
//            // setting progress percentage
//            pDialog.setProgress(Integer.parseInt(progress[0]));
//        }
        @Override
        protected void onPostExecute(String Flag) {
            hideDialog();
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SinglaGroups/temp"+FileExtn;
            File file = new File(path);
            if (file.exists()) {
                Uri uri = null;
                // So you have to use Provider
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    uri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".provider", file);
                }else{
                    uri = Uri.fromFile(file);
                }
                if (Flag.equals("0")) {
                    //TODO: View
                    FileOpenByIntent.FileOpen(mContext, path, uri);
                }else if (Flag.equals("1")){
                    //TODO: Share
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("*/*");
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                    mContext.startActivity(Intent.createChooser(intent, "Share File"));
                }
            }else{
                MessageDialog.MessageDialog(mContext,"","File not exist");
            }
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
}