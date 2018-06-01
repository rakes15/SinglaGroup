package uploadimagesfiles.voucherdocupload.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.view.ContextThemeWrapper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.singlagroup.AppController;
import com.singlagroup.LoginActivity;
import com.singlagroup.R;
import com.singlagroup.customwidgets.DateFormatsMethods;
import com.singlagroup.customwidgets.MessageDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import orderbooking.StaticValues;
import orderbooking.customerlist.adapter.RunningFairAdapter;
import services.NetworkUtils;
import uploadimagesfiles.voucherdocupload.AttachmentActivity;
import uploadimagesfiles.voucherdocupload.VoucherDocumentUploadActivity;
import uploadimagesfiles.voucherdocupload.datasets.MasterDocumentDataset;
import uploadimagesfiles.voucherdocupload.datasets.VoucherDocumentDataset;

/**
 * Created by Rakesh on 08-Oct-16.
 */
public class RecyclerViewVoucherDocumentAdapter extends RecyclerView.Adapter<RecyclerViewVoucherDocumentAdapter.RecyclerViewHolder>{

    private Context context;
    private LayoutInflater inflater;
    private List<VoucherDocumentDataset> voucherList, filterVoucherList;
    private List<MasterDocumentDataset> masterList, filterMasterList;
    private String VType,VHeading,Type;
    private static String TAG = RecyclerViewVoucherDocumentAdapter.class.getSimpleName();
    public RecyclerViewVoucherDocumentAdapter(Context context, List<VoucherDocumentDataset> voucherList,List<MasterDocumentDataset> masterList,String VType,String VHeading,String Type) {
        this.context=context;
        this.voucherList=voucherList;
        this.masterList=masterList;
        this.VType=VType;
        this.VHeading=VHeading;
        this.Type=Type;
        this.filterVoucherList = new ArrayList<>();
        this.filterVoucherList.addAll(this.voucherList);
        this.filterMasterList = new ArrayList<>();
        this.filterMasterList.addAll(this.masterList);
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.cardview_all_customer_list, parent, false);
        RecyclerViewHolder viewHolder=new RecyclerViewHolder(v);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {

        if (Type.equals("0")){
            VoucherDocumentDataset dataset = filterVoucherList.get(position);
            setTableLayoutVoucher(dataset,holder.tableLayout);
            holder.cardView.setTag(dataset);
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    VoucherDocumentDataset dataset = (VoucherDocumentDataset) view.getTag();
                    Intent intent = new Intent(context, VoucherDocumentUploadActivity.class);
                    intent.putExtra("VType", ""+dataset.getVType());
                    intent.putExtra("VHeading", VHeading);
                    intent.putExtra("VID", dataset.getDocID());
                    intent.putExtra("Type", Type);
                    context.startActivity(intent);
                }
            });
        }else if (Type.equals("1")) {
            MasterDocumentDataset dataset = filterMasterList.get(position);
            setTableLayoutMaster(dataset,holder.tableLayout);
            if (dataset.getSubParty() == 1){
                holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.TransparentRed));
            }else{
                holder.cardView.setBackgroundColor(context.getResources().getColor(android.R.color.background_light));
            }
            holder.cardView.setTag(dataset);
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MasterDocumentDataset dataset = (MasterDocumentDataset) view.getTag();
                    Intent intent = new Intent(context, VoucherDocumentUploadActivity.class);
                    intent.putExtra("VType", VType);
                    intent.putExtra("VHeading", VHeading);
                    intent.putExtra("VID", dataset.getDocID());
                    intent.putExtra("Type", Type);
                    context.startActivity(intent);
                }
            });
        }

    }
    @Override
    public int getItemCount() {
        int size = 0;
        if (Type.equals("0")) {
            size = (null != filterVoucherList ? filterVoucherList.size() : 0);
        }else if (Type.equals("1")) {
            size = (null != filterMasterList ? filterMasterList.size() : 0);
        }
        return size;
    }
    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        TableLayout tableLayout;
        CardView cardView;
        public RecyclerViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.card_view);
            tableLayout = (TableLayout) itemView.findViewById(R.id.table_Layout);
        }
    }
    private void setTableLayoutVoucher(final VoucherDocumentDataset dataset, TableLayout tableLayout){

        //TODO: TableLayout set
        tableLayout.removeAllViewsInLayout();
        tableLayout.removeAllViews();
        //TODO: 1st Row
        View v = inflater.inflate(R.layout.table_row, tableLayout, false);
        TextView txtHeader= (TextView) v.findViewById(R.id.header);
        txtHeader.setText("Doc No:");

        TextView txt= (TextView) v.findViewById(R.id.content);
        txt.setText(""+(dataset.getDocNo()==null?"":dataset.getDocNo()));
        tableLayout.addView(v);

        //TODO: 2nd Row
        View v1 = inflater.inflate(R.layout.table_row, tableLayout, false);
        TextView txtHeader1= (TextView) v1.findViewById(R.id.header);
        txtHeader1.setText("Doc Date:");

        TextView txt1= (TextView) v1.findViewById(R.id.content);
        txt1.setText(""+(dataset.getDocDate()==null?"": DateFormatsMethods.DateFormat_DD_MM_YYYY(dataset.getDocDate())));
        tableLayout.addView(v1);

        String PartyName = (dataset.getPartyName()==null?"":dataset.getPartyName());
        String SubPartyName = (dataset.getSubPartyName()==null?"":dataset.getSubPartyName());
        String GLName = (dataset.getGLName()==null?"":dataset.getGLName());
        String DocAmount = (dataset.getDocAmount()==null?"":dataset.getDocAmount());
        String DetAmount = (dataset.getDetAmount()==null?"":dataset.getDetAmount());
        String RefDate = (dataset.getRefDate()==null?"":DateFormatsMethods.DateFormat_DD_MM_YYYY(dataset.getRefDate()));
        String RefDocNo = (dataset.getRefDocNo()==null?"":dataset.getRefDocNo());

        if (!PartyName.isEmpty()) {
            v1 = inflater.inflate(R.layout.table_row, tableLayout, false);
            txtHeader1 = (TextView) v1.findViewById(R.id.header);
            txtHeader1.setText("Party Name:");

            txt1 = (TextView) v1.findViewById(R.id.content);
            txt1.setText("" + PartyName);
            tableLayout.addView(v1);
        }
        if (!SubPartyName.isEmpty()) {
            v1 = inflater.inflate(R.layout.table_row, tableLayout, false);
            txtHeader1 = (TextView) v1.findViewById(R.id.header);
            txtHeader1.setText("SubParty Name:");

            txt1 = (TextView) v1.findViewById(R.id.content);
            txt1.setText("" + SubPartyName);
            tableLayout.addView(v1);
        }
        if (!GLName.isEmpty()) {
            v1 = inflater.inflate(R.layout.table_row, tableLayout, false);
            txtHeader1 = (TextView) v1.findViewById(R.id.header);
            txtHeader1.setText("GLName:");

            txt1 = (TextView) v1.findViewById(R.id.content);
            txt1.setText("" + GLName);
            tableLayout.addView(v1);
        }
        if (!RefDate.isEmpty()) {
            v1 = inflater.inflate(R.layout.table_row, tableLayout, false);
            txtHeader1 = (TextView) v1.findViewById(R.id.header);
            txtHeader1.setText("Ref Date:");

            txt1 = (TextView) v1.findViewById(R.id.content);
            txt1.setText("" + RefDate);
            tableLayout.addView(v1);
        }
        if (!RefDocNo.isEmpty()) {
            v1 = inflater.inflate(R.layout.table_row, tableLayout, false);
            txtHeader1 = (TextView) v1.findViewById(R.id.header);
            txtHeader1.setText("Ref Doc No:");

            txt1 = (TextView) v1.findViewById(R.id.content);
            txt1.setText("" + RefDocNo);
            tableLayout.addView(v1);
        }

        if (!DocAmount.isEmpty()) {
            v1 = inflater.inflate(R.layout.table_row, tableLayout, false);
            txtHeader1 = (TextView) v1.findViewById(R.id.header);
            txtHeader1.setText("Document Amount:");

            txt1 = (TextView) v1.findViewById(R.id.content);
            txt1.setText("₹" + DocAmount);
            tableLayout.addView(v1);
        }
        if (!DetAmount.isEmpty()) {
            v1 = inflater.inflate(R.layout.table_row, tableLayout, false);
            txtHeader1 = (TextView) v1.findViewById(R.id.header);
            txtHeader1.setText("Details Amount:");

            txt1 = (TextView) v1.findViewById(R.id.content);
            txt1.setText("₹" + DetAmount);
            tableLayout.addView(v1);
        }


        //TODO: 3rd Row
        final String str = (dataset.getAttechDet().size()==0)?"No file attached":" "+dataset.getAttechDet().size()+" Files";
        View v2 = inflater.inflate(R.layout.table_row, tableLayout, false);
        //TODO: Header
        TextView txtHeader2= (TextView) v2.findViewById(R.id.header);
        txtHeader2.setText("Attachment:");
        txtHeader2.setTag(dataset);
        txtHeader2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VoucherDocumentDataset datasets = (VoucherDocumentDataset)view.getTag();
                if (datasets.getAttechDet().size()>0) {
                    //DialogAttachedList(datasets,null);
                    Intent intent = new Intent(context, AttachmentActivity.class);
                    intent.putExtra("VType", datasets.getVType());
                    intent.putExtra("VHeading", VHeading);
                    intent.putExtra("VoucherDataset", datasets);
                    intent.putExtra("Type", Type);
                    context.startActivity(intent);
                }else {
                    MessageDialog.MessageDialog(context, dataset.getDocNo(), str);
                }
            }
        });
        //TODO: Content
        TextView txt2= (TextView) v2.findViewById(R.id.content);
        txt2.setText(str);
        txt2.setTextColor(context.getResources().getColor(R.color.Color_Green));
        txt2.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_attachment_black_24dp,0);
        txt2.setGravity(Gravity.CENTER_VERTICAL);
        txt2.setTag(dataset);
        txt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VoucherDocumentDataset datasets = (VoucherDocumentDataset)view.getTag();
                if (datasets.getAttechDet().size()>0) {
                    //DialogAttachedList(datasets,null);
                    Intent intent = new Intent(context, AttachmentActivity.class);
                    intent.putExtra("VType", datasets.getVType());
                    intent.putExtra("VHeading", VHeading);
                    intent.putExtra("VoucherDataset", datasets);
                    intent.putExtra("Type", Type);
                    context.startActivity(intent);
                }else {
                    MessageDialog.MessageDialog(context, dataset.getDocNo(), str);
                }
            }
        });
        tableLayout.addView(v2);
    }
    private void setTableLayoutMaster(final MasterDocumentDataset dataset, TableLayout tableLayout){

        //TODO: TableLayout set
        tableLayout.removeAllViewsInLayout();
        tableLayout.removeAllViews();
        //TODO: 1st Row
        View v = inflater.inflate(R.layout.table_row, tableLayout, false);
        TextView txtHeader= (TextView) v.findViewById(R.id.header);
        txtHeader.setText("Party Name:");

        TextView txt= (TextView) v.findViewById(R.id.content);
        txt.setText(""+(dataset.getPartyName()==null?"":dataset.getPartyName()));
        tableLayout.addView(v);

        String City = (dataset.getCity()==null?"":dataset.getCity());
        String State = (dataset.getState()==null?"":dataset.getState());
        String GLName = (dataset.getGLName()==null?"":dataset.getGLName());
        if (!City.isEmpty()) {
            View v1 = inflater.inflate(R.layout.table_row, tableLayout, false);
            TextView txtHeader1 = (TextView) v1.findViewById(R.id.header);
            txtHeader1.setText("City:");

            TextView txt1 = (TextView) v1.findViewById(R.id.content);
            txt1.setText("" + City);
            tableLayout.addView(v1);
        }
        if (!State.isEmpty()) {
            View v1 = inflater.inflate(R.layout.table_row, tableLayout, false);
            TextView txtHeader1 = (TextView) v1.findViewById(R.id.header);
            txtHeader1.setText("State:");

            TextView txt1 = (TextView) v1.findViewById(R.id.content);
            txt1.setText("" + State);
            tableLayout.addView(v1);
        }
        if (!GLName.isEmpty()) {
            View v1 = inflater.inflate(R.layout.table_row, tableLayout, false);
            TextView txtHeader1 = (TextView) v1.findViewById(R.id.header);
            txtHeader1.setText("GLName:");

            TextView txt1 = (TextView) v1.findViewById(R.id.content);
            txt1.setText("" + GLName);
            tableLayout.addView(v1);
        }

        //TODO: 3rd Row
        final String str = (dataset.getAttechDet().size()==0)?"No file attached":" "+dataset.getAttechDet().size()+" Files";
        View v2 = inflater.inflate(R.layout.table_row, tableLayout, false);
        //TODO: Header
        TextView txtHeader2= (TextView) v2.findViewById(R.id.header);
        txtHeader2.setText("Attachment:");
        txtHeader2.setTag(dataset);
        txtHeader2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MasterDocumentDataset datasets = (MasterDocumentDataset)view.getTag();
                if (datasets.getAttechDet().size()>0) {
                    //DialogAttachedList(null,datasets);
                    Intent intent = new Intent(context, AttachmentActivity.class);
                    intent.putExtra("VType", datasets.getVType());
                    intent.putExtra("VHeading", VHeading);
                    intent.putExtra("MasterDataset", datasets);
                    intent.putExtra("Type", Type);
                    context.startActivity(intent);
                }else {
                    MessageDialog.MessageDialog(context, "", str);
                }
            }
        });
        //TODO: Content
        TextView txt2= (TextView) v2.findViewById(R.id.content);
        txt2.setText(str);
        txt2.setTextColor(context.getResources().getColor(R.color.Color_Green));
        txt2.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_attachment_black_24dp,0);
        txt2.setGravity(Gravity.CENTER_VERTICAL);
        txt2.setTag(dataset);
        txt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MasterDocumentDataset datasets = (MasterDocumentDataset)view.getTag();
                if (datasets.getAttechDet().size()>0) {
                    //DialogAttachedList(null,datasets);
                    Intent intent = new Intent(context, AttachmentActivity.class);
                    intent.putExtra("VType", datasets.getVType());
                    intent.putExtra("VHeading", VHeading);
                    intent.putExtra("MasterDataset", datasets);
                    intent.putExtra("Type", Type);
                    context.startActivity(intent);
                }else {
                    MessageDialog.MessageDialog(context, "", str);
                }
            }
        });
        tableLayout.addView(v2);
    }
    //TODO: Filter Search
    public void filter(final String text) {

        // Searching could be complex..so we will dispatch it to a different thread...
        new Thread(new Runnable() {
            @Override
            public void run() {

                if (Type.equals("0")) {
                    // Clear the filter list
                    filterVoucherList.clear();
                    //Toast.makeText(context,text,Toast.LENGTH_LONG).show();
                    Log.e("TAG", text);
                    // If there is no search value, then add all original list items to filter list
                    if (TextUtils.isEmpty(text)) {
                        filterVoucherList.addAll(voucherList);
                    } else {
                        // Iterate in the original List and add it to filter list...
                        for (VoucherDocumentDataset item : voucherList) {
                            String DocDate = (item.getDocDate()==null?"":item.getDocDate());
                            String DocNo = (item.getDocNo()==null?"":item.getDocNo());
                            String PartyName = (item.getPartyName()==null?"":item.getPartyName());
                            String SubPartyName = (item.getSubPartyName()==null?"":item.getSubPartyName());
                            if (DocDate != null && DocNo != null && PartyName != null  && SubPartyName != null && item.getGLName() != null && item.getRefDate() != null && item.getRefDocNo() != null)
                                if (DocDate.toLowerCase().contains(text.toLowerCase()) ||
                                        DocNo.toLowerCase().contains(text.toLowerCase()) ||
                                        PartyName.toLowerCase().contains(text.toLowerCase()) ||
                                        SubPartyName.toLowerCase().contains(text.toLowerCase()) ||
                                        item.getGLName().toLowerCase().contains(text.toLowerCase()) ||
                                        item.getRefDate().toLowerCase().contains(text.toLowerCase()) ||
                                        item.getRefDocNo().toLowerCase().contains(text.toLowerCase())) {
                                    // Adding Matched items
                                    filterVoucherList.add(item);
                                }
                        }
                    }
                }else if (Type.equals("1")) {
                    // Clear the filter list
                    filterMasterList.clear();
                    //Toast.makeText(context,text,Toast.LENGTH_LONG).show();
                    Log.e("TAG", text);
                    // If there is no search value, then add all original list items to filter list
                    if (TextUtils.isEmpty(text)) {
                        filterMasterList.addAll(masterList);
                    } else {
                        // Iterate in the original List and add it to filter list...
                        for (MasterDocumentDataset item : masterList) {
                            String PartyName = (item.getPartyName()==null?"":item.getPartyName());
                            String City = (item.getCity()==null?"":item.getCity());
                            String State = (item.getState()==null?"":item.getState());
                            String GLName = (item.getGLName()==null?"":item.getGLName());
                            if (PartyName != null && City != null && State != null && GLName != null)
                                if (PartyName.toLowerCase().contains(text.toLowerCase()) ||
                                        City.toLowerCase().contains(text.toLowerCase()) ||
                                        State.toLowerCase().contains(text.toLowerCase()) ||
                                        GLName.toLowerCase().contains(text.toLowerCase())) {
                                    // Adding Matched items
                                    filterMasterList.add(item);
                                }
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
}
