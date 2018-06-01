package callcenter.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import callcenter.model.IncomingCalls;
import callcenter.model.IncomingCalls;
import orderbooking.StaticValues;
import services.NetworkUtils;

/**
 * Created by Rakesh on 13-Dec-17.
 */
public class IncomingCallsAdapter extends RecyclerView.Adapter<IncomingCallsAdapter.RecyclerViewHolder>{

    private Context context;
    private LayoutInflater inflater;
    private List<IncomingCalls> datasetList,filterDatasetList;
    private ProgressDialog progressDialog;
    private MediaPlayer mPlayer;
    private static String TAG = IncomingCallsAdapter.class.getSimpleName();
    public IncomingCallsAdapter(Context context, List<IncomingCalls> datasetList) {
        this.context=context;
        this.datasetList=datasetList;
        this.filterDatasetList = new ArrayList<>();
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

        final IncomingCalls dataset = filterDatasetList.get(position);
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
                    for (IncomingCalls item : datasetList) {
                        if(item.getParty()!=null && item.getSubparty()!=null && item.getCPName()!=null && item.getAutoNo()!=null && item.getCalledNumber()!=null && item.getCallStatusMsg()!=null)
                            if (item.getParty().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getSubparty().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getCPName().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getAutoNo().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getCalledNumber().toLowerCase().contains(text.toLowerCase()) ||
                                    item.getCallStatusMsg().toLowerCase().contains(text.toLowerCase())) {
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
    private void setTableLayout(IncomingCalls dataset, TableLayout tableLayout, TableLayout tableLayout2, TableLayout tableLayout3,final WebView webView){
        //TODO: tableLayout set
        tableLayout.removeAllViewsInLayout();
        tableLayout.removeAllViews();
        View v = inflater.inflate(R.layout.table_row, tableLayout, false);
        // Call Number
        tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"Call Number",(dataset.getCalledNumber()==null || dataset.getCalledNumber().equals("null") ?"":dataset.getCalledNumber())));
        // Call Statsu Msg
        v = inflater.inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"Call Status",(dataset.getCallStatusMsg()==null || dataset.getCallStatusMsg().equals("null") ?"":dataset.getCallStatusMsg())));
        //TODO: Hold Time
        v = inflater.inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"Hold Time",(dataset.getHoldTime()==null || dataset.getHoldTime().equals("null") ?"":dataset.getHoldTime())));
        //TODO: Duration
        v = inflater.inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"Duration",(dataset.getDuration()==null || dataset.getDuration().equals("null") ?"":dataset.getDuration())));
        //TODO: Total Agent
        v = inflater.inflate(R.layout.table_row, tableLayout, false);
        tableLayout.addView(CustomTextView.setTableRow2Columns(context,v,"Total Agent",""+dataset.getSGAgentCount()));
        //TODO: Party Name
        String Party = (dataset.getParty()==null || dataset.getParty().equals("null") ?"":dataset.getParty());
        if (!Party.isEmpty()) {
            v = inflater.inflate(R.layout.table_row, tableLayout, false);
            tableLayout.addView(CustomTextView.setTableRow2Columns(context, v, "Party ", Party));
        }
        //TODO: Sub Party Name
        String SubParty = (dataset.getSubparty()==null || dataset.getSubparty().equals("null") ?"":dataset.getSubparty());
        if (!SubParty.isEmpty()) {
            v = inflater.inflate(R.layout.table_row, tableLayout, false);
            tableLayout.addView(CustomTextView.setTableRow2Columns(context, v, "Sub Party", SubParty));
        }
        //TODO: Contact Person
        String ContactPerson = (dataset.getCPName()==null || dataset.getCPName().equals("null") ?"":dataset.getCPName());
        if (!ContactPerson.isEmpty()) {
            v = inflater.inflate(R.layout.table_row, tableLayout, false);
            tableLayout.addView(CustomTextView.setTableRow2Columns(context, v, "Contact Person", ContactPerson));
        }
        //TODO: Type Name
        String TypeName = (dataset.getTypeName()==null || dataset.getTypeName().equals("null") ?"":dataset.getTypeName());
        if (!TypeName.isEmpty()) {
            v = inflater.inflate(R.layout.table_row, tableLayout, false);
            tableLayout.addView(CustomTextView.setTableRow2Columns(context, v, "Contact Person", TypeName));
        }

        String FileUrl = (dataset.getRecordFileUrl()==null || dataset.getRecordFileUrl().equals("null") ?"":dataset.getRecordFileUrl());
        if (!FileUrl.isEmpty()) {

            //TODO: tableLayout3 set
            tableLayout2.removeAllViewsInLayout();
            tableLayout2.removeAllViews();
            //TODO:  Buttons
            v = inflater.inflate(R.layout.table_row_single_column_button, tableLayout2, false);
            // TODO: Audio File Play
            final Button btnPlay = (Button) v.findViewById(R.id.Button_1);
            btnPlay.setGravity(Gravity.CENTER_HORIZONTAL);
            btnPlay.setText("Play");
            btnPlay.setTag(FileUrl);
            btnPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String Url = (String) view.getTag();

                    btnPlay.setEnabled(false);
                    mPlayer = new MediaPlayer();
                    // Set the media player audio stream type
                    mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    //Try to play music/audio from url
                    try{
                    /*
                        void setDataSource (String path)
                            Sets the data source (file-path or http/rtsp URL) to use.

                        Parameters
                            path String : the path of the file, or the http/rtsp URL of the stream you want to play

                        Throws
                            IllegalStateException : if it is called in an invalid state

                                When path refers to a local file, the file may actually be opened by a
                                process other than the calling application. This implies that the
                                pathname should be an absolute path (as any other process runs with
                                unspecified current working directory), and that the pathname should
                                reference a world-readable file. As an alternative, the application
                                could first open the file for reading, and then use the file
                                descriptor form setDataSource(FileDescriptor).

                            IOException
                            IllegalArgumentException
                            SecurityException
                    */
                        // Set the audio data source
                        mPlayer.setDataSource(Url);

                    /*
                        void prepare ()
                            Prepares the player for playback, synchronously. After setting the
                            datasource and the display surface, you need to either call prepare()
                            or prepareAsync(). For files, it is OK to call prepare(), which blocks
                            until MediaPlayer is ready for playback.

                        Throws
                            IllegalStateException : if it is called in an invalid state
                            IOException
                    */
                        // Prepare the media player
                        mPlayer.prepare();

                        // Start playing audio from http url
                        mPlayer.start();

                        // Inform user for audio streaming
                        Toast.makeText(context,"Playing",Toast.LENGTH_SHORT).show();
                    }catch (IOException e){
                        // Catch the exception
                        e.printStackTrace();
                    }catch (IllegalArgumentException e){
                        e.printStackTrace();
                    }catch (SecurityException e){
                        e.printStackTrace();
                    }catch (IllegalStateException e){
                        e.printStackTrace();
                    }

                    mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            Toast.makeText(context,"End",Toast.LENGTH_SHORT).show();
                            btnPlay.setEnabled(true);
                        }
                    });
                }
            });
            tableLayout2.addView(v);
        }
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
